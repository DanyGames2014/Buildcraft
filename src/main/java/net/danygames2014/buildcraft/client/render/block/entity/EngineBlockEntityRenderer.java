package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.block.entity.BaseEngineBlockEntity;
import net.danygames2014.buildcraft.client.render.block.EngineRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.lwjgl.opengl.GL11;

public class EngineBlockEntityRenderer extends BlockEntityRenderer {
    private final EngineRenderer engineRenderer;
    private final String baseTexturePath;


    public EngineBlockEntityRenderer(String baseTexturePath){
        this.baseTexturePath = baseTexturePath;
        this.engineRenderer = new EngineRenderer();
    }

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if(blockEntity instanceof BaseEngineBlockEntity baseEngineBlockEntity){
            engineRenderer.render(Minecraft.INSTANCE.textureManager, baseEngineBlockEntity.energyStage, baseEngineBlockEntity.progress, baseEngineBlockEntity.facing, baseTexturePath, x, y, z);
        }
    }
}
