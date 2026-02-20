package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.CombustionEngineBlockEntity;
import net.danygames2014.buildcraft.screen.handler.CombustionEngineScreenHandler;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

public class CombustionEngineScreen extends EngineScreen {
    CombustionEngineBlockEntity engine;
    
    public CombustionEngineScreen(PlayerEntity player, CombustionEngineBlockEntity engine) {
        super(new CombustionEngineScreenHandler(player, engine), engine);
        this.engine = engine;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Combustion Engine", 54, 6, 4210752);
        textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/combustion_engine.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);
        drawFluid(engine.getFluid(1, null), engine.getScaledBurnTime(58), x + 104, y + 19, 16, 58);
        drawFluid(engine.getFluid(0, null), engine.getScaledCoolant(58), x + 122, y + 19, 16, 58);

    }

    @SuppressWarnings("SameParameterValue")
    private void drawFluid(FluidStack fluidStack, int level, int x, int y, int width, int height){
        if(fluidStack == null || fluidStack.fluid == null) {
            return;
        }
        int textureId = fluidStack.fluid.getStillBlock().getTexture(0);
        Atlas.Sprite sprite = Atlases.getTerrain().getTexture(textureId);
        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();

        int color = 0xFFFFFF; // TODO: change this to the colormultiplier

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 1.0F);

        int fullX = width / 16;
        int fullY = height / 16;
        int lastX = width - fullX * 16;
        int lastY = height - fullY * 16;
        int fullLvl = (height - level) / 16;
        int lastLvl = (height - level) - fullLvl * 16;
        for(int i = 0; i < fullX; i++) {
            for(int j = 0; j < fullY; j++) {
                if(j >= fullLvl) {
                    drawCutSprite(sprite, x + i * 16, y + j * 16, 16, 16, j == fullLvl ? lastLvl : 0);
                }
            }
        }
        for(int i = 0; i < fullX; i++) {
            drawCutSprite(sprite, x + i * 16, y + fullY * 16, 16, lastY, fullLvl == fullY ? lastLvl : 0);
        }
        for(int i = 0; i < fullY; i++) {
            if(i >= fullLvl) {
                drawCutSprite(sprite, x + fullX * 16, y + i * 16, lastX, 16, i == fullLvl ? lastLvl : 0);
            }
        }
        drawCutSprite(sprite, x + fullX * 16, y + fullY * 16, lastX, lastY, fullLvl == fullY ? lastLvl : 0);

        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/combustion_engine.png"));
        drawTexture(x, y, 176, 0, 16, 60);
    }

    private void drawCutSprite(Atlas.Sprite sprite, int x, int y, int width, int height, int cut){
        Tessellator tess = Tessellator.INSTANCE;
        tess.startQuads();
        tess.vertex(x, y + height, zOffset, sprite.getStartU(), this.getInterpolatedV(sprite, height));
        tess.vertex(x + width, y + height, zOffset, this.getInterpolatedU(sprite, width), this.getInterpolatedV(sprite, height));
        tess.vertex(x + width, y + cut, zOffset, this.getInterpolatedU(sprite, width), this.getInterpolatedV(sprite, cut));
        tess.vertex(x, y + cut, zOffset, sprite.getStartU(), this.getInterpolatedV(sprite, cut));
        tess.draw();
    }

    private double getInterpolatedU(Atlas.Sprite sprite, double delta){
        double var3 = sprite.getEndU() - sprite.getStartU();
        return sprite.getStartU() + var3 * (float)delta / 16.0F;
    }

    private double getInterpolatedV(Atlas.Sprite sprite, double delta){
        double var3 = sprite.getEndV() - sprite.getStartV();
        return sprite.getStartV() + var3 * ((float)delta / 16.0F);
    }
}
