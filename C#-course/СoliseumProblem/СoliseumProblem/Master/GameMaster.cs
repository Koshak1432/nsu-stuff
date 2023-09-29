using CardsLib;
using StrategiesLib;

namespace ColiseumProblem.Master;

public class GameMaster : IJudge
{
    // replace by array of array of cards? and params in cnstr
    private readonly Card[] elonCards;
    private readonly Card[] markCards;

    public GameMaster(Card[] elonCards, Card[] markCards)
    {
        this.elonCards = elonCards;
        this.markCards = markCards;
    }

    public bool MakeDecision(ICardPickStrategy elonStrategy, ICardPickStrategy markStrategy)
    {
        var elonPick = elonStrategy.Pick(elonCards);
        var markPick = markStrategy.Pick(markCards);

        return markCards[elonPick].Color == elonCards[markPick].Color;
    }
}