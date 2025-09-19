package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.buildcraft.util.MathUtil;

public class GoldenPipeBehavior extends PipeBehavior {
    @Override
    public double modifyItemSpeed(TravellingItemEntity item) {
        return MathUtil.clamp(item.speed * 4D, TravellingItemEntity.DEFAULT_SPEED * 4D, TravellingItemEntity.DEFAULT_SPEED * 15D);
    }
}

