package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.Salvo;

import java.util.ArrayList;
import java.util.List;

public class HitsDTO {

    private List<String> self;
    private List<String> opponent;


    public HitsDTO() {
        this.self = new ArrayList<>();
        this.opponent = new ArrayList<>();
    }

    public List<String> getSelf() {
        return self;
    }

    public void setSelf(List<String> self) {
        this.self = self;
    }

    public List<String> getOpponent() {
        return opponent;
    }

    public void setOpponent(List<String> opponent) {
        this.opponent = opponent;
    }
}
