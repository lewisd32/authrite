package com.lewisd.authrite.auth;

import com.lewisd.authrite.resources.model.Player;
import com.lewisd.authrite.resources.model.User;

import java.util.UUID;

public enum Roles {
    PLAYER {
        @Override
        protected boolean canReadUser(final User principal, final UUID userId) {
            return userId.equals(principal.getId());
        }

        @Override
        protected boolean canWriteUser(final User principal, final UUID userId) {
            return userId.equals(principal.getId());
        }
    },
    RO_ADMIN {
        @Override
        protected boolean canReadUser(final User principal, final UUID userId) {
            return true;
        }

        @Override
        protected boolean canWriteUser(final User principal, final UUID userId) {
            return userId.equals(principal.getId());
        }
    },
    ADMIN {
        @Override
        protected boolean canReadUser(final User principal, final UUID userId) {
            return true;
        }

        @Override
        protected boolean canWriteUser(final User principal, final UUID userId) {
            return true;
        }
    };

    public String getName() {
        return name();
    }

    public boolean canRead(final Class<?> resourceClass, final User principal, final UUID resourceId) {
        if (resourceClass == User.class) {
            return canReadUser(principal, resourceId);
        }
        return false;
    }

    protected abstract boolean canReadUser(User principal, UUID userId);

    public boolean canWrite(final Class<?> resourceClass, final User principal, final UUID resourceId) {
        if (resourceClass == User.class) {
            return canWriteUser(principal, resourceId);
        } else if (resourceClass == Player.class) {
            // TODO: Implement this
            return false;
        }
        return false;
    }

    protected abstract boolean canWriteUser(User principal, UUID userId);
}
