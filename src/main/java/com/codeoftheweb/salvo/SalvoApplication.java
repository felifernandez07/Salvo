package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Autowired
	PasswordEncoder passwordEncoder;
	@Bean
	public CommandLineRunner initData(PlayerRepository PlayerRep, GameRepository GameRep, GamePlayerRepository GamePlayerRep, ShipRepository ShipRep, SalvoRepository SalvoRep, ScoreRepository ScoreRep) {
		return (args) -> {
			Player player1 = new Player("jose@gmail.com",passwordEncoder.encode("24"));
			PlayerRep.save(player1);
			Player player2 = new Player("esteban@gmail.com", passwordEncoder.encode("42"));
			PlayerRep.save(player2);
			Player player3 = new Player("alan@gmail.com", passwordEncoder.encode("KB"));
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

			GamePlayer gp1 = new GamePlayer(new Date(), player1, game1);
			GamePlayerRep.save(gp1);

			GamePlayer gp2 = new GamePlayer(new Date(), player2, game1);
			GamePlayerRep.save(gp2);

			Ship ship1 = new Ship("cruiser", List.of("H1", "H2", "H3", "H4"), gp1);
			ShipRep.save(ship1);

			Ship ship2 = new Ship("patrol_boat", List.of("A1", "A2"), gp1);
			ShipRep.save(ship2);

			Ship ship3 = new Ship("submarine", List.of("B1", "C1", "D1"), gp2);
			ShipRep.save(ship3);

			Salvo salvo1 = new Salvo(List.of("H1", "H2", "H3", "H4"), 1, gp1);
			Salvo salvo2 = new Salvo(List.of("A1", "B1", "C1", "D1"), 2, gp1);
			Salvo salvo3 = new Salvo(List.of("A1", "A2", "A3", "A4"), 1, gp2);

			SalvoRep.save(salvo1);
			SalvoRep.save(salvo2);
			SalvoRep.save(salvo3);

			Score score1 = new Score(1, new Date(), player1, game1);
			Score score2 = new Score(0, new Date(), player2, game1);

			ScoreRep.save(score1);
			ScoreRep.save(score2);
		};
	}

	}
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	PlayerRepository playerRepository;

	@Bean
	public PasswordEncoder passwordEncoder() { return PasswordEncoderFactories.createDelegatingPasswordEncoder();}

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputUserName -> { Player player = playerRepository.findByEmail(inputUserName);
			if (player != null) {
				return new User(player.getEmail(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputUserName);
			}
		});
	}
}

	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
					.antMatchers("/web/**","/h2-console/**","/api/**").permitAll()
					/*.antMatchers(HttpMethod.POST, "/api/players").permitAll()
					.antMatchers(HttpMethod.POST, "/api/game_view/**").permitAll()
					.antMatchers(HttpMethod.POST, "/api/game/{gameID}/players").permitAll()
					.antMatchers(HttpMethod.POST, "/games/players/{gamePlayerId}/ships").permitAll()*/
					.antMatchers("/**").hasAuthority("USER")
					.antMatchers("/h2-console/**").permitAll().anyRequest().authenticated()
					.and().csrf().ignoringAntMatchers("/h2-console/**")
					.and().headers().frameOptions().sameOrigin();

			http
			        .formLogin()
					.usernameParameter("name")
					.passwordParameter("pwd")
					.loginPage("/api/login");
			http.logout().logoutUrl("/api/logout");

			// turn off checking for CSRF tokens
			http.csrf().disable();

			// if user is not authenticated, just send an authentication failure response
			http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

			// if login is successful, just clear the flags asking for authentication
			http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

			// if login fails, just send an authentication failure response
			http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

			// if logout is successful, just send a success response
			http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
		}

		private void clearAuthenticationAttributes(HttpServletRequest request) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			}
		}

}

