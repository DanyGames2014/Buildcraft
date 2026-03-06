package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.item.FacadeItemRenderer;
import net.danygames2014.buildcraft.pluggable.FacadePluggable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.lwjgl.opengl.GL11;

public class FacadeItem extends TemplateItem implements PipePluggableItem, CustomItemRenderer {
    public FacadeItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public PipePluggable createPipePluggable(PipeBlockEntity pipe, Direction side, ItemStack stack) {
        return new FacadePluggable(stack);
    }

    public static ItemStack createStack(Block block, int meta, boolean hollow){
        ItemStack stack = new ItemStack(Buildcraft.facade);
        stack.getStationNbt().putString("id", BlockRegistry.INSTANCE.getId(block).toString());
        stack.getStationNbt().putInt("meta", meta);
        stack.getStationNbt().putBoolean("hollow", hollow);
        return stack;
    }

    public static boolean isHollow(ItemStack stack){
        if(stack.getStationNbt().contains("hollow")){
            return stack.getStationNbt().getBoolean("hollow");
        }
        return false;
    }

    public static Block getBlock(ItemStack stack) {
        if(stack.getStationNbt().contains("hollow")){
            return BlockRegistry.INSTANCE.get(Identifier.tryParse(stack.getStationNbt().getString("id")));
        }
        return Block.STONE;
    }

    public static int getMeta(ItemStack stack) {
        if(stack.getStationNbt().contains("meta")){
            return stack.getStationNbt().getInt("meta");
        }
        return 0;
    }

    @Override
    public void renderInGui(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, TextRenderer textRenderer, TextureManager textureManager, ItemStack stack, int x, int y) {
        GL11.glPushMatrix();

        GL11.glTranslatef((float)(x - 2), (float)(y + 3), -3.0F);
        GL11.glScalef(10.0F, 10.0F, 10.0F);
        GL11.glTranslatef(1.0F, 0.5F, 1.0F);
        GL11.glScalef(1.0F, 1.0F, -1.0F);
        GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        GL11.glScalef(1.1F, 1.1F, 1.1F);
        FacadeItemRenderer.INSTANCE.renderFacadeItem(Minecraft.INSTANCE.worldRenderer.blockRenderManager, stack, -0.3f, -0.35f, -0.7f);
        GL11.glPopMatrix();
    }

    @Override
    public void renderInHand(SpriteAtlasTexture atlas, Sprite texture, Tessellator tessellator, LivingEntity entity, ItemStack stack) {
    }

    @Override
    public boolean renderInHandBlock(SpriteAtlasTexture atlas, Tessellator tessellator, LivingEntity entity, ItemStack stack) {
        FacadeItemRenderer.INSTANCE.renderFacadeItem(Minecraft.INSTANCE.worldRenderer.blockRenderManager, stack, 0, 0, 0);
        return true;
    }

    @Override
    public boolean renderOnGround(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, Tessellator tessellator, ItemEntity itemEntity, float x, float y, float z, float delta, ItemStack stack, float yOffset, float angle, byte renderedAmount, SpriteAtlasTexture atlas) {
        return false;
    }

    @Override
    public boolean renderOnGroundBlock(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, Tessellator tessellator, ItemEntity itemEntity, float x, float y, float z, float delta, ItemStack stack, float yOffset, float angle, byte renderedAmount, SpriteAtlasTexture atlas) {
        GL11.glScalef(0.50F, 0.50F, 0.50F);
        FacadeItemRenderer.INSTANCE.renderFacadeItem(Minecraft.INSTANCE.worldRenderer.blockRenderManager, stack, -0.6F, 0f, -0.6F);
        return true;
    }
}
