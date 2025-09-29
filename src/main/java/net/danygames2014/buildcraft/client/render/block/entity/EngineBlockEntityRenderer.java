package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.block.entity.BaseEngineBlockEntity;
import net.danygames2014.buildcraft.client.render.block.EngineRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

import javax.annotation.Nullable;

public class EngineBlockEntityRenderer extends BlockEntityRenderer {
    private final EngineRenderer engineRenderer;
    private final String baseTexturePath;
    @Nullable
    private final String chamberTexturePath;
    @Nullable
    private final String trunkTexturePath;


    public EngineBlockEntityRenderer(String baseTexturePath, @Nullable String chamberTexturePath, @Nullable String trunkTexturePath){
        this.baseTexturePath = baseTexturePath;
        this.chamberTexturePath = chamberTexturePath;
        this.trunkTexturePath = trunkTexturePath;
        this.engineRenderer = new EngineRenderer();
    }

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if(blockEntity instanceof BaseEngineBlockEntity baseEngineBlockEntity){
            engineRenderer.render(Minecraft.INSTANCE.textureManager, baseEngineBlockEntity.energyStage, baseEngineBlockEntity.progress, baseEngineBlockEntity.facing, baseTexturePath, chamberTexturePath, trunkTexturePath, x, y, z);
        }
    }
}
