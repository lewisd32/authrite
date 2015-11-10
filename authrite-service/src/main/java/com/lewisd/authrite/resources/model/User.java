package com.lewisd.authrite.resources.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lewisd.authrite.auth.Roles;
import com.lewisd.authrite.jdbc.model.DBUser;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User implements Principal {

    private final UUID id;
    @NotEmpty
    @Email(regexp = ".+@.+\\..+")
    private final String email;
    @NotEmpty
    @Length(min = 3, max = 20)
    private final String displayName;

    private final Set<Roles> roles;

    @JsonCreator
    public User(@JsonProperty("id") final UUID id,
                @JsonProperty("email") final String email,
                @JsonProperty("displayName") final String displayName,
                @JsonProperty("roles") final Set<Roles> roles) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.roles = roles == null ? Collections.emptySet() : new HashSet<>(roles);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public static User fromDB(final DBUser user) {
        return new User(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRoles());
    }

    @JsonIgnore
    @Override
    public String getName() {
        return email;
    }

    @Override
    public boolean implies(final Subject subject) {
        return false;
    }

    public boolean canRead(final Class<?> resourceClass, final UUID userId) {
        for (Roles role : roles) {
            if (role.canRead(resourceClass, this, userId)) {
                return true;
            }
        }
        return false;
    }

    public boolean canModify(final Class<?> resourceClass, final UUID userId) {
        for (Roles role : roles) {
            if (role.canWrite(resourceClass, this, userId)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean isAdminForReading() {
        return roles.contains(Roles.ADMIN) || roles.contains(Roles.RO_ADMIN);
    }
}
