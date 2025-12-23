package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameterItemStack;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.block.entity.pipe.statement.BCStatement;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Locale;

public class TriggerInventoryLevel extends BCStatement implements TriggerExternal {

    public enum TriggerType {
        BELOW25(0.25F), BELOW50(0.5F), BELOW75(0.75F);
        public final float level;

        TriggerType(float level) {
            this.level = level;
        }
    }

    public TriggerType type;

    public TriggerInventoryLevel(TriggerType type) {
        super(Buildcraft.NAMESPACE.id("inventorylevel." + type.name().toLowerCase(Locale.ENGLISH)));
        this.type = type;
    }

    @Override
    public int maxParameters() {
        return 1;
    }

    @Override
    public int minParameters() {
        return 1;
    }

    @Override
    public String getDescription() {
        return String.format(TranslationStorage.getInstance().get("gate.trigger.inventorylevel.below"), (int) (type.level * 100));
    }

    @Override
    public boolean isTriggerActive(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters) {
        // A parameter is required
        if (parameters == null || parameters.length < 1 || parameters[0] == null) {
            return false;
        }

        ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(target.world, target.x, target.y, target.z, ItemHandlerBlockCapability.class);

        if (capability != null) {
            ItemStack searchStack = parameters[0].getItemStack();

            if (searchStack == null) {
                return false;
            }

            int stackSpace = 0;
            int foundItems = 0;

            ItemStack[] inventory = capability.getInventory(side.getOpposite());

            // TODO: also check if the item is valid in the current slot
            for(int index = 0; index < inventory.length; index++){
                ItemStack stackInSlot = inventory[index];
                if (stackInSlot == null || searchStack.isItemEqual(stackInSlot)) {
                    stackSpace++;
                    foundItems += stackInSlot == null ? 0 : stackInSlot.count;
                }
            }

            if (stackSpace > 0) {
                float percentage = foundItems / ((float) stackSpace * (float) searchStack.getMaxCount());
                return percentage < type.level;
            }

        }

        return false;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_inventory_" + type.name().toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public StatementParameter createParameter(int index) {
        return new StatementParameterItemStack();
    }
}
