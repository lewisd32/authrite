package com.lewisd.authrite.services;

import java.util.UUID;

public class LoginResult {
    private final UUID userId;
    private final String token;

    public LoginResult(final UUID userId, final String token) {
        this.userId = userId;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
