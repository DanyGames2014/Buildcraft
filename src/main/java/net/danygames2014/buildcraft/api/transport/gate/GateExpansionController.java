package net.danygames2014.buildcraft.api.transport.gate;

import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public abstract class GateExpansionController {

    public final GateExpansion type;
    public final BlockEntity pipe;

    public GateExpansionController(GateExpansion type, BlockEntity pipe) {
        this.type = type;
        this.pipe = pipe;
    }

    public GateExpansion getType() {
        return type;
    }

    public boolean isActive() {
        return false;
    }

    public void tick(Gate gate) {
    }

    public void startResolution() {
    }

    public boolean resolveAction(Statement action, int count) {
        return false;
    }

    public boolean isTriggerActive(Statement trigger, StatementParameter[] parameters) {
        return false;
    }

    public void addTriggers(List<TriggerInternal> list) {
    }

    public void addActions(List<ActionInternal> list) {
    }

    public void writeNBT(NbtCompound nbt) {
    }

    public void readNBT(NbtCompound nbt) {
    }
}
