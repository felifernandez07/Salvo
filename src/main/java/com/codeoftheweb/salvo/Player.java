package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.GamePlayer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity

public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator= "native")
    @GenericGenerator(name= "native", strategy = "native")

    private long id;


    public Player(){}
    public Player(String email, String password) {
        this.email =email;
        this.password = password;
    }

    @OneToMany (fetch = FetchType.EAGER, mappedBy = "player")
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    public long getId() {
        return id;
    }
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    @OneToMany (fetch = FetchType.EAGER, mappedBy = "player")
    private Set<Score> scores = new HashSet<>();

    public Optional<Score> getScore(Game game){
        return scores.stream().filter(score -> score.getGameId().equals(game.getId())).findFirst();
    }

    private String password;

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    private String email;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}

