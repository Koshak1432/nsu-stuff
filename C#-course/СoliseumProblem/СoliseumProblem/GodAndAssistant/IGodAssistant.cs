using CardsLib;

namespace ColiseumProblem.GodAndAssistant;

public interface IGodAssistant
{
    public void ShuffleDeck(Card[] cards, string? customOrder = null);

    public Card[] CreateDeck();

    public SplitDeck SplitDeck(Card[] deck);
}