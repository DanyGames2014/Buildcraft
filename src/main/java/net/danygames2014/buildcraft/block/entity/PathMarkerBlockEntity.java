package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.core.BlockIndex;
import net.danygames2014.buildcraft.api.core.PathProvider;
import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.block.entity.pipe.LaserData;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class PathMarkerBlockEntity extends LandMarkerBlockEntity implements PathProvider {
    private static ArrayList<PathMarkerBlockEntity> availableMarkers = new ArrayList<PathMarkerBlockEntity>();

    public int x0, y0, z0, x1, y1, z1;
    public boolean loadLink0 = false;
    public boolean loadLink1 = false;

    public LaserData[] lasers = new LaserData[2];
    public boolean tryingToConnect = false;

    public PathMarkerBlockEntity[] links = new PathMarkerBlockEntity[2];

    public boolean isFullyConnected() {
        return lasers[0] != null && lasers[1] != null;
    }

    public boolean isLinkedTo(PathMarkerBlockEntity pathMarker) {
        return links[0] == pathMarker || links[1] == pathMarker;
    }

    public PathMarkerBlockEntity(){}

    public void connect(PathMarkerBlockEntity marker, LaserData laser) {
        if (lasers[0] == null) {
            lasers[0] = laser;
            links[0] = marker;
        } else if (lasers[1] == null) {
            lasers[1] = laser;
            links[1] = marker;
        }

        if (isFullyConnected()) {
            availableMarkers.remove(this);
        }
    }

    public void createLaserAndConnect(PathMarkerBlockEntity pathMarker) {
        if (world.isRemote) {
            return;
        }

        LaserData laser = new LaserData
                                  (new Position(x + 0.5, y + 0.5, z + 0.5),
                                          new Position(pathMarker.x + 0.5, pathMarker.y + 0.5, pathMarker.z + 0.5));

        LaserData laser2 = new LaserData (laser.head, laser.tail);
        laser2.isVisible = false;

        connect(pathMarker, laser);
        pathMarker.connect(this, laser2);
    }

    /**
     * Searches the availableMarkers list for the nearest available that is
     * within searchSize
     */
    private PathMarkerBlockEntity findNearestAvailablePathMarker() {
        PathMarkerBlockEntity nearestAvailable = null;
        double nearestDistance = 0, distance;

        for (PathMarkerBlockEntity t : availableMarkers) {
            if (t == this || t == this.links[0] || t == this.links[1] || t.world.dimension.id != this.world.dimension.id) {
                continue;
            }

            int dx = this.x - t.x;
            int dy = this.y - t.y;
            int dz = this.z - t.z;
            distance = dx * dx + dy * dy + dz * dz;

            if (distance > LandMarkerBlockEntity.MARKER_RANGE * LandMarkerBlockEntity.MARKER_RANGE) {
                continue;
            }

            if (nearestAvailable == null || distance < nearestDistance) {
                nearestAvailable = t;
                nearestDistance = distance;
            }
        }

        return nearestAvailable;
    }

    @Override
    public void tryConnection() {

        if (world.isRemote || isFullyConnected()) {
            return;
        }

        // Allow the user to stop the path marker from searching for new path markers to connect
        tryingToConnect = !tryingToConnect;

        //sendNetworkUpdate();
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isRemote) {
            return;
        }

        if (tryingToConnect) {
            PathMarkerBlockEntity nearestPathMarker = findNearestAvailablePathMarker();

            if (nearestPathMarker != null) {
                createLaserAndConnect(nearestPathMarker);
            }

            tryingToConnect = false;

            //sendNetworkUpdate();
            world.setBlocksDirty(x, y, z,
                    x, y, z);
        }
    }

    @Override
    public List<BlockIndex> getPath() {
        HashSet<BlockIndex> visitedPaths = new HashSet<BlockIndex>();
        ArrayList<BlockIndex> res = new ArrayList<BlockIndex>();

        PathMarkerBlockEntity nextTile = this;

        while (nextTile != null) {
            BlockIndex b = new BlockIndex(nextTile.x, nextTile.y, nextTile.z);

            visitedPaths.add(b);
            res.add(b);

            if (nextTile.links[0] != null
                        && !visitedPaths.contains(new BlockIndex(nextTile.links[0].x, nextTile.links[0].y, nextTile.links[0].z))) {
                nextTile = nextTile.links[0];
            } else if (nextTile.links[1] != null
                               && !visitedPaths.contains(new BlockIndex(nextTile.links[1].x, nextTile.links[1].y, nextTile.links[1].z))) {
                nextTile = nextTile.links[1];
            } else {
                nextTile = null;
            }
        }

        return res;
    }

    @Override
    public void init(BlockState blockState) {
        super.init(blockState);
        if (!world.isRemote && !isFullyConnected()) {
            availableMarkers.add(this);
        }

        if (loadLink0) {
            BlockEntity e0 = world.getBlockEntity(x0, y0, z0);

            if (links[0] != e0 && links[1] != e0 && e0 instanceof PathMarkerBlockEntity pathMarkerBlockEntity) {
                createLaserAndConnect(pathMarkerBlockEntity);
            }

            loadLink0 = false;
        }

        if (loadLink1) {
            BlockEntity e1 = world.getBlockEntity(x1, y1, z1);

            if (links[0] != e1 && links[1] != e1 && e1 instanceof PathMarkerBlockEntity pathMarkerBlockEntity) {
                createLaserAndConnect(pathMarkerBlockEntity);
            }

            loadLink1 = false;
        }

        //sendNetworkUpdate();
    }

    private void unlink(PathMarkerBlockEntity tile) {
        if (links[0] == tile) {
            lasers[0] = null;
            links[0] = null;
        }

        if (links[1] == tile) {
            lasers[1] = null;
            links[1] = null;
        }

        if (!isFullyConnected() && !availableMarkers.contains(this) && !world.isRemote) {
            availableMarkers.add(this);
        }

        //sendNetworkUpdate();
    }

    @Override
    public void markRemoved() {
        if(!isRemoved()){
            availableMarkers.remove(this);
        }
        super.markRemoved();
    }

    public static void clearAvailableMarkersList() {
        availableMarkers.clear();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("x0")) {
            x0 = nbt.getInt("x0");
            y0 = nbt.getInt("y0");
            z0 = nbt.getInt("z0");

            loadLink0 = true;
        }

        if (nbt.contains("x1")) {
            x1 = nbt.getInt("x1");
            y1 = nbt.getInt("y1");
            z1 = nbt.getInt("z1");

            loadLink1 = true;
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (links[0] != null) {
            nbt.putInt("x0", links[0].x);
            nbt.putInt("y0", links[0].y);
            nbt.putInt("z0", links[0].z);
        }

        if (links[1] != null) {
            nbt.putInt("x1", links[1].x);
            nbt.putInt("y1", links[1].y);
            nbt.putInt("z1", links[1].z);
        }
    }

    public static void clearAvailableMarkersList(World w) {
        for (Iterator<PathMarkerBlockEntity> it = availableMarkers.iterator(); it.hasNext();) {
            PathMarkerBlockEntity t = it.next();
            if (t.world.dimension.id != w.dimension.id) {
                it.remove();
            }
        }
    }
}
