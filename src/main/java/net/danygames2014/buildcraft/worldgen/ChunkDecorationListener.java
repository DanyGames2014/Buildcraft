package net.danygames2014.buildcraft.worldgen;

import net.danygames2014.buildcraft.config.Config;
import net.danygames2014.buildcraft.worldgen.oil.OilSpringFeature;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.world.WorldEvent;
import net.modificationstation.stationapi.api.event.world.gen.WorldGenEvent;

public class ChunkDecorationListener {
    public OilSpringFeature oilSpringFeature;
    
    @EventListener
    public void decorate(WorldGenEvent.ChunkDecoration event) {
        if (event.world.dimension.id == 0) {
            decorateOverworld(event);
        }
    }

    private void decorateOverworld(WorldGenEvent.ChunkDecoration event) {
        if (Config.WORLDGEN_CONFIG.generateOil) {
            oilSpringFeature.generateOil(event.world, event.random, event.x, event.z);
        }
    }

    @EventListener
    public void initFeatures(WorldEvent.Init event) {
        oilSpringFeature = new OilSpringFeature();
    }
}
