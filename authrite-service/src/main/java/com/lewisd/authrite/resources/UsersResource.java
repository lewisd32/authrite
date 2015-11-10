package com.lewisd.authrite.resources;

import com.lewisd.authrite.auth.JWTConfiguration;
import com.lewisd.authrite.errors.Exceptions;
import com.lewisd.authrite.resources.model.User;
import com.lewisd.authrite.resources.requests.CreateUserRequest;
import com.lewisd.authrite.resources.requests.LoginRequest;
import com.lewisd.authrite.resources.requests.PasswordChangeRequest;
import com.lewisd.authrite.resources.requests.UpdateUserRequest;
import com.lewisd.authrite.services.LoginResult;
import com.lewisd.authrite.services.UsersService;
import io.dropwizard.auth.Auth;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Optional;
import java.util.UUID;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {

    private final UsersService usersService;
    private final JWTConfiguration jwtConfiguration;

    @Inject
    public UsersResource(final UsersService usersService, final JWTConfiguration jwtConfiguration) {
        this.usersService = usersService;
        this.jwtConfiguration = jwtConfiguration;
    }

    @GET
    public User dumpUser(@Auth final User user) {
        return user;
    }

    @GET
    @Path("/{userId}")
    public User getUser(@Auth final User principal,
                        @PathParam("userId") final UUID userId) {
        return usersService.getUser(principal, userId);
    }

    @POST
    public User createUser(@Valid final CreateUserRequest request) {
        return usersService.createUser(request.getUser(), request.getPassword());
    }

    @Path("/refreshJWT")
    @GET
    public Response refreshJWT(@Auth final User principal) {
        final String token = usersService.refreshJWT(principal);

        return buildResponseFromToken(token);
    }

    private Response buildResponseFromToken(final String token) {
        return Response.noContent()
                .cookie(new NewCookie(jwtConfiguration.getCookieName(), token))
                .build();
    }

    @Path("/login")
    @POST
    public Response loginUser(@Valid final LoginRequest loginRequest) {
        final String email = loginRequest.getEmail();
        final String password = loginRequest.getPassword();
        final Optional<LoginResult> maybeResult = usersService.loginUser(email, password);

        if (maybeResult.isPresent()) {
            final LoginResult result = maybeResult.get();
            return Response.seeOther(
                    UriBuilder.fromResource(UsersResource.class)
                            .path(UsersResource.class, "getUser")
                            .build(result.getUserId()))
                    .cookie(new NewCookie(jwtConfiguration.getCookieName(), result.getToken()))
                    .build();


        } else {
            throw Exceptions.webAppException(UsersService.USER_NOT_FOUND, Response.Status.NOT_FOUND);
        }
    }

    @Path("/{userId}")
    @POST
    public Response updateUser(@Auth final User principal,
                               @PathParam("userId") final UUID userId,
                               @Valid final UpdateUserRequest request) {
        final String token = usersService.updateUser(principal, userId, request);

        return buildResponseFromToken(token);
    }

    @POST
    @Path("/{userId}/changePassword")
    public void changePassword(@Auth final User principal,
                               @PathParam("userId") final UUID userId,
                               @Valid final PasswordChangeRequest passwordChangeRequest) {
        usersService.changePassword(principal, userId,
                                    passwordChangeRequest.getOldPassword(), passwordChangeRequest.getNewPassword());
    }

}