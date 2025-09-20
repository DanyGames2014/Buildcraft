package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.api.core.Debuggable;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.client.render.item.PipeItemRenderer;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.danygames2014.buildcraft.util.RaycastResult;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.api.Wrenchable;
import net.danygames2014.uniwrench.item.WrenchBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.client.model.block.BlockWithInventoryRenderer;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("deprecation")
public class PipeBlock extends TemplateBlockWithEntity implements Wrenchable, Debuggable, BlockWithWorldRenderer, BlockWithInventoryRenderer {
    public final PipeBehavior behavior;
    public final PipeTransporter.PipeTransporterFactory transporterFactory;
    public final PipeBlockEntityFactory blockEntityFactory;
    @Environment(EnvType.CLIENT)
    public static float tickDelta;
    @Environment(EnvType.CLIENT)
    private PipeWorldRenderer pipeWorldRenderer;
    @Environment(EnvType.CLIENT)
    private PipeItemRenderer pipeItemRenderer;
    @Environment(EnvType.CLIENT)
    private Identifier texture;

    public PipeBlock(Identifier identifier, Material material, Identifier texture, PipeBehavior behavior, PipeTransporter.PipeTransporterFactory transporter, PipeBlockEntityFactory blockEntityFactory) {
        super(identifier, material);
        this.blockEntityFactory = blockEntityFactory;
        this.behavior = behavior;
        this.transporterFactory = transporter;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.texture = texture;
            this.pipeWorldRenderer = new PipeWorldRenderer();
            this.pipeItemRenderer = new PipeItemRenderer();
        }
    }

    // Properties
    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.UP, Properties.DOWN, Properties.NORTH, Properties.SOUTH, Properties.EAST, Properties.WEST);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState()
                .with(Properties.UP, false)
                .with(Properties.DOWN, false)
                .with(Properties.NORTH, false)
                .with(Properties.SOUTH, false)
                .with(Properties.EAST, false)
                .with(Properties.WEST, false);
    }

    // Connecting Logic
    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);
        updateConnections(world, x, y, z);
        if(world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipeBlockEntity){
            pipeBlockEntity.neighborUpdate();
        }
    }

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        super.onPlaced(world, x, y, z);
        updateConnections(world, x, y, z);
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        if(world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipeBlockEntity){
            pipeBlockEntity.onBreak();
        }
        super.onBreak(world, x, y, z);
    }

    public void updateConnections(World world, int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);

        for (Direction side : Direction.values()) {
            state = state.with(PROPERTY_LOOKUP.get(side), this.canConnectTo(world, x, y, z, side));
        }

        world.setBlockState(x, y, z, state);
        
        if (world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe) {
            pipe.updateValidOutputDirections();
        }
    }
    
    public boolean canConnectTo(World world, int x, int y, int z, Direction side) {
        if (world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe) {
            return pipe.canConnectTo(x, y, z, side);
        }
        
        return false;
    }
    
    // Wrenching
    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        // Wrench + Sneaking = Disassemble
        if (wrenchMode == WrenchMode.MODE_WRENCH) {
            if (isSneaking) {
                int meta = world.getBlockMeta(x, y, z);
                world.setBlockStateWithNotify(x, y, z, States.AIR.get());
                this.dropStacks(world, x, y, z, meta);
                return true;
            }
        }

        return false;
    }

    // Block Entity
    @Override
    protected BlockEntity createBlockEntity() {
        return blockEntityFactory.create(this);
    }
    
    // Bounding Box and Collision Shape
    private final float minOffset = PipeWorldRenderer.PIPE_MIN_POS;
    private final float maxOffset = PipeWorldRenderer.PIPE_MAX_POS;

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        PlayerEntity player = PlayerHelper.getPlayerFromGame();

        RaycastResult raycastResult = raycastPipe(world, x, y, z, player);
        if(raycastResult == null){
            return Box.createCached(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
        }
        else {
            return raycastResult.box.translate(x, y, z);
        }
    }

    @Override
    public void addIntersectingBoundingBox(World world, int x, int y, int z, Box box, ArrayList boxes) {
        BlockState state = world.getBlockState(x, y, z);

        this.setBoundingBox(minOffset, minOffset, minOffset, maxOffset, maxOffset, maxOffset);
        super.addIntersectingBoundingBox(world, x, y, z, box, boxes);

        if (state.get(Properties.UP)) {
            this.setBoundingBox(minOffset, minOffset, minOffset, maxOffset, 1.0F, maxOffset);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.DOWN)) {
            this.setBoundingBox(minOffset, 0.0F, minOffset, maxOffset, maxOffset, maxOffset);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.WEST)) {
            this.setBoundingBox(minOffset, minOffset, minOffset, maxOffset, maxOffset, 1.0F);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.EAST)) {
            this.setBoundingBox(minOffset, minOffset, 0.0F, maxOffset, maxOffset, maxOffset);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.SOUTH)) {
            this.setBoundingBox(minOffset, minOffset, minOffset, 1.0F, maxOffset, maxOffset);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.NORTH)) {
            this.setBoundingBox(0.0F, minOffset, minOffset, maxOffset, maxOffset, maxOffset);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public HitResult raycast(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos) {
        RaycastResult raycastResult = raycastPipe(world, x, y, z, startPos, endPos);
        if(raycastResult == null){
            return null;
        } else {
            return raycastResult.hit;
        }

    }

    public RaycastResult raycastPipe(World world, int x, int y, int z, PlayerEntity player){
        double distance = 5d;
        Vec3d positionVector = player.getPosition(tickDelta);
        Vec3d lookVector = player.getLookVector(tickDelta);

        Vec3d endVector = positionVector.add(lookVector.x * distance, lookVector.y * distance, lookVector.z * distance);

        return raycastPipe(world, x, y, z, positionVector, endVector);
    }

    private RaycastResult raycastPipe(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos){
        HitResult[] hits = new HitResult[31];
        Box[] boxes = new Box[31];
        Direction[] sideHit = new Direction[31];

        BlockState blockState = world.getBlockState(x, y, z);


        Box box = getPipeBoundingBox(null);
        setBoundingBox(box);
        boxes[6] = box;
        hits[6] = super.raycast(world, x, y, z, startPos, endPos);
        sideHit[6] = null;
        for(Direction side : Direction.values()){
            if(isPipeConnected(blockState, side)){
                box = getPipeBoundingBox(side);
                setBoundingBox(box);
                boxes[side.ordinal()] = box;
                hits[side.ordinal()] = super.raycast(world, x, y, z, startPos, endPos);
                sideHit[side.ordinal()] = side;
            }
        }

        // get closest hit

        double minLengthSquared = Double.POSITIVE_INFINITY;
        int minIndex = -1;

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

        // TODO: handling which part gets hit, will be fixed later
        if (minIndex == -1) {
            return null;
        } else {
            //Part hitPart;

            if (minIndex < 7) {
                //hitPart = Part.Pipe;
            } else {
                //hitPart = Part.Pluggable;
            }

            return new RaycastResult(hits[minIndex], boxes[minIndex], sideHit[minIndex]);
        }
    }

    boolean isPipeConnected(BlockState blockState, Direction direction){
        switch (direction){
            case UP:
                if(blockState.contains(Properties.UP)){
                    return blockState.get(Properties.UP);
                }
                break;
            case DOWN:
                if(blockState.contains(Properties.DOWN)){
                    return blockState.get(Properties.DOWN);
                }
                break;
            case WEST:
                if(blockState.contains(Properties.WEST)){
                    return blockState.get(Properties.WEST);
                }
                break;
            case EAST:
                if(blockState.contains(Properties.EAST)){
                    return blockState.get(Properties.EAST);
                }
                break;
            case NORTH:
                if(blockState.contains(Properties.NORTH)){
                    return blockState.get(Properties.NORTH);
                }
                break;
            case SOUTH:
                if(blockState.contains(Properties.SOUTH)){
                    return blockState.get(Properties.SOUTH);
                }
                break;
        }
        return false;
    }

    private void setBoundingBox(Box box){
        setBoundingBox((float) box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ);
    }

    // Rendering
    @Environment(EnvType.CLIENT)
    @Override
    public int getTexture(int side) {
        if(Atlases.getTerrain() == null){
            return 0;
        }
        return Atlases.getTerrain().getTexture(texture).index;
    }
    
    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderWorld(BlockRenderManager blockRenderManager, BlockView blockView, int x, int y, int z) {
        if(blockView.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe){
            pipeWorldRenderer.renderPipe(blockRenderManager, blockView, pipe, x, y, z);
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void renderInventory(BlockRenderManager blockRenderManager, int i) {
        pipeItemRenderer.renderPipeItem(blockRenderManager, this, i, -0.5F, -0.5F, -0.5F);
    }

    // Debug
    @Override
    public void debug(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side) {
        if (world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe) {
            System.out.println(pipe);
            player.sendMessage(pipe.toString());
        }
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            if (player.getHand() != null && !(player.getHand().getItem() instanceof WrenchBase)) {
                if (world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe) {
                    if (pipe.transporter instanceof ItemPipeTransporter pipeTransporter) {
                        pipeTransporter.injectItem(player.getHand(), Direction.values()[new Random().nextInt(Direction.values().length)]);
                        player.inventory.main[player.inventory.selectedSlot] = null;
                        player.inventory.markDirty();
                        return true;
                    }
                }
            }
        }
        
        return super.onUse(world, x, y, z, player);
    }

    private boolean addOrStripPipePluggable(World world, int x, int y, int z, ItemStack stack, PlayerEntity player, Direction side, PipeBlockEntity pipe){
        return false;
    }

    private Box getPipeBoundingBox(@Nullable Direction side){
        if(side == null){
            return Box.createCached(minOffset, minOffset, minOffset, maxOffset, maxOffset, maxOffset);
        }

        float[][] bounds = new float[3][2];

        bounds[0][0] = minOffset;
        bounds[0][1] = maxOffset;

        bounds[1][0] = 0;
        bounds[1][1] = minOffset;

        bounds[2][0] = minOffset;
        bounds[2][1] = maxOffset;

        MatrixTransformation.transform(bounds, side);
        return Box.createCached(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
    }

    // Property Lookup
    public static final HashMap<Direction, BooleanProperty> PROPERTY_LOOKUP = new HashMap<>();

    static {
        PROPERTY_LOOKUP.put(Direction.UP, Properties.UP);
        PROPERTY_LOOKUP.put(Direction.DOWN, Properties.DOWN);
        PROPERTY_LOOKUP.put(Direction.NORTH, Properties.NORTH);
        PROPERTY_LOOKUP.put(Direction.SOUTH, Properties.SOUTH);
        PROPERTY_LOOKUP.put(Direction.EAST, Properties.EAST);
        PROPERTY_LOOKUP.put(Direction.WEST, Properties.WEST);
    }
}
