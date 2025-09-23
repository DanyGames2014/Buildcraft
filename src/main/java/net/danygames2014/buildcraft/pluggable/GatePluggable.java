package net.danygames2014.buildcraft.pluggable;

import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Set;

public class GatePluggable extends PipePluggable {

    public GateMaterial material;
    public GateLogic logic;
    public GateExpansion[] expansions;
    public boolean isLit, isPulsing;

    public Gate realGate, instantiatedGate;
    private float pulseStage;

    public GatePluggable(){

    }

    public GatePluggable(Gate gate){
        instantiatedGate = gate;
        initFromGate(gate);
    }

    private void initFromGate(Gate gate){
        this.material = gate.material;
        this.logic = gate.logic;

        Set<GateExpansion> gateExpansions = gate.expansions.keySet();
        this.expansions = gateExpansions.toArray(new GateExpansion[gateExpansions.size()]);
    }

    @Override
    public ItemStack[] getDropItems(PipeBlockEntity pipe) {
        return new ItemStack[0];
    }

    @Override
    public Box getBoundingBox(Direction side) {
        float min = PipeWorldRenderer.PIPE_MIN_POS + 0.05F;
        float max = PipeWorldRenderer.PIPE_MAX_POS - 0.05F;

        float[][] bounds = new float[3][2];
        // X START - END
        bounds[0][0] = min;
        bounds[0][1] = max;
        // Y START - END
        bounds[1][0] = PipeWorldRenderer.PIPE_MIN_POS - 0.10F;
        bounds[1][1] = PipeWorldRenderer.PIPE_MIN_POS;
        // Z START - END
        bounds[2][0] = min;
        bounds[2][1] = max;

        MatrixTransformation.transform(bounds, side);
        return Box.createCached(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
    }

    @Override
    public PipePluggableRenderer getRenderer() {
        return null;
    }

    @Override
    public boolean requiresRenderUpdate(PipePluggable old) {
        return false;
    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }

    @Override
    public void writeNbt(NbtCompound nbt) {

    }

    @Override
    public void update(PipeBlockEntity pipe, Direction direction) {
        if (isPulsing || pulseStage > 0.11F) {
            // if it is moving, or is still in a moved state, then complete
            // the current movement
            pulseStage = (pulseStage + 0.01F) % 1F;
        } else {
            pulseStage = 0;
        }
    }

    @Override
    public void onAttachedToPipe(PipeBlockEntity pipe, Direction direction) {
        if (!pipe.world.isRemote) {
            if (instantiatedGate != null) {
                pipe.gates[direction.ordinal()] = instantiatedGate;
            } else {
                Gate gate = pipe.gates[direction.ordinal()];
                if (gate == null || gate.material != material || gate.logic != logic) {
                    pipe.gates[direction.ordinal()] = new Gate(pipe, material, logic, direction);
                    for (GateExpansion expansion : expansions) {
                        pipe.gates[direction.ordinal()].addGateExpansion(expansion);
                    }
                    pipe.scheduleRenderUpdate();
                }
            }

            realGate = pipe.gates[direction.ordinal()];
        }
    }

    @Override
    public void onDetachedFromPipe(PipeBlockEntity pipe, Direction direction) {
        if (!pipe.world.isRemote) {
            Gate gate = pipe.gates[direction.ordinal()];
            if (gate != null) {
                gate.resetGate();
                pipe.gates[direction.ordinal()] = null;
            }
            pipe.scheduleRenderUpdate();
        }
    }

    @Override
    public boolean isBlocking(PipeBlockEntity pipe, Direction direction) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GatePluggable)) {
            return false;
        }
        GatePluggable o = (GatePluggable) obj;
        if (o.material.ordinal() != material.ordinal()) {
            return false;
        }
        if (o.logic.ordinal() != logic.ordinal()) {
            return false;
        }
        if (o.expansions.length != expansions.length) {
            return false;
        }
        for (int i = 0; i < expansions.length; i++) {
            if (o.expansions[i] != expansions[i]) {
                return false;
            }
        }
        return true;
    }

    public float getPulseStage() {
        return pulseStage;
    }

    public GateMaterial getMaterial() {
        return material;
    }

    public GateLogic getLogic() {
        return logic;
    }

    public GateExpansion[] getExpansions() {
        return expansions;
    }
}
