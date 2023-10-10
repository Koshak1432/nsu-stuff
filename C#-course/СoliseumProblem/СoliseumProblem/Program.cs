using ColiseumProblem.Db;
using ColiseumProblem.GodAndAssistant;
using ColiseumProblem.ManyExperimentsWorker;
using ColiseumProblem.OneExperimentWorker;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using StrategiesLib;

namespace ColiseumProblem;

class Program
{
    private static void Main(string[] args)
    {
        CreateHostBuilder(args).Build().Run();
    }

    private static IHostBuilder CreateHostBuilder(string[] args)
    {
        return Host.CreateDefaultBuilder(args)
            .ConfigureServices((hostContext, services) =>
            {
                services.AddHostedService<ExperimentsWorker>();
                services.AddScoped<IGodAssistant, GodAssistant>();
                services.AddScoped<IGod, God>();
                services.AddScoped<IColiseumSandbox, ColiseumSandbox>();
                services.AddScoped<ICardPickStrategy, FirstStrategy>();
                services.AddScoped<ICardPickStrategy, FirstRedStrategy>();
                services.AddScoped<ICardPickStrategy, FirstBlackStrategy>();
                services.AddScoped<ConditionRepository>();
                services.AddDbContext<ColiseumContext>(options =>
                    options.UseNpgsql(Constants.ConnectionString));

            });
    }

}