package com.lewisd.authrite.auth;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

public final class JWTAuthFilter<P extends Principal> extends AuthFilter<Cookie, P> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthFilter.class);

    private String cookieName;

    private JWTAuthFilter() {
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final Map<String, Cookie> cookies = requestContext.getCookies();
        final Cookie cookie = cookies.get(cookieName);
        if (cookie != null) {
            try {
                final Optional<P> principal = authenticator.authenticate(cookie);
                if (principal.isPresent()) {
                    requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return principal.get();
                        }

                        @Override
                        public boolean isUserInRole(final String role) {
                            return authorizer.authorize(principal.get(), role);
                        }

                        @Override
                        public boolean isSecure() {
                            return requestContext.getSecurityContext().isSecure();
                        }

                        @Override
                        public String getAuthenticationScheme() {
                            return SecurityContext.BASIC_AUTH;
                        }
                    });
                    return;
                }
            } catch (final AuthenticationException e) {
                LOGGER.warn("Error authenticating credentials", e);
                throw new InternalServerErrorException();
            }
        }
        throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }

    public static class Builder<P extends Principal> extends
            AuthFilterBuilder<Cookie, P, JWTAuthFilter<P>> {

        private String cookieName;

        public Builder() {
            setRealm("defaultRealm");
            setPrefix("");
            setUnauthorizedHandler(new DefaultUnauthorizedHandler());
        }

        // CHECKSTYLE.OFF: HiddenField
        // For some reason checkstyle isn't recognizing this as a setter.
        public Builder<P> setCookieName(final String cookieName) {
            this.cookieName = cookieName;
            return this;
        }
        // CHECKSTYLE.ON: HiddenField

        @Override
        protected JWTAuthFilter<P> newInstance() {
            final JWTAuthFilter<P> instance = new JWTAuthFilter<>();
            instance.cookieName = cookieName;
            return instance;
        }

        @Override
        public JWTAuthFilter<P> buildAuthFilter() {
            Preconditions.checkArgument(cookieName != null, "CookieName is not set");

            return super.buildAuthFilter();
        }
    }

}
