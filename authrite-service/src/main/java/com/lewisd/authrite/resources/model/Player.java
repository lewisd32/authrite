package com.lewisd.authrite.resources.model;

import com.lewisd.authrite.jdbc.model.DBPlayer;

import java.util.UUID;

public class Player {
    private final UUID gameId;

    public Player(final UUID gameId) {

        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public static Player fromDB(final DBPlayer player) {
        return new Player(
                player.getGameId()
        );
    }

}
