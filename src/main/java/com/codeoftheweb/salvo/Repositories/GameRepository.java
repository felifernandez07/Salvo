package com.codeoftheweb.salvo.Repositories;

import com.codeoftheweb.salvo.Classes.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {
        List<Game> findByDate(String date);
    }


