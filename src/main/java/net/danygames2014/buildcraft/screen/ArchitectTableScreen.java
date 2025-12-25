package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.ArchitectTableBlockEntity;
import net.danygames2014.buildcraft.screen.handler.ArchitectTableScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class ArchitectTableScreen extends HandledScreen {
    public ArchitectTableBlockEntity blockEntity;

    public ArchitectTableScreen(PlayerEntity player, ArchitectTableBlockEntity blockEntity) {
        super(new ArchitectTableScreenHandler(player, blockEntity));
        this.blockEntity = blockEntity;
        this.backgroundHeight = 165;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Architect Table", 8, 6, 4210752);
        textRenderer.draw("Inventory", 8, this.backgroundHeight - 92, 4210752);
    }

    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/architect_table.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        if (blockEntity.progress > 0) {
            int cookProgress = (int) (((float) blockEntity.progress / blockEntity.maxProgress) * 22F);
            drawTexture(x + 80, y + 35, 177, 14, cookProgress, 16);
        }
    }
}
