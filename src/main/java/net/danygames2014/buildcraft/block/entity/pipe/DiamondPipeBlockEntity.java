package net.danygames2014.buildcraft.block.entity.pipe;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidBucket;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.math.Direction;

public class DiamondPipeBlockEntity extends PipeBlockEntity implements Inventory {
    public ItemStack[] filterInventory = new ItemStack[54];
    
    public Int2ObjectOpenHashMap<ObjectArrayList<Direction>> itemRoutingCache = new Int2ObjectOpenHashMap<>();
    public Object2ObjectOpenHashMap<Fluid, ObjectArrayList<Direction>> fluidRoutingCache = new Object2ObjectOpenHashMap<>();
    
    public boolean filterMeta = true;
    public boolean filterTags = false;
    
    public DiamondPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    public DiamondPipeBlockEntity() {
        
    }
    
    private boolean doesFilterMatch(ItemStack stack, FilterDirection direction) {
        boolean emptyFilter = true;
        
        for (int i = direction.startIndex; i < direction.endIndex; i++) {
            if (filterInventory[i] != null) {
                emptyFilter = false;
                ItemStack filterStack = filterInventory[i];
                
                // Filter based on Item ID and Meta
                if (filterMeta) {
                    if (filterStack.itemId == stack.itemId && filterStack.getDamage() == stack.getDamage()) {
                        return true;
                    }
                } else {
                    if (filterStack.itemId == stack.itemId) {
                        return true;
                    }
                }
                
                // Filter based on tags
                if (filterTags) {
                    // TODO: Filtering in diamond pipes based on tags
                }
            }
        }
        
        return emptyFilter;
    }

    private boolean doesFluidFilterMatch(Fluid fluid, FilterDirection direction) {
        boolean emptyFilter = true;

        for (int i = direction.startIndex; i < direction.endIndex; i++) {
            if (filterInventory[i] != null) {
                emptyFilter = false;
                ItemStack filterStack = filterInventory[i];
                
                if (filterStack.getItem() instanceof FluidBucket fluidBucket) {
                    if (fluidBucket.getFluid() == fluid) {
                        return true;
                    }
                }
                
                if (filterStack.getItem() instanceof BlockItem blockItem) {
                    if (FluidRegistry.get(blockItem.getBlock().id) == fluid) {
                        return true;
                    }
                }
            }
        }

        return emptyFilter;
    }

    public void filterChanged() {
        cleanRoutingCache();
    }
    
    private void cleanRoutingCache() {
        itemRoutingCache.clear();
        fluidRoutingCache.clear();
    }
    
    private ObjectArrayList<Direction> calculateItemRoute(ItemStack stack) {
        var directions = new ObjectArrayList<Direction>();
        
        for (FilterDirection direction : FilterDirection.values()) {
            if (doesFilterMatch(stack, direction)) {
                directions.add(direction.side);
            }
        }
        
        return directions;
    }

    public ObjectArrayList<Direction> getItemRoutes(ItemStack stack) {
        int routingId = (stack.itemId << 4) | (stack.getDamage() & 0b1111);

        return itemRoutingCache.computeIfAbsent(routingId, routeId -> calculateItemRoute(stack));
    }

    private ObjectArrayList<Direction> calculateFluidRoute(Fluid fluid) {
        var directions = new ObjectArrayList<Direction>();

        for (FilterDirection direction : FilterDirection.values()) {
            if (doesFluidFilterMatch(fluid, direction)) {
                directions.add(direction.side);
            }
        }

        return directions;
    }

    public ObjectArrayList<Direction> getFluidRoutes(Fluid fluid) {
        return fluidRoutingCache.computeIfAbsent(fluid, fl -> calculateFluidRoute(fluid));
    }
    
    // Inventory
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return super.canPlayerUse(player);
    }

    @Override
    public int size() {
        return 54;
    }

    @Override
    public ItemStack getStack(int slot) {
        return filterInventory[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        filterInventory[slot] = stack;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public String getName() {
        return "Filter";
    }
    
    public enum FilterDirection {
        BLACK(Direction.DOWN, 0, 8),
        WHITE(Direction.UP, 9, 17),
        RED(Direction.NORTH, 18, 26),
        BLUE(Direction.SOUTH, 27, 35),
        GREEN(Direction.EAST, 36, 44),
        YELLOW(Direction.WEST, 45, 53);

        public final Direction side;
        public final int startIndex;
        public final int endIndex;
        
        FilterDirection(Direction side, int startIndex, int endIndex) {
            this.side = side;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }
}
