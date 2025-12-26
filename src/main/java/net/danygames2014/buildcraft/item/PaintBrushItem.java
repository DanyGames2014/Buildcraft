package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.core.PaintableBlock;
import net.danygames2014.buildcraft.util.ColorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Locale;

public class PaintBrushItem extends TemplateItem {
    public final int color;

    public PaintBrushItem(int color) {
        super(Buildcraft.NAMESPACE.id(ColorUtil.getName(color) + "_paintbrush"));
        this.color = color;

        setMaxCount(1);
        setMaxDamage(63);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {
        BlockState blockState = world.getBlockState(x, y, z);

        if(blockState.getBlock() instanceof PaintableBlock paintableBlock){
            if(color >= 0){
                if(paintableBlock.recolorBlock(world, x, y, z, Direction.byId(side), color)){
                    stack.damage(1, user);
                    return true;
                }
            } else {
                if(paintableBlock.canRemoveColor(world, x, y, z, Direction.byId(side))){
                    return paintableBlock.removeColorFromBlock(world, x, y, z, Direction.byId(side));
                }
            }
        }
        return false;
    }
}
