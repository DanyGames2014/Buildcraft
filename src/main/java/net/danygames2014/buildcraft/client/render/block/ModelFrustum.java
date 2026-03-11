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

    /** An array of 6 TexturedQuads, one for each face of a cube */
    private final Quad[] quadList;

    public ModelFrustum(ModelPart par1ModelRenderer, int textureOffsetX, int textureOffsetY, float originXI, float originYI, float originZI,
                        int bottomWidth, int bottomDepth, int topWidth, int topDepth, int height, float scaleFactor) {

        float originX = originXI;
        float originY = originYI;
        float originZ = originZI;

        this.posX1 = originX;
        this.posY1 = originY;
        this.posZ1 = originZ;

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

        Vertex v1 = new Vertex(originX + bottomDeltaX, originY, originZ + bottomDeltaZ, 0.0F, 0.0F);
        Vertex v2 = new Vertex(targetX - bottomDeltaX, originY, originZ + bottomDeltaZ, 0.0F, 8.0F);
        Vertex v3 = new Vertex(targetX - topDeltaX, targetY, originZ + topDeltaZ, 8.0F, 8.0F);
        Vertex v4 = new Vertex(originX + topDeltaX, targetY, originZ + topDeltaZ, 8.0F, 0.0F);

        Vertex v5 = new Vertex(originX + bottomDeltaX, originY, targetZ - bottomDeltaZ, 0.0F, 0.0F);
        Vertex v6 = new Vertex(targetX - bottomDeltaX, originY, targetZ - bottomDeltaZ, 0.0F, 8.0F);
        Vertex v7 = new Vertex(targetX - topDeltaX, targetY, targetZ - topDeltaZ, 8.0F, 8.0F);
        Vertex v8 = new Vertex(originX + topDeltaX, targetY, targetZ - topDeltaZ, 8.0F, 0.0F);

        int depth = Math.max(bottomDepth, topDepth);
        int width = Math.max(bottomWidth, topWidth);

        this.quadList[0] = new Quad(new Vertex[] { v6, v2, v3, v7 }, textureOffsetX + depth + width, textureOffsetY
                                                                                                                                                + depth, textureOffsetX + depth + width + depth, textureOffsetY + depth + height);
        this.quadList[1] = new Quad(new Vertex[] { v1, v5, v8, v4 }, textureOffsetX, textureOffsetY + depth,
                textureOffsetX + depth, textureOffsetY + depth + height);
        this.quadList[2] = new Quad(new Vertex[] { v6, v5, v1, v2 }, textureOffsetX + depth, textureOffsetY,
                textureOffsetX + depth + width, textureOffsetY + depth);
        this.quadList[3] = new Quad(new Vertex[] { v3, v4, v8, v7 }, textureOffsetX + depth + width, textureOffsetY
                                                                                                                                                + depth, textureOffsetX + depth + width + width, textureOffsetY);
        this.quadList[4] = new Quad(new Vertex[] { v2, v1, v4, v3 }, textureOffsetX + depth, textureOffsetY
                                                                                                                                        + depth, textureOffsetX + depth + width, textureOffsetY + depth + height);
        this.quadList[5] = new Quad(new Vertex[] { v5, v6, v7, v8 }, textureOffsetX + depth + width + depth,
                textureOffsetY + depth, textureOffsetX + depth + width + depth + width, textureOffsetY + depth + height);
        
        this.quadList[1] = new Quad(new Vertex[] { v1, v5, v8, v4 }, textureOffsetX, textureOffsetY + depth,
                textureOffsetX + depth, textureOffsetY + depth + height);
        this.quadList[2] = new Quad(new Vertex[] { v6, v5, v1, v2 }, textureOffsetX + depth, textureOffsetY,
                textureOffsetX + depth + width, textureOffsetY + depth);
        this.quadList[3] = new Quad(new Vertex[] { v3, v4, v8, v7 }, textureOffsetX + depth + width, textureOffsetY
                                                                                                                                                + depth, textureOffsetX + depth + width + width, textureOffsetY);
        this.quadList[4] = new Quad(new Vertex[] { v2, v1, v4, v3 }, textureOffsetX + depth, textureOffsetY
                                                                                                                                        + depth, textureOffsetX + depth + width, textureOffsetY + depth + height);
        this.quadList[5] = new Quad(new Vertex[] { v5, v6, v7, v8 }, textureOffsetX + depth + width + depth,
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
