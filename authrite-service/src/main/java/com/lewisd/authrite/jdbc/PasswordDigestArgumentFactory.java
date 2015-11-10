package com.lewisd.authrite.jdbc;

import com.lewisd.authrite.auth.PasswordDigest;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

public class PasswordDigestArgumentFactory implements ArgumentFactory<PasswordDigest> {

    @Override
    public boolean accepts(final Class<?> expectedType, final Object value, final StatementContext ctx) {
        return expectedType == PasswordDigest.class && value instanceof PasswordDigest;
    }

    @Override
    public Argument build(final Class<?> expectedType, final PasswordDigest value, final StatementContext ctx) {
        return new StringArgument(value.getDigest());
    }
}
