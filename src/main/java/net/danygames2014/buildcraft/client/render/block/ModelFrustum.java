package net.danygames2014.buildcraft.client.render.block;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.Quad;
import net.minecraft.client.model.Vertex;
import net.minecraft.client.render.Tessellator;

public class ModelFrustum {
    /** X vertex coordinate of lower box corner */
    public final float posX1;

    /** Y vertex coordinate of lower box corner */
    public final float posY1;

    /** Z vertex coordinate of lower box corner */
    public final float posZ1;

    /** X vertex coordinate of upper box corner */
    public final float posX2;

    /** Y vertex coordinate of upper box corner */
    public final float posY2;

    /** Z vertex coordinate of upper box corner */
    public final float posZ2;

    /** The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8 points on a cube */
    private Vertex[] vertexPositions;

    /** An array of 6 TexturedQuads, one for each face of a cube */
    private Quad[] quadList;

    public ModelFrustum(ModelPart par1ModelRenderer, int textureOffsetX, int textureOffsetY, float originXI, float originYI, float originZI,
                        int bottomWidth, int bottomDepth, int topWidth, int topDepth, int height, float scaleFactor) {

        float originX = originXI;
        float originY = originYI;
        float originZ = originZI;

        this.posX1 = originX;
        this.posY1 = originY;
        this.posZ1 = originZ;

        this.vertexPositions = new Vertex[8];
        this.quadList = new Quad[6];

        float bottomDeltaX = bottomWidth > topWidth ? 0 : (topWidth - bottomWidth) / 2f;
        float topDeltaX = bottomWidth > topWidth ? (bottomWidth - topWidth) / 2f : 0;

        float bottomDeltaZ = bottomDepth > topDepth ? 0 : (topDepth - bottomDepth) / 2f;
        float topDeltaZ = bottomDepth > topDepth ? (bottomDepth - topDepth) / 2f : 0;

        float targetX = originX + Math.max((float) bottomWidth, (float) topWidth);
        float targetY = originY + height;
        float targetZ = originZ + Math.max((float) bottomDepth, (float) topDepth);

        this.posX2 = targetX;
        this.posY2 = targetY;
        this.posZ2 = targetZ;

        originX -= scaleFactor;
        originY -= scaleFactor;
        originZ -= scaleFactor;
        targetX += scaleFactor;
        targetY += scaleFactor;
        targetZ += scaleFactor;

        if (par1ModelRenderer.mirror) {
            float var14 = targetX;
            targetX = originX;
            originX = var14;
        }

        Vertex var23 = new Vertex(originX + bottomDeltaX, originY, originZ + bottomDeltaZ, 0.0F, 0.0F);
        Vertex var15 = new Vertex(targetX - bottomDeltaX, originY, originZ + bottomDeltaZ, 0.0F, 8.0F);
        Vertex var16 = new Vertex(targetX - topDeltaX, targetY, originZ + topDeltaZ, 8.0F, 8.0F);
        Vertex var17 = new Vertex(originX + topDeltaX, targetY, originZ + topDeltaZ, 8.0F, 0.0F);

        Vertex var18 = new Vertex(originX + bottomDeltaX, originY, targetZ - bottomDeltaZ, 0.0F, 0.0F);
        Vertex var19 = new Vertex(targetX - bottomDeltaX, originY, targetZ - bottomDeltaZ, 0.0F, 8.0F);
        Vertex var20 = new Vertex(targetX - topDeltaX, targetY, targetZ - topDeltaZ, 8.0F, 8.0F);
        Vertex var21 = new Vertex(originX + topDeltaX, targetY, targetZ - topDeltaZ, 8.0F, 0.0F);
        this.vertexPositions[0] = var23;
        this.vertexPositions[1] = var15;
        this.vertexPositions[2] = var16;
        this.vertexPositions[3] = var17;
        this.vertexPositions[4] = var18;
        this.vertexPositions[5] = var19;
        this.vertexPositions[6] = var20;
        this.vertexPositions[7] = var21;

        int depth = Math.max(bottomDepth, topDepth);
        int width = Math.max(bottomWidth, topWidth);

        this.quadList[0] = new Quad(new Vertex[] { var19, var15, var16, var20 }, textureOffsetX + depth + width, textureOffsetY
                                                                                                                                                + depth, textureOffsetX + depth + width + depth, textureOffsetY + depth + height);
        this.quadList[1] = new Quad(new Vertex[] { var23, var18, var21, var17 }, textureOffsetX, textureOffsetY + depth,
                textureOffsetX + depth, textureOffsetY + depth + height);
        this.quadList[2] = new Quad(new Vertex[] { var19, var18, var23, var15 }, textureOffsetX + depth, textureOffsetY,
                textureOffsetX + depth + width, textureOffsetY + depth);
        this.quadList[3] = new Quad(new Vertex[] { var16, var17, var21, var20 }, textureOffsetX + depth + width, textureOffsetY
                                                                                                                                                + depth, textureOffsetX + depth + width + width, textureOffsetY);
        this.quadList[4] = new Quad(new Vertex[] { var15, var23, var17, var16 }, textureOffsetX + depth, textureOffsetY
                                                                                                                                        + depth, textureOffsetX + depth + width, textureOffsetY + depth + height);
        this.quadList[5] = new Quad(new Vertex[] { var18, var19, var20, var21 }, textureOffsetX + depth + width + depth,
                textureOffsetY + depth, textureOffsetX + depth + width + depth + width, textureOffsetY + depth + height);
        this.quadList[1] = new Quad(new Vertex[] { var23, var18, var21, var17 }, textureOffsetX, textureOffsetY + depth,
                textureOffsetX + depth, textureOffsetY + depth + height);
        this.quadList[2] = new Quad(new Vertex[] { var19, var18, var23, var15 }, textureOffsetX + depth, textureOffsetY,
                textureOffsetX + depth + width, textureOffsetY + depth);
        this.quadList[3] = new Quad(new Vertex[] { var16, var17, var21, var20 }, textureOffsetX + depth + width, textureOffsetY
                                                                                                                                                + depth, textureOffsetX + depth + width + width, textureOffsetY);
        this.quadList[4] = new Quad(new Vertex[] { var15, var23, var17, var16 }, textureOffsetX + depth, textureOffsetY
                                                                                                                                        + depth, textureOffsetX + depth + width, textureOffsetY + depth + height);
        this.quadList[5] = new Quad(new Vertex[] { var18, var19, var20, var21 }, textureOffsetX + depth + width + depth,
                textureOffsetY + depth, textureOffsetX + depth + width + depth + width, textureOffsetY + depth + height);

        if (par1ModelRenderer.mirror) {
            for (Quad element : this.quadList) {
                element.flip();
            }
        }
    }

    /** Draw the six sided box defined by this ModelBox */
    public void render(Tessellator tessellator, float scale) {
        for (Quad element : this.quadList) {
            element.render(tessellator, scale);
        }
    }
}
