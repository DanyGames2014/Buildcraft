package net.danygames2014.buildcraft.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemUtil {
    public static void dropTryIntoPlayerInventory(World world, int x, int y, int z, ItemStack stack, PlayerEntity player) {
        if (player != null) {
            player.inventory.addStack(stack);
        }
        dropItems(world, stack, x, y, z);
    }

    public static void dropItems(World world, Inventory inv, int x, int y, int z) {
        for (int slot = 0; slot < inv.size(); ++slot) {
            ItemStack items = inv.getStack(slot);

            if (items != null && items.count > 0) {
                dropItems(world, inv.getStack(slot).copy(), x, y, z);
            }
        }
    }

    public static void dropItems(World world, ItemStack stack, int i, int j, int k) {
        if (stack == null || stack.count <= 0) {
            return;
        }

        float f1 = 0.7F;
        double d = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
        double d1 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
        double d2 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
        ItemEntity itemEntity = new ItemEntity(world, i + d, j + d1, k + d2, stack);
        itemEntity.pickupDelay = 10;

        world.spawnEntity(itemEntity);
    }
}
