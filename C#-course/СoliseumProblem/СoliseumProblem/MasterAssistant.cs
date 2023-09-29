using CardsLib;

namespace ColiseumProblem;

// add interface
public class MasterAssistant
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

    
    private static void FillHalfColor(Card[] cards, CardColor color)
    {
        var offset = (color == CardColor.Red) ? Constants.CardsNum / 2 : 0;
        for (var i = 0; i < Constants.CardsNum / 2; ++i)
        {
            cards[i + offset] = new Card(color);
        }
    }
}