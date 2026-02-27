package net.danygames2014.buildcraft.mixin;

import net.danygames2014.buildcraft.screen.handler.GateInterfaceScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "tick", at = @At("HEAD"))
    void tickScreenHandler(CallbackInfo ci){
        if(player != null && player.currentScreenHandler instanceof GateInterfaceScreenHandler screenHandler){
            screenHandler.tick();
        }
    }
}
