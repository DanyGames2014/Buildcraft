package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.block.PipeBlock;

public interface PipeBlockEntityFactory {
    PipeBlockEntity create(PipeBlock block);
}
