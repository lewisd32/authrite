package com.lewisd.authrite.acctests.tests;

import com.lewisd.authrite.acctests.framework.DslTestCase;
import org.junit.Before;
import org.junit.Test;

public class LoginAccTest extends DslTestCase {

    @Before
    public void beforeEachTest() {
        publicApi.createUser("test@test.com", "Tester", "password123");
        adminApi.login("admin@lewisd.com", "<password>");
    }

    @Test
    public void shouldLoginUserAndUpdateLastLoginDate() {
        adminApi.assertUser("test@test.com", "lastLoginDate: is never");

        // We need some padding around the recorded times because the jwtToken expiry
        // is only recorded to the second, not the millisecond.
        recordTime("beforeLogin", "padding: 1 second");
        publicApi.login("test@test.com", "password123");
        recordTime("afterLogin", "padding: 1 second");

        // Session cookie
        publicApi.assertJwtToken("cookieMaxAge: -1 second");
        publicApi.assertJwtToken("cookieExpiry: is never");

        publicApi.assertJwtToken("jwtTokenExpiry: between 1 hour after beforeLogin and 1 hour after afterLogin");

        adminApi.assertUser("test@test.com", "lastLoginDate: between beforeLogin and afterLogin");
        adminApi.assertUser("test@test.com", "createdDate: before beforeLogin");
        adminApi.assertUser("test@test.com", "modifiedDate: before beforeLogin");
        adminApi.assertUser("test@test.com", "deletedDate: is never");
        adminApi.assertUser("test@test.com", "emailValidatedDate: is never");
        adminApi.assertUser("test@test.com", "lastPasswordChangeDate: before beforeLogin");
    }

    @Test
    public void shouldNotLoginUserWithMissingEmail() {
        publicApi.login("ABSENT", "password123",
                "expectedStatus: UNPROCESSABLE_ENTITY", "expectedError: email may not be empty");
    }

    @Test
    public void shouldNotLoginUserWithEmptyEmail() {
        publicApi.login("", "password123",
                "expectedStatus: UNPROCESSABLE_ENTITY", "expectedError: email may not be empty");
    }

    @Test
    public void shouldNotLoginUserWithNonexistentEmail() {
        publicApi.login("nobody@test.com", "password123",
                "expectedStatus: NOT_FOUND", "expectedError: User not found");
    }

    @Test
    public void shouldNotLoginUserWithMissingPassword() {
        publicApi.login("test@test.com", "ABSENT",
                "expectedStatus: UNPROCESSABLE_ENTITY", "expectedError: password may not be empty");
    }

    @Test
    public void shouldNotLoginUserWithEmptyPassword() {
        publicApi.login("test@test.com", "",
                "expectedStatus: UNPROCESSABLE_ENTITY", "expectedError: password may not be empty");
    }

    @Test
    public void shouldNotLoginUserWithIncorrectPassword() {
        publicApi.login("test@test.com", "wrongpassword123",
                "expectedStatus: NOT_FOUND", "expectedError: User not found");
    }

    @Test
    public void shouldNotLoginUserWithOtherUserPassword() {
        publicApi.createUser("other@test.com", "Other", "otherpassword");
        publicApi.login("test@test.com", "otherpassword",
                "expectedStatus: NOT_FOUND", "expectedError: User not found");
    }

}
