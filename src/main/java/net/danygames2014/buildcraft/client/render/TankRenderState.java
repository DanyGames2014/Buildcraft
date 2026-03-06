package net.danygames2014.buildcraft.client.render;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TankRenderState implements Serializable {
    public FluidStack fluid;

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        if(fluid != null){
            stream.writeUTF(fluid.fluid.getIdentifier().toString());
            stream.writeInt(fluid.amount);
        } else {
            stream.writeUTF("none");
        }
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        String id = stream.readUTF();
        if(!id.equals("none")){
            int amount = stream.readInt();
            fluid = new FluidStack(FluidRegistry.get(Identifier.of(id)), amount);
        } else {
            fluid = null;
        }
    }
}
