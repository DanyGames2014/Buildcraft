package net.danygames2014.buildcraft.block.entity.pipe.event;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.List;

public class ItemPipeEvent extends PipeEvent{
    public final TravellingItemEntity item;
    public ItemPipeEvent(PipeBlockEntity pipe, TravellingItemEntity item) {
        super(pipe);
        this.item = item;
    }

    public static class Entered extends ItemPipeEvent {
        public boolean cancelled = false;

        public Entered(PipeBlockEntity pipe, TravellingItemEntity item) {
            super(pipe, item);
        }
    }

    public static class ReachedCenter extends ItemPipeEvent {
        public ReachedCenter(PipeBlockEntity pipe, TravellingItemEntity item) {
            super(pipe, item);
        }
    }

    public static class ReachedEnd extends ItemPipeEvent {
        public final BlockEntity dest;
        public boolean handled = false;

        public ReachedEnd(PipeBlockEntity pipe, TravellingItemEntity item, BlockEntity dest) {
            super(pipe, item);
            this.dest = dest;
        }
    }

    public static class DropItem extends ItemPipeEvent {
        public ItemEntity entity;
        public Direction direction;

        public DropItem(PipeBlockEntity pipe, TravellingItemEntity item, ItemEntity entity) {
            super(pipe, item);
            this.entity = entity;
            // item.travelDirection is probably wrong here
            this.direction = item.travelDirection != null ? item.travelDirection : item.input;
        }
    }

    public static class FindDest extends ItemPipeEvent {
        public final List<Direction> destinations;
        public boolean shuffle = true;

        public FindDest(PipeBlockEntity pipe, TravellingItemEntity item, List<Direction> destinations) {
            super(pipe, item);
            this.destinations = destinations;
        }
    }

    public static class AdjustSpeed extends ItemPipeEvent {
        public boolean handled = false;

        public AdjustSpeed(PipeBlockEntity pipe, TravellingItemEntity item) {
            super(pipe, item);
        }
    }
}
