package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.api.energy.IPowerEmitter;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public class EnergyPipeTransporter extends PipeTransporter {
    public EnergyPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public PipeType getType() {
        return PipeType.ENERGY;
    }

    @Override
    public boolean canConnectTo(BlockEntity other, Direction side) {
        if (other instanceof IPowerEmitter powerEmitter) {
            return powerEmitter.canEmitPowerFrom(side.getOpposite());
        }

        if (other instanceof IPowerReceptor powerReceptor) {
            return powerReceptor.getPowerReceiver(side.getOpposite()) != null;
        }
        
        return false;
    }
}
