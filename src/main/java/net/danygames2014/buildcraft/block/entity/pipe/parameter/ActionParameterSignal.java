package net.danygames2014.buildcraft.block.entity.pipe.parameter;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementMouseClick;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ActionParameterSignal implements StatementParameter {
    private static Atlas.Sprite[] icons;

    public PipeWire color = null;

    public ActionParameterSignal() {

    }

    @Override
    public Identifier getIdentifier() {
        return Buildcraft.NAMESPACE.id("pipe_wire_action");
    }

    @Override
    public Atlas.Sprite getSprite() {
        if (color == null) {
            return null;
        } else {
            return icons[color.ordinal()];
        }
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ActionParameterSignal) {
            ActionParameterSignal param = (ActionParameterSignal) object;

            return param.color == color;
        } else {
            return false;
        }
    }

    @Override
    public void registerTextures() {
        icons = new Atlas.Sprite[] {
            Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_red_active")),
            Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_blue_active")),
            Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_green_active")),
            Atlases.getGuiItems().addTexture(Buildcraft.NAMESPACE.id("item/trigger/trigger_pipesignal_yellow_active"))
        };
    }

    @Override
    public String getDescription() {
        if (color == null) {
            return null;
        }
        return String.format(TranslationStorage.getInstance().get("gate.action.pipe.wire"), TranslationStorage.getInstance().get("color." + color.name().toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public void click(StatementContainer source, Statement stmt, ItemStack stack, StatementMouseClick mouse) {
        int maxColor = 4;

        if (color == null) {
            color = mouse.getButton() == 0 ? PipeWire.RED : PipeWire.values()[maxColor - 1];
        } else if (color == (mouse.getButton() == 0 ? PipeWire.values()[maxColor - 1] : PipeWire.RED)) {
            color = null;
        } else {
            do {
                color = PipeWire.values()[(mouse.getButton() == 0 ? color.ordinal() + 1 : color.ordinal() - 1) & 3];
            } while (color.ordinal() >= maxColor);
        }
    }

    @Override
    public void readNBT(NbtCompound nbt) {
        if (nbt.contains("color")) {
            color = PipeWire.values()[nbt.getByte("color")];
        }
    }

    @Override
    public void writeNBT(NbtCompound nbt) {
        if (color != null) {
            nbt.putByte("color", (byte) color.ordinal());
        }
    }

    @Override
    public StatementParameter rotateLeft() {
        return this;
    }
}
