package net.danygames2014.buildcraft.mixin;

import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.minecraft.block.FlowingLiquidBlock;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingLiquidBlock.class)
public abstract class FlowingLiquidBlockMixin extends LiquidBlock {
    @Shadow
    protected abstract boolean isLiquidBreaking(World world, int x, int y, int z);

    public FlowingLiquidBlockMixin(int i, Material material) {
        super(i, material);
    }

    // TODO: Move to NyaLib and build a fluid interactions API around it
    @Inject(method = "canSpreadTo", at = @At(value = "HEAD"), cancellable = true)
    public void aVoid(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Fluid fluid = FluidRegistry.get(this.id);
        Fluid otherFluid = FluidRegistry.get(world.getBlockId(x, y, z));
        
        // If we cant fetch the fluid for this block, something is wrong
        if (fluid == null) {
            throw new IllegalStateException("Tried to spread a non-fluid block!");
        }
        
        // If the other fluid is null, delegate check to is liquid breaking
        if (otherFluid == null) {
            cir.setReturnValue(!this.isLiquidBreaking(world, x, y, z));
        }
        
        if (fluid == otherFluid) {
            cir.setReturnValue(false);
        }
        
        if (otherFluid != null) {
            cir.setReturnValue(false);
        }
    }
}
