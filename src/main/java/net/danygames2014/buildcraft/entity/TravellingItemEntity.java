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
import net.modificationstation.stationapi.api.util.math.Direction;

// TODO: Somehow rectify the random y offset and animation that ItemRenderer/ArsenicItemRenderer keeps adding

@HasTrackingParameters(updatePeriod = 1, sendVelocity = TriState.TRUE, trackingDistance = 32)
public class TravellingItemEntity extends ItemEntity {
    public static final double MINIMUM_SPEED = 0.01D;
    public static final double DEFAULT_SPEED = 0.01D;
    public static final double MAXIMUM_SPEED = 0.25D;
    public static final double ACCELERATION_MODIFIER = 3.0D;
    public static final double DECCELERATION_MODIFIER = 0.98D;
    
    public ItemPipeTransporter transporter;
    public Direction input = null;
    public Direction travelDirection = null;
    public Direction lastTravelDirection = null;
    public double speed = 0.01D;
    public double lastSpeed = 0.0D;
    public int invalidTimer = 0;
    public boolean toMiddle = true;

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
        if (transporter == null) {
            addToTransporter();
        }
        
        this.baseTick();

        if (transporter != null) {
            double pipeSpeed = transporter.blockEntity.behavior.modifyItemSpeed(this);
            
            if (speed < pipeSpeed) {
                speed = Math.min(pipeSpeed, speed * ACCELERATION_MODIFIER);
            } else if (speed > pipeSpeed) {
                speed *= DECCELERATION_MODIFIER;
            }
            
            if (speed < MINIMUM_SPEED) {
                speed = MINIMUM_SPEED;
            } else if (speed > MAXIMUM_SPEED) {
                speed = MAXIMUM_SPEED;
            }
        }

//        if (toMiddle && travelDirection != lastTravelDirection && transporter != null) {
//            this.setPosition(transporter.blockEntity.x + 0.5D, transporter.blockEntity.y + 0.25D, transporter.blockEntity.z + 0.5D);
//            toMiddle = false;
//        }
        
        if (travelDirection != null) {
            if (travelDirection != lastTravelDirection || speed != lastSpeed) {
                this.velocityX = travelDirection.getOffsetX() * speed;
                this.velocityY = travelDirection.getOffsetY() * speed;
                this.velocityZ = travelDirection.getOffsetZ() * speed;
            }
        } else {
            invalidTimer++;
        }

        lastTravelDirection = travelDirection;
        lastSpeed = speed;

        this.move(this.velocityX, this.velocityY, this.velocityZ);
        
        if (this.invalidTimer >= 50) {
            drop();
        }
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

    public void drop(){
        this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y, this.z, this.stack));
        this.markDead();
    }
    
    @Override
    public void onPlayerInteraction(PlayerEntity player) {
        
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("speed", speed);
        nbt.putInt("input", input.getId());
        nbt.putInt("travelDirection", travelDirection.getId());
    }

    @Override
    public void read(NbtCompound nbt) {
        super.read(nbt);
        speed = nbt.getDouble("speed");
        input = Direction.byId(nbt.getInt("input"));
        travelDirection = Direction.byId(nbt.getInt("travelDirection"));
    }
}
