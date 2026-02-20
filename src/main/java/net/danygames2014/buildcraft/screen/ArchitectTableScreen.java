package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.ArchitectTableBlockEntity;
import net.danygames2014.buildcraft.config.Config;
import net.danygames2014.buildcraft.packet.ArchitectTableNameFieldPacket;
import net.danygames2014.buildcraft.screen.handler.ArchitectTableScreenHandler;
import net.danygames2014.buildcraft.screen.widget.TransparentTextFieldWidget;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ArchitectTableScreen extends HandledScreen {
    public final ArchitectTableBlockEntity blockEntity;
    public final PlayerEntity player;
    
    public TextFieldWidget nameField;

    public ArchitectTableScreen(PlayerEntity player, ArchitectTableBlockEntity blockEntity) {
        super(new ArchitectTableScreenHandler(player, blockEntity));
        this.blockEntity = blockEntity;
        this.player = player;
        this.backgroundHeight = 165;
    }

    @Override
    public void init() {
        super.init();
        this.nameField = new TransparentTextFieldWidget(this, this.textRenderer, this.width / 2 - 54, this.height / 2 - 21, 88, 9, "");
        this.nameField.setMaxLength(16);
    }

    @Override
    public void tick() {
        super.tick();
        nameField.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        nameField.render();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        nameField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            super.keyPressed(character, keyCode);
            return;
        }
        
        if (keyCode == Keyboard.KEY_RETURN) {
            nameField.setFocused(false);
        }
        
        if (nameField.focused) {
            nameField.keyPressed(character, keyCode);
            blockEntity.blueprintName = nameField.getText();
            PacketHelper.send(new ArchitectTableNameFieldPacket(nameField.getText(), false));
            return;
        }
        
        super.keyPressed(character, keyCode);
    }

    @Override
    public void handleTab() {
        nameField.setFocused(true);
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
            int cookProgress = (int) (((float) blockEntity.progress / Config.MACHINE_CONFIG.architectTable.blueprintTime) * 22F);
            drawTexture(x + 73, y + 35, 177, 14, cookProgress, 16);
        }
    }
}
