package com.lewisd.authrite.acctests.framework;

import com.auth0.jwt.JWTVerifyException;
import com.google.common.collect.Sets;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Response;
import com.lewisd.authrite.acctests.framework.context.TestContext;
import com.lewisd.authrite.acctests.framework.driver.PublicApiDriver;
import com.lewisd.authrite.acctests.framework.driver.SystemDriver;
import com.lewisd.authrite.acctests.framework.time.DurationAssertion;
import com.lewisd.authrite.acctests.framework.time.DurationAssertionFactory;
import com.lewisd.authrite.acctests.framework.time.TimeAssertion;
import com.lewisd.authrite.acctests.framework.time.TimeAssertionFactory;
import com.lewisd.authrite.acctests.framework.time.TimeParser;
import com.lewisd.authrite.acctests.model.User;
import com.lewisd.authrite.auth.JWTConfiguration;
import com.lewisd.authrite.errors.APIError;
import com.lmax.simpledsl.DslParams;
import com.lmax.simpledsl.OptionalParam;
import com.lmax.simpledsl.RequiredParam;
import io.github.unacceptable.alias.AliasStore;
import io.github.unacceptable.dsl.SimpleDsl;
import io.github.unacceptable.lazy.Lazily;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.*;

//CHECKSTYLE.OFF: ClassDataAbstractionCoupling
//CHECKSTYLE.OFF: ClassFanOutComplexity
public class PublicApi extends SimpleDsl<PublicApiDriver, TestContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicApi.class);

    private static Map<String, Integer> statusCodes;

    public PublicApi(final SystemDriver systemDriver, final TestContext testContext) {
        super(systemDriver::publicApiDriver, testContext);
    }

    public void createUser(final String... args) {
        final DslParams params = new DslParams(
                args,
                PublicApi.emailParam(),
                PublicApi.displayNameParam(),
                PublicApi.passwordParam(),
                PublicApi.expectedStatusParam(),
                PublicApi.expectedError(),
                PublicApi.idParam()
        );
        final String email = testContext.emailAddresses.resolve(params.value("email"));
        final String displayName = testContext.displayNames.resolve(params.value("displayName"));
        final String password = testContext.passwords.resolve(params.value("password"));
        final int expectedStatus = getStatusCode(params.value("expectedStatus"));
        final String id = params.value("id");

        final Response response = driver().createUser(email, displayName, password, expectedStatus, id);

        if (expectedStatus == 200) {
            final User user = response.as(User.class);
            assertNull("Shouldn't have jwt cookie", response.getCookie("jwtToken"));
            assertEquals(email, user.getEmail());
            assertEquals(displayName, user.getDisplayName());
            assertNotNull(user.getId());
            if (id != null) {
                Assert.assertNotEquals(id, user.getId());
            }
            testContext.addUser(user);
        } else {
            assertExpectedErrors(params, response);
        }
    }

    public void login(final String... args) {
        final DslParams params = new DslParams(
                args,
                PublicApi.emailParam(),
                PublicApi.passwordParam(),
                PublicApi.expectedStatusParam().setDefault("SEE_OTHER"),
                PublicApi.expectedError()
        );

        final String email = testContext.emailAddresses.resolve(params.value("email"));
        final String password = testContext.passwords.resolve(params.value("password"));
        final int expectedStatus = getStatusCode(params.value("expectedStatus"));

        final Response response = driver().login(email, password, expectedStatus);

        if (expectedStatus == 303) {
            // Find the id of the user from when it was created
            final UUID userId = testContext.getUserId(email);
            final String location = response.getHeader("Location");

            // Assert that the userId in the redirect is correct
            Assert.assertThat(location, endsWith("/" + userId));
        } else {
            assertExpectedErrors(params, response);
        }
    }

    public void assertUser(final String... args) {
        final DslParams params = new DslParams(
                args,
                new RequiredParam("user"),
                new OptionalParam("email"),
                new OptionalParam("displayName"),
                PublicApi.expectedStatusParam(),
                PublicApi.expectedError(),
                new OptionalParam("createdDate"),
                new OptionalParam("modifiedDate"),
                new OptionalParam("deletedDate"),
                new OptionalParam("lastLoginDate"),
                new OptionalParam("lastPasswordChangeDate"),
                new OptionalParam("emailValidatedDate")
        );

        final String userEmail = testContext.emailAddresses.resolve(params.value("user"));
        final UUID userId = testContext.getUserId(userEmail);

        final String email = testContext.emailAddresses.resolve(params.value("email"));
        final String displayName = testContext.displayNames.resolve(params.value("displayName"));
        final int expectedStatus = getStatusCode(params.value("expectedStatus"));

        final Response response = driver().getUser(userId.toString(), expectedStatus);

        if (expectedStatus == 200) {
            final User user = response.as(User.class);
            if (email != null) {
                assertEquals(email, user.getEmail());
            }
            if (displayName != null) {
                assertEquals(displayName, user.getDisplayName());
            }
            assertUserDates(params, user);
        } else {
            assertExpectedErrors(params, response);
        }
    }

    private void assertUserDates(final DslParams params, final User user) {
        if (params.hasValue("createdDate")) {
            assertTime("createdDate", params.value("createdDate"), user.getCreatedDate());
        }
        if (params.hasValue("modifiedDate")) {
            assertTime("modifiedDate", params.value("modifiedDate"), user.getModifiedDate());
        }
        if (params.hasValue("deletedDate")) {
            assertTime("deletedDate", params.value("deletedDate"), user.getDeletedDate());
        }
        if (params.hasValue("lastLoginDate")) {
            assertTime("lastLoginDate", params.value("lastLoginDate"), user.getLastLoginDate());
        }
        if (params.hasValue("lastPasswordChangeDate")) {
            assertTime("lastPasswordChangeDate", params.value("lastPasswordChangeDate"), user.getLastPasswordChangeDate());
        }
        if (params.hasValue("emailValidatedDate")) {
            assertTime("emailValidatedDate", params.value("emailValidatedDate"), user.getEmailValidatedDate());
        }
    }

    public void assertJwtToken(final String... args) {
        final DslParams params = new DslParams(
                args,
                new OptionalParam("cookieMaxAge"),
                new OptionalParam("cookieExpiry"),
                new OptionalParam("jwtTokenExpiry")
        );

        final Cookie cookie = driver().getJtwCookie();

        if (params.hasValue("cookieMaxAge")) {
            assertDuration(params.value("cookieMaxAge"), Duration.ofSeconds(cookie.getMaxAge()));
        }
        if (params.hasValue("cookieExpiry")) {
            assertTime("cookieExpiry", params.value("cookieExpiry"), cookie.getExpiryDate());
        }

        if (params.hasValue("jwtTokenExpiry")) {
            final String jwtToken = cookie.getValue();
            final Map<String, Object> map;
            try {
                map = new JWTConfiguration().buildTokenManager().getJwtVerifier().verify(jwtToken);
            } catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException | JWTVerifyException | IOException e) {
                throw new RuntimeException("Unable to parse token", e);
            }
            final Date expiryDate = new Date(((int) map.get("exp")) * 1000L);
            assertTime("jwtTokenExpiry", params.value("jwtTokenExpiry"), expiryDate);
        }

    }

    public void updateUser(final String... args) {
        final DslParams params = new DslParams(
                args,
                new RequiredParam("user"),
                new OptionalParam("email"),
                new OptionalParam("displayName"),
                new OptionalParam("password"),
                PublicApi.expectedStatusParam().setDefault("NO_CONTENT"),
                PublicApi.expectedError()
        );

        final String userEmail = testContext.emailAddresses.resolve(params.value("user"));
        final String email = testContext.emailAddresses.resolve(params.value("email"));
        final String displayName = testContext.displayNames.resolve(params.value("displayName"));
        final String password = testContext.passwords.resolve(params.value("password"));

        final int expectedStatus = getStatusCode(params.value("expectedStatus"));

        final UUID userId = testContext.getUserId(userEmail);
        final Response response = driver().updateUser(userId, email, displayName, password, expectedStatus);

        if (expectedStatus == 204) {
            assertNotNull("Should have jwt cookie", response.getCookie("jwtToken"));
        } else {
            assertExpectedErrors(params, response);
        }
    }

    public void regenerateToken(final String... args) {
        final DslParams params = new DslParams(
                args,
                new OptionalParam("email"),
                new OptionalParam("displayName"),
                new OptionalParam("id"),
                new OptionalParam("expiry"),
                new OptionalParam("secret").setDefault(new JWTConfiguration().getJwtSecret())
        );

        final String expiry;
        if (params.hasValue("expiry")) {
            if (AliasStore.ABSENT.equals(params.value("expiry"))) {
                expiry = AliasStore.ABSENT;
            } else {
                final String description = params.value("expiry");
                final Date expiryDate = new TimeParser(testContext.dates, "expiry").parse(description);

                expiry = Long.toString(TimeUnit.MILLISECONDS.toSeconds(expiryDate.getTime()));
            }
        } else {
            expiry = null;
        }


        driver().regenerateToken(
                params.value("secret"),
                AliasStore.ABSENT.equals(params.value("id"))
                        ? AliasStore.ABSENT
                        : params.value("id"),
                AliasStore.ABSENT.equals(params.value("email"))
                        ? AliasStore.ABSENT
                        : testContext.emailAddresses.resolve(params.value("email")),
                AliasStore.ABSENT.equals(params.value("displayName"))
                        ? AliasStore.ABSENT
                        : testContext.displayNames.resolve(params.value("displayName")),
                expiry
        );
    }

    public void refreshToken(final String... args) {
        final DslParams params = new DslParams(
                args,
                PublicApi.expectedStatusParam().setDefault("NO_CONTENT"),
                PublicApi.expectedError()
        );

        final int expectedStatus = getStatusCode(params.value("expectedStatus"));

        final Response response = driver().refreshToken(expectedStatus);

        if (expectedStatus == 204) {
            assertNotNull("Should have jwt cookie", response.getCookie("jwtToken"));
        } else {
            assertExpectedErrors(params, response);
        }
    }

    public void changePassword(final String... args) {
        final DslParams params = new DslParams(
                args,
                PublicApi.emailParam(),
                new RequiredParam("oldPassword"),
                new RequiredParam("newPassword"),
                PublicApi.expectedStatusParam().setDefault("NO_CONTENT"),
                PublicApi.expectedError()
        );
        final String email = testContext.emailAddresses.resolve(params.value("email"));
        final String oldPassword = testContext.passwords.resolve(params.value("oldPassword"));
        final String newPassword = testContext.passwords.resolve(params.value("newPassword"));
        final int expectedStatus = getStatusCode(params.value("expectedStatus"));

        final UUID userId = testContext.getUserId(email);
        final Response response = driver().changePassword(userId, oldPassword, newPassword, expectedStatus);

        if (expectedStatus == 204) {
            assertNull("Shouldn't have jwt cookie", response.getCookie("jwtToken"));
        } else {
            assertExpectedErrors(params, response);
        }
    }

    private void assertTime(final String fieldName, final String timeExpectation, final Date time) {
        final TimeAssertion timeAssertion = new TimeAssertionFactory(testContext.dates, fieldName).parse(timeExpectation);
        timeAssertion.assertMatches(time);
    }

    private void assertDuration(final String timeExpectation, final Duration duration) {
        final DurationAssertion durationAssertion = new DurationAssertionFactory().parse(timeExpectation);
        durationAssertion.assertMatches(duration);
    }

    public void ensureUserIdDoesNotMatch(final String... args) {
        final DslParams params = new DslParams(
                args,
                PublicApi.idParam()
        );

        final String id = params.value("id");

        final Response response = driver().getUser(200);

        final User user = response.as(User.class);
        Assert.assertNotEquals(id, user.getId().toString());
    }


    private void assertExpectedErrors(final DslParams params, final Response response) {
        final String[] expectedErrors = params.values("expectedError");
        if (expectedErrors == null || expectedErrors.length == 0) {
            throw new IllegalArgumentException("expectedError is required with non-OK status");
        }
        final APIError error = response.as(APIError.class);

        final Set<String> expectedErrorsSet = Sets.newHashSet(expectedErrors);
        final Set<String> actualErrorsSet = Sets.newHashSet(error.getErrors());
        assertEquals(expectedErrorsSet, actualErrorsSet);
    }

    private static RequiredParam emailParam() {
        return new RequiredParam("email");
    }

    private static RequiredParam displayNameParam() {
        return new RequiredParam("displayName");
    }

    private static RequiredParam passwordParam() {
        return new RequiredParam("password");
    }

    private static OptionalParam expectedStatusParam() {
        return new OptionalParam("expectedStatus")
                .setAllowedValues(getStatusCodes().keySet().toArray(new String[0]))
                .setDefault("OK");
    }

    private static OptionalParam expectedError() {
        return new OptionalParam("expectedError")
                .setAllowMultipleValues();
    }

    private static OptionalParam idParam() {
        return new OptionalParam("id");
    }

    private static Map<String, Integer> getStatusCodes() {
        //CHECKSTYLE.OFF: InnerAssignment
        return statusCodes = Lazily.create(statusCodes, PublicApi::buildStatusCodes);
        //CHECKSTYLE.ON: InnerAssignment
    }

    private static Map<String, Integer> buildStatusCodes() {
        final HashMap<String, Integer> map = new HashMap<>();

        for (final Field field : HttpStatus.class.getFields()) {
            if (field.getName().startsWith("SC_")) {
                final String name = field.getName().substring(3);
                try {
                    final int value = (int) field.get(HttpStatus.class);
                    map.put(name, value);
                } catch (final IllegalAccessException e) {
                    LOGGER.warn("Couldn't read field " + field, e);
                }
            }
        }
        return map;
    }

    private int getStatusCode(final String statusCodeName) {
        return getStatusCodes().get(statusCodeName);
    }

}
