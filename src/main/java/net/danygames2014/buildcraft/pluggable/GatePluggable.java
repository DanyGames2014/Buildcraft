package net.danygames2014.buildcraft.pluggable;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

public class GatePluggable extends PipePluggable {

    public GateMaterial material;
    public GateLogic logic;
    public boolean isLit, isPulsing;

    @Override
    public ItemStack[] getDropItems(PipeBlockEntity pipe) {
        return new ItemStack[0];
    }

    @Override
    public Box getBoundingBox(Direction side) {
        return null;
    }

    @Override
    public PipePluggableRenderer getRenderer() {
        return null;
    }

    @Override
    public void readNbt(NbtCompound nbt) {

    }

    @Override
    public void writeNbt(NbtCompound nbt) {

    }
}
