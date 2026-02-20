package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementMouseClick;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;

public class TriggerParameterSignal implements StatementParameter {
    public static Atlas.Sprite[] sprites = new Atlas.Sprite[8];

    public boolean active = false;
    public PipeWire color = null;

    public TriggerParameterSignal(){

    }

    @Override
    public Identifier getIdentifier() {
        return Buildcraft.NAMESPACE.id("pipeWireTrigger");
    }

    @Override
    public Atlas.Sprite getSprite() {
        if (color == null) {
            return null;
        }

        return sprites[color.ordinal() + (active ? 4 : 0)];
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public void registerTextures() {
        sprites = new Atlas.Sprite[]{
                Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_red_inactive")),
                Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_blue_inactive")),
                Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_green_inactive")),
                Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_yellow_inactive")),
                Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_red_active")),
                Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_blue_active")),
                Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_green_active")),
                Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_yellow_active"))
        };
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void click(StatementContainer source, Statement stmt, ItemStack stack, StatementMouseClick mouse) {
        int maxColor = 4;
        if (mouse.getButton() == 0) {
            if (color == null) {
                active = true;
                color = PipeWire.RED;
            } else if (active) {
                active = false;
            } else if (color == PipeWire.values()[maxColor - 1]) {
                color = null;
            } else {
                do {
                    color = PipeWire.values()[(color.ordinal() + 1) & 3];
                } while (color.ordinal() >= maxColor);
                active = true;
            }
        } else {
            if (color == null) {
                active = false;
                color = PipeWire.values()[maxColor - 1];
            } else if (!active) {
                active = true;
            } else if (color == PipeWire.RED) {
                color = null;
            } else {
                do {
                    color = PipeWire.values()[(color.ordinal() - 1) & 3];
                } while (color.ordinal() >= maxColor);
                active = false;
            }
        }
    }

    @Override
    public void readNBT(NbtCompound nbt) {
        active = nbt.getBoolean("active");

        if (nbt.contains("color")) {
            color = PipeWire.values()[nbt.getByte("color")];
        }
    }

    @Override
    public void writeNBT(NbtCompound nbt) {
        nbt.putBoolean("active", active);

        if (color != null) {
            nbt.putByte("color", (byte) color.ordinal());
        }
    }

    @Override
    public StatementParameter rotateLeft() {
        return this;
    }
}
