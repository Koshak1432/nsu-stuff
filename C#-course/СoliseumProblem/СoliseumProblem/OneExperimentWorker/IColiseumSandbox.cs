namespace ColiseumProblem.OneExperimentWorker;

public interface IColiseumSandbox
{
    public Task<int> RunExperiment(string? customOrder = null);
}