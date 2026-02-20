package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.pluggable.PlugPluggable;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PlugItem extends TemplateItem implements PipePluggableItem {
    public PlugItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public PipePluggable createPipePluggable(PipeBlockEntity pipe, Direction side, ItemStack stack) {
        return new PlugPluggable();
    }
}
