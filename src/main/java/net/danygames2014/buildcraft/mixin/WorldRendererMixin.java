package net.danygames2014.buildcraft.mixin;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "renderBlockOutline", at =  @At("HEAD"))
    public void setTickDelta(PlayerEntity player, HitResult hitResult, int i, ItemStack stack, float tickDelta, CallbackInfo ci){
        PipeBlock.tickDelta = tickDelta;
    }
}
