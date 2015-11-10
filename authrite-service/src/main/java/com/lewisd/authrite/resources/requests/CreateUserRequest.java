package com.lewisd.authrite.resources.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lewisd.authrite.auth.PasswordManagementConfiguration;
import com.lewisd.authrite.resources.model.User;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

public class CreateUserRequest {

    @Valid
    private final User user;

    @NotEmpty
    @Length(min = PasswordManagementConfiguration.MIN_LENGTH, max = PasswordManagementConfiguration.MAX_LENGTH)
    private final String password;

    @JsonCreator
    public CreateUserRequest(@JsonProperty("user") final User user,
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
