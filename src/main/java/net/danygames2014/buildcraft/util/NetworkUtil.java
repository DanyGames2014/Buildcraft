package net.danygames2014.buildcraft.util;

import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.modificationstation.stationapi.api.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetworkUtil {
    public static void writeFluid(FluidStack stack, DataOutputStream stream) throws IOException {
        if(stack == null){
            stream.writeUTF("null");
        } else {
            stream.writeUTF(stack.fluid.getIdentifier().toString());
            stream.writeInt(stack.amount);
        }
    }

    public static FluidStack readFluid(DataInputStream stream) throws IOException{
        String id = stream.readUTF();
        if(id.equals("null")){
            return null;
        } else {
            int amount = stream.readInt();
            return new FluidStack(FluidRegistry.get(Identifier.tryParse(id)), amount);
        }
    }
}
