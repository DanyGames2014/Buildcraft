package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public enum PipeWire {
    RED(() -> Buildcraft.redPipeWire),
    BLUE(() -> Buildcraft.bluePipeWire),
    GREEN(() -> Buildcraft.greenPipeWire),
    YELLOW(() -> Buildcraft.yellowPipeWire);

    private final Supplier<Item> itemSupplier;

    PipeWire(Supplier<Item> item){
        this.itemSupplier = item;
    }

    public static PipeWire fromOrdinal(int ordinal){
        if(ordinal < 0 || ordinal >= values().length){
            return RED;
        } else {
            return values()[ordinal];
        }
    }

    public ItemStack getStack() {
        return new ItemStack(itemSupplier.get());
    }

    public Item getItem() {
        return itemSupplier.get();
    }

    public static PipeWire fromItem(Item item) {
        for (PipeWire wire : values()) {
            if (wire.itemSupplier.get().equals(item)) {
                return wire;
            }
        }
        return null;
    }
}
