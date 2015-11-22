package com.lewisd.authrite.acctests.framework.driver;

import com.lewisd.authrite.acctests.framework.context.TestContext;
import com.lewisd.authrite.acctests.model.User;

import java.util.UUID;

public class DslTools {
    private final TestContext testContext;

    public DslTools(final TestContext testContext) {
        this.testContext = testContext;
    }

    public void generateFakeUserId(final String alias) {
        final String email = testContext.emailAddresses.resolve(alias);
        final UUID userId = UUID.randomUUID();
        final User user = new User();
        user.setId(userId);
        user.setEmail(email);
        testContext.addUser(user);
    }
}
