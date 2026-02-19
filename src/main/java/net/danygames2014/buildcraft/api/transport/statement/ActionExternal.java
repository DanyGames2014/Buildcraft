package net.danygames2014.buildcraft.api.transport.statement;

import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface ActionExternal extends Statement {
    void actionActivate(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters);
}
