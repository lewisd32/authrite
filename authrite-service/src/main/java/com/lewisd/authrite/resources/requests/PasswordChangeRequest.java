package com.lewisd.authrite.resources.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lewisd.authrite.auth.PasswordManagementConfiguration;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class PasswordChangeRequest {
    @NotEmpty
    private final String oldPassword;

    @NotEmpty
    @Length(min = PasswordManagementConfiguration.MIN_LENGTH, max = PasswordManagementConfiguration.MAX_LENGTH)
    private final String newPassword;

    @JsonCreator
    public PasswordChangeRequest(
            @JsonProperty("oldPassword") final String oldPassword,
            @JsonProperty("newPassword") final String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    @JsonProperty
    public String getOldPassword() {
        return oldPassword;
    }

    @JsonProperty
    public String getNewPassword() {
        return newPassword;
    }
}
