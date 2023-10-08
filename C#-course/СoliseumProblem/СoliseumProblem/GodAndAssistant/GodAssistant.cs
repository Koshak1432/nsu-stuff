using CardsLib;
using Microsoft.VisualBasic;

namespace ColiseumProblem.GodAndAssistant;

// add interface
public class GodAssistant : IGodAssistant
{
    private static readonly Random Random = new ();

    public Card[] CreateDeck()
    {
        var cards = new Card[Constants.CardsNum];
        FillHalfColor(cards, CardColor.Black);
        FillHalfColor(cards, CardColor.Red);
        return cards;
    }

    // Fisher–Yates shuffle
    public void ShuffleDeck(Card[] cards, string? customOrder = null)
    {
        if (cards.Length != Constants.CardsNum)
        {
            throw new ArgumentException("Cards len must be " + Constants.CardsNum + ", current: " + cards.Length);
        }
        if (customOrder == null)
        {
            for (var i = cards.Length - 1; i > 0; --i)
            {
                var k = Random.Next(i + 1);
                (cards[i], cards[k]) = (cards[k], cards[i]);
            }
        }
        else
        {
            if (customOrder.Length != Constants.CardsNum)
            {
                throw new ArgumentException("Invalid custom order length");
            }
            var lower = customOrder.ToLower();
            if (!(lower.Count(c => c == 'r') == Constants.CardsNum / 2 && lower.Count(c => c == 'b') == Constants.CardsNum / 2))
            {
                throw new ArgumentException("Custom order must contain exactly " + Constants.CardsNum +
                                            " red and black cards");
            }
            
            var blackIdx = 0;
            var redIdx = 0;
            var shuffled = new Card[cards.Length];
            for (var i = 0; i < cards.Length; ++i)
            {
                if (lower[i] == 'b')
                {
                    CycleToColor(cards, CardColor.Black, ref blackIdx);
                    shuffled[i] = cards[blackIdx];
                }
                else
                {
                    CycleToColor(cards, CardColor.Red, ref redIdx);
                    shuffled[i] = cards[redIdx];
                }
            }

            Array.Copy(shuffled, cards, cards.Length);
        }
    }

    private static void CycleToColor(Card[] cards, CardColor color, ref int colorIdx)
    {
        while (cards[colorIdx].Color != color)
        {
            ++colorIdx;
        }
    }

    public SplitDeck SplitDeck(Card[] deck)
    {
        var elonCards = new Card[Constants.CardsNum / 2];
        var markCards = new Card[Constants.CardsNum / 2];
        Array.Copy(deck, 0, elonCards, 0, Constants.CardsNum / 2);
        Array.Copy(deck, Constants.CardsNum / 2, markCards, 0, Constants.CardsNum / 2);
        var split = new SplitDeck(elonCards, markCards);

        return split;
    }


    private static void FillHalfColor(Card[] cards, CardColor color)
    {
        var offset = (color == CardColor.Red) ? Constants.CardsNum / 2 : 0;
        for (var i = 0; i < Constants.CardsNum / 2; ++i)
        {
            cards[i + offset] = new Card(color);
        }
    }
}