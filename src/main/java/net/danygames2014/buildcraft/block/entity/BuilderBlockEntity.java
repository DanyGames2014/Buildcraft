package net.danygames2014.buildcraft.block.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.BuilderBlock;
import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.danygames2014.buildcraft.item.BlueprintData;
import net.danygames2014.buildcraft.item.BlueprintData.BlueprintEntry;
import net.danygames2014.buildcraft.item.BlueprintPersistentState;
import net.danygames2014.buildcraft.item.BuilderBlueprintItem;
import net.danygames2014.buildcraft.item.BuilderTemplateItem;
import net.danygames2014.nyalib.particle.ParticleHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeavesBlockItem;
import net.minecraft.item.SecondaryBlockItem;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class BuilderBlockEntity extends AreaWorkerBlockEntity implements Inventory {
    public SimpleInventory inventory = new SimpleInventory(28, "Builder", this::markDirty);
    public BlueprintData blueprint = null;
    
    // Status
    public ObjectArrayList<BlueprintEntry> remainingEntries = new ObjectArrayList<>();
    public Int2ObjectOpenHashMap<NeededBlockEntry> neededBlockEntries = new Int2ObjectOpenHashMap<>();
    public BuilderState state = BuilderState.IDLE;
    public int cooldown = 0;
    public int remainingBlocks = 0;

    // Settings
    public boolean rotationEnabled = false;
    public boolean clearAreaBeforeBuild = false;
    public boolean pauseOnMissingBlock = true;
    public boolean pauseOnBlockedBlock = true;
    
    public BuilderBlockEntity() {
    }

    public void stopConstruction() {
        state = BuilderState.STOPPED;
        destroyWorkingArea();
        blueprint = null;
        remainingEntries.clear();
        neededBlockEntries.clear();
    }
    
    public void startConstruction() {
        if (state == BuilderState.READY) {
            state = BuilderState.BUILDING;
        }
    }
    
    public void pauseConstruction() {
        if (state == BuilderState.BUILDING) {
            state = BuilderState.READY;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (world.isRemote) {
            return;
        }

        remainingBlocks = remainingEntries.size();
        
        if (hasBlueprint()) {
            if (state == BuilderState.STOPPED) {
                return;
            }
            
            if (blueprint == null) {
                blueprint = BlueprintPersistentState.get(world, inventory.getStack(0).getDamage()).data;
                remainingEntries.addAll(blueprint.entries);
                calculateNeededBlocks();
            }
            
            if (workingArea == null) {
                Direction blueprintFacing = blueprint.facing;
                Direction builderFacing = null;

                BlockState builderState = world.getBlockState(x,y,z);
                if (builderState.getBlock() instanceof BuilderBlock block) {
                    builderFacing = builderState.get(Properties.HORIZONTAL_FACING);
                }
                
                if (builderFacing == null) {
                    return;
                }
                
                int minX = x;
                int minY = y;
                int minZ = z;
                int maxX = x;
                int maxY = y + (blueprint.sizeY - 1);
                int maxZ = z;
                
                if (rotationEnabled) {
                    return; // NYI
                } else {
                    switch (builderFacing.getOpposite()) {
                        case EAST -> {
                            minX = x;
                            minZ = z - blueprint.sizeZ;
                            maxX = x + (blueprint.sizeX - 1);
                            maxZ = z - 1;
                        }
                        
                        case WEST -> {
                            minX = x - (blueprint.sizeX - 1);
                            minZ = z + 1;
                            maxX = x;
                            maxZ = z + blueprint.sizeZ;
                        }
                        
                        case NORTH -> {
                            minX = x - blueprint.sizeX;
                            minZ = z - (blueprint.sizeZ - 1);
                            maxX = x - 1;
                            maxZ = z;
                        }
                        
                        case SOUTH -> {
                            minX = x + 1;
                            minZ = z;
                            maxX = x + blueprint.sizeX;
                            maxZ = z + (blueprint.sizeZ - 1);
                        }
                    }
                }
                
                constructWorkingArea(minX, minY, minZ, maxX, maxY, maxZ);
            }

            if (state == BuilderState.IDLE && blueprint != null && workingArea != null) {
                state = BuilderState.READY;
                return;
            }

            tickConstruction();
        } else {
            stopConstruction();
            state = BuilderState.IDLE;
        }
    }
    
    public void tickConstruction() {
        if (state != BuilderState.BUILDING) {
            return;
        }
        
        if (blueprint == null || workingArea == null) {
            return;
        }
        
        if (remainingEntries.isEmpty()) {
            state = BuilderState.STOPPED;
            destroyWorkingArea();
            return;
        }
        
        if (cooldown <= 0) {
            PlaceResult result = placeEntry(remainingEntries.get(0));
            
            switch (result) {
                case SUCCESS -> {
                    remainingEntries.remove(0);
                    cooldown = 10;
                }
                case ERROR -> {
                    remainingEntries.remove(0);
                    cooldown = 10;
                }
                case ALREADY_EXISTS -> {
                    remainingEntries.remove(0);
                }
                case BLOCKED -> {
                    markBlockedBlock(workingArea.minX + remainingEntries.get(0).x, workingArea.minY + remainingEntries.get(0).y, workingArea.minZ + remainingEntries.get(0).z);
                    cooldown = 10;
                }
                case MISSING_RESOURCE -> {
                    markBlockedBlock(workingArea.minX + remainingEntries.get(0).x, workingArea.minY + remainingEntries.get(0).y, workingArea.minZ + remainingEntries.get(0).z);
                    cooldown = 20;
                }
            }

            return;
        } 
        
        cooldown--;
    }
    
    public PlaceResult placeEntry(BlueprintEntry entry) {
        BlockState currentState = world.getBlockState(workingArea.minX + entry.x, workingArea.minY + entry.y, workingArea.minZ + entry.z);
        BlockState targetState = null;

        // Try to construct the state from the entry
        Block targetBlock = BlockRegistry.INSTANCE.get(Identifier.of(entry.id));
        if (targetBlock != null) {
            targetState = targetBlock.getDefaultState();
            // TODO: Properties
        }
        
        if (targetState == null) {
            return PlaceResult.ERROR;
        }
        
        if (currentState.isOf(targetState.getBlock())) {
            removeNeededBlock(targetBlock, entry.meta);
            return PlaceResult.ALREADY_EXISTS;
        }
        
        if (!currentState.isAir()) {
            return PlaceResult.BLOCKED;
        }
        
        if (findAndConsume(targetState, entry.meta)) {
            world.setBlockStateWithMetadataWithNotify(workingArea.minX + entry.x, workingArea.minY + entry.y, workingArea.minZ + entry.z, targetState, entry.meta);
            removeNeededBlock(targetBlock, entry.meta);
        } else {
            return PlaceResult.MISSING_RESOURCE;
        }
        
        return PlaceResult.SUCCESS;
    }
    
    public boolean findAndConsume(BlockState state, int meta) {
        Block block = state.getBlock();
        
        for (int slot = 1; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            
            if (stack == null) {
                continue;
            }

            if (stack.getItem() instanceof LeavesBlockItem blockItem) {
                if (blockItem.getBlock() == block && (blockItem.getPlacementMetadata(stack.getDamage()) - 8) == meta) {
                    if (inventory.removeStack(slot, 1).count >= 1) {
                        return true;
                    }
                }
            }
            
            if (stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() == block && blockItem.getPlacementMetadata(stack.getDamage()) == meta) {
                    if (inventory.removeStack(slot, 1).count >= 1) {
                        return true;
                    }
                } 
            }
            
            if (stack.getItem() instanceof SecondaryBlockItem blockItem) {
                if (blockItem.id == block.id && blockItem.getPlacementMetadata(stack.getDamage()) == meta) {
                    if (inventory.removeStack(slot, 1).count >= 1) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public void calculateNeededBlocks() {
        neededBlockEntries.clear();
        for (BlueprintEntry entry : blueprint.entries) {
            Block block = BlockRegistry.INSTANCE.get(Identifier.of(entry.id));
            if (block != null) {
                addNeededBlock(block, entry.meta);
            }
        }
    }
    
    public void addNeededBlock(Block block, int meta) {
        neededBlockEntries.putIfAbsent(block.id << 4 | meta & 0b1111, new NeededBlockEntry(block, meta));
        neededBlockEntries.get(block.id << 4 | meta & 0b1111).count++;
    }
    
    public void removeNeededBlock(Block block, int meta) {
        NeededBlockEntry entry = neededBlockEntries.get(block.id << 4 | meta & 0b1111);
        entry.count--;
        
        if (entry.count <= 0) {
            neededBlockEntries.remove(block.id << 4 | meta & 0b1111);
        }
    }
    
    public void markBlockedBlock(int x, int y, int z) {
        double density = 0.05D;
        
        for (double xPos = x; xPos <= x + 1; xPos += density) {
            ParticleHelper.addParticle(world, "reddust", xPos, y, z, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double zPos = z; zPos <= z + 1; zPos += density) {
            ParticleHelper.addParticle(world, "reddust", x, y, zPos, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double yPos = y; yPos <= y + 1; yPos += density) {
            ParticleHelper.addParticle(world, "reddust", x, yPos, z, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double xPos = x; xPos <= x + 1; xPos += density) {
            ParticleHelper.addParticle(world, "reddust", xPos, y, z + 1, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double zPos = z; zPos <= z + 1; zPos += density) {
            ParticleHelper.addParticle(world, "reddust", x + 1, y, zPos, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double yPos = y; yPos <= y + 1; yPos += density) {
            ParticleHelper.addParticle(world, "reddust", x + 1, yPos, z, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double xPos = x; xPos <= x + 1; xPos += density) {
            ParticleHelper.addParticle(world, "reddust", xPos, y + 1, z, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double zPos = z; zPos <= z + 1; zPos += density) {
            ParticleHelper.addParticle(world, "reddust", x, y + 1, zPos, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double yPos = y; yPos <= y + 1; yPos += density) {
            ParticleHelper.addParticle(world, "reddust", x, yPos, z + 1, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double xPos = x; xPos <= x + 1; xPos += density) {
            ParticleHelper.addParticle(world, "reddust", xPos, y + 1, z + 1, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double zPos = z; zPos <= z + 1; zPos += density) {
            ParticleHelper.addParticle(world, "reddust", x + 1, y + 1, zPos, 1.0D, 0.0D, 0.0D, 16);
        }

        for (double yPos = y; yPos <= y + 1; yPos += density) {
            ParticleHelper.addParticle(world, "reddust", x + 1, yPos, z + 1, 1.0D, 0.0D, 0.0D, 16);
        }
    }
    
    public boolean hasBlueprint() {
        ItemStack inputStack = inventory.getStack(0);
        
        if (inputStack == null) {
            return false;
        }
        
        if (inputStack.getDamage() == 0) {
            return false;
        }
        
        if (inputStack.getItem() instanceof BuilderBlueprintItem || inputStack.getItem() instanceof BuilderTemplateItem) {
            return true;
        }
        
        return false;
    }

    // Inventory
    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return inventory.removeStack(slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.setStack(slot, stack);
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public int getMaxCountPerStack() {
        return inventory.getMaxCountPerStack();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
    
    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
    }
    
    public enum BuilderState {
        IDLE,
        READY,
        BUILDING,
        STOPPED
    }
    
    public enum PlaceResult {
        SUCCESS,
        ERROR,
        ALREADY_EXISTS,
        BLOCKED,
        MISSING_RESOURCE
    }
    
    public class NeededBlockEntry {
        public Block block;
        public int meta;
        public int count;

        public NeededBlockEntry(Block block, int meta) {
            this.block = block;
            this.meta = meta;
            this.count = 0;
        }
    }
}
