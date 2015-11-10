package com.lewisd.authrite.services;

import com.google.common.collect.Sets;
import com.lewisd.authrite.auth.JwtTokenManager;
import com.lewisd.authrite.auth.PasswordDigest;
import com.lewisd.authrite.auth.PasswordManagementConfiguration;
import com.lewisd.authrite.auth.Roles;
import com.lewisd.authrite.errors.Exceptions;
import com.lewisd.authrite.jdbc.dao.UsersDao;
import com.lewisd.authrite.jdbc.model.DBUser;
import com.lewisd.authrite.resources.model.User;
import com.lewisd.authrite.resources.model.UserWithDates;
import com.lewisd.authrite.resources.requests.UpdateUserRequest;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UsersService {
    public static final String EXPECTED_ONE_ROW_UPDATED_FORMAT = "Expected 1 row updated, was %d";
    public static final String YOU_DO_NOT_HAVE_PERMISSION_TO_UPDATE_THIS_USER = "You do not have permission to update this user";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INCORRECT_PASSWORD = "Incorrect password";
    private final DBI dbi;
    private final JwtTokenManager jwtTokenManager;
    private final PasswordManagementConfiguration passwordManagementConfiguration;

    public UsersService(final DBI dbi, final JwtTokenManager jwtTokenManager,
                        final PasswordManagementConfiguration passwordManagementConfiguration) {
        this.dbi = dbi;
        this.jwtTokenManager = jwtTokenManager;
        this.passwordManagementConfiguration = passwordManagementConfiguration;
    }

    public User getUser(final User principal, final UUID userId) {
        if (principal.canRead(User.class, userId)) {
            try (final Handle handle = dbi.open()) {
                final UsersDao usersDao = handle.attach(UsersDao.class);
                final Optional<DBUser> maybeUser = usersDao.getUser(userId);
                return maybeUser.map((user) -> {
                    if (principal.isAdminForReading()) {
                        return UserWithDates.fromDB(user);
                    } else {
                        return User.fromDB(user);
                    }
                }).orElseThrow(() -> Exceptions.webAppException(USER_NOT_FOUND, Response.Status.NOT_FOUND));
            }
        }
        throw Exceptions.webAppException(USER_NOT_FOUND, Response.Status.NOT_FOUND);
    }

    public User createUser(final User user, final String password) {
        final DBUser dbUser = new DBUser(
                UUID.randomUUID(),
                null,
                null,
                null,
                null,
                null,
                null,
                user.getEmail(),
                user.getDisplayName(),
                PasswordDigest.generateFromPassword(passwordManagementConfiguration.getBcryptCost(), password),
                Sets.newHashSet(Roles.PLAYER)
        );

        try (final Handle handle = dbi.open()) {
            final UsersDao usersDao = handle.attach(UsersDao.class);

            if (usersDao.findUserByEmail(dbUser.getEmail()).isPresent()) {
                throw Exceptions.webAppException("Email already in use", Response.Status.CONFLICT);
            }
            if (usersDao.findUserByDisplayName(dbUser.getDisplayName()).isPresent()) {
                throw Exceptions.webAppException("Display name already in use", Response.Status.CONFLICT);
            }

            usersDao.createUser(dbUser);
        }

        return User.fromDB(dbUser);
    }

    public String refreshJWT(final User user) {
        return generateToken(user);
    }

    public Optional<LoginResult> loginUser(final String email, final String password) {
        try (final Handle handle = dbi.open()) {
            final UsersDao usersDao = handle.attach(UsersDao.class);

            final Optional<DBUser> maybeUser = usersDao.findUserByEmail(email);

            if (maybeUser.isPresent()) {
                final DBUser user = maybeUser.get();
                if (user.getPasswordDigest().checkPassword(password)) {
                    usersDao.updateLastLoginDate(user.getId());

                    final String token = generateToken(User.fromDB(user));

                    return Optional.of(new LoginResult(user.getId(), token));
                }
            }
            return Optional.empty();
        }
    }

    public String updateUser(final User principal, final UUID userId, final UpdateUserRequest request) {
        if (!principal.canModify(User.class, userId)) {
            throw Exceptions.webAppException(YOU_DO_NOT_HAVE_PERMISSION_TO_UPDATE_THIS_USER, Response.Status.FORBIDDEN);
        }

        final User updatedUser = request.getUser();

        if (updatedUser.getId() != null && !principal.getId().equals(updatedUser.getId())) {
            throw Exceptions.webAppException("User id cannot be changed", Response.Status.FORBIDDEN);
        }

        try (final Handle handle = dbi.open()) {
            final UsersDao usersDao = handle.attach(UsersDao.class);
            final Optional<DBUser> oldUser = usersDao.getUser(userId);
            if (!oldUser.isPresent()) {
                throw Exceptions.webAppException(String.format("No user found with id %s", userId), Response.Status.NOT_FOUND);
            }
            updateEmail(userId, request.getPassword(), updatedUser, usersDao, oldUser.get());
            updateDisplayName(userId, updatedUser, usersDao, oldUser.get());
        }

        final User newUser = getUser(principal, userId);

        return generateToken(newUser);
    }

    private void updateDisplayName(final UUID userId, final User updatedUser, final UsersDao usersDao, final DBUser oldUser) {
        final String newDisplayName = updatedUser.getDisplayName();
        if (!oldUser.getDisplayName().equals(newDisplayName)) {
            if (usersDao.findUserByDisplayName(newDisplayName).isPresent()) {
                throw Exceptions.webAppException("That display name is already in use", Response.Status.CONFLICT);
            }
            final int rowCount = usersDao.updateDisplayName(userId.toString(), newDisplayName);
            if (rowCount != 1) {
                throw new RuntimeException(String.format(EXPECTED_ONE_ROW_UPDATED_FORMAT, rowCount));
            }
        }
    }

    private void updateEmail(final UUID userId, final String password, final User updatedUser, final UsersDao usersDao, final DBUser oldUser) {
        final String newEmail = updatedUser.getEmail();
        if (!oldUser.getEmail().equals(newEmail)) {

            // validate password before allowing email to be changed
            validatePassword(userId, password, usersDao);

            if (usersDao.findUserByEmail(newEmail).isPresent()) {
                throw Exceptions.webAppException("That email address is already in use", Response.Status.CONFLICT);
            }
            final int rowCount = usersDao.updateEmail(userId, newEmail);
            if (rowCount != 1) {
                throw new RuntimeException(String.format(EXPECTED_ONE_ROW_UPDATED_FORMAT, rowCount));
            }
        }
    }

    private void validatePassword(final UUID userId, final String password, final UsersDao usersDao) {
        final Optional<DBUser> maybeUser = usersDao.getUser(userId);

        if (!maybeUser.isPresent()) {
            throw Exceptions.webAppException(USER_NOT_FOUND, Response.Status.NOT_FOUND);
        }
        final DBUser user = maybeUser.get();
        if (!user.getPasswordDigest().checkPassword(password)) {
            throw Exceptions.webAppException(INCORRECT_PASSWORD, Response.Status.UNAUTHORIZED);
        }
    }

    public void changePassword(final User principal, final UUID userId, final String oldPassword, final String newPassword) {
        if (!principal.canModify(User.class, userId)) {
            throw Exceptions.webAppException(YOU_DO_NOT_HAVE_PERMISSION_TO_UPDATE_THIS_USER, Response.Status.FORBIDDEN);
        }
        try (final Handle handle = dbi.open()) {
            final UsersDao usersDao = handle.attach(UsersDao.class);

            final Optional<DBUser> maybeUser = usersDao.getUser(userId);
            if (!maybeUser.isPresent()) {
                throw Exceptions.webAppException(USER_NOT_FOUND, Response.Status.NOT_FOUND);
            }

            final DBUser user = maybeUser.get();
            if (!user.getPasswordDigest().checkPassword(oldPassword)) {
                throw Exceptions.webAppException(INCORRECT_PASSWORD, Response.Status.UNAUTHORIZED);
            }

            final PasswordDigest passwordDigest =
                    PasswordDigest.generateFromPassword(passwordManagementConfiguration.getBcryptCost(), newPassword);
            usersDao.changePassword(userId, passwordDigest);
        }
    }

    private String generateToken(final User user) {
        final Map<String, Object> claim = jwtTokenManager.toJWTClaim(user);
        return jwtTokenManager.getJwtSigner().sign(claim, jwtTokenManager.getJwtOptions());
    }

}
