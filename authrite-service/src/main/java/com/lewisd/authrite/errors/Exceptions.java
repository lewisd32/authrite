package com.lewisd.authrite.errors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public final class Exceptions {

    private Exceptions() {
        // util
    }

    public static WebApplicationException webAppException(
            final String message,
            final Response.Status status) {
        return webAppException(message, status, null);
    }

    public static WebApplicationException webAppException(
            final String message,
            final Response.Status status,
            final Throwable cause) {
        final APIError error = new APIError(message);
        final Response response = Response.status(status).entity(error).build();
        return new WebApplicationException(message, cause, response);
    }

}
