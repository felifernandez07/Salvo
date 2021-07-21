package com.codeoftheweb.salvo.Classes;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gamePlayer")
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "gamePlayer")
    private Set<Salvo> salvoes = new HashSet<>();

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    //public Player getPlayer() {return player;}
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {return game;}
    public void setGame(Game game) {
        this.game = game;
    }

    public GamePlayer(){}
    public GamePlayer(Date date, Player player, Game game) {
        this.date = date;
        this.player = player;
        this.game = game;
    }

    public long getId() {
        return id;
    }
    public Player getPlayer() {
        return player;
    }

    public Set<Ship> getShips() {
        return ships;
    }
    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }
    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public Optional<Score> getScore(){
        return player.getScore(game);
    }

    public Optional<GamePlayer> getOpponent(){
        return this.getGame().getGamePlayers().stream().filter(gp -> gp.getId() != this.getId()).findFirst();
    }

}
