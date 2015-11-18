package com.lewisd.authrite.acctests.framework.database;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MigrateDatabaseRule implements TestRule {
    private final String databaseUrl;
    private final String username;
    private final String password;

    public MigrateDatabaseRule(final String databaseUrl, final String username, final String password) {
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (Connection c = connect()) {
                    final Liquibase liquibase = makeLiquibase(c);
                    migrate(liquibase);
                }
                base.evaluate();
            }
        };
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(databaseUrl, username, password);
    }

    private void migrate(final Liquibase liquibase) throws LiquibaseException {
        final String contexts = null;
        liquibase.update(contexts);
    }

    private Liquibase makeLiquibase(final Connection connection) throws LiquibaseException {
        final String changeLogFile = "migrations.xml";
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(classLoader);
        final JdbcConnection jdbcConnection = new JdbcConnection(connection);

        return new Liquibase(changeLogFile, resourceAccessor, jdbcConnection);
    }
}
