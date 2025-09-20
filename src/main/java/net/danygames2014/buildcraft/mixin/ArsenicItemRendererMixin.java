package net.danygames2014.buildcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.render.model.BakedModel;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.impl.client.arsenic.renderer.render.ArsenicItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArsenicItemRenderer.class)
public class ArsenicItemRendererMixin {
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/modificationstation/stationapi/impl/client/arsenic/renderer/render/ArsenicItemRenderer;renderModel(Lnet/minecraft/entity/ItemEntity;FFFFLnet/minecraft/item/ItemStack;FFBLnet/modificationstation/stationapi/api/client/texture/SpriteAtlasTexture;Lnet/modificationstation/stationapi/api/client/render/model/BakedModel;)V"
            ),
            remap = false
    )
    void renderModel(ArsenicItemRenderer instance, ItemEntity item, float x, float y, float z, float delta, ItemStack stack, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, BakedModel model, Operation<Void> original){
        float f1 = var11;
        float f2 = var12;
        if(item instanceof TravellingItemEntity){
            if(stack.getItem() instanceof BlockItem){
                f1 = 0.25F;
            } else {
                f1 = 0;
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
    void renderVanilla(ArsenicItemRenderer instance, ItemEntity item, float x, float y, float z, float delta, ItemStack stack, float var11, float var12, byte renderedAmount, SpriteAtlasTexture atlas, Operation<Void> original){
        float f1 = var11;
        float f2 = var12;
        if(item instanceof TravellingItemEntity){
            if(stack.getItem() instanceof BlockItem){
                f1 = 0.25F;
            } else {
                f1 = 0;
            }
            f2 = 0;
        }
        original.call(instance, item, x, y, z, delta, stack, f1, f2, renderedAmount, atlas);
    }
}
