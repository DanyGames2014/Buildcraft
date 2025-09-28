package net.danygames2014.buildcraft.screen.slot;

import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.buildcraft.screen.AdvancedInterfaceScreen;
import net.danygames2014.buildcraft.screen.BuildcraftScreen;
import net.danygames2014.buildcraft.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

public class AdvancedSlot {
    private static final String SLOT_TEXTURE = "/assets/buildcraft/stationapi/textures/gui/slot.png";
    public int x, y;
    public AdvancedInterfaceScreen screen;
    public boolean drawBackground = false;

    public AdvancedSlot(AdvancedInterfaceScreen screen, int x, int y) {
        this.x = x;
        this.y = y;
        this.screen = screen;
    }

    public String getDescription() {
        return null;
    }

    public final void drawTooltip(AdvancedInterfaceScreen screen, int x, int y) {
        String s = TranslationStorage.getInstance().get(getDescription());

        if (s != null) {
            screen.drawTooltip(s, x, y);
        } else {
            ItemStack stack = getItemStack();

            if (stack != null) {
                int cornerX = (screen.width - screen.getBackgroundWidth()) / 2;
                int cornerY = (screen.height - screen.getBackgroundHeight()) / 2;

                int xS = x - cornerX;
                int yS = y - cornerY;

                //screen.renderToolTip(stack, xS, yS);
            }
        }
    }

    public Atlas.Sprite getSprite() {
        return null;
    }

    public ItemStack getItemStack() {
        return null;
    }

    public boolean isDefined() {
        return true;
    }

    public void drawSprite(int cornerX, int cornerY) {
        Minecraft mc = Minecraft.INSTANCE;

        if (drawBackground) {
            mc.textureManager.bindTexture(mc.textureManager.getTextureId(SLOT_TEXTURE));
            screen.drawTexture(cornerX + x - 1, cornerY + y - 1, 0, 0, 18, 18);
        }

        if (!isDefined()) {
            return;
        }

        if (getItemStack() != null) {
            drawStack(getItemStack());
        } else if (getSprite() != null) {
            StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();
            //System.out.printf("Drawing advanced sprite %s (%d,%d) at %d %d\n", getIcon().getIconName(), getIcon().getOriginX(),getIcon().getOriginY(),cornerX + x, cornerY + y);

            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_COLOR_BUFFER_BIT);

            GL11.glDisable(GL11.GL_LIGHTING); // Make sure that render states are reset, an ItemStack can derp them up.
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);


            screen.drawSprite(getSprite(), cornerX + x, cornerY + y, 16, 16);

            GL11.glPopAttrib();
        }

    }

    public void drawStack(ItemStack item) {
        int cornerX = (screen.width - screen.getBackgroundWidth()) / 2;
        int cornerY = (screen.height - screen.getBackgroundHeight()) / 2;

        screen.drawStack(item, cornerX + x, cornerY + y);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void selected () {

    }
}
