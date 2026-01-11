package net.danygames2014.buildcraft.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityBlockWithParent extends EntityBlock{
    public Entity parent;

    private int invalidTimer = 0;

    public EntityBlockWithParent(World world) {
        super(world);
    }

    public EntityBlockWithParent(World world, double x, double y, double z, double xSize, double ySize, double zSize, Entity parent){
        super(world, x, y, z, xSize, ySize, zSize);
        this.parent = parent;
    }

    @Override
    public void tick() {
        super.tick();
        if(parent == null || parent.dead){
            invalidTimer++;
            if(invalidTimer > 20){
                markDead();
            }
        } else {
            invalidTimer = 0;
        }
    }
}
