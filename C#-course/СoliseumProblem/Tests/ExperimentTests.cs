using CardsLib;
using ColiseumProblem;
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
        var wrapper = new Mock<IStrategiesWrapper>();
        IColiseumSandbox sandbox = new ColiseumSandbox(godMock.Object, assistantMock.Object);
        sandbox.RunExperiment(wrapper.Object);

        assistantMock.Verify(a => a.CreateDeck(), Times.Once);
        assistantMock.Verify(a => a.ShuffleDeck(It.IsAny<Card[]>(), null), Times.Once);
    }
    
    
    [Fact]
    public void CorrectDecision()
    {
        IGod god = new God();
        IGodAssistant assistant = new GodAssistant();
        IColiseumSandbox sandbox = new ColiseumSandbox(god, assistant);
        var strategies = new Strategies(new FirstRedStrategy(), new FirstBlackStrategy());
        IStrategiesWrapper wrapper = new StrategiesWrapper(strategies);
        var customOrder = "BRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBR";
        
        Assert.Equal(0, sandbox.RunExperiment(wrapper, customOrder));
    }
    
    
}