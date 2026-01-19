package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.RefineryBlockEntity;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.danygames2014.buildcraft.util.raycast.IndexRaycastResult;
import net.danygames2014.buildcraft.util.raycast.PipeRaycastResult;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidBucket;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class RefineryBlock extends TemplateMachineBlock {
    public float[][][] bounds = new float[3][3][2];

    public RefineryBlock(Identifier identifier, Material material) {
        super(identifier, material);
        // tank 0
        bounds[0][0][0] = 0f;
        bounds[0][0][1] = 0.5f;

        bounds[0][1][0] = 0f;
        bounds[0][1][1] = 1f;

        bounds[0][2][0] = 0f;
        bounds[0][2][1] = 0.5f;

        // tank 1
        bounds[1][0][0] = 0.5f;
        bounds[1][0][1] = 1f;

        bounds[1][1][0] = 0f;
        bounds[1][1][1] = 1f;

        bounds[1][2][0] = 0f;
        bounds[1][2][1] = 0.5f;

        // tank 2
        bounds[2][0][0] = 0.25f;
        bounds[2][0][1] = 0.75f;

        bounds[2][1][0] = 0f;
        bounds[2][1][1] = 1f;

        bounds[2][2][0] = 0.5f;
        bounds[2][2][1] = 1f;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(Properties.HORIZONTAL_FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new RefineryBlockEntity();
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(world.getBlockEntity(x, y, z) instanceof RefineryBlockEntity blockEntity){
            IndexRaycastResult raycastResult = raycastTank(world, x, y, z, player);
            FluidStack tankStack = blockEntity.getFluid(raycastResult.index, null);
            int remainingCapacity = blockEntity.getRemainingFluidCapacity(raycastResult.index, null);
            if(player.getHand() != null){
                ItemStack hand = player.getHand();
                if(hand.getItem() instanceof FluidBucket fluidBucket){
                    Fluid bucketFluid = fluidBucket.getFluid();
                    if(bucketFluid != null && raycastResult.index != 2){
                        if((tankStack == null || tankStack.fluid == bucketFluid) && remainingCapacity >= bucketFluid.getBucketSize()){
                            blockEntity.insertFluid(new FluidStack(bucketFluid, bucketFluid.getBucketSize()), raycastResult.index, null);
                            player.getHand().itemId = fluidBucket.getEmptyBucketItem().id;
                            return true;
                        }
                    }
                    if(bucketFluid == null){
                        if(tankStack != null && tankStack.amount >= tankStack.fluid.getBucketSize()){
                            blockEntity.extractFluid(raycastResult.index, tankStack.fluid.getBucketSize(), null);
                            player.getHand().itemId = fluidBucket.getFullBucketItem(tankStack.fluid).id;
                            return true;
                        }
                    }
                }
            }
            System.out.println(raycastResult.index);
        }

        return false;
    }

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        PlayerEntity player = PlayerHelper.getPlayerFromGame();

        IndexRaycastResult raycastResult = raycastTank(world, x, y, z, player);
        if (raycastResult == null) {
            return Box.createCached(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
        } else {
            return raycastResult.box.translate(x, y, z);
        }
    }

    private void setBoundingBox(float[][] box) {
        setBoundingBox(box[0][0], box[1][0], box[2][0], box[0][1], box[1][1], box[2][1]);
    }

    @Override
    public HitResult raycast(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos) {
        IndexRaycastResult raycastResult = raycastTank(world, x, y, z, startPos, endPos);
        if (raycastResult == null) {
            return null;
        } else {
            return raycastResult.hit;
        }
    }

    public IndexRaycastResult raycastTank(World world, int x, int y, int z, PlayerEntity player) {
        double distance = 5d;

        double eyeHeight = 0;

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            eyeHeight = player.getEyeHeight();
        }

        // TODO: move the constants to a common place
        double playerX = player.prevX + (player.x - player.prevX) * (double) PipeBlock.tickDelta;
        double playerY = player.prevY + (player.y - player.prevY) * (double) PipeBlock.tickDelta;
        double playerZ = player.prevZ + (player.z - player.prevZ) * (double) PipeBlock.tickDelta;

        Vec3d positionVector = Vec3d.create(playerX, playerY + eyeHeight, playerZ);
        Vec3d lookVector = player.getLookVector(PipeBlock.tickDelta);

        Vec3d endVector = positionVector.add(lookVector.x * distance, lookVector.y * distance, lookVector.z * distance);

        return raycastTank(world, x, y, z, positionVector, endVector);
    }

    private IndexRaycastResult raycastTank(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos) {
        BlockState blockState = world.getBlockState(x, y, z);

        if(!blockState.contains(Properties.HORIZONTAL_FACING)){
            return null;
        }

        Direction direction = blockState.get(Properties.HORIZONTAL_FACING);

        HitResult[] hits = new HitResult[3];
        Box[] boxes = new Box[3];

        for(int i = 0; i < bounds.length; i++) {

            float[][] currentBounds = MatrixTransformation.deepClone(bounds[i]);
            MatrixTransformation.transformHorizontalFacing(currentBounds, direction);

            setBoundingBox(currentBounds);
            boxes[i] = Box.create(currentBounds[0][0], currentBounds[1][0], currentBounds[2][0], currentBounds[0][1], currentBounds[1][1], currentBounds[2][1]);
            hits[i] = super.raycast(world, x, y, z, startPos, endPos);
        }

        // get closest hit

        double minLengthSquared = Double.POSITIVE_INFINITY;
        int minIndex = 0;

        for (int i = 0; i < hits.length; i++) {
            HitResult hit = hits[i];
            if (hit == null) {
                continue;
            }

            double lengthSquared = hit.pos.squaredDistanceTo(startPos);

            if (lengthSquared < minLengthSquared) {
                minLengthSquared = lengthSquared;
                minIndex = i;
            }
        }

        setBoundingBox(0, 0, 0, 1, 1, 1);

        return new IndexRaycastResult(minIndex, hits[minIndex], boxes[minIndex], null);
    }
}
