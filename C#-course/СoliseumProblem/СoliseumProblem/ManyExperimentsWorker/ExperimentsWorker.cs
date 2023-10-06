using ColiseumProblem.OneExperimentWorker;
using Microsoft.Extensions.Hosting;

namespace ColiseumProblem.ManyExperimentsWorker;

public class ExperimentsWorker : BackgroundService
{
    private readonly IColiseumSandbox _sandbox;
    private readonly IHostApplicationLifetime _lifetime; 

    public ExperimentsWorker(IColiseumSandbox sandbox, IHostApplicationLifetime lifetime)
    {
        _sandbox = sandbox;
        _lifetime = lifetime;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await Task.Run(() =>
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
        }, CancellationToken.None);

        _lifetime.StopApplication();
    }
}