package net.danygames2014.buildcraft.block.entity.pipe.event;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;

public abstract class PipeEvent {
    public final PipeBlockEntity pipe;

    public PipeEvent(PipeBlockEntity pipe){
        this.pipe = pipe;
    }
}
