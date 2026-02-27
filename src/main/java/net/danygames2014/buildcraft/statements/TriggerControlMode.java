package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.danygames2014.buildcraft.api.blockentity.Controllable;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;
// TODO: finish implementing
public class TriggerControlMode extends BCStatement implements TriggerExternal {


    @Override
    public boolean isTriggerActive(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters) {
        if(target instanceof Controllable controllable){
            return controllable.getControlMode() == ControlMode.ON;
        }

        return false;
    }

    @Override
    public StatementParameter createParameter(int index) {
        return super.createParameter(index);
    }

    @Override
    public int maxParameters() {
        return 1;
    }
}
