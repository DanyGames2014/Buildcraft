package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.screen.handler.ChuteScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import org.lwjgl.opengl.GL11;

public class ChuteScreen extends HandledScreen {
    public ChuteScreen(PlayerInventory playerInventory, ChuteBlockEntity chuteBlockEntity) {
        super(new ChuteScreenHandler(playerInventory, chuteBlockEntity));
    }

    @Override
    protected void drawBackground(float tickDelta) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/chute_screen.png"));
        int j = (width - backgroundWidth) / 2;
        int k = (height - backgroundHeight) / 2;
        this.drawTexture(j, k, 0, 0, backgroundWidth, backgroundHeight);
    }
}
