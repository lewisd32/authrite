package com.lewisd.authrite.acctests.model;

import com.lewisd.authrite.auth.Roles;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class User {

    private UUID id;
    private String email;
    private String displayName;
    private Set<Roles> roles;
    private Date createdDate;
    private Date modifiedDate;
    private Date deletedDate;
    private Date lastLoginDate;
    private Date lastPasswordChangeDate;
    private Date emailValidatedDate;

    public User() {
        // for deserialization
    }

    public User(UUID id, String email, String displayName, Set<Roles> roles) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
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
}
