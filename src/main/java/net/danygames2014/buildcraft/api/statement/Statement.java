package net.danygames2014.buildcraft.api.statement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Statement {
    /**
     * Every statement needs a unique tag, it should be in the format of
     * "&lt;modid&gt;:&lt;name&gt;.
     *
     * @return the unique id
     */
    String getUniqueTag();

    @Environment(EnvType.CLIENT)
    int getIcon();

    @Environment(EnvType.CLIENT)
    void registerIcons(int iconRegister);

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
