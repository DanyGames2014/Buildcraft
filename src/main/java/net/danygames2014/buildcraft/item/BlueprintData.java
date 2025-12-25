package net.danygames2014.buildcraft.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.BlockRegistry;

import java.util.ArrayList;
import java.util.Map;

public class BlueprintData {
    public ArrayList<BlueprintEntry> entries;

    public BlueprintData() {
        entries = new ArrayList<>();
    }
    
    public void addEntry(int x, int y, int z, BlockState state) {
        BlueprintEntry entry = new BlueprintEntry();
        entry.x = x;
        entry.y = y;
        entry.z = z;
        entry.id = String.valueOf(BlockRegistry.INSTANCE.getId(state.getBlock()));
        for (var property : state.getProperties()) {
            entry.properties.put(property.getName(), String.valueOf(state.get(property)));
        }
        
        entries.add(entry);
    }
    
    public void writeNbt(NbtCompound nbt) {
        NbtList entries = new NbtList();
        for (BlueprintEntry entry : this.entries) {
            NbtCompound entryNbt = new NbtCompound();
            entry.writeNbt(entryNbt);
            entries.add(entryNbt);
        }
        nbt.put("entries", entries);
    }
    
    public void readNbt(NbtCompound nbt) {
        NbtList entries = nbt.getList("entries");
        for (int i = 0; i < entries.size(); i++) {
            BlueprintEntry entry = new BlueprintEntry();
            entry.readNbt((NbtCompound) entries.get(i));
            this.entries.add(entry);
        }
    }

    public static class BlueprintEntry {
        public String id;
        public int x;
        public int y;
        public int z;
        public Object2ObjectOpenHashMap<String, String> properties = new Object2ObjectOpenHashMap<>();
        
        public void writeNbt(NbtCompound nbt) {
            nbt.putString("id", id);
            nbt.putInt("x", x);
            nbt.putInt("y", y);
            nbt.putInt("z", z);
            NbtCompound propertiesNbt = new NbtCompound();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                propertiesNbt.putString(entry.getKey(), entry.getValue());
            }
            nbt.put("properties", propertiesNbt);
        }
        
        public void readNbt(NbtCompound nbt) {
            id = nbt.getString("id");
            x = nbt.getInt("x");
            y = nbt.getInt("y");
            z = nbt.getInt("z");
            NbtCompound propertiesNbt = nbt.getCompound("properties");
            for (var propertyO : propertiesNbt.values()) {
                NbtString propertyNbt = (NbtString) propertyO;
                properties.put(propertyNbt.getKey(), propertyNbt.value);
            }
        }
    }
}
