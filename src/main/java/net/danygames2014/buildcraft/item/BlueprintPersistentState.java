package net.danygames2014.buildcraft.item;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class BlueprintPersistentState extends PersistentState {
    public BlueprintData data = new BlueprintData();
    public int rawId;
    
    public BlueprintPersistentState(int id) {
        super("blueprint_" + id);
        this.rawId = id;
    }
    
    public static BlueprintPersistentState get(World world) {
        return get(world, world.getIdCount("blueprint"));
    }
    
    public static BlueprintPersistentState get(World world, int id) {
        BlueprintPersistentState blueprintState = (BlueprintPersistentState) world.persistentStateManager.getOrCreate(BlueprintPersistentState.class, "blueprint_" + id);
        
        if (blueprintState == null) {
            blueprintState = new BlueprintPersistentState(id);
            world.persistentStateManager.set("blueprint_" + id, blueprintState);
        }

        blueprintState.rawId = id;
        return blueprintState;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        data.readNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        data.writeNbt(nbt);
    }
}
