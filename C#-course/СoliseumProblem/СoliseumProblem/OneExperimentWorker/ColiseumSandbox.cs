using CardsLib;
using ColiseumProblem.GodAndAssistant;
using StrategiesLib;

namespace ColiseumProblem.OneExperimentWorker;

// проводит 1 эксперимент
public class ColiseumSandbox : IColiseumSandbox
{
    private readonly IGod _god;
    private readonly IGodAssistant _assistant;

    public ColiseumSandbox(IGod god, IGodAssistant assistant)
    {
        this._god = god;
        this._assistant = assistant;
    }

    public int RunExperiment()
    {
        var cards = _assistant.CreateDeck();
        _assistant.ShuffleDeck(cards);

        var elonCards = new Card[Constants.CardsNum / 2];
        var markCards = new Card[Constants.CardsNum / 2];
        Array.Copy(cards, 0, elonCards, 0, Constants.CardsNum / 2);
        Array.Copy(cards, Constants.CardsNum / 2, markCards, 0, Constants.CardsNum / 2);

        _god.SetDecks(elonCards, markCards);
        var decision = _god.MakeDecision(new PickFirstStrategy(), new PickFirstStrategy());
        return (decision ? 1 : 0);
    }

    private static void PrintPlayerCards(Card[] cards)
    {
        foreach (var card in cards)
        {
            Console.Write(card + " ");
        }

        Console.Write("\n");
    }
}