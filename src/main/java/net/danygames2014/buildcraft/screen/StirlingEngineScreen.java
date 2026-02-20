package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.StirlingEngineBlockEntity;
import net.danygames2014.buildcraft.screen.handler.StirlingEngineScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class StirlingEngineScreen extends EngineScreen {
    final StirlingEngineBlockEntity engine;
    
    public StirlingEngineScreen(PlayerEntity player, StirlingEngineBlockEntity engine) {
        super(new StirlingEngineScreenHandler(player, engine), engine);
        this.engine = engine;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Stirling Engine", 54, 6, 4210752);
        textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/stirling_engine.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        if (engine.getScaledBurnTime(12) > 0) {
            int scaledBurnTime = engine.getScaledBurnTime(12);
            drawTexture(x + 80, (y + 24 + 12) - scaledBurnTime, 176, 12 - scaledBurnTime, 14, scaledBurnTime + 2);
        }
    }
}
