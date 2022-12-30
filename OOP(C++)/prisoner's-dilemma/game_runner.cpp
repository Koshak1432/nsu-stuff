#include "game_runner.h"

#include "console_interface.h"

static std::vector<std::unique_ptr<Strategy>> make_strategies_from_names(const std::vector<std::string> &names)
{
	assert(COLS == names.size());
	std::vector<std::unique_ptr<Strategy>> strategies{};
	strategies.reserve(COLS);
	for (auto &name : names)
	{
		strategies.push_back(Strategy_factory::get_instance()->create_product_by_id(name));
	}
	return strategies;
}

static void add_to_global_scores(std::map<std::string, int> &total_scores, const std::vector<std::string> &names, const Result &result)
{
	for (std::size_t i = 0; i < names.size(); ++i)
	{
		auto it = total_scores.find(names[i]);
		if (it == total_scores.end())
		{
			throw std::invalid_argument("invalid"); //impossible???
		}
		it->second += result.scores_[i];
	}
}

Fast_runner::Fast_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps) : game(matrix, make_strategies_from_names(names)), names_(std::move(names)), steps_(steps)
{}

Tournament_runner::Tournament_runner(const Matrix &matrix, std::vector<std::string> names, std::size_t steps) :names_(std::move(names)), steps_(steps), matrix_(matrix)
{}

Detailed_runner::Detailed_runner(const Matrix &matrix, std::vector<std::string> names) :game(matrix, make_strategies_from_names(names)), names_(std::move(names))
{}

void Fast_runner::run(CLI &ui)
{
	for (std::size_t i = 0; i < steps_; ++i)
	{
		game.step();
	}
	ui.print_after_game(names_, game.get_result());
}

void Detailed_runner::run(CLI &ui)
{
	while (ui.read_msg())
	{
		game.step();
		ui.print_intermediate(names_, game.get_result());
	}
	ui.print_after_game(names_, game.get_result());
}

void Tournament_runner::run(CLI &ui)
{
	std::map<std::string, int> total_scores;
	for (auto &name : names_)
	{
		total_scores.insert({name, 0});
	}

	std::vector<bool> bool_vec(names_.size());
	std::fill(bool_vec.begin(), bool_vec.begin() + COLS, true);
	do
	{
		std::vector<std::string> names{};
		names.reserve(COLS);
		for(std::size_t i = 0; i < bool_vec.size(); ++i)
		{
			if (bool_vec[i])
			{
				names.push_back(names_[i]);
			}
		}
		Game game(matrix_, make_strategies_from_names(names));
		for (std::size_t i = 0; i < steps_; ++i)
		{
			game.step();
		}
		add_to_global_scores(total_scores, names, game.get_result());
		ui.print_after_game(names, game.get_result());
	} while (std::prev_permutation(bool_vec.begin(), bool_vec.end()));

	ui.print_final(total_scores);
}
