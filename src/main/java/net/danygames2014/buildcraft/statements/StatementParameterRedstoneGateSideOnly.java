package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementMouseClick;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;

public class StatementParameterRedstoneGateSideOnly implements StatementParameter {

    private static Atlas.Sprite icon;
    public boolean isOn = false;

    @Override
    public Identifier getIdentifier() {
        return Buildcraft.NAMESPACE.id("redstoneGateSideOnly");
    }

    @Override
    public Atlas.Sprite getSprite() {
        if (!isOn) {
            return null;
        } else {
            return icon;
        }
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public void registerTextures() {
        icon = Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/redstone_gate_side_only"));
    }

    @Override
    public String getDescription() {
        return isOn ? TranslationStorage.getInstance().get("gate.parameter.redstone.gateSideOnly") : "";
    }

    @Override
    public void click(StatementContainer source, Statement stmt, ItemStack stack, StatementMouseClick mouse) {
        isOn = !isOn;
    }

    @Override
    public void readNBT(NbtCompound nbt) {
        if (nbt.contains("isOn")) {
            isOn = nbt.getByte("isOn") == 1;
        }
    }

    @Override
    public void writeNBT(NbtCompound nbt) {
        nbt.putByte("isOn", isOn ? (byte) 1 : (byte) 0);
    }

    @Override
    public StatementParameter rotateLeft() {
        return this;
    }
}
