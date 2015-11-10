package com.lewisd.authrite.jdbc;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.util.UUID;

public class UUIDArgumentFactory implements ArgumentFactory<UUID> {
    @Override
    public boolean accepts(final Class<?> expectedType, final Object value, final StatementContext ctx) {
        return expectedType == UUID.class && value instanceof UUID;
    }

    @Override
    public Argument build(final Class<?> expectedType, final UUID value, final StatementContext ctx) {
        return new StringArgument(value.toString());
    }
}
