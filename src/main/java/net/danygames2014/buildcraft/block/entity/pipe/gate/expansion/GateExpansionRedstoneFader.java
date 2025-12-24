package net.danygames2014.buildcraft.block.entity.pipe.gate.expansion;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansionController;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Arrays;
import java.util.List;

public class GateExpansionRedstoneFader extends GateExpansionBuildcraft implements GateExpansion {
    public static GateExpansionRedstoneFader INSTANCE = new GateExpansionRedstoneFader();

    private GateExpansionRedstoneFader() {
        super(Buildcraft.NAMESPACE.id("fader"));
    }

    @Override
    public GateExpansionController makeController(PipeBlockEntity pipe) {
        return new GateExpansionControllerRedstoneFader(pipe);
    }

    private class GateExpansionControllerRedstoneFader extends GateExpansionController {
        public GateExpansionControllerRedstoneFader(BlockEntity pipe) {
            super(GateExpansionRedstoneFader.this, pipe);
        }

        @Override
        public void addTriggers(List<TriggerInternal> list) {
            super.addTriggers(list);
            list.addAll(Arrays.asList(Buildcraft.triggerRedstoneLevel));
        }

        @Override
        public void addActions(List<ActionInternal> list) {
            super.addActions(list);
            list.addAll(Arrays.asList(Buildcraft.actionRedstoneLevel));
        }
    }
}
