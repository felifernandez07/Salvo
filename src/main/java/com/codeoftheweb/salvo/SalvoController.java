package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController

public class SalvoController {
    @Autowired
    private GameRepository GameRep;
    @Autowired
    private GamePlayerRepository GamePlayerRep;

    @RequestMapping("/games")
    public List<Object> getGames() {
        List<Game> lista = GameRep.findAll();
        return lista.stream().map(listId -> makeGameDTO(listId)).collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{x}")
    public Map<String, Object> getGame(@PathVariable Long x){
        GamePlayer gpl= GamePlayerRep.findById(x).get();
        Game game= gpl.getGame();
        Map<String, Object> m= makeGameDTO(game);
        m.put("ships",gpl.getShips().stream().map(barco->makeShipsDTO(barco)).collect(Collectors.toList()));
        m.put("salvoes", gpl.getGame().getGamePlayers().stream().flatMap((a) -> a.getSalvoes().stream().map(this::makeSalvoesDTO)));
        return m;

    }


    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto=new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gp-> makeGamePlayerDTO(gp)).collect(Collectors.toList()));
        dto.put("scores", game.getGamePlayers().stream().map(gp -> makeScoreDTO(gp)).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> jugadordto=new LinkedHashMap<String, Object>();
        jugadordto.put("id", gamePlayer.getId());
        jugadordto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return jugadordto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto=new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    private Map<String, Object> makeShipsDTO(Ship ship) {
        Map<String, Object> dto=new LinkedHashMap<String, Object>();
        dto.put("type",ship.getType());
        dto.put("locations",ship.getShiplocation());
        return dto;
    }

    private Map<String, Object> makeSalvoesDTO(Salvo salvo) {
        Map<String, Object> dto=new LinkedHashMap<String, Object>();
        dto.put("turn",salvo.getTurn());
        dto.put("player",salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations",salvo.getSalvolocation());
        return dto;
    }

    private Map<String, Object> makeScoreDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (gamePlayer.getScore().isPresent()) {
            dto.put("player", gamePlayer.getPlayer().getId());
            dto.put("score", gamePlayer.getScore().get().getScore());
            dto.put("finishDate", gamePlayer.getScore().get().getDate());
            return dto;
        } else {
            dto.put("score", "el juego no tiene puntaje");
            return dto;
        }
}
}

