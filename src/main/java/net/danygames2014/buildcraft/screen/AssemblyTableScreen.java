package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.AssemblyTableBlockEntity;
import net.danygames2014.buildcraft.packet.SelectAssemblyRecipeC2SPacket;
import net.danygames2014.buildcraft.screen.handler.AssemblyTableScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.opengl.GL11;

public class AssemblyTableScreen extends HandledScreen {
    AssemblyTableBlockEntity blockEntity;
    ItemRenderer itemRenderer;
    PlayerEntity player;

    // Only used when on server
    public int[] resultIds;
    public byte[] resultAmounts;
    public boolean[] selected;
    public boolean[] active;

    public AssemblyTableScreen(PlayerEntity player, AssemblyTableBlockEntity blockEntity) {
        super(new AssemblyTableScreenHandler(player, blockEntity));
        this.blockEntity = blockEntity;
        this.backgroundHeight = 206;
        this.backgroundWidth = 175;
        this.itemRenderer = new ItemRenderer();
    }

    public int getRecipeIndex(int mouseX, int mouseY) {
        int clickX = mouseX - (width - backgroundWidth) / 2;
        int clickY = mouseY - (height - backgroundHeight) / 2;
        
        int x = MathHelper.floor((clickX - 134) / 18.0F);
        int y = MathHelper.floor((clickY - 36) / 18.0F);
        return y + x * 4;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        
        if (blockEntity.world == null || blockEntity.world.isRemote) {
            PacketHelper.send(new SelectAssemblyRecipeC2SPacket((byte) getRecipeIndex(mouseX, mouseY)));            
        } else {
            blockEntity.selectRecipe(getRecipeIndex(mouseX, mouseY));
        }
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Assembly Table", 60, 15, 4210752);
        textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);

        for (int column = 0; column < 2; column++) {
            for (int row = 0; row < 4; row++) {
                int recipeIndex = row + column * 4;
                
                if (blockEntity.world != null && !blockEntity.world.isRemote) {
                    if (recipeIndex < blockEntity.recipes.size()) {
                        AssemblyTableBlockEntity.RecipeEntry recipe = blockEntity.recipes.get(recipeIndex);
                        int x = 134 + column * 18;
                        int y = 36 + row * 18;

                        if (recipe.selected && !recipe.active) {
                            int textureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/assembly_table.png");
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            minecraft.textureManager.bindTexture(textureId);
                            drawTexture(x, y, 177, 1, 16, 16);
                        }

                        if (recipe.active) {
                            int textureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/assembly_table.png");
                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            minecraft.textureManager.bindTexture(textureId);
                            drawTexture(x, y, 196, 1, 16, 16);
                        }

                        itemRenderer.renderGuiItem(this.textRenderer, this.minecraft.textureManager, recipe.icon, x, y);
                    }
                } else {
                    if (resultIds != null) {
                        if (recipeIndex < resultIds.length) {
                            int x = 134 + column * 18;
                            int y = 36 + row * 18;

                            if (selected[recipeIndex] && !active[recipeIndex]) {
                                int textureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/assembly_table.png");
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                minecraft.textureManager.bindTexture(textureId);
                                drawTexture(x, y, 177, 1, 16, 16);
                            }

                            if (active[recipeIndex]) {
                                int textureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/assembly_table.png");
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                minecraft.textureManager.bindTexture(textureId);
                                drawTexture(x, y, 196, 1, 16, 16);
                            }

                            itemRenderer.renderGuiItem(this.textRenderer, this.minecraft.textureManager, new ItemStack(resultIds[recipeIndex], resultAmounts[recipeIndex], 0), x, y);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/assembly_table.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);
        
        int progress = blockEntity.getProgressScaled(70);
        if (progress > 0) {
            drawTexture(x + 95, y + 36 + 70 - progress, 176, 18, 4, progress);
        }
    }
}
