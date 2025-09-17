package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.nyalib.block.JsonOverrideRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.modificationstation.stationapi.api.util.Identifier;

public class PipeJsonOverride {
    public static void registerPipeJsonOverride(Identifier blockIdentifier, Identifier texture) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            return;
        }

        JsonOverrideRegistry.registerBlockModelOverride(blockIdentifier.withSuffixedPath("_middle"), pipeMiddleModelJson);
        JsonOverrideRegistry.registerBlockModelTextureOverride(blockIdentifier.withSuffixedPath("_middle"), "texture", texture);
        
        JsonOverrideRegistry.registerBlockModelOverride(blockIdentifier.withSuffixedPath("_side"), pipeSideModelJson);
        JsonOverrideRegistry.registerBlockModelTextureOverride(blockIdentifier.withSuffixedPath("_side"), "texture", texture);

        JsonOverrideRegistry.registerItemModelOverride(blockIdentifier, pipeMiddleModelJson);
        JsonOverrideRegistry.registerItemModelTextureOverride(blockIdentifier, "texture", texture);
        
        String pipeState = pipeStateJson;
        pipeState = pipeState.replace("MIDDLE", getBlockModelPath(blockIdentifier.withSuffixedPath("_middle")));
        pipeState = pipeState.replace("SIDE", getBlockModelPath(blockIdentifier.withSuffixedPath("_side")));
        JsonOverrideRegistry.registerBlockstateOverride(blockIdentifier, pipeState); 
    }

    private static String getBlockModelPath(String blockIdentifier) {
        return getBlockModelPath(Identifier.of(blockIdentifier));
    }

    private static String getBlockModelPath(Identifier blockIdentifier) {
        return blockIdentifier.namespace + ":block/" + blockIdentifier.path;
    }
    
    // Item
    private static final String itemJson = ("""
            {
              "parent": "PATH"
            }"""
    );
    
    // texture, particle
    private static final String pipeMiddleModelJson = ("""
            {
              "parent": "buildcraft:block/template/pipe_middle",
              "textures": {
              }
            }"""
    );

    // texture, particle
    private static final String pipeSideModelJson = ("""
            {
              "parent": "buildcraft:block/template/pipe_side",
              "textures": {
              }
            }"""
    );

    private static final String pipeStateJson = ("""
            {
              "multipart": [
                {
                  "apply": {
                    "model": "MIDDLE"
                  }
                },
                {
                  "apply": {
                    "model": "SIDE",
                    "uvlock": false,
                    "x": 90,
                    "y": 270
                  },
                  "when": {
                    "north": "true"
                  }
                },
                {
                  "apply": {
                    "model": "SIDE",
                    "uvlock": false,
                    "x": 90,
                    "y": 0
                  },
                  "when": {
                    "east": "true"
                  }
                },
                {
                  "apply": {
                    "model": "SIDE",
                    "uvlock": false,
                    "x": 90,
                    "y": 90
                  },
                  "when": {
                    "south": "true"
                  }
                },
                {
                  "apply": {
                    "model": "SIDE",
                    "uvlock": false,
                    "x": 90,
                    "y": 180
                  },
                  "when": {
                    "west": "true"
                  }
                },
                {
                  "apply": {
                    "model": "SIDE",
                    "uvlock": false
                  },
                  "when": {
                    "up": "true"
                  }
                },
                {
                  "apply": {
                    "model": "SIDE",
                    "uvlock": false,
                    "x": 180
                  },
                  "when": {
                    "down": "true"
                  }
                }
              ]
            }"""
    );
}
