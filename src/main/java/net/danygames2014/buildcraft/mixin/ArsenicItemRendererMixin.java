package net.danygames2014.buildcraft.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.buildcraft.item.CustomItemRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArsenicItemRenderer.class)
public class ArsenicItemRendererMixin {

    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    @Inject(
            method = "renderItemOnGui(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true)
    public void renderInGui(TextRenderer textRenderer, TextureManager textureManager, ItemStack stack, int x, int y, CallbackInfo methodCi, CallbackInfo ci) {
        if (stack.getItem() instanceof CustomItemRenderer customItemRenderer) {
            customItemRenderer.renderInGui(ArsenicItemRenderer.class.cast(this), this.itemRenderer, textRenderer, textureManager, stack, x, y);
            methodCi.cancel();
            ci.cancel();
        }
    }
    
    @Unique
    boolean cancelVanillaVertex = false;
    
    @Inject(method = "renderVanilla", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;normal(FFF)V", shift = At.Shift.AFTER))
    public void renderOnGround(ItemEntity itemEntity, float x, float y, float z, float delta, ItemStack stack, float yOffset, float angle, byte renderedAmount, SpriteAtlasTexture atlas, CallbackInfo ci, @Local(name = "var15") Tessellator tessellator) {
        cancelVanillaVertex = false;
        if (stack.getItem() instanceof CustomItemRenderer customItemRenderer) {
            cancelVanillaVertex = customItemRenderer.renderOnGround(ArsenicItemRenderer.class.cast(this), this.itemRenderer, tessellator, itemEntity, x, y, z, delta, stack, yOffset, angle, renderedAmount, atlas);
        }
    }
    
    @WrapWithCondition(method = "renderVanilla", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V"))
    public boolean cancelVanillaVertex(Tessellator instance, double x, double y, double z, double u, double v) {
        return !cancelVanillaVertex;
    }

    // Fixing offsets for travelling item entities
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/modificationstation/stationapi/impl/client/arsenic/renderer/render/ArsenicItemRenderer;renderModel(Lnet/minecraft/entity/ItemEntity;FFFFLnet/minecraft/item/ItemStack;FFBLnet/modificationstation/stationapi/api/client/texture/SpriteAtlasTexture;Lnet/modificationstation/stationapi/api/client/render/model/BakedModel;)V"
            ),
            remap = false
    )
    void renderModel(ArsenicItemRenderer instance, ItemEntity item, float x, float y, float z, float delta, ItemStack stack, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, BakedModel model, Operation<Void> original) {
        float f1 = var11;
        float f2 = var12;
        if (item instanceof TravellingItemEntity) {
            if (stack.getItem() instanceof BlockItem) {
                f1 = 0.25F;
            } else {
                f1 = 0;
            }

            if (item.world.isRemote) {
                y -= 0.5F;
            }
        }
        original.call(instance, item, x, y, z, delta, stack, f1, f2, renderedAmount, atlas, model);
    }

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/modificationstation/stationapi/impl/client/arsenic/renderer/render/ArsenicItemRenderer;renderVanilla(Lnet/minecraft/entity/ItemEntity;FFFFLnet/minecraft/item/ItemStack;FFBLnet/modificationstation/stationapi/api/client/texture/SpriteAtlasTexture;)V"
            ),
            remap = false
    )
    void renderVanilla(ArsenicItemRenderer instance, ItemEntity item, float x, float y, float z, float delta, ItemStack stack, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, Operation<Void> original) {
        float f1 = var11;
        float f2 = var12;
        if (item instanceof TravellingItemEntity) {
            if (stack.getItem() instanceof BlockItem) {
                f1 = 0.25F;
            } else {
                f1 = 0;
            }
            f2 = 0;

            if (item.world.isRemote) {
                y -= 0.5F;
            }
        }
        original.call(instance, item, x, y, z, delta, stack, f1, f2, renderedAmount, atlas);
    }
}
