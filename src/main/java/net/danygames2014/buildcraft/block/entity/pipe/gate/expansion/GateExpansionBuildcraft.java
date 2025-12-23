package net.danygames2014.buildcraft.block.entity.pipe.gate.expansion;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansionController;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;

public class GateExpansionBuildcraft implements GateExpansion {

    private final Identifier identifier;
    private Atlas.Sprite iconBlock;
    private Atlas.Sprite iconItem;

    public GateExpansionBuildcraft(Identifier identifier){
        this.identifier = identifier;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getDisplayName() {
        return TranslationStorage.getInstance().get("gate.expansion." + identifier.path);
    }

    @Override
    public GateExpansionController makeController(PipeBlockEntity pipe) {
        return null;
    }

    @Override
    public void registerTextures() {
        iconBlock = Atlases.getTerrain().addTexture(Buildcraft.NAMESPACE.id("block/gate/gate_expansion_" + identifier.path));
        iconItem = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/gate/gate_expansion_" + identifier.path));
    }

    @Override
    public Atlas.Sprite getOverlayBlockSprite() {
        return iconBlock;
    }

    @Override
    public Atlas.Sprite getOverlayItemSprite() {
        return iconItem;
    }
}
