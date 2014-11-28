package com.rk.framely.block;

import com.rk.framely.Framely;
import com.rk.framely.proxy.ClientProxy;
import com.rk.framely.proxy.CommonProxy;
import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import com.rk.framely.tileentity.TileEntityFrameBase;
import com.rk.framely.tileentity.TileEntityFrameManager;
import com.rk.framely.util.LogHelper;
import com.rk.framely.util.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.sql.Ref;

public class BlockFrameManager extends BlockFrameBase implements ITileEntityProvider {

    public static IIcon blockMovement;

    private IIcon[] icons = new IIcon[2];

    public BlockFrameManager(){
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_FRAME_MANAGER_NAME);
    }


    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityFrameManager();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityFrameManager) {
                player.openGui(Framely.INSTANCE, Reference.GUI_FRAME_MANAGER, world, x, y, z);
            }
            return true;
        }
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int p_149725_5_) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityFrameManager) {
            ((TileEntityFrameManager) tile).onBlockRemoved();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        String name = getUnwrappedUnlocalizedName(this.getUnlocalizedName());
        blockMovement = iconRegister.registerIcon(name + "_blockMovement");
        icons[0] = iconRegister.registerIcon(name + "_unbound");
        icons[1] = iconRegister.registerIcon(name + "_bound");
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity entity = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if(entity instanceof TileEntityFrameBase){
                if(((TileEntityFrameBase) entity).getFrameManager()!=null){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return CommonProxy.renderIdFrameManager;
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        if(tileEntity instanceof TileEntityFrameManager) {
            TileEntityFrameManager tileManager = (TileEntityFrameManager) tileEntity;
            if(tileManager.relativeConstruction.isEmpty())
                return icons[0];
        }
        return icons[1];
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[0];
    }

}
