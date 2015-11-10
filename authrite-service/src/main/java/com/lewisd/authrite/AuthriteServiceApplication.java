package com.lewisd.authrite;

import com.github.dirkraft.dropwizard.fileassets.FileAssetsBundle;
import com.lewisd.authrite.auth.JWTConfiguration;
import com.lewisd.authrite.auth.JwtTokenManager;
import com.lewisd.authrite.auth.PasswordManagementConfiguration;
import com.lewisd.authrite.resources.PlayersResource;
import com.lewisd.authrite.resources.UsersResource;
import com.lewisd.authrite.resources.model.User;
import com.lewisd.authrite.services.UsersService;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.java8.Java8Bundle;
import io.dropwizard.java8.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;

// CHECKSTYLE.OFF: ClassDataAbstractionCoupling
public class AuthriteServiceApplication extends Application<AuthriteServiceConfiguration> {

    // Set to `false` for development, true elsewhere.
    // `false` will load assets from src/main/resources, allowing a fast edit-reload cycle.
    private final boolean useClasspathAssets = Boolean.parseBoolean(System.getProperty("authrite.assets.useClasspath", "true"));

    public static void main(final String[] args) throws Exception {
        new AuthriteServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "authrite-service";
    }

    @Override
    public void initialize(final Bootstrap<AuthriteServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new Java8Bundle());

        if (useClasspathAssets) {
            bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
        } else {
            bootstrap.addBundle(new FileAssetsBundle("src/main/resources/assets", "/"));
        }

        bootstrap.addBundle(new MigrationsBundle<AuthriteServiceConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(final AuthriteServiceConfiguration configuration) {
                return configuration.getDatabase();
            }
        });
    }

    @Override
    public void run(final AuthriteServiceConfiguration configuration,
                    final Environment environment) {

        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(environment, configuration.getDatabase(), "h2");
        final JWTConfiguration jwtConfiguration = configuration.getJwt();
        final JwtTokenManager jwtTokenManager = jwtConfiguration.buildTokenManager();
        final PasswordManagementConfiguration passwordManagement = configuration.getPasswordManagement();
        final UsersService usersService = new UsersService(dbi, jwtTokenManager, passwordManagement);
        final UsersResource usersResource = new UsersResource(usersService, jwtConfiguration);
        environment.jersey().register(usersResource);

        final PlayersResource playersResource = new PlayersResource(dbi);
        environment.jersey().register(playersResource);

        environment.jersey().register(new AuthDynamicFeature(jwtConfiguration.buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        //Required to use @Auth to inject a custom Principal type into your resource
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }

}
