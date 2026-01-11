package net.danygames2014.buildcraft.entity;

import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.block.entity.AreaWorkerBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.LaserData;
import net.danygames2014.buildcraft.client.render.LaserRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.MutableBlockPos;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;

public class RobotEntity extends Entity {
    private static final int TARGET_X_KEY = 1;
    private static final int TARGET_Y_KEY = 2;
    private static final int TARGET_Z_KEY = 3;
    private static final int LASER_TEXTURE_KEY = 4;

    private BlockPos areaWorkerPosition;
    private MutableBlockPos destination;

    private AreaWorkerBlockEntity areaWorker;

    public LaserData laserData;

    public RobotEntity(World world) {
        super(world);
        ignoreFrustumCull = true;
        laserData = new LaserData(new Position(0, 0, 0), new Position(0, 0, 0));
        destination = new MutableBlockPos(0, 0, 0);
    }

    public RobotEntity(World world, AreaWorkerBlockEntity areaWorker){
        this(world);
        this.areaWorker = areaWorker;
        this.areaWorkerPosition = new BlockPos(areaWorker.x, areaWorker.y, areaWorker.z);
    }

    @Override
    public boolean interact(PlayerEntity player) {
        return true;
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(TARGET_X_KEY, 0);
        dataTracker.startTracking(TARGET_Y_KEY, 0);
        dataTracker.startTracking(TARGET_Z_KEY, 0);
        dataTracker.startTracking(LASER_TEXTURE_KEY, LaserRenderer.LASER_TEXTURES[0]);
    }

    @Override
    protected void readNbt(NbtCompound nbt) {
        if(nbt.contains("LaserTarget")){
            dataTracker.set(TARGET_X_KEY, nbt.getInt("LaserTargetX"));
            dataTracker.set(TARGET_Y_KEY, nbt.getInt("LaserTargetY"));
            dataTracker.set(TARGET_Z_KEY, nbt.getInt("LaserTargetZ"));
        }

        if(nbt.contains("AreaWorkerPosition")){
            areaWorkerPosition = StationBlockPos.fromLong(nbt.getLong("AreaWorkerPosition"));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putLong("LaserTargetX", dataTracker.getInt(TARGET_X_KEY));
        nbt.putLong("LaserTargetY", dataTracker.getInt(TARGET_Y_KEY));
        nbt.putLong("LaserTargetZ", dataTracker.getInt(TARGET_Z_KEY));

        if(areaWorkerPosition != null){
            nbt.putLong("AreaWorkerPosition", areaWorkerPosition.asLong());
        }
    }

    public int getLaserTargetX(){
        return dataTracker.getInt(TARGET_X_KEY);
    }

    public int getLaserTargetY(){
        return dataTracker.getInt(TARGET_Y_KEY);
    }

    public int getLaserTargetZ(){
        return dataTracker.getInt(TARGET_Z_KEY);
    }

    public void setLaserTarget(int x, int y, int z){
        dataTracker.set(TARGET_X_KEY, x);
        dataTracker.set(TARGET_Y_KEY, y);
        dataTracker.set(TARGET_Z_KEY, z);

        setDestination(x, y, z);
    }

    public void setLaserTexture(String texture){
        dataTracker.set(LASER_TEXTURE_KEY, texture);
    }

    public String getLaserTexture(){
        return dataTracker.getString(LASER_TEXTURE_KEY);
    }

    private void setDestination(int destX, int destY, int destZ){
        destination.set(
                Math.max(Math.min(destX, areaWorker.workingArea.maxX - 1), areaWorker.workingArea.minX + 1),
                Math.max(Math.min(destY, areaWorker.workingArea.maxY - 1), areaWorker.workingArea.minY + 1),
                Math.max(Math.min(destZ, areaWorker.workingArea.maxZ - 1), areaWorker.workingArea.minZ + 1)
        );

        velocityX = (destination.getX() - x) / 75 * 1;
        velocityY = (destination.getY() - y) / 75 * 1;
        velocityZ = (destination.getZ() - z) / 75 * 1;
    }

    private boolean reachedDestination(){
        return getDistance(destination.getX(), destination.getY(), destination.getZ()) < 0.2f;
    }

    @Override
    public void tick() {
        super.tick();
        laserData.iterateTexture();
        if(!world.isRemote){
            if(areaWorkerPosition != null && areaWorker == null){
                if(world.getBlockEntity(areaWorkerPosition.getX(), areaWorkerPosition.getY(), areaWorkerPosition.getZ()) instanceof AreaWorkerBlockEntity blockEntity){
                    areaWorker = blockEntity;
                }
            }
            move();
        }

        if(areaWorkerPosition == null || areaWorker.isRemoved() || areaWorker.robot != this){
            markDead();
        }
    }

    private void move(){
        if(!reachedDestination()){
            setPosition(x + velocityX, y + velocityY, z + velocityZ);
        }
    }

    @Override
    public boolean shouldRender(double distance) {
        return distance < 50000;
    }

    @Override
    public boolean shouldRender(Vec3d pos) {
        return true;
    }
}
