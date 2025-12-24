package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.pipe.DiamondPipeBlockEntity;
import net.danygames2014.buildcraft.packet.ToggleDiamondPipeFilterC2SPacket;
import net.danygames2014.buildcraft.screen.handler.DiamondPipeScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import org.lwjgl.opengl.GL11;

public class DiamondPipeScreen extends HandledScreen {
    public DiamondPipeBlockEntity pipe;
    
    public DiamondPipeScreen(PlayerEntity player, DiamondPipeBlockEntity blockEntity) {
        super(new DiamondPipeScreenHandler(player, blockEntity));
        this.pipe = blockEntity;
        this.backgroundHeight = 222;
    }

    @Override
    protected void drawForeground() {
        textRenderer.draw(pipe.getName(), 8, 6, 4210752);


        textRenderer.draw("Meta:", 107, 6, 4210752);
        textRenderer.draw(pipe.filterMeta ? "Filter" : "Ignore", 138, 6, 0x000000);

        textRenderer.draw("Tags:", 40, 6, 4210752);
        textRenderer.draw(pipe.filterTags ? "Filter" : "Ignore", 72, 6, 0x000000);
        
        
        textRenderer.draw("Inventory", 8, this.backgroundHeight - 96 + 2, 4210752);
    }

    protected void drawBackground(float tickDelta) {
        int bgTextureId = minecraft.textureManager.getTextureId("/assets/buildcraft/stationapi/textures/gui/filter.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.textureManager.bindTexture(bgTextureId);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(x, y, 0, 0, backgroundWidth, backgroundHeight);
        
        if (pipe.filterMeta) {
            drawTexture(x + 133, y + 4, 176, 0, 36, 12);
        } else {
            drawTexture(x + 133, y + 4, 176, 12, 36, 12);
        }

        if (pipe.filterTags) {
            drawTexture(x + 67, y + 4, 176, 0, 36, 12);
        } else {
            drawTexture(x + 67, y + 4, 176, 12, 36, 12);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        int var4 = (this.width - this.backgroundWidth) / 2;
        int var5 = (this.height - this.backgroundHeight) / 2;
        mouseX -= var4;
        mouseY -= var5;

        byte filterIndex = -1;
        boolean filterValue = false;
        
        if (mouseX >= 133 && mouseX <= 167 && mouseY >= 4 && mouseY <= 15) {
            filterIndex = 0;
            filterValue = !pipe.filterMeta;
        }

        if (mouseX >= 67 && mouseX <= 101 && mouseY >= 4 && mouseY <= 15) {
            filterIndex = 1;
            filterValue = !pipe.filterTags;
        }
        
        if (filterIndex != -1) {
            if (pipe.world == null || pipe.world.isRemote) {
                PacketHelper.send(new ToggleDiamondPipeFilterC2SPacket(filterIndex, filterValue));
            } else {
                switch (filterIndex) {
                    case 0 -> pipe.filterMeta = filterValue;
                    case 1 -> pipe.filterTags = filterValue;
                }
            }
        }
    }
}
