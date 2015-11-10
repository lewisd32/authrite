package com.lewisd.authrite.jdbc.dao;

import com.google.common.collect.ImmutableList;
import com.lewisd.authrite.jdbc.model.DBPlayer;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.UUID;

@RegisterMapper(DBPlayerMapper.class)
public interface PlayerDao {

    @SqlQuery("select * from players where gameId=:gameId")
    ImmutableList<DBPlayer> getPlayersForGame(@Bind("gameId") UUID gameId);

    @SqlQuery("select * from players where userId=:userId")
    ImmutableList<DBPlayer> getPlayersForUser(@Bind("userId") UUID userId);

}
