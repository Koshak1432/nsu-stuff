using CardsLib;

namespace ColiseumProblem.GodAndAssistant;

public interface IGodAssistant
{
    public void ShuffleDeck(Card[] cards);

    public Card[] CreateDeck();

    public (Card[], Card[]) SplitDeck(Card[] deck);
}