package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansions;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.block.entity.pipe.gate.*;
import net.danygames2014.buildcraft.pluggable.GatePluggable;
import net.glasslauncher.mods.alwaysmoreitems.api.SubItemProvider;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GateItem extends TemplateItem implements CustomTooltipProvider, PipePluggableItem {
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

    public static GateMaterial getMaterial(ItemStack stack) {
        NbtCompound nbt = stack.getStationNbt();

        if (nbt == null) {
            return GateMaterial.REDSTONE;
        } else {
            return GateMaterial.fromOrdinal(nbt.getByte(NBT_TAG_MAT));
        }
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

    public static void addGateExpansion(ItemStack stack, GateExpansion expansion) {
        NbtCompound nbt = stack.getStationNbt();

        if (nbt == null) {
            return;
        }

        NbtList expansionList = nbt.getList(NBT_TAG_EX);
        expansionList.add(new NbtString(expansion.getIdentifier().toString()));
        nbt.put(NBT_TAG_EX, expansionList);
    }

    public static boolean hasGateExpansion(ItemStack stack, GateExpansion expansion) {
        NbtCompound nbt = stack.getStationNbt();

        if (nbt == null) {
            return false;
        }

        try {
            NbtList expansionList = nbt.getList(NBT_TAG_EX);

            for (int i = 0; i < expansionList.size(); i++) {
                NbtString ex = (NbtString) expansionList.get(i);

                if (ex.value.equals(expansion.getIdentifier().toString())) {
                    return true;
                }
            }
        } catch (RuntimeException error) {
        }

        return false;
    }

    public static Set<GateExpansion> getInstalledExpansions(ItemStack stack) {
        Set<GateExpansion> expansions = new HashSet<GateExpansion>();
        NbtCompound nbt = stack.getStationNbt();

        if (nbt == null) {
            return expansions;
        }

        try {
            NbtList expansionList = nbt.getList(NBT_TAG_EX);
            for (int i = 0; i < expansionList.size(); i++) {
                NbtString exTag = (NbtString) expansionList.get(i);
                GateExpansion ex = GateExpansions.getExpansion(Identifier.tryParse(exTag.value));
                if (ex != null) {
                    expansions.add(ex);
                }
            }
        } catch (RuntimeException error) {
        }

        return expansions;
    }

    public static ItemStack makeGateItem(GateMaterial material, GateLogic logic) {
        ItemStack stack = new ItemStack(Buildcraft.gateItem);
        NbtCompound nbt = stack.getStationNbt();
        nbt.putByte(NBT_TAG_MAT, (byte) material.ordinal());
        nbt.putByte(NBT_TAG_LOGIC, (byte) logic.ordinal());

        return stack;
    }

    public static ItemStack makeGateItem(Gate gate) {
        ItemStack stack = new ItemStack(Buildcraft.gateItem);
        NbtCompound nbt = stack.getStationNbt();
        nbt.putByte(NBT_TAG_MAT, (byte) gate.material.ordinal());
        nbt.putByte(NBT_TAG_LOGIC, (byte) gate.logic.ordinal());

        for (GateExpansion expansion : gate.expansions.keySet()) {
            addGateExpansion(stack, expansion);
        }

        return stack;
    }

    @SubItemProvider
    public List<ItemStack> getSubItems(){
        List<ItemStack> items = new ArrayList<>();
        for (GateMaterial material : GateMaterial.VALUES) {
            for (GateLogic logic : GateLogic.VALUES) {
                if (material == GateMaterial.REDSTONE && logic == GateLogic.OR) {
                    continue;
                }

                items.add(makeGateItem(material, logic));

                for (GateExpansion exp : GateExpansions.getExpansions()) {
                    ItemStack stackExpansion = makeGateItem(material, logic);
                    addGateExpansion(stackExpansion, exp);
                    items.add(stackExpansion);
                }
            }
        }
        return items;
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack itemStack, String s) {
        TranslationStorage translationStorage = TranslationStorage.getInstance();
        List<String> lines = new ArrayList<>();
        lines.add(GateDefinition.getLocalizedName(getMaterial(itemStack), getLogic(itemStack)));
//        lines.add(translationStorage.get("tip.gate.wires"));
//        lines.add(translationStorage.get("tip.gate.wires." + getMaterial(itemStack).getTag()));

        Set<GateExpansion> expansions = getInstalledExpansions(itemStack);

        if (!expansions.isEmpty()) {
            lines.add(translationStorage.get("tip.gate.expansions"));

            for (GateExpansion expansion : expansions) {
                lines.add(expansion.getDisplayName());
            }
        }

        return lines.toArray(String[]::new);
    }

    @Override
    public PipePluggable createPipePluggable(PipeBlockEntity pipe, Direction side, ItemStack stack) {
        return new GatePluggable(GateFactory.makeGate(pipe, stack, side));
    }
}
