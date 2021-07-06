package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private Long id;
    private int turn;

    @ElementCollection
    @Column(name = "Location")
    private List<String> salvolocation;

    public Salvo() {
    }

    public Salvo( List<String> salvolocation, int turn, GamePlayer gp) {
        this.salvolocation = salvolocation;
        this.turn = turn;
        this.gamePlayer = gp;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GamePlayer_id")
    private GamePlayer gamePlayer;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }
    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getSalvolocation() {
        return salvolocation;
    }
    public void setSalvolocation(List<String> salvolocation) {
        this.salvolocation = salvolocation;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

}






