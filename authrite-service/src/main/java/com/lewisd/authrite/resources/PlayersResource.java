package com.lewisd.authrite.resources;

import com.lewisd.authrite.jdbc.dao.PlayerDao;
import com.lewisd.authrite.resources.model.Player;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class PlayersResource {

    private final DBI dbi;

    @Inject
    public PlayersResource(final DBI dbi) {
        this.dbi = dbi;
    }

    @Path("/games/{gameId}/players")
    @GET
    public List<Player> listPlayersInGame(@PathParam("gameId") final UUID gameId) {
        try (final Handle handle = dbi.open()) {
            final PlayerDao playerDao = handle.attach(PlayerDao.class);

            return playerDao.getPlayersForGame(gameId).stream().
                    map(Player::fromDB).collect(toList());
        }
    }

    @Path("/users/{userId}/players")
    @GET
    public List<Player> listPlayersForUser(@PathParam("userId") final UUID userId) {
        try (final Handle handle = dbi.open()) {
            final PlayerDao playerDao = handle.attach(PlayerDao.class);

            return playerDao.getPlayersForUser(userId).stream().
                    map(Player::fromDB).collect(toList());
        }
    }

}
