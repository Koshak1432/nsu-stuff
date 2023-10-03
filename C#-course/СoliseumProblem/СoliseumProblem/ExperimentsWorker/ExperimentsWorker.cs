using ColiseumProblem.OneExperimentWorker;

namespace ColiseumProblem.ExperimentsWorker;

public class ExperimentsWorker : IExperimentsWorker
{
    private readonly IColiseumSandbox _sandbox;

    public ExperimentsWorker(IColiseumSandbox sandbox)
    {
        this._sandbox = sandbox;
    }
    
    public void RunExperiments(int numExperiments)
    {
        var positiveCount = 0f;
        var watch = System.Diagnostics.Stopwatch.StartNew();
        for (var i = 0; i < Constants.NumExperiments; ++i)
        {
            positiveCount += _sandbox.RunExperiment();
        }

        watch.Stop();
        var elapsedSeconds = watch.ElapsedMilliseconds;
        var ratio = positiveCount / Constants.NumExperiments;
        Console.WriteLine("RES RATIO: " + ratio);
        Console.WriteLine("TIME ELAPSED MS: " + elapsedSeconds);
    }
}