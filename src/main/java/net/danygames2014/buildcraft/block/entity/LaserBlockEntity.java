package net.danygames2014.buildcraft.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.danygames2014.buildcraft.api.blockentity.Controllable;
import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.api.core.SafeTimeTracker;
import net.danygames2014.buildcraft.api.energy.ILaserTarget;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.entity.pipe.LaserData;
import net.danygames2014.buildcraft.client.render.LaserRenderer;
import net.danygames2014.buildcraft.registry.ControlModeRegistry;
import net.danygames2014.nyalib.block.BlockEntityInit;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.LinkedList;
import java.util.List;

public class LaserBlockEntity extends BlockEntity implements IPowerReceptor, Controllable, BlockEntityInit {
    private static final float LASER_OFFSET = 2.0F / 16.0F;
    private static final short POWER_AVERAGING = 100;

    public LaserData laser = new LaserData();

    private final SafeTimeTracker laserTickTracker = new SafeTimeTracker(10);
    private final SafeTimeTracker searchTracker = new SafeTimeTracker(100, 100);
    private final SafeTimeTracker networkTracker = new SafeTimeTracker(3);

    private int powerIndex = 0;

    private double powerAverage = 0;
    private final double[] power = new double[POWER_AVERAGING];

    private ILaserTarget laserTarget;
    public PowerHandler powerHandler;
    protected ControlMode mode = ControlMode.ON;
    private static final ObjectArrayList<ControlMode> SUPPORTED_CONTROL_MODES = new ObjectArrayList<>() {
        {
            add(ControlMode.ON);
            add(ControlMode.OFF);
        }
    }; 

    public LaserBlockEntity(){
        powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);
        initPowerProvider();
    }

    private void initPowerProvider() {
        powerHandler.configure(25, 200, 25, 1000);
    }

    public void init(BlockState blockState){
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

        laser.iterateTexture();

        if (world.isRemote)
            return;

        // If a gate disabled us, remove laser and do nothing.
        if (mode == ControlMode.OFF) {
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
        return laserTarget != null && !laserTarget.isInvalidTarget() && laserTarget.requiresLaserEnergy();
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

        if (avg <= 1.0) {
            return LaserRenderer.LASER_TEXTURES[0];
        } else if (avg <= 2.0) {
            return LaserRenderer.LASER_TEXTURES[1];
        } else if (avg <= 3.0) {
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
        mode = ControlModeRegistry.get(Identifier.of(nbt.getString("mode")));
        initPowerProvider();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        powerHandler.writeToNBT(nbt);
        nbt.putString("mode", mode.identifier.toString());
    }

    @Override
    public ControlMode getControlMode() {
        return mode;
    }

    @Override
    public void setControlMode(ControlMode mode) {
        this.mode = mode;
    }

    @Override
    public ObjectArrayList<ControlMode> getSupportedControlModes() {
        return SUPPORTED_CONTROL_MODES;
    }
}
