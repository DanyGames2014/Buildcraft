package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.api.transport.statement.container.RedstoneStatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.container.SidedStatementContainer;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.danygames2014.buildcraft.util.DirectionUtil;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

import java.util.Locale;

public class TriggerRedstoneInput extends BCStatement implements TriggerInternal {
    boolean active;

    public TriggerRedstoneInput(boolean active) {
        super(Buildcraft.NAMESPACE.id("redstone.input." + (active ? "active" : "inactive")));
        this.active = active;
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.trigger.redstone.input." + (active ? "active" : "inactive"));
    }

    @Override
    public StatementParameter createParameter(int index) {
        StatementParameter param = null;
        if(index == 0){
            param = new StatementParameterRedstoneGateSideOnly();
        }

        return param;
    }

    @Override
    public boolean isTriggerActive(StatementContainer container, StatementParameter[] parameters) {
        if (container instanceof RedstoneStatementContainer) {
            // TODO: check if side should be null
            int level = ((RedstoneStatementContainer) container).getRedstoneInput(null);
            if (parameters.length > 0 && parameters[0] instanceof StatementParameterRedstoneGateSideOnly &&
                        ((StatementParameterRedstoneGateSideOnly) parameters[0]).isOn &&
                        container instanceof SidedStatementContainer) {
                level = ((RedstoneStatementContainer) container).getRedstoneInput(((SidedStatementContainer) container).getSide());
            }

            return active ? level > 0 : level == 0;
        } else {
            return false;
        }
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_redstoneinput_"  + (active ? "active" : "inactive")));
    }
}
