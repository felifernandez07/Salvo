package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.GamePlayer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity

public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator= "native")
    @GenericGenerator(name= "native", strategy = "native")

    private long id;

    private String userName;

    public Player(){}

    public Player(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName=userName;
    }

    @OneToMany (fetch = FetchType.EAGER, mappedBy = "player")
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    public long getId() {
        return id;
    }
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }
}

