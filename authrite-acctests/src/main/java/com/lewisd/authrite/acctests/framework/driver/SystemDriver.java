package com.lewisd.authrite.acctests.framework.driver;

import com.lewisd.authrite.acctests.framework.Lazily;
import com.lewisd.authrite.acctests.framework.database.DatabaseContext;
import com.lewisd.authrite.AuthriteServiceApplication;
import com.lewisd.authrite.AuthriteServiceConfiguration;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.ws.rs.client.Client;

public class SystemDriver {
    private static final int SELENIUM_WAIT_SECONDS = Integer.getInteger("selenium.timeout", 2);
    private static final String DATABASE_ADMIN_URL = System.getProperty("database.adminUrl", "jdbc:h2:./target/example");
    private static final String DATABASE_USERNAME = System.getProperty("database.username", "sa");
    private static final String DATABASE_PASSWORD = System.getProperty("database.password", "sa");

    private static final ConfigOverride LOG_THRESHOLD = ConfigOverride.config("logging.level", System.getProperty("app.logging.threshold", "INFO"));
    private static final String ACCTEST_BASEURI_PROPERTY = "acctest.baseuri";

    private final DatabaseContext databaseContext = new DatabaseContext(DATABASE_ADMIN_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

    private final DropwizardAppRule<AuthriteServiceConfiguration> appRule = new DropwizardAppRule<>(AuthriteServiceApplication.class,
            "src/main/resources/authrite-service.yml",
            LOG_THRESHOLD,
            databaseContext.databaseUrl(),
            databaseContext.user(),
            databaseContext.password());

    private String baseUrl = null;
    private Client publicApiClient = null;

    public String baseUrl() {
        return baseUrl = Lazily.create(baseUrl, () -> {
            String propertyValue = System.getProperty(ACCTEST_BASEURI_PROPERTY);
            if (propertyValue != null) {
                return propertyValue;
            } else {
                return String.format("http://localhost:%d/", appRule.getLocalPort());
            }
        });
    }

    public TestRule rules() {
        if (System.getProperty(ACCTEST_BASEURI_PROPERTY) == null) {
            return RuleChain
                    .outerRule(databaseContext.rules())
                    .around(appRule);
        } else {
            return new TestRule() {
                @Override
                public Statement apply(final Statement base, final Description description) {
                    return base;
                }
            };
        }
    }

    public PublicApiDriver publicApiDriver() {
        return new PublicApiDriver(this, baseUrl());
    }

}
