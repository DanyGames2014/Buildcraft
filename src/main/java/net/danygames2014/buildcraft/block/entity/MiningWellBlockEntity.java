package net.danygames2014.buildcraft.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.MiningWellBlock;
import net.danygames2014.buildcraft.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.tag.TagKey;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiningWellBlockEntity extends BlockEntity implements IPowerReceptor {
    private static final Random RANDOM = new Random();
    private static final ObjectArrayList<Vec3i> SEARCH_OFFSETS = new ObjectArrayList<>();

    private final PickaxeItem pickaxe = (PickaxeItem) Item.IRON_PICKAXE;
    private final TagKey<Block> target = TagKey.of(BlockRegistry.INSTANCE.getKey(), Buildcraft.NAMESPACE.id("mining_well_target"));
    protected PowerHandler powerHandler;
    private final ObjectArrayList<Vec3i> queue = new ObjectArrayList<>();

    static {
        // Top and Bottom
        SEARCH_OFFSETS.add(new Vec3i(0, 1, 0));
        SEARCH_OFFSETS.add(new Vec3i(0, -1, 0));
        
        // Sides
        SEARCH_OFFSETS.add(new Vec3i(1, 0, 0));
        SEARCH_OFFSETS.add(new Vec3i(-1, 0, 0));
        SEARCH_OFFSETS.add(new Vec3i(0, 0, 1));
        SEARCH_OFFSETS.add(new Vec3i(0, 0, -1));

        // Diagonals
        SEARCH_OFFSETS.add(new Vec3i(1, 0, 1));
        SEARCH_OFFSETS.add(new Vec3i(1, 0, -1));
        SEARCH_OFFSETS.add(new Vec3i(-1, 0, 1));
        SEARCH_OFFSETS.add(new Vec3i(-1, 0, -1));
    }

    @Override
    public void tick() {
        super.tick();
        if (isActive()) {
            powerHandler.update();
        } else {
            if (world.getTime() % 4 == 0) {
                retract();
            }
        }
    }
    
    public boolean isActive() {
        BlockState state = world.getBlockState(x,y, z);
        
        if (state.isOf(Buildcraft.miningWell)) {
            return state.get(MiningWellBlock.ACTIVE);
        }
        
        return false;
    }
    
    public void setActive(boolean active) {
        BlockState state = world.getBlockState(x,y, z);

        if (state.isOf(Buildcraft.miningWell)) {
            world.setBlockStateWithNotify(x, y, z, state.with(MiningWellBlock.ACTIVE, active));
            
            if (active) {
                powerHandler.configure(2, Config.MACHINE_CONFIG.miningWellConfig.mjPerBlock * 2, Config.MACHINE_CONFIG.miningWellConfig.mjPerBlock, Config.MACHINE_CONFIG.miningWellConfig.mjPerBlock * 10);        
            } else {
                powerHandler.configure(0, 0, Integer.MAX_VALUE, Config.MACHINE_CONFIG.miningWellConfig.mjPerBlock * 10);
            }
        }
    }

    public MiningWellBlockEntity() {
        powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);

        powerHandler.configure(2, Config.MACHINE_CONFIG.miningWellConfig.mjPerBlock * 2, Config.MACHINE_CONFIG.miningWellConfig.mjPerBlock, Config.MACHINE_CONFIG.miningWellConfig.mjPerBlock * 10);
        powerHandler.configurePowerPerdition(1, 1);
    }

    @Override
    public PowerHandler.PowerReceiver getPowerReceiver(Direction side) {
        return powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider) {
        if (!isActive()) {
            return;
        }

        float mj = Config.MACHINE_CONFIG.miningWellConfig.mjPerBlock;

        // Check if enough energy is avalible
        if (powerHandler.useEnergy(mj, mj, false) < mj) {
            return;
        }

        // Mine the next block
        if (mine()) {
            powerHandler.useEnergy(mj, mj, true);
        }
    }

    public boolean mine() {
        if (queue.isEmpty()) {
            return advance();
        }

        Vec3i pos = queue.remove(0);
        return mineBlock(pos.x, pos.y, pos.z);
    }

    public boolean advance() {
        int currentY = y - 1;

        // Loop down the pipes until we get to the bottom of them
        while (world.getBlockState(x, currentY, z).isOf(Buildcraft.miningPipe)) {
            currentY--;
        }

        if (currentY == world.getBottomY() || world.getBlockState(x, currentY, z).isOf(Block.BEDROCK)) {
            finish();
            return true;
        }

        // Try to mine the next block
        if (mineBlock(x, currentY, z)) {
            world.setBlockStateWithNotify(x, currentY, z, Buildcraft.miningPipe.getDefaultState());
            findBlocks(x, currentY, z);
            return true;
        }

        return false;
    }

    public boolean mineBlock(int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);

        if (state.isAir()) {
            return true;
        }

        if (canHarvest(state.getBlock())) {
            int meta = world.getBlockMeta(x, y, z);

            List<ItemStack> drops = state.getBlock().getDropList(world, x, y, z, state, meta);

            if (drops == null) {
                drops = new ArrayList<>();
                int itemId = state.getBlock().getDroppedItemId(meta, RANDOM);
                int count = state.getBlock().getDroppedItemCount(RANDOM);
                if (count > 0) {
                    drops.add(new ItemStack(itemId, count, 0));
                }
            }

            for (var stack : drops) {
                ItemEntity itemEntity = new ItemEntity(world, this.x + 0.5D, this.y + 1.5D, this.z + 0.5D, stack);
                world.spawnEntity(itemEntity);
            }

            world.setBlockStateWithNotify(x, y, z, States.AIR.get());
            return true;
        }

        return false;
    }

    public boolean canHarvest(Block block) {
        if (block.material.isHandHarvestable()) {
            return true;
        } else {
            return pickaxe.isSuitableFor(block);
        }
    }

    public void findBlocks(int x, int y, int z) {
        for (Vec3i side : SEARCH_OFFSETS) {
            Vec3i pos = new Vec3i(x + side.x, y + side.y, z + side.z);
            if (world.getBlockState(pos.x, pos.y, pos.z).isIn(target) && !queue.contains(pos)) {
                queue.addAll(walk(pos));
            }
        }
    }

    public ArrayList<Vec3i> walk(Vec3i start) {
        // ArrayList for list of blocks yet to explore
        ArrayList<Vec3i> open = new ArrayList<>();
        // ArrayList for list of blocks that have been found
        ArrayList<Vec3i> closed = new ArrayList<>();

        // Add the starting position to explore
        open.add(start);

        // Go until open isnt empty
        while (!open.isEmpty()) {
            // Get the position to explore
            Vec3i pos = open.get(0);
            // Look at all of its sides
            for (Vec3i dir : SEARCH_OFFSETS) {
                // Get the side and see if there is a block on it. Then check if it doesnt already exist
                Vec3i side = new Vec3i(pos.x + dir.x, pos.y + dir.y, pos.z + dir.z);
                if (!closed.contains(side)) {
                    if (world.getBlockState(side.x, side.y, side.z).isIn(target)) {
                        open.add(side);
                    }
                }
            }

            // Add the position to closed and remove it from open
            closed.add(pos);
            open.remove(pos);
        }

        return closed;
    }

    public void finish() {
        setActive(false);
        retract();
    }

    public void retract() {
        int currentY = y - 1;

        // Loop down the pipes until we get to the bottom of them
        while (world.getBlockState(x, currentY, z).isOf(Buildcraft.miningPipe)) {
            if (!world.getBlockState(x, currentY - 1, z).isOf(Buildcraft.miningPipe)) {
                world.setBlockStateWithNotify(x, currentY, z, States.AIR.get());
                return;
            }
            currentY--;
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }
}
