package net.danygames2014.buildcraft.block.entity.pipe.transporter;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.block.entity.pipe.event.ItemPipeEvent;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Iterator;

public class ItemPipeTransporter extends PipeTransporter {
    public final ObjectOpenHashSet<TravellingItemEntity> contents;

    public ItemPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
        this.contents = new ObjectOpenHashSet<>();
    }

    @Override
    public PipeType getType() {
        return PipeType.ITEM;
    }

    // Pipe Logic
    @Override
    public void tick() {
        super.tick();
        
        if (world.isRemote) {
            return;
        }
        
        Iterator<TravellingItemEntity> iterator = contents.iterator();
        while (iterator.hasNext()) {
            TravellingItemEntity item = iterator.next();
            if (item == null || item.dead) {
                iterator.remove();
                continue;
            }

            if (reachedOutOfBounds(item)) {
                dropItem(item);
                iterator.remove();
                continue;
            }

            if (item.toMiddle && reachedMiddle(item)) {
                Direction dir = routeItem(item);
                if (dir == null) {
                    switch (blockEntity.behavior.getFailedPathingResult(blockEntity, this, item)) {
                        case DROP -> {
                            dropItem(item);
                            iterator.remove();
                        }
                        
                        case VOID -> {
                            iterator.remove();
                            item.markDead();
                        }
                        
                        case BOUNCE -> {
                            iterator.remove();
                            injectItem(item.stack, item.travelDirection);
                            item.markDead();
                        }
                    }
                } else {
                    item.travelDirection = dir;
                    item.toMiddle = false;
                    item.setPosition(x + 0.5D, y + 0.25D, z + 0.5D);
                }
                continue;
            }

            if (reachedEnd(item)) {

                BlockEntity neighbor = this.blockEntity.getBlockEntity(item.travelDirection);
                ItemPipeEvent.ReachedEnd event = new ItemPipeEvent.ReachedEnd(this.blockEntity, item, neighbor);
                this.blockEntity.eventBus.handleEvent(ItemPipeEvent.ReachedEnd.class, event);
                boolean handleItem = !event.handled;

                if(handleItem){
                    switch (handOffItem(item)) {
                        case DROP -> {
                            dropItem(item);
                            iterator.remove();
                        }

                        case REMOVE -> {
                            iterator.remove();
                        }

                        case BOUNCE -> {
                            iterator.remove();
                            injectItem(item.stack, item.travelDirection);
                            item.markDead();
                        }
                    }
                }
            }
        }
        
        blockEntity.behavior.transporterTick(blockEntity, this);
    }

    public void dropItem(TravellingItemEntity item) {
        ItemEntity itemEntity = new ItemEntity(world, item.x, item.y, item.z, item.stack);
        world.spawnEntity(itemEntity);
        item.markDead();
    }

    public HandOffResult handOffItem(TravellingItemEntity item) {
        Direction side = item.travelDirection;
        BlockEntity sideBlockEntity = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());
        
        if (sideBlockEntity instanceof PipeBlockEntity pipe) {
            if (pipe.transporter instanceof ItemPipeTransporter otherTransporter) {
                otherTransporter.receiveTravellingItem(item, side.getOpposite());
                return HandOffResult.REMOVE;
            }
        }

        ItemHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), ItemHandlerBlockCapability.class);
        if (cap != null) {
            if (cap.canInsertItem(side.getOpposite())) {
                ItemStack returnedStack = cap.insertItem(item.stack, side.getOpposite());
                if (returnedStack == null) {
                    item.markDead();
                    return HandOffResult.REMOVE;
                } else {
                    item.stack = returnedStack;
                    return blockEntity.behavior.getFailedInsertResult(blockEntity, this, item);
                }
            } else {
                return blockEntity.behavior.getFailedInsertResult(blockEntity, this, item);
            }
        }

        return HandOffResult.DROP;
    }

    public Direction routeItem(TravellingItemEntity item) {
        return blockEntity.behavior.routeItem(blockEntity, blockEntity.validOutputDirections, item);
    }
    
    @Override
    public void onBreak() {
        for (TravellingItemEntity item : contents) {
            dropItem(item);
        }
        contents.clear();
    }

    // Adding Items
    public void addItem(ItemStack stack) {
        TravellingItemEntity itemEntity = new TravellingItemEntity(world, x + 0.5D, y + 0.25D, z + 0.5D, stack);
        addItem(itemEntity);
        world.spawnEntity(itemEntity);
    }

    public void injectItem(ItemStack stack, Direction side) {
        TravellingItemEntity itemEntity = new TravellingItemEntity(world, x + 0.5D + (side.getOffsetX() * 0.5D), y + 0.25D + (side.getOffsetY() * 0.25D), z + 0.5D + (side.getOffsetZ() * 0.5D), stack);
        itemEntity.input = side;
        itemEntity.travelDirection = side.getOpposite();
        itemEntity.lastTravelDirection = itemEntity.travelDirection;

        ItemPipeEvent.Entered event = new ItemPipeEvent.Entered(this.blockEntity, itemEntity);
        this.blockEntity.eventBus.handleEvent(ItemPipeEvent.Entered.class, event);
        if(event.cancelled){
            return;
        }

        addItem(itemEntity);
        world.spawnEntity(itemEntity);
    }

    public void addItem(TravellingItemEntity itemEntity) {
        if (!contents.contains(itemEntity)) {
            contents.add(itemEntity);
        }
        itemEntity.transporter = this;
        itemEntity.toMiddle = true;
    }

    public void receiveTravellingItem(TravellingItemEntity item, Direction side) {
        item.input = side;
        item.transporter = this;
        item.toMiddle = true;

        ItemPipeEvent.Entered event = new ItemPipeEvent.Entered(this.blockEntity, item);
        this.blockEntity.eventBus.handleEvent(ItemPipeEvent.Entered.class, event);
        if(event.cancelled){
            return;
        }

        if (!contents.contains(item)) {
            contents.add(item);
        }
    }

    // Checking item position
    public boolean reachedMiddle(TravellingItemEntity itemEntity) {
        double middleTolerance = itemEntity.speed * 1.01D;

//        boolean xCheck = Math.abs(x + 0.5D - itemEntity.x) < middleTolerance;
//        boolean yCheck = Math.abs(y + 0.25D - itemEntity.y) < middleTolerance;
//        boolean zCheck = Math.abs(z + 0.5D - itemEntity.z) < middleTolerance;
//        System.out.println("---");
//        System.out.println(x + 0.5D + " " + itemEntity.x + " " + Math.abs(x + 0.5D - itemEntity.x) + " " + middleTolerance + " " + xCheck);
//        System.out.println(y + 0.25D + " " + itemEntity.y + " " + Math.abs(y + 0.25D - itemEntity.y) + " " + middleTolerance + " " + yCheck);
//        System.out.println(z + 0.5D + " " + itemEntity.z + " " + Math.abs(z + 0.5D - itemEntity.z) + " " + middleTolerance + " " + zCheck);

//        return xCheck && yCheck && zCheck;

        return (
                Math.abs(x + 0.5D - itemEntity.x) < middleTolerance &&
                        Math.abs(y + 0.25D - itemEntity.y) < middleTolerance &&
                        Math.abs(z + 0.5D - itemEntity.z) < middleTolerance
        );
    }

    public boolean reachedEnd(TravellingItemEntity itemEntity) {
        return itemEntity.x > x + 1 || itemEntity.x < x || itemEntity.y > y + 1 || itemEntity.y < y || itemEntity.z > z + 1 || itemEntity.z < z;
    }

    public boolean reachedOutOfBounds(TravellingItemEntity itemEntity) {
        return itemEntity.x > x + 2 || itemEntity.x < x - 1 || itemEntity.y > y + 2 || itemEntity.y < y - 1 || itemEntity.z > z + 2 || itemEntity.z < z - 1;
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

    // Debug
    @Override
    public String toString() {
        return "ItemPipeTransporter{" +
                "contents=" + contents +
                ", world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public enum HandOffResult {
        BOUNCE,
        DROP,
        REMOVE
    }
    
    public enum FailedPathingResult {
        DROP,
        VOID,
        BOUNCE
    }
}
