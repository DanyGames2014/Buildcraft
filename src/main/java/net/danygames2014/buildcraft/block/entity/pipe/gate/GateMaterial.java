package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.danygames2014.buildcraft.Buildcraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

import java.util.Locale;

public enum GateMaterial {
    REDSTONE("gate_interface_1.png", 146, 1, 0, 0),
    IRON("gate_interface_2.png", 164, 2, 0, 0),
    GOLD("gate_interface_3.png", 200, 4, 1, 0),
    DIAMOND("gate_interface_4.png", 200, 8, 1, 0),
    EMERALD("gate_interface_5.png", 200, 4, 3, 3),
    GLOWSTONE("gate_interface_6.png", 164, 2, 1, 1);
    public static final GateMaterial[] VALUES = values();
    public final String backgroundTexture;
    public final int guiHeight;
    public final int numSlots;
    public final int numTriggerParameters;
    public final int numActionParameters;

    @Environment(EnvType.CLIENT)
    private Atlas.Sprite blockTexture;
    @Environment(EnvType.CLIENT)
    private Atlas.Sprite itemTexture;

    GateMaterial(String backgroundTexture, int guiHeight, int numSlots, int numTriggerParameters, int numActionParameters){
        this.backgroundTexture = backgroundTexture;
        this.guiHeight = guiHeight;
        this.numSlots = numSlots;
        this.numTriggerParameters = numTriggerParameters;
        this.numActionParameters = numActionParameters;
    }

    @Environment(EnvType.CLIENT)
    public Atlas.Sprite getBlockTexture(){
        return blockTexture;
    }

    @Environment(EnvType.CLIENT)
    public Atlas.Sprite getItemTexture(){
        return itemTexture;
    }

    public String getTag(){
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Environment(EnvType.CLIENT)
    public void registerTextures(){
        blockTexture = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/gate/gate_material_" + getTag()));
        itemTexture = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/gate/gate_material_" + getTag()));
    }

    public static GateMaterial fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= VALUES.length) {
            return REDSTONE;
        }
        return VALUES[ordinal];
    }
}
