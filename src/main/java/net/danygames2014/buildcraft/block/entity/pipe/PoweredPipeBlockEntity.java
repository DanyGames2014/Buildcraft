package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PoweredPipeBlockEntity extends PipeBlockEntity implements IPowerReceptor {
    protected PowerHandler powerHandler;
    
    public PoweredPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
        configurePower();
    }

    public PoweredPipeBlockEntity() {
        super();
        configurePower();
    }
    
    public void configurePower() {
        powerHandler = new PowerHandler(this, PowerHandler.Type.PIPE);
        powerHandler.configure(1.0D, 64.0D, 1.0D, 64.0D);
        powerHandler.configurePowerPerdition(0,0);
    }

    @Override
    public PowerHandler.PowerReceiver getPowerReceiver(Direction side) {
        return powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider) {

    }

    @Override
    public World getWorld() {
        return world;
    }
}
