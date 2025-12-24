package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.container.RedstoneStatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.container.SidedStatementContainer;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class ActionRedstoneOutput extends BCStatement implements ActionInternal {

    public ActionRedstoneOutput(Identifier identifier) {
        // Used by fader output
        super(identifier);
    }

    public ActionRedstoneOutput() {
        super(Buildcraft.NAMESPACE.id("redstone.output"));
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.action.redstone.signal");
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

    protected boolean isSideOnly(StatementParameter[] parameters) {
        if (parameters != null && parameters.length >= 1 && parameters[0] instanceof StatementParameterRedstoneGateSideOnly) {
            return ((StatementParameterRedstoneGateSideOnly) parameters[0]).isOn;
        }

        return false;
    }

    @Override
    public void actionActivate(StatementContainer source, StatementParameter[] parameters) {
        if (source instanceof RedstoneStatementContainer) {
            Direction side = null;
            if (source instanceof SidedStatementContainer && isSideOnly(parameters)) {
                side = ((SidedStatementContainer) source).getSide();
            }
            ((RedstoneStatementContainer) source).setRedstoneOutput(side, getSignalLevel());
        }
    }

    protected int getSignalLevel() {
        return 15;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/action_redstoneoutput"));
    }
}
