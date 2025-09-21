package net.danygames2014.buildcraft.block.entity.pipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.danygames2014.buildcraft.client.render.block.PipePluggableState;
import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.buildcraft.packet.clientbound.PipeUpdatePacket;
import net.danygames2014.buildcraft.util.DirectionUtil;
import net.danygames2014.buildcraft.util.PipeUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Random;

public class PipeBlockEntity extends BlockEntity {
    public PipeBlock pipeBlock;
    public PipeBehavior behavior;
    public PipeTransporter transporter;
    public final PipeRenderState renderState = new PipeRenderState();
    public final PipePluggableState pluggableState = new PipePluggableState();
    public ObjectArrayList<Direction> validOutputDirections = null;
    public final Random random = new Random();

    public boolean[] wireSet = new boolean[]{false, false, false, false};
    public int[] signalStrength = new int[]{0, 0, 0, 0};

    public Object2ObjectOpenHashMap<Direction, PipeConnectionType> connections = null;

    protected PipeSideProperties sideProperties = new PipeSideProperties();
    protected boolean attachPluggables = false;

    protected boolean neighbourUpdate = false;
    protected boolean refreshRenderState = false;

    // Empty constructor for loading
    public PipeBlockEntity() {
    }

    // Normal constructor for when the pipe is first created
    public PipeBlockEntity(PipeBlock pipeBlock) {
        this.pipeBlock = pipeBlock;
        init();
    }

    // Init
    private boolean hasInit = false;

    public void init() {
        behavior = pipeBlock.behavior;
        transporter = pipeBlock.transporterFactory.create(this);
        transporter.init();
        scheduleRenderUpdate();
        hasInit = true;
    }

    // Tick
    @Override
    public void tick() {
        super.tick();

        if (!hasInit) {
            init();
        }

        if (connections == null) {
            updateConnections();
            updateValidOutputDirections();
        }

        // TODO: This does more, but I only added the render related code for now
        if (neighbourUpdate) {
            refreshRenderState = true;
            neighbourUpdate = false;
            updateConnections();
        }

        if (refreshRenderState) {
            refreshRenderState();
            refreshRenderState = false;
        }

        if(attachPluggables){
            attachPluggables = false;
            for(int i = 0; i < Direction.values().length; i++){
                if(sideProperties.pluggables[i] != null){
                    // TODO: figure out what this does
                    //pipe.eventBus.registerHandler(sideProperties.pluggables[i]);
                    sideProperties.pluggables[i].onAttachedToPipe(this, Direction.byId(i));
                }
            }
            markDirty();
        }

        transporter.tick();
    }

    public void updateConnections() {
        if (connections == null) {
            connections = new Object2ObjectOpenHashMap<>(6);
        }

        // Clean out old connections
        for (Direction side : Direction.values()) {
            if (connections.get(side) != PipeConnectionType.NONE && canConnectTo(x,y,z, side) == PipeConnectionType.NONE) {
                connections.put(side, PipeConnectionType.NONE);
            }
        }

        // Update connections
        for (Direction side : Direction.values()) {
            //System.out.println(side + " -> " + world.getBlockState(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ()) + " -> " + world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ()));
            connections.put(side, canConnectTo(x, y, z, side));
        }
    }
    
    public void updateValidOutputDirections() {
        if (validOutputDirections == null) {
            validOutputDirections = new ObjectArrayList<>(6);
        }

        validOutputDirections.clear();
        for (Direction side : Direction.values()) {
            if (connections.get(side) != PipeConnectionType.NONE) {
                validOutputDirections.add(side);
            }
        }
    }

    public void neighborUpdate() {
        neighbourUpdate = true;
    }

    public void scheduleRenderUpdate() {
        refreshRenderState = true;
    }

