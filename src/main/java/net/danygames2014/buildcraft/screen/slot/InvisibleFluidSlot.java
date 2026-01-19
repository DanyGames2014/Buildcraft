package net.danygames2014.buildcraft.screen.slot;

import net.danygames2014.nyalib.fluid.FluidSlot;
import net.danygames2014.nyalib.fluid.block.FluidHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;

public class InvisibleFluidSlot extends FluidSlot {
    public InvisibleFluidSlot(FluidHandler handler, int index, int x, int y, int width, int height) {
        super(handler, index, x, y, width, height);
    }

    @Override
    public void render(Minecraft minecraft, TextRenderer textRenderer, ItemRenderer itemRenderer, HandledScreen screen, int mouseX, int mouseY, float delta, FluidSlot slot, int slotX, int slotY) {
    }
}
