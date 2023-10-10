using CardsLib;
using ColiseumProblem;
using ColiseumProblem.Db;
using ColiseumProblem.GodAndAssistant;
using ColiseumProblem.OneExperimentWorker;
using Microsoft.EntityFrameworkCore;
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
        var clientMock = new Mock<HttpClient>();
        IColiseumSandbox sandbox = new ColiseumSandbox(godMock.Object, assistantMock.Object, clientMock.Object);
        sandbox.RunExperiment();
        
        assistantMock.Verify(a => a.CreateDeck(), Times.Once);
        assistantMock.Verify(a => a.ShuffleDeck(It.IsAny<Card[]>(), null), Times.Once);
    }
    
    
    [Fact]
    public void CorrectDecision()
    {
        IGod god = new God();
        IGodAssistant assistant = new GodAssistant();
        var clientMock = new Mock<HttpClient>();

        IColiseumSandbox sandbox = new ColiseumSandbox(god, assistant, clientMock.Object);
        var strategies = new Strategies(new FirstRedStrategy(), new FirstBlackStrategy());
        IStrategiesWrapper wrapper = new StrategiesWrapper(strategies);
        var customOrder = "BRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBRBR";
        
        Assert.Equal(0, sandbox.RunExperiment(customOrder));
    }
    
    
}