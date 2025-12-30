package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.block.entity.pipe.LaserData;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;

public abstract class AreaWorkerBlockEntity extends BlockEntity {
    public WorkingArea workingArea;
    public int minHeight = 1;
    
    public AreaWorkerBlockEntity() {
    }
    
    public boolean renderWorkingArea() {
        return true;
    }
    
    public void constructWorkingArea(LandMarkerBlockEntity.Origin origin) {
        this.constructWorkingArea(origin.xMin, origin.yMin, origin.zMin, origin.xMax, origin.yMax, origin.zMax);
    }
    
    public void constructWorkingArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        workingArea = new WorkingArea(this, minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    public void destroyWorkingArea() {
        workingArea = null;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        
        if (workingArea != null) {
            NbtCompound workingAreaNbt = new NbtCompound();
            workingArea.writeNbt(workingAreaNbt);
            nbt.put("workingArea", workingAreaNbt);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        
        if (nbt.contains("workingArea")) {
            workingArea = new WorkingArea();
            workingArea.readNbt(nbt.getCompound("workingArea"));
        }
    }

    public static class WorkingArea {
        public int minX;
        public int minY;
        public int minZ;
        public int maxX;
        public int maxY;
        public int maxZ;
        public ArrayList<LaserData> lasers = new ArrayList<>();

        public WorkingArea() {
        }

        public WorkingArea(AreaWorkerBlockEntity areaWorker, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;

            if (this.minY + (areaWorker.minHeight - 1) > this.maxY) {
                this.maxY = this.minY + (areaWorker.minHeight - 1);
            }
            
            constructLasers();
        }
        
        public void constructLasers() {
            // Bottom Frame
            addLaser(minX, minY, minZ, maxX, minY, minZ);
            addLaser(minX, minY, minZ, minX, minY, maxZ);
            addLaser(maxX, minY, minZ, maxX, minY, maxZ);
            addLaser(minX, minY, maxZ, maxX, minY, maxZ);

            if (minY != maxY) {
                // Pillars
                addLaser(minX, minY, minZ, minX, maxY, minZ);
                addLaser(maxX, minY, minZ, maxX, maxY, minZ);
                addLaser(minX, minY, maxZ, minX, maxY, maxZ);
                addLaser(maxX, minY, maxZ, maxX, maxY, maxZ);

                // Top Frame
                addLaser(minX, maxY, minZ, maxX, maxY, minZ);
                addLaser(minX, maxY, minZ, minX, maxY, maxZ);
                addLaser(maxX, maxY, minZ, maxX, maxY, maxZ);
                addLaser(minX, maxY, maxZ, maxX, maxY, maxZ);
            }
        }

        public void addLaser(int startX, int startY, int startZ, int endX, int endY, int endZ) {
            lasers.add(new LaserData(new Position(startX, startY, startZ), new Position(endX, endY, endZ)));
        }
        
        public void writeNbt(NbtCompound nbt) {
            nbt.putInt("minX", minX);
            nbt.putInt("minY", minY);
            nbt.putInt("minZ", minZ);
            nbt.putInt("maxX", maxX);
            nbt.putInt("maxY", maxY);
            nbt.putInt("maxZ", maxZ);
        }
        
        public void readNbt(NbtCompound nbt) {
            minX = nbt.getInt("minX");
            minY = nbt.getInt("minY");
            minZ = nbt.getInt("minZ");
            maxX = nbt.getInt("maxX");
            maxY = nbt.getInt("maxY");
            maxZ = nbt.getInt("maxZ");
            
            constructLasers();
        }

        public int sizeX() {
            return maxX - minX + 1;
        }

        public int sizeY() {
            return maxY - minY + 1;
        }

        public int sizeZ() {
            return maxZ - minZ + 1;
        }
    }
}
