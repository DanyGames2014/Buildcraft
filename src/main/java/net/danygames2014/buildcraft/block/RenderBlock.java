package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.util.TextureMatrix;
import net.minecraft.block.material.Material;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;

public class RenderBlock extends TemplateBlock {
    private int renderMask = 0;
    private int colorMultiplier = 0xFFFFFF;

    private int textureOffset = 0;

    public RenderBlock(Identifier identifier) {
        super(identifier, Material.AIR);
    }

    public int getColorMultiplier() {
        return colorMultiplier;
    }

    public void setTextureOffset(int textureOffset){
        this.textureOffset = textureOffset;
    }

    @Override
    public int getTexture(int side) {
        return textureOffset;
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

    public void setRenderAllSides(){
        renderMask = 0x3F;
    }
}
