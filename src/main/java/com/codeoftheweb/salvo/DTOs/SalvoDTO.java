package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.Salvo;

import java.util.List;

public class SalvoDTO {

    private int turn;
    private long player;
    private List<String> locations;

    public SalvoDTO() {
    }

    public SalvoDTO(Salvo salvo) {
        this.turn = salvo.getTurn();
        this.player = salvo.getGamePlayer().getPlayer().getId();   //ver esto
        this.locations = salvo.getSalvoLocations();

    }

    public long getTurn() {
        return turn;
    }
    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getLocations() {
        return locations;
    }
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public long getPlayer() {
        return player;
    }
    public void setPlayer(long player) {
        this.player = player;
    }
}
