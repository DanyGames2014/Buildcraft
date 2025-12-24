package net.danygames2014.buildcraft.block.entity.pipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.api.core.SynchedBlockEntity;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.api.transport.statement.StatementSlot;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.PipeBehavior;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateFactory;
import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.danygames2014.buildcraft.client.render.block.PipePluggableState;
import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.buildcraft.packet.clientbound.BlockEntityUpdatePacket;
import net.danygames2014.buildcraft.pluggable.FacadePluggable;
import net.danygames2014.buildcraft.pluggable.GatePluggable;
import net.danygames2014.buildcraft.registry.StateRegistry;
import net.danygames2014.buildcraft.util.DirectionUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;
import java.util.Random;

public class PipeBlockEntity extends BlockEntity implements SynchedBlockEntity, Inventory {
    public PipeBlock pipeBlock;
    public PipeBehavior behavior;
    public PipeTransporter transporter;
    public final PipeRenderState renderState = new PipeRenderState();
    public final PipePluggableState pluggableState = new PipePluggableState();
    public ObjectArrayList<Direction> validOutputDirections = null;
    public final Random random = new Random();

    public boolean[] wireSet = new boolean[]{false, false, false, false};
    public int[] signalStrength = new int[]{0, 0, 0, 0};
    public final Gate[] gates = new Gate[Direction.values().length];

    public PipeEventBus eventBus = new PipeEventBus();

    public Object2ObjectOpenHashMap<Direction, PipeConnectionType> connections = null;

    public int redstoneInput;
    public int[] redstoneInputSide = new int[Direction.values().length];

    protected PipeSideProperties sideProperties = new PipeSideProperties();
    protected boolean attachPluggables = false;

    protected boolean neighbourUpdate = false;
    protected boolean refreshRenderState = false;
    protected boolean sendClientUpdate = false;
    protected boolean resyncGateExpansions = false;
    private boolean internalUpdateScheduled = false;

    // Empty constructor for loading
    public PipeBlockEntity() {
    }

    // Normal constructor for when the pipe is first created
    public PipeBlockEntity(PipeBlock pipeBlock) {
        this.pipeBlock = pipeBlock;
        //eventBus.registerHandler(this);
        init();
    }

    // Init
    private boolean hasInit = false;

    public void init() {
        if (pipeBlock == null) {
            if (world != null && y != 0 && world.getBlockState(x,y,z).getBlock() instanceof PipeBlock block) {
                pipeBlock = block;
            }
        }
        
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
        }

        // TODO: This does more, but I only added the render related code for now
        if (neighbourUpdate) {
            refreshRenderState = true;
            neighbourUpdate = false;
            updateConnections();
            transporter.onConnectionsUpdate();
        }

        if(internalUpdateScheduled){
            internalUpdate();
            internalUpdateScheduled = false;
        }

        if (refreshRenderState) {
            refreshRenderState();
            refreshRenderState = false;
        }

