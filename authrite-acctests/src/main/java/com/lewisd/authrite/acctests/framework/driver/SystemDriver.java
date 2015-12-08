package com.lewisd.authrite.acctests.framework.driver;

import com.lewisd.authrite.AuthriteServiceApplication;
import com.lewisd.authrite.AuthriteServiceConfiguration;
import com.lewisd.authrite.acctests.framework.database.H2DatabaseContext;
import io.dropwizard.testing.ConfigOverride;
import io.github.unacceptable.database.DatabaseContext;
import io.github.unacceptable.dropwizard.context.ApplicationContext;
import io.github.unacceptable.dropwizard.system.AbstractSystemDriver;
import io.github.unacceptable.liquibase.LiquibaseEnhancer;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

public class SystemDriver extends AbstractSystemDriver {
    private final DatabaseContext databaseContext = LiquibaseEnhancer.configureContext(new H2DatabaseContext());

    private final ApplicationContext<AuthriteServiceConfiguration> appContext =
            new ApplicationContext<>(
                    AuthriteServiceApplication.class,
                    "src/main/resources/authrite-service.yml",
                    ConfigOverride.config("database.url", databaseContext.databaseUrl()),
                    ConfigOverride.config("database.user", databaseContext.username()),
                    ConfigOverride.config("database.password", databaseContext.password()));

    @Override
    protected String internalAppBaseUrl() {
        return appContext.url();
    }

    @Override
    protected TestRule internalAppRules() {
        return RuleChain
                .outerRule(databaseContext.rules())
                .around(appContext.rules());
    }

    public PublicApiDriver publicApiDriver() {
        return new PublicApiDriver();
    }
}
