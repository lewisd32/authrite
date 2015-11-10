package com.lewisd.authrite.acctests.tests;

import com.lewisd.authrite.acctests.framework.DslTestCase;
import org.junit.Ignore;
import org.junit.Test;

public class CreateUserAccTest extends DslTestCase {

    @Test
    public void shouldCreateUserAndSetDatesCorrectly() {
        publicApi.createUser("test@test.com", "Tester", "password123");
        recordTime("afterUserCreated");

        adminApi.login("admin@lewisd.com", "<password>");
        adminApi.assertUser("test@test.com", "createdDate: between testStart and afterUserCreated");
        adminApi.assertUser("test@test.com", "modifiedDate: between testStart and afterUserCreated");
        adminApi.assertUser("test@test.com", "deletedDate: is never");
        adminApi.assertUser("test@test.com", "lastLoginDate: is never");
        adminApi.assertUser("test@test.com", "lastPasswordChangeDate: between testStart and afterUserCreated");
        adminApi.assertUser("test@test.com", "emailValidatedDate: is never");
    }

    @Test
    public void shouldIgnoreIdInRequest() {
        publicApi.createUser("test@test.com", "Tester", "password123",
                             "id: 062ece7c-a1e5-4fd6-a7fc-04894b88bb9b");
        publicApi.login("test@test.com", "password123");
        publicApi.ensureUserIdDoesNotMatch("id: 062ece7c-a1e5-4fd6-a7fc-04894b88bb9b");
    }

    @Ignore("This fails because jersey returns a 400 trying to get 'hiya' into a UUID")
    @Test
    public void shouldIgnoreMalformedIdInRequest() {
        publicApi.createUser("test@test.com", "Tester", "password123",
                             "id: hiya");
        publicApi.login("test@test.com", "password123");
        publicApi.ensureUserIdDoesNotMatch("id: hiya");
    }

    @Test
    public void shouldNotCreateUserWithMissingEmail() {
        publicApi.createUser("ABSENT", "Tester", "password123",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.email may not be empty");
    }

    @Test
    public void shouldNotCreateUserWithEmptyEmail() {
        publicApi.createUser("<>", "Tester", "password123",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.email may not be empty",
                             "expectedError: user.email not a well-formed email address");
    }

    @Test
    public void shouldNotCreateUserWithInvalidEmail() {
        publicApi.createUser("<test.test.com>", "Tester", "password123",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.email not a well-formed email address");
    }

    @Test
    public void shouldNotCreateUserWithDuplicateEmail() {
        publicApi.createUser("test@test.com", "Tester1", "password123");
        publicApi.createUser("test@test.com", "Tester2", "password123",
                             "expectedStatus: CONFLICT",
                             "expectedError: Email already in use");
        // TODO: This exposes the application to an enumeration attack,
        // whereby someone could determine which email addresses are
        // registered.
    }

    @Test
    public void shouldNotCreateUserWithMissingDisplayName() {
        publicApi.createUser("test@test.com", "ABSENT", "password123",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.displayName may not be empty");
    }

    @Test
    public void shouldNotCreateUserWithEmptyDisplayName() {
        publicApi.createUser("test@test.com", "<>", "password123",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.displayName may not be empty",
                             "expectedError: user.displayName length must be between 3 and 20");
    }

    @Test
    public void shouldNotCreateUserWithTooShortDisplayName() {
        publicApi.createUser("test@test.com", "<Ed>", "password123",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.displayName length must be between 3 and 20");
    }

    @Test
    public void shouldNotCreateUserWithTooLongDisplayName() {
        publicApi.createUser("test@test.com", "<Edward Finkelstein the Fourth>", "password123",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: user.displayName length must be between 3 and 20");
    }

    @Test
    public void shouldNotCreateUserWithDuplicateDisplayName() {
        publicApi.createUser("test1@test.com", "Tester", "password123");
        publicApi.createUser("test2@test.com", "Tester", "password123",
                             "expectedStatus: CONFLICT",
                             "expectedError: Display name already in use");
    }

    @Test
    public void shouldNotCreateUserWithMissingPassword() {
        publicApi.createUser("test@test.com", "Tester", "ABSENT",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: password may not be empty");
    }

    @Test
    public void shouldNotCreateUserWithEmptyPassword() {
        publicApi.createUser("test@test.com", "Tester", "<>",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: password may not be empty",
                             "expectedError: password length must be between 8 and 100");
    }

    @Test
    public void shouldNotCreateUserWithTooShortPassword() {
        publicApi.createUser("test@test.com", "Tester", "<secret>",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: password length must be between 8 and 100");
    }

    @Test
    public void shouldNotCreateUserWithTooLongPassword() {
        publicApi.createUser("test@test.com", "Tester",
                             "<supersecret1supersecret2supersecret3supersecret4supersecret5"
                             + "supersecret6supersecret7supersecret8supersecret9>",
                             "expectedStatus: UNPROCESSABLE_ENTITY",
                             "expectedError: password length must be between 8 and 100");
    }

}