        if(sendClientUpdate) {
            sendClientUpdate = false;
            if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER){
                Packet updatePacket = getBlockEntityUpdatePacket();
                for(Object o : world.players){
                    PlayerEntity player = (PlayerEntity) o;
                    if(player.getDistance(x, y, z) < 40){
                        PacketHelper.sendTo(player, updatePacket);
                    }
                }
            }
        }

        if(attachPluggables){
            attachPluggables = false;
            for(int i = 0; i < Direction.values().length; i++){
                if(sideProperties.pluggables[i] != null){
                    // TODO: figure out what this does
                    eventBus.registerHandler(sideProperties.pluggables[i]);
                    sideProperties.pluggables[i].onAttachedToPipe(this, Direction.byId(i));
                }
            }
            markDirty();
        }

        transporter.tick();

        if(!world.isRemote){
            for(Gate gate : gates){
                if(gate != null) {
                    gate.resolveActions();
                    gate.tick();
                }
            }
        }

        for (Direction direction : Direction.values()) {
            PipePluggable p = getPipePluggable(direction);
            if (p != null) {
                p.update(this, direction);
            }
        }

        if(world.isRemote) {
            if(resyncGateExpansions) {
                syncGateExpansions();
            }
        }
    }

    public void updateConnections() {
        if (world == null || world.isRemote) {
            return;
        }
        
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
        
        updateValidOutputDirections();
    }
    
    public void updateValidOutputDirections() {
        if (validOutputDirections == null) {
            validOutputDirections = new ObjectArrayList<>(6);
        }

        validOutputDirections.clear();
        for (Direction side : Direction.values()) {
            if (behavior.isValidOutputDirection(this, side, connections.get(side))) {
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
        if(world.isRemote) {
            return renderState.pipeConnectionMatrix.getConnectionType(side);
        }
        
        BlockEntity other = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());

        if (other == null) {
            return PipeConnectionType.NONE;
        }
        
        if(hasBlockingPluggable(side)){
            if(other instanceof PipeBlockEntity otherPipe && otherPipe.connections.get(side.getOpposite()) != PipeConnectionType.NONE){
                otherPipe.neighborUpdate();
            }
            return PipeConnectionType.NONE;
        }

        if (other instanceof PipeBlockEntity otherPipe) {
            if(otherPipe.hasBlockingPluggable(side.getOpposite())) {
                return PipeConnectionType.NONE;
            }
            
            if (otherPipe.transporter == null) {
                return PipeConnectionType.NONE;
            }
            
            return behavior.canConnectToPipe(this, otherPipe, otherPipe.behavior, side);
        }

        PipeConnectionType transporterConnectType = behavior.getConnectionType(transporter.getType(), this, world, x, y, z, side);
        //noinspection RedundantIfStatement
        if (transporterConnectType != PipeConnectionType.NONE) {
            return transporterConnectType;
        }

        return PipeConnectionType.NONE;
    }

    public void actionsActivated(Collection<StatementSlot> actions) {
    }

    protected void notifyBlocksOfNeighborChange(Direction side) {
        world.notifyNeighbors(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), 0);
    }

    public void updateNeighbors(boolean needSelf) {
        if (needSelf) {
            world.notifyNeighbors(x, y, z, 0);
        }
        for (Direction side : Direction.values()) {
            notifyBlocksOfNeighborChange(side);
        }
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
        if(direction == null){
            return false;
        } else {
            return sideProperties.pluggables[direction.ordinal()] instanceof FacadePluggable;
        }
    }

    public boolean hasEnabledFacade(Direction direction){
        return hasFacade(direction) && !((FacadePluggable) getPipePluggable(direction)).isTransparent();
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
            eventBus.unregisterHandler(sideProperties.pluggables[direction.ordinal()]);
        }

        sideProperties.pluggables[direction.ordinal()] = pluggable;
        if(pluggable != null){
            eventBus.registerHandler(pluggable);
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

    private void readNearbyPipesSignal(PipeWire color){
        boolean foundBiggerSignal = false;

        for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = getBlockEntity(direction);

            if (blockEntity instanceof PipeBlockEntity pipe) {
                if (isWireConnectedTo(blockEntity, color, direction)) {
                    foundBiggerSignal |= receiveSignal(pipe.signalStrength[color.ordinal()] - 1, color);
                }
            }
        }

        if (!foundBiggerSignal && signalStrength[color.ordinal()] != 0) {
            signalStrength[color.ordinal()] = 0;
            scheduleRenderUpdate();

            for (Direction direction : Direction.values()) {
                BlockEntity blockEntity = getBlockEntity(direction);

                if (blockEntity instanceof PipeBlockEntity pipe) {
                    pipe.internalUpdateScheduled = true;
                }
            }
        }
    }

    public void updateSignalState() {
        for (PipeWire color : PipeWire.values()) {
            updateSignalStateForColor(color);
        }
    }

    private void updateSignalStateForColor(PipeWire wire) {
        if (!wireSet[wire.ordinal()]) {
            return;
        }

        // STEP 1: compute internal signal strength

        boolean readNearbySignal = true;
        for (Gate gate : gates) {
            if (gate != null && gate.broadcastSignal.get(wire.ordinal())) {
                receiveSignal(255, wire);
                readNearbySignal = false;
            }
        }

        if (readNearbySignal) {
            readNearbyPipesSignal(wire);
        }

        // STEP 2: transmit signal in nearby blocks

        if (signalStrength[wire.ordinal()] > 1) {
            for (Direction direction : Direction.values()) {
                BlockEntity blockEntity = getBlockEntity(direction);

                if (blockEntity instanceof PipeBlockEntity pipe) {

                    if (pipe.wireSet[wire.ordinal()]) {
                        if (isWireConnectedTo(blockEntity, wire, direction)) {
                            pipe.receiveSignal(signalStrength[wire.ordinal()] - 1, wire);
                        }
                    }
                }
            }
        }
    }


    private boolean receiveSignal(int signal, PipeWire color) {
        if (world == null) {
            return false;
        }

        int oldSignal = signalStrength[color.ordinal()];

        if (signal >= signalStrength[color.ordinal()] && signal != 0) {
            signalStrength[color.ordinal()] = signal;
            internalUpdateScheduled = true;

            if (oldSignal == 0) {
                scheduleRenderUpdate();
            }

            return true;
        } else {
            return false;
        }
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

        for (int i = 0; i < Direction.values().length; i++) {
            final String key = "redstoneInputSide[" + i + "]";
            nbt.putByte(key, (byte) redstoneInputSide[i]);
        }

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
        for(int i = 0; i < Direction.values().length; i++){
            final String key = "Gate[" + i + "]";
            Gate gate = gates[i];
            if (gate != null) {
                NbtCompound gateNBT = new NbtCompound();
                gate.writeToNBT(gateNBT);
                nbt.put(key, gateNBT);
            }
        }
        sideProperties.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.pipeBlock = (PipeBlock) BlockRegistry.INSTANCE.get(Identifier.of(nbt.getString("pipeId")));
        
        if (this.pipeBlock == null) {
            throw new IllegalStateException("PipeBlockEntity does not have any pipe block");
        }

        redstoneInput = 0;

        for (int i = 0; i < Direction.values().length; i++) {
            final String key = "redstoneInputSide[" + i + "]";
            if (nbt.contains(key)) {
                redstoneInputSide[i] = nbt.getByte(key);

                if (redstoneInputSide[i] > redstoneInput) {
                    redstoneInput = redstoneInputSide[i];
                }
            } else {
                redstoneInputSide[i] = 0;
            }
        }
        
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

        for (int i = 0; i < 4; ++i) {
            wireSet[i] = nbt.getBoolean("wireSet[" + i + "]");
        }

        for (int i = 0; i < Direction.values().length; i++) {
            final String key = "Gate[" + i + "]";
            gates[i] = nbt.contains(key) ? GateFactory.makeGate(this, nbt.getCompound(key)) : null;
        }

        sideProperties.readNbt(nbt);
        init();
        updateValidOutputDirections();
        
        attachPluggables = true;
    }

    @Override
    public String toString() {
        return "PipeBlockEntity{" +
                "pipeBlock=" + pipeBlock +
                ", behavior=" + behavior +
                ", transporter=" + transporter +
                ", validOutputDirections=" + validOutputDirections +
                ", connections=" + connections +
                ", neighbourUpdate=" + neighbourUpdate +
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
        if (world == null || world.isRemote) {
            return;
        }
        
        // Pipe connections:
        for (Direction direction : Direction.values()) {
            // TODO: Actually use the returned connection type here to swap the texture when neeeded, no fucking idea how to do it, sorry ralf
            renderState.pipeConnectionMatrix.setConnected(direction, connections.get(direction));
            //renderState.pipeConnectionMatrix.setConnected(direction, this.canConnectTo(x, y, z, direction));
        }

        for (int i = 0; i < 7; i++) {
            Direction direction = DirectionUtil.getById(i);
            renderState.textureMatrix.setTextureIdentifier(direction, pipeBlock.getTextureIdentifierForSide(direction, direction != null ? this.canConnectTo(x, y, z, direction) : null));
        }

        for(PipeWire color : PipeWire.values()){
            renderState.wireMatrix.setWire(color, wireSet[color.ordinal()]);

            for(Direction direction : Direction.values()){
                renderState.wireMatrix.setWireConnected(color, direction, isWireConnectedTo(getBlockEntity(direction), color, direction));
            }

            boolean lit = signalStrength[color.ordinal()] > 0;

            switch (color){
                case RED:
                    renderState.wireMatrix.setWireTextureIdentifier(color, lit ? TextureListener.redPipeWireLit : TextureListener.redPipeWire);
                    break;
                case BLUE:
                    renderState.wireMatrix.setWireTextureIdentifier(color, lit ? TextureListener.bluePipeWireLit : TextureListener.bluePipeWire);
                    break;
                case GREEN:
                    renderState.wireMatrix.setWireTextureIdentifier(color, lit ? TextureListener.greenPipeWireLit : TextureListener.greenPipeWire);
                    break;
                case YELLOW:
                    renderState.wireMatrix.setWireTextureIdentifier(color, lit ? TextureListener.yellowPipeWireLit : TextureListener.yellowPipeWire);
                    break;
            }
        }

        pluggableState.setPluggables(sideProperties.pluggables);

        if(renderState.isDirty()){
            renderState.clean();
        }
        sendUpdateToClient();
    }

    public void sendUpdateToClient() {
        sendClientUpdate = true;
    }

    private void internalUpdate(){
        updateSignalState();
    }

    @Override
    public Packet createUpdatePacket() {
        return getBlockEntityUpdatePacket();
    }

    public BlockEntityUpdatePacket getBlockEntityUpdatePacket(){
        BlockEntityUpdatePacket packet = new BlockEntityUpdatePacket(x, y, z);
        packet.addStateForSerialization(renderState);
        packet.addStateForSerialization(pluggableState);
        return packet;
    }

    @Override
    public Serializable getStateInstance(byte stateId) {
        Class stateClass = StateRegistry.getClass(stateId);
        if(stateClass == PipeRenderState.class){
            return renderState;
        }
        if(stateClass == PipePluggableState.class){
            return pluggableState;
        }
        throw new RuntimeException("Unknown state requested: " + stateId + " this is a bug!");
    }

    public net.danygames2014.buildcraft.api.transport.gate.Gate getGate(Direction side){
        if(side == null){
            return null;
        }
        return gates[side.ordinal()];
    }

    @Override
    public void afterStateUpdated(byte stateId) {
        if(!world.isRemote){
            return;
        }
        Class stateClass = StateRegistry.getClass(stateId);
        if(stateClass == PipeRenderState.class){
            if(renderState.needsRenderUpdate()){
                world.setBlockDirty(x, y, z);
                renderState.clean();
            }
            return;
        }
        if(stateClass == PipePluggableState.class){
            PipePluggable[] newPluggables = pluggableState.getPluggables();

            // mark for render update if necessary
            for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
                PipePluggable old = sideProperties.pluggables[i];
                PipePluggable newer = newPluggables[i];
                if (old == null && newer == null) {
                    continue;
                } else if (old != null && newer != null && old.getClass() == newer.getClass()) {
                    if (newer.requiresRenderUpdate(old)) {
                        world.setBlockDirty(x, y, z);
                        break;
                    }
                } else {
                    // one of them is null but not the other, so update
                    world.setBlockDirty(x, y, z);
                    break;
                }
            }
            sideProperties.pluggables = newPluggables.clone();

            for (int i = 0; i < Direction.values().length; i++) {
                final PipePluggable pluggable = getPipePluggable(Direction.byId(i));
                if (pluggable instanceof GatePluggable) {
                    final GatePluggable gatePluggable = (GatePluggable) pluggable;
                    Gate gate = gates[i];
                    if (gate == null || gate.logic != gatePluggable.getLogic() || gate.material != gatePluggable.getMaterial()) {
                        gates[i] = GateFactory.makeGate(this, gatePluggable.getMaterial(), gatePluggable.getLogic(), Direction.byId(i));
                    }
                } else {
                    gates[i] = null;
                }
            }

            syncGateExpansions();
        }
    }

    private void syncGateExpansions() {
        resyncGateExpansions = false;
        for (int i = 0; i < Direction.values().length; i++) {
            Gate gate = gates[i];
            if (gate == null) {
                continue;
            }
            GatePluggable gatePluggable = (GatePluggable) sideProperties.pluggables[i];
            if (gatePluggable.getExpansions().length > 0) {
                for (GateExpansion expansion : gatePluggable.getExpansions()) {
                    if (expansion != null) {
                        if (!gate.expansions.containsKey(expansion)) {
                            gate.addGateExpansion(expansion);
                        }
                    } else {
                        resyncGateExpansions = true;
                    }
                }
            }
        }
    }

    // Dummy Inventory
    @Override
    public int size() {
        return 0;
    }

    @Override
    public ItemStack getStack(int slot) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getMaxCountPerStack() {
        return 0;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
    // End of Dummy Inventory
}
