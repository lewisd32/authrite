package com.lewisd.authrite.resources.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lewisd.authrite.auth.Roles;
import com.lewisd.authrite.jdbc.model.DBUser;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class UserWithDates extends User {

    private final Date createdDate;
    private final Date modifiedDate;
    private final Date deletedDate;
    private final Date lastLoginDate;
    private final Date lastPasswordChangeDate;
    private final Date emailValidatedDate;

    // CHECKSTYLE.OFF: ParameterNumber
    public UserWithDates(@JsonProperty("id") final UUID id,
                         @JsonProperty("email") final String email,
                         @JsonProperty("displayName") final String displayName,
                         @JsonProperty("roles") final Set<Roles> roles,
                         @JsonProperty("createdDate") final Date createdDate,
                         @JsonProperty("modifiedDate") final Date modifiedDate,
                         @JsonProperty("deletedDate") final Date deletedDate,
                         @JsonProperty("lastLoginDate") final Date lastLoginDate,
                         @JsonProperty("lastPasswordChangeDate") final Date lastPasswordChangeDate,
                         @JsonProperty("emailValidatedDate") final Date emailValidatedDate) {
        // CHECKSTYLE.ON: ParameterNumber
        super(id, email, displayName, roles);
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.deletedDate = deletedDate;
        this.lastLoginDate = lastLoginDate;
        this.lastPasswordChangeDate = lastPasswordChangeDate;
        this.emailValidatedDate = emailValidatedDate;
    }

    @JsonProperty
    public Date getCreatedDate() {
        return createdDate;
    }

    @JsonProperty
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @JsonProperty
    public Date getDeletedDate() {
        return deletedDate;
    }

    @JsonProperty
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    @JsonProperty
    public Date getLastPasswordChangeDate() {
        return lastPasswordChangeDate;
    }

    @JsonProperty
    public Date getEmailValidatedDate() {
        return emailValidatedDate;
    }

    public static UserWithDates fromDB(final DBUser user) {
        return new UserWithDates(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRoles(),
                user.getCreatedDate(),
                user.getModifiedDate(),
                user.getDeletedDate(),
                user.getLastLoginDate(),
                user.getLastPasswordChangeDate(),
                user.getEmailValidatedDate());
    }

}
