package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository PlayerRep, GameRepository GameRep, GamePlayerRepository GamePlayerRep, ShipRepository ShipRep, SalvoRepository SalvoRep, ScoreRepository ScoreRep) {
		return (args) -> {
			Player player1 = new Player("jose@gmail.com");
			PlayerRep.save(player1);
			Player player2 = new Player("esteban@gmail.com");
			PlayerRep.save(player2);
			Player player3 = new Player("alan@gmail.com");
			PlayerRep.save(player3);

			Game game1 = new Game();
			Game game2 = new Game();
			Game game3 = new Game();

			Date date1 = new Date();
			Date date2 = Date.from(date1.toInstant().plusSeconds(3600));
			Date date3 = Date.from(date2.toInstant().plusSeconds(3600));

			game1.setDate(date1);
			game2.setDate(date2);
			game3.setDate(date3);

			GameRep.save(game1);
			GameRep.save(game2);
			GameRep.save(game3);

			GamePlayer gp1=new GamePlayer(new Date(), player1,game1);
			GamePlayerRep.save(gp1);

			GamePlayer gp2=new GamePlayer(new Date(), player2,game1);
			GamePlayerRep.save(gp2);

			Ship ship1=new Ship("cruiser", List.of("H1","H2","H3","H4"),gp1);
			ShipRep.save(ship1);

			Ship ship2=new Ship("patrol_boat", List.of("A1","A2"),gp1);
			ShipRep.save(ship2);

			Ship ship3=new Ship("submarine", List.of("B1","C1","D1"),gp2);
			ShipRep.save(ship3);

			Salvo salvo1=new Salvo(List.of("H1","H2","H3","H4"), 1, gp1);
			Salvo salvo2=new Salvo(List.of("A1","B1","C1","D1"), 2, gp1);
			Salvo salvo3=new Salvo(List.of("A1","A2","A3","A4"), 1, gp2);

			SalvoRep.save(salvo1);
			SalvoRep.save(salvo2);
			SalvoRep.save(salvo3);

			Score score1=new Score(1,new Date(), player1, game1);
			Score score2=new Score(0,new Date(), player2, game1);

			ScoreRep.save(score1);
			ScoreRep.save(score2);

		};
	}
}
