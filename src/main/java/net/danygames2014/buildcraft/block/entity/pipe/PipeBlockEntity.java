package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PipeBlockEntity extends BlockEntity {
    public PipeBlock pipeBlock;
    public PipeBehavior behavior;
    public PipeTransporter transporter;
    public final PipeRenderState renderState = new PipeRenderState();

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

        // TODO: This does more, but I only added the render related code for now
        if (neighbourUpdate) {
            refreshRenderState = true;
            neighbourUpdate = false;
        }

        if (refreshRenderState) {
            refreshRenderState();
            refreshRenderState = false;
        }

        transporter.tick();
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
    public boolean canConnectTo(int x, int y, int z, Direction side) {
        BlockEntity other = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());

        if (other == null) {
            return false;
        }

        if (other instanceof PipeBlockEntity pipe) {
            return behavior.canConnectTo(this, pipe, pipe.behavior);
        }

        //noinspection RedundantIfStatement
        if (transporter.canConnectTo(other, side)) {
            return true;
        }

        return false;
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("pipeId", String.valueOf(BlockRegistry.INSTANCE.getId(pipeBlock)));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.pipeBlock = (PipeBlock) BlockRegistry.INSTANCE.get(Identifier.of(nbt.getString("pipeId")));
        init();
    }

    @Override
    public String toString() {
        return "PipeBlockEntity{" +
                "pipeBlock=" + pipeBlock +
                ", behavior=" + behavior +
                ", transporter=" + transporter +
                ", hasInit=" + hasInit +
                ", world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    protected void refreshRenderState() {
        // Pipe connections:
        for (Direction direction : Direction.values()) {
            renderState.pipeConnectionMatrix.setConnected(direction, this.canConnectTo(x, y, z, direction));
        }

        for (int i = 0; i < 7; i++) {
            Direction direction = Direction.byId(i);
            renderState.textureMatrix.setTextureIndex(direction, pipeBlock.getTexture(i));
        }

        if (renderState.isDirty()) {
            renderState.clean();
        }
    }
}
