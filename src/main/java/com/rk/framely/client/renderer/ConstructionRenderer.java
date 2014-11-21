package com.rk.framely.client.renderer;


import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ConstructionRenderer {

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
