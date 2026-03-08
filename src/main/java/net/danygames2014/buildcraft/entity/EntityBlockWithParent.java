package net.danygames2014.buildcraft.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.server.entity.EntitySpawnDataProvider;
import net.modificationstation.stationapi.api.server.entity.HasTrackingParameters;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.TriState;

// TODO: Make this work properly on server
@HasTrackingParameters(trackingDistance = 32, updatePeriod = 1, sendVelocity = TriState.TRUE)
public class EntityBlockWithParent extends EntityBlock implements EntitySpawnDataProvider {
    public Entity parent;

    private int invalidTimer = 0;

    public EntityBlockWithParent(World world) {
        super(world);
    }

    public EntityBlockWithParent(World world, double x, double y, double z, double xSize, double ySize, double zSize, Entity parent){
        super(world, x, y, z, xSize, ySize, zSize);
        this.parent = parent;
    }

    public EntityBlockWithParent(World world, double x, double y, double z) {
        super(world, x, y, z);
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

    @Override
    public Identifier getHandlerIdentifier() {
        return Buildcraft.NAMESPACE.id("entity_block_with_parent");
    }
}
