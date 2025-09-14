package net.danygames2014.buildcraft.api.energy;

import net.modificationstation.stationapi.api.util.StringIdentifiable;

public enum EnergyStage implements StringIdentifiable {
    BLUE("blue"),
    GREEN("green"),
    YELLOW("yellow"),
    RED("red"),
    OVERHEAT("overheat");

    final String name;
    
    EnergyStage(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
