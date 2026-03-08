package net.danygames2014.buildcraft.block.entity.pipe.statement;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.EnergyPipeTransporter;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.energyhandler.EnergyHandlerBlockCapability;
import net.danygames2014.nyalib.capability.block.energyhandler.EnergyStorageBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.util.math.Direction;
public class TriggerEnergy extends BCStatement implements TriggerExternal {
    @Override
    public boolean isTriggerActive(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters) {
        EnergyStorageBlockCapability storageCapability = CapabilityHelper.getCapability(target, EnergyStorageBlockCapability.class);

        int energyStored = 0;
        int energyMaxStored = 0;

        if(storageCapability != null){
            energyStored = storageCapability.getEnergyStored();
            energyMaxStored =  storageCapability.getEnergyCapacity();
        }

        if (energyMaxStored > 0) {
            if (high) {
                return ((double) energyStored / energyMaxStored) > 0.95;
            } else {
                return ((double) energyStored / energyMaxStored) < 0.05;
            }
        }

        return false;
    }

    private final boolean high;

    public TriggerEnergy(boolean high) {
        super(Buildcraft.NAMESPACE.id("energyStored" + (high ? "high" : "low")));
        this.high = high;
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.buildcraft.trigger.machine.energyStored." + (high ? "high" : "low"));
    }
}
