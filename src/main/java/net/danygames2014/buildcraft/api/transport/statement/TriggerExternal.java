package net.danygames2014.buildcraft.api.transport.statement;

import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface TriggerExternal extends Statement {
    boolean isTriggerActive(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters);
}
