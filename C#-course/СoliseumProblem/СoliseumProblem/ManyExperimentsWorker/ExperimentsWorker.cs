using ColiseumProblem.Db;
using ColiseumProblem.OneExperimentWorker;
using Microsoft.Extensions.Hosting;
using StrategiesLib;

namespace ColiseumProblem.ManyExperimentsWorker;

public class ExperimentsWorker : BackgroundService
{
    private readonly IColiseumSandbox _sandbox;
    private readonly IHostApplicationLifetime _lifetime;
    private readonly ColiseumContext _context;
    
    public ExperimentsWorker(IColiseumSandbox sandbox, IHostApplicationLifetime lifetime, ColiseumContext context)
    {
        _sandbox = sandbox;
        _lifetime = lifetime;
        _context = context;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await Task.Run(() =>
        {
            var positiveCount = 0f;
            
            // var strategies = new Strategies(new FirstRedStrategy(), new FirstRedStrategy());
            // var strategiesWrapper = new StrategiesWrapper(strategies);
            var watch = System.Diagnostics.Stopwatch.StartNew();
            var experimentsConditions = _context.experiments_conditions.ToList();
            foreach (var condition in experimentsConditions)
            {
                positiveCount += _sandbox.RunExperiment(condition.condition);
            }

            watch.Stop();
            var elapsedSeconds = watch.ElapsedMilliseconds;
            var ratio = positiveCount / experimentsConditions.Count;
            Console.WriteLine("RES RATIO: " + ratio);
            Console.WriteLine("TIME ELAPSED MS: " + elapsedSeconds);
        }, CancellationToken.None);

        _lifetime.StopApplication();
    }
}