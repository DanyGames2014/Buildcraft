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
        event.addSurfaceDepositBiome(Biome.TAIGA);
        event.addSurfaceDepositBiome(Biome.ICE_DESERT);
        
        event.addBiomeBonusMultiplier(Biome.DESERT, 7);
        event.addBiomeBonusMultiplier(Biome.TAIGA, 3);
        event.addBiomeBonusMultiplier(Biome.ICE_DESERT, 10);
    }
}
