package net.danygames2014.buildcraft.block.entity.pipe.gate;

import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.item.GateItem;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.math.Direction;

public final class GateFactory {
    public static Gate makeGate(PipeBlockEntity pipe, GateMaterial material, GateLogic logic, Direction direction) {
        return new Gate(pipe, material, logic, direction);
    }

    public static Gate makeGate(PipeBlockEntity pipe, ItemStack stack, Direction direction) {
        if (stack == null || stack.count <= 0 || !(stack.getItem() instanceof GateItem)) {
            return null;
        }

        Gate gate = makeGate(pipe, GateItem.getMaterial(stack), GateItem.getLogic(stack), direction);

        for (GateExpansion expansion : GateItem.getInstalledExpansions(stack)) {
            gate.addGateExpansion(expansion);
        }

        return gate;
    }
}
