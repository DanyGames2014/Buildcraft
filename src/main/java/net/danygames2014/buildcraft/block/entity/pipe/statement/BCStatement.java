package net.danygames2014.buildcraft.block.entity.pipe.statement;

import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementManager;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

public abstract class BCStatement implements Statement {
    protected final Identifier uniqueTag;

    protected Atlas.Sprite icon;

    /**
     * UniqueTag accepts multiple possible tags, use this feature to migrate to
     * more standardized tags if needed, otherwise just pass a single string.
     * The first passed string will be the one used when saved to disk.
     */
    public BCStatement(Identifier... uniqueTag) {
        this.uniqueTag = uniqueTag[0];
        for (Identifier tag : uniqueTag) {
            StatementManager.statements.put(tag, this);
        }
    }

    @Override
    public Identifier getIdentifier() {
        return uniqueTag;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Atlas.Sprite getSprite() {
        return icon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerTextures() {
    }

    @Override
    public int maxParameters() {
        return 0;
    }

    @Override
    public int minParameters() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public Statement rotateLeft() {
        return this;
    }

    @Override
    public StatementParameter createParameter(int index) {
        return null;
    }
}
