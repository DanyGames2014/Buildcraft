package net.danygames2014.buildcraft.block.entity.pipe.statement;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.danygames2014.buildcraft.api.transport.statement.Statement;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.StatementMouseClick;
import net.danygames2014.buildcraft.api.transport.statement.StatementParameter;
import net.danygames2014.buildcraft.registry.ControlModeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

// TODO: finish implementing
public class StatementParameterControlMode implements StatementParameter {

    private ControlMode controlMode;

    @Override
    public Identifier getIdentifier() {
        return Buildcraft.NAMESPACE.id("control_mode");
    }

    @Override
    public Atlas.Sprite getSprite() {
        return controlMode.sprite;
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public void registerTextures() {

    }

    @Override
    public String getDescription() {
//        return controlMode.
        return "";
    }

    @Override
    public void click(StatementContainer source, Statement stmt, ItemStack stack, StatementMouseClick mouse) {

    }

    @Override
    public void readNBT(NbtCompound nbt) {
        if(nbt.contains("mode")){
            controlMode = ControlModeRegistry.get(Identifier.tryParse(nbt.getString("mode")));
        }
    }

    @Override
    public void writeNBT(NbtCompound nbt) {
        if(controlMode != null){
            nbt.putString("mode", controlMode.identifier.toString());
        }
    }

    @Override
    public StatementParameter rotateLeft() {
        return this;
    }
}
