package net.danygames2014.buildcraft.block.entity.pipe;
import net.danygames2014.buildcraft.client.render.PipePluggableDynamicRenderer;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public abstract class PipePluggable {
    public abstract ItemStack[] getDropItems(PipeBlockEntity pipe);

    public void update(PipeBlockEntity pipe, Direction direction){

    }

    public void onAttachedToPipe(PipeBlockEntity pipe, Direction direction){
        validate(pipe, direction);
    }

    public void onDetachedFromPipe(PipeBlockEntity pipe, Direction direction){
        invalidate();
    }

    public void isBlocking(PipeBlockEntity pipe, Direction direction){

    }

    public void invalidate(){

    }

    public void validate(PipeBlockEntity pipe, Direction direction){

    }

    public boolean isSolidOnSide(){
        return false;
    }

    public abstract Box getBoundingBox(Direction side);

    @Environment(EnvType.CLIENT)
    public abstract PipePluggableRenderer getRenderer();

    @Environment(EnvType.CLIENT)
    public PipePluggableDynamicRenderer getDynamicRenderer(){
        return null;
    }

    public boolean requiresRenderUpdate(PipePluggable old){
        return true;
    }

    public abstract void readNbt(NbtCompound nbt);
    public abstract void writeNbt(NbtCompound nbt);
}
