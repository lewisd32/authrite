package com.lewisd.authrite.acctests.framework.context;

import com.google.common.collect.Sets;
import com.lewisd.authrite.acctests.model.User;
import com.lewisd.authrite.auth.Roles;
import io.github.unacceptable.alias.AliasStore;
import io.github.unacceptable.alias.EmailAddressGenerator;
import io.github.unacceptable.alias.PasswordGenerator;
import io.github.unacceptable.alias.UsernameGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Shared state for the current test run. DSL classes can use their test context to convert aliases (used in test cases)
 * into run-unique actual values, while reusing those values within a test run.
 *
 * @see Dsl
 */
//CHECKSTYLE.OFF: VisibilityModifier
public class TestContext {
    public static final String ADMIN_EMAIL = "admin@lewisd.com";
    public static final String ADMIN_DISPLAY_NAME = "Admin";
    public final AliasStore displayNames = new AliasStore(new UsernameGenerator(20)::generate);
    public final AliasStore emailAddresses = new AliasStore(EmailAddressGenerator::defaultGenerate);
    public final AliasStore passwords = new AliasStore(PasswordGenerator::defaultGenerate);
    public final DateStore dates = new DateStore();

    private final Map<String, UUID> userIdsByEmail = new HashMap<>();
    private final Map<UUID, String> emailsByUserId = new HashMap<>();

    public TestContext() {
        emailAddresses.store(ADMIN_EMAIL, ADMIN_EMAIL);
        displayNames.store(ADMIN_DISPLAY_NAME, ADMIN_DISPLAY_NAME);
        addUser(new User(UUID.fromString("46c668be-e2d2-4452-96a9-a8a0452ac922"),
                         ADMIN_EMAIL,
                         ADMIN_DISPLAY_NAME,
                         Sets.newHashSet(Roles.PLAYER, Roles.ADMIN)
        ));
    }

    public void addUser(final User user) {
        userIdsByEmail.put(user.getEmail(), user.getId());
        emailsByUserId.put(user.getId(), user.getEmail());
    }

    public UUID getUserId(final String email) {
        return userIdsByEmail.get(email);
    }

    public String getUserEmail(final UUID id) {
        return emailsByUserId.get(id);
    }

}
