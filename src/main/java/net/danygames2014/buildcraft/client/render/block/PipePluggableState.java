package net.danygames2014.buildcraft.client.render.block;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.registry.PluggableRegistry;
import net.danygames2014.buildcraft.util.ConnectionMatrix;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PipePluggableState implements Serializable {
    private PipePluggable[] pluggables = new PipePluggable[Direction.values().length];
    private final ConnectionMatrix pluggableMatrix = new ConnectionMatrix();

    public PipePluggable[] getPluggables() {
        return pluggables;
    }

    public void setPluggables(PipePluggable[] pluggables) {
        this.pluggables = pluggables;
        this.pluggableMatrix.clean();
        for (Direction direction : Direction.values()) {
            this.pluggableMatrix.setConnected(direction, pluggables[direction.ordinal()] != null);
        }
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        this.pluggableMatrix.writeData(stream);
        for(PipePluggable p : pluggables){
            if(p != null){
                stream.writeUTF(PluggableRegistry.getIdentifier(p.getClass()).toString());
                p.writeData(stream);
            }
        }
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        this.pluggableMatrix.readData(stream);
        for(Direction dir : Direction.values()){
            if(this.pluggableMatrix.isConnected(dir)){
                Identifier identifier = Identifier.tryParse(stream.readUTF());
                pluggables[dir.ordinal()] = PluggableRegistry.getPluggableFactory(identifier).create();
                if(pluggables[dir.ordinal()] != null){
                    pluggables[dir.ordinal()].readData(stream);
                }
            }
        }
    }
}
