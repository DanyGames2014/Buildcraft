package net.danygames2014.buildcraft.block.material;

import net.minecraft.block.MapColor;
import net.minecraft.block.material.Material;

public class PipeMaterial extends Material {
    public PipeMaterial(MapColor mapColor) {
        super(mapColor);
        setDestroyPistonBehavior();
    }
}
