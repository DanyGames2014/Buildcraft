package net.danygames2014.buildcraft.block.entity.pipe.gate.expansion;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.api.transport.gate.Gate;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansionController;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.statements.ActionEnergyPulsar;
import net.danygames2014.buildcraft.statements.ActionSingleEnergyPulse;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.List;

public class GateExpansionPulsar extends GateExpansionBuildcraft implements GateExpansion {
    public static GateExpansionPulsar INSTANCE = new GateExpansionPulsar();

    private GateExpansionPulsar() {
        super(Buildcraft.NAMESPACE.id("pulsar"));
    }

    @Override
    public GateExpansionController makeController(PipeBlockEntity pipe) {
        return new GateExpansionControllerPulsar(pipe);
    }

    private class GateExpansionControllerPulsar extends GateExpansionController {

        private static final int PULSE_PERIOD = 10;
        private boolean isActive;
        private boolean singlePulse;
        private boolean hasPulsed;
        private int tick;
        private int count;

        public GateExpansionControllerPulsar(BlockEntity pipe) {
            super(GateExpansionPulsar.this, pipe);

            // by default, initialize tick so that not all gates created at
            // one single moment would do the work at the same time. This
            // spreads a bit work load. Note, this is not a problem for
            // existing gates since tick is stored in NBT
            tick = (int) (Math.random() * PULSE_PERIOD);
        }

        @Override
        public void startResolution() {
            if (isActive()) {
                disablePulse();
            }
        }

        @Override
        public boolean resolveAction(Statement action, int count) {
            if (action instanceof ActionEnergyPulsar) {
                enablePulse(count);
                return true;
            } else if (action instanceof ActionSingleEnergyPulse) {
                enableSinglePulse(count);
                return true;
            }
            return false;
        }

        @Override
        public void addActions(List<ActionInternal> list) {
            super.addActions(list);
            list.add(Buildcraft.actionEnergyPulser);
            list.add(Buildcraft.actionSingleEnergyPulse);
        }

        @Override
        public void tick(Gate gate) {
            if (!isActive && hasPulsed)
                hasPulsed = false;

            if (tick++ % PULSE_PERIOD != 0) {
                // only do the treatement once every period
                return;
            }

            if (!isActive) {
                gate.setPulsing(false);
                return;
            }

            PowerHandler.PowerReceiver powerReceptor = pipe instanceof IPowerReceptor ? ((IPowerReceptor) pipe).getPowerReceiver(null) : null;

            if (powerReceptor != null && (!singlePulse || !hasPulsed)) {
                gate.setPulsing(true);
                powerReceptor.receiveEnergy(PowerHandler.Type.GATE, Math.min(1 << (count - 1), 64) * 1.01f, Direction.WEST);
                hasPulsed = true;
            } else {
                gate.setPulsing(true);
            }
        }

        private void enableSinglePulse(int count) {
            singlePulse = true;
            isActive = true;
            this.count = count;
        }

        private void enablePulse(int count) {
            isActive = true;
            singlePulse = false;
            this.count = count;
        }

        private void disablePulse() {
            if (!isActive) {
                hasPulsed = false;
            }
            isActive = false;
            this.count = 0;
        }

        @Override
        public boolean isActive() {
            return isActive;
        }

        @Override
        public void writeNBT(NbtCompound nbt) {
            nbt.putBoolean("singlePulse", singlePulse);
            nbt.putBoolean("isActive", isActive);
            nbt.putBoolean("hasPulsed", hasPulsed);
            nbt.putByte("pulseCount", (byte) count);
            nbt.putInt("tick", tick);
        }

        @Override
        public void readNBT(NbtCompound nbt) {
            isActive = nbt.getBoolean("isActive");
            singlePulse = nbt.getBoolean("singlePulse");
            hasPulsed = nbt.getBoolean("hasPulsed");
            count = nbt.getByte("pulseCount");
            tick = nbt.getInt("tick");
        }
    }
}
