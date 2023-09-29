using ColiseumProblem.ExperimentWorker;

namespace ColiseumProblem;

class Program
{
    private static void Main(string[] args)
    {
        IColiseumExperimentWorker worker = new ColiseumExperimentWorker();
        var positiveCount = 0f;
        var watch = System.Diagnostics.Stopwatch.StartNew();
        for (var i = 0; i < Constants.NumExperiments; ++i)
        {
            positiveCount += worker.RunExperiment();
        }
        watch.Stop();
        var elapsedSeconds = watch.ElapsedMilliseconds / 1000;
        var ratio = positiveCount / Constants.NumExperiments;
        Console.WriteLine("RES RATIO: " + ratio);
        Console.WriteLine("TIME ELAPSED: " + elapsedSeconds);
    }
    
}