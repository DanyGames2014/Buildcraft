package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameterItemStack;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.EnergyPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.nyalib.fluid.FluidBucket;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;

import java.util.Locale;

public class TriggerPipeContents extends BCStatement implements TriggerInternal {
    public enum PipeContents {
        empty,
        containsItems,
        containsFluids,
        containsEnergy,
        requestsEnergy,
        tooMuchEnergy;
        public TriggerInternal trigger;
    }

    private PipeContents kind;

    public TriggerPipeContents(PipeContents kind) {
        super(Buildcraft.NAMESPACE.id("pipe.contents." + kind.name().toLowerCase(Locale.ENGLISH)));
        this.kind = kind;
        kind.trigger = this;
    }

    @Override
    public int maxParameters() {
        switch (kind) {
            case containsItems:
            case containsFluids:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.trigger.pipe." + kind.name());
    }


    @Override
    public boolean isTriggerActive(StatementContainer source, StatementParameter[] parameters) {
        if (!(source instanceof Gate gate)) {
            return false;
        }

        PipeBlockEntity pipe = gate.pipe;
        StatementParameter parameter = parameters[0];

        if (pipe.transporter instanceof ItemPipeTransporter itemTransporter) {
            if (kind == PipeContents.empty) {
                return itemTransporter.contents.isEmpty();
            } else if (kind == PipeContents.containsItems) {
                if (parameter != null && parameter.getItemStack() != null) {
                    for (TravellingItemEntity item : itemTransporter.contents) {
                        if (item.stack.isItemEqual(parameter.getItemStack())) {
                            return true;
                        }
                    }
                } else {
                    return !itemTransporter.contents.isEmpty();
                }
            }
        } else if (pipe.transporter instanceof FluidPipeTransporter fluidTransporter) {

            if (kind == PipeContents.empty) {
                return fluidTransporter.fluidType == null;
            } else {
                if (parameter != null && parameter.getItemStack() != null) {
                    FluidStack searchedFluid = null;
                    // TODO: this might be wrong, double check
                    if(parameter.getItemStack().getItem() instanceof FluidBucket fluidBucket){
                        searchedFluid = new FluidStack(fluidBucket.getFluid());
                    }

                    if (searchedFluid != null) {
                        return fluidTransporter.fluidType != null && searchedFluid.isFluidEqual(new FluidStack(fluidTransporter.fluidType));
                    }
                } else {
                    return fluidTransporter.fluidType != null;
                }
            }
        } else if (pipe.transporter instanceof EnergyPipeTransporter energyTransporter) {
            switch (kind) {
                case empty:
                    for (double s : energyTransporter.displayPower) {
                        if (s > 1e-4) {
                            return false;
                        }
                    }

                    return true;
                case containsEnergy:
                    for (double s : energyTransporter.displayPower) {
                        if (s > 1e-4) {
                            return true;
                        }
                    }

                    return false;
                case requestsEnergy:
                    return energyTransporter.isQueryingPower();
                default:
                case tooMuchEnergy:
                    return energyTransporter.isOverloaded();
            }
        }

        return false;
    }

    @Override
    public StatementParameter createParameter(int index) {
        return new StatementParameterItemStack();
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipecontents_" + kind.name().toLowerCase(Locale.ENGLISH)));
    }
}
