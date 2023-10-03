using CardsLib;
using ColiseumProblem.GodAndAssistant;
using StrategiesLib;
using Xunit;

namespace Tests;

public class StrategiesTests
{
    private IGodAssistant _assistant = new GodAssistant();
    private const int CardsTotal = 36;

    [Fact]
    public void CheckFirstPickStrategy()
    {
        var deck = _assistant.CreateDeck();
        var halfDeck = new Card[CardsTotal / 2];
        Array.Copy(deck, 0, halfDeck, 0, CardsTotal / 2);
        var strategy = new PickFirstStrategy();

        Assert.Equal(0, strategy.Pick(halfDeck));
    }
}