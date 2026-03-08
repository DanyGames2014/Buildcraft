package net.danygames2014.buildcraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Collections;
import java.util.List;

public class ItemUtil {
    private static final List<Direction> DIRECTIONS = new ObjectArrayList<>(Direction.values());
    
    public static void dropTryIntoPlayerInventory(World world, int x, int y, int z, ItemStack stack, PlayerEntity player) {
        if (player != null) {
            player.inventory.addStack(stack);
            player.inventory.markDirty();
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

    /**
     * Tries to add the passed stack to any valid inventories around the given
     * coordinates.
     *
     * @return The remainded of the added stack
     */
    public static ItemStack addToRandomInventory(ItemStack stack, World world, int x, int y, int z) {
        Collections.shuffle(DIRECTIONS);
        for (Direction side : DIRECTIONS) {
            var cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), ItemHandlerBlockCapability.class);
            
            if (cap != null) {
                stack = cap.insertItem(stack, side.getOpposite());
            }
            
            if (stack == null) {
                return null;
            }
        }

        return stack;
    }

    public static ItemStack addToRandomPipeEntry(BlockEntity source, ItemStack stack) {
        Collections.shuffle(DIRECTIONS);
        World world = source.world;
        int x = source.x;
        int y = source.y;
        int z = source.z;
        
        for (Direction side : DIRECTIONS) {
            BlockEntity blockEntity = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());
            
            if (blockEntity instanceof PipeBlockEntity pipe && pipe.transporter instanceof ItemPipeTransporter transporter) {
                if (pipe.connections.get(side.getOpposite()) != PipeConnectionType.NONE) {
                    transporter.injectItem(stack, side.getOpposite());
                    stack = null;
                }
                
                continue;
            }
            
            if (stack == null) {
                return null;
            }
        }

        return stack;
    }
}
