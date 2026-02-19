package net.danygames2014.buildcraft.api.transport.statement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

public interface StatementParameter {
    Identifier getIdentifier();

    @Environment(EnvType.CLIENT)
    Atlas.Sprite getSprite();

    ItemStack getItemStack();

    /**
     * Something that is initially unintuitive: you HAVE to
     * keep your icons as static variables, due to the fact
     * that every IStatementParameter is instantiated upon creation,
     * in opposition to IStatements which are singletons (due to the
     * fact that they, unlike Parameters, store no additional data)
     */
    @Environment(EnvType.CLIENT)
    void registerTextures();

    /**
     * Return the parameter description in the UI
     */
    String getDescription();

    void click(StatementContainer source, Statement stmt, ItemStack stack, StatementMouseClick mouse);

    void readNBT(NbtCompound nbt);

    void writeNBT(NbtCompound nbt);

    /**
     * This returns the parameter after a left rotation. Used in particular in
     * blueprints orientation.
     */
    StatementParameter rotateLeft();
}
