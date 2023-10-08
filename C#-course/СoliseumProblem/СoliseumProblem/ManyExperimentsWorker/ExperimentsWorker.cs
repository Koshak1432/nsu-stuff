using ColiseumProblem.GodAndAssistant;
using ColiseumProblem.OneExperimentWorker;
using Microsoft.Extensions.Hosting;
using StrategiesLib;

namespace ColiseumProblem.ManyExperimentsWorker;

public class ExperimentsWorker : BackgroundService
{
    private readonly IColiseumSandbox _sandbox;
    private readonly IHostApplicationLifetime _lifetime; 

    
    // todo add customOrder?
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
            var strategies = new Strategies(new FirstRedStrategy(), new FirstRedStrategy());
            var watch = System.Diagnostics.Stopwatch.StartNew();
            for (var i = 0; i < Constants.NumExperiments; ++i)
            {
                positiveCount += _sandbox.RunExperiment(strategies);
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