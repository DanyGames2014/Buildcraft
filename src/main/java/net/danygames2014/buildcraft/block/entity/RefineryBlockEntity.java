package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.blockentity.HasWork;
import net.danygames2014.buildcraft.api.core.SafeTimeTracker;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.recipe.refinery.RefineryRecipe;
import net.danygames2014.buildcraft.recipe.refinery.RefineryRecipeRegistry;
import net.danygames2014.nyalib.block.BlockEntityInit;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.danygames2014.nyalib.fluid.block.ManagedFluidHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class RefineryBlockEntity extends BlockEntity implements ManagedFluidHandler, HasWork, IPowerReceptor, BlockEntityInit {
    public static int CAPACITY_PER_SLOT = 1000 * 4;

    public RefineryRecipe currentRecipe;

    public Direction facing = Direction.NORTH;

    public float animationSpeed = 1;
    private short animationStage = 0;

    private SafeTimeTracker time = new SafeTimeTracker();
    private SafeTimeTracker updateNetworkTime = new SafeTimeTracker(10);

    private PowerHandler powerHandler;
    private boolean isActive;

    private Identifier currentRecipeIdentifier;

    public RefineryBlockEntity() {
        powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);
        initPowerProvider();

        this.addFluidSlot(CAPACITY_PER_SLOT);
        this.addFluidSlot(CAPACITY_PER_SLOT);
        this.addFluidSlot(CAPACITY_PER_SLOT);
    }

    private void initPowerProvider() {
        powerHandler.configure(25, 100, 25, 1000);
    }

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote) {
            simpleAnimationIterate();
            return;
        }

        if (updateNetworkTime.markTimeIfDelay(world)) {
            // TODO: is this needed?
//            sendNetworkUpdate();
        }

        isActive = false;

        updateRecipe();

        if (currentRecipe == null) {
            decreaseAnimation();
            return;
        }

        if (getRemainingFluidCapacity(2, null) < currentRecipe.outputAmount) {
            decreaseAnimation();
            return;
        }

        if (!containsInput(currentRecipe.inputFluids[0]) || !containsInput(currentRecipe.inputFluids[1])) {
            decreaseAnimation();
            return;
        }

        isActive = true;

        if (powerHandler.getEnergyStored() >= currentRecipe.energyCost) {
            increaseAnimation();
        } else {
            decreaseAnimation();
        }

        if (!time.markTimeIfDelay(world, currentRecipe.craftingTime)) {
            return;
        }

        double energyUsed = powerHandler.useEnergy(currentRecipe.energyCost, currentRecipe.energyCost, true);

        if (energyUsed != 0) {
            if (consumeInput(new FluidStack(currentRecipe.inputFluids[0], currentRecipe.inputAmounts[0])) && consumeInput(new FluidStack(currentRecipe.inputFluids[1], currentRecipe.inputAmounts[1]))) {
                insertFluid(new FluidStack(currentRecipe.outputFluid, currentRecipe.outputAmount), 2, null);
            }
        }
    }

    private boolean containsInput(Fluid ingredient) {
        if(ingredient == null){
            return true;
        }

        return (getFluid(0, null) != null && getFluid(0, null).fluid == ingredient)
                || (getFluid(1, null) != null && getFluid(1, null).fluid == ingredient);
    }

    private boolean consumeInput(FluidStack liquid) {
        if (liquid == null || liquid.fluid == null)
            return true;

        if (getFluid(0, null) != null && getFluid(0, null).isFluidEqual(liquid)) {
            extractFluid(0, liquid.amount, null);
            return true;
        } else if (getFluid(1, null) != null && getFluid(1, null).isFluidEqual(liquid)) {
            extractFluid(1, liquid.amount, null);
            return true;
        }

        return false;
    }

    @Override
    public boolean hasWork() {
        return isActive;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        animationStage = nbt.getShort("animationStage");
        animationSpeed = nbt.getFloat("animationSpeed");

        powerHandler.readFromNBT(nbt);
        initPowerProvider();

        updateRecipe();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putShort("animationStage", animationStage);
        nbt.putFloat("animationSpeed", animationSpeed);

        powerHandler.writeToNBT(nbt);
    }

    /**
     * Used to iterate the animation without computing the speed
     */
    public void simpleAnimationIterate() {
        if (animationSpeed > 1) {
            animationStage += animationSpeed;

            if (animationStage > 300) {
                animationStage = 100;
            }
        } else if (animationStage > 0) {
            animationStage--;
        }
    }

    public void increaseAnimation() {
        if (animationSpeed < 2) {
            animationSpeed = 2;
        } else if (animationSpeed <= 5) {
            animationSpeed += 0.1;
        }

        animationStage += animationSpeed;

        if (animationStage > 300) {
            animationStage = 100;
        }
    }

    public void decreaseAnimation() {
        if (animationSpeed >= 1) {
            animationSpeed -= 0.1;

            animationStage += animationSpeed;

            if (animationStage > 300) {
                animationStage = 100;
            }
        } else {
            if (animationStage > 0) {
                animationStage--;
            }
        }
    }

    private void updateRecipe() {
        currentRecipe = null;

        for(RefineryRecipe recipe : RefineryRecipeRegistry.getInstance().registry.values()){
            if(containsInput(recipe.inputFluids[0]) && containsInput(recipe.inputFluids[1])){
                currentRecipe = recipe;
//                currentRecipeIdentifier = recipe
                if(currentRecipe.inputFluids[0] != null && currentRecipe.inputFluids[1] != null){
                    break;
                }
            }
        }
    }

    @Override
    public PowerHandler.PowerReceiver getPowerReceiver(Direction side) {
        return powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider) {
    }

    public int getAnimationStage() {
        return animationStage;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void init(BlockState blockState) {
        if(blockState.contains(Properties.HORIZONTAL_FACING)){
            facing = blockState.get(Properties.HORIZONTAL_FACING);
            getFluidSlot(0, null).setAllowedSides(facing.rotateYClockwise(), facing.getOpposite());
            getFluidSlot(1, null).setAllowedSides(facing.rotateYCounterclockwise(), facing.getOpposite());
            getFluidSlot(2, null).setAllowedSides(facing);
        }
    }
}
