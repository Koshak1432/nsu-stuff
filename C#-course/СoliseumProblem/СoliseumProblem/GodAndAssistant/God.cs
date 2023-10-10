using CardsLib;
using StrategiesLib;

namespace ColiseumProblem.GodAndAssistant;

public class God : IGod
{
    // replace by array of array of cards? and params in cnstr
    private Card[]? _elonCards;
    private Card[]? _markCards;

    public bool MakeDecision(int elonPick, int markPick)
    {
        if (_elonCards == null || _markCards == null)
        {
            throw new NullReferenceException("Elon or/and mark cards are null");
        }
        
        if (elonPick < 0 || elonPick >= Constants.CardsNum || markPick < 0 || markPick >= Constants.CardsNum)
        {
            throw new ArgumentException($"Invalid picks, elon: {elonPick} , mark: {markPick}");
        }
        return _markCards[elonPick].Color == _elonCards[markPick].Color;
    }


    public void SetDecks(Card[] elonDeck, Card[] markDeck)
    {
        _elonCards = elonDeck;
        _markCards = markDeck;
    }
}