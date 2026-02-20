package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.api.transport.statement.ActionExternal;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.ActionProvider;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Collection;
import java.util.LinkedList;

public class PipeActionProvider implements ActionProvider {
    @Override
    public Collection<ActionInternal> getInternalActions(StatementContainer container) {
        LinkedList<ActionInternal> result = new LinkedList<>();
        PipeBlockEntity pipe = null;
        if (container instanceof net.danygames2014.buildcraft.api.transport.gate.Gate) {
            pipe = ((net.danygames2014.buildcraft.api.transport.gate.Gate) container).getPipe();

            if (container instanceof Gate) {
                ((Gate) container).addActions(result);
            }
        }

        //noinspection IfStatementWithIdenticalBranches
        if (pipe == null) {
            return result;
        }

//        result.addAll(pipe.getActions());

        return result;
    }

    @Override
    public Collection<ActionExternal> getExternalActions(Direction side, BlockEntity blockEntity) {
        return null;
    }
}
