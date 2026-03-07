package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.item.FacadeItemRenderer;
import net.danygames2014.buildcraft.pluggable.FacadePluggable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider;
import net.modificationstation.stationapi.api.client.render.model.json.ModelTransformation;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glTranslatef;

@EnvironmentInterface(value = EnvType.CLIENT, itf = CustomItemRenderer.class)
public class FacadeItem extends TemplateItem implements PipePluggableItem, CustomItemRenderer, CustomTooltipProvider {
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

    @Environment(EnvType.CLIENT)
    @Override
    public void renderInGui(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, TextRenderer textRenderer, TextureManager textureManager, ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glColor3f(1f, 1f, 1f);
        GL11.glTranslatef((float)(x - 2), (y + 2f), 1);
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

    @Environment(EnvType.CLIENT)
    @Override
    public void renderInHand(SpriteAtlasTexture atlas, Sprite texture, Tessellator tessellator, LivingEntity entity, ItemStack stack) {
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderInHandBlock(SpriteAtlasTexture atlas, Tessellator tessellator, LivingEntity entity, ItemStack stack) {
        GL11.glRotatef(60f, 0f, 0f, 1f);
        GL11.glRotatef(-30f, 1f, 0f, 0f);
        GL11.glTranslatef(0F, -0.5f, 0F);
        FacadeItemRenderer.INSTANCE.renderFacadeItem(Minecraft.INSTANCE.worldRenderer.blockRenderManager, stack, 0, 0, 0);
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderOnGround(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, Tessellator tessellator, ItemEntity itemEntity, float x, float y, float z, float delta, ItemStack stack, float yOffset, float angle, byte renderedAmount, SpriteAtlasTexture atlas) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderOnGroundBlock(ArsenicItemRenderer arsenicItemRenderer, ItemRenderer itemRenderer, Tessellator tessellator, ItemEntity itemEntity, float x, float y, float z, float delta, ItemStack stack, float yOffset, float angle, byte renderedAmount, SpriteAtlasTexture atlas) {
        GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(0.25F, 0.25F, 0.25F);

        for(int i = 0; i < renderedAmount; i++) {
            GL11.glPushMatrix();

            if (i > 0) {
                float offsetX = (itemRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.25F;
                float offsetY = (itemRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.25F;
                float offsetZ = (itemRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.25F;
                glTranslatef(offsetX, offsetY, offsetZ);
            }

            FacadeItemRenderer.INSTANCE.renderFacadeItem(Minecraft.INSTANCE.worldRenderer.blockRenderManager, stack, -0.6F, 0f, -0.6F);

            GL11.glPopMatrix();
        }
        return true;
    }

    @Override
    public @NotNull String[] getTooltip(ItemStack itemStack, String s) {
        return new String[]{getBlock(itemStack).getTranslatedName() + " " + TranslationStorage.getInstance().get("facade.buildcraft." + (isHollow(itemStack) ? "hollow" : "normal")+ ".name")};
    }
}
