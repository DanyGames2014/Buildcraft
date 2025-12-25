package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.pluggable.LensPluggable;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class LensItem extends TemplateItem implements PipePluggableItem {
    public LensItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public PipePluggable createPipePluggable(PipeBlockEntity pipe, Direction side, ItemStack stack) {
        if(pipe.transporter.getType() == PipeType.ITEM){
            return new LensPluggable(stack);
        } else {
            return null;
        }
    }

    public int getDye(ItemStack stack) {
        return 15 - (stack.getDamage() & 15);
    }

    // TODO: add color name to translation key
    @Override
    public String getTranslationKey(ItemStack stack) {
        return stack.getDamage() >= 16 ? "item.Filter.name" : "item.Lens.name";
    }
}
