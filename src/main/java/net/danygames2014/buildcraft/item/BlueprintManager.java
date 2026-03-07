package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class BlueprintManager {
    public static ArrayList<String> blueprints = new ArrayList<>();
    public static Path blueprintDir = Paths.get("blueprints");
    
    public static BlueprintData load(String name) {
        if (name == null) {
            return null;
        }
        
        Path path = blueprintDir.resolve(name);
        
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            Buildcraft.LOGGER.warn("Blueprint " + name + " not found");
            return null;
        }

        NbtCompound blueprintNbt = new NbtCompound();
        try {
            blueprintNbt = NbtIo.readCompressed(Files.newInputStream(path));
        } catch (IOException e) {
            Buildcraft.LOGGER.error("Failed to read blueprint " + name, e);
        }
        
        BlueprintData data = new BlueprintData();
        data.readNbt(blueprintNbt);
        return data;
    }
    
    public static void save(BlueprintData data) {
        if (data == null) {
            Buildcraft.LOGGER.warn("Tried to save a null blueprint");
            return;
        }
        
        Path path = blueprintDir.resolve(data.name);
        
        try {
            NbtCompound blueprintNbt = new NbtCompound();
            data.writeNbt(blueprintNbt);
            NbtIo.writeCompressed(blueprintNbt, Files.newOutputStream(path));
            loadBlueprintList();
        } catch (IOException e) {
            Buildcraft.LOGGER.error("Failed to save blueprint " + data.name, e);
        }
    }
 
    public static void delete(String name) {
        Path path = blueprintDir.resolve(name);
        
        try {
            Files.delete(path);
            blueprints.remove(name);
            loadBlueprintList();
        } catch (IOException e) {
            Buildcraft.LOGGER.error("Failed to delete blueprint " + name, e);
        }
    }
    
    public static ArrayList<String> getBlueprints() {
        loadBlueprintList();
        
        return blueprints;
    }
    
    public static void loadBlueprintList() {
        blueprints.clear();
        
        try {
            if (Files.notExists(blueprintDir)) {
                Files.createDirectories(blueprintDir);
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(blueprintDir)) {
                for (Path entry : stream) {
                    if (Files.isRegularFile(entry)) {
                        blueprints.add(entry.getFileName().toString());
                    }
                }
            }
            
            Buildcraft.LOGGER.info("Loaded " + blueprints.size() + " blueprints");
        } catch (IOException e) {
            Buildcraft.LOGGER.error("Failed to load blueprints", e);
        }
    }
}
