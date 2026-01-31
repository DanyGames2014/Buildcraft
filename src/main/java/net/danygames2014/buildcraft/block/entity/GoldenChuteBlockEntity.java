package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.config.Config;

public class GoldenChuteBlockEntity extends ChuteBlockEntity {
    @Override
    public int getDelay() {
        return Config.MACHINE_CONFIG.goldenChute.tickDelay;
    }

    @Override
    public boolean canExtractFromAbove() {
        return Config.MACHINE_CONFIG.goldenChute.allowInventoryExtraction;
    }

    @Override
    public boolean canPickupItemsFromWorld() {
        return Config.MACHINE_CONFIG.goldenChute.allowItemPickup;
    }
}
