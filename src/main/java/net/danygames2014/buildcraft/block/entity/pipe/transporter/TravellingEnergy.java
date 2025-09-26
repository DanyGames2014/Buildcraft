package net.danygames2014.buildcraft.block.entity.pipe.transporter;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public class TravellingEnergy {
    public static final int DEFAULT_TRANSFER_DELAY = 1;
    
    public EnergyPipeTransporter transporter;
    public Direction input = null;
    public Direction travelDirection = null;
    public Direction lastTravelDirection = null;
    public double transferDelay = DEFAULT_TRANSFER_DELAY;
    public double lastTransferDelay = 0;
    public int invalidTimer = 0;
    boolean invalid = false;
    public TravelStage stage = TravelStage.ENTER;
    
    public int energyAmount;
    public World world;
    
    public TravellingEnergy(World world, EnergyPipeTransporter transporter) {
        this.world = world;
        this.transporter = transporter;
    }
    
    public void tick() {
        // Last
        lastTravelDirection = travelDirection;
        lastTransferDelay = transferDelay;

        // Invalid timer
        if (travelDirection == null) {
            invalidTimer++;
        }

        if (invalidTimer >= 50) {
            invalid = true;
        }
    }

    // NBT
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("input", input.getId());
        nbt.putInt("travelDirection", travelDirection.getId());
        nbt.putDouble("transferDelay", transferDelay);
        nbt.putDouble("lastTransferDelay", lastTransferDelay);
        nbt.putInt("stage", stage.ordinal());
    }

    public void read(NbtCompound nbt) {
        input = Direction.byId(nbt.getInt("input"));
        travelDirection = Direction.byId(nbt.getInt("travelDirection"));
        transferDelay = nbt.getDouble("transferDelay");
        lastTransferDelay = nbt.getDouble("lastTransferDelay");
        stage = TravelStage.values()[nbt.getInt("stage")];
    }

    public enum TravelStage {
        ENTER,
        MIDDLE,
        EXIT;
    }
}
