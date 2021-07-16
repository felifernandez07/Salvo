package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
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
    @Autowired
    private ShipRepository shipRep;


  /*@PostMapping("/players")
    public void addPlayer(@RequestBody Player player) { playerService.savePlayer(player); }

    @GetMapping("/players")
    public List<Player> getPlayers() { return playerService.getPlayers();}*/


    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            dto.put("player", makePlayerDTO(playerRep.findByEmail(authentication.getName())));
        }
        dto.put("games", gameRep.findAll().stream().map(f -> makeGameDTO(f)).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping("/game_view/{x}")
    public ResponseEntity<Map<String, Object>> getGame(@PathVariable Long x, Authentication authentication) {
        GamePlayer gpl = gamePlayerRep.findById(x).get();
        Game game = gpl.getGame();
        Player player = playerRep.findByEmail(authentication.getName());
        if (gpl.getPlayer().getId()!=player.getId()){
            return new ResponseEntity<>(makeMap("error","cheater"),HttpStatus.UNAUTHORIZED);
        }
        else {
            Map<String, Object> m = makeGameDTOAux(game);
            m.put("ships", gpl.getShips().stream().map(barco -> makeShipsDTO(barco)).collect(Collectors.toList()));
            m.put("salvoes", gpl.getGame().getGamePlayers().stream().flatMap((a) -> a.getSalvoes().stream().map(this::makeSalvoesDTO)));
            m.put("hits", makeHitsDTO());
            return new ResponseEntity<>(m,HttpStatus.CREATED);
        }
    }

    private Map<String, Object> makeHitsDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put ("self",new ArrayList<>()) ;
        dto.put("opponent",new ArrayList<>());
        return dto;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gp -> makeGamePlayerDTO(gp)).collect(Collectors.toList()));
        dto.put("scores", game.getGamePlayers().stream().map(gp -> makeScoreDTO(gp)).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> makeGameDTOAux(Game game){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("created", game.getDate());
        dto.put ("gameState","PLACESHIPS");
        dto.put("gamePlayers", game.getGamePlayers().stream().map(gp -> makeGamePlayerDTO(gp)).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        //dto.put("gpid", makeGamePlayerDTO(gamePlayerRep.getById()));
        dto.put("id", player.getId());
        dto.put("email", player.getEmail());
        return dto;
    }

    private Map<String, Object> makeShipsDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getShipLocations());
        return dto;
    }

    private Map<String, Object> makeSalvoesDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations", salvo.getSalvolocation());
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
        if (playerRep.findByEmail(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRep.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);

        //if (Player!=gamePlayerRep.findById()) {
        //   return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);}
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
                    return new ResponseEntity<>(makeMap("error","you must place your 5 ships"), HttpStatus.FORBIDDEN);
                }
                return new ResponseEntity<>(makeMap("error","The player doesnt match with this game"), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(makeMap("error","you have to log in"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(makeMap("error","match not found"), HttpStatus.UNAUTHORIZED);
    }
}




