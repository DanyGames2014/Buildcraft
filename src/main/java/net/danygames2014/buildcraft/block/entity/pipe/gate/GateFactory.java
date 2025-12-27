package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansionController;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansions;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.item.GateItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public final class GateFactory {
    public static Gate makeGate(PipeBlockEntity pipe, GateMaterial material, GateLogic logic, Direction direction) {
        return new Gate(pipe, material, logic, direction);
    }

    public static Gate makeGate(PipeBlockEntity pipe, ItemStack stack, Direction direction) {
        if (stack == null || stack.count <= 0 || !(stack.getItem() instanceof GateItem gateItem)) {
            return null;
        }

        Gate gate = makeGate(pipe, gateItem.gateMaterial, gateItem.gateLogic, direction);

        for (GateExpansion expansion : GateItem.getInstalledExpansions(stack)) {
            gate.addGateExpansion(expansion);
        }

        return gate;
    }

    public static Gate makeGate(PipeBlockEntity pipe, NbtCompound nbt) {
        GateMaterial material = GateMaterial.REDSTONE;
        GateLogic logic = GateLogic.AND;
        Direction direction = null;

        if (nbt.contains("material")) {
            try {
                material = GateMaterial.valueOf(nbt.getString("material"));
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
        if (nbt.contains("logic")) {
            try {
                logic = GateLogic.valueOf(nbt.getString("logic"));
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
        if (nbt.contains("direction")) {
            direction = Direction.byId(nbt.getInt("direction"));
        }

        Gate gate = makeGate(pipe, material, logic, direction);
        gate.readNBT(nbt);

        NbtList exList = nbt.getList("expansions");
        for (int i = 0; i < exList.size(); i++) {
            NbtCompound conNBT = (NbtCompound)exList.get(i);
            GateExpansion ex = GateExpansions.getExpansion(Identifier.tryParse(conNBT.getString("type")));
            if (ex != null) {
                GateExpansionController con = ex.makeController(pipe);
                con.readNBT(conNBT.getCompound("data"));
                gate.expansions.put(ex, con);
            }
        }

        return gate;
    }
}
