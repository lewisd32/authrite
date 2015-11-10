package com.lewisd.authrite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lewisd.authrite.auth.JWTConfiguration;
import com.lewisd.authrite.auth.PasswordManagementConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AuthriteServiceConfiguration extends Configuration {

    @Valid
    @NotNull
    private JWTConfiguration jwt = new JWTConfiguration();

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private PasswordManagementConfiguration passwordManagement = new PasswordManagementConfiguration();

    @JsonProperty
    public JWTConfiguration getJwt() {
        return jwt;
    }

    @JsonProperty
    public void setJwt(final JWTConfiguration jwt) {
        this.jwt = jwt;
    }

    @JsonProperty
    public DataSourceFactory getDatabase() {
        return database;
    }

    @JsonProperty
    public void setDatabase(final DataSourceFactory database) {
        this.database = database;
    }

    @JsonProperty
    public PasswordManagementConfiguration getPasswordManagement() {
        return passwordManagement;
    }

    @JsonProperty
    public void setPasswordManagement(final PasswordManagementConfiguration passwordManagement) {
        this.passwordManagement = passwordManagement;
    }
}
