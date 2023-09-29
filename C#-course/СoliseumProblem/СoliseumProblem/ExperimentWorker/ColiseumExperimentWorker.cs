using CardsLib;
using ColiseumProblem.Master;
using StrategiesLib;

namespace ColiseumProblem.ExperimentWorker;

// проводит 1 эксперимент
public class ColiseumExperimentWorker : IColiseumExperimentWorker
{
    public int RunExperiment()
    {
        MasterAssistant assistant = new MasterAssistant();
        var cards = assistant.CreateDeck();
        assistant.ShuffleDeck(cards);
        
        var elonCards = new Card[Constants.CardsNum / 2];
        var markCards = new Card[Constants.CardsNum / 2];
        Array.Copy(cards, 0, elonCards, 0, Constants.CardsNum / 2);
        Array.Copy(cards, Constants.CardsNum / 2, markCards, 0, Constants.CardsNum / 2);

        IJudge master = new GameMaster(elonCards, markCards);

        // Console.WriteLine("ELON:");
        // PrintPlayerCards(elonCards);
        // Console.WriteLine("MARK:");
        // PrintPlayerCards(markCards);

        ICardPickStrategy elonStrategy = new PickFirstStrategy();
        ICardPickStrategy markStrategy = new PickFirstStrategy();
        var decision = master.MakeDecision(elonStrategy, markStrategy);
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