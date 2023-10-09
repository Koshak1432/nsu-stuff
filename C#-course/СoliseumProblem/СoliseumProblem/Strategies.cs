using StrategiesLib;

namespace ColiseumProblem;

public record Strategies(ICardPickStrategy ElonStrategy, ICardPickStrategy MarkStrategy);

public class StrategiesWrapper : IStrategiesWrapper
{
    private readonly Strategies _strategies;

    public StrategiesWrapper(Strategies strategies)
    {
        _strategies = strategies;
    }
    
    public (ICardPickStrategy, ICardPickStrategy) GetStrategies()
    {
        return (_strategies.ElonStrategy, _strategies.MarkStrategy);
    }
}