using CardsLib;
using ColiseumProblem;
using ColiseumProblem.GodAndAssistant;
using Xunit;

namespace Tests;

public class DeckTests
{
    private readonly IGodAssistant _assistant = new GodAssistant();
    
    [Fact]
    public void CheckCardsNumWithoutShuffle()
    {
        var deck = _assistant.CreateDeck();
        var blackNum = 0;
        var redNum = 0;
        CountCards(deck, ref blackNum, ref redNum);
        Assert.Equal(Constants.CardsNum / 2, blackNum);
        Assert.Equal(Constants.CardsNum / 2, redNum);
    }
    
    [Fact]
    public void CheckCardsNumWithShuffle()
    {
        var deck = _assistant.CreateDeck();
        var blackNum = 0;
        var redNum = 0;
        _assistant.ShuffleDeck(deck);
        CountCards(deck, ref blackNum, ref redNum);
        Assert.Equal(Constants.CardsNum / 2, blackNum);
        Assert.Equal(Constants.CardsNum / 2, redNum);
    }

    private static void CountCards(Card[] deck, ref int blackNum, ref int redNum)
    {
        foreach (var card in deck)
        {
            if (card.Color == CardColor.Black)
            {
                blackNum++;
            }
            else
            {
                redNum++;
            }
        }
    }
}