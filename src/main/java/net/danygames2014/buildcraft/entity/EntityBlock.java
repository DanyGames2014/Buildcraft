package net.danygames2014.buildcraft.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTrackerEntry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.server.entity.EntitySpawnDataProvider;
import net.modificationstation.stationapi.api.server.entity.HasTrackingParameters;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.TriState;

// TODO: Make this work properly on server
@HasTrackingParameters(trackingDistance = 32, updatePeriod = 1, sendVelocity = TriState.TRUE)
public class EntityBlock extends Entity implements EntitySpawnDataProvider {
    public float shadowSize = 0;
    public float pitch = 0;
    public float yaw = 0;
    public float roll = 0;
    private static final int SIZE_X_KEY = 1;
    private static final int SIZE_Y_KEY = 2;
    private static final int SIZE_Z_KEY = 3;
    private static final int TEXTURE_KEY = 4;
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

    public EntityBlock(World world, double x, double y, double z, float xSize, float ySize, float zSize){
        this(world);
        setXSize(xSize);
        setYSize(ySize);
        setZSize(zSize);
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

        boundingBox.maxX = x + getXSize();
        boundingBox.maxY = y + getYSize();
        boundingBox.maxZ = z + getZSize();
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
        this.dataTracker.startTracking(SIZE_X_KEY, 0f);
        this.dataTracker.startTracking(SIZE_Y_KEY, 0f);
        this.dataTracker.startTracking(SIZE_Z_KEY, 0f);
        this.dataTracker.startTracking(TEXTURE_KEY, "");
    }

    public void setXSize(float size){
        this.dataTracker.set(SIZE_X_KEY, size);
    }
    public void setYSize(float size){
        this.dataTracker.set(SIZE_Y_KEY, size);
    }
    public void setZSize(float size){
        this.dataTracker.set(SIZE_Z_KEY, size);
    }

    public void setTextureIdentifier(Identifier identifier){
        this.dataTracker.set(TEXTURE_KEY, identifier.toString());
    }

    public float getXSize(){
        if(dataTracker.entries.get(SIZE_X_KEY) == null) return 0f;
        return (Float) ((DataTrackerEntry)dataTracker.entries.get(SIZE_X_KEY)).get();
    }
    public float getYSize(){
        if(dataTracker.entries.get(SIZE_Y_KEY) == null) return 0f;
        return (Float) ((DataTrackerEntry)dataTracker.entries.get(SIZE_Y_KEY)).get();
    }
    public float getZSize(){
        if(dataTracker.entries.get(SIZE_Z_KEY) == null) return 0f;
        return (Float) ((DataTrackerEntry)dataTracker.entries.get(SIZE_Z_KEY)).get();
    }

    public Identifier getTextureIdentifier(){
        if(dataTracker.entries.get(SIZE_Z_KEY) == null) return null;
        String id = dataTracker.getString(TEXTURE_KEY);
        if(id == null) return null;
        return Identifier.of(id);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
    }

    @Override
    protected void readNbt(NbtCompound nbt) {
    }

    @Override
    public boolean saveSelfNbt(NbtCompound nbt) {
        return false;
    }

    @Override
    public void write(NbtCompound nbt) {
    }

    @Override
    public void read(NbtCompound nbt) {
    }

    @Override
    public Identifier getHandlerIdentifier() {
        return Buildcraft.NAMESPACE.id("entity_block");
    }
}
