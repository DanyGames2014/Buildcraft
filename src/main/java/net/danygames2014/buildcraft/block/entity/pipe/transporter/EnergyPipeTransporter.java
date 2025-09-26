package net.danygames2014.buildcraft.block.entity.pipe.transporter;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Iterator;

public class EnergyPipeTransporter extends PipeTransporter {
    public final ObjectOpenHashSet<TravellingEnergy> contents;
    /**
     * Index 0-5 is {@link Direction} ids. Index 6 is the center;
     */
    public int[] fillLevel = new int[7];
    
    public EnergyPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
        this.contents = new ObjectOpenHashSet<>();
    }

    @Override
    public PipeType getType() {
        return PipeType.ENERGY;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (world.isRemote) {
            return;
        }
        
        Iterator<TravellingEnergy> iterator = contents.iterator();
        while (iterator.hasNext()) {
            TravellingEnergy energy = iterator.next();
            
            if (energy == null || energy.invalid) {
                iterator.remove();
                continue;
            }
            
            energy.tick();
            
        }
    }
}
