package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.api.core.Debuggable;
import net.danygames2014.buildcraft.block.entity.pipe.ItemPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBehavior;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeTransporter;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.client.render.item.PipeItemRenderer;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
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
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.client.model.block.BlockWithInventoryRenderer;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("deprecation")
public class PipeBlock extends TemplateBlockWithEntity implements Wrenchable, Debuggable, BlockWithWorldRenderer, BlockWithInventoryRenderer {
    public final PipeBehavior behavior;
    public final PipeTransporter.PipeTransporterFactory transporterFactory;
    @Environment(EnvType.CLIENT)
    private PipeWorldRenderer pipeWorldRenderer;
    @Environment(EnvType.CLIENT)
    private PipeItemRenderer pipeItemRenderer;
    @Environment(EnvType.CLIENT)
    private Identifier texture;

    public PipeBlock(Identifier identifier, Material material, Identifier texture, PipeBehavior behavior, PipeTransporter.PipeTransporterFactory transporter) {
        super(identifier, material);
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
        return new PipeBlockEntity(this);
    }
    
    // Bounding Box and Collision Shape
    private final float minOffset = 0.25F;
    private final float maxOffset = 0.75F;

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);

        float minX = minOffset;
        float minY = minOffset;
        float minZ = minOffset;

        float maxX = maxOffset;
        float maxY = maxOffset;
        float maxZ = maxOffset;

        if (state.get(Properties.UP)) {
            maxY = 1.0F;
        }

        if (state.get(Properties.DOWN)) {
            minY = 0.0F;
        }

        if (state.get(Properties.WEST)) {
            maxZ = 1.0F;
        }

        if (state.get(Properties.EAST)) {
            minZ = 0.0F;
        }

        if (state.get(Properties.NORTH)) {
            minX = 0.0F;
        }

        if (state.get(Properties.SOUTH)) {
            maxX = 1.0F;
        }

        return Box.createCached(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
    }

    @Override
    public void addIntersectingBoundingBox(World world, int x, int y, int z, Box box, ArrayList boxes) {
        BlockState state = world.getBlockState(x, y, z);

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
