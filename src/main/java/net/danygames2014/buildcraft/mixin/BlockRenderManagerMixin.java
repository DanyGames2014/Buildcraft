package net.danygames2014.buildcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.danygames2014.buildcraft.block.RenderBlock;
import net.minecraft.block.Block;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {
    @Unique
    int tesselatorIndexEast = 0;
    @Unique
    int tesselatorIndexSouth = 0;


    @WrapOperation(method = "renderSouthFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V"))
    public void fixUVSouth(Tessellator instance, double x, double y, double z, double u, double v, Operation<Void> original, Block block){
        if(!(block instanceof RenderBlock rb) || !rb.applyUVFix){
            original.call(instance, x, y, z, u, v);
            return;
        }
        RenderBlock renderBlock = (RenderBlock) block;
        if(tesselatorIndexSouth > 3){
            tesselatorIndexSouth = 0;
        }

        SpriteAtlasTexture atlas = StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE);

        float uSize = 1f / atlas.getWidth();
        float vSize = 1f / atlas.getHeight();

        Atlas.Sprite sprite = Atlases.getTerrain().getTexture(renderBlock.getTexture(0));

        double startU = sprite.getStartU() + ((1 - block.minZ) * 16) * uSize;
        double endU = sprite.getStartU() + ((1 - block.maxZ) * 16) * uSize;

        double startV = sprite.getStartV() + ((1 - block.minY) * 16) * vSize;
        double endV = sprite.getStartV() + ((1 - block.maxY) * 16) * vSize;

        switch(tesselatorIndexSouth){
            case 0:
                original.call(instance, x, y, z, endU, startV);
                break;
            case 1:
                original.call(instance, x, y, z, startU, startV);
                break;
            case 2:
                original.call(instance, x, y, z, startU, endV);
                break;
            case 3:
                original.call(instance, x, y, z, endU, endV);
                break;
        }

        tesselatorIndexSouth++;
    }

    @WrapOperation(method = "renderEastFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V"))
    public void fixUVWest(Tessellator instance, double x, double y, double z, double u, double v, Operation<Void> original, Block block){
        if(!(block instanceof RenderBlock rb) || !rb.applyUVFix){
            original.call(instance, x, y, z, u, v);
            return;
        }
        RenderBlock renderBlock = (RenderBlock) block;
        if(tesselatorIndexEast > 3){
            tesselatorIndexEast = 0;
        }

        SpriteAtlasTexture atlas = StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE);

        float uSize = 1f / atlas.getWidth();
        float vSize = 1f / atlas.getHeight();

        Atlas.Sprite sprite = Atlases.getTerrain().getTexture(renderBlock.getTexture(0));

        double startU = sprite.getStartU() + ((1 - block.maxX) * 16) * uSize;
        double endU   = sprite.getStartU() + ((1 - block.minX) * 16) * uSize;

        double startV = sprite.getStartV() + ((1 - block.maxY) * 16) * vSize;
        double endV   = sprite.getStartV() + ((1 - block.minY) * 16) * vSize;

        switch(tesselatorIndexEast){
            case 0:
                original.call(instance, x, y, z, endU, startV);
                break;
            case 1:
                original.call(instance, x, y, z, startU, startV);
                break;
            case 2:
                original.call(instance, x, y, z, startU, endV);
                break;
            case 3:
                original.call(instance, x, y, z, endU, endV);
                break;
        }

        tesselatorIndexEast++;
    }
}
