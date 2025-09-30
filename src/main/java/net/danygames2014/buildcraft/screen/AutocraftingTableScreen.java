package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.AutocraftingTableBlockEntity;
import net.danygames2014.buildcraft.screen.handler.AutocraftingTableScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class AutocraftingTableScreen extends HandledScreen {
    public AutocraftingTableBlockEntity blockEntity;
    
    public AutocraftingTableScreen(PlayerEntity player, AutocraftingTableBlockEntity blockEntity) {
        super(new AutocraftingTableScreenHandler(player, blockEntity));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Autocrafting Table", 60, 6, 4210752);
        textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);
    }

    @Override
    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/gui/crafting.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);
    }
}
