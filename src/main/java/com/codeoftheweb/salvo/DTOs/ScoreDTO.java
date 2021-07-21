package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.GamePlayer;
import com.codeoftheweb.salvo.Classes.Score;

import java.util.Date;
import java.util.Optional;

public class ScoreDTO {

    private Object score;
    private Date finishDate;
    private long player;

    public ScoreDTO() {
    }

    public ScoreDTO(GamePlayer gamePlayer) {
        if (gamePlayer.getScore().isPresent()) {
            this.player = gamePlayer.getPlayer().getId();
            this.score = gamePlayer.getScore().get().getScore();
            this.finishDate = gamePlayer.getScore().get().getFinishDate();
        } else {
            this.score = "el juego no tiene puntaje";
        }
    }

    public Object getScore() {
        return score;
    }

    public void setScore(Object score) {
        this.score = score;
    }

    public long getPlayer() {
        return player;
    }
    public void setPlayer(long player) {
        this.player = player;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }


}
