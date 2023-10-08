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
        var order = "BBBBBBBBBBBBBBBBBRRRRRRRRRRRRRRRRRRB";
        _assistant.ShuffleDeck(deck, order);
        var (firstDeck, secondDeck) = _assistant.SplitDeck(deck);
        ICardPickStrategy firstRed = new FirstRedStrategy();
        ICardPickStrategy firstBlack = new FirstBlackStrategy();

        Assert.Equal(17, firstRed.Pick(firstDeck));
        Assert.Equal(17, firstBlack.Pick(secondDeck));
    }
}