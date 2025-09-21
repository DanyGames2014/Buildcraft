package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.util.DirectionUtil;
import net.danygames2014.buildcraft.util.TextureMatrix;
import net.minecraft.block.material.Material;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;

public class RenderBlock extends TemplateBlock {
    private int renderMask = 0;
    private int colorMultiplier = 0xFFFFFF;

    public boolean applyUVFix = false;

    private TextureMatrix textureMatrix = new TextureMatrix();

    public RenderBlock(Identifier identifier) {
        super(identifier, Material.AIR);
    }

    public int getColorMultiplier() {
        return colorMultiplier;
    }

    public void setTextureOffset(int textureOffset){
        for(Direction direction : DirectionUtil.directionsWithInvalid){
            textureMatrix.setTextureIndex(direction, textureOffset);
        }
    }

    public void setTextureOffsetForSide(Direction direction, int textureOffset){
        textureMatrix.setTextureIndex(direction, textureOffset);
    }

    @Override
    public int getTexture(int side) {
        return textureMatrix.getTextureIndex(DirectionUtil.getById(side));
    }

    @Override
    public int getColor(int meta) {
        return this.getColorMultiplier();
    }

    public void setColor(int color){
        this.colorMultiplier = color;
    }

    @Override
    public boolean isSideVisible(BlockView blockView, int x, int y, int z, int side) {
        return (renderMask & (1 << side)) != 0;
    }

    public void setRenderMask(int renderMask) {
        this.renderMask = renderMask;
    }

    public void setRenderSide(Direction side, boolean render){
        if (render) {
            renderMask |= 1 << side.ordinal();
        } else {
            renderMask &= ~(1 << side.ordinal());
        }
    }

    public void setRenderAllSides(){
        renderMask = 0x3F;
    }
}
