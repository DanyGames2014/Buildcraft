package net.danygames2014.buildcraft.event;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.mine_diver.unsafeevents.Event;
import net.mine_diver.unsafeevents.event.EventPhases;
import net.minecraft.world.biome.Biome;

import java.util.Set;

import static net.mine_diver.unsafeevents.event.EventPhases.*;

@EventPhases({
        DEFAULT_PHASE,
        OilBiomeEvent.POST_PHASE   
})
public class OilBiomeEvent extends Event {
    public static final String POST_PHASE = "post";

    private final Object2IntOpenHashMap<Biome> biomeBonusMultipliers;
    private final Set<Biome> surfaceDepositBiomes;
    private final Set<Biome> excludedBiomes;

    public OilBiomeEvent(Object2IntOpenHashMap<Biome> biomeBonusMultipliers, Set<Biome> surfaceDepositBiomes, Set<Biome> excludedBiomes) {
        this.biomeBonusMultipliers = biomeBonusMultipliers;
        this.surfaceDepositBiomes = surfaceDepositBiomes;
        this.excludedBiomes = excludedBiomes;
    }

    /**
     * Assigns a biome a bonus multiplier, this means that the chance to spawn oil will be multiplied by this value
     *
     * @param biome           The biome to assign the multiplier
     * @param bonusMultiplier The multiplier to assign
     */
    public void addBiomeBonusMultiplier(Biome biome, int bonusMultiplier) {
        biomeBonusMultipliers.put(biome, bonusMultiplier);
    }

    public Object2IntOpenHashMap<Biome> getBiomeBonusMultpliers() {
        return biomeBonusMultipliers;
    }

    /**
     * Add a biome to the list of biomes that can spawn surface deposits.
     * Adding a biome to this list will allow it to spawn oil lakes
     */
    public void addSurfaceDepositBiome(Biome biome) {
        surfaceDepositBiomes.add(biome);
    }

    public Set<Biome> getSurfaceDepositBiomes() {
        return surfaceDepositBiomes;
    }

    /**
     * Add a biome to the list of excluded biomes.
     * Adding a biome to this list will prevent any oil spawning in it
     */
    public void addExcludedBiome(Biome biome) {
        excludedBiomes.add(biome);
    }

    public Set<Biome> getExcludedBiomes() {
        return excludedBiomes;
    }
}
