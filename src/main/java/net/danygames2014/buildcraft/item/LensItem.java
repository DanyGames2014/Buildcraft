package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeJsonOverride;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.pluggable.LensPluggable;
import net.danygames2014.buildcraft.util.ColorUtil;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.math.Direction;

public class LensItem extends TemplateItem implements PipePluggableItem {
    public final int color;
    public final boolean isFilter;

    public LensItem(int color, boolean isFilter) {
        super(Buildcraft.NAMESPACE.id(ColorUtil.getName(color) + "_" + (isFilter ? "filter" : "lens")));
        this.color = color;
        this.isFilter = isFilter;
        PipeJsonOverride.registerLensJsonOverride(Buildcraft.NAMESPACE.id(ColorUtil.getName(color) + "_" + (isFilter ? "filter" : "lens")), Buildcraft.NAMESPACE.id("item/" + (isFilter ? "filter" : "lens")));
    }

    @Override
    public PipePluggable createPipePluggable(PipeBlockEntity pipe, Direction side, ItemStack stack) {
        if(pipe.transporter.getType() == PipeType.ITEM){
            return new LensPluggable(color, isFilter);
        } else {
            return null;
        }
    }
}
