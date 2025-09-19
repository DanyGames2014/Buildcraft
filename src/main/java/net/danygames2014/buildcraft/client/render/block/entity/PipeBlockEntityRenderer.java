package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.client.render.entity.EntityBlockRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.IntHashMap;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.lwjgl.opengl.GL11;

public class PipeBlockEntityRenderer extends BlockEntityRenderer {
    public static final PipeBlockEntityRenderer INSTANCE = new PipeBlockEntityRenderer();

    public static final float DISPLAY_MULTIPLIER = 0.1f;
    public static final int POWER_STAGES = 100;

    private static final int LIQUID_STAGES = 40;
    private static final int MAX_ITEMS_TO_RENDER = 10;

    public int[] displayPowerList = new int[POWER_STAGES];
    public int[] displayPowerListOverload = new int[POWER_STAGES];

    private final IntHashMap displayFluidLists = new IntHashMap();
    private final int[] angleY = {0, 0, 270, 90, 0, 180};
    private final int[] angleZ = {90, 270, 0, 0, 0, 0};

    private boolean initialized = false;

    private class DisplayFluidList {
        public int[] sideHorizontal = new int[LIQUID_STAGES];
        public int[] sideVertical = new int[LIQUID_STAGES];
        public int[] centerHorizontal = new int[LIQUID_STAGES];
        public int[] centerVertical = new int[LIQUID_STAGES];
    }

