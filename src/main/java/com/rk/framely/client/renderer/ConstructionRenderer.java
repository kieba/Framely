package com.rk.framely.client.renderer;


import com.rk.framely.block.BlockFrameManager;
import com.rk.framely.util.Pos;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ConstructionRenderer {

    private static final float OVERLAY_FIX = -1.0f / 4096.0f;
    private static final float ONE = 1.0f - OVERLAY_FIX;

    public static void renderConstructionMovement(List<Pos> list, double x, double y, double z, ForgeDirection direction, int tick, int maxTicks) {
        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        float progress = tick / (float) maxTicks;
        x += direction.offsetX * progress;
        y += direction.offsetY * progress;
        z += direction.offsetZ * progress;

        float d3 = BlockFrameManager.blockMovement.getInterpolatedU(0.0);
        float d4 = BlockFrameManager.blockMovement.getInterpolatedU(16.0);
        float d5 = BlockFrameManager.blockMovement.getInterpolatedV(0.0);
        float d6 = BlockFrameManager.blockMovement.getInterpolatedV(16.0);

        GL11.glPushMatrix();
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBegin(GL11.GL_QUADS);

        for (int i = 0; i < list.size(); i++) {
            drawBlockMovement(list.get(i), d3, d4, d5, d6);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private static void drawBlockMovement(Pos p, float d3, float d4, float d5, float d6) {
        // DOWN
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + OVERLAY_FIX, p.z + ONE, d3, d6);
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + OVERLAY_FIX, p.z + OVERLAY_FIX, d3, d5);
        addVertexWithUV(p.x + ONE, p.y + OVERLAY_FIX, p.z + OVERLAY_FIX, d4, d5);
        addVertexWithUV(p.x + ONE, p.y + OVERLAY_FIX, p.z + ONE, d4, d6);

        //UP
        addVertexWithUV(p.x + ONE, p.y + ONE, p.z + ONE, d3, d6);
        addVertexWithUV(p.x + ONE, p.y + ONE, p.z + OVERLAY_FIX, d3, d5);
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + ONE, p.z + OVERLAY_FIX, d4, d5);
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + ONE, p.z + ONE, d4, d6);

        //NORTH
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + ONE, p.z + OVERLAY_FIX, d4, d5);
        addVertexWithUV(p.x + ONE, p.y + ONE, p.z + OVERLAY_FIX, d3, d5);
        addVertexWithUV(p.x + ONE, p.y + OVERLAY_FIX, p.z + OVERLAY_FIX, d3, d6);
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + OVERLAY_FIX, p.z + OVERLAY_FIX, d4, d6);

        //SOUTH
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + ONE, p.z + ONE, d3, d5);
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + OVERLAY_FIX, p.z + ONE, d3, d6);
        addVertexWithUV(p.x + ONE, p.y + OVERLAY_FIX, p.z + ONE, d4, d6);
        addVertexWithUV(p.x + ONE, p.y + ONE, p.z + ONE, d4, d5);

        //WEST
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + ONE, p.z + ONE, d4, d5);
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + ONE, p.z + OVERLAY_FIX, d3, d5);
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + OVERLAY_FIX, p.z + OVERLAY_FIX, d3, d6);
        addVertexWithUV(p.x + OVERLAY_FIX, p.y + OVERLAY_FIX, p.z + ONE, d4, d6);

        //EAST
        addVertexWithUV(p.x + ONE, p.y + OVERLAY_FIX, p.z + ONE, d3, d6);
        addVertexWithUV(p.x + ONE, p.y + OVERLAY_FIX, p.z + OVERLAY_FIX, d4, d6);
        addVertexWithUV(p.x + ONE, p.y + ONE, p.z + OVERLAY_FIX, d4, d5);
        addVertexWithUV(p.x + ONE, p.y + ONE, p.z + ONE, d3, d5);
    }
    
    private static void addVertexWithUV(float x, float y, float z, float u, float v) {
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(x, y, z);
    }

    public static void renderConstruction(List<Pos> list, double x, double y, double z) {
        GL11.glPushMatrix();

        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float) x, (float)  y, (float) z);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2.5F);
        GL11.glBegin(GL11.GL_LINES);

        GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);

        for (int i = 0; i < list.size(); i++) {
            drawBlockBounds(list.get(i));
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

    private static void drawBlockBounds(Pos p) {
        //Bottom
        GL11.glVertex3d(p.x, p.y, p.z);
        GL11.glVertex3d(p.x, p.y, p.z + 1);

        GL11.glVertex3d(p.x, p.y, p.z);
        GL11.glVertex3d(p.x + 1, p.y, p.z);

        GL11.glVertex3d(p.x + 1, p.y, p.z + 1);
        GL11.glVertex3d(p.x, p.y, p.z + 1);

        GL11.glVertex3d(p.x + 1, p.y, p.z + 1);
        GL11.glVertex3d(p.x + 1, p.y, p.z);

        //Top
        GL11.glVertex3d(p.x, p.y + 1, p.z);
        GL11.glVertex3d(p.x, p.y + 1, p.z + 1);

        GL11.glVertex3d(p.x, p.y + 1, p.z);
        GL11.glVertex3d(p.x + 1, p.y + 1, p.z);

        GL11.glVertex3d(p.x + 1, p.y + 1, p.z + 1);
        GL11.glVertex3d(p.x, p.y + 1, p.z + 1);

        GL11.glVertex3d(p.x + 1, p.y + 1, p.z + 1);
        GL11.glVertex3d(p.x + 1, p.y + 1, p.z);

        //Sides
        GL11.glVertex3d(p.x, p.y, p.z);
        GL11.glVertex3d(p.x, p.y + 1, p.z);

        GL11.glVertex3d(p.x + 1, p.y, p.z);
        GL11.glVertex3d(p.x + 1, p.y + 1, p.z);

        GL11.glVertex3d(p.x, p.y, p.z + 1);
        GL11.glVertex3d(p.x, p.y + 1, p.z + 1);

        GL11.glVertex3d(p.x + 1, p.y, p.z + 1);
        GL11.glVertex3d(p.x + 1, p.y + 1, p.z + 1);
    }
}
