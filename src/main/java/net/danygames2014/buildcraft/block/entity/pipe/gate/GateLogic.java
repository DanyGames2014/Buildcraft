package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.danygames2014.buildcraft.Buildcraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

import java.util.Locale;

public enum GateLogic {
    AND, OR;
    public static final GateLogic[] VALUES = values();

    @Environment(EnvType.CLIENT)
    private Atlas.Sprite litTexture;

    @Environment(EnvType.CLIENT)
    private Atlas.Sprite darkTexture;

    @Environment(EnvType.CLIENT)
    private Atlas.Sprite itemTexture;

    @Environment(EnvType.CLIENT)
    private Atlas.Sprite gateTexture;

    @Environment(EnvType.CLIENT)
    public Atlas.Sprite getLitTexture() {
        return litTexture;
    }

    @Environment(EnvType.CLIENT)
    public Atlas.Sprite getDarkTexture() {
        return darkTexture;
    }

    @Environment(EnvType.CLIENT)
    public Atlas.Sprite getItemTexture() {
        return itemTexture;
    }

    @Environment(EnvType.CLIENT)
    public Atlas.Sprite getGateTexture() {
        return gateTexture;
    }

    // TODO: actually register textures
    @Environment(EnvType.CLIENT)
    public void registerTextures(){
        litTexture = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/gate/gate_" + getTag() + "_lit"));
        darkTexture = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/gate/gate_" + getTag() + "_dark"));
        gateTexture = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/gate/gate_" + getTag()));

        itemTexture = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/gate/gate_logic_" + getTag()));
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
