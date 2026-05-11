package com.softdesign.tourney.event;

import java.io.Serializable;


public class TournamentEventMessage implements Serializable {

    private String action;           // "CREATED", "UPDATED", "DELETED"
    private String tournamentName;
    private String triggeredByUsername;

    public TournamentEventMessage() {}

    public TournamentEventMessage(String action, String tournamentName, String triggeredByUsername) {
        this.action = action;
        this.tournamentName = tournamentName;
        this.triggeredByUsername = triggeredByUsername;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public String getTriggeredByUsername() { return triggeredByUsername; }
    public void setTriggeredByUsername(String triggeredByUsername) { this.triggeredByUsername = triggeredByUsername; }
}