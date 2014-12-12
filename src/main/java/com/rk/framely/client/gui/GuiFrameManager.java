package com.rk.framely.client.gui;

import com.rk.framely.Framely;
import com.rk.framely.network.PacketTileSimpleAction;
import com.rk.framely.reference.Reference;
import com.rk.framely.reference.Textures;
import com.rk.framely.tileentity.TileEntityFrameManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class GuiFrameManager extends GuiContainer {

    public static final int BUTTON_ID_START_INDEX = 0;

    private GuiAdvButton btnGenerateConstruction;
    private GuiAdvButton btnRemoveConstruction;
    private GuiAdvButton btnShowConstruction;

    private TileEntityFrameManager frameManager;

    public GuiFrameManager(InventoryPlayer inventory, TileEntityFrameManager frameManager) {
        super(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer p_75145_1_) {
                return true;
            }
        });
        this.frameManager = frameManager;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 0){ //buildConstruct
            PacketTileSimpleAction ptsa = new PacketTileSimpleAction(Reference.NETWORK_PACKET_ID_TILE_SIMPLE_ACTION,frameManager.getPosition(),"buildConstruct");
            Framely.INSTANCE.packetHandler.sendPacket(ptsa.getPacket());
        }
        if(button.id == 1){ //buildConstruct
            PacketTileSimpleAction ptsa = new PacketTileSimpleAction(Reference.NETWORK_PACKET_ID_TILE_SIMPLE_ACTION,frameManager.getPosition(),"removeConstruct");
            Framely.INSTANCE.packetHandler.sendPacket(ptsa.getPacket());
        }
        if(button.id == 2){ //buildConstruct
            PacketTileSimpleAction ptsa = new PacketTileSimpleAction(Reference.NETWORK_PACKET_ID_TILE_SIMPLE_ACTION,frameManager.getPosition(),"grid");
            Framely.INSTANCE.packetHandler.sendPacket(ptsa.getPacket());
        }

    }

    @Override
    public void initGui() {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        int buttonId = BUTTON_ID_START_INDEX;
        Random rand = new Random();
        btnGenerateConstruction = new GuiAdvButton(buttonId++, x+10,y+10,9,10,176,0, Textures.frameManagerGui);
        btnRemoveConstruction = new GuiAdvButton(buttonId++, x+10,y+25,9,10,176,0, Textures.frameManagerGui);
        btnShowConstruction = new GuiAdvButton(buttonId++, x+10,y+40,9,10,176,0, Textures.frameManagerGui);

        buttonList.add(btnGenerateConstruction);
        buttonList.add(btnRemoveConstruction);
        buttonList.add(btnShowConstruction);

        btnGenerateConstruction.visible = true;
        btnRemoveConstruction.visible = true;
        btnShowConstruction.visible = true;
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Textures.loadTexture(Textures.frameManagerGui);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        fontRendererObj.drawString("Build Construct",x+20,y+10,4210752);
        fontRendererObj.drawString("Destroy Construct",x+20,y+25,4210752);

        if(frameManager.showConstructionGrid){
            fontRendererObj.drawString("Hide Construct Grid",x+20,y+40,4210752);
        }else
        {
            fontRendererObj.drawString("Show Construct Grid",x+20,y+40,4210752);
        }

        fontRendererObj.drawString("Power Consumption: " + frameManager.getEnergyPerMovement() + " RF", x+10, y+60, 4210752);
    }
}
