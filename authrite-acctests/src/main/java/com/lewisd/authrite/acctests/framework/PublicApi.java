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
import com.lewisd.authrite.acctests.framework.valueholder.ValueHolder;
import com.lewisd.authrite.acctests.model.User;
import com.lewisd.authrite.auth.JWTConfiguration;
import com.lewisd.authrite.errors.APIError;
import com.lmax.simpledsl.DslParams;
import com.lmax.simpledsl.OptionalParam;
import com.lmax.simpledsl.RequiredParam;
import io.github.unacceptable.alias.AbsentWrappingGenerator;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
        final ValueHolder<String> email = ValueHolder.fromStore(testContext.emailAddresses);
        final ValueHolder<String> displayName = ValueHolder.fromStore(testContext.displayNames);
        final ValueHolder<String> password = ValueHolder.fromStore(testContext.passwords);
        final ValueHolder<Integer> expectedStatus = ValueHolder.withFunction(this::getStatusCode);
        final ValueHolder<List<String>> expectedErrors = ValueHolder.multipleValues();
        final ValueHolder<String> id = ValueHolder.singleValue();

        new DslParams(
                args,
                PublicApi.emailParam(email),
                PublicApi.displayNameParam(displayName),
                PublicApi.passwordParam(password),
                PublicApi.expectedStatusParam(expectedStatus),
                PublicApi.expectedError(expectedErrors),
                PublicApi.idParam(id)
        );

        final Response response = driver().createUser(
                email.get(),
                displayName.get(),
                password.get(),
                expectedStatus.get(),
                id.get());

        if (expectedStatus.get() == 200) {
            final User user = response.as(User.class);
            assertNull("Shouldn't have jwt cookie", response.getCookie("jwtToken"));
            assertEquals(email.get(), user.getEmail());
            assertEquals(displayName.get(), user.getDisplayName());
            assertNotNull(user.getId());
            if (id.hasValue()) {
                Assert.assertNotEquals(id.get(), user.getId());
            }
            testContext.addUser(user);
        } else {
            assertExpectedErrors(expectedErrors, response);
        }
    }

    public void login(final String... args) {
        final ValueHolder<String> email = ValueHolder.fromStore(testContext.emailAddresses);
        final ValueHolder<String> password = ValueHolder.fromStore(testContext.passwords);
        final ValueHolder<Integer> expectedStatus = ValueHolder.withFunction(this::getStatusCode);
        final ValueHolder<List<String>> expectedErrors = ValueHolder.multipleValues();


        new DslParams(
                args,
                PublicApi.emailParam(email),
                PublicApi.passwordParam(password),
                PublicApi.expectedStatusParam(expectedStatus).setDefault("SEE_OTHER"),
                PublicApi.expectedError(expectedErrors)
        );

        final Response response = driver().login(email.get(), password.get(), expectedStatus.get());

        if (expectedStatus.get() == 303) {
            // Find the id of the user from when it was created
            final UUID userId = testContext.getUserId(email.get());
            final String location = response.getHeader("Location");

            // Assert that the userId in the redirect is correct
            Assert.assertThat(location, endsWith("/" + userId));
        } else {
            assertExpectedErrors(expectedErrors, response);
        }
    }

    public void assertUser(final String... args) {
        final ValueHolder<String> userEmail = ValueHolder.fromStore(testContext.emailAddresses);
        final ValueHolder<String> email = ValueHolder.fromStore(testContext.emailAddresses);
        final ValueHolder<String> displayName = ValueHolder.fromStore(testContext.displayNames);
        final ValueHolder<Integer> expectedStatus = ValueHolder.withFunction(this::getStatusCode);
        final ValueHolder<List<String>> expectedErrors = ValueHolder.multipleValues();
        final ValueHolder<String> createdDate = ValueHolder.singleValue();
        final ValueHolder<String> modifiedDate = ValueHolder.singleValue();
        final ValueHolder<String> deletedDate = ValueHolder.singleValue();
        final ValueHolder<String> lastLoginDate = ValueHolder.singleValue();
        final ValueHolder<String> lastPasswordChangeDate = ValueHolder.singleValue();
        final ValueHolder<String> emailValidatedDate = ValueHolder.singleValue();

        new DslParams(
                args,
                new RequiredParam("user").setConsumer(userEmail),
                new OptionalParam("email").setConsumer(email),
                new OptionalParam("displayName").setConsumer(displayName),
                PublicApi.expectedStatusParam(expectedStatus),
                PublicApi.expectedError(expectedErrors),
                new OptionalParam("createdDate").setConsumer(createdDate),
                new OptionalParam("modifiedDate").setConsumer(modifiedDate),
                new OptionalParam("deletedDate").setConsumer(deletedDate),
                new OptionalParam("lastLoginDate").setConsumer(lastLoginDate),
                new OptionalParam("lastPasswordChangeDate").setConsumer(lastPasswordChangeDate),
                new OptionalParam("emailValidatedDate").setConsumer(emailValidatedDate)
        );

        final UUID userId = testContext.getUserId(userEmail.get());

        final Response response = driver().getUser(userId.toString(), expectedStatus.get());

        if (expectedStatus.get() == 200) {
            final User user = response.as(User.class);
            if (email.hasValue()) {
                assertEquals(email.get(), user.getEmail());
            }
            if (displayName.hasValue()) {
                assertEquals(displayName.get(), user.getDisplayName());
            }
            assertUserDates(user,
                            createdDate,
                            modifiedDate,
                            deletedDate,
                            lastLoginDate,
                            lastPasswordChangeDate,
                            emailValidatedDate);
        } else {
            assertExpectedErrors(expectedErrors, response);
        }
    }

    private void assertUserDates(final User user,
                                 final ValueHolder<String> createdDate,
                                 final ValueHolder<String> modifiedDate,
                                 final ValueHolder<String> deletedDate,
                                 final ValueHolder<String> lastLoginDate,
                                 final ValueHolder<String> lastPasswordChangeDate,
                                 final ValueHolder<String> emailValidatedDate) {
        if (createdDate.hasValue()) {
            assertTime("createdDate", createdDate.getRawValue(), user.getCreatedDate());
        }
        if (modifiedDate.hasValue()) {
            assertTime("modifiedDate", modifiedDate.get(), user.getModifiedDate());
        }
        if (deletedDate.hasValue()) {
            assertTime("deletedDate", deletedDate.get(), user.getDeletedDate());
        }
        if (lastLoginDate.hasValue()) {
            assertTime("lastLoginDate", lastLoginDate.get(), user.getLastLoginDate());
        }
        if (lastPasswordChangeDate.hasValue()) {
            assertTime("lastPasswordChangeDate", lastPasswordChangeDate.get(), user.getLastPasswordChangeDate());
        }
        if (emailValidatedDate.hasValue()) {
            assertTime("emailValidatedDate", emailValidatedDate.get(), user.getEmailValidatedDate());
        }
    }

    public void assertJwtToken(final String... args) {
        final ValueHolder<String> cookieMaxAge = ValueHolder.singleValue();
        final ValueHolder<String> cookieExpiry = ValueHolder.singleValue();
        final ValueHolder<String> jwtTokenExpiry = ValueHolder.singleValue();

        new DslParams(
                args,
                new OptionalParam("cookieMaxAge").setConsumer(cookieMaxAge),
                new OptionalParam("cookieExpiry").setConsumer(cookieExpiry),
                new OptionalParam("jwtTokenExpiry").setConsumer(jwtTokenExpiry)
        );

        final Cookie cookie = driver().getJtwCookie();

        if (cookieMaxAge.hasValue()) {
            assertDuration(cookieMaxAge.get(), Duration.ofSeconds(cookie.getMaxAge()));
        }
        if (cookieExpiry.hasValue()) {
            assertTime("cookieExpiry", cookieExpiry.get(), cookie.getExpiryDate());
        }

        if (jwtTokenExpiry.hasValue()) {
            final String jwtToken = cookie.getValue();
            final Map<String, Object> map;
            try {
                map = new JWTConfiguration().buildTokenManager().getJwtVerifier().verify(jwtToken);
            } catch (final NoSuchAlgorithmException | InvalidKeyException | SignatureException | JWTVerifyException | IOException e) {
                throw new RuntimeException("Unable to parse token", e);
            }
            final Date expiryDate = new Date(((int) map.get("exp")) * 1000L);
            assertTime("jwtTokenExpiry", jwtTokenExpiry.get(), expiryDate);
        }

    }

    public void updateUser(final String... args) {
        final ValueHolder<String> userEmail = ValueHolder.fromStore(testContext.emailAddresses);
        final ValueHolder<String> email = ValueHolder.fromStore(testContext.emailAddresses);
        final ValueHolder<String> displayName = ValueHolder.fromStore(testContext.displayNames);
        final ValueHolder<String> password = ValueHolder.fromStore(testContext.passwords);
        final ValueHolder<Integer> expectedStatus = ValueHolder.withFunction(this::getStatusCode);
        final ValueHolder<List<String>> expectedErrors = ValueHolder.multipleValues();

        new DslParams(
                args,
                new RequiredParam("user").setConsumer(userEmail),
                new OptionalParam("email").setConsumer(email),
                new OptionalParam("displayName").setConsumer(displayName),
                new OptionalParam("password").setConsumer(password),
                PublicApi.expectedStatusParam(expectedStatus).setDefault("NO_CONTENT"),
                PublicApi.expectedError(expectedErrors)
        );

        final UUID userId = testContext.getUserId(userEmail.get());
        final Response response = driver().updateUser(
                userId,
                email.get(),
                displayName.get(),
                password.get(),
                expectedStatus.get());

        if (expectedStatus.get() == 204) {
            assertNotNull("Should have jwt cookie", response.getCookie("jwtToken"));
        } else {
            assertExpectedErrors(expectedErrors, response);
        }
    }

    public void regenerateToken(final String... args) {
        final ValueHolder<String> email = ValueHolder.fromStore(testContext.emailAddresses);
        final ValueHolder<String> displayName = ValueHolder.fromStore(testContext.displayNames);
        final ValueHolder<String> id = ValueHolder.singleValue();
        final ValueHolder<String> expiry = ValueHolder.withFunction(alias -> new AbsentWrappingGenerator<>(description -> {
            final Date expiryDate = new TimeParser(testContext.dates, "expiry").parse(description);
            return Long.toString(TimeUnit.MILLISECONDS.toSeconds(expiryDate.getTime()));
        }).generate(alias));
        final ValueHolder<String> secret = ValueHolder.singleValue();

        new DslParams(
                args,
                new OptionalParam("email").setConsumer(email),
                new OptionalParam("displayName").setConsumer(displayName),
                new OptionalParam("id").setConsumer(id),
                new OptionalParam("expiry").setConsumer(expiry),
                new OptionalParam("secret").setDefault(new JWTConfiguration().getJwtSecret()).setConsumer(secret)
        );

        driver().regenerateToken(
                secret.get(),
                id.get(),
                email.get(),
                displayName.get(),
                expiry.get()
        );
    }

    public void refreshToken(final String... args) {
        final ValueHolder<Integer> expectedStatus = ValueHolder.withFunction(this::getStatusCode);
        final ValueHolder<List<String>> expectedErrors = ValueHolder.multipleValues();

        new DslParams(
                args,
                PublicApi.expectedStatusParam(expectedStatus).setDefault("NO_CONTENT"),
                PublicApi.expectedError(expectedErrors)
        );

        final Response response = driver().refreshToken(expectedStatus.get());

        if (expectedStatus.get() == 204) {
            assertNotNull("Should have jwt cookie", response.getCookie("jwtToken"));
        } else {
            assertExpectedErrors(expectedErrors, response);
        }
    }

    public void changePassword(final String... args) {
        final ValueHolder<String> email = ValueHolder.fromStore(testContext.emailAddresses);
        final ValueHolder<String> oldPassword = ValueHolder.fromStore(testContext.passwords);
        final ValueHolder<String> newPassword = ValueHolder.fromStore(testContext.passwords);
        final ValueHolder<Integer> expectedStatus = ValueHolder.withFunction(this::getStatusCode);
        final ValueHolder<List<String>> expectedErrors = ValueHolder.multipleValues();

        new DslParams(
                args,
                PublicApi.emailParam(email),
                new RequiredParam("oldPassword").setConsumer(oldPassword),
                new RequiredParam("newPassword").setConsumer(newPassword),
                PublicApi.expectedStatusParam(expectedStatus).setDefault("NO_CONTENT"),
                PublicApi.expectedError(expectedErrors)
        );

        final UUID userId = testContext.getUserId(email.get());
        final Response response = driver().changePassword(
                userId,
                oldPassword.get(),
                newPassword.get(),
                expectedStatus.get());

        if (expectedStatus.get() == 204) {
            assertNull("Shouldn't have jwt cookie", response.getCookie("jwtToken"));
        } else {
            assertExpectedErrors(expectedErrors, response);
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
        final ValueHolder<String> id = ValueHolder.singleValue();

        new DslParams(
                args,
                PublicApi.idParam(id)
        );

        final Response response = driver().getUser(200);

        final User user = response.as(User.class);
        Assert.assertNotEquals(id.get(), user.getId().toString());
    }


    private void assertExpectedErrors(final ValueHolder<List<String>> expectedErrors, final Response response) {
        if (expectedErrors.get() == null || expectedErrors.get().isEmpty()) {
            throw new IllegalArgumentException("expectedError is required with non-OK status");
        }
        final APIError error = response.as(APIError.class);

        final Set<String> expectedErrorsSet = Sets.newHashSet(expectedErrors.get());
        final Set<String> actualErrorsSet = Sets.newHashSet(error.getErrors());
        assertEquals(expectedErrorsSet, actualErrorsSet);
    }

    private static RequiredParam emailParam(final Consumer<String> consumer) {
        return new RequiredParam("email").setConsumer(consumer).getAsRequiredParam();
    }

    private static RequiredParam displayNameParam(final Consumer<String> consumer) {
        return (RequiredParam) new RequiredParam("displayName").setConsumer(consumer);
    }

    private static RequiredParam passwordParam(final Consumer<String> consumer) {
        return (RequiredParam) new RequiredParam("password").setConsumer(consumer);
    }

    private static OptionalParam expectedStatusParam(final Consumer<String> consumer) {
        return (OptionalParam) new OptionalParam("expectedStatus")
                .setAllowedValues(getStatusCodes().keySet().stream().toArray(String[]::new))
                .setConsumer(consumer)
                .setDefault("OK");
    }

    private static OptionalParam expectedError(final Consumer<String> consumer) {
        return (OptionalParam) new OptionalParam("expectedError")
                .setAllowMultipleValues()
                .setConsumer(consumer);
    }

    private static OptionalParam idParam(final Consumer<String> consumer) {
        return (OptionalParam) new OptionalParam("id").setConsumer(consumer);
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
