package net.danygames2014.buildcraft.item;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;

public interface CustomItemRenderer {
    void renderInGui(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, TextRenderer textRenderer, TextureManager textureManager, ItemStack stack, int x, int y);
    
    void renderInHand(SpriteAtlasTexture atlas, Sprite texture, Tessellator tessellator, LivingEntity entity, ItemStack stack);

    boolean renderOnGround(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, Tessellator tessellator, ItemEntity itemEntity, float x, float y, float z, float delta, ItemStack stack, float yOffset, float angle, byte renderedAmount, SpriteAtlasTexture atlas);
}
