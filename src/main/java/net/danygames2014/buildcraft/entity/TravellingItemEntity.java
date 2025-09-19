package net.danygames2014.buildcraft.entity;

import net.danygames2014.buildcraft.block.entity.pipe.ItemPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.server.entity.HasTrackingParameters;
import net.modificationstation.stationapi.api.util.TriState;

// TODO: Somehow rectify the random y offset and animation that ItemRenderer/ArsenicItemRenderer keeps adding

@HasTrackingParameters(updatePeriod = 1, sendVelocity = TriState.TRUE, trackingDistance = 32)
public class TravellingItemEntity extends ItemEntity {
    public ItemPipeTransporter transporter;
    public boolean scheduledAddToTransporter = false;
    public double speed = 0.01D;

    public TravellingItemEntity(World world, double x, double y, double z) {
        this(world, x, y, z, null);
    }
    
    public TravellingItemEntity(World world, double x, double y, double z, ItemStack stack) {
        this(world);
        this.setPosition(x, y, z);
        this.stack = stack;
    }

    public TravellingItemEntity(World world) {
        super(world);
        this.noClip = true;
    }
    
    public void addToTransporter() {
        BlockEntity pipeBlockEntity = world.getBlockEntity(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        
        if (pipeBlockEntity instanceof PipeBlockEntity pipe && pipe.transporter instanceof ItemPipeTransporter pipeTransporter) {
            pipeTransporter.addItem(this);
            this.transporter = pipeTransporter;
        }
    }

    // Item Logic
    public void reset() {
        
    }
    
    // Entity Logic
    @Override
    public void baseTick() {
        this.age++;
        
        this.prevHorizontalSpeed = this.horizontalSpeed;
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;

        if (this.y < -64.0D) {
            this.tickInVoid();
        }
    }

    @Override
    public void tick() {
        if (scheduledAddToTransporter) {
            addToTransporter();
            scheduledAddToTransporter = false;
        }
        
        this.baseTick();

        this.velocityX = 0.00D;
        this.velocityY = 0.00D;
        this.velocityZ = 0.01D;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    public boolean checkWaterCollisions() {
        return false;
    }

    @Override
    public boolean damage(Entity damageSource, int amount) {
        this.health -= amount;
        if (this.health <= 0) {
            this.markDead();
        }

        return false;
    }

    @Override
    public void markDead() {
        if (transporter != null) {
            transporter.contents.remove(this);
        }
        super.markDead();
    }

    @Override
    public void onPlayerInteraction(PlayerEntity player) {
        
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void read(NbtCompound nbt) {
        super.read(nbt);
        scheduledAddToTransporter = true;
    }
}
