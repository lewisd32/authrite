package com.lewisd.authrite.errors;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class APIError {
    private final String[] errors;

    @JsonCreator
    public APIError(@JsonProperty("errors") final String... errors) {
        this.errors = errors;
    }

    @JsonProperty
    public String[] getErrors() {
        return errors;
    }
}
