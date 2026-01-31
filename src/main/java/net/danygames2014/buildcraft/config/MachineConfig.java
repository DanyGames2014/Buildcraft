package net.danygames2014.buildcraft.config;

import net.glasslauncher.mods.gcapi3.api.ConfigCategory;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class MachineConfig {
    @ConfigCategory(name = "Chute")
    public ChuteConfig chute = new ChuteConfig();
    
    @ConfigCategory(name = "Golden Chute")
    public GoldenChuteConfig goldenChute = new GoldenChuteConfig();
    
    @ConfigCategory(name = "Architect Table")
    public ArchitectTableConfig architectTable = new ArchitectTableConfig();
    
    @ConfigCategory(name = "Builder")
    public BuilderConfig builder = new BuilderConfig();
    
    @ConfigCategory(name = "Mining Well")
    public MiningWellConfig miningWell = new MiningWellConfig();
    
    @ConfigCategory(name = "Refinery")
    public RefineryConfig refinery = new RefineryConfig();
    
    public static class ChuteConfig {
        @ConfigEntry(name = "Tick Delay", minValue = 1, maxValue = 100, description = "Will work every x ticks")
        public Integer tickDelay = 5;
        
        @ConfigEntry(name = "Allow Item pickup from World")
        public Boolean allowItemPickup = true;
        
        @ConfigEntry(name = "Allow extraction from Inventories above")
        public Boolean allowInventoryExtraction = true;
    }
    
    public static class GoldenChuteConfig {
        @ConfigEntry(name = "Tick Delay", minValue = 1, maxValue = 100, description = "Will work every x ticks")
        public Integer tickDelay = 2;

        @ConfigEntry(name = "Allow Item pickup from World")
        public Boolean allowItemPickup = true;

        @ConfigEntry(name = "Allow extraction from Inventories above")
        public Boolean allowInventoryExtraction = true;
    }
    
    public static class ArchitectTableConfig {
        @ConfigEntry(name = "Enabled", description = "Only disables the functionality, not the block itself", multiplayerSynced = true)
        public Boolean enabled = true;
        
        @ConfigEntry(name = "Blueprint Time", minValue = 5, maxValue = 1000, multiplayerSynced = true)
        public Integer blueprintTime = 100;
    }
    
    public static class BuilderConfig {
        @ConfigEntry(name = "Enabled", description = "Only disables the functionality, not the block itself", multiplayerSynced = true)
        public Boolean enabled = true;
        
        @ConfigEntry(name = "MJ per block", description = "Setting to 0 disables the energy requirement", minValue = 0, maxValue = 128, multiplayerSynced = true)
        public Integer mjPerBlock = 25;
    }
    
    public static class MiningWellConfig {
        @ConfigEntry(name = "MJ per block", minValue = 2, maxValue = 128, multiplayerSynced = true)
        public Integer mjPerBlock = 25;
    }
    
    public static class RefineryConfig {
        @ConfigEntry(name = "MJ per mB", minValue = 2, maxValue = 2048, multiplayerSynced = true)
        public Integer mjPerMb = 25;
        
        @ConfigEntry(name = "Input Tanks Fluid Capacity", minValue = 50, maxValue = 32000, multiplayerSynced = true)
        public Integer inputFluidCapacity = 4000;
        
        @ConfigEntry(name = "Output Tank Fluid Capacity", minValue = 50, maxValue = 32000, multiplayerSynced = true)
        public Integer outputFluidCapacity = 4000;
    }
}
