package net.danygames2014.buildcraft.screen.slot;

import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.screen.AdvancedInterfaceScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;

import java.util.ArrayList;

public abstract class StatementSlot extends AdvancedSlot{
    public int slot;
    public ArrayList<StatementParameterSlot> parameters = new ArrayList<>();
    public StatementSlot(AdvancedInterfaceScreen screen, int x, int y, int slot) {
        super(screen, x, y);

        this.slot = slot;
    }

    @Override
    public String getDescription() {
        Statement stmt = getStatement();

        if (stmt != null) {
            return stmt.getDescription();
        } else {
            return "";
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Atlas.Sprite getSprite() {
        Statement stmt = getStatement();

        if (stmt != null) {
            return stmt.getSprite();
        } else {
            return null;
        }
    }

    @Override
    public boolean isDefined() {
        return getStatement() != null;
    }

    public abstract Statement getStatement();
}
