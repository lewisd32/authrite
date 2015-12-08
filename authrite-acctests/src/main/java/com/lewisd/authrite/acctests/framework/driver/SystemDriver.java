package com.lewisd.authrite.acctests.framework.driver;

import com.lewisd.authrite.AuthriteServiceApplication;
import com.lewisd.authrite.AuthriteServiceConfiguration;
import com.lewisd.authrite.acctests.framework.database.H2DatabaseContext;
import io.dropwizard.testing.ConfigOverride;
import io.github.unacceptable.database.DatabaseContext;
import io.github.unacceptable.dropwizard.context.ApplicationContext;
import io.github.unacceptable.liquibase.LiquibaseEnhancer;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

public class SystemDriver {
    private final DatabaseContext databaseContext = LiquibaseEnhancer.configureContext(new H2DatabaseContext());

    private final ApplicationContext<AuthriteServiceConfiguration> appContext =
            new ApplicationContext<>(
                    AuthriteServiceApplication.class,
                    "src/main/resources/authrite-service.yml",
                    ConfigOverride.config("database.url", databaseContext.databaseUrl()),
                    ConfigOverride.config("database.user", databaseContext.username()),
                    ConfigOverride.config("database.password", databaseContext.password()));

    @Rule
    private final TestRule rules = rules();

    public String baseUrl() {
        return appContext.url();
    }

    public TestRule rules() {
        if (!appContext.isExternal()) {
            return RuleChain
                    .outerRule(databaseContext.rules())
                    .around(appContext.rules());
        } else {
            return new NoOpRule();
        }
    }

    public PublicApiDriver publicApiDriver() {
        return new PublicApiDriver();
    }

    private class NoOpRule extends ExternalResource {
    }
}
