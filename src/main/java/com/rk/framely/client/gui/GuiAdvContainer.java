package com.rk.framely.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

    public abstract class GuiAdvContainer extends GuiContainer {

        protected TileEntity tile;

        public GuiAdvContainer(Container par1Container, TileEntity tile) {
            super(par1Container);
            this.tile = tile;
            onGuiOpen();
        }

        protected void onGuiOpen() {

        }

        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
        }

}
