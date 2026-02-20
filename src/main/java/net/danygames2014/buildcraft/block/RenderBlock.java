package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.util.DirectionUtil;
import net.danygames2014.buildcraft.util.TextureMatrix;
import net.danygames2014.buildcraft.util.TextureUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.material.Material;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class RenderBlock extends TemplateBlock {
    private int renderMask = 0;
    private int colorMultiplier = 0xFFFFFF;

    public boolean applyUVFix = false;

    private final TextureMatrix textureMatrix = new TextureMatrix();

    public RenderBlock(Identifier identifier) {
        super(identifier, Material.AIR);
        this.setTranslationKey(identifier.namespace.id("render_block"));
    }

    public int getColorMultiplier() {
        return colorMultiplier;
    }

    @Override
    public int getColorMultiplier(BlockView blockView, int x, int y, int z) {
        return colorMultiplier;
    }

    public void setTextureIdentifier(Identifier identifier){
        for(Direction direction : DirectionUtil.directionsWithInvalid){
            textureMatrix.setTextureIdentifier(direction, identifier);
        }
    }

    public void setTextureIdentifierForSide(Direction direction, Identifier identifier){
        textureMatrix.setTextureIdentifier(direction, identifier);
    }

    @Override
    public int getTexture(int side) {
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || Atlases.getTerrain() == null){
            return 0;
        }
        return TextureUtil.getTerrainTextureOffset(textureMatrix.getTextureIdentifier(DirectionUtil.getById(side)));
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
