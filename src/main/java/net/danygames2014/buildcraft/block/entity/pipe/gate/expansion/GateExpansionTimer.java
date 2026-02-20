package net.danygames2014.buildcraft.block.entity.pipe.gate.expansion;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.gate.Gate;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansionController;
import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.statements.TriggerClockTimer;
import net.minecraft.block.entity.BlockEntity;

import java.util.List;

public class GateExpansionTimer extends GateExpansionBuildcraft implements GateExpansion {
    public static GateExpansionTimer INSTANCE = new GateExpansionTimer();

    private GateExpansionTimer() {
        super(Buildcraft.NAMESPACE.id("timer"));
    }

    @Override
    public GateExpansionController makeController(PipeBlockEntity pipe) {
        return new GateExpansionControllerTimer(pipe);
    }

    private class GateExpansionControllerTimer extends GateExpansionController {
        private static class Timer {
            private static final int ACTIVE_TIME = 5;
            private final TriggerClockTimer.Time time;
            private int clock;

            public Timer(TriggerClockTimer.Time time) {
                this.time = time;
            }

            public void tick() {
                if (clock > -ACTIVE_TIME) {
                    clock--;
                } else {
                    clock = time.delay * 20 + ACTIVE_TIME;
                }
            }

            public boolean isActive() {
                return clock < 0;
            }
        }

        private final Timer[] timers = new Timer[TriggerClockTimer.Time.VALUES.length];

        public GateExpansionControllerTimer(BlockEntity pipe) {
            super(GateExpansionTimer.this, pipe);
            for (TriggerClockTimer.Time time : TriggerClockTimer.Time.VALUES) {
                timers[time.ordinal()] = new Timer(time);
            }
        }

        @Override
        public boolean isTriggerActive(Statement trigger, StatementParameter[] parameters) {
            if (trigger instanceof TriggerClockTimer timerTrigger) {
                return timers[timerTrigger.time.ordinal()].isActive();
            }
            return super.isTriggerActive(trigger, parameters);
        }

        @Override
        public void addTriggers(List<TriggerInternal> list) {
            super.addTriggers(list);
            for (TriggerClockTimer.Time time : TriggerClockTimer.Time.VALUES) {
                list.add(Buildcraft.triggerTimer[time.ordinal()]);
            }
        }

        @Override
        public void tick(Gate gate) {
            for (Timer timer : timers) {
                timer.tick();
            }
        }
    }
}