    // Pipe Logic
    /**
     * @param x    The x position of this block
     * @param y    The y position of this block
     * @param z    The z position of this block
     * @param side The side on which the target block is located
     * @return Whether this pipe can connect to the target block
     */
    public PipeConnectionType canConnectTo(int x, int y, int z, Direction side) {
        BlockEntity other = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());

        if (other == null) {
            return PipeConnectionType.NONE;
        }
        if(hasBlockingPluggable(side)){
            if(other instanceof PipeBlockEntity pipe && pipe.connections.get(side.getOpposite()) != PipeConnectionType.NONE){
                pipe.neighborUpdate();
            }
            return PipeConnectionType.NONE;
        }

        if (other instanceof PipeBlockEntity pipe) {
            if(pipe.hasBlockingPluggable(side.getOpposite())){
                return PipeConnectionType.NONE;
            }

//            if(pipe.canConnectTo(x, y, z, side.getOpposite()) == PipeConnectionType.NONE){
//                return PipeConnectionType.NONE;
//            }

            return behavior.canConnectToPipe(this, pipe, pipe.behavior);
        }

        PipeConnectionType transporterConnectType = behavior.getConnectionType(transporter.getType(), this, world, x, y, z, side);
        //noinspection RedundantIfStatement
        if (transporterConnectType != PipeConnectionType.NONE) {
            return transporterConnectType;
        }

