package net.danygames2014.buildcraft.worldgen.oil;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.event.OilBiomeEvent;
import net.danygames2014.buildcraft.init.FluidListener;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.danygames2014.nyalib.fluid.Fluids;
import net.minecraft.block.Block;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Random;
import java.util.Set;

@SuppressWarnings({"RedundantIfStatement", "SameParameterValue", "UnnecessaryLocalVariable", "unused"})
public class OilSpringFeature {
    public static final OilSpringFeature INSTANCE = new OilSpringFeature();
    private static final byte LARGE_WELL_HEIGHT = 16;
    private static final byte MEDIUM_WELL_HEIGHT = 6;
    /**
     * Biomes which allow the generation of oil lakes
     */
    public final Set<Biome> surfaceDepositBiomes = new ObjectOpenHashSet<>();
    /**
     * Biomes which will not generate any oil
     */
    public final Set<Biome> excludedBiomes = new ObjectOpenHashSet<>();
    /**
     * The bonus of each biome
     */
    public final Object2IntOpenHashMap<Biome> biomeBonusMultipliers = new Object2IntOpenHashMap<>();

    private enum GenType {
        LARGE,
        MEDIUM,
        LAKE,
        NONE
    }

    public OilSpringFeature() {
        StationAPI.EVENT_BUS.post(new OilBiomeEvent(biomeBonusMultipliers, surfaceDepositBiomes, excludedBiomes));
    }

    public void generateOil(World world, Random random, int chunkX, int chunkZ) {
        // shift to world coordinates
        int x = chunkX + 8 + random.nextInt(16);
        int z = chunkZ + 8 + random.nextInt(16);

        Biome biome = world.method_1781().getBiome(x, z);

        // Do not generate oil in the End or Nether
        if (excludedBiomes.contains(biome)) {
            return;
        }

        boolean oilBiome = surfaceDepositBiomes.contains(biome);
//                || BiomeDictionary.isBiomeOfType(biome, DESERT)
//                || (BiomeDictionary.isBiomeOfType(biome, WASTELAND) && !BiomeDictionary.isBiomeOfType(biome, FROZEN))
//                || (BiomeDictionary.isBiomeOfType(biome, FOREST) && BiomeDictionary.isBiomeOfType(biome, FROZEN));

        double bonus = oilBiome ? 3.0 : 1.0;
        if (biomeBonusMultipliers.containsKey(biome)) {
            bonus *= biomeBonusMultipliers.getInt(biome);
        }

        GenType type = GenType.NONE;
        if (random.nextDouble() <= 0.0004 * bonus) {// 0.04%
            type = GenType.LARGE;
        } else if (random.nextDouble() <= 0.001 * bonus) {// 0.1%
            type = GenType.MEDIUM;
        } else if (oilBiome && random.nextDouble() <= 0.02 * bonus) {// 2%
            type = GenType.LAKE;
        }

        if (type == GenType.NONE) {
            return;
        }

        // Find ground level
        int groundLevel = getTopBlock(world, x, z);
        if (groundLevel < 5) {
            return;
        }

        double deviation = surfaceDeviation(world, x, groundLevel, z, 8);
        if (deviation > 0.45) {
            return;
        }

        // Generate a Well
        switch (type) {
            case LARGE, MEDIUM -> {
                int wellX = x;
                int wellZ = z;

                int wellHeight = MEDIUM_WELL_HEIGHT;
                if (type == GenType.LARGE) {
                    wellHeight = LARGE_WELL_HEIGHT;
                }

                int maxHeight = groundLevel + wellHeight;
                if (maxHeight >= world.getHeight() - 1) {
                    return;
                }

                // Generate a spherical cave deposit
                int wellY = 20 + random.nextInt(10);

                int radius;
                if (type == GenType.LARGE) {
                    radius = 8 + random.nextInt(9);
                } else {
                    radius = 4 + random.nextInt(4);
                }

                int radiusSq = radius * radius;

                for (int poolX = -radius; poolX <= radius; poolX++) {
                    for (int poolY = -radius; poolY <= radius; poolY++) {
                        for (int poolZ = -radius; poolZ <= radius; poolZ++) {
                            int distance = poolX * poolX + poolY * poolY + poolZ * poolZ;

                            if (distance <= radiusSq) {
                                world.setBlockStateWithNotify(poolX + wellX, poolY + wellY, poolZ + wellZ, FluidListener.oil.getStillBlock().getDefaultState());
                            }
                        }
                    }
                }

                // Generate Lake around Spout
                int lakeRadius;
                if (type == GenType.LARGE) {
                    lakeRadius = 25 + random.nextInt(20);
                } else {
                    lakeRadius = 5 + random.nextInt(10);
                }
                generateSurfaceDeposit(world, random, biome, wellX, groundLevel, wellZ, lakeRadius);

                // Generate Spout
                int baseY;
                if (type == GenType.LARGE && random.nextDouble() <= 0.25) {
                    baseY = 0;
                } else {
                    baseY = wellY;
                }

                for (int y = baseY + 1; y <= maxHeight; ++y) {
                    world.setBlockStateWithNotify(wellX, y, wellZ, FluidListener.oil.getStillBlock().getDefaultState());
                }

                if (type == GenType.LARGE) {
                    for (int y = wellY; y <= maxHeight - wellHeight / 2; ++y) {
                        world.setBlockStateWithNotify(wellX + 1, y, wellZ, FluidListener.oil.getStillBlock().getDefaultState());
                        world.setBlockStateWithNotify(wellX - 1, y, wellZ, FluidListener.oil.getStillBlock().getDefaultState());
                        world.setBlockStateWithNotify(wellX, y, wellZ + 1, FluidListener.oil.getStillBlock().getDefaultState());
                        world.setBlockStateWithNotify(wellX, y, wellZ - 1, FluidListener.oil.getStillBlock().getDefaultState());
                    }
                }
            }
            
            case LAKE -> {
                // Generate a surface oil lake
                int lakeX = x;
                int lakeZ = z;
                int lakeY = groundLevel;

                int blockId = world.getBlockId(lakeX, lakeY, lakeZ);
                if (blockId == biome.topBlockId) {
                    generateSurfaceDeposit(world, random, biome, lakeX, lakeY, lakeZ, 5 + random.nextInt(10));
                }
            }
        }
    }

