using StrategiesLib;

namespace ColiseumProblem;

public interface IStrategiesWrapper
{
    public (ICardPickStrategy elonStrategy, ICardPickStrategy markStrategy) GetStrategies();
}