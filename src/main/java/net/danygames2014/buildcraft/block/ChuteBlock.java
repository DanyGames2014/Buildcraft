package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.client.render.block.ChuteModel;
import net.danygames2014.buildcraft.screen.ChuteScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.model.block.BlockWithInventoryRenderer;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class ChuteBlock extends TemplateBlockWithEntity implements BlockWithWorldRenderer, BlockWithInventoryRenderer {
    private final ChuteModel chuteModel;
    private final Random random;

    public ChuteBlock(Identifier identifier) {
        super(identifier, Material.METAL);
        chuteModel = new ChuteModel();
        random = new Random();
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new ChuteBlockEntity();
    }

    @Override
    public void renderInventory(BlockRenderManager blockRenderManager, int i) {
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        chuteModel.render(Minecraft.INSTANCE.textureManager, 0, 0, 0);
    }

    @Override
    public boolean renderWorld(BlockRenderManager blockRenderManager, BlockView blockView, int i, int i1, int i2) {
        return true;
    }

    // TODO: dont open gui when player is holding pipe
    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        super.onUse(world, x, y, z, player);

        if(player.isSneaking()){
            return false;
        }
        if(world.getBlockEntity(x, y, z) instanceof ChuteBlockEntity chuteBlockEntity){
            GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("chute_screen"), chuteBlockEntity, new ChuteScreenHandler(player.inventory, chuteBlockEntity));
            return true;
        }
        return false;
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        ChuteBlockEntity chuteBlockEntity = (ChuteBlockEntity)world.getBlockEntity(x, y, z);

        for(int i = 0; i < chuteBlockEntity.size(); ++i) {
            ItemStack stack = chuteBlockEntity.getStack(i);
            if (stack != null) {
                float xOffset = this.random.nextFloat() * 0.8F + 0.1F;
                float yOffset = this.random.nextFloat() * 0.8F + 0.1F;
                float zOffset = this.random.nextFloat() * 0.8F + 0.1F;

                while(stack.count > 0) {
                    int stackCount = this.random.nextInt(21) + 10;
                    if (stackCount > stack.count) {
                        stackCount = stack.count;
                    }

                    stack.count -= stackCount;
                    ItemEntity itemEntity = new ItemEntity(world, (float)x + xOffset, (float)y + yOffset, (float)z + zOffset, new ItemStack(stack.itemId, stackCount, stack.getDamage()));
                    float var13 = 0.05F;
                    itemEntity.velocityX = ((float)this.random.nextGaussian() * var13);
                    itemEntity.velocityY = ((float)this.random.nextGaussian() * var13 + 0.2F);
                    itemEntity.velocityZ = ((float)this.random.nextGaussian() * var13);
                    world.spawnEntity(itemEntity);
                }
            }
        }

        super.onBreak(world, x, y, z);
    }
}
