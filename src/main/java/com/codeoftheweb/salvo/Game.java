package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator= "native")
    @GenericGenerator(name= "native", strategy = "native")

    private Long id;
    private Date date;

    public Game () {}
    public Game(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    @OneToMany (fetch = FetchType.EAGER, mappedBy = "game")
    private Set<Score> scores = new HashSet<>();

}