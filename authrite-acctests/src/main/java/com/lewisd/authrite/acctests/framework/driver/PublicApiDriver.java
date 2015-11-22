package com.lewisd.authrite.acctests.framework.driver;

import com.auth0.jwt.JWTVerifyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.lewisd.authrite.auth.JWTConfiguration;
import com.lewisd.authrite.auth.JwtTokenManager;
import io.github.unacceptable.alias.AbsentWrappingGenerator;
import io.github.unacceptable.alias.AliasStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class PublicApiDriver {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicApiDriver.class);

    private String loggedInEmail;
    private String jwtToken;
    private Cookie jtwCookie;

    public Response createUser(final String email, final String displayName, final String password, final int expectedStatusCode,
                               final String id) {
        final Map<String, Object> user = new HashMap<>();
        if (email != null) {
            user.put("email", email);
        }
        if (displayName != null) {
            user.put("displayName", displayName);
        }

        if (id != null) {
            user.put("id", id);
        }

        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user", user);
        requestBody.put("password", password);

        final String requestBodyJson = createJsonRequestBody(requestBody);

        LOGGER.info("Sending " + requestBodyJson);
        return buildResponse()
                .body(requestBodyJson)
                .when()
                .post("/users/")
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public Response login(final String email, final String password, final int expectedStatusCode) {
        final Map<String, Object> requestBody = new HashMap<>();
        if (email != null) {
            requestBody.put("email", email);
        }
        if (password != null) {
            requestBody.put("password", password);
        }

        final String requestBodyJson = createJsonRequestBody(requestBody);

        LOGGER.info("Sending " + requestBodyJson);
        final Response response = buildResponse()
                .body(requestBodyJson)
                .when()
                .post("/users/login")
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(expectedStatusCode)
                .extract()
                .response();

        if (response.statusCode() < 400) {
            loggedInEmail = email;
            saveCookie(response);
        }

        return response;
    }

    private void saveCookie(final Response response) {
        jwtToken = response.getCookie("jwtToken");
        jtwCookie = response.getDetailedCookie("jwtToken");
    }

    public Response getUser(final int expectedStatusCode) {
        assertNotNull("Must be logged in first", loggedInEmail);
        assertNotNull("Must be logged in first", jwtToken);

        return buildResponse()
                .when()
                .get("/users/")
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public Response getUser(final String userId, final int expectedStatusCode) {
        if (loggedInEmail == null || jwtToken == null) {
            LOGGER.warn("Must be logged in first");
        }

        return buildResponse()
                .when()
                .get("/users/" + userId)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    public Response updateUser(final UUID userId, final String email, final String displayName, final String password, final int expectedStatusCode) {
        final Map<String, Object> user = new HashMap<>();
        if (email != null) {
            user.put("email", email);
        }
        if (displayName != null) {
            user.put("displayName", displayName);
        }

        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user", user);
        if (password != null) {
            requestBody.put("password", password);
        }

        final String requestBodyJson = createJsonRequestBody(requestBody);

        LOGGER.info("Sending " + requestBodyJson);
        final Response response = buildResponse()
                .body(requestBodyJson)
                .when()
                .post("/users/" + userId)
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(expectedStatusCode)
                .extract()
                .response();

        if (expectedStatusCode < 300) {
            saveCookie(response);
        }

        return response;
    }

    private RequestSpecification buildResponse() {
        RequestSpecification requestSpecification = RestAssured.given()
                .contentType(ContentType.JSON);
        if (jwtToken != null) {
            requestSpecification = requestSpecification.cookie("jwtToken", jwtToken);
        }
        return requestSpecification;
    }

    private String createJsonRequestBody(final Map<String, Object> requestBody) {
        final String requestBodyJson;
        try {
            requestBodyJson = new ObjectMapper().writeValueAsString(requestBody);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Unable to generate JSON request body", e);
        }
        return requestBodyJson;
    }

    public Cookie getJtwCookie() {
        return jtwCookie;
    }

    public void regenerateToken(final String secret, final String userId, final String email, final String displayName, final String expiry) {

        final JWTConfiguration verifyingConfiguration = new JWTConfiguration();
        final JwtTokenManager tokenManager = verifyingConfiguration.buildTokenManager();

        final Map<String, Object> map = decodeJwtToken(jwtToken, tokenManager);

        if (email != null) {
            addOrRemoveParam(map, "email", email);
        }
        if (displayName != null) {
            addOrRemoveParam(map, "displayName", displayName);
        }
        if (userId != null) {
            addOrRemoveParam(map, "id", userId);
        }
        if (expiry != null) {
            addOrRemoveParam(map, "exp", expiry);
            if (map.containsKey("exp")) {
                // If it was in the map, convert it to a Long
                map.put("exp", Long.parseLong((String) map.get("exp")));
            }
        }

        final JWTConfiguration signingConfiguration = new JWTConfiguration();
        signingConfiguration.setJwtSecret(secret);
        jwtToken = signingConfiguration.buildTokenManager().getJwtSigner().sign(map);
    }

    public Response refreshToken(final int expectedStatusCode) {
        final Response response = buildResponse()
                .when()
                .get("/users/refreshJWT")
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(expectedStatusCode)
                .extract()
                .response();

        if (expectedStatusCode < 300) {
            saveCookie(response);
        }

        return response;

    }

    public Response changePassword(final UUID userId, final String oldPassword, final String newPassword, final int expectedStatusCode) {
        final Map<String, Object> requestBody = new HashMap<>();
        if (oldPassword != null) {
            requestBody.put("oldPassword", oldPassword);
        }
        if (newPassword != null) {
            requestBody.put("newPassword", newPassword);
        }

        final String requestBodyJson = createJsonRequestBody(requestBody);

        LOGGER.info("Sending " + requestBodyJson);
        return buildResponse()
                .body(requestBodyJson)
                .when()
                .post("/users/" + userId + "/changePassword")
                .then()
                .log()
                .body()
                .assertThat()
                .statusCode(expectedStatusCode)
                .extract()
                .response();
    }

    private Map<String, Object> decodeJwtToken(final String token, final JwtTokenManager tokenManager) {
        final Map<String, Object> map;
        try {
            map = tokenManager.getJwtVerifier().verify(token);
        } catch (final NoSuchAlgorithmException | JWTVerifyException | SignatureException | IOException | InvalidKeyException e) {
            throw new RuntimeException("Unable to verify existing token", e);
        }
        return map;
    }

    private void addOrRemoveParam(final Map<String, Object> map, final String fieldName, final String value) {
        if (value.equals(AbsentWrappingGenerator.ABSENT)) {
            map.remove(fieldName);
        } else {
            map.put(fieldName, value);
        }
    }

}
