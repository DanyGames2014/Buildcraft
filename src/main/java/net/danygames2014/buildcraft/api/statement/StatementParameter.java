package net.danygames2014.buildcraft.api.statement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface StatementParameter {
    /**
     * Every parameter needs a unique tag, it should be in the format of
     * "&lt;modi&gt;:&lt;name&gt;".
     *
     * @return the unique id
     */
    String getUniqueTag();

    @Environment(EnvType.CLIENT)
    int getIcon();

    ItemStack getItemStack();

    /**
     * Something that is initially unintuitive: you HAVE to
     * keep your icons as static variables, due to the fact
     * that every IStatementParameter is instantiated upon creation,
     * in opposition to IStatements which are singletons (due to the
     * fact that they, unlike Parameters, store no additional data)
     */
    @Environment(EnvType.CLIENT)
    void registerIcons(int iconRegister);

    /**
     * Return the parameter description in the UI
     */
    String getDescription();

    void onClick(StatementContainer source, Statement stmt, ItemStack stack, StatementMouseClick mouse);

    void readNBT(NbtCompound nbt);

    void writeNBT(NbtCompound nbt);

    /**
     * This returns the parameter after a left rotation. Used in particular in
     * blueprints orientation.
     */
    StatementParameter rotateLeft();
}
