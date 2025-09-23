package net.danygames2014.buildcraft.block.entity.pipe.statement;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.parameter.ActionParameterSignal;
import net.minecraft.client.resource.language.TranslationStorage;

import java.util.Locale;

public class ActionSignalOutput extends BCStatement implements ActionInternal {
    public final PipeWire color;

    public ActionSignalOutput(PipeWire color) {
        super(Buildcraft.NAMESPACE.id("pipe.wire.output." + color.name().toLowerCase(Locale.ENGLISH)));

        this.color = color;
    }

    @Override
    public void actionActivate(StatementContainer source, StatementParameter[] parameters) {
        Gate gate = (Gate) source;

        gate.broadcastSignal(color);

        for (StatementParameter param : parameters) {
            if (param != null && param instanceof ActionParameterSignal) {
                ActionParameterSignal signal = (ActionParameterSignal) param;

                if (signal.color != null) {
                    gate.broadcastSignal(signal.color);
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return String.format(TranslationStorage.getInstance().get("gate.action.pipe.wire"), TranslationStorage.getInstance().get("color." + color.name().toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public int maxParameters() {
        return 3;
    }

    @Override
    public StatementParameter createParameter(int index) {
        return new ActionParameterSignal();
    }
}
