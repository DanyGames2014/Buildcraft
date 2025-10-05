package net.danygames2014.buildcraft.screen.slot;

import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameterItemStack;
import net.danygames2014.buildcraft.screen.AdvancedInterfaceScreen;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;

public abstract class StatementParameterSlot extends AdvancedSlot{
    public int slot;
    public StatementSlot statementSlot;

    public StatementParameterSlot(AdvancedInterfaceScreen screen, int x, int y, int slot, StatementSlot iStatementSlot) {
        super(screen, x, y);
        this.slot = slot;
        this.statementSlot = iStatementSlot;
        statementSlot.parameters.add(this);
    }

    @Override
    public boolean isDefined() {
        return getParameter() != null;
    }

    @Override
    public String getDescription() {
        StatementParameter parameter = getParameter();

        // HACK: We're explicitly returning null so that the item stack description is used.
        if (parameter != null && !(parameter instanceof StatementParameterItemStack)) {
            return parameter.getDescription() != null ? parameter.getDescription() : "";
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getItemStack() {
        StatementParameter parameter = getParameter();

        if (parameter != null) {
            return parameter.getItemStack();
        } else {
            return null;
        }
    }

    @Override
    public Atlas.Sprite getSprite() {
        StatementParameter parameter = getParameter();

        if (parameter != null) {
            return parameter.getSprite();
        } else {
            return null;
        }
    }

    public abstract StatementParameter getParameter();

    public boolean isAllowed() {
        return statementSlot.getStatement() != null && slot < statementSlot.getStatement().maxParameters();
    }

    public boolean isRequired() {
        return statementSlot.getStatement() != null && slot < statementSlot.getStatement().minParameters();
    }

    public abstract void setParameter(StatementParameter param, boolean notifyServer);
}
