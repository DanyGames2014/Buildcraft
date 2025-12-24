package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.block.PipeBlock;

public class ObsidianPipeBlockEntity extends PoweredPipeBlockEntity {
    public ObsidianPipeBlockEntity(PipeBlock pipeBlock) {
        super(pipeBlock);
    }

    public ObsidianPipeBlockEntity() {
        super();
    }

    @Override
    public void configurePower() {
        super.configurePower();
        powerHandler.configure(1.0D, 16.0D, 1.0D, 128.0D);
    }
}
