using CardsLib;
using ColiseumProblem.GodAndAssistant;
using ColiseumProblem.OneExperimentWorker;
using Moq;
using StrategiesLib;
using Xunit;

namespace Tests;

public class ExperimentTests
{
    [Fact]
    public void CreateAndShuffleDeckOnce()
    {
        var assistantMock = new Mock<IGodAssistant>();
        var godMock = new Mock<IGod>();
        IColiseumSandbox sandbox = new ColiseumSandbox(godMock.Object, assistantMock.Object);
        sandbox.RunExperiment();

        assistantMock.Verify(a => a.CreateDeck(), Times.Once);
        assistantMock.Verify(a => a.ShuffleDeck(It.IsAny<Card[]>()), Times.Once);

    }

    [Fact]
    public void CorrectDecision()
    {
        IGodAssistant assistant = new GodAssistant();
        IGod god = new God();
        var deck = assistant.CreateDeck();
        var splitDeck = assistant.SplitDeck(deck);
        var elonCards = splitDeck.Item1;
        var markCards = splitDeck.Item2;
        god.SetDecks(elonCards, markCards);
        var decision = god.MakeDecision(new PickFirstStrategy(), new PickFirstStrategy());
        Assert.False(decision);
    }
    
    
}