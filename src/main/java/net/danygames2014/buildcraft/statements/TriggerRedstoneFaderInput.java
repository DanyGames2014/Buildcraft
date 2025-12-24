package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

public class TriggerRedstoneFaderInput extends BCStatement implements TriggerInternal {
    public final int level;

    public TriggerRedstoneFaderInput(int level){
        super(Buildcraft.NAMESPACE.id(String.format("redtone.input.%02d", level)));
        this.level = level;
    }

    @Override
    public String getDescription() {
        return String.format(TranslationStorage.getInstance().get("gate.trigger.redstone.input.level"), level);
    }

    @Override
    public boolean isTriggerActive(StatementContainer source, StatementParameter[] parameters) {
        if (!(source instanceof Gate)) {
            return false;
        }

        Gate gate = (Gate) source;
        PipeBlockEntity tile = gate.getPipe();
        int inputLevel = tile.redstoneInput;
        if (parameters.length > 0 && parameters[0] instanceof StatementParameterRedstoneGateSideOnly &&
                    ((StatementParameterRedstoneGateSideOnly) parameters[0]).isOn) {
            inputLevel = tile.redstoneInputSide[gate.getSide().ordinal()];
        }

        return inputLevel == level;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id(String.format("item/trigger/redstone_%02d", level)));
    }

    @Override
    public StatementParameter createParameter(int index) {
        StatementParameter param = null;

        if (index == 0) {
            param = new StatementParameterRedstoneGateSideOnly();
        }

        return param;
    }

    @Override
    public int maxParameters() {
        return 1;
    }
}
