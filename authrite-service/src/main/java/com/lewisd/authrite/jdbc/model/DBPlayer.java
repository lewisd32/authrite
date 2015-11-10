package com.lewisd.authrite.jdbc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class DBPlayer {
    private UUID gameId;
    private UUID userId;
    private UUID raceId;
    private int slot;
    private boolean ready;

    public DBPlayer(final UUID gameId, final UUID userId, final UUID raceId, final int slot, final boolean ready) {
        this.gameId = gameId;
        this.userId = userId;
        this.raceId = raceId;
        this.slot = slot;
        this.ready = ready;
    }

    @JsonProperty
    public UUID getGameId() {
        return gameId;
    }

    @JsonProperty
    public void setGameId(final UUID gameId) {
        this.gameId = gameId;
    }

    @JsonProperty
    public UUID getUserId() {
        return userId;
    }

    @JsonProperty
    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    @JsonProperty
    public UUID getRaceId() {
        return raceId;
    }

    @JsonProperty
    public void setRaceId(final UUID raceId) {
        this.raceId = raceId;
    }

    @JsonProperty
    public int getSlot() {
        return slot;
    }

    @JsonProperty
    public void setSlot(final int slot) {
        this.slot = slot;
    }

    @JsonProperty
    public boolean isReady() {
        return ready;
    }

    @JsonProperty
    public void setReady(final boolean ready) {
        this.ready = ready;
    }
}
