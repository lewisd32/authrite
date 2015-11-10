package com.lewisd.authrite.jdbc.dao;

import com.lewisd.authrite.auth.PasswordDigest;
import com.lewisd.authrite.auth.Roles;
import com.lewisd.authrite.jdbc.PasswordDigestArgumentFactory;
import com.lewisd.authrite.jdbc.UUIDArgumentFactory;
import com.lewisd.authrite.jdbc.model.DBUser;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RegisterMapper(DBUserMapper.class)
@RegisterArgumentFactory({UUIDArgumentFactory.class, PasswordDigestArgumentFactory.class})
public abstract class UsersDao {

    public Optional<DBUser> getUser(final UUID id) {
        return mergeUsers(getUserOrNull(id));
    }

    @SqlQuery("select users.*,userRoles.role "
            + "from users inner join userRoles on users.id=userRoles.userId "
            + "where users.id=:id "
            + "and users.deletedDate is null "
            + "and userRoles.deletedDate is null")
    protected abstract List<DBUser> getUserOrNull(@Bind("id") UUID id);

    public Optional<DBUser> findUserByEmail(final String email) {
        return mergeUsers(findUserByEmailOrNull(email));
    }

    @SqlQuery("select users.*,userRoles.role "
            + "from users inner join userRoles on users.id=userRoles.userId "
            + "where email=:email "
            + "and users.deletedDate is null "
            + "and userRoles.deletedDate is null")
    protected abstract List<DBUser> findUserByEmailOrNull(@Bind("email") String email);

    public Optional<DBUser> findUserByDisplayName(final String displayName) {
        return mergeUsers(findUserByDisplayNameOrNull(displayName));
    }

    @SqlQuery("select users.*,userRoles.role "
              + "from users inner join userRoles on users.id=userRoles.userId "
              + "where displayName=:displayName "
              + "and users.deletedDate is null "
              + "and userRoles.deletedDate is null")
    protected abstract List<DBUser> findUserByDisplayNameOrNull(@Bind("displayName") String displayName);

    @SqlUpdate("update users set lastLoginDate=current_timestamp() where id=:id and deletedDate is null")
    public abstract int updateLastLoginDate(@Bind("id") UUID id);

    @Transaction
    public void createUser(final DBUser user) {
        insertUser(user);
        for (final Roles role : user.getRoles()) {
            insertUserRole(UUID.randomUUID(), user, role);
        }
    }

    @SqlUpdate("insert into users (id, createdDate, modifiedDate, lastPasswordChangeDate, email, displayName, passwordDigest) values "
               + "(:id, current_timestamp(), current_timestamp(), current_timestamp(), :email, :displayName, :passwordDigest)")
    protected abstract void insertUser(@BindBean DBUser user);

    @SqlUpdate("insert into userRoles (id, userId, role, createdDate) values "
               + "(:id, :user.id, :role.name, current_timestamp())")
    protected abstract void insertUserRole(@Bind("id") UUID id, @BindBean("user") DBUser user, @BindBean("role") Roles role);

    @SqlUpdate("update users set email=:email, emailValidatedDate=null, modifiedDate=current_timestamp() where "
            + "id=:id and deletedDate is null")
    public abstract int updateEmail(@Bind("id") UUID id, @Bind("email") String email);

    @SqlUpdate("update users set displayName=:displayName, modifiedDate=current_timestamp() where "
            + "id=:id and deletedDate is null")
    public abstract int updateDisplayName(@Bind("id") String id, @Bind("displayName") String displayName);

    @SqlUpdate("update users set passwordDigest=:passwordDigest, lastPasswordChangeDate=current_timestamp(), "
            + "modifiedDate=current_timestamp() where "
            + "id=:id and deletedDate is null")
    public abstract int changePassword(@Bind("id") UUID id, @Bind("passwordDigest") PasswordDigest passwordDigest);

    @SqlUpdate("update users set emailValidatedDate=current_timestamp() where "
            + "id=:id and deletedDate is null")
    public abstract int flagEmailValidated(@BindBean DBUser user);

    @SqlUpdate("delete from users where id=:id and deletedDate is null")
    public abstract int deleteUser(@Bind("id") UUID id);


    private Optional<DBUser> mergeUsers(final List<DBUser> users) {
        // This is an elaborate workaround to get JDBI to do two things:
        // 1. Return more than 1 row (for users with multiple roles)
        // 2. Throw an exception if a query (on email or displayName) matches more than one user Id
        if (users.isEmpty()) {
            return Optional.empty();
        }

        final DBUser user = users.get(0);
        for (final DBUser otherUser : users) {
            user.getRoles().addAll(otherUser.getRoles());
        }

        return Optional.ofNullable(user);
    }

}
