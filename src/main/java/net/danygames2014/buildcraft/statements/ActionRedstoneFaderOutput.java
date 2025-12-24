package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

public class ActionRedstoneFaderOutput extends ActionRedstoneOutput implements ActionInternal {
    public final int level;

    public ActionRedstoneFaderOutput(int level) {
        super(Buildcraft.NAMESPACE.id(String.format("buildcraft:redstone.output.%02d", level)));
        this.level = level;
    }

    @Override
    public String getDescription() {
        return String.format(TranslationStorage.getInstance().get("gate.trigger.redstone.input.level"), level);
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id(String.format("item/trigger/redstone_%02d", level)));
    }

    @Override
    protected int getSignalLevel() {
        return level;
    }
}
