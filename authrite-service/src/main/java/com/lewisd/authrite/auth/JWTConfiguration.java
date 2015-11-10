package com.lewisd.authrite.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lewisd.authrite.resources.model.User;

public class JWTConfiguration {
    private String jwtSecret = "secret";
    private int jwtExpirySeconds = 3600;
    private String cookieName = "jwtToken";

    @JsonProperty
    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(final String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @JsonProperty
    public int getJwtExpirySeconds() {
        return jwtExpirySeconds;
    }

    public void setJwtExpirySeconds(final int jwtExpirySeconds) {
        this.jwtExpirySeconds = jwtExpirySeconds;
    }

    @JsonProperty
    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }

    public JWTAuthFilter<User> buildAuthFilter() {
        return new JWTAuthFilter.Builder<User>()
                .setCookieName(this.getCookieName())
                .setAuthenticator(new JWTAuthenticator(buildTokenManager()))
//                .setAuthorizer(new ExampleAuthorizer())
                .buildAuthFilter();
    }

    public JwtTokenManager buildTokenManager() {
        return new JwtTokenManager(jwtSecret, jwtExpirySeconds);
    }

}
