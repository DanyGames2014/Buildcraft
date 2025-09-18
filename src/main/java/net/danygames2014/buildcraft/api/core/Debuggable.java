package net.danygames2014.buildcraft.api.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface Debuggable {
    void debug(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side);
}
