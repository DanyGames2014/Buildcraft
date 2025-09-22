package net.danygames2014.buildcraft.api.transport;

import net.danygames2014.buildcraft.api.statement.Statement;
import net.danygames2014.buildcraft.api.statement.StatementParameter;
import net.danygames2014.buildcraft.api.statement.StatementSlot;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;

import java.util.List;

public interface Gate {
    PipeBlockEntity getPipe();

    List<Statement> getTriggers();
    List<Statement> getActions();

    List<StatementSlot> getActiveActions();

    List<StatementParameter> getTriggerParameters(int index);
    List<StatementParameter> getActionParameters(int index);
}
