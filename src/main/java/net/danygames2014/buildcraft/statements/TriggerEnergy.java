package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.danygames2014.nyalib.energy.EnergyHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.util.math.Direction;

// TODO: check if this trigger makes sense for b1.7.3

public class TriggerEnergy extends BCStatement implements TriggerInternal {
    public static class Neighbor {
        public BlockEntity blockEntity;
        public Direction side;

        public Neighbor(BlockEntity blockEntity, Direction side) {
            this.blockEntity = blockEntity;
            this.side = side;
        }
    }

    private final boolean high;

    public TriggerEnergy(boolean high) {
        super(Buildcraft.NAMESPACE.id("energyStored" + (high ? "high" : "low")));
        this.high = high;
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.trigger.machine.energyStored." + (high ? "high" : "low"));
    }

    @Override
    public boolean isTriggerActive(StatementContainer source, StatementParameter[] parameters) {
        return false;
    }
}
