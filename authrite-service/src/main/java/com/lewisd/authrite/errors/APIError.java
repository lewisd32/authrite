package com.lewisd.authrite.errors;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class APIError {
    private final String[] errors;

    @JsonCreator
    public APIError(@JsonProperty("errors") final String... errors) {
        this.errors = clone(errors);
    }

    @JsonProperty
    public String[] getErrors() {
        return clone(errors);
    }

    private static String[] clone(final String[] array) {
        return Arrays.copyOf(array, array.length);
    }

}
