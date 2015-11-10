package com.lewisd.authrite.acctests.tests;

import com.lewisd.authrite.acctests.framework.DslTestCase;
import org.junit.Before;
import org.junit.Test;

public class GetUserAccTest extends DslTestCase {

    @Before
    public void beforeEachTest() {
        publicApi.createUser("test@test.com", "Tester", "password123");
        publicApi.login("test@test.com", "password123");
    }

    @Test
    public void shouldNotGetOwnUserWhenNotLoggedIn() {
        publicApi2.assertUser("test@test.com",
                              "expectedStatus: UNAUTHORIZED",
                              "expectedError: Credentials are required to access this resource.");
    }

    @Test
    public void shouldGetOwnUser() {
        publicApi.assertUser("test@test.com", "email: test@test.com", "displayName: Tester");
    }

    @Test
    public void shouldNotGetOtherUser() {
        publicApi2.createUser("other@test.com", "Other", "password123");

        publicApi.assertUser("other@test.com",
                             "expectedStatus: NOT_FOUND",
                             "expectedError: User not found");
    }

}