    public void generateSurfaceDeposit(World world, Random rand, int x, int y, int z, int radius) {
        Biome biome = world.method_1781().getBiome(x, z);
        generateSurfaceDeposit(world, rand, biome, x, y, z, radius);
    }

    private void generateSurfaceDeposit(World world, Random rand, Biome biome, int x, int y, int z, int radius) {
        int depth = rand.nextDouble() < 0.5 ? 1 : 2;

        // Center
        setOilColumnForLake(world, biome, x, y, z, depth);

        // Generate tendrils, from the center outward
        for (int w = 1; w <= radius; ++w) {
            float proba = (float) (radius - w + 4) / (float) (radius + 4);

            setOilWithProba(world, biome, rand, proba, x, y, z + w, depth);
            setOilWithProba(world, biome, rand, proba, x, y, z - w, depth);
            setOilWithProba(world, biome, rand, proba, x + w, y, z, depth);
            setOilWithProba(world, biome, rand, proba, x - w, y, z, depth);

            for (int i = 1; i <= w; ++i) {
                setOilWithProba(world, biome, rand, proba, x + i, y, z + w, depth);
                setOilWithProba(world, biome, rand, proba, x + i, y, z - w, depth);
                setOilWithProba(world, biome, rand, proba, x + w, y, z + i, depth);
                setOilWithProba(world, biome, rand, proba, x - w, y, z + i, depth);

                setOilWithProba(world, biome, rand, proba, x - i, y, z + w, depth);
                setOilWithProba(world, biome, rand, proba, x - i, y, z - w, depth);
                setOilWithProba(world, biome, rand, proba, x + w, y, z - i, depth);
                setOilWithProba(world, biome, rand, proba, x - w, y, z - i, depth);
            }
        }

        // Fill in holes
        for (int dx = x - radius; dx <= x + radius; ++dx) {
            for (int dz = z - radius; dz <= z + radius; ++dz) {
                if (isOil(world, dx, y, dz)) {
                    continue;
                }
                if (isOilSurrounded(world, dx, y, dz)) {
                    setOilColumnForLake(world, biome, dx, y, dz, depth);
                }
            }
        }
    }

