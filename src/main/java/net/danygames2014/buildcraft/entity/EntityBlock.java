package net.danygames2014.buildcraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class EntityBlock extends Entity {

    public int texture;
    public float shadowSize = 0;
    public float pitch = 0;
    public float yaw = 0;
    public float roll = 0;
    public double xSize, ySize, zSize;
    private int brightness = -1;

    public EntityBlock(World world){
        super(world);
        noClip = true;
        fireImmune = true;
    }

    public EntityBlock(World world, double x, double y, double z){
        super(world);
        setPositionAndAngles(x, y, z, 0, 0);
    }

    public EntityBlock(World world, double x, double y, double z, double xSize, double ySize, double zSize){
        this(world);
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        setPositionAndAngles(x, y, z, 0, 0);
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
        boundingBox.minX = this.x;
        boundingBox.minY = this.y;
        boundingBox.minZ = this.z;

        boundingBox.maxX = x + this.xSize;
        boundingBox.maxY = y + this.ySize;
        boundingBox.maxZ = z + this.zSize;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        setPosition(x + dx, y + dy, z + dz);
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    @Override
    public float getBrightnessAtEyes(float tickDelta) {
        return brightness > 0 ? brightness : super.getBrightnessAtEyes(tickDelta);
    }

    @Override
    public boolean shouldRender(double distance) {
        return distance < 50000;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readNbt(NbtCompound nbt) {
        nbt.putDouble("x_size", xSize);
        nbt.putDouble("y_size", ySize);
        nbt.putDouble("z_size", zSize);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        xSize = nbt.getDouble("x_size");
        ySize = nbt.getDouble("y_size");
        zSize = nbt.getDouble("z_size");
    }
}
