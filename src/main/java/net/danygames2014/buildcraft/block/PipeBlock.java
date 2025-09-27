package net.danygames2014.buildcraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.danygames2014.buildcraft.api.core.Debuggable;
import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.PipeBehavior;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.client.render.item.PipeItemRenderer;
import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.buildcraft.item.PipeWireItem;
import net.danygames2014.buildcraft.pluggable.FacadePluggable;
import net.danygames2014.buildcraft.util.ItemUtil;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.danygames2014.buildcraft.util.RaycastResult;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.api.Wrenchable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
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
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.client.model.block.BlockWithInventoryRenderer;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class PipeBlock extends TemplateBlockWithEntity implements Wrenchable, Debuggable, BlockWithWorldRenderer, BlockWithInventoryRenderer {
    public final PipeBehavior behavior;
    public final PipeTransporter.PipeTransporterFactory transporterFactory;
    public final PipeBlockEntityFactory blockEntityFactory;

    public static int lastSideUsed;
    @Environment(EnvType.CLIENT)
    public static float tickDelta;
    @Environment(EnvType.CLIENT)
    private PipeWorldRenderer pipeWorldRenderer;
    @Environment(EnvType.CLIENT)
    private PipeItemRenderer pipeItemRenderer;
    private Identifier texture;
    @Nullable
    private Identifier alternativeTexture;

    public PipeBlock(Identifier identifier, Material material, Identifier texture, @Nullable Identifier alternativeTexture, PipeBehavior behavior, PipeTransporter.PipeTransporterFactory transporter, PipeBlockEntityFactory blockEntityFactory) {
        super(identifier, material);
        this.blockEntityFactory = blockEntityFactory;
        this.behavior = behavior;
        this.transporterFactory = transporter;
        this.texture = texture;
        this.alternativeTexture = alternativeTexture;
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            TextureListener.dynamicBlockTextures.add(texture);
            if(alternativeTexture != null){
                TextureListener.dynamicBlockTextures.add(alternativeTexture);
            }
            this.pipeWorldRenderer = new PipeWorldRenderer();
            this.pipeItemRenderer = new PipeItemRenderer();
        }
    }

    // Connecting Logic
    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);
        updateConnections(world, x, y, z);
        if (world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipeBlockEntity) {
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
        if (world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipeBlockEntity) {
            pipeBlockEntity.onBreak();
        }
        super.onBreak(world, x, y, z);
    }

    public void updateConnections(World world, int x, int y, int z) {
        if (world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe) {
            pipe.updateConnections();
        }
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

    @Environment(EnvType.CLIENT)
    @Override
    public int getTexture(int side) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || Atlases.getTerrain() == null) {
            return 0;
        }
        if(Atlases.getTerrain().getTexture(texture) != null){
            return Atlases.getTerrain().getTexture(texture).index;
        }
        return 0;
    }

    public Identifier getTextureIdentifierForSide(@Nullable Direction direction, @Nullable PipeConnectionType connectionType){
        if(direction != null && alternativeTexture != null && connectionType == PipeConnectionType.ALTERNATE){
            return alternativeTexture;
        }
        return texture;
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
        if (raycastResult == null) {
            return Box.createCached(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
        } else {
            return raycastResult.box.translate(x, y, z);
        }
    }

    @Override
    public void addIntersectingBoundingBox(World world, int x, int y, int z, Box box, ArrayList boxes) {
        if (world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe) {
            Object2ObjectOpenHashMap<Direction, PipeConnectionType> connections = pipe.connections;

            this.setBoundingBox(minOffset, minOffset, minOffset, maxOffset, maxOffset, maxOffset);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);

            if (connections.get(Direction.UP) != PipeConnectionType.NONE) {
                this.setBoundingBox(minOffset, minOffset, minOffset, maxOffset, 1.0F, maxOffset);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (connections.get(Direction.DOWN) != PipeConnectionType.NONE) {
                this.setBoundingBox(minOffset, 0.0F, minOffset, maxOffset, maxOffset, maxOffset);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (connections.get(Direction.WEST) != PipeConnectionType.NONE) {
                this.setBoundingBox(minOffset, minOffset, minOffset, maxOffset, maxOffset, 1.0F);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (connections.get(Direction.EAST) != PipeConnectionType.NONE) {
                this.setBoundingBox(minOffset, minOffset, 0.0F, maxOffset, maxOffset, maxOffset);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (connections.get(Direction.SOUTH) != PipeConnectionType.NONE) {
                this.setBoundingBox(minOffset, minOffset, minOffset, 1.0F, maxOffset, maxOffset);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (connections.get(Direction.NORTH) != PipeConnectionType.NONE) {
                this.setBoundingBox(0.0F, minOffset, minOffset, maxOffset, maxOffset, maxOffset);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            float facadeThickness = FacadePluggable.FACADE_THICKNESS;

            if (pipe.hasEnabledFacade(Direction.SOUTH)) {
                setBoundingBox(1 - facadeThickness, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (pipe.hasEnabledFacade(Direction.NORTH)) {
                setBoundingBox(0.0F, 0.0F, 0.0F, facadeThickness, 1.0F, 1.0F);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (pipe.hasEnabledFacade(Direction.UP)) {
                setBoundingBox(0.0F, 1 - facadeThickness, 0.0F, 1.0F, 1.0F, 1.0F);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (pipe.hasEnabledFacade(Direction.DOWN)) {
                setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, facadeThickness, 1.0F);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (pipe.hasEnabledFacade(Direction.WEST)) {
                setBoundingBox(0.0F, 0.0F, 1 - facadeThickness, 1.0F, 1.0F, 1.0F);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            if (pipe.hasEnabledFacade(Direction.EAST)) {
                setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, facadeThickness);
                super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
            }

            this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public HitResult raycast(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos) {
        RaycastResult raycastResult = raycastPipe(world, x, y, z, startPos, endPos);
        if (raycastResult == null) {
            return null;
        } else {
            return raycastResult.hit;
        }

    }

    public RaycastResult raycastPipe(World world, int x, int y, int z, PlayerEntity player) {
        double distance = 5d;
        Vec3d positionVector = player.getPosition(tickDelta);
        Vec3d lookVector = player.getLookVector(tickDelta);

        Vec3d endVector = positionVector.add(lookVector.x * distance, lookVector.y * distance, lookVector.z * distance);

        return raycastPipe(world, x, y, z, positionVector, endVector);
    }

    private RaycastResult raycastPipe(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos){
        if(!(world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe)){
            return null;
        }

        HitResult[] hits = new HitResult[31];
        Box[] boxes = new Box[31];
        Direction[] sideHit = new Direction[31];

        Box box = getPipeBoundingBox(null);
        setBoundingBox(box);
        boxes[6] = box;
        hits[6] = super.raycast(world, x, y, z, startPos, endPos);
        sideHit[6] = null;
        for (Direction side : Direction.values()) {
            if (isPipeConnected(pipe, side)) {
                box = getPipeBoundingBox(side);
                setBoundingBox(box);
                boxes[side.ordinal()] = box;
                hits[side.ordinal()] = super.raycast(world, x, y, z, startPos, endPos);
                sideHit[side.ordinal()] = side;
            }
        }

        for(Direction side : Direction.values()){
            if(pipe.getPipePluggable(side) != null){
                Box pluggableBox = pipe.getPipePluggable(side).getBoundingBox(side);
                setBoundingBox(pluggableBox);
                boxes[7 + side.ordinal()] = pluggableBox;
                hits[7 + side.ordinal()] = super.raycast(world, x, y, z, startPos, endPos);
                sideHit[7 + side.ordinal()] = side;
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
            RaycastResult.Part hitPart;

            if (minIndex < 7) {
                hitPart = RaycastResult.Part.Pipe;
            } else {
                hitPart = RaycastResult.Part.Pluggable;
            }

            return new RaycastResult(hitPart, hits[minIndex], boxes[minIndex], sideHit[minIndex]);
        }
    }

    boolean isPipeConnected(PipeBlockEntity pipe, Direction direction) {
        if (pipe.connections != null) {
            return pipe.connections.get(direction) != PipeConnectionType.NONE;
        }

        return false;
    }

    private void setBoundingBox(Box box) {
        setBoundingBox((float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
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
        if (blockView.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe) {
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
    public boolean isSolidFace(BlockView blockView, int x, int y, int z, int face) {
        if(blockView.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe){
            Direction side = Direction.byId(face);
            return pipe.hasPipePluggable(side) && pipe.getPipePluggable(side).isSolidOnSide();
        }
        return false;
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if(!(world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe)){
            return false;
        }
        ItemStack stack = player.getHand();
        if(stack == null && player.isSneaking()){
            if(stripEquipment(world, x, y, z, player, pipe, Direction.byId(lastSideUsed))){
                world.setBlockDirty(x, y, z);
                world.notifyNeighbors(x, y, z, id);
                return true;
            }
        }
        if(stack != null && stack.getItem() instanceof PipeWireItem pipeWireItem){
            if(addOrStripWire(player, pipe, PipeWire.fromItem(pipeWireItem))){
                world.setBlockDirty(x, y, z);
                world.notifyNeighbors(x, y, z, id);
                return true;
            }
        }
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            if(player.getHand() != null && player.getHand().getItem() instanceof PipePluggableItem){
                return false;
            }
//            if (player.getHand() != null && !(player.getHand().getItem() instanceof WrenchBase)) {
//                if (pipe.transporter instanceof ItemPipeTransporter pipeTransporter) {
//                    pipeTransporter.injectItem(player.getHand(), Direction.values()[new Random().nextInt(Direction.values().length)]);
//                    player.inventory.main[player.inventory.selectedSlot] = null;
//                    player.inventory.markDirty();
//                    return true;
//                }
//            }
        }

        return super.onUse(world, x, y, z, player);
    }

    // TODO: might be unneeded now that lastSideUsed is a thing
    public boolean onUseItem(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side){
        if(!(world.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe)){
            return false;
        }
        if(stack == null){
            return false;
        }
        if(stack.getItem() instanceof  PipePluggableItem){
            if(addOrStripPipePluggable(world, x, y, z, stack, user, Direction.byId(side), pipe)){
                world.notifyNeighbors(x, y, z, id);
                world.setBlockDirty(x, y, z);
                return true;
            }
        }
        return false;
    }

    private boolean stripEquipment(World world, int x, int y, int z, PlayerEntity player, PipeBlockEntity pipe, Direction side){
        if(!world.isRemote){
            Direction nSide = side;

            RaycastResult raycastResult = raycastPipe(world, x, y, z, player);
            if(raycastResult != null && raycastResult.hitPart != RaycastResult.Part.Pipe){
                nSide = raycastResult.sideHit;
            }
            if(pipe.hasPipePluggable(nSide)){
                return pipe.setPluggable(nSide, null, player);
            }

            for(PipeWire color : PipeWire.values()){
                if(stripWire(pipe, color, player)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean addOrStripPipePluggable(World world, int x, int y, int z, ItemStack stack, PlayerEntity player, Direction side, PipeBlockEntity pipe){
        RaycastResult raycastResult = raycastPipe(world, x, y, z, player);

        Direction placementSide = raycastResult != null && raycastResult.sideHit != null ? raycastResult.sideHit : side;

        PipePluggableItem pluggableItem = (PipePluggableItem) stack.getItem();
        PipePluggable pluggable = pluggableItem.createPipePluggable(pipe, placementSide, stack);

        if(pluggable == null){
            return false;
        }

        if(player.isSneaking()){
            if(pipe.hasPipePluggable(side) && raycastResult != null && raycastResult.hitPart == RaycastResult.Part.Pluggable
                && pluggable.getClass().isInstance(pipe.getPipePluggable(side))){
                return pipe.setPluggable(side, null, player);
            }
        }

        if(raycastResult != null && raycastResult.hitPart == RaycastResult.Part.Pipe){
            if(!pipe.hasPipePluggable(side)){
                if(pipe.setPluggable(placementSide, pluggable, player)){
                    stack.count--;
                }
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    private boolean addOrStripWire(PlayerEntity player, PipeBlockEntity pipe, PipeWire color){
        if(addWire(pipe, color)){
            player.getHand().count --;
            return true;
        }
        return player.isSneaking() && stripWire(pipe, color, player);
    }

    private boolean addWire(PipeBlockEntity pipe, PipeWire color){
        if(!pipe.wireSet[color.ordinal()]){
            pipe.wireSet[color.ordinal()] = true;
            pipe.signalStrength[color.ordinal()] = 0;

            //pipe.updateSignalStrength
            pipe.scheduleRenderUpdate();
            return true;
        }
        return false;
    }

    private boolean stripWire(PipeBlockEntity pipe, PipeWire color, PlayerEntity player){
        if(pipe.wireSet[color.ordinal()]){
            if(!pipe.world.isRemote){
                dropWire(color, pipe, player);
            }

            pipe.signalStrength[color.ordinal()] = 0;
            pipe.wireSet[color.ordinal()] = false;

            //pipe.updateSignalStrength

            //updateNeighborSignalStrenth
            pipe.scheduleRenderUpdate();
            return true;
        }
        return false;
    }

    private void dropWire(PipeWire pipeWire, PipeBlockEntity pipe, PlayerEntity player){
        ItemUtil.dropTryIntoPlayerInventory(pipe.world, pipe.x, pipe.y, pipe.z, pipeWire.getStack(), player);
    }

    private Box getPipeBoundingBox(@Nullable Direction side) {
        if (side == null) {
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
}
