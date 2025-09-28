package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

public enum GateLogic {
    AND, OR;
    public static final GateLogic[] VALUES = values();

    @Environment(EnvType.CLIENT)
    private int litTexture;

    @Environment(EnvType.CLIENT)
    private int darkTexture;

    @Environment(EnvType.CLIENT)
    private int itemTexture;

    @Environment(EnvType.CLIENT)
    private int gateTexture;

    @Environment(EnvType.CLIENT)
    public int getLitTexture() {
        return litTexture;
    }

    @Environment(EnvType.CLIENT)
    public int getDarkTexture() {
        return darkTexture;
    }

    @Environment(EnvType.CLIENT)
    public int getItemTexture() {
        return itemTexture;
    }

    @Environment(EnvType.CLIENT)
    public int getGateTexture() {
        return gateTexture;
    }

    // TODO: actually register textures
    @Environment(EnvType.CLIENT)
    public void registerBlockTextures(){

    }

    @Environment(EnvType.CLIENT)
    public void registerItemIcons(){

    }

    public String getTag() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public static GateLogic fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= VALUES.length) {
            return AND;
        }
        return VALUES[ordinal];
    }
}