    public void onTextureReload(){
        if (initialized) {
            for (int i = 0; i < POWER_STAGES; i++){
                GL11.glDeleteLists(displayPowerList[i], 1);
                GL11.glDeleteLists(displayPowerListOverload[i], 1);
            }
        }
        displayFluidLists.clear();

        initialized = false;
    }

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if(blockEntity instanceof PipeBlockEntity pipe){
            renderGatesWires(pipe, x, y, z);
        }
    }

    private void renderGatesWires(PipeBlockEntity pipe, double x, double y, double z){
        PipeRenderState state = pipe.renderState;

        if (state.wireMatrix.hasWire(PipeWire.RED)) {
            pipeWireRender(pipe, PipeWorldRenderer.PIPE_MIN_POS, PipeWorldRenderer.PIPE_MAX_POS, PipeWorldRenderer.PIPE_MIN_POS, PipeWire.RED, x, y, z);
        }

        if (state.wireMatrix.hasWire(PipeWire.BLUE)) {
            pipeWireRender(pipe, PipeWorldRenderer.PIPE_MAX_POS, PipeWorldRenderer.PIPE_MAX_POS, PipeWorldRenderer.PIPE_MAX_POS, PipeWire.BLUE, x, y, z);
        }

        if (state.wireMatrix.hasWire(PipeWire.GREEN)) {
            pipeWireRender(pipe, PipeWorldRenderer.PIPE_MAX_POS, PipeWorldRenderer.PIPE_MIN_POS, PipeWorldRenderer.PIPE_MIN_POS, PipeWire.GREEN, x, y, z);
        }

        if (state.wireMatrix.hasWire(PipeWire.YELLOW)) {
            pipeWireRender(pipe, PipeWorldRenderer.PIPE_MIN_POS, PipeWorldRenderer.PIPE_MIN_POS, PipeWorldRenderer.PIPE_MAX_POS, PipeWire.YELLOW, x, y, z);
        }
    }

    private void pipeWireRender(PipeBlockEntity pipe, float cx, float cy, float cz, PipeWire color, double x, double y, double z) {

        PipeRenderState state = pipe.renderState;

        float minX = PipeWorldRenderer.PIPE_MIN_POS;
        float minY = PipeWorldRenderer.PIPE_MIN_POS;
        float minZ = PipeWorldRenderer.PIPE_MIN_POS;

        float maxX = PipeWorldRenderer.PIPE_MAX_POS;
        float maxY = PipeWorldRenderer.PIPE_MAX_POS;
        float maxZ = PipeWorldRenderer.PIPE_MAX_POS;

        boolean foundX = false, foundY = false, foundZ = false;

        if (state.wireMatrix.isWireConnected(color, Direction.WEST)) {
            minX = 0;
            foundX = true;
        }

        if (state.wireMatrix.isWireConnected(color, Direction.EAST)) {
            maxX = 1;
            foundX = true;
        }

        if (state.wireMatrix.isWireConnected(color, Direction.DOWN)) {
            minY = 0;
            foundY = true;
        }

        if (state.wireMatrix.isWireConnected(color, Direction.UP)) {
            maxY = 1;
            foundY = true;
        }

        if (state.wireMatrix.isWireConnected(color, Direction.NORTH)) {
            minZ = 0;
            foundZ = true;
        }

        if (state.wireMatrix.isWireConnected(color, Direction.SOUTH)) {
            maxZ = 1;
            foundZ = true;
        }

        boolean center = false;

        if (minX == 0 && maxX != 1 && (foundY || foundZ)) {
            if (cx == PipeWorldRenderer.PIPE_MIN_POS) {
                maxX = PipeWorldRenderer.PIPE_MIN_POS;
            } else {
                center = true;
            }
        }

        if (minX != 0 && maxX == 1 && (foundY || foundZ)) {
            if (cx == PipeWorldRenderer.PIPE_MAX_POS) {
                minX = PipeWorldRenderer.PIPE_MAX_POS;
            } else {
                center = true;
            }
        }

        if (minY == 0 && maxY != 1 && (foundX || foundZ)) {
            if (cy == PipeWorldRenderer.PIPE_MIN_POS) {
                maxY = PipeWorldRenderer.PIPE_MIN_POS;
            } else {
                center = true;
            }
        }

        if (minY != 0 && maxY == 1 && (foundX || foundZ)) {
            if (cy == PipeWorldRenderer.PIPE_MAX_POS) {
                minY = PipeWorldRenderer.PIPE_MAX_POS;
            } else {
                center = true;
            }
        }

        if (minZ == 0 && maxZ != 1 && (foundX || foundY)) {
            if (cz == PipeWorldRenderer.PIPE_MIN_POS) {
                maxZ = PipeWorldRenderer.PIPE_MIN_POS;
            } else {
                center = true;
            }
        }

        if (minZ != 0 && maxZ == 1 && (foundX || foundY)) {
            if (cz == PipeWorldRenderer.PIPE_MAX_POS) {
                minZ = PipeWorldRenderer.PIPE_MAX_POS;
            } else {
                center = true;
            }
        }

        boolean found = foundX || foundY || foundZ;

        GL11.glPushMatrix();
        GL11.glColor3f(1, 1, 1);
        GL11.glTranslatef((float) x, (float) y, (float) z);

        float scale = 1.001f;
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);


        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();

        EntityBlockRenderer.RenderInfo renderBox = new EntityBlockRenderer.RenderInfo();
        renderBox.texture = state.wireMatrix.getWireTextureIndex(color);
        boolean isLit = (state.wireMatrix.getWireTextureIndex(color) & 1) > 0;

        // Z render

        if (minZ != PipeWorldRenderer.PIPE_MIN_POS || maxZ != PipeWorldRenderer.PIPE_MAX_POS || !found) {
            renderBox.setBounds(cx == PipeWorldRenderer.PIPE_MIN_POS ? cx - 0.05F : cx, cy == PipeWorldRenderer.PIPE_MIN_POS ? cy - 0.05F : cy, minZ, cx == PipeWorldRenderer.PIPE_MIN_POS ? cx
                                                                                                                                                      : cx + 0.05F, cy == PipeWorldRenderer.PIPE_MIN_POS ? cy : cy + 0.05F, maxZ);
            renderLitBox(renderBox, isLit);
        }

        // X render

        if (minX != PipeWorldRenderer.PIPE_MIN_POS || maxX != PipeWorldRenderer.PIPE_MAX_POS || !found) {
            renderBox.setBounds(minX, cy == PipeWorldRenderer.PIPE_MIN_POS ? cy - 0.05F : cy, cz == PipeWorldRenderer.PIPE_MIN_POS ? cz - 0.05F : cz, maxX, cy == PipeWorldRenderer.PIPE_MIN_POS ? cy
                                                                                                                                                            : cy + 0.05F, cz == PipeWorldRenderer.PIPE_MIN_POS ? cz : cz + 0.05F);
            renderLitBox(renderBox, isLit);
        }

        // Y render

        if (minY != PipeWorldRenderer.PIPE_MIN_POS || maxY != PipeWorldRenderer.PIPE_MAX_POS || !found) {
            renderBox.setBounds(cx == PipeWorldRenderer.PIPE_MIN_POS ? cx - 0.05F : cx, minY, cz == PipeWorldRenderer.PIPE_MIN_POS ? cz - 0.05F : cz, cx == PipeWorldRenderer.PIPE_MIN_POS ? cx
                                                                                                                                                      : cx + 0.05F, maxY, cz == PipeWorldRenderer.PIPE_MIN_POS ? cz : cz + 0.05F);
            renderLitBox(renderBox, isLit);
        }

        if (center || !found) {
            renderBox.setBounds(cx == PipeWorldRenderer.PIPE_MIN_POS ? cx - 0.05F : cx, cy == PipeWorldRenderer.PIPE_MIN_POS ? cy - 0.05F : cy, cz == PipeWorldRenderer.PIPE_MIN_POS ? cz - 0.05F : cz,
                    cx == PipeWorldRenderer.PIPE_MIN_POS ? cx : cx + 0.05F, cy == PipeWorldRenderer.PIPE_MIN_POS ? cy : cy + 0.05F, cz == PipeWorldRenderer.PIPE_MIN_POS ? cz : cz + 0.05F);
            renderLitBox(renderBox, isLit);
        }

        GL11.glPopMatrix();
    }

    private static void renderLitBox(EntityBlockRenderer.RenderInfo info, boolean isLit){
        EntityBlockRenderer.INSTANCE.renderBlock(info);
        if(isLit){
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GL11.glDepthMask(true);
            EntityBlockRenderer.INSTANCE.renderBlock(info);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
}
