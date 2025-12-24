package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

import java.util.Locale;

public class TriggerClockTimer extends BCStatement implements TriggerInternal {
    public enum Time {

        SHORT(5), MEDIUM(10), LONG(15);
        public static final Time[] VALUES = values();
        public final int delay;

        Time(int delay) {
            this.delay = delay;
        }
    }
    public final Time time;

    public TriggerClockTimer(Time time) {
        super(Buildcraft.NAMESPACE.id("timer." + time.name().toLowerCase(Locale.ENGLISH)));
        this.time = time;
    }

    @Override
    public String getDescription() {
        return String.format(TranslationStorage.getInstance().get("gate.trigger.timer"), time.delay);
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_timer_" + time.name().toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public boolean isTriggerActive(StatementContainer source, StatementParameter[] parameters) {
        return false;
    }
}
