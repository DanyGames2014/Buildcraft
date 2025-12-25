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

public class TriggerFluidContainerLevel extends BCStatement implements TriggerExternal {
    public enum TriggerType {

        BELOW25(0.25F), BELOW50(0.5F), BELOW75(0.75F);

        public final float level;

        TriggerType(float level) {
            this.level = level;
        }
    }

    public TriggerType type;

    public TriggerFluidContainerLevel(TriggerType type) {
        super(Buildcraft.NAMESPACE.id("fluid." + type.name().toLowerCase(Locale.ENGLISH)));
        this.type = type;
    }

    @Override
    public int maxParameters() {
        return 1;
    }

    @Override
    public String getDescription() {
        return String.format(TranslationStorage.getInstance().get("gate.trigger.fluidlevel.below"), (int) (type.level * 100));
    }


    // TODO: check if this is correct
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
            if (liquids == null || liquids.length == 0) {
                return false;
            }

            for (int i = 0; i < liquids.length; i++) {
                FluidStack c = liquids[i];
                if (c == null) {
                    continue;
                }
                if (c.fluid == null) {
                    if (searchedFluid == null) {
                        return true;
                    }
                    return c.isFluidEqual(searchedFluid) && capability.getFluidCapacity(i, side) > 0;
                }

                if (searchedFluid == null || searchedFluid.isFluidEqual(c)) {
                    float percentage = (float) c.amount / (float) capability.getFluidCapacity(i, side);
                    return percentage < type.level;
                }
            }
        }

        return false;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_liquidcontainer_" + type.name().toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public StatementParameter createParameter(int index) {
        return new StatementParameterItemStack();
    }
}
