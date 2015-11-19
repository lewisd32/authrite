package com.lewisd.authrite.acctests.tests;

import com.lewisd.authrite.acctests.framework.DslTestCase;
import org.junit.Before;
import org.junit.Test;

public class ChangePasswordAccTest extends DslTestCase {

    @Before
    public void beforeEachTest() {
        publicApi.createUser("test@test.com", "Tester", "password123");
        publicApi.login("test@test.com", "password123");
        adminApi.login("admin@lewisd.com", "<password>");
    }

    @Test
    public void shouldChangePasswordAndUpdateDatesCorrectly() {
        recordTime("beforePasswordChanged");
        publicApi.changePassword("test@test.com", "oldPassword: password123", "newPassword: password456");
        recordTime("afterPasswordChanged");

        adminApi.assertUser("test@test.com", "createdDate: between testStart and beforePasswordChanged");
        adminApi.assertUser("test@test.com", "modifiedDate: between beforePasswordChanged and afterPasswordChanged");
        adminApi.assertUser("test@test.com", "deletedDate: is never");
        adminApi.assertUser("test@test.com", "lastLoginDate: between testStart and beforePasswordChanged");
        adminApi.assertUser("test@test.com", "lastPasswordChangeDate: between beforePasswordChanged and afterPasswordChanged");
        adminApi.assertUser("test@test.com", "emailValidatedDate: is never");
    }

    @Test
    public void shouldNotChangePasswordWhenNotLoggedIn() {
        publicApi2.changePassword("test@test.com", "oldPassword: password123", "newPassword: password456",
                                  "expectedStatus: UNAUTHORIZED",
                                  "expectedError: Credentials are required to access this resource.");
    }

    @Test
    public void shouldNotChangeOtherUserPassword() {
        publicApi2.createUser("playerOne", "Player One", "password");
        publicApi2.login("playerOne", "password");

        publicApi.changePassword("playerOne", "oldPassword: password", "newPassword: newpassword",
                                 "expectedStatus: FORBIDDEN",
                                 "expectedError: You do not have permission to update this user");
    }

    @Test
    public void shouldNotChangeMissingUserPassword() {
        dslTools.generateFakeUserId("someaddress@lewisd.com");
        publicApi.changePassword("someaddress@lewisd.com", "oldPassword: password123", "newPassword: password456",
                                 "expectedStatus: FORBIDDEN",
                                 "expectedError: You do not have permission to update this user");

        // Exception should not give away presence of userId or not. Same exception as above.
    }

    @Test
    public void shouldNotChangePasswordWithMissingOldPassword() {
        recordTime("beforePasswordChanged");
        publicApi.changePassword("test@test.com", "oldPassword: ABSENT", "newPassword: password456",
                                 "expectedStatus: UNPROCESSABLE_ENTITY",
                                 "expectedError: oldPassword may not be empty");

        adminApi.assertUser("test@test.com", "lastPasswordChangeDate: before beforePasswordChanged");
    }

    @Test
    public void shouldNotChangePasswordWithMissingNewPassword() {
        recordTime("beforePasswordChanged");
        publicApi.changePassword("test@test.com", "oldPassword: password123", "newPassword: ABSENT",
                                 "expectedStatus: UNPROCESSABLE_ENTITY",
                                 "expectedError: newPassword may not be empty");

        adminApi.assertUser("test@test.com", "lastPasswordChangeDate: before beforePasswordChanged");
    }

    @Test
    public void shouldNotChangePasswordWithIncorrectOldPassword() {
        recordTime("beforePasswordChanged");
        publicApi.changePassword("test@test.com", "oldPassword: password", "newPassword: password456",
                                 "expectedStatus: UNAUTHORIZED",
                                 "expectedError: Incorrect password");

        adminApi.assertUser("test@test.com", "lastPasswordChangeDate: before beforePasswordChanged");
    }

    @Test
    public void shouldNotChangePasswordWithTooShortNewPassword() {
        recordTime("beforePasswordChanged");
        publicApi.changePassword("test@test.com", "oldPassword: password123", "newPassword: <short>",
                                 "expectedStatus: UNPROCESSABLE_ENTITY",
                                 "expectedError: newPassword length must be between 8 and 100");

        adminApi.assertUser("test@test.com", "lastPasswordChangeDate: before beforePasswordChanged");
    }

}