    private boolean isOilOrWater(World world, int x, int y, int z) {
        Fluid fluid = FluidRegistry.get(world.getBlockId(x, y, z));

        return fluid == FluidListener.oil || fluid == Fluids.WATER;
    }

    private boolean isOil(World world, int x, int y, int z) {
        Fluid fluid = FluidRegistry.get(world.getBlockId(x, y, z));

        return fluid == FluidListener.oil;
    }

    private boolean isReplaceableForLake(World world, Biome biome, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        if (blockId == 0) {
            return true;
        }

        if (blockId == biome.soilBlockId || blockId == biome.topBlockId) {
            return true;
        }

        Block block = Block.BLOCKS[blockId];
        if (!block.material.blocksMovement()) {
            return true;
        }

        //if (block.isGenMineableReplaceable(world, x, y, z, Block.STONE.id)) {
        if (blockId == Block.STONE.id) {
            return true;
        }

        if (block instanceof PlantBlock) {
            return true;
        }

        //if (!world.isBlockOpaqueCube(x, y, z)) {
        if (!block.isFullCube()) {
            return true;
        }

        return false;
    }

    private boolean isOilAdjacent(World world, int x, int y, int z) {
        return isOil(world, x + 1, y, z)
                || isOil(world, x - 1, y, z)
                || isOil(world, x, y, z + 1)
                || isOil(world, x, y, z - 1);
    }

    private boolean isOilSurrounded(World world, int x, int y, int z) {
        return isOil(world, x + 1, y, z)
                && isOil(world, x - 1, y, z)
                && isOil(world, x, y, z + 1)
                && isOil(world, x, y, z - 1);
    }

    private void setOilWithProba(World world, Biome biome, Random rand, float proba, int x, int y, int z, int depth) {
        if (rand.nextFloat() <= proba && world.getBlockId(x, y - depth - 1, z) != 0) {
            if (isOilAdjacent(world, x, y, z)) {
                setOilColumnForLake(world, biome, x, y, z, depth);
            }
        }
    }

    private void setOilColumnForLake(World world, Biome biome, int x, int y, int z, int depth) {
        if (isReplaceableForLake(world, biome, x, y + 1, z)) {
            if (!world.isAir(x, y + 2, z)) {
                return;
            }

            if (isOilOrWater(world, x, y, z) || isBlockSolidOnSide(world, x, y - 1, z, Direction.UP)) {
                world.setBlockStateWithNotify(x, y, z, FluidListener.oil.getStillBlock().getDefaultState());
            } else {
                return;
            }

            if (!world.isAir(x, y + 1, z)) {
                world.setBlock(x, y + 1, z, 0);
            }

            for (int d = 1; d <= depth - 1; d++) {
                if (isOilOrWater(world, x, y - d, z) || !isBlockSolidOnSide(world, x, y - d - 1, z, Direction.UP)) {
                    return;
                }
                world.setBlockStateWithNotify(x, y - d, z, FluidListener.oil.getStillBlock().getDefaultState());
            }
        }
    }

    private boolean isBlockSolidOnSide(World world, int x, int y, int z, Direction side) {
        Block block = Block.BLOCKS[world.getBlockId(x, y, z)];
        if (block == null) {
            return false;
        }

        return block.isSolidFace(world, x, y, z, side.getId());
    }

    private int getTopBlock(World world, int x, int z) {
        int y = world.getTopY(x, z);

        for (; y > 0; --y) {
            int blockId = world.getBlockId(x, y, z);
            Block block = Block.BLOCKS[blockId];
            if (blockId == 0) {
                continue;
            }
            if (block instanceof LiquidBlock) {
                return y;
            }
            if (!block.material.blocksMovement()) {
                continue;
            }
            if (block instanceof PlantBlock) {
                continue;
            }
            return y - 1;
        }

        return -1;
    }

    private double surfaceDeviation(World world, int x, int y, int z, int radius) {
        int diameter = radius * 2;
        double centralTendancy = y;
        double deviation = 0;
        for (int i = 0; i < diameter; i++) {
            for (int k = 0; k < diameter; k++) {
                deviation += getTopBlock(world, x - radius + i, z - radius + k) - centralTendancy;
            }
        }
        return Math.abs(deviation / centralTendancy);
    }
}
