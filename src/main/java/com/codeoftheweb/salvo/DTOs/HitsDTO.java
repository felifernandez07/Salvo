package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Salvo;
import com.codeoftheweb.salvo.Classes.Ship;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HitsDTO {

    private List<Map> self;
    private List<Map> opponent;

    public HitsDTO(){}

    public HitsDTO(GamePlayer gamePlayer) {
        if(gamePlayer.getOpponentPlayer().isPresent()) {
            this.self = getDamages(gamePlayer.getOpponentPlayer().get());
            this.opponent = getDamages(gamePlayer);
        }else {
            this.self = new ArrayList<>();
            this.opponent = new ArrayList<>();
        }
    }


    /*private Map<String, Object> getHits(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("hitsLocations", this.getHitLocations(salvo));
        dto.put("damages", this.getDamages(Salvo));
        dto.put("missed", salvo.getSalvoLocations().size() - this.getHitLocations(salvo).size());
        return dto;
    }*/


    private List<Map> getDamages(GamePlayer gpl) {
        List<Map> selfz = new ArrayList<>();
        //List<String> self = gpl.getSalvoes().stream().flatMap(b -> b.getSalvoLocations().stream()).collect(Collectors.toList());
        //List<String> oppo = gpl.getOpponentPlayer().get().getShips().stream().flatMap(p -> p.getShipLocations().stream()).collect(Collectors.toList());
        if (!gpl.getOpponentPlayer().isPresent()) {
            return null;
        }

        int carrierTotal = 0;
        int battleshipTotal = 0;
        int submarineTotal = 0;
        int destroyerTotal = 0;
        int patrolboatTotal = 0;

        List<String> carrierLocation = getLocationByType(gpl.getOpponentPlayer().get(),"carrier");
        List<String> battleshipLocation = getLocationByType(gpl.getOpponentPlayer().get(),"battleship");
        List<String> submarineLocation = getLocationByType(gpl.getOpponentPlayer().get(),"submarine");
        List<String> destroyerLocation = getLocationByType(gpl.getOpponentPlayer().get(),"destroyer");
        List<String> patrolboatLocation = getLocationByType(gpl.getOpponentPlayer().get(),"patrolboat");


        for (Salvo salvo : gpl.getSalvoes()) {

            int carrierHitsInTurn = 0;
            int battleshipHitsInTurn = 0;
            int submarineHitsInTurn = 0;
            int destroyerHitsInTurn = 0;
            int patrolboatHitsInTurn = 0;
            int missed = salvo.getSalvoLocations().size();

            /*HitsDTO hitsMapPerTurn = new HitsDTO();
            DamageDTO damagePerTurn = new DamageDTO();*/

            //List<String> salvoLocationList = new ArrayList<>();
            List<String> hitsCellList = new ArrayList<>();

            for (String salvoshot : salvo.getSalvoLocations()) {
                if (carrierLocation.contains(salvoshot)) {
                    carrierTotal++;
                    carrierHitsInTurn++;
                    hitsCellList.add(salvoshot);
                    missed--;
                }
                if (battleshipLocation.contains(salvoshot)) {
                    battleshipTotal++;
                    battleshipHitsInTurn++;
                    hitsCellList.add(salvoshot);
                    missed--;
                }
                if (submarineLocation.contains(salvoshot)) {
                    submarineTotal++;
                    submarineHitsInTurn++;
                    hitsCellList.add(salvoshot);
                    missed--;
                }
                if (destroyerLocation.contains(salvoshot)) {
                    destroyerTotal++;
                    destroyerHitsInTurn++;
                    hitsCellList.add(salvoshot);
                    missed--;
                }
                if (patrolboatLocation.contains(salvoshot)) {
                    patrolboatTotal++;
                    patrolboatHitsInTurn++;
                    hitsCellList.add(salvoshot);
                    missed--;
                }
            }

            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("carrierHits",carrierHitsInTurn);
            dto.put("battleshipHits",battleshipHitsInTurn);
            dto.put("submarineHits",submarineHitsInTurn);
            dto.put("destroyerHits",destroyerHitsInTurn);
            dto.put("patrolboatHits",patrolboatHitsInTurn);
            dto.put("carrier",carrierTotal);
            dto.put("battleship",battleshipTotal);
            dto.put("submarine",submarineTotal);
            dto.put("destroyer",destroyerTotal);
            dto.put("patrolboat",patrolboatTotal);



            Map<String, Object> dtototal = new LinkedHashMap<>();
            dtototal.put("turn", salvo.getTurn());
            dtototal.put("hitLocations", hitsCellList);
            dtototal.put("damages",dto);
            dtototal.put("missed",missed);

            selfz.add(dtototal);
        }
        return selfz;
    }

    private  List<String> getLocationByType (GamePlayer gamePlayer,String type){
        Ship locations= gamePlayer.getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst().orElse(null);
        if (locations!=null){
            return locations.getShipLocations();
        }
        return new ArrayList<>();
    }

   /* private List<String> getHitLocations(Salvo salvo) {
        GamePlayer opponent = salvo.getGamePlayer().getOpponentPlayer().get();
        List<String> loca = opponent.getShips().stream().flatMap(f -> f.getShipLocations().stream()).collect(Collectors.toList());
        List<String> hits = loca.stream().filter(x -> salvo.getSalvoLocations().contains(x)).collect(Collectors.toList());
        return hits;
    }*/
    /*public  int fullHits (GamePlayer gamePlayer){
        return gamePlayer.getSalvoes().stream().flatMap(f -> getHitLocations(f).stream()).collect(Collectors.toList()).size();
    }*/

    public List<Map> getSelf() {
        return self;
    }

    public void setSelf(List<Map> self) {
        this.self = self;
    }

    public List<Map> getOpponent() {
        return opponent;
    }

    public void setOpponent(List<Map> opponent) {
        this.opponent = opponent;
    }
}

