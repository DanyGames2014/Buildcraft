package net.danygames2014.buildcraft.entity;

import net.danygames2014.buildcraft.block.entity.QuarryBlockEntity;
import net.danygames2014.buildcraft.init.TextureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class MechanicalArmEntity extends Entity {
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
        xArm = newDrill(world, 0, 0, 0, 1, 0.5, 0.5);
        yArm = newDrill(world, 0, 0, 0, 0.5, 1, 0.5);
        zArm = newDrill(world, 0, 0, 0, 0.5, 0.5, 1);

        head = newDrillHead(world, 0, 0, 0, 0.2, 1, 0.2);
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
        this.yArm.ySize = yRoot - headT[1] - 1;
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

    void setHead(double x, double y, double z) {
        this.headX = (int) (x * 32D);
        this.headY = (int) (y * 32D);
        this.headZ = (int) (z * 32D);
    }

    private void setArmSize(double x, double z) {
        armSizeX = x;
        xArm.xSize = x;
        armSizeZ = z;
        zArm.zSize = z;
        updatePosition();
    }

    @SuppressWarnings("SameParameterValue")
    private EntityBlockWithParent newDrill(World w, double x, double y, double z, double sizeX, double sizeY, double sizeZ){
        EntityBlockWithParent entityBlock = new EntityBlockWithParent(w, x, y, z, sizeX, sizeY, sizeZ, this);
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            entityBlock.texture = TextureListener.drill.index;
        }
        return entityBlock;
    }

    @SuppressWarnings("SameParameterValue")
    private EntityBlockWithParent newDrillHead(World w, double x, double y, double z, double sizeX, double sizeY, double sizeZ){
        EntityBlockWithParent entityBlock = new EntityBlockWithParent(w, x, y, z, sizeX, sizeY, sizeZ, this);
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            entityBlock.texture = TextureListener.drillHead.index;
        }
        return entityBlock;
    }

    @Override
    protected boolean pushOutOfBlock(double x, double y, double z) {
        return false;
    }
}
