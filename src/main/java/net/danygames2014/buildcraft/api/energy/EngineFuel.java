package net.danygames2014.buildcraft.api.energy;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@SuppressWarnings("ClassCanBeRecord")
public class EngineFuel {
    public final Fluid fluid;
    public final float powerPerCycle;
    public final int burnTime;

    public EngineFuel(Fluid fluid, float powerPerCycle, int burnTime) {
        this.fluid = fluid;
        this.powerPerCycle = powerPerCycle;
        this.burnTime = burnTime;
    }

    public static EngineFuel fromDataInputStream(DataInputStream stream) throws IOException {
        return new EngineFuel(FluidRegistry.get(Identifier.of(stream.readUTF())), stream.readFloat(), stream.readInt());
    }

    public void writeData(DataOutputStream stream) throws IOException {
        stream.writeUTF(fluid.getIdentifier().toString());
        stream.writeFloat(powerPerCycle);
        stream.writeInt(burnTime);
    }
}
