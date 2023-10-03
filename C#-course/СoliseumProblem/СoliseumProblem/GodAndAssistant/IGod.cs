using CardsLib;
using StrategiesLib;

namespace ColiseumProblem.GodAndAssistant;

public interface IGod
{
    // maybe add params(variable arguments)
    // как сделать расширяемым?
    public bool MakeDecision(ICardPickStrategy elonStrategy, ICardPickStrategy markStrategy);

    public void SetDecks(Card[] elonDeck, Card[] markDeck);
    
}