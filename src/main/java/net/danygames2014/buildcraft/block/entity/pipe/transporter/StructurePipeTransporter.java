package net.danygames2014.buildcraft.block.entity.pipe.transporter;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;

public class StructurePipeTransporter extends PipeTransporter {
    public StructurePipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public PipeType getType() {
        return PipeType.STRUCTURE;
    }
}
