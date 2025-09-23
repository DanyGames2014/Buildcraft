package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;

import java.awt.*;
import java.util.ArrayList;

public class GateItem extends TemplateItem {
    protected static final String NBT_TAG_MAT = "mat";
    protected static final String NBT_TAG_LOGIC = "logic";
    protected static final String NBT_TAG_EX = "ex";

    private static ArrayList<ItemStack> allGates;

    public GateItem(Identifier identifier) {
        super(identifier);
        setHasSubtypes(false);
        setMaxDamage(0);
    }

    public static void setMaterial(ItemStack stack, GateMaterial material){
        stack.getStationNbt().putByte(NBT_TAG_MAT, (byte) material.ordinal());
    }

    public static GateLogic getLogic(ItemStack stack){
        if(!stack.getStationNbt().contains(NBT_TAG_LOGIC)){
            return GateLogic.AND;
        } else {
            return GateLogic.fromOrdinal(stack.getStationNbt().getByte(NBT_TAG_LOGIC));
        }
    }

    public static void setLogic(ItemStack stack, GateLogic logic){
        stack.getStationNbt().putByte(NBT_TAG_LOGIC, (byte) logic.ordinal());
    }
}
