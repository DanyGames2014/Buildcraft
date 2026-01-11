package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.BuilderBlockEntity;
import net.danygames2014.buildcraft.packet.BuilderCommandPacket;
import net.danygames2014.buildcraft.screen.handler.BuilderScreenHandler;
import net.danygames2014.buildcraft.screen.widget.CustomButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class BuilderScreen extends HandledScreen {
    public BuilderBlockEntity blockEntity;
    public PlayerEntity player;
    
    public CustomButtonWidget startButton;
    public CustomButtonWidget pauseButton;
    public CustomButtonWidget stopButton;

    public BuilderScreen(PlayerEntity player, BuilderBlockEntity blockEntity) {
        super(new BuilderScreenHandler(player, blockEntity));
        this.blockEntity = blockEntity;
        this.player = player;
        this.backgroundHeight = 222;
        this.backgroundWidth = 176;
    }

    @Override
    public void init() {
        super.init();
        
        int startX = (width / 2) - (backgroundWidth / 2);
        int startY = (height / 2) - (backgroundHeight / 2);
        
        this.startButton = new CustomButtonWidget(100, startX + 62, startY + 47, 16, 16, "/assets/buildcraft/stationapi/textures/gui/builder.png", 0, 224);
        this.buttons.add(startButton);
        this.pauseButton = new CustomButtonWidget(101, startX + 80, startY + 47, 16, 16,"/assets/buildcraft/stationapi/textures/gui/builder.png",  16, 224);
        this.buttons.add(pauseButton);
        this.stopButton = new CustomButtonWidget(102, startX + 98, startY + 47, 16, 16,"/assets/buildcraft/stationapi/textures/gui/builder.png", 0, 240);
        this.buttons.add(stopButton);
    }

    @Override
    public void tick() {
        super.tick();
        
        switch (blockEntity.state) {
            case IDLE -> {
                startButton.active = false;
                pauseButton.active = false;
                stopButton.active = false;
            }
            case READY -> {
                startButton.active = true;
                pauseButton.active = false;
                stopButton.active = true;
            }
            case BUILDING -> {
                startButton.active = false;
                pauseButton.active = true;
                stopButton.active = true;
            }
            case STOPPED -> {
                startButton.active = false;
                pauseButton.active = false;
                stopButton.active = false;
            }
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 100 -> {
                if (player.world.isRemote) {
                    PacketHelper.send(new BuilderCommandPacket(0));
                } else {
                    blockEntity.startConstruction();
                }
            }
            
            case 101 -> {
                if (player.world.isRemote) {
                    PacketHelper.send(new BuilderCommandPacket(1));
                } else {
                    blockEntity.pauseConstruction();
                }
            }
            
            case 102 -> {
                if (player.world.isRemote) {
                    PacketHelper.send(new BuilderCommandPacket(2));
                } else {
                    blockEntity.stopConstruction();
                }
            }
        }
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw("Builder", 8, 6, 4210752);
        textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);
        
        if (blockEntity.hasBlueprint()) {
            textRenderer.draw("Remaining", -68, 6, 4210752);
        }
        
        if (blockEntity.remainingEntries != null) {
            textRenderer.draw("Remaining: ", 7, 26, 4210752);
            textRenderer.draw(blockEntity.remainingBlocks + " blocks", 7, 36, 4210752);
        }
    }

    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/builder.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);

        if (blockEntity.hasBlueprint()) {
            drawTexture(x - 76, y, 176, 0, 80, 151);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        
        int x = ((width - backgroundWidth) / 2) - 69;
        int y = (height - backgroundHeight) / 2 + 18;
        
        int column = 0;

        GL11.glPushMatrix();
        GL11.glRotatef(120.0F, 1.0F, 0.0F, 0.0F);
        Lighting.turnOn();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        for (var neededBlock : blockEntity.neededBlockEntries.values()) {
            ItemStack stack = new ItemStack(neededBlock.block, neededBlock.count, neededBlock.meta);
            itemRenderer.renderGuiItem(textRenderer, Minecraft.INSTANCE.textureManager, stack, x + (column * 18), y);
            itemRenderer.renderGuiItemDecoration(textRenderer, Minecraft.INSTANCE.textureManager, stack, x + (column * 18), y);
            
            if (column++ >= 3) {
                column = 0;
                y += 18;
            }
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        Lighting.turnOff();
        GL11.glPopMatrix();
    }
}
