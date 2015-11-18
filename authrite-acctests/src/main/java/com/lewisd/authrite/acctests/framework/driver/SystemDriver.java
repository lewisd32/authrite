package com.lewisd.authrite.acctests.framework.driver;

import com.lewisd.authrite.AuthriteServiceApplication;
import com.lewisd.authrite.AuthriteServiceConfiguration;
import com.lewisd.authrite.acctests.framework.database.H2DatabaseContext;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.github.unacceptable.database.DatabaseContext;
import io.github.unacceptable.lazy.Lazily;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SystemDriver {
    private static final ConfigOverride LOG_THRESHOLD = ConfigOverride.config("logging.level", System.getProperty("app.logging.threshold", "INFO"));
    private static final String ACCTEST_BASEURI_PROPERTY = "acctest.baseuri";

    private final DatabaseContext databaseContext = new H2DatabaseContext();

    private final DropwizardAppRule<AuthriteServiceConfiguration> appRule = new DropwizardAppRule<>(
            AuthriteServiceApplication.class,
            "src/main/resources/authrite-service.yml",
            LOG_THRESHOLD,
            ConfigOverride.config("database.url", databaseContext.databaseUrl()),
            ConfigOverride.config("database.user", databaseContext.username()),
            ConfigOverride.config("database.password", databaseContext.password()));

    private String baseUrl = null;

    public String baseUrl() {
        //CHECKSTYLE.OFF: InnerAssignment
        return baseUrl = Lazily.create(baseUrl, this::buildBaseUrl);
        //CHECKSTYLE.ON: InnerAssignment
    }

    private String buildBaseUrl() {
        final String propertyValue = System.getProperty(ACCTEST_BASEURI_PROPERTY);
        if (propertyValue != null) {
            return propertyValue;
        } else {
            return String.format("http://localhost:%d/", appRule.getLocalPort());
        }
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
        return new PublicApiDriver();
    }

}
