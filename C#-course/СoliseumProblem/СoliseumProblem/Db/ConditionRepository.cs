using ColiseumProblem.Entities;

namespace ColiseumProblem.Db;

public class ConditionRepository
{
    private readonly ColiseumContext _context;

    public ConditionRepository(ColiseumContext context)
    {
        _context = context;
    }

    public void Save(ExperimentCondition condition)
    {
        _context.experiments_conditions.Add(condition);
        _context.SaveChanges();
    }
}