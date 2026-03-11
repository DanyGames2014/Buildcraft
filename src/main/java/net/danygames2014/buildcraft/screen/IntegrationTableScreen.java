package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.IntegrationTableBlockEntity;
import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.buildcraft.screen.handler.IntegrationTableScreenHandler;
import net.danygames2014.buildcraft.util.ScreenUtil;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class IntegrationTableScreen extends BuildcraftScreen {
    IntegrationTableBlockEntity blockEntity;
    ItemRenderer itemRenderer;
    PlayerEntity player;

    ItemStack previewStack;

    public IntegrationTableScreen(PlayerEntity player, IntegrationTableBlockEntity blockEntity) {
        super(new IntegrationTableScreenHandler(player, blockEntity), blockEntity);
        this.blockEntity = blockEntity;
        this.backgroundHeight = 165;
        this.backgroundWidth = 175;
        this.itemRenderer = new ItemRenderer();

        this.previewStack = new ItemStack(Block.STONE);

        ledgerManager.add(new IntegrationTableLedger(blockEntity));
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Integration Table", (this.backgroundWidth / 2) - (87 / 2), 8, 4210752);
        textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);

        if (blockEntity.previewStack != null) {
            itemRenderer.renderGuiItem(this.textRenderer, this.minecraft.textureManager, blockEntity.previewStack, 116, 44);
        }
    }

    float colorSwitch = 0.0F;
    boolean otherColor = false;

    @Override
    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/integration_table.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        colorSwitch += tickDelta;

        int progress = blockEntity.scaledProgress;
        if (progress > 0) {
            if (colorSwitch > 10.0F) {
                colorSwitch = 0.0F;
                otherColor = !otherColor;
            }

            drawTexture(x + 13, y + 40, 0, otherColor ? 190 : 214, progress, 24);
        }
    }

    protected class IntegrationTableLedger extends Ledger {
        IntegrationTableBlockEntity table;
        int headerColor = 0xE1C92F;
        int subheaderColor = 0xAAAFB8;
        int textColor = 0x000000;

        public IntegrationTableLedger(IntegrationTableBlockEntity table) {
            this.table = table;
            maxHeight = 94;
            overlayColor = 0xD46C1F;
        }

        @Override
        public void draw(int x, int y) {
            drawBackground(x, y);
            ScreenUtil.drawSprite(TextureListener.energySprite, x + 3, y + 4, 16, 16, zOffset);

            if (!isFullyOpened()) {
                return;
            }

            TranslationStorage translationStorage = TranslationStorage.getInstance();

            textRenderer.drawWithShadow(translationStorage.get("gui.energy"), x + 22, y + 8, headerColor);
            textRenderer.drawWithShadow(translationStorage.get("gui.assemblyCurrentRequired") + ":", x + 22, y + 20, subheaderColor);
            textRenderer.draw(String.format("%d MJ", table.currentRecipe != null ? table.currentRecipe.recipeTime : 0), x + 22, y + 32, textColor);
            textRenderer.drawWithShadow(translationStorage.get("gui.stored") + ":", x + 22, y + 44, subheaderColor);
            textRenderer.draw(String.format("%.1f MJ", table.progress), x + 22, y + 56, textColor);
            textRenderer.drawWithShadow(translationStorage.get("gui.assemblyRate") + ":", x + 22, y + 68, subheaderColor);
            textRenderer.draw(String.format("%.1f MJ/t", table.progress - table.lastProgress), x + 22, y + 80, textColor);
        }

        @Override
        public String getTooltip() {
            return table.progress + " MJ";
        }
    }
}
