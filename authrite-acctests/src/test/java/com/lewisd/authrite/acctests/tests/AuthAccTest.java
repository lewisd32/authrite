package com.lewisd.authrite.acctests.tests;

import com.lewisd.authrite.acctests.framework.DslTestCase;
import org.junit.Before;
import org.junit.Test;

public class AuthAccTest extends DslTestCase {

    @Before
    public void beforeEachTest() {
        publicApi.createUser("test@test.com", "Tester", "password123");
        publicApi.login("test@test.com", "password123");
    }

    @Test
    public void shouldGetUserWhenTokenContainsWrongEmail() {
        // This isn't validated (this is actually ok).
        // This could happen if the user changed their email address, but the JwtToken cookie
        // didn't get updated for some reason.

        publicApi.regenerateToken("email: oldaddress@test.com");
        publicApi.assertUser("test@test.com", "email: test@test.com", "displayName: Tester");
    }

    @Test
    public void shouldGetUserWhenTokenContainsWrongDisplayName() {
        // This isn't validated (this is actually ok).
        // This could happen if the user changed their display name, but the JwtToken cookie
        // didn't get updated for some reason.

        publicApi.regenerateToken("displayName: Old Name");
        publicApi.assertUser("test@test.com", "email: test@test.com", "displayName: Tester");
    }

    @Test
    public void shouldNotGetUserWithExpiredToken() {
        publicApi.regenerateToken("expiry: 2 seconds before now");
        publicApi.assertUser("test@test.com",
                             "expectedStatus: UNAUTHORIZED",
                             "expectedError: Credentials are required to access this resource.");
    }

    @Test
    public void shouldNotGetUserWithIncorrectlySigned() {
        publicApi.regenerateToken("secret: too many secrets");
        publicApi.assertUser("test@test.com",
                             "expectedStatus: UNAUTHORIZED",
                             "expectedError: Credentials are required to access this resource.");
    }

}
