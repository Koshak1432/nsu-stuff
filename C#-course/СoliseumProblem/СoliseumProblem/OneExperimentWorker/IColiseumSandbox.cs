namespace ColiseumProblem.OneExperimentWorker;

public interface IColiseumSandbox
{
    public int RunExperiment(Strategies strategies, string? customOrder = null);
}