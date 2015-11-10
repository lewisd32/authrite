package com.lewisd.authrite.jdbc.model;

import com.lewisd.authrite.auth.PasswordDigest;
import com.lewisd.authrite.auth.Roles;

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
                  final Date createdDate,
                  final Date modifiedDate,
                  final Date deletedDate,
                  final Date lastLoginDate,
                  final Date lastPasswordChangeDate,
                  final Date emailValidatedDate,
                  final String email,
                  final String displayName,
                  final PasswordDigest passwordDigest,
                  final Set<Roles> roles) {
        // CHECKSTYLE.ON: ParameterNumber
        this.id = id;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.deletedDate = deletedDate;
        this.lastLoginDate = lastLoginDate;
        this.lastPasswordChangeDate = lastPasswordChangeDate;
        this.emailValidatedDate = emailValidatedDate;
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
        return createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(final Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(final Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(final Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastPasswordChangeDate() {
        return lastPasswordChangeDate;
    }

    public void setLastPasswordChangeDate(final Date lastPasswordChangeDate) {
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public Date getEmailValidatedDate() {
        return emailValidatedDate;
    }

    public void setEmailValidatedDate(final Date emailValidatedDate) {
        this.emailValidatedDate = emailValidatedDate;
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
}
