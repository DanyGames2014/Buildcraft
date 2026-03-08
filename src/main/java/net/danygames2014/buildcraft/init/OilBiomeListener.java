package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.event.OilBiomeEvent;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.world.biome.Biome;

public class OilBiomeListener {
    @EventListener
    public void registerOilBiomes(OilBiomeEvent event) {
        event.addExcludedBiome(Biome.SKY);
        event.addExcludedBiome(Biome.HELL);
        
        event.addSurfaceDepositBiome(Biome.DESERT);
        event.addSurfaceDepositBiome(Biome.TUNDRA);
        event.addSurfaceDepositBiome(Biome.PLAINS);
        event.addSurfaceDepositBiome(Biome.SAVANNA);
        
        event.addBiomeBonusMultiplier(Biome.DESERT, 2);
        event.addBiomeBonusMultiplier(Biome.TUNDRA, 2);
    }
}
