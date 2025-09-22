package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.danygames2014.buildcraft.api.transport.GateExpansion;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;

public abstract class GateExpansionController {
    public final GateExpansion type;
    public final PipeBlockEntity pipe;

    public GateExpansionController(GateExpansion type, PipeBlockEntity pipe) {
        this.type = type;
        this.pipe = pipe;
    }

    public GateExpansion getType(){
        return type;
    }

    public boolean isActive() {
        return false;
    }
}
