package com.lewisd.authrite.jdbc.dao;

import com.lewisd.authrite.auth.PasswordDigest;
import com.lewisd.authrite.auth.Roles;
import com.lewisd.authrite.jdbc.model.DBUser;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DBUserMapper implements ResultSetMapper<DBUser> {

    public static final String ID_COLUMN = "id";

    @Override
    public DBUser map(final int index, final ResultSet r, final StatementContext ctx) throws SQLException {
        final String id = r.getString(ID_COLUMN);
        final Set<Roles> roles = new HashSet<>();
        while (true) {
            roles.add(Roles.valueOf(r.getString("role")));
            if (!r.isLast()) {
                r.next();
                if (!id.equals(r.getString(ID_COLUMN))) {
                    throw new IllegalStateException("Multiple user Ids found in query");
                }
            } else {
                break;
            }
        }

        return new DBUser(
                UUID.fromString(r.getString(ID_COLUMN)),
                r.getTimestamp("createdDate"),
                r.getTimestamp("modifiedDate"),
                r.getTimestamp("deletedDate"),
                r.getTimestamp("lastLoginDate"),
                r.getTimestamp("lastPasswordChangeDate"),
                r.getTimestamp("emailValidatedDate"),
                r.getString("email"),
                r.getString("displayName"),
                PasswordDigest.fromDigest(r.getString("passwordDigest")),
                roles);
    }
}
