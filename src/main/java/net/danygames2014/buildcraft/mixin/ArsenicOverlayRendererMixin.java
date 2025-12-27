package net.danygames2014.buildcraft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.danygames2014.buildcraft.item.CustomItemRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.texture.Sprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;

@Mixin(ArsenicOverlayRenderer.class)
public class ArsenicOverlayRendererMixin {

    @Inject(
            method = "renderVanilla(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/modificationstation/stationapi/api/client/texture/SpriteAtlasTexture;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
                    ordinal = 1
            ),
            cancellable = true
    )
    public void renderInHand(LivingEntity entity, ItemStack item, SpriteAtlasTexture atlas, CallbackInfo ci, @Local(name = "texture") Sprite texture, @Local(name = "var3") Tessellator tessellator) {
        if (item.getItem() instanceof CustomItemRenderer customItemRenderer) {
            customItemRenderer.renderInHand(atlas, texture, tessellator, entity, item);
            glDisable(GL_RESCALE_NORMAL);
            ci.cancel();
        }
    }
}
