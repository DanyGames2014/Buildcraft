package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum PipeWire {
    RED(Buildcraft.redPipeWire),
    BLUE(Buildcraft.bluePipeWire),
    GREEN(Buildcraft.greenPipeWire),
    YELLOW(Buildcraft.yellowPipeWire);

    private final Item item;

    PipeWire(Item item){
        this.item = item;
    }

    public static PipeWire fromOrdinal(int ordinal){
        if(ordinal < 0 || ordinal >= values().length){
            return RED;
        } else {
            return values()[ordinal];
        }
    }

    public ItemStack getStack() {
        return new ItemStack(item);
    }

    public Item getItem() {
        return item;
    }

    public static PipeWire fromItem(Item item) {
        for (PipeWire wire : values()) {
            if (wire.item == item) {
                return wire;
            }
        }
        return null;
    }
}
