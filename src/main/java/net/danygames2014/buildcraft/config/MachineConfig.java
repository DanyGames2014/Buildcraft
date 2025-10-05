package net.danygames2014.buildcraft.config;

import net.glasslauncher.mods.gcapi3.api.ConfigCategory;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class MachineConfig {
    @ConfigCategory(name = "Mining Well")
    public MiningWellConfig miningWellConfig = new MiningWellConfig();
    
    public static class MiningWellConfig {
        @ConfigEntry(name = "MJ per block", maxValue = 2048, multiplayerSynced = true)
        public Integer mjPerBlock = 64;
    }
}
