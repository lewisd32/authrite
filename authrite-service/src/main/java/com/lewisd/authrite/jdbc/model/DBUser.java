package com.lewisd.authrite.jdbc.model;

import com.lewisd.authrite.auth.PasswordDigest;
import com.lewisd.authrite.auth.Roles;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DBUser {
    private UUID id;
    private Date createdDate;
    private Date modifiedDate;
    private Date deletedDate;
    private Date lastLoginDate;
    private Date lastPasswordChangeDate;
    private Date emailValidatedDate;
    private String email;
    private String displayName;
    private PasswordDigest passwordDigest;
    private Set<Roles> roles;

    // CHECKSTYLE.OFF: ParameterNumber
    public DBUser(final UUID id,
                  @Nullable final Date createdDate,
                  @Nullable final Date modifiedDate,
                  @Nullable final Date deletedDate,
                  @Nullable final Date lastLoginDate,
                  @Nullable final Date lastPasswordChangeDate,
                  @Nullable final Date emailValidatedDate,
                  final String email,
                  final String displayName,
                  final PasswordDigest passwordDigest,
                  final Set<Roles> roles) {
        // CHECKSTYLE.ON: ParameterNumber
        this.id = id;
        this.createdDate = clone(createdDate);
        this.modifiedDate = clone(modifiedDate);
        this.deletedDate = clone(deletedDate);
        this.lastLoginDate = clone(lastLoginDate);
        this.lastPasswordChangeDate = clone(lastPasswordChangeDate);
        this.emailValidatedDate = clone(emailValidatedDate);
        this.email = email;
        this.displayName = displayName;
        this.passwordDigest = passwordDigest;
        this.roles = new HashSet<>(roles);
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return clone(createdDate);
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = clone(createdDate);
    }

    public Date getModifiedDate() {
        return clone(modifiedDate);
    }

    public void setModifiedDate(final Date modifiedDate) {
        this.modifiedDate = clone(modifiedDate);
    }

    public Date getDeletedDate() {
        return clone(deletedDate);
    }

    public void setDeletedDate(final Date deletedDate) {
        this.deletedDate = clone(deletedDate);
    }

    public Date getLastLoginDate() {
        return clone(lastLoginDate);
    }

    public void setLastLoginDate(final Date lastLoginDate) {
        this.lastLoginDate = clone(lastLoginDate);
    }

    public Date getLastPasswordChangeDate() {
        return clone(lastPasswordChangeDate);
    }

    public void setLastPasswordChangeDate(final Date lastPasswordChangeDate) {
        this.lastPasswordChangeDate = clone(lastPasswordChangeDate);
    }

    public Date getEmailValidatedDate() {
        return clone(emailValidatedDate);
    }

    public void setEmailValidatedDate(final Date emailValidatedDate) {
        this.emailValidatedDate = clone(emailValidatedDate);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public PasswordDigest getPasswordDigest() {
        return passwordDigest;
    }

    public void setPasswordDigest(final PasswordDigest passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(final Set<Roles> roles) {
        this.roles = roles;
    }

    private static Date clone(final Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

}
