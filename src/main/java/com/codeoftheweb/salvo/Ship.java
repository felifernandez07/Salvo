package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator= "native")
    @GenericGenerator(name= "native", strategy = "native")

    private Long id;
    private String type;

    @ElementCollection
    @Column(name = "Location")
    private List<String> shipLocations;

    public Ship(){}
    public Ship(String shipType, List<String> shipLocation, GamePlayer gp) {
        this.type = shipType;
        this.shipLocations = shipLocation;
        this.gamePlayer= gp;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }
    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GamePlayer_id")
    private GamePlayer gamePlayer;

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }


}
