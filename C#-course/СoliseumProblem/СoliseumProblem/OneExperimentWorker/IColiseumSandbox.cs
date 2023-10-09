namespace ColiseumProblem.OneExperimentWorker;

public interface IColiseumSandbox
{
    public int RunExperiment(IStrategiesWrapper strategies, string? customOrder = null);
}