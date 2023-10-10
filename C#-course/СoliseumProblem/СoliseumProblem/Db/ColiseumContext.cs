using ColiseumProblem.Entities;
using Microsoft.EntityFrameworkCore;

namespace ColiseumProblem.Db;

public class ColiseumContext : DbContext
{
    public ColiseumContext()
    {
        
    }
    
    public ColiseumContext(DbContextOptions<ColiseumContext> options) : base(options)
    {
    }
    
    public DbSet<ExperimentCondition> experiments_conditions { get; set; }
}