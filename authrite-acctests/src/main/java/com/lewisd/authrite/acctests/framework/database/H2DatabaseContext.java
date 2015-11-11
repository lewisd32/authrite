package com.lewisd.authrite.acctests.framework.database;

import io.github.unacceptable.database.DatabaseContext;
import org.junit.rules.TestRule;

public class H2DatabaseContext extends DatabaseContext {

    @Override
    public String databaseUrl() {
        return "jdbc:h2:./target/" + databaseName();
    }

    @Override
    public TestRule rules() {
        // Don't chain because we don't need the TemporaryDatabaseRule for H2.
        return makeMigrationRule();
    }

    private MigrateDatabaseRule makeMigrationRule() {
        return new MigrateDatabaseRule(databaseUrl(), username(), password());
    }

    @Override
    protected String defaultPassword() {
        return "sa";
    }

    @Override
    protected String defaultUsername() {
        return "sa";
    }
}
