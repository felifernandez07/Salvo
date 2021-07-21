package com.codeoftheweb.salvo.Controller;

import com.codeoftheweb.salvo.Repositories.*;
import com.codeoftheweb.salvo.Classes.*;
import com.codeoftheweb.salvo.DTOs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")


public class SalvoController {
    @Autowired
    private GameRepository gameRep;
    @Autowired
    private GamePlayerRepository gamePlayerRep;
    @Autowired
    private PlayerRepository playerRep;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ShipRepository shipRep;
    @Autowired
    private SalvoRepository salvoRep;



    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            Player player = playerRep.findByEmail(authentication.getName());
            dto.put("player", new PlayerDTO(player));
        }
        dto.put("games", gameRep.findAll().stream().map(f -> new GameDTO(f)).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping("/game_view/{x}")
    public ResponseEntity<?> getGame(@PathVariable Long x, Authentication authentication) {
        GamePlayer gpl = gamePlayerRep.findById(x).get();
        Game game = gpl.getGame();
        Player player = playerRep.findByEmail(authentication.getName());
        if (gpl.getPlayer().getId()!=player.getId()){
            return new ResponseEntity<>(makeMap("error","cheater"),HttpStatus.UNAUTHORIZED);
        }
        else {
            return new ResponseEntity<>(new GameAuxDTO(game,gpl),HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (playerRep.findByEmail(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRep.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @PostMapping("/games")
    public ResponseEntity <Map<String, Object>> createGame(Authentication authentication) {
        if (isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error","no user found"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRep.findByEmail(authentication.getName());

        if (player == null){
            return new ResponseEntity<>(makeMap("error","there is no player"), HttpStatus.UNAUTHORIZED);
        }
        Game game = new Game(new Date());
        gameRep.save(game);

        GamePlayer gamePlayer = new GamePlayer(new Date(),player,game);
        gamePlayerRep.save(gamePlayer);

        return new ResponseEntity<>(makeMap("gpid",gamePlayer.getId()),HttpStatus.CREATED);
    }

    @PostMapping ("/game/{gameID}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameID, Authentication authentication) {

        Date joinDate = new Date();
        Game game = gameRep.getById(gameID);

        if (!isGuest(authentication)) {
            if (game != null) {
                if (game.getGamePlayers().size() <2) {
                    GamePlayer gamePlayer = game.getGamePlayers().stream().findFirst().get();
                    Player player = playerRep.findByEmail(authentication.getName());
                    if (gamePlayer.getPlayer() != player) {
                        GamePlayer gamePlayer1 = new GamePlayer(new Date(),player,game);
                        gamePlayerRep.save(gamePlayer1);
                        return new ResponseEntity<>(makeMap("gpid", gamePlayer1.getId()), HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(makeMap("Error", "You can not rejoin a game"), HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(makeMap("Error", "The game is full, please join or create another one. Thanks!"), HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(makeMap("Error", "The game does not exists"), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(makeMap("Error", "You must login!"), HttpStatus.UNAUTHORIZED);
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
}

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> placeShips (@PathVariable long gamePlayerId, @RequestBody List<Ship> ships,Authentication authentication) {

        if (gamePlayerRep.findById(gamePlayerId).isPresent()){
            GamePlayer gp = gamePlayerRep.findById(gamePlayerId).get();
            if (!isGuest(authentication)){
                Player pl = playerRep.findByEmail(authentication.getName());
                if (gp.getPlayer().getId() == pl.getId()){
                    if (gp.getShips().size()==0){
                        if (ships.size() == 5) {

                            ships.forEach(ship -> shipRep.save(new Ship(ship.getType(), ship.getShipLocations(), gp)));
                            return new ResponseEntity<>(makeMap("OK", "ships successfully added"), HttpStatus.CREATED);

                        } else if (ships.size()<5) {
                            return new ResponseEntity<>(makeMap("error","you have to place five ships"), HttpStatus.FORBIDDEN);
                        }
                        return new ResponseEntity<>(makeMap("error","you cant place more than 5 ships"), HttpStatus.FORBIDDEN);
                    }
                    return new ResponseEntity<>(makeMap("error","you cant place your ships again"), HttpStatus.FORBIDDEN);
                }
                return new ResponseEntity<>(makeMap("error","The player doesnt match with this game"), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(makeMap("error","you have to log in"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(makeMap("error","match not found"), HttpStatus.UNAUTHORIZED);
    }

@PostMapping("/games/players/{gamePlayerId}/salvoes")
public ResponseEntity<Map<String, Object>> StoreSalvoes (@PathVariable long gamePlayerId, @RequestBody Salvo salvos,Authentication authentication) {

GamePlayer gamePlayer= gamePlayerRep.getById(gamePlayerId);
Player player = playerRep.findByEmail(authentication.getName());
if (isGuest(authentication)) {
    return new ResponseEntity<>(makeMap("Error", "You must log in"), HttpStatus.UNAUTHORIZED);
}
if (gamePlayer.getPlayer().getId()!= player.getId()) {
    return new ResponseEntity<>(makeMap("Error", "You dont belong here"), HttpStatus.UNAUTHORIZED);
}
if (gamePlayer.getSalvoes().size()!=0) {
    return new ResponseEntity<>(makeMap("Error", "You already fired"), HttpStatus.FORBIDDEN);
}
if (gamePlayer.getSalvoes().size()>5 || gamePlayer.getSalvoes().size()<=0) {
    return new ResponseEntity<>(makeMap("Eror","You have to submit a salvo and a max of 5"),HttpStatus.FORBIDDEN);
}
if (gamePlayer.getSalvoes().size()<= gamePlayer.getOpponent().get().getSalvoes().size()) {
   salvoRep.save(new Salvo(salvos.getSalvoLocations(), salvos.getTurn(), salvos.getGamePlayer()));
   return new ResponseEntity<>(HttpStatus.CREATED);
}
return new ResponseEntity<>(makeMap("Error","You already submitted a salvo"), HttpStatus.FORBIDDEN);

}
}




