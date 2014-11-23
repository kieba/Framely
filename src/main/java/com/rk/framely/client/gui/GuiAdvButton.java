package com.rk.framely.client.gui;

import com.rk.framely.reference.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;


public class GuiAdvButton extends GuiButton {

    private int texPosX;
    private int texPosY;
    private int texHeight;
    private int texWidth;
    private int buttonIndex = 0;
    private boolean isSelected = false;
    private ResourceLocation texture;

    public GuiAdvButton(int pID, int xPos, int yPos, int pWidth, int pHeight, int texPosX, int texPosY, int texWidth, int texHeight, ResourceLocation texture)
    {
        this(pID, xPos, yPos, pWidth, pHeight, texPosX, texPosY, texture);
        this.texHeight = texHeight;
        this.texWidth = texWidth;
    }

    public GuiAdvButton(int pID, int xPos, int yPos, int pWidth, int pHeight, int texPosX, int texPosY, ResourceLocation texture)
    {
        super(pID, xPos, yPos,  pWidth, pHeight, "");
        this.texPosX = texPosX;
        this.texPosY = texPosY;
        this.texture = texture;
        this.texHeight = pHeight;
        this.texWidth = pWidth;
    }

    public void setButtonIndex(int index) {
        buttonIndex = index;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setTextureFile(ResourceLocation texture) {
        this.texture = texture;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        if (this.visible) {
            Textures.loadTexture(texture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var5 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            float var7 = 0.00390625F;
            double top = ((var5 || isSelected) ? texPosY + texHeight : texPosY) * var7;
            double bottom = ((var5 || isSelected) ? texPosY + 2 * texHeight : texPosY + texHeight) * var7;
            double left = (texPosX + buttonIndex * texWidth) * var7;
            double right = (texPosX + (1 + buttonIndex) * texWidth) * var7;
            Tessellator var9 = Tessellator.instance;
            var9.startDrawingQuads();
            var9.addVertexWithUV(this.xPosition, this.yPosition, (double) this.zLevel, left, top);
            var9.addVertexWithUV(this.xPosition, this.yPosition + this.height, (double) this.zLevel, left, bottom);
            var9.addVertexWithUV(this.xPosition + this.width, this.yPosition + this.height, (double) this.zLevel, right, bottom);
            var9.addVertexWithUV(this.xPosition + this.width, this.yPosition, (double) this.zLevel, right, top);
            var9.draw();

            this.mouseDragged(par1Minecraft, par2, par3);
        }
    }
}

