package com.codeoftheweb.salvo.DTOs;

import com.codeoftheweb.salvo.Classes.Ship;
import java.util.List;

public class ShipsDTO {

    private String type;
    private List<String> locations;

    public ShipsDTO() {
    }

    public ShipsDTO(Ship ship) {
        this.type = ship.getType();
        this.locations = ship.getShipLocations();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
