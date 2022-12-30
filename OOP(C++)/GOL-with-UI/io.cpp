#include "io.h"

#include <iostream>
#include <QString>
#include <QVector>
#include <QIODevice>
#include <QDebug>

#include "engine.h"

namespace
{
	constexpr QChar LIVE_CELL = 'o';
	constexpr QChar DEAD_CELL = 'b';
	constexpr QChar END_OF_LINE = '$';
	constexpr QChar RLE_END = '!';
	constexpr int CHARS_NUMBER = 128;
	constexpr int DEFAULT_OFFSET = 10;

	enum class Expectations
	{
		SPACE = CHARS_NUMBER,
		NUMBER,
	};

	struct RLEHeader
	{
		int x = 0;
		int y = 0;
		Rules rules;
	};

	void expectChar(QIODevice *device, char expectedChar)
	{
		if (expectedChar != device->peek(sizeof(expectedChar)).at(0))
		{
			throw std::invalid_argument("Invalid data in file (expectChar)");
		}
	}

	bool isDigit(QIODevice *device)
	{
		return (0 != std::isdigit(device->peek(sizeof(char)).at(0)));
	}

	QString readNumber(QIODevice *device)
	{
		QString str;
		char digit;
		while (isDigit(device))
		{
			if (!device->getChar(&digit))
			{
				throw std::invalid_argument("Invalid data in file (readNumber)");
			}
			str.append(digit);
		}
		return str;
	}

	void skipWhiteSpaces(QIODevice *device)
	{
		char ch;
		if (sizeof(char) != device->peek(&ch, sizeof(char)))
		{
			throw std::invalid_argument("Skip white spaces error");
		}
		while (' ' == ch)
		{
			device->skip(sizeof(char));
			if (sizeof(char) != device->peek(&ch, sizeof(char)))
			{
				throw std::invalid_argument("Skip white spaces error");
			}
		}
	}

	std::vector<bool> readRule(const QString &numInString)
	{
		std::vector<bool> ruleVec(9, false);
		for (auto digit: numInString)
		{
			int idx = int(digit.toLatin1()) - '0';
			ruleVec[idx] = true;
		}
		return ruleVec;
	}

	RLEHeader readHeader(QIODevice *device, State &currentState)
	{
		std::vector<Expectations> expect {Expectations {'x'}, Expectations::SPACE, Expectations {'='},
										  Expectations::SPACE,
										  Expectations::NUMBER, Expectations {','}, Expectations::SPACE,
										  Expectations {'y'},
										  Expectations::SPACE, Expectations {'='}, Expectations::SPACE,
										  Expectations::NUMBER,
										  Expectations {','}, Expectations::SPACE, Expectations {'r'},
										  Expectations {'u'},
										  Expectations {'l'}, Expectations {'e'}, Expectations::SPACE,
										  Expectations {'='},
										  Expectations::SPACE, Expectations {'B'}, Expectations::NUMBER,
										  Expectations {'/'},
										  Expectations {'S'}, Expectations::NUMBER, Expectations::SPACE,
										  Expectations {'\n'}};

		constexpr int numNumbers = 4;
		RLEHeader header;
		QStringList stringNumbers {};
		stringNumbers.reserve(numNumbers);

		for (auto action: expect)
		{
			if (static_cast<int>(action) < static_cast<int>(Expectations::SPACE))
			{
				expectChar(device, static_cast<char>(action));
				device->skip(sizeof(char));
				continue;
			}
			switch (action)
			{
				case Expectations::SPACE:
				{
					skipWhiteSpaces(device);
					break;
				}
				case Expectations::NUMBER:
				{
					stringNumbers.append(readNumber(device));
					break;
				}
				default:
				{
					assert(false);
				}
			}
		}
		Rules rules(readRule(stringNumbers[2]), readRule(stringNumbers[3]));

		bool ok = true;
		int headerWidth = stringNumbers[0].toInt(&ok);
		int headerHeight = stringNumbers[1].toInt(&ok);
		if (!ok)
		{
			throw std::invalid_argument("Can't convert width and height from header info");
		}

		header.x = headerWidth;
		header.y = headerHeight;
		header.rules = std::move(rules);
		return header;


	}

