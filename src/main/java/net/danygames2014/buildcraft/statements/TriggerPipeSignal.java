package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.gate.Gate;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

import java.util.Locale;

public class TriggerPipeSignal extends BCStatement implements TriggerInternal {
    boolean active;
    PipeWire color;

    public TriggerPipeSignal(boolean active, PipeWire color){
        super(Buildcraft.NAMESPACE.id("pipe.wire.input." + color.name().toLowerCase(Locale.ENGLISH) + (active ? ".active" : ".inactive")));
        this.active = active;
        this.color = color;
    }

    @Override
    public int maxParameters() {
        return 3;
    }

    @Override
    public boolean isTriggerActive(StatementContainer source, StatementParameter[] parameters) {
        if (!(source instanceof Gate)) {
            return false;
        }

        PipeBlockEntity pipe = ((Gate) source).getPipe();

        if (active) {
            if (pipe.signalStrength[color.ordinal()] == 0) {
                return false;
            }
        } else {
            if (pipe.signalStrength[color.ordinal()] > 0) {
                return false;
            }
        }

        for (StatementParameter param : parameters) {
            if (param instanceof TriggerParameterSignal signal) {
                if (signal.color != null) {
                    if (signal.active) {
                        if (pipe.signalStrength[signal.color.ordinal()] == 0) {
                            return false;
                        }
                    } else {
                        if (pipe.signalStrength[signal.color.ordinal()] > 0) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public StatementParameter createParameter(int index) {
        return new TriggerParameterSignal();
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_" + color.name().toLowerCase(Locale.ENGLISH) + "_" + (active ? "active" : "inactive")));
    }
}
