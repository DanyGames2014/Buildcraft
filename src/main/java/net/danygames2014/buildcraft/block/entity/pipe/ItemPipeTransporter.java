package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;

public class ItemPipeTransporter extends PipeTransporter {
    public final PipeBlockEntity blockEntity;
    public final ArrayList<TravellingItemEntity> contents;
    public final World world;
    public final int x;
    public final int y;
    public final int z;

    public ItemPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
        this.blockEntity = blockEntity;
        this.world = blockEntity.world;
        this.x = blockEntity.x;
        this.y = blockEntity.y;
        this.z = blockEntity.z;
        this.contents = new ArrayList<>();
    }

    @Override
    public PipeType getType() {
        return PipeType.ITEM;
    }

    // Connecting Logic
    @Override
    public boolean canConnectTo(BlockEntity other, Direction side) {
        ItemHandlerBlockCapability cap = CapabilityHelper.getCapability(other.world, other.x, other.y, other.z, ItemHandlerBlockCapability.class);

        if (cap != null) {
            return cap.canConnectItem(side.getOpposite());
        }

        return false;
    }

    @Override
    public void tick() {
        // TODO: This will probably crash, make it use a synchronized collection
        for (TravellingItemEntity item : contents) {
            if (item == null || item.dead) {
                contents.remove(item);
                continue;
            }

            if (reachedOutOfBounds(item)) {
                ItemEntity itemEntity = new ItemEntity(world, x, y, z, item.stack);
                world.spawnEntity(itemEntity);
                contents.remove(item);
                continue;
            }
        }
    }

    // Adding Items
    public void addItem(ItemStack stack) {
        TravellingItemEntity itemEntity = new TravellingItemEntity(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
        addItem(itemEntity);
    }

    public void addItem(TravellingItemEntity itemEntity) {
        if (!contents.contains(itemEntity)) {
            contents.add(itemEntity);
            blockEntity.world.spawnEntity(itemEntity);
        }
    }

    // TODO: injectItem

    // Checking item position
    public boolean reachedMiddle(TravellingItemEntity itemEntity) {
        double middleTolerance = itemEntity.speed * 1.01F;
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
                '}';
    }
}
