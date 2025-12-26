package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.screen.slot.AdvancedSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.platform.Lighting;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;

public class AdvancedInterfaceScreen extends BuildcraftScreen{
    private static final ItemRenderer itemRenderer = new ItemRenderer();
    public ArrayList<AdvancedSlot> slots = new ArrayList<>();

    public AdvancedInterfaceScreen(ScreenHandler container, Inventory inventory) {
        super(container, inventory);
    }

    public int getSlotIndexAtLocation(int i, int j) {

        int x = i - ((width - backgroundWidth) / 2);
        int y = j - ((height - backgroundHeight) / 2);

        for (int position = 0; position < slots.size(); ++position) {
            AdvancedSlot s = slots.get(position);

            if (s != null && x >= s.x && x <= s.x + 16 && y >= s.y && y <= s.y + 16) {
                return position;
            }
        }
        return -1;
    }

    public AdvancedSlot getSlotAtLocation(int i, int j) {
        int id = getSlotIndexAtLocation(i, j);

        if (id != -1) {
            return slots.get(id);
        } else {
            return null;
        }
    }

    @Override
    protected void drawBackground(float tickDelta) {
        //RenderHelper.enableGUIStandardItemLighting();
        int x = ((width - backgroundWidth) / 2);
        int y = ((height - backgroundHeight) / 2);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
        int i1 = 240;
        int k1 = 240;
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1 / 1.0F, k1 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (slots != null) {
            for (AdvancedSlot slot : slots) {
                if (slot != null) {
                    slot.drawSprite(x, y);
                }
            }
        }

        GL11.glPopMatrix();
    }

    public void drawTooltipForSlotAt(int mouseX, int mouseY) {
        AdvancedSlot slot = getSlotAtLocation(mouseX, mouseY);

        if (slot != null) {
            slot.drawTooltip(this, mouseX, mouseY);
        }
    }

    public void drawTooltip(String caption, int mouseX, int mouseY) {
        if (caption.length() > 0) {
            int i2 = mouseX - 0;
            int k2 = mouseY - 0;
            minecraft.textRenderer.drawWithShadow(caption, mouseX, mouseY, 0xFFFFFF);
            //drawCreativeTabHoveringText(caption, i2, k2);
            //RenderHelper.enableGUIStandardItemLighting();
        }
    }

    public int getBackgroundWidth(){
        return backgroundWidth;
    }

    public int getBackgroundHeight(){
        return backgroundHeight;
    }

    public static ItemRenderer getItemRenderer () {
        return itemRenderer;
    }

    public void drawStack(ItemStack item, int x, int y) {
        Minecraft mc = Minecraft.INSTANCE;

        if (item != null) {
            GL11.glPushMatrix();
            GL11.glRotatef(120.0F, 1.0F, 0.0F, 0.0F);
            Lighting.turnOn();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            AdvancedInterfaceScreen.getItemRenderer().renderGuiItem(mc.textRenderer, mc.textureManager, item, x, y);
            AdvancedInterfaceScreen.getItemRenderer().renderGuiItemDecoration(mc.textRenderer, mc.textureManager, item, x, y);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            Lighting.turnOff();
            GL11.glPopMatrix();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        AdvancedSlot slot = getSlotAtLocation(mouseX, mouseY);

        if (slot != null && slot.isDefined()) {
            slotClicked(slot, button);
        }
    }

    public void resetNullSlots(int size) {
        slots.clear();

        for (int i = 0; i < size; ++i) {
            slots.add(null);
        }
    }

    protected void drawBackgroundSlots() {
        //RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
        int i1 = 240;
        int k1 = 240;

        int x = ((width - backgroundWidth) / 2);
        int y = ((height - backgroundHeight) / 2);

        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1 / 1.0F, k1 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (slots != null) {
            for (AdvancedSlot slot : slots) {
                if (slot != null) {
                    slot.drawSprite(x, y);
                }
            }
        }

        GL11.glPopMatrix();
    }

    // TODO: Use this for all children of this class
    protected void slotClicked(AdvancedSlot slot, int mouseButton) {
    }
}
