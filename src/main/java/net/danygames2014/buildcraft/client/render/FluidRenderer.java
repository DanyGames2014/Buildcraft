package net.danygames2014.buildcraft.client.render;

import net.danygames2014.buildcraft.client.render.entity.EntityBlockRenderer;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public final class FluidRenderer {
    public static final int DISPLAY_STAGES = 100;
    private static Map<Fluid, int[]> flowingRenderCache = new HashMap<>();
    private static Map<Fluid, int[]> stillRenderCache = new HashMap<>();
    private static final EntityBlockRenderer.RenderInfo liquidBlock = new EntityBlockRenderer.RenderInfo();

    // TODO: run this when stapi reloads resources
    public static void onTextureReload(){
        for (int[] ia : flowingRenderCache.values()) {
            for (int i : ia) {
                GL11.glDeleteLists(i, 1);
            }
        }
        flowingRenderCache.clear();

        for (int[] ia : stillRenderCache.values()) {
            for (int i : ia) {
                GL11.glDeleteLists(i, 1);
            }
        }
        stillRenderCache.clear();
    }

    public static int getFluidTexture(FluidStack fluidStack, boolean flowing) {
        if (fluidStack == null) {
            return -1;
        }
        return getFluidTexture(fluidStack.fluid, flowing);
    }

    public static int getFluidTexture(Fluid fluid, boolean flowing) {
        if (fluid == null) {
            return -1;
        }
        return flowing ? fluid.getFlowingBlock().getTexture(0) : fluid.getStillBlock().getTexture(0);
    }

    public static int[] getFluidDisplayLists(FluidStack fluidStack, World world, boolean flowing) {
        if (fluidStack == null) {
            return null;
        }
        Fluid fluid = fluidStack.fluid;
        if (fluid == null) {
            return null;
        }
        Map<Fluid, int[]> cache = flowing ? flowingRenderCache : stillRenderCache;
        int[] diplayLists = cache.get(fluid);
        if (diplayLists != null) {
            return diplayLists;
        }

        diplayLists = new int[DISPLAY_STAGES];

        if (fluid.getStillBlock() != null) {
            liquidBlock.baseBlock = fluid.getStillBlock();
        } else {
            liquidBlock.baseBlock = Block.WATER;
        }
        liquidBlock.texture = getFluidTexture(fluidStack, flowing);

        cache.put(fluid, diplayLists);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        for (int s = 0; s < DISPLAY_STAGES; ++s) {
            diplayLists[s] = GL11.glGenLists(1);
            GL11.glNewList(diplayLists[s], GL11.GL_COMPILE);

            liquidBlock.minX = 0.01f;
            liquidBlock.minY = 0;
            liquidBlock.minZ = 0.01f;

            liquidBlock.maxX = 0.99f;
            liquidBlock.maxY = Math.max(s, 1) / (float) DISPLAY_STAGES;
            liquidBlock.maxZ = 0.99f;

            EntityBlockRenderer.INSTANCE.renderBlock(liquidBlock);

            GL11.glEndList();
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        return diplayLists;
    }
}
