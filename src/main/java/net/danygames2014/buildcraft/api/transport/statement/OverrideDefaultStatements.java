package net.danygames2014.buildcraft.api.transport.statement;

import java.util.List;

public interface OverrideDefaultStatements {
    List<TriggerExternal> overrideTriggers();
    List<ActionExternal> overrideActions();
}
