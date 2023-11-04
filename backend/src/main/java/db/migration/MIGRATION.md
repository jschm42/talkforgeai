# Example Java code for Flyway migrations

    package db.migration;
    
    import org.flywaydb.core.api.migration.Context;
    
    import java.sql.Statement;
    
    public class V2__complex_data_transformation extends BaseJavaMigration {
    
        @Override
        public void migrate(Context context) throws Exception {
            try (Statement stmt = context.getConnection().createStatement()) {
                // Example: Migrate data from one table to another with some transformation
                stmt.execute("INSERT INTO new_table SELECT * FROM old_table WHERE some_condition");
    
                // ... other Java code to perform your migration
            }
        }
    
    }
