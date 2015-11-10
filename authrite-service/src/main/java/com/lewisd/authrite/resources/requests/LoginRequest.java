package com.lewisd.authrite.resources.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class LoginRequest {
    @NotEmpty
    private final String email;
    @NotEmpty
    private final String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("email") final String email,
                        @JsonProperty("password") final String password) {
        this.email = email;
        this.password = password;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }
}
