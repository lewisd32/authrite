package com.lewisd.authrite.jdbc.dao;

import com.lewisd.authrite.jdbc.model.DBPlayer;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DBPlayerMapper implements ResultSetMapper<DBPlayer> {
    @Override
    public DBPlayer map(final int index, final ResultSet r, final StatementContext ctx) throws SQLException {
        return new DBPlayer(
                UUID.fromString(r.getString("gameId")),
                UUID.fromString(r.getString("userId")),
                UUID.fromString(r.getString("raceId")),
                r.getInt("slot"),
                r.getBoolean("ready"));
    }
}
