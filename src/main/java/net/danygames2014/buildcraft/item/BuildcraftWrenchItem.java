package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.api.core.Debuggable;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.item.WrenchBase;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;

public class BuildcraftWrenchItem extends WrenchBase {
    public BuildcraftWrenchItem(Identifier identifier) {
        super(identifier);
        addWrenchMode(WrenchMode.MODE_WRENCH);
        addWrenchMode(WrenchMode.MODE_ROTATE);
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            addWrenchMode(WrenchMode.MODE_DEBUG);
        }
    }

    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if (wrenchMode == WrenchMode.MODE_DEBUG && world.getBlockState(x,y,z).getBlock() instanceof Debuggable debuggable) {
            debuggable.debug(stack, player, isSneaking, world, x, y, z, side);
            return true;
        }
        
        return super.wrenchRightClick(stack, player, isSneaking, world, x, y, z, side, wrenchMode);
    }
}
