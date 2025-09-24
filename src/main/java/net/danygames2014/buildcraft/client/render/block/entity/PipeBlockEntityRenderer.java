package net.danygames2014.buildcraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.block.entity.pipe.TravellingFluid.FlowDirection;
import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.client.render.entity.EntityBlockRenderer;
import net.danygames2014.buildcraft.init.FluidListener;
import net.danygames2014.nyalib.fluid.Fluid;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
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

    private final Int2ObjectOpenHashMap displayFluidLists = new Int2ObjectOpenHashMap();
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

            if(pipe.transporter instanceof FluidPipeTransporter){
                renderFluids(pipe, x, y, z);
            }
        }
    }

    private DisplayFluidList getDisplayFluidList(Fluid fluid, int skylight, int blocklight, int flags, World world){
        int finalBlockLight = Math.max(flags & 31, blocklight);
        int listId = (fluid.getIdentifier().hashCode() & 0x3FFFF) << 13 | (flags & 0xE0 | finalBlockLight) << 5 | (skylight & 31);
        if (displayFluidLists.containsKey(listId)) {
            return (DisplayFluidList) displayFluidLists.get(listId);
        }

        if(fluid == null){
            return null;
        }

        DisplayFluidList d = new DisplayFluidList();
        displayFluidLists.put(listId, d);

        EntityBlockRenderer.RenderInfo block = new EntityBlockRenderer.RenderInfo();
        if(fluid.getStillBlock() != null){
            block.baseBlock = fluid.getStillBlock();
        } else {
            block.baseBlock = Block.WATER;
        }

        block.texture = fluid.getStillBlock().getTexture(0);
        block.brightness = skylight << 16 | finalBlockLight;

        float size = PipeWorldRenderer.PIPE_MAX_POS - PipeWorldRenderer.PIPE_MIN_POS;

        for(int s = 0; s < LIQUID_STAGES; ++s) {
            float ratio = (float) s / (float) LIQUID_STAGES;

            // SIDE HORIZONTAL

            d.sideHorizontal[s] = GL11.glGenLists(1);;
            GL11.glNewList(d.sideHorizontal[s], GL11.GL_COMPILE);

            block.minX = 0.0F;
            block.minZ = PipeWorldRenderer.PIPE_MIN_POS + 0.01F;

            block.maxX = block.minX + size / 2F + 0.01F;
            block.maxZ = block.minZ + size - 0.02F;

            block.minY = PipeWorldRenderer.PIPE_MIN_POS + 0.01F;
            block.maxY = block.minY + (size - 0.02F) * ratio;

            EntityBlockRenderer.INSTANCE.renderBlock(block);

            GL11.glEndList();

            // SIDE VERTICAL

            d.sideVertical[s] = GL11.glGenLists(1);
            GL11.glNewList(d.sideVertical[s], GL11.GL_COMPILE);

            block.minY = (float) (PipeWorldRenderer.PIPE_MAX_POS - 0.01);
            block.maxY = 1;

            block.minX = (float) (0.5 - (size / 2 - 0.01) * ratio);
            block.maxX = (float) (0.5 + (size / 2 - 0.01) * ratio);

            block.minZ = (float) (0.5 - (size / 2 - 0.01) * ratio);
            block.maxZ = (float) (0.5 + (size / 2 - 0.01) * ratio);

            EntityBlockRenderer.INSTANCE.renderBlock(block);

            GL11.glEndList();

            // CENTER HORIZONTAL

            d.centerHorizontal[s] = GL11.glGenLists(1);
            GL11.glNewList(d.centerHorizontal[s], GL11.GL_COMPILE);

            block.minX = (float) (PipeWorldRenderer.PIPE_MIN_POS + 0.01);
            block.minZ = (float) (PipeWorldRenderer.PIPE_MIN_POS + 0.01);

            block.maxX = (float) (block.minX + size - 0.02);
            block.maxZ = (float) (block.minZ + size - 0.02);

            block.minY = (float) (PipeWorldRenderer.PIPE_MIN_POS + 0.01);
            block.maxY = block.minY + (size - 0.02F) * ratio;

            EntityBlockRenderer.INSTANCE.renderBlock(block);

            GL11.glEndList();

            // CENTER VERTICAL

            d.centerVertical[s] = GL11.glGenLists(1);
            GL11.glNewList(d.centerVertical[s], GL11.GL_COMPILE);

            block.minY = (float) (PipeWorldRenderer.PIPE_MIN_POS + 0.01);
            block.maxY = (float) (PipeWorldRenderer.PIPE_MAX_POS - 0.01);

            block.minX = (float) (0.5 - (size / 2 - 0.02) * ratio);
            block.maxX = (float) (0.5 + (size / 2 - 0.02) * ratio);

            block.minZ = (float) (0.5 - (size / 2 - 0.02) * ratio);
            block.maxZ = (float) (0.5 + (size / 2 - 0.02) * ratio);

            EntityBlockRenderer.INSTANCE.renderBlock(block);

            GL11.glEndList();
        }
        return d;
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

        if (state.wireMatrix.isWireConnected(color, Direction.NORTH)) { //south
            minX = 0;
            foundX = true;
        }

        if (state.wireMatrix.isWireConnected(color, Direction.SOUTH)) { //north
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

        if (state.wireMatrix.isWireConnected(color, Direction.EAST)) { //west
            minZ = 0;
            foundZ = true;
        }

        if (state.wireMatrix.isWireConnected(color, Direction.WEST)) { //east
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

    private void renderFluids(PipeBlockEntity pipe, double x, double y, double z) {
        FluidPipeTransporter transporter = (FluidPipeTransporter) pipe.transporter;

        boolean needsRender = false;
        for (var side : ForgeDirection.values()) {
            if (transporter.getSideFillLevel(side) > 0) {
                needsRender = true;
                break;
            }
        }

        if (!needsRender) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTranslatef((float) x, (float) y, (float) z);

        int skylight = pipe.world.getBrightness(LightType.SKY, pipe.x, pipe.y, pipe.z);
        int blockLight = pipe.world.getBrightness(LightType.BLOCK, pipe.x, pipe.y, pipe.z);

        boolean sides = false, above = false;

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (side == ForgeDirection.UNKNOWN) {
                continue;
            }
            
            if (pipe.connections.get(side.getDirection()) == PipeConnectionType.NONE) {
                continue;
            }
            
            if (transporter.getSideFillLevel(side) <= 0) {
                continue;
            }

            DisplayFluidList d = getDisplayFluidList(FluidListener.fuel, skylight, blockLight, 0, pipe.world);

            if (d == null) {
                continue;
            }

            int stage = (int) ((float) transporter.getSideFillLevel(side) / (float) (transporter.getCapacity()) * (LIQUID_STAGES - 1));
            stage = Math.abs(stage);

            GL11.glPushMatrix();
            int list = 0;

            switch (side) {
                case UP:
                    above = true;
                    list = d.sideVertical[stage];
                    break;
                case DOWN:
                    GL11.glTranslatef(0, -0.75F, 0);
                    list = d.sideVertical[stage];
                    break;
                case EAST:
                case WEST:
                case SOUTH:
                case NORTH:
                    sides = true;
                    // Yes, this is kind of ugly, but was easier than transform the coordinates above.
                    GL11.glTranslatef(0.5F, 0.0F, 0.5F);
                    GL11.glRotatef(angleY[side.getDirection().ordinal()], 0, 1, 0);
                    GL11.glRotatef(angleZ[side.getDirection().ordinal()], 0, 0, 1);
                    GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
                    list = d.sideHorizontal[stage];
                    break;
                default:
            }
            StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();
            //RenderUtils.setGLColorFromInt(fluidRenderData.color); TODO: support fluid color
            GL11.glCallList(list);
            GL11.glPopMatrix();
        }
        
        // CENTER
        if (transporter.getSideFillLevel(ForgeDirection.UNKNOWN) > 0) {
            DisplayFluidList d = getDisplayFluidList(FluidListener.fuel, skylight, blockLight, 0, pipe.world);

            if (d != null) {
                int stage = (int) ((float) transporter.getSideFillLevel(ForgeDirection.UNKNOWN) / (float) (transporter.getCapacity()) * (LIQUID_STAGES - 1));

                StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();
                //RenderUtils.setGLColorFromInt(fluidRenderData.color); TODO: support fluid color

                if (above) {
                    GL11.glCallList(d.centerVertical[stage]);
                }

                if (!above || sides) {
                    GL11.glCallList(d.centerHorizontal[stage]);
                }
            }

        }

        GL11.glPopAttrib();
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
