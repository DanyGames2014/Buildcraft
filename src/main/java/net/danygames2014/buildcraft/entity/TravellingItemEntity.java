package net.danygames2014.buildcraft.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.server.entity.EntitySpawnDataProvider;
import net.modificationstation.stationapi.api.server.entity.HasTrackingParameters;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.TriState;
import net.modificationstation.stationapi.api.util.math.Direction;

@HasTrackingParameters(updatePeriod = 1, sendVelocity = TriState.FALSE, trackingDistance = 32)
public class TravellingItemEntity extends ItemEntity implements EntitySpawnDataProvider {
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

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(10, -1);
        dataTracker.startTracking(11, 0);
    }

    public void addToTransporter() {
        BlockEntity pipeBlockEntity = world.getBlockEntity(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        
        if (pipeBlockEntity instanceof PipeBlockEntity pipe && pipe.transporter instanceof ItemPipeTransporter pipeTransporter) {
            pipeTransporter.addItem(this);
            this.transporter = pipeTransporter;
        }
    }

    // Entity Logic
    @Override
    public void baseTick() {
        if (world.isRemote) {
            return;
        }
        
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
        if (!world.isRemote && transporter == null) {
            addToTransporter();
        }
        
        this.baseTick();

        if (!world.isRemote) {
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

            if (travelDirection != null) {
                if (travelDirection != lastTravelDirection || speed != lastSpeed) {
                    dataTracker.set(10, travelDirection.ordinal());
                    dataTracker.set(11, (int) (speed * 10000));
                    this.velocityX = travelDirection.getOffsetX() * speed;
                    this.velocityY = travelDirection.getOffsetY() * speed;
                    this.velocityZ = travelDirection.getOffsetZ() * speed;
                }
            } else {
                invalidTimer++;
            }
            
            lastTravelDirection = travelDirection;
            lastSpeed = speed;
        }

        // Client velocity code
        if (world.isRemote) {
            if (dataTracker.getInt(10) != -1) {
                travelDirection = Direction.values()[dataTracker.getInt(10)];
            }
            
            if (dataTracker.getInt(11) != 0) {
                speed = dataTracker.getInt(11) / 10000.0D;
            }
            
            if (travelDirection != null) {
                this.velocityX = travelDirection.getOffsetX() * speed;
                this.velocityY = travelDirection.getOffsetY() * speed;
                this.velocityZ = travelDirection.getOffsetZ() * speed;
            }

            lastTravelDirection = travelDirection;
        }
        
        this.move(this.velocityX, this.velocityY, this.velocityZ);

        if (!world.isRemote) {
            if (this.invalidTimer >= 50) {
                drop();
            }
        }
    }

    @Override
    public boolean checkWaterCollisions() {
        return false;
    }

    @Override
    protected boolean pushOutOfBlock(double x, double y, double z) {
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        speed = nbt.getDouble("speed");
        input = Direction.byId(nbt.getInt("input"));
        travelDirection = Direction.byId(nbt.getInt("travelDirection"));
    }

    @Override
    public boolean syncTrackerAtSpawn() {
        return true;
    }

    @Override
    public Identifier getHandlerIdentifier() {
        return Buildcraft.NAMESPACE.id("travelling_item");
    }

    @Override
    public void writeToMessage(MessagePacket message) {
        message.ints = new int[] {
                message.ints[0], 
                message.ints[1], 
                message.ints[2], 
                message.ints[3], 
                message.ints[4], 
                this.stack.itemId, 
                this.stack.count, 
                this.stack.getDamage()
        };
    }

    @Override
    public void readFromMessage(MessagePacket message) {
        this.stack = new ItemStack(
                message.ints[5], 
                message.ints[6], 
                message.ints[7]
        );
    }
}
