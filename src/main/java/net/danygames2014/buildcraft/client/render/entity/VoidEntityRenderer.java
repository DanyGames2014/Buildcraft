package net.danygames2014.buildcraft.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

public class VoidEntityRenderer extends EntityRenderer {
    public static final VoidEntityRenderer INSTANCE = new VoidEntityRenderer();
    
    @Override
    public void render(Entity entity, double x, double y, double z, float yaw, float pitch) {

    }
}
