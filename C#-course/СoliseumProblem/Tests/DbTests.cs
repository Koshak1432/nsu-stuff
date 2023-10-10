using ColiseumProblem.Db;
using ColiseumProblem.Entities;
using ColiseumProblem.GodAndAssistant;
using Microsoft.EntityFrameworkCore;
using Xunit;

namespace Tests;

public class DbTests
{
    [Fact]
    public void SaveAndReadConditions()
    {
        var numExperiments = 100;
        var options =
            new DbContextOptionsBuilder<ColiseumContext>().UseInMemoryDatabase(Guid.NewGuid().ToString()).Options;
        IGodAssistant assistant = new GodAssistant();
        using (var context = new ColiseumContext(options))
        {
            var repository = new ConditionRepository(context);
            for (var i = 0; i < numExperiments; ++i)
            {
                assistant.CreateDeck();
                var order = assistant.GetDeckOrder();
                var condtition = new ExperimentCondition()
                {
                    condition = order
                };
                repository.Save(condtition);
            }

            var experiments = context.experiments_conditions.ToList();
            Assert.Equal(numExperiments, experiments.Count);
        }
    }
}