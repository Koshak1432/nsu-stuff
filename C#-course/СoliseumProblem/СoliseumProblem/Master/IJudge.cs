using StrategiesLib;

namespace ColiseumProblem.Master;

public interface IJudge
{
    // maybe add params(variable arguments)
    public bool MakeDecision(ICardPickStrategy elonStrategy, ICardPickStrategy markStrategy);


}