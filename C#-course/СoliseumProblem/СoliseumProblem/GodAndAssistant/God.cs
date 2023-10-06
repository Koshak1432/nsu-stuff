using CardsLib;
using StrategiesLib;

namespace ColiseumProblem.GodAndAssistant;

public class God : IGod
{
    // replace by array of array of cards? and params in cnstr
    private Card[]? _elonCards;
    private Card[]? _markCards;

    public bool MakeDecision(ICardPickStrategy elonStrategy, ICardPickStrategy markStrategy)
    {
        if (_elonCards == null || _markCards == null)
        {
            throw new NullReferenceException("Elon or/and mark cards are null");
        }
        var elonPick = elonStrategy.Pick(_elonCards);
        var markPick = markStrategy.Pick(_markCards);

        return _markCards[elonPick].Color == _elonCards[markPick].Color;
    }


    public void SetDecks(Card[] elonDeck, Card[] markDeck)
    {
        _elonCards = elonDeck;
        _markCards = markDeck;
    }
}