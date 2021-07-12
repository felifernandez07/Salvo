package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api")
@RestController

public class SalvoController {
    @Autowired
    private GameRepository gameRep;
    @Autowired
    private GamePlayerRepository gamePlayerRep;
    @Autowired
    private PlayerRepository playerRep;
    @Autowired
    private PasswordEncoder passwordEncoder;

  /*  @PostMapping("/players")
    public void addPlayer(@RequestBody Player player) { playerService.savePlayer(player); }

    @GetMapping("/players")
    public List<Player> getPlayers() { return playerService.getPlayers();}*/


    @RequestMapping("/games")
        public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("player","Guest");
        }
        else{
            dto.put("player",makePlayerDTO(playerRep.findByEmail(authentication.getName())));
        }

        dto.put("games", gameRep.findAll().stream().map(f -> makeGameDTO(f)).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping("/game_view/{x}")
    public Map<String, Object> getGame(@PathVariable Long x){
        GamePlayer gpl= gamePlayerRep.findById(x).get();
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
        dto.put("email", player.getEmail());
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

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (playerRep.findByEmail(email)!=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRep.save(new Player( email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}




