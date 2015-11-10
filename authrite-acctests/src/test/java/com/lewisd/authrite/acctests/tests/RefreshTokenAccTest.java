package com.lewisd.authrite.acctests.tests;

import com.lewisd.authrite.acctests.framework.DslTestCase;
import org.junit.Before;
import org.junit.Test;

public class RefreshTokenAccTest extends DslTestCase {

    @Before
    public void beforeEachTest() {
        publicApi.createUser("test@test.com", "Tester", "password123");
        publicApi.login("test@test.com", "password123");
    }

    @Test
    public void shouldRefreshToken() {
        // We need some padding around the recorded times because the jwtToken expiry
        // is only recorded to the second, not the millisecond.
        recordTime("beforeRefresh", "padding: 1 second");
        publicApi.refreshToken();
        recordTime("afterRefresh", "padding: 1 second");

        publicApi.assertJwtToken("jwtTokenExpiry: between 1 hour after beforeRefresh and 1 hour after afterRefresh");

        publicApi.assertUser("test@test.com", "email: test@test.com", "displayName: Tester");
    }

    @Test
    public void shouldNotRefreshTokenWithExpiredToken() {
        publicApi.regenerateToken("expiry: 2 seconds before now");
        publicApi.refreshToken("expectedStatus: UNAUTHORIZED",
                               "expectedError: Credentials are required to access this resource.");
    }

    @Test
    public void shouldNotGetUserWithIncorrectlySigned() {
        publicApi.regenerateToken("secret: too many secrets");
        publicApi.refreshToken("expectedStatus: UNAUTHORIZED",
                               "expectedError: Credentials are required to access this resource.");
    }

}
