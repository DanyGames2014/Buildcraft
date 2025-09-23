package net.danygames2014.buildcraft.api.transport.statement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

public interface Statement {
    Identifier getIdentifier();

    @Environment(EnvType.CLIENT)
    Atlas.Sprite getSprite();

    @Environment(EnvType.CLIENT)
    void registerTextures();

    /**
     * Return the maximum number of parameter this statement can have, 0 if none.
     */
    int maxParameters();

    /**
     * Return the minimum number of parameter this statement can have, 0 if none.
     */
    int minParameters();

    /**
     * Return the statement description in the UI
     */
    String getDescription();

    /**
     * Create parameters for the statement.
     */
    StatementParameter createParameter(int index);

    /**
     * This returns the statement after a left rotation. Used in particular in
     * blueprints orientation.
     */
    Statement rotateLeft();
}
