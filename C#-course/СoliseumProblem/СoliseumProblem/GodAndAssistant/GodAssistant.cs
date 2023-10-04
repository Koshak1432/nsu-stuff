using CardsLib;

namespace ColiseumProblem.GodAndAssistant;

// add interface
public class GodAssistant : IGodAssistant
{
    private static Random _random = new Random();

    public Card[] CreateDeck()
    {
        var cards = new Card[Constants.CardsNum];
        FillHalfColor(cards, CardColor.Black);
        FillHalfColor(cards, CardColor.Red);
        return cards;
    }

    // Fisher–Yates shuffle
    public void ShuffleDeck(Card[] cards)
    {
        for (var i = Constants.CardsNum - 1; i > 0; --i)
        {
            int k = _random.Next(i + 1);
            (cards[i], cards[k]) = (cards[k], cards[i]);
        }
    }

    public (Card[], Card[]) SplitDeck(Card[] deck)
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
}