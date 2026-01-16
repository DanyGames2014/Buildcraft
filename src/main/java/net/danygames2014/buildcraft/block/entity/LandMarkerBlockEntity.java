package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.core.BlockEntityAreaProvider;
import net.danygames2014.buildcraft.api.core.LaserKind;
import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.entity.EntityBlock;
import net.danygames2014.buildcraft.util.LaserUtil;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LandMarkerBlockEntity extends BlockEntity implements BlockEntityAreaProvider {
    public static int MARKER_RANGE = 64;
    public static class TileWrapper implements Serializable {

        public int x, y, z;
        private LandMarkerBlockEntity marker;

        public TileWrapper() {
            x = Integer.MAX_VALUE;
            y = Integer.MAX_VALUE;
            z = Integer.MAX_VALUE;
        }

        public TileWrapper(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public boolean isSet() {
            return x != Integer.MAX_VALUE;
        }

        public LandMarkerBlockEntity getMarker(World world) {
            if (!isSet()) {
                return null;
            }

            if (marker == null) {
                if (world.getBlockEntity(x, y, z) instanceof LandMarkerBlockEntity blockEntity) {
                    marker = blockEntity;
                }
            }

            return marker;
        }

        public void reset() {
            x = Integer.MAX_VALUE;
            y = Integer.MAX_VALUE;
            z = Integer.MAX_VALUE;
        }

        @Override
        public void readData(DataInputStream stream) throws IOException {
            x = stream.readInt();
            if (isSet()) {
                y = stream.readShort();
                z = stream.readInt();
            }
        }

        @Override
        public void writeData(DataOutputStream stream) throws IOException {
            stream.writeInt(x);
            if (isSet()) {
                // Only X is used for checking if a vector is set, so we can save space on the Y coordinate.
                stream.writeShort(y);
                stream.writeInt(z);
            }
        }
    }

    public static class Origin implements Serializable {
        public TileWrapper vectO = new TileWrapper();
        public TileWrapper[] vect = {new TileWrapper(), new TileWrapper(), new TileWrapper()};
        public int xMin, yMin, zMin, xMax, yMax, zMax;

        public boolean isSet() {
            return vectO.isSet();
        }

        @Override
        public void writeData(DataOutputStream stream) throws IOException{
            vectO.writeData(stream);
            for (TileWrapper tw : vect) {
                tw.writeData(stream);
            }
            stream.writeInt(xMin);
            stream.writeShort(yMin);
            stream.writeInt(zMin);
            stream.writeInt(xMax);
            stream.writeShort(yMax);
            stream.writeInt(zMax);
        }

        @Override
        public void readData(DataInputStream stream) throws IOException{
            vectO.readData(stream);
            for (TileWrapper tw : vect) {
                tw.readData(stream);
            }
            xMin = stream.readInt();
            yMin = stream.readShort();
            zMin = stream.readInt();
            xMax = stream.readInt();
            yMax = stream.readShort();
            zMax = stream.readInt();
        }
    }

    public Origin origin = new Origin();
    public boolean showSignals = false;

    private Position initVectO;
    private Position[] initVect;
    private EntityBlock[] lasers;
    private EntityBlock[] signals;
    private boolean hasInit = false;

    public void updateSignals() {
        if (!world.isRemote) {
            showSignals = world.isPowered(x, y, z);
            //sendNetworkUpdate();
        }
    }

    private void switchSignals() {
        if (signals != null) {
            for (EntityBlock b : signals) {
                if (b != null) {
                    world.remove(b);
                }
            }
            signals = null;
        }
        if (showSignals) {
            signals = new EntityBlock[6];
            if (!origin.isSet() || !origin.vect[0].isSet()) {
                signals[0] = LaserUtil.createLaser(world, new Position(x, y, z), new Position(x + MARKER_RANGE - 1, y, z), LaserKind.BLUE);
                signals[1] = LaserUtil.createLaser(world, new Position(x - MARKER_RANGE + 1, y, z), new Position(x, y, z), LaserKind.BLUE);
            }

            if (!origin.isSet() || !origin.vect[1].isSet()) {
                signals[2] = LaserUtil.createLaser(world, new Position(x, y, z), new Position(x, y + MARKER_RANGE - 1, z), LaserKind.BLUE);
                signals[3] = LaserUtil.createLaser(world, new Position(x, y - MARKER_RANGE + 1, z), new Position(x, y, z), LaserKind.BLUE);
            }

            if (!origin.isSet() || !origin.vect[2].isSet()) {
                signals[4] = LaserUtil.createLaser(world, new Position(x, y, z), new Position(x, y, z + MARKER_RANGE - 1), LaserKind.BLUE);
                signals[5] = LaserUtil.createLaser(world, new Position(x, y, z - MARKER_RANGE + 1), new Position(x, y, z), LaserKind.BLUE);
            }
        }
    }

    public void init(){
        updateSignals();

        if (initVectO != null) {
            origin = new Origin();

            origin.vectO = new TileWrapper((int) initVectO.x, (int) initVectO.y, (int) initVectO.z);

            for (int i = 0; i < 3; ++i) {
                if (initVect[i] != null) {
                    linkTo((LandMarkerBlockEntity) world.getBlockEntity((int) initVect[i].x, (int) initVect[i].y, (int) initVect[i].z), i);
                }
            }
        }
    }

    public void tryConnection() {
        if (world.isRemote) {
            return;
        }

        for (int j = 0; j < 3; ++j) {
            if (!origin.isSet() || !origin.vect[j].isSet()) {
                setVect(j);
            }
        }

        //sendNetworkUpdate();
    }

    void setVect(int n) {
        int[] coords = new int[3];

        coords[0] = x;
        coords[1] = y;
        coords[2] = z;

        if (!origin.isSet() || !origin.vect[n].isSet()) {
            for (int j = 1; j < MARKER_RANGE; ++j) {
                coords[n] += j;

                Block block = world.getBlockState(coords[0], coords[1], coords[2]).getBlock();

                if (block == Buildcraft.landMarker) {
                    LandMarkerBlockEntity marker = (LandMarkerBlockEntity) world.getBlockEntity(coords[0], coords[1], coords[2]);

                    if (linkTo(marker, n)) {
                        break;
                    }
                }

                coords[n] -= j;
                coords[n] -= j;

                block = world.getBlockState(coords[0], coords[1], coords[2]).getBlock();

                if (block == Buildcraft.landMarker) {
                    LandMarkerBlockEntity marker = (LandMarkerBlockEntity) world.getBlockEntity(coords[0], coords[1], coords[2]);

                    if (linkTo(marker, n)) {
                        break;
                    }
                }

                coords[n] += j;
            }
        }
    }

    private boolean linkTo(LandMarkerBlockEntity marker, int n) {
        if (marker == null) {
            return false;
        }

        if (origin.isSet() && marker.origin.isSet()) {
            return false;
        }

        if (!origin.isSet() && !marker.origin.isSet()) {
            origin = new Origin();
            marker.origin = origin;
            origin.vectO = new TileWrapper(x, y, z);
            origin.vect[n] = new TileWrapper(marker.x, marker.y, marker.z);
        } else if (!origin.isSet()) {
            origin = marker.origin;
            origin.vect[n] = new TileWrapper(x, y, z);
        } else {
            marker.origin = origin;
            origin.vect[n] = new TileWrapper(marker.x, marker.y, marker.z);
        }

        origin.vectO.getMarker(world).createLasers();
        updateSignals();
        marker.updateSignals();

        return true;
    }

    private void createLasers() {
        if (lasers != null) {
            for (EntityBlock entity : lasers) {
                if (entity != null) {
                    world.remove(entity);
                }
            }
        }

        lasers = new EntityBlock[12];
        Origin o = origin;

        if (!origin.vect[0].isSet()) {
            o.xMin = origin.vectO.x;
            o.xMax = origin.vectO.x;
        } else if (origin.vect[0].x < x) {
            o.xMin = origin.vect[0].x;
            o.xMax = x;
        } else {
            o.xMin = x;
            o.xMax = origin.vect[0].x;
        }

        if (!origin.vect[1].isSet()) {
            o.yMin = origin.vectO.y;
            o.yMax = origin.vectO.y;
        } else if (origin.vect[1].y < y) {
            o.yMin = origin.vect[1].y;
            o.yMax = y;
        } else {
            o.yMin = y;
            o.yMax = origin.vect[1].y;
        }

        if (!origin.vect[2].isSet()) {
            o.zMin = origin.vectO.z;
            o.zMax = origin.vectO.z;
        } else if (origin.vect[2].z < z) {
            o.zMin = origin.vect[2].z;
            o.zMax = z;
        } else {
            o.zMin = z;
            o.zMax = origin.vect[2].z;
        }

        lasers = LaserUtil.createLaserBox(world, o.xMin, o.yMin, o.zMin, o.xMax, o.yMax, o.zMax, LaserKind.RED);
    }

    @Override
    public void tick() {
        super.tick();
        if(!hasInit){
            hasInit = true;
            init();
        }
    }

    @Override
    public boolean isValidFromLocation(int x, int y, int z) {
        // Rules:
        // - one or two, but not three, of the coordinates must be equal to the marker's location
        // - one of the coordinates must be either -1 or 1 away
        // - it must be physically touching the box
        // - however, it cannot be INSIDE the box
        int equal = (x == x ? 1 : 0) + (y == y ? 1 : 0) + (z == z ? 1 : 0);
        int touching = 0;

        if (equal == 0 || equal == 3) {
            return false;
        }

        if (x < (xMin() - 1) || x > (xMax() + 1) || y < (yMin() - 1) || y > (yMax() + 1)
                    || z < (zMin() - 1) || z > (zMax() + 1)) {
            return false;
        }

        if (x >= xMin() && x <= xMax() && y >= yMin() && y <= yMax() && z >= zMin() && z <= zMax()) {
            return false;
        }

        if (xMin() - x == 1 || x - xMax() == 1) {
            touching++;
        }

        if (yMin() - y == 1 || y - yMax() == 1) {
            touching++;
        }

        if (zMin() - z == 1 || z - zMax() == 1) {
            touching++;
        }

        return touching == 1;
    }

    @Override
    public int xMin() {
        if (origin.isSet()) {
            return origin.xMin;
        }
        return x;
    }

    @Override
    public int yMin() {
        if (origin.isSet()) {
            return origin.yMin;
        }
        return y;
    }

    @Override
    public int zMin() {
        if (origin.isSet()) {
            return origin.zMin;
        }
        return z;
    }

    @Override
    public int xMax() {
        if (origin.isSet()) {
            return origin.xMax;
        }
        return x;
    }

    @Override
    public int yMax() {
        if (origin.isSet()) {
            return origin.yMax;
        }
        return y;
    }

    @Override
    public int zMax() {
        if (origin.isSet()) {
            return origin.zMax;
        }
        return z;
    }

    public void destroy() {
        LandMarkerBlockEntity markerOrigin = null;

        if (origin.isSet()) {
            markerOrigin = origin.vectO.getMarker(world);

            Origin o = origin;

            if (markerOrigin != null && markerOrigin.lasers != null) {
                for (EntityBlock entity : markerOrigin.lasers) {
                    if (entity != null) {
                        entity.markDead();
                    }
                }
                markerOrigin.lasers = null;
            }

            for (TileWrapper m : o.vect) {
                LandMarkerBlockEntity mark = m.getMarker(world);

                if (mark != null) {
                    if (mark.lasers != null) {
                        for (EntityBlock entity : mark.lasers) {
                            if (entity != null) {
                                entity.markDead();
                            }
                        }
                        mark.lasers = null;
                    }

                    if (mark != this) {
                        mark.origin = new Origin();
                    }
                }
            }

            if (markerOrigin != this && markerOrigin != null) {
                markerOrigin.origin = new Origin();
            }

            for (TileWrapper wrapper : o.vect) {
                LandMarkerBlockEntity mark = wrapper.getMarker(world);

                if (mark != null) {
                    mark.updateSignals();
                }
            }
            if (markerOrigin != null) {
                markerOrigin.updateSignals();
            }
        }

        if (signals != null) {
            for (EntityBlock block : signals) {
                if (block != null) {
                    block.markDead();
                }
            }
        }

        signals = null;

        if (!world.isRemote && markerOrigin != null && markerOrigin != this) {
            //markerOrigin.sendNetworkUpdate();
        }
    }

    public void removeFromWorld() {
        if (!origin.isSet()) {
            return;
        }

        Origin o = origin;

        for (TileWrapper m : o.vect.clone()) {
            if (m.isSet()) {
                world.setBlock(m.x, m.y, m.z, 0);

                Buildcraft.landMarker.drop(world, m.x, m.y, m.z, world.getBlockState(m.x, m.y, m.z), world.getBlockMeta(m.x, m.y, m.z));
            }
        }

        world.setBlock(o.vectO.x, o.vectO.y, o.vectO.z, 0);

        Buildcraft.landMarker.drop(world, o.vectO.x, o.vectO.y, o.vectO.z, world.getBlockState(o.vectO.x, o.vectO.y, o.vectO.z), world.getBlockMeta(o.vectO.x, o.vectO.y, o.vectO.z));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("vectO")) {
            initVectO = new Position(nbt.getCompound("vectO"));
            initVect = new Position[3];

            for (int i = 0; i < 3; ++i) {
                if (nbt.contains("vect" + i)) {
                    initVect[i] = new Position(nbt.getCompound("vect" + i));
                }
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (origin.isSet() && origin.vectO.getMarker(world) == this) {
            NbtCompound vectO = new NbtCompound();

            new Position(origin.vectO.getMarker(world)).writeNBT(vectO);
            nbt.put("vectO", vectO);

            for (int i = 0; i < 3; ++i) {
                if (origin.vect[i].isSet()) {
                    NbtCompound vect = new NbtCompound();
                    new Position(origin.vect[i].x, origin.vect[i].y, origin.vect[i].z).writeNBT(vect);
                    nbt.put("vect" + i, vect);
                }
            }
        }
    }
}
