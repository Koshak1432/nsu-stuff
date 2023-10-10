using System.Text;
using CardsLib;
using Microsoft.VisualBasic;

namespace ColiseumProblem.GodAndAssistant;

// add interface
public class GodAssistant : IGodAssistant
{
    private static readonly Random Random = new ();
    private Card[]? _deck;
    
    public Card[] CreateDeck()
    {
        var cards = new Card[Constants.CardsNum];
        FillHalfColor(cards, CardColor.Black);
        FillHalfColor(cards, CardColor.Red);
        _deck = cards;
        return cards;
    }

    public Card[] GetDeck()
    {
        if (_deck == null)
        {
            throw new NullReferenceException("Deck is null");
        }
        return _deck;
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
            
            var shuffled = PlaceInOrder(cards, customOrder);
            Array.Copy(shuffled, cards, cards.Length);
        }
    }

    private static Card[] PlaceInOrder(Card[] cards, string customOrder)
    {
        var lower = customOrder.ToLower();
        if (!IsCountValid(lower))
        {
            throw new ArgumentException("Order must contain exactly " + Constants.CardsNum/2 +
                                        " red and" + Constants.CardsNum / 2 + " black cards");
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

        return shuffled;
    }

    private static bool IsCountValid(string lower)
    {
        return lower.Count(c => c == 'r') == Constants.CardsNum / 2 && lower.Count(c => c == 'b') == Constants.CardsNum / 2;
    }

    private static void CycleToColor(Card[] cards, CardColor color, ref int colorIdx)
    {
        while (cards[colorIdx].Color != color)
        {
            ++colorIdx;
        }
    }

    public (Card[] elon, Card[] mark) SplitDeck(Card[] deck)
    {
        var elonCards = new Card[Constants.CardsNum / 2];
        var markCards = new Card[Constants.CardsNum / 2];
        Array.Copy(deck, 0, elonCards, 0, Constants.CardsNum / 2);
        Array.Copy(deck, Constants.CardsNum / 2, markCards, 0, Constants.CardsNum / 2);
        
        return (elonCards, markCards);
    }


    private static void FillHalfColor(Card[] cards, CardColor color)
    {
        var offset = (color == CardColor.Red) ? Constants.CardsNum / 2 : 0;
        for (var i = 0; i < Constants.CardsNum / 2; ++i)
        {
            cards[i + offset] = new Card(color);
        }
    }

    public string GetDeckOrder()
    {
        if (_deck == null)
        {
            throw new NullReferenceException("Deck is null");
        }
        var builder = new StringBuilder();
        foreach (var card in _deck)
        {
            builder.Append(card.Color == CardColor.Black ? "b" : "r");
        }

        var res = builder.ToString();
        if (!IsCountValid(res))
        {
            throw new ArgumentException("Order must contain exactly " + Constants.CardsNum/2 +
                                        " red and" + Constants.CardsNum / 2 + " black cards");
        }

        return builder.ToString();
    }
}