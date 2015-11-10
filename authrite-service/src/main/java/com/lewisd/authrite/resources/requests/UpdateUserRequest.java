package com.lewisd.authrite.resources.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lewisd.authrite.resources.model.User;

import javax.validation.Valid;

public class UpdateUserRequest {

    @Valid
    private final User user;

    private final String password;

    @JsonCreator
    public UpdateUserRequest(@JsonProperty("user") final User user,
                             @JsonProperty("password") final String password) {
        this.user = user;
        this.password = password;
    }

    @JsonProperty
    public User getUser() {
        return user;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }
}
