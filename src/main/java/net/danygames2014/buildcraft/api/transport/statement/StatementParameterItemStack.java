package net.danygames2014.buildcraft.api.transport.statement;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

public class StatementParameterItemStack implements StatementParameter {

    protected ItemStack stack;

    @Override
    public Identifier getIdentifier() {
        return Buildcraft.NAMESPACE.id("stack");
    }

    @Override
    public Atlas.Sprite getSprite() {
        return null;
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public void registerTextures() {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StatementParameterItemStack param) {
            return ItemStack.areEqual(stack, param.stack);
        } else {
            return false;
        }
    }

    // TODO: this is probably incorrect
    @Override
    public String getDescription() {
        if (stack != null) {
            return stack.getTranslationKey();
        } else {
            return "";
        }
    }

    @Override
    public void click(StatementContainer source, Statement stmt, ItemStack stack, StatementMouseClick mouse) {
        if (stack != null) {
            this.stack = stack.copy();
            this.stack.count = 1;
        } else {
            this.stack = null;
        }
    }

    @Override
    public void readNBT(NbtCompound nbt) {
        if (nbt.contains("stack")) {
            stack = new ItemStack(nbt.getCompound("stack"));
        }
    }

    @Override
    public void writeNBT(NbtCompound nbt) {
        if (stack != null) {
            NbtCompound stackNbt = new NbtCompound();
            stack.writeNbt(stackNbt);
            nbt.put("stack", stackNbt);
        }
    }

    @Override
    public StatementParameter rotateLeft() {
        return this;
    }
}
