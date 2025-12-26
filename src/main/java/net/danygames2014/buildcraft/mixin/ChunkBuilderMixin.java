package net.danygames2014.buildcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBuilder.class)
public class ChunkBuilderMixin {

    @Shadow public boolean[] renderLayerEmpty;

    @WrapOperation(method = "rebuild", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderLayer()I"))
    int test(Block instance, Operation<Integer> original, @Local(ordinal = 7)int var11, @Local(ordinal = 0)BlockRenderManager var10, @Local(ordinal = 13)int var17, @Local(ordinal = 11)int var15, @Local(ordinal = 12)int var16){
        if(instance instanceof PipeBlock){
            renderLayerEmpty[var11] = false;

            PipeBlock.currentRenderPass = var11;
            var10.render(instance, var17, var15, var16);
        }
        return original.call(instance);
    }
}
