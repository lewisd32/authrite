package com.lewisd.authrite.auth;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordManagementConfiguration {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 100;

    private int bcryptCost;

    @JsonProperty
    public int getBcryptCost() {
        return bcryptCost;
    }

    @JsonProperty
    public void setBcryptCost(final int bcryptCost) {
        this.bcryptCost = bcryptCost;
    }
}
