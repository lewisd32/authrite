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

    public MigrateDatabaseRule(String databaseUrl, String username, String password) {
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try (Connection c = connect()) {
                    Liquibase liquibase = makeLiquibase(c);
                    migrate(liquibase);
                }
                base.evaluate();
            }
        };
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(databaseUrl, username, password);
    }

    private void migrate(Liquibase liquibase) throws LiquibaseException {
        String contexts = null;
        liquibase.update(contexts);
    }

    private Liquibase makeLiquibase(Connection connection) throws LiquibaseException {
        String changeLogFile = "migrations.xml";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(classLoader);
        JdbcConnection jdbcConnection = new JdbcConnection(connection);

        return new Liquibase(changeLogFile, resourceAccessor, jdbcConnection);
    }
}
