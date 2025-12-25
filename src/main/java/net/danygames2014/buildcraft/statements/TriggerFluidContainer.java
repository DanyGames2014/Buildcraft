package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameterItemStack;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.fluid.FluidBucket;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Locale;

public class TriggerFluidContainer extends BCStatement implements TriggerExternal {
    public enum State {
        Empty, Contains, Space, Full
    }

    public State state;

    public TriggerFluidContainer(State state) {
        super(Buildcraft.NAMESPACE.id("fluid." + state.name().toLowerCase(Locale.ENGLISH)));
        this.state = state;
    }

    @Override
    public int maxParameters() {
        return state == State.Contains || state == State.Space ? 1 : 0;
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.trigger.fluid." + state.name().toLowerCase(Locale.ENGLISH));
    }

    // TODO: confirm if this is actually working or not
    @Override
    public boolean isTriggerActive(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters) {
        FluidHandlerBlockCapability capability = CapabilityHelper.getCapability(target.world, target.x, target.y, target.z, FluidHandlerBlockCapability.class);

        if (capability != null) {
            FluidStack searchedFluid = null;

            if (parameters != null && parameters.length >= 1 && parameters[0] != null && parameters[0].getItemStack() != null) {
                if(parameters[0].getItemStack().getItem() instanceof FluidBucket bucket){
                    searchedFluid = new FluidStack(bucket.getFluid());
                }

            }

            if (searchedFluid != null) {
                searchedFluid.amount = 1;
            }

            FluidStack[] liquids = capability.getFluids(side);
            if (liquids == null) {
                return false;
            }

            switch (state) {
                case Empty:
                    for (FluidStack c : liquids) {
                        if (c != null && c.fluid != null && c.amount > 0 && (searchedFluid == null || searchedFluid.isFluidEqual(c))) {
                            return false;
                        }
                    }
                    return true;
                case Contains:
                    for (FluidStack c : liquids) {
                        if (c != null && c.fluid != null && c.amount > 0 && (searchedFluid == null || searchedFluid.isFluidEqual(c))) {
                            return true;
                        }
                    }
                    return false;
                case Space:
                    if (searchedFluid == null) {
                        for (int i = 0; i < liquids.length; i++) {
                            FluidStack c = liquids[i];
                            if (c != null && (c.fluid == null || capability.getFluidCapacity(i, side) > 0)) {
                                return true;
                            }
                        }
                        return false;
                    } else {
                        for (int i = 0; i < liquids.length; i++) {
                            FluidStack c = liquids[i];
                            if (c != null && (c.fluid == null || capability.getFluidCapacity(i, side) > 0 && c.fluid == searchedFluid.fluid)) {
                                return true;
                            }
                        }
                    }
                    return false;
                case Full:
                    if (searchedFluid == null) {
                        for (int i = 0; i < liquids.length; i++) {
                            FluidStack c = liquids[i];
                            if (c != null && (c.fluid == null || capability.getFluidCapacity(i, side) <= 0)) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        for (int i = 0; i < liquids.length; i++) {
                            FluidStack c = liquids[i];
                            if (c != null && (c.fluid == null || capability.getFluidCapacity(i, side) <= 0 && c.fluid == searchedFluid.fluid)) {
                                return true;
                            }
                        }
                    }
                    return false;
            }
        }

        return false;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_liquidcontainer_" + state.name().toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public StatementParameter createParameter(int index) {
        return new StatementParameterItemStack();
    }
}
