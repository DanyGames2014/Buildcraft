package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.blockentity.Controllable;
import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.api.core.SafeTimeTracker;
import net.danygames2014.buildcraft.api.energy.ILaserTarget;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.api.transport.statement.ActionReceptor;
import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.block.entity.pipe.LaserData;
import net.danygames2014.buildcraft.client.render.LaserRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.LinkedList;
import java.util.List;

public class LaserBlockEntity extends BlockEntity implements IPowerReceptor, Controllable {
    private static final float LASER_OFFSET = 2.0F / 16.0F;
    private static final short POWER_AVERAGING = 100;

    public LaserData laser = new LaserData();

    private boolean hasInit = false;

    private final SafeTimeTracker laserTickTracker = new SafeTimeTracker(10);
    private final SafeTimeTracker searchTracker = new SafeTimeTracker(100, 100);
    private final SafeTimeTracker networkTracker = new SafeTimeTracker(3);

    private int powerIndex = 0;

    private double powerAverage = 0;
    private final double[] power = new double[POWER_AVERAGING];

    private ILaserTarget laserTarget;
    protected PowerHandler powerHandler;
    protected Controllable.Mode mode = Mode.On;
    private static final PowerHandler.PerditionCalculator PERDITION = new PowerHandler.PerditionCalculator(0.5F);

    public LaserBlockEntity(){
        powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);
        initPowerProvider();
    }

    private void initPowerProvider() {
        powerHandler.configure(25, 150, 25, 1000);
        powerHandler.setPerdition(PERDITION);
    }

    public void init(){
        if (laser == null) {
            laser = new LaserData();
        }

        laser.isVisible = false;
        laser.head = new Position(x, y, z);
        laser.tail = new Position(x, y, z);
        laser.isGlowing = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isRemote)
            return;

        if(!hasInit){
            init();
            hasInit = true;
        }

        // If a gate disabled us, remove laser and do nothing.
        if (mode == Controllable.Mode.Off) {
            removeLaser();
            return;
        }

        // Check for any available tables at a regular basis
        if (canFindTable()) {
            findTable();
        }

        // If we still don't have a valid table or the existing has
        // become invalid, we disable the laser and do nothing.
        if (!isValidTable()) {
            removeLaser();
            return;
        }

        // Disable the laser and do nothing if no energy is available.
        if (powerHandler.getEnergyStored() == 0) {
            removeLaser();
            return;
        }

//        // We have a table and can work, so we create a laser if
//        // necessary.
//        if (laser == null) {
//            createLaser();
//        }

        // We have a laser and may update it
        if (laser != null) {
            laser.isVisible = true;
            if(canUpdateLaser()){
                updateLaser();
            }
        }

        // Consume power and transfer it to the table.
        double power = powerHandler.useEnergy(0, getMaxPowerSent(), true);
        laserTarget.receiveLaserEnergy(power);

        if (laser != null) {
            pushPower(power);
        }

        onPowerSent(power);

        //sendNetworkUpdate();
    }

    private void pushPower(double received) {
        powerAverage -= power[powerIndex];
        powerAverage += received;
        power[powerIndex] = (short) received;
        powerIndex++;

        if (powerIndex == power.length) {
            powerIndex = 0;
        }
    }

    protected void removeLaser() {
        if (laser.isVisible) {
            laser.isVisible = false;
            // force sending the network update even if the network tracker
            // refuses.
            //super.sendNetworkUpdate();
        }
    }

    protected float getMaxPowerSent() {
        return 4;
    }

    protected void onPowerSent(double power) {
    }

    protected boolean canFindTable() {
        return searchTracker.markTimeIfDelay(world);
    }

    protected boolean canUpdateLaser() {
        return laserTickTracker.markTimeIfDelay(world);
    }

    @Override
    public PowerHandler.PowerReceiver getPowerReceiver(Direction side) {
        return powerHandler.getPowerReceiver();
    }

    protected boolean isValidTable() {

        if (laserTarget == null || laserTarget.isInvalidTarget() || !laserTarget.requiresLaserEnergy())
            return false;

        return true;
    }

    private Direction getFacing(){
        BlockState blockState = world.getBlockState(x, y, z);
        if(blockState.contains(Properties.FACING)){
            return blockState.get(Properties.FACING);
        }
        return Direction.UP;
    }

    protected void findTable() {
        int minX = x - 5;
        int minY = y - 5;
        int minZ = z - 5;
        int maxX = x + 5;
        int maxY = y + 5;
        int maxZ = z + 5;

        switch (getFacing().rotateCounterclockwise(Direction.Axis.Y)) {
            case WEST:
                maxX = x;
                break;
            case EAST:
                minX = x;
                break;
            case DOWN:
                maxY = y;
                break;
            case UP:
                minY = y;
                break;
            case NORTH:
                maxZ = z;
                break;
            default:
            case SOUTH:
                minZ = z;
                break;
        }

        List<ILaserTarget> targets = new LinkedList<>();

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {

                    BlockEntity blockEntity = world.getBlockEntity(x, y, z);
                    if (blockEntity instanceof ILaserTarget table) {

                        if (table.requiresLaserEnergy()) {
                            targets.add(table);
                        }
                    }

                }
            }
        }

        if (targets.isEmpty())
            return;

        laserTarget = targets.get(world.random.nextInt(targets.size()));
    }

    @Override
    public void doWork(PowerHandler workProvider) {

    }

    protected void updateLaser() {

        double px = 0, py = 0, pz = 0;

        switch (getFacing().rotateCounterclockwise(Direction.Axis.Y)) {

            case WEST:
                px = -LASER_OFFSET;
                break;
            case EAST:
                px = LASER_OFFSET;
                break;
            case DOWN:
                py = -LASER_OFFSET;
                break;
            case UP:
                py = LASER_OFFSET;
                break;
            case NORTH:
                pz = -LASER_OFFSET;
                break;
            case SOUTH:
            default:
                pz = LASER_OFFSET;
                break;
        }

        Position head = new Position(x + 0.5 + px, y + 0.5 + py, z + 0.5 + pz);
        Position tail = new Position(laserTarget.getXCoord() + 0.475 + (world.random.nextFloat() - 0.5) / 5F, laserTarget.getYCoord() + 9F / 16F,
                laserTarget.getZCoord() + 0.475 + (world.random.nextFloat() - 0.5) / 5F);

        laser.head = head;
        laser.tail = tail;

        if (!laser.isVisible) {
            laser.isVisible = true;
        }
    }

    public String getTexture() {
        double avg = powerAverage / POWER_AVERAGING;

        if (avg <= 10.0) {
            return LaserRenderer.LASER_TEXTURES[0];
        } else if (avg <= 20.0) {
            return LaserRenderer.LASER_TEXTURES[1];
        } else if (avg <= 30.0) {
            return LaserRenderer.LASER_TEXTURES[2];
        } else {
            return LaserRenderer.LASER_TEXTURES[3];
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        powerHandler.readFromNBT(nbt);
        mode = Mode.values()[nbt.getByte("mode")];
        initPowerProvider();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        powerHandler.writeToNBT(nbt);
        nbt.putByte("mode", (byte) mode.ordinal());
    }

    @Override
    public Mode getControlMode() {
        return mode;
    }

    @Override
    public void setControlMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public boolean acceptsControlMode(Mode mode) {
        return mode == Controllable.Mode.On || mode == Controllable.Mode.Off;
    }
}
