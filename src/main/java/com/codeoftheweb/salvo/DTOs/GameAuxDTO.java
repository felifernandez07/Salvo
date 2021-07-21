package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.Game;
import com.codeoftheweb.salvo.Classes.GamePlayer;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class GameAuxDTO {

    private Long id;
    private Date created;
    private String gameState;
    private Set<GamePlayerDTO> gamePlayers;
    private Set<ShipsDTO>ships;
    private Set<SalvoDTO>salvoes;
    private HitsDTO hits;

    public GameAuxDTO() {
    }

    public GameAuxDTO(Game game, GamePlayer gpl) {
        this.id = game.getId();
        this.created = game.getDate();
        this.gameState = "PLACESHIPS";
        this.gamePlayers = game.getGamePlayers().stream().map(g->new GamePlayerDTO(g)).collect(Collectors.toSet());
        this.ships= gpl.getShips().stream().map(barco -> new ShipsDTO(barco)).collect(Collectors.toSet());
        this.salvoes= gpl.getGame().getGamePlayers().stream().flatMap((a) -> a.getSalvoes().stream().map(s-> new SalvoDTO(s))).collect(Collectors.toSet());
        this.hits= new HitsDTO();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayerDTO> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<ShipsDTO> getShips() {
        return ships;
    }

    public void setShips(Set<ShipsDTO> ships) {
        this.ships = ships;
    }

    public Set<SalvoDTO> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<SalvoDTO> salvoes) {
        this.salvoes = salvoes;
    }

    public HitsDTO getHits() {
        return hits;
    }

    public void setHits(HitsDTO hits) {
        this.hits = hits;
    }
}
