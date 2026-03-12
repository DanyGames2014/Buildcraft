package net.danygames2014.buildcraft.block.entity.pipe.statement;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameterItemStack;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Locale;

public class TriggerInventory extends BCStatement implements TriggerExternal {

    public enum State {
        Empty, Contains, Space, Full
    }

    public State state;

    public TriggerInventory(State state) {
        super(Buildcraft.NAMESPACE.id("inventory." + state.name().toLowerCase(Locale.ENGLISH)));
        this.state = state;
    }

    @Override
    public int maxParameters() {
        return state == State.Contains || state == State.Space ? 1 : 0;
    }

    @Override
    public String getDescription() {
        return TranslationStorage.getInstance().get("gate.buildcraft.trigger.inventory." + state.name().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public boolean isTriggerActive(BlockEntity target, Direction side, StatementContainer source, StatementParameter[] parameters) {
        ItemStack searchedStack = null;

        if (parameters != null && parameters.length >= 1 && parameters[0] != null) {
            searchedStack = parameters[0].getItemStack();
        }

        ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(target.world, target.x, target.y, target.z, ItemHandlerBlockCapability.class);

        if (capability != null) {
            boolean hasSlots = capability.getItemSlots(side.getOpposite()) > 0;
            boolean foundItems = false;
            boolean foundSpace = false;

            ItemStack[] inventory = capability.getInventory(side.getOpposite());

            for (ItemStack stack : inventory) {
                // TODO: check if buildcraft also tests if the items fit together count wise or if it only checks item type
                if (stack != null) {
                    if (searchedStack == null || searchedStack.isItemEqual(stack)) {
                        foundItems = true;
                    }
                }

                if (searchedStack == null) {
                    if (stack == null || stack.count < stack.getMaxCount()) {
                        foundSpace = true;
                    }
                } else {
                    if (stack == null || (stack.isItemEqual(searchedStack) && stack.count < stack.getMaxCount())) {
                        foundSpace = true;
                    }
                }
            }

            if (!hasSlots) {
                return false;
            }

            return switch (state) {
                case Empty -> !foundItems;
                case Contains -> foundItems;
                case Space -> foundSpace;
                default -> !foundSpace;
            };
        }

        return false;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_inventory_" + state.name().toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public StatementParameter createParameter(int index) {
        return new StatementParameterItemStack();
    }
}
