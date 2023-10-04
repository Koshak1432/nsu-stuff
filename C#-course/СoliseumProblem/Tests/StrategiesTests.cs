using CardsLib;
using ColiseumProblem;
using ColiseumProblem.GodAndAssistant;
using StrategiesLib;
using Xunit;

namespace Tests;

public class StrategiesTests
{
    private readonly IGodAssistant _assistant = new GodAssistant();

    [Fact]
    public void CheckFirstPickStrategy()
    {
        var deck = _assistant.CreateDeck();
        var halfDeck = new Card[Constants.CardsNum / 2];
        Array.Copy(deck, 0, halfDeck, 0, Constants.CardsNum / 2);
        var strategy = new PickFirstStrategy();

        Assert.Equal(0, strategy.Pick(halfDeck));
    }
}