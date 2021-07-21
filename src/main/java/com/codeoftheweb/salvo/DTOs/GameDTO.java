package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.Game;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class GameDTO {

    private Long id;
    private Date created;
    private Set<GamePlayerDTO> gamePlayers ;
    private Set<ScoreDTO> scores;

    public GameDTO() {
    }
    public GameDTO(Game game) {
        this.id = game.getId();
        this.created = game.getDate();
        this.gamePlayers = game.getGamePlayers().stream().map( gp ->new GamePlayerDTO(gp)).collect(Collectors.toSet());
        this.scores = game.getGamePlayers().stream().map( x -> new ScoreDTO(x)).collect(Collectors.toSet());
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

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }
    public void setGamePlayers(Set<GamePlayerDTO> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<ScoreDTO> getScores() {
        return scores;
    }
    public void setScores(Set<ScoreDTO> scores) {
        this.scores = scores;
    }
}
