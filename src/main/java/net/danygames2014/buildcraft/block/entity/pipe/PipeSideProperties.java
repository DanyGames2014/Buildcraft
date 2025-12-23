package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.registry.PluggableRegistry;
import net.danygames2014.buildcraft.util.ItemUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PipeSideProperties {
    PipePluggable[] pluggables = new PipePluggable[Direction.values().length];

    public void rotateLeft(){
        PipePluggable[] newPluggables = new PipePluggable[Direction.values().length];
        for(Direction direction : Direction.values()){
            newPluggables[direction.rotateCounterclockwise(Direction.Axis.Y).ordinal()] = pluggables[direction.ordinal()];
        }
        pluggables = newPluggables;
    }

    public boolean dropItem(PipeBlockEntity pipe, Direction direction, PlayerEntity player){
        boolean result = false;
        PipePluggable pluggable = pluggables[direction.ordinal()];
        if(pluggable != null){
            pluggable.onDetachedFromPipe(pipe, direction);
            if(!pipe.world.isRemote){
                ItemStack[] stacks = pluggable.getDropItems(pipe);
                if(stacks != null){
                    for(ItemStack stack : stacks){
                        ItemUtil.dropTryIntoPlayerInventory(pipe.world, pipe.x, pipe.y, pipe.z, stack, player);
                    }
                }
            }
        }
        return result;
    }

    public void invalidate() {
        for(PipePluggable p : pluggables){
            if(p != null){
                p.invalidate();
            }
        }
    }

    public void validate(PipeBlockEntity pipe){
        for (Direction d : Direction.values()) {
            PipePluggable p = pluggables[d.ordinal()];

            if (p != null) {
                p.validate(pipe, d);
            }
        }
    }

    public void writeNbt(NbtCompound nbt){
        for(int i = 0; i < Direction.values().length; i++){
            PipePluggable pluggable = pluggables[i];
            final String key = "pluggable[" + i + "]";
            if(pluggable != null){
                NbtCompound pluggableData = new NbtCompound();
                pluggableData.putString("id", PluggableRegistry.getIdentifier(pluggable.getClass()).toString());
                pluggable.writeNbt(pluggableData);
                nbt.put(key, pluggableData);
            }
        }
    }

    public void readNbt(NbtCompound nbt){
        for(int i = 0; i < Direction.values().length; i++){
            final String key = "pluggable[" + i + "]";
            if(!nbt.contains(key)){
                continue;
            }
            NbtCompound pluggableData = nbt.getCompound(key);
            Identifier identifier = Identifier.tryParse(pluggableData.getString("id"));
            PipePluggable pluggable = PluggableRegistry.getPluggableFactory(identifier).create();
            pluggable.readNbt(pluggableData);
            pluggables[i] = pluggable;
        }
    }
}