	void readRLE(QIODevice *device, Field &field, int &x, int &y, bool &end)
	{
		while (true)
		{
			int runCount = 0;
			QString stringRunCount;
			char tag;
			bool ok = true;

			stringRunCount = readNumber(device);
			if (stringRunCount.isEmpty())
			{
				runCount = 1;
			}
			else
			{
				runCount = stringRunCount.toInt(&ok);
				if (!ok)
				{
					throw std::invalid_argument("Can't read rle");
				}
			}
			skipWhiteSpaces(device);
			if (!device->getChar(&tag))
			{
				throw std::invalid_argument("Can't read rle");
			}
			if ('$' == tag)
			{
				x = 0;
				y += runCount;
			}
			else if ('\n' == tag)
			{
				if (!stringRunCount.isEmpty())
				{
					throw std::invalid_argument("New line after run count is forbidden");
				}
				else
				{
					return;
				}
			}
			else if ('!' == tag)
			{
				end = true;
				return;
			}
			else //RLE readers that cannot handle more than two states should treat all letters other than b as equivalent to o.
			{
				for (int i = 0; i < runCount; ++i)
				{
					if (x < field.getWidth() && y < field.getHeight())
					{
						field.setCell(x, y, DEAD_CELL != tag);
						++x;
					}
					else
					{
						throw std::invalid_argument("Invalid info in rle");
					}
				}
			}

		}
	}

	void writeRulesIdx(QTextStream &out, const std::vector<bool> &rule)
	{
		for (int i = 0; i < rule.size(); ++i)
		{
			if (rule[i])
			{
				out << i;
			}
		}
	}

	void writeHeader(QTextStream &out, const State &state)
	{
		Rules rules(state.getRules());

		out << "x = " << state.getWidth() << ", y = " << state.getHeight() << ", rule = B";
		writeRulesIdx(out, rules.birth_);
		out << "/S";
		writeRulesIdx(out, rules.survival_);
		out << "\n";
	}

	QString getRowData(const Field &field, int row)
	{
		QString rowString;
		rowString.reserve(field.getWidth());
		for (int col = 0; col < field.getWidth(); ++col)
		{
			field.getCell(col, row) ? rowString.append(LIVE_CELL) : rowString.append(DEAD_CELL);
		}
		return rowString;
	}

	QString getEncodedString(QString data) //get compressed string
	{
		if (data.isEmpty())
		{
			return {};
		}
		QChar prevChar = data[0];
		ulong count = 1;
		QString encoding;
		encoding.reserve(data.size());

		for (qsizetype i = 1; i < data.size(); ++i)
		{
			QChar currentChar = data[i];
			if (currentChar != prevChar)
			{
				if (count > 1)
				{
					encoding += QString::number(count);
				}
				encoding += prevChar;
				prevChar = currentChar;
				count = 1;
			}
			else
			{
				++count;
			}
		}
		if (DEAD_CELL != prevChar)
		{
			if (count > 1)
			{
				encoding += QString::number(count);
			}
			encoding += prevChar;
		}
		return encoding;
	}

	void writeRLE(QTextStream &out, const Field &field)
	{
		for (int row = 0; row < field.getHeight(); ++row)
		{
			out << getEncodedString(getRowData(field, row)) + END_OF_LINE + '\n';
		}
		out << RLE_END;
	}

	void setSizeFromHeader(const RLEHeader &header, Field &field)
	{
		if (header.y > field.getHeight())
		{
			field.resize(field.getWidth(), header.y + DEFAULT_OFFSET);
		}
		if (header.x > field.getWidth())
		{
			field.resize(header.x + DEFAULT_OFFSET, field.getHeight());
		}
	}
}

void readState(QIODevice *device, State &currentState)
{
	char ch;
	Field field(currentState.getWidth(), currentState.getHeight());
	RLEHeader header;
	bool begin = false;
	int x = 0;
	int y = 0;

	while (true)
	{
		if (sizeof(ch) != device->peek(&ch, sizeof(ch)))
		{
			throw std::invalid_argument("Can't peek char");
		}
		switch (ch)
		{
			case ' ':
			{
				throw std::invalid_argument("Space in the beginning of line is forbidden");
			}
			case '#':
			{
				device->readLine();
				break;
			}
			case 'x':
			{
				if (begin)
				{
					throw std::invalid_argument("Another beginning of rle info");
				}
				begin = true;
				header = readHeader(device, currentState);
				setSizeFromHeader(header, currentState.getCurrent());
				setSizeFromHeader(header, currentState.getNext());
				field = Field(header.x, header.y);
				break;
			}
			default:
			{
				bool end = false;
				readRLE(device, field, x, y, end);
				if (end)
				{
					currentState.clear();
					currentState.setRules(std::move(header.rules));
					currentState.getCurrent().copyToCenterFrom(field);
					return;
				}
			}
		}
	}
}

void saveToFile(QIODevice *device, State &state)
{
	QTextStream out(device);
	writeHeader(out, state);
	writeRLE(out, state.getCurrent());
}