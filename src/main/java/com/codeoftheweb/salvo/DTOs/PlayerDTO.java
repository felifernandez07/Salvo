package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.Player;

public class PlayerDTO {

   private long id;

    private String email;


    public PlayerDTO(Player player) {
        this.id = player.getId();
        this.email = player.getEmail();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