        return PipeConnectionType.NONE;
    }

    public boolean hasBlockingPluggable(Direction side){
        PipePluggable pluggable = getPipePluggable(side);
        if(pluggable == null){
            return false;
        }

        return pluggable.isBlocking(this, side);
    }

    public void onBreak() {
        if (transporter != null) {
            transporter.onBreak();
        }
    }

    public boolean hasFacade(Direction direction){
        throw new NotImplementedException();
    }

    public boolean hasGate(Direction direction){
        throw new NotImplementedException();
    }

    public boolean setPluggable(Direction direction, PipePluggable pluggable) {
        return setPluggable(direction, pluggable, null);
    }

    public boolean setPluggable(Direction direction, PipePluggable pluggable, PlayerEntity player){
        if(world != null && world.isRemote){
            return false;
        }

        if(direction == null){
            return false;
        }

        if(sideProperties.pluggables[direction.ordinal()] != null){
            sideProperties.dropItem(this, direction, player);
            // TODO: implement this later
            //pipe.eventBus.unregisterHandler(sideProperties.pluggables[direction.ordinal()]);
        }

        sideProperties.pluggables[direction.ordinal()] = pluggable;
        if(pluggable != null){
            //pipe.eventBus.registerHandler(pluggable);
            pluggable.onAttachedToPipe(this, direction);
        }
        markDirty();
        return true;
    }

    public PipePluggable getPipePluggable(Direction side){
        if(side == null){
            return null;
        }
        return sideProperties.pluggables[side.ordinal()];
    }

    public boolean hasPipePluggable(Direction side){
        if (side == null) {
            return false;
        }

        return sideProperties.pluggables[side.ordinal()] != null;
    }

    @Override
    public void markDirty() {
        updateConnections();
        scheduleRenderUpdate();
        super.markDirty();
    }

    public boolean isWireConnectedTo(BlockEntity blockEntity, PipeWire color, Direction direction){
        if(!(blockEntity instanceof PipeBlockEntity pipe)){
            return false;
        }

        if(!pipe.wireSet[color.ordinal()]){
            return false;
        }

        if(hasBlockingPluggable(direction) || pipe.hasBlockingPluggable(direction.getOpposite())){
            return false;
        }

        return true; //PipeUtil.checkPipeConnections(this, pipe);
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("pipeId", String.valueOf(BlockRegistry.INSTANCE.getId(pipeBlock)));

        // Connections
        NbtList connections = new NbtList();
        for (var side : this.connections.entrySet()) {
            NbtCompound connection = new NbtCompound();
            connection.putInt("side", side.getKey().ordinal());
            connection.putInt("type", side.getValue().ordinal());
            connections.add(connection);
        }
        nbt.put("connections", connections);
        for (int i = 0; i < 4; ++i) {
            nbt.putBoolean("wireSet[" + i + "]", wireSet[i]);
        }
        sideProperties.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.pipeBlock = (PipeBlock) BlockRegistry.INSTANCE.get(Identifier.of(nbt.getString("pipeId")));
        // Connections
        if (connections == null) {
            connections = new Object2ObjectOpenHashMap<>(6);
        }

        NbtList connections = nbt.getList("connections");
        for (int i = 0; i < connections.size(); i++) {
            NbtCompound connection = (NbtCompound) connections.get(i);
            Direction side = Direction.byId(connection.getInt("side"));
            PipeConnectionType type = PipeConnectionType.values()[connection.getInt("type")];
            this.connections.put(side, type);
        }

        updateValidOutputDirections();

        for (int i = 0; i < 4; ++i) {
            wireSet[i] = nbt.getBoolean("wireSet[" + i + "]");
        }

        sideProperties.readNbt(nbt);
        init();
        attachPluggables = true;
    }

    @Override
    public String toString() {
        return "PipeBlockEntity{" +
                "pipeBlock=" + pipeBlock +
                ", behavior=" + behavior +
                ", transporter=" + transporter +
                ", renderState=" + renderState +
                ", validOutputDirections=" + validOutputDirections +
                ", random=" + random +
                ", connections=" + connections +
                ", neighbourUpdate=" + neighbourUpdate +
                ", refreshRenderState=" + refreshRenderState +
                ", hasInit=" + hasInit +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", world=" + world +
                '}';
    }

    public BlockEntity getBlockEntity(Direction direction){
        return world.getBlockEntity(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());
    }

    protected void refreshRenderState() {
        // Pipe connections:
        for (Direction direction : Direction.values()) {
            // TODO: Actually use the returned connection type here to swap the texture when neeeded, no fucking idea how to do it, sorry ralf
            renderState.pipeConnectionMatrix.setConnected(direction, this.canConnectTo(x, y, z, direction) != PipeConnectionType.NONE);
        }

        for (int i = 0; i < 7; i++) {
            Direction direction = DirectionUtil.getById(i);
            renderState.textureMatrix.setTextureIndex(direction, pipeBlock.getTextureForSide(direction, direction != null ? this.canConnectTo(x, y, z, direction) : null));
        }

        for(PipeWire color : PipeWire.values()){
            renderState.wireMatrix.setWire(color, wireSet[color.ordinal()]);

            for(Direction direction : Direction.values()){
                renderState.wireMatrix.setWireConnected(color, direction, isWireConnectedTo(getBlockEntity(direction), color, direction));
            }

            boolean lit = signalStrength[color.ordinal()] > 0;

            switch (color){
                case RED:
                    renderState.wireMatrix.setWireTextureIndex(color, lit ? TextureListener.redPipeWireLit.index : TextureListener.redPipeWire.index);
                    break;
                case BLUE:
                    renderState.wireMatrix.setWireTextureIndex(color, lit ? TextureListener.bluePipeWireLit.index : TextureListener.bluePipeWire.index);
                    break;
                case GREEN:
                    renderState.wireMatrix.setWireTextureIndex(color, lit ? TextureListener.greenPipeWireLit.index : TextureListener.greenPipeWire.index);
                    break;
                case YELLOW:
                    renderState.wireMatrix.setWireTextureIndex(color, lit ? TextureListener.yellowPipeWireLit.index : TextureListener.yellowPipeWire.index);
                    break;
            }
        }

        pluggableState.setPluggables(sideProperties.pluggables);

        if(renderState.isDirty()){
            renderState.clean();
        }
    }

    @Override
    public Packet createUpdatePacket() {
        return new PipeUpdatePacket(renderState, new BlockPos(x ,y, z));
    }
}
