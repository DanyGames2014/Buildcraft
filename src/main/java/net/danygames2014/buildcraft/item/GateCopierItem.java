package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.block.entity.pipe.pluggable.GatePluggable;
import net.danygames2014.buildcraft.block.entity.pipe.pluggable.PipePluggable;
import net.danygames2014.buildcraft.util.raycast.PipeRaycastResult;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class GateCopierItem extends TemplateItem {
    public GateCopierItem(Identifier identifier) {
        super(identifier);
        setMaxCount(1);
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {
        if (world.isRemote) {
            return true;
        }
        boolean isCopying = !user.isSneaking();
        BlockEntity blockEntity = world.getBlockEntity(x, y, z);
        PipePluggable pluggable = null;
        Gate gate = null;

        if(!(blockEntity instanceof PipeBlockEntity)){
            isCopying = true;
        } else {
            BlockState blockState = world.getBlockState(x, y, z);
            if (blockEntity instanceof PipeBlockEntity pipe && blockState.getBlock() instanceof PipeBlock pipeBlock) {
                PipeRaycastResult raycastResult = pipeBlock.raycastPipe(world, x, y, z, user);
                if (raycastResult != null && raycastResult.box != null && raycastResult.hitPart == PipeRaycastResult.Part.Pluggable) {
                    pluggable = pipe.getPipePluggable((raycastResult.sideHit));
                }
            }
        }

        if(pluggable instanceof GatePluggable gatePluggable){
            gate = gatePluggable.realGate;
        }

        if(isCopying) {
            if(gate == null) {
                stack.getStationNbt().values().clear();
                user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.clear"));
                return true;
            }

            stack.getStationNbt().values().clear();
            gate.writeStatementsToNBT(stack.getStationNbt());
            stack.getStationNbt().putByte("material", (byte) gate.material.ordinal());
            stack.getStationNbt().putByte("logic", (byte) gate.material.ordinal());
            user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.gate_copied"));
        } else {
            if(!stack.getStationNbt().contains("logic")){
                user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.no_information"));
                return true;
            } else if (gate == null){
                user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.no_gate"));
                return true;
            }
            GateMaterial dataMaterial = GateMaterial.fromOrdinal(stack.getStationNbt().getByte("material"));
            GateMaterial gateMaterial = gate.material;

            if (gateMaterial.numSlots < dataMaterial.numSlots) {
                user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.warning.slots"));
            }
            if (gateMaterial.numActionParameters < dataMaterial.numActionParameters) {
                user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.warning.action_parameters"));
            }
            if (gateMaterial.numTriggerParameters < dataMaterial.numTriggerParameters) {
                user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.warning.trigger_parameters"));
            }
            if (stack.getStationNbt().getByte("logic") != gate.logic.ordinal()) {
                user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.warning.logic"));
            }

            gate.readStatementsFromNBT(stack.getStationNbt());
            if(!gate.verifyGateStatements()){
                user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.warning.load"));
            }

            if(blockEntity instanceof PipeBlockEntity pipe){
                pipe.sendUpdateToClient();
            }
            user.sendMessage(TranslationStorage.getInstance().get("chat.buildcraft.gate_copier.gate_pasted"));
            return true;
        }

        return true;
    }
}
