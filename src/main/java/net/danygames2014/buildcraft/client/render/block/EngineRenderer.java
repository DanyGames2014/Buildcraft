package net.danygames2014.buildcraft.client.render.block;

import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.texture.TextureManager;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class EngineRenderer {
    private final ModelPart box;
    private final ModelPart trunk;
    private final ModelPart movingBox;
    private final ModelPart chamber;

    private static final float[] angleMap = new float[6];

    static {
        angleMap[Direction.EAST.ordinal()] = (float) -Math.PI / 2;
        angleMap[Direction.WEST.ordinal()] = (float) Math.PI / 2;
        angleMap[Direction.UP.ordinal()] = 0;
        angleMap[Direction.DOWN.ordinal()] = (float) Math.PI;
        angleMap[Direction.SOUTH.ordinal()] = (float) Math.PI / 2;
        angleMap[Direction.NORTH.ordinal()] = (float) -Math.PI / 2;
    }

    public EngineRenderer(){
        box = new ModelPart(0, 1);
        box.addCuboid(-8F, -8F, -8F, 16, 4, 16);
        box.pivotX = 8;
        box.pivotY = 8;
        box.pivotZ = 8;

        trunk = new ModelPart(1, 1);
        trunk.addCuboid(-4F, -4F, -4F, 8, 12, 8);
        trunk.pivotX = 8F;
        trunk.pivotY = 8F;
        trunk.pivotZ = 8F;

        movingBox = new ModelPart(0, 1);
        movingBox.addCuboid(-8F, -4, -8F, 16, 4, 16);
        movingBox.pivotX = 8F;
        movingBox.pivotY = 8F;
        movingBox.pivotZ = 8F;

        chamber = new ModelPart(1, 1);
        chamber.addCuboid(-5F, -4, -5F, 10, 2, 10);
        chamber.pivotX = 8F;
        chamber.pivotY = 8F;
        chamber.pivotZ = 8F;
    }

    @SuppressWarnings("ExtractMethodRecommender")
    public void render(TextureManager textureManager, EnergyStage energy, float progress, Direction facing, String baseTexture, @Nullable String chamberTexturePath, @Nullable String trunkTexturePath, double x, double y, double z){
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glTranslatef((float) x, (float) y, (float) z);

        Direction renderFacing = facing.rotateCounterclockwise(Direction.Axis.Y);

        float step;

        if (progress > 0.5) {
            step = 7.99F - (progress - 0.5F) * 2F * 7.99F;
        } else {
            step = progress * 2F * 7.99F;
        }

        float translatefact = step / 16;

        float[] angle = {0, 0, 0};
        float[] translate = {renderFacing.getOffsetX(), renderFacing.getOffsetY(), -renderFacing.getOffsetZ()};

        switch (renderFacing) {
            case EAST:
            case WEST:
            case DOWN:
                angle[2] = angleMap[renderFacing.ordinal()];
                break;
            case SOUTH:
            case NORTH:
                angle[0] = angleMap[renderFacing.ordinal()];
            default:

                break;
        }

        box.pitch = angle[0];
        box.yaw = angle[1];
        box.roll = angle[2];

        trunk.pitch = angle[0];
        trunk.yaw = angle[1];
        trunk.roll = angle[2];

        movingBox.pitch = angle[0];
        movingBox.yaw = angle[1];
        movingBox.roll = angle[2];

        chamber.pitch = angle[0];
        chamber.yaw = angle[1];
        chamber.roll = angle[2];

        float factor = (float) (1.0 / 16.0);

        textureManager.bindTexture(textureManager.getTextureId(baseTexture));

        box.render(factor);

        GL11.glTranslatef(translate[2] * translatefact, translate[1] * translatefact, translate[0] * translatefact);
        movingBox.render(factor);
        GL11.glTranslatef(-translate[2] * translatefact, -translate[1] * translatefact, -translate[0] * translatefact);

        textureManager.bindTexture(textureManager.getTextureId(chamberTexturePath != null ? chamberTexturePath : "/assets/buildcraft/stationapi/textures/block/engine_chamber.png"));

        float chamberf = 2F / 16F;

        for (int i = 0; i <= step + 2; i += 2) {
            chamber.render(factor);
            GL11.glTranslatef(translate[2] * chamberf, translate[1] * chamberf, translate[0] * chamberf);
        }

        for (int i = 0; i <= step + 2; i += 2) {
            GL11.glTranslatef(-translate[2] * chamberf, -translate[1] * chamberf, -translate[0] * chamberf);
        }

        String texture = switch (energy) {
            case BLUE -> "/assets/buildcraft/stationapi/textures/block/engine_trunk_blue.png";
            case GREEN -> "/assets/buildcraft/stationapi/textures/block/engine_trunk_green.png";
            case YELLOW -> "/assets/buildcraft/stationapi/textures/block/engine_trunk_yellow.png";
            default -> "/assets/buildcraft/stationapi/textures/block/engine_trunk_red.png";
        };

        if(trunkTexturePath != null){
            texture = trunkTexturePath;
        }

        textureManager.bindTexture(textureManager.getTextureId(texture));

        trunk.render(factor);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

}
