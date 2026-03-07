package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.BlueprintLibraryBlockEntity;
import net.danygames2014.buildcraft.item.BlueprintManager;
import net.danygames2014.buildcraft.screen.handler.BlueprintLibraryScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class BlueprintLibraryScreen extends HandledScreen {
    public final BlueprintLibraryBlockEntity blockEntity;
    public final PlayerEntity player;
    
    public ButtonWidget previousButton;
    public ButtonWidget nextButton;
    
    public ButtonWidget deleteButton;
    public ButtonWidget lockButton;
    
    public BlueprintLibraryScreen(PlayerEntity player, BlueprintLibraryBlockEntity blockEntity) {
        super(new BlueprintLibraryScreenHandler(player, blockEntity));
        this.blockEntity = blockEntity;
        this.player = player;
        this.backgroundHeight = 221;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        super.init();

        int j = (width - this.backgroundWidth) / 2;
        int k = (height - this.backgroundHeight) / 2;
        
        this.nextButton = new ButtonWidget(1000, j + 100, k + 23, 20, 20, "<");
        this.buttons.add(this.nextButton);
        this.previousButton = new ButtonWidget(1001, j + 122, k + 23, 20, 20, ">");
        this.buttons.add(this.previousButton);
        this.deleteButton = new ButtonWidget(1002, j + 100, k + 114, 25, 20, "Del");
        this.buttons.add(this.deleteButton);
        this.lockButton = new ButtonWidget(1003, j + 127, k + 114, 40, 20, "Lock");
        this.buttons.add(this.lockButton);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Blueprint Library", 8, 10, 4210752);
        if (handler instanceof BlueprintLibraryScreenHandler library) {
            
            int c = 0;
            for (String name : BlueprintManager.blueprints) {
                if (name == null) {
                    break;
                }

                if (name.length() > 16) {
                    name = name.substring(0, 16);
                }

                if (name.equals(library.selectedBlueprintName)) {
                    int l1 = 8;
                    int i2 = 24;
                    fillGradient(l1, i2 + 9 * c, l1 + 88, i2 + 9 * (c + 1), 0x80ffffff, 0x80ffffff);
                }

                textRenderer.draw(name, 9, 25 + 9 * c, 0x404040);
                c++;
            }
        }
    }

    @Override
    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);

        if (handler instanceof BlueprintLibraryScreenHandler library) {
            int xMin = (width - backgroundWidth) / 2;
            int yMin = (height - backgroundHeight) / 2;

            int x = i - xMin;
            int y = j - yMin;

            if (x >= 8 && x <= 88) {
                int ySlot = (y - 24) / 9;

                if (ySlot >= 0 && ySlot <= 11) {
                    if (ySlot < BlueprintManager.blueprints.size()) {
                        library.selectedBlueprintName = BlueprintManager.blueprints.get(ySlot);
                    }
                }
            }

        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);
        
        if (button.id == 1002) {
            if (handler instanceof BlueprintLibraryScreenHandler library) {
                BlueprintManager.delete(library.selectedBlueprintName);
            }
        }
    }

    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/blueprint_library_rw.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);
    }
}
