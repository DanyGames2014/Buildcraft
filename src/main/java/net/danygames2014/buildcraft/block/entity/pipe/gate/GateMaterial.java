package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

public enum GateMaterial {
    REDSTONE("gate_interface_1.png", 146, 1, 0, 0),
    IRON("gate_interface_2.png", 164, 2, 0, 0),
    GOLD("gate_interface_3.png", 200, 4, 1, 0),
    DIAMOND("gate_interface_4.png", 200, 8, 1, 0);

    public static final GateMaterial[] VALUES = values();
    public final String backgroundTexture;
    public final int guiHeight;
    public final int numSlots;
    public final int numTriggerParameters;
    public final int numActionParameters;

    @Environment(EnvType.CLIENT)
    private int blockTexture;
    @Environment(EnvType.CLIENT)
    private int itemTexture;

    GateMaterial(String backgroundTexture, int guiHeight, int numSlots, int numTriggerParameters, int numActionParameters){
        this.backgroundTexture = backgroundTexture;
        this.guiHeight = guiHeight;
        this.numSlots = numSlots;
        this.numTriggerParameters = numTriggerParameters;
        this.numActionParameters = numActionParameters;
    }

    @Environment(EnvType.CLIENT)
    public int getBlockTexture(){
        return blockTexture;
    }

    @Environment(EnvType.CLIENT)
    public int getItemTexture(){
        return itemTexture;
    }

    public String getTag(){
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Environment(EnvType.CLIENT)
    public void setBlockTexture(int texture){
        if(this != REDSTONE){
            blockTexture = texture;
        }
    }

    @Environment(EnvType.CLIENT)
    public void setItemTexture(int texture){
        if(this != REDSTONE){
            itemTexture = texture;
        }
    }

    public static GateMaterial fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= VALUES.length) {
            return REDSTONE;
        }
        return VALUES[ordinal];
    }
}
