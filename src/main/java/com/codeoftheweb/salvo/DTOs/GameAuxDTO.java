package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.Game;
import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Salvo;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
        this.gameState = makeStates(gpl);   //
        this.gamePlayers = game.getGamePlayers().stream().map(g->new GamePlayerDTO(g)).collect(Collectors.toSet());
        this.ships= gpl.getShips().stream().map(barco -> new ShipsDTO(barco)).collect(Collectors.toSet());
        if (gpl.getOpponentPlayer().isPresent()){
            this.salvoes= gpl.getGame().getGamePlayers()
                    .stream()
                    .flatMap((a) -> a.getSalvoes().stream())
                    .map(s-> new SalvoDTO(s))
                    .collect(Collectors.toSet());
        }else {
            this.salvoes= new HashSet<>();
        }
        this.hits= new HitsDTO(gpl);
    }

    public String makeStates (GamePlayer gamePlayer) {
        if (gamePlayer.getShips().size() < 5) {
            return "PLACESHIPS";
        }
        if (gamePlayer.getOpponentPlayer().isEmpty() || gamePlayer.getOpponentPlayer().get().getShips().size()<5){
            return "WAITINGFOROPP";
        }
        if (fullHits(gamePlayer)==17 && fullHits(gamePlayer.getOpponentPlayer().get())==17 &&  gamePlayer.getSalvoes().size()==gamePlayer.getOpponentPlayer().get().getSalvoes().size()){
                return "TIE";

        }
        if (fullHits(gamePlayer)==17 && fullHits(gamePlayer)>fullHits(gamePlayer.getOpponentPlayer().get()) &&  gamePlayer.getSalvoes().size()==gamePlayer.getOpponentPlayer().get().getSalvoes().size() ){
            return "WON";
        }
        if (fullHits(gamePlayer.getOpponentPlayer().get())==17 && fullHits(gamePlayer)<fullHits(gamePlayer.getOpponentPlayer().get()) && gamePlayer.getSalvoes().size()==gamePlayer.getOpponentPlayer().get().getSalvoes().size()){
            return "LOST";
        }
        if (gamePlayer.getSalvoes().size()<= gamePlayer.getOpponentPlayer().get().getSalvoes().size()){
            return "PLAY";
        }
        if ((gamePlayer.getSalvoes().size() > gamePlayer.getOpponentPlayer().get().getSalvoes().size())){
            return "WAIT";
        }
        return "UNDEFINED";
    }

    public int fullHits (GamePlayer gamePlayer){
        return gamePlayer.getSalvoes().stream().flatMap(f -> getHitLocations(f).stream()).collect(Collectors.toList()).size();
    }

    private List<String> getHitLocations(Salvo salvo) {
        GamePlayer opponent = salvo.getGamePlayer().getOpponentPlayer().get();
        List<String> loca = opponent.getShips().stream().flatMap(f -> f.getShipLocations().stream()).collect(Collectors.toList());
        List<String> hits = loca.stream().filter(x -> salvo.getSalvoLocations().contains(x)).collect(Collectors.toList());
        return hits;
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
