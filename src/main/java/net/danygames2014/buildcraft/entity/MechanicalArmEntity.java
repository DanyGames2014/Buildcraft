package net.danygames2014.buildcraft.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.QuarryBlockEntity;
import net.danygames2014.buildcraft.init.TextureListener;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.server.entity.EntitySpawnDataProvider;
import net.modificationstation.stationapi.api.server.entity.HasTrackingParameters;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.TriState;

@HasTrackingParameters(trackingDistance = 32, updatePeriod = 1, sendVelocity = TriState.TRUE)
public class MechanicalArmEntity extends Entity implements EntitySpawnDataProvider {
    protected QuarryBlockEntity parent;

    private double armSizeX;
    private double armSizeZ;
    private double xRoot;
    private double yRoot;
    private double zRoot;

    private int headX, headY, headZ;
    private EntityBlockWithParent xArm, yArm, zArm, head;

    private boolean initialized = false;

    public MechanicalArmEntity(World world) {
        super(world);
        makeParts(world);
        noClip = true;
    }

    public MechanicalArmEntity(World world, double x, double y, double z, double width, double height, QuarryBlockEntity parent){
        this(world);
        setPositionAndAngles(parent.x, parent.y, parent.z, 0, 0);
        this.xRoot = x;
        this.yRoot = y;
        this.zRoot = z;
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;
        setArmSize(width, height);
        setHead(x, y - 2, z);
        this.parent = parent;
        parent.setArm(this);
        updatePosition();
    }

    public MechanicalArmEntity(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readNbt(NbtCompound nbt) {
        xRoot = nbt.getDouble("xRoot");
        yRoot = nbt.getDouble("yRoot");
        zRoot = nbt.getDouble("zRoot");
        armSizeX = nbt.getDouble("armSizeX");
        armSizeZ = nbt.getDouble("armSizeZ");
        setArmSize(armSizeX, armSizeZ);
        updatePosition();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putDouble("xRoot", xRoot);
        nbt.putDouble("yRoot", yRoot);
        nbt.putDouble("zRoot", zRoot);
        nbt.putDouble("armSizeX", armSizeX);
        nbt.putDouble("armSizeZ", armSizeZ);
    }

    @Override
    public void tick() {
        if(!initialized && parent != null) {
            setPositionAndAngles(parent.x, parent.y, parent.z, 0, 0);
            setArmSize(armSizeX, armSizeZ);
            setHead(xRoot, yRoot - 2, zRoot);
            updatePosition();
            world.spawnEntity(xArm);
            world.spawnEntity(yArm);
            world.spawnEntity(zArm);
            world.spawnEntity(head);
            initialized = true;
        }
        super.tick();

        updatePosition();
        if (parent == null) {
            findAndJoinQuarry();
        }

        if (parent == null || parent.isRemoved()) {
            markDead();
        }
    }

    private void makeParts(World world) {
        xArm = newDrill(world, 0, 0, 0, 1f, 0.5f, 0.5f);
        yArm = newDrill(world, 0, 0, 0, 0.5f, 1, 0.5f);
        zArm = newDrill(world, 0, 0, 0, 0.5f, 0.5f, 1);

        head = newDrillHead(world, 0, 0, 0, 0.2f, 1, 0.2f);
        head.shadowSize = 1.0F;
    }

    private void findAndJoinQuarry() {
        if (world.getBlockEntity((int) x, (int) y, (int) z) instanceof QuarryBlockEntity quarryBlockEntity) {
            parent = quarryBlockEntity;
            parent.setArm(this);
        } else {
            markDead();
        }
    }

    public void updatePosition() {
        double[] headT = getHead();
        this.xArm.setPosition(xRoot, yRoot, headT[2] + 0.25);
        this.yArm.setYSize((float) (yRoot - headT[1] - 1));
        this.yArm.setPosition(headT[0] + 0.25, headT[1] + 1, headT[2] + 0.25);
        this.zArm.setPosition(headT[0] + 0.25, yRoot, zRoot);
        this.head.setPosition(headT[0] + 0.4, headT[1], headT[2] + 0.4);
    }

    @Override
    public void markDead() {
        if (world != null && world.isRemote) {
            xArm.markDead();
            yArm.markDead();
            zArm.markDead();
            head.markDead();
        }
        super.markDead();
    }

    private double[] getHead() {
        return new double[] { this.headX / 32D, this.headY / 32D, this.headZ / 32D };
    }

    public void setHead(double x, double y, double z) {
        this.headX = (int) (x * 32D);
        this.headY = (int) (y * 32D);
        this.headZ = (int) (z * 32D);
    }

    private void setArmSize(double x, double z) {
        armSizeX = x;
        xArm.setXSize((float) x);
        armSizeZ = z;
        zArm.setZSize((float) z);
        updatePosition();
    }

    @SuppressWarnings("SameParameterValue")
    private EntityBlockWithParent newDrill(World w, double x, double y, double z, float sizeX, float sizeY, float sizeZ){
        EntityBlockWithParent entityBlock = new EntityBlockWithParent(w, x, y, z, sizeX, sizeY, sizeZ, this);
        entityBlock.setTextureIdentifier(TextureListener.drill);
        return entityBlock;
    }

    @SuppressWarnings("SameParameterValue")
    private EntityBlockWithParent newDrillHead(World w, double x, double y, double z, float sizeX, float sizeY, float sizeZ){
        EntityBlockWithParent entityBlock = new EntityBlockWithParent(w, x, y, z, sizeX, sizeY, sizeZ, this);
        entityBlock.setTextureIdentifier(TextureListener.drillHead);
        return entityBlock;
    }

    @Override
    protected boolean pushOutOfBlock(double x, double y, double z) {
        return false;
    }

    @Override
    public Identifier getHandlerIdentifier() {
        return Buildcraft.NAMESPACE.id("mechanical_arm");
    }
}
