package com.lewisd.authrite.jdbc;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class StringArgument implements Argument {
    private final String value;

    StringArgument(final String value) {
        this.value = value;
    }

    @Override
    public void apply(final int position, final PreparedStatement statement, final StatementContext ctx) throws SQLException {
        if (value != null) {
            statement.setString(position, value);
        } else {
            statement.setNull(position, Types.VARCHAR);
        }
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }

}
