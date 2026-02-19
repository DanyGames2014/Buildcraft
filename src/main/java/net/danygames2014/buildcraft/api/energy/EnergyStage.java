package net.danygames2014.buildcraft.api.energy;

import net.modificationstation.stationapi.api.util.StringIdentifiable;

public enum EnergyStage implements StringIdentifiable {
    BLUE("blue", 0xFF0000FF, 0xFF009DFF),
    GREEN("green", 0xFF007F00, 0xFF5EE700),
    YELLOW("yellow", 0xFFFF4500, 0xFFFFDF00),
    RED("red", 0xFF7F0000, 0xFFFF3636),
    OVERHEAT("overheat", 0xFF7F0000, 0xFFFF3636);

    final String name;
    public final int primaryColor;
    public final int secondaryColor;

    EnergyStage(String name, int primaryColor, int secondaryColor) {
        this.name = name;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    @Override
    public String asString() {
        return name;
    }
}
