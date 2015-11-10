package com.lewisd.authrite.acctests.tests;

import com.lewisd.authrite.acctests.framework.DslTestCase;
import org.junit.Before;
import org.junit.Test;

public class UpdateUserAccTest extends DslTestCase {

    @Before
    public void beforeEveryTest() {
        publicApi.createUser("test@lewisd.com", "Tester", "password123");
        publicApi.login("test@lewisd.com", "password123");
    }

    @Test
    public void shouldUpdateEmail() {
        publicApi.updateUser("test@lewisd.com", "email: test2@lewisd.com", "displayName: Tester", "password: password123");
        publicApi.assertUser("test@lewisd.com", "email: test2@lewisd.com");
    }

    @Test
    public void shouldNotUpdateEmailWithoutPassword() {
        publicApi.updateUser("test@lewisd.com", "email: test2@lewisd.com", "displayName: Tester",
                             "expectedStatus: UNAUTHORIZED",
                             "expectedError: Incorrect password");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldUpdateDisplayName() {
        publicApi.updateUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester2");
        publicApi.assertUser("test@lewisd.com", "displayName: Tester2");
    }

    @Test
    public void shouldRefreshJwtTokenWhenUpdated() {
        recordTime("beforeUpdate", "padding: 1 second");
        publicApi.updateUser("test@lewisd.com", "email: test2@lewisd.com", "displayName: Tester", "password: password123");
        recordTime("afterUpdate", "padding: 1 second");

        publicApi.assertJwtToken("jwtTokenExpiry: between 1 hour after beforeUpdate and 1 hour after afterUpdate");
    }

    @Test
    public void shouldRefreshJwtTokenWhenNoChanges() {
        recordTime("beforeUpdate", "padding: 1 second");
        publicApi.updateUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
        recordTime("afterUpdate", "padding: 1 second");

        publicApi.assertJwtToken("jwtTokenExpiry: between 1 hour after beforeUpdate and 1 hour after afterUpdate");
    }

    @Test
    public void shouldNotUpdateWhenNotLoggedIn() {
        publicApi2.updateUser("test@lewisd.com", "email: test2@lewisd.com", "displayName: Tester2",
                              "expectedStatus: UNAUTHORIZED",
                              "expectedError: Credentials are required to access this resource.");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateOtherUser() {
        publicApi2.createUser("playerOne", "Player One", "password");
        publicApi2.login("playerOne", "password");
        publicApi.updateUser("playerOne", "email: anotherAddress@lewisd.com", "displayName: Player One",
                              "expectedStatus: FORBIDDEN",
                              "expectedError: You do not have permission to update this user");

        publicApi2.assertUser("playerOne", "email: playerOne", "displayName: Player One");
    }

    @Test
    public void shouldNotUpdateMissingUser() {
        dslTools.generateFakeUserId("someaddress@lewisd.com");
        publicApi.updateUser("someaddress@lewisd.com", "email: anotherAddress@lewisd.com", "displayName: Player One",
                             "expectedStatus: FORBIDDEN",
                             "expectedError: You do not have permission to update this user");
        // Error message should not give away presence of userId or not.
        // Should be the same error message as in shouldNotUpdateOtherUser.
    }

    @Test
    public void shouldNotUpdateUserWithMissingEmail() {
        publicApi.updateUser("test@lewisd.com", "email: ABSENT", "displayName: Tester",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.email may not be empty");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateUserWithEmptyEmail() {
        publicApi.updateUser("test@lewisd.com", "email: ", "displayName: Tester",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.email may not be empty",
                             "expectedError: user.email not a well-formed email address");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateUserWithInvalidEmail() {
        publicApi.updateUser("test@lewisd.com", "email: <test.test.com>", "displayName: Tester",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.email not a well-formed email address");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateUserWithDuplicateEmail() {
        publicApi2.createUser("other@lewisd.com", "Other", "password");
        publicApi.updateUser("test@lewisd.com", "email: other@lewisd.com", "displayName: Tester", "password: password123",
                             "expectedStatus: CONFLICT",
                             "expectedError: That email address is already in use");
        // TODO: This exposes the application to an enumeration attack,
        // whereby someone could determine which email addresses are
        // registered.

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateUserWithMissingDisplayName() {
        publicApi.updateUser("test@lewisd.com", "email: test@lewisd.com", "displayName: ABSENT",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.displayName may not be empty");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateUserWithEmptyDisplayName() {
        publicApi.updateUser("test@lewisd.com", "email: test@lewisd.com", "displayName: ",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.displayName may not be empty",
                             "expectedError: user.displayName length must be between 3 and 20");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateUserWithTooShortDisplayName() {
        publicApi.updateUser("test@lewisd.com", "email: test@lewisd.com", "displayName: <Ed>",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.displayName length must be between 3 and 20");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateUserWithTooLongDisplayName() {
        publicApi.updateUser("test@lewisd.com", "email: test@lewisd.com", "displayName: <Edward Finkelstein the Fourth>",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.displayName length must be between 3 and 20");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

    @Test
    public void shouldNotUpdateUserWithDuplicateDisplayName() {
        publicApi2.createUser("other@lewisd.com", "Other", "password");
        publicApi.updateUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Other",
                             "expectedStatus: CONFLICT",
                             "expectedError: That display name is already in use");

        publicApi.assertUser("test@lewisd.com", "email: test@lewisd.com", "displayName: Tester");
    }

}
