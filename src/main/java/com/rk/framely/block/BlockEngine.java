package com.rk.framely.block;

import com.rk.framely.Framely;
import com.rk.framely.proxy.CommonProxy;
import com.rk.framely.reference.Reference;
import com.rk.framely.tileentity.TileEntityEngine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEngine extends BlockFrameBase implements ITileEntityProvider {

    private final static int[][] ICON_ROTATION = new int[][] {
            {5,4,0,0,0,0}, //DOWN
            {4,5,1,1,1,1}, //UP
            {1,1,5,4,3,2}, //NORTH
            {0,0,4,5,2,3}, //SOUTH
            {3,3,2,3,5,4}, //WEST
            {2,2,3,2,4,5}  //EAST
    };

    /**
     * icons[0] = down
     * icons[1] = up
     * icons[2] = right
     * icons[3] = left
     * icons[4] = in
     * icons[5] = out
     */
    private IIcon[] icons = new IIcon[6];

    public BlockEngine() {
        super(Material.iron);
        this.setBlockName(Reference.BLOCK_ENGINE_NAME);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int faceHit, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityEngine) {
                ((TileEntityEngine) tile).onBlockActivated();
            }
            return true;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityEngine();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityEngine) {
            ForgeDirection direction = ForgeDirection.UNKNOWN;
            int facing = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

            if (facing == 0) {
                direction = ForgeDirection.NORTH;
            } else if (facing == 1) {
                direction = ForgeDirection.EAST;
            } else if (facing == 2) {
                direction = ForgeDirection.SOUTH;
            } else if (facing == 3) {
                direction = ForgeDirection.WEST;
            }

            if (entityLiving.rotationPitch > 60.0f) {
                direction = ForgeDirection.DOWN;
            } else if (entityLiving.rotationPitch < -60.0f) {
                direction = ForgeDirection.UP;
            }

            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityEngine) {
                ((TileEntityEngine) te).setDir(direction);
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        String name = getUnwrappedUnlocalizedName(this.getUnlocalizedName());
        icons[0] = iconRegister.registerIcon(name + "_down");
        icons[1] = iconRegister.registerIcon(name + "_up");
        icons[2] = iconRegister.registerIcon(name + "_right");
        icons[3] = iconRegister.registerIcon(name + "_left");
        icons[4] = iconRegister.registerIcon(name + "_in");
        icons[5] = iconRegister.registerIcon(name + "_out");
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity tileEntity = blockAccess.getTileEntity(x, y, z);
        if(tileEntity instanceof TileEntityEngine) {
            return icons[ICON_ROTATION[((TileEntityEngine)tileEntity).getDir().ordinal()][side]];
        }
        return icons[side];
    }

    /*
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return CommonProxy.framelyRenderId;
    }
    */
}
