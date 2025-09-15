package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.CombustionEngineBlockEntity;
import net.danygames2014.buildcraft.block.entity.StirlingEngineBlockEntity;
import net.danygames2014.buildcraft.screen.handler.CombustionEngineScreenHandler;
import net.danygames2014.buildcraft.screen.handler.StirlingEngineScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class CombustionEngineScreen extends HandledScreen {
    CombustionEngineBlockEntity engine;
    
    public CombustionEngineScreen(PlayerEntity player, CombustionEngineBlockEntity engine) {
        super(new CombustionEngineScreenHandler(player, engine));
        this.engine = engine;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Stirling Engine", 54, 6, 4210752);
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
    }
}
