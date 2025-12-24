package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.ActionExternal;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.ActionProvider;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.container.RedstoneStatementContainer;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DefaultActionProvider implements ActionProvider {
    @Override
    public Collection<ActionInternal> getInternalActions(StatementContainer container) {
        LinkedList<ActionInternal> res = new LinkedList<>();

        if (container instanceof RedstoneStatementContainer) {
            res.add(Buildcraft.actionRedstone);
        }

        return res;
    }

    @Override
    public Collection<ActionExternal> getExternalActions(Direction side, BlockEntity blockEntity) {
        LinkedList<ActionExternal> res = new LinkedList<>();

//        try {
//            if (tile instanceof IControllable) {
//                for (IControllable.Mode mode : IControllable.Mode.values()) {
//                    if (mode != IControllable.Mode.Unknown &&
//                                ((IControllable) tile).acceptsControlMode(mode)) {
//                        res.add(BuildCraftCore.actionControl[mode.ordinal()]);
//                    }
//                }
//            }
//        } catch (Throwable error) {
//            BCLog.logger.error("Outdated API detected, please update your mods!");
//        }

        return res;
    }
}
