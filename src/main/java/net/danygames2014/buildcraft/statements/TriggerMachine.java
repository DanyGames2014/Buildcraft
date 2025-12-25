package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.blockentity.HasWork;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.math.Direction;

public class TriggerMachine extends BCStatement implements TriggerExternal {
    boolean active;

    public TriggerMachine(boolean active) {
        super(Buildcraft.NAMESPACE.id("work." + (active ? "scheduled" : "done")));

        this.active = active;
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.trigger.machine." + (active ? "scheduled" : "done"));
    }

    @Override
    public boolean isTriggerActive(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters) {
        if (target instanceof HasWork machine) {
            if (active) {
                return machine.hasWork();
            } else {
                return !machine.hasWork();
            }
        }

        return false;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_machine_" + (active ? "active" : "inactive")));
    }
}
