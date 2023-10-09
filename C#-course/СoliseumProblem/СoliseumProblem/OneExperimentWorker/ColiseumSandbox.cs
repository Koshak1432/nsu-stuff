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
        _god = god;
        _assistant = assistant;
    }

    public int RunExperiment(IStrategiesWrapper strategies, string? customOrder = null)
    {
        var deck = _assistant.CreateDeck();
        _assistant.ShuffleDeck(deck, customOrder);
        var (elonCards, markCards) = _assistant.SplitDeck(deck);
        _god.SetDecks(elonCards, markCards);
        var (elonStrategy, markStrategy) = strategies.GetStrategies();
        var decision = _god.MakeDecision(elonStrategy, markStrategy);
        return decision ? 1 : 0;
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