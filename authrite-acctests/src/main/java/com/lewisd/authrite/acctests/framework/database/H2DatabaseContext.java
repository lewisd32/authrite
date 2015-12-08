package com.lewisd.authrite.acctests.framework.database;

import io.github.unacceptable.database.DatabaseContext;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

public class H2DatabaseContext extends DatabaseContext {

    @Override
    public String databaseUrl() {
        return "jdbc:h2:./target/" + databaseName();
    }

    @Override
    protected String defaultPassword() {
        return "sa";
    }

    @Override
    protected String defaultUsername() {
        return "sa";
    }

    @Override
    protected TestRule constructRules() {
        // Disable the default rule that creates a temporary database since H2 does it automatically.
        return new NoOpRule();
    }

    private class NoOpRule extends ExternalResource {
    }
}
