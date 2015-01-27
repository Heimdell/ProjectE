package moze_intel.projecte.gameObjs.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import moze_intel.projecte.gameObjs.items.ItemCharge;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SwingItemPKT;
import moze_intel.projecte.utils.Utils;
import moze_intel.projecte.utils.ConnectedBlockTraversal;
import moze_intel.projecte.utils.Point;
import moze_intel.projecte.utils.CoordinateBox;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DarkAxe extends ItemCharge
{
	final int BLOCKS_PER_CHARGE_POINT = 40;

	public DarkAxe()
	{
		super("dm_axe", (byte) 3);
		this.setNoRepair();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		return block.getMaterial() == Material.wood || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine;
	}
	
	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass) 
	{
		if (toolClass.equals("axe"))
		{
			return 4;
		}
		
		return -1;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int metadata)
	{
		if (canHarvestBlock(block, stack) || ForgeHooks.canToolHarvestBlock(block, metadata, stack))
		{
			return 14.0f + (12.0f * this.getCharge(stack));
		}
		
		return 1.0F;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			byte charge = this.getCharge(stack);
			
			if (charge == 0)
			{
				return stack;
			}
			
			final World worldRef = world;

			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

			if (mop == null)
				return stack;

			ConnectedBlockTraversal<Point> traversal = new ConnectedBlockTraversal() 
			{
				@Override
				public boolean traversable(Object obj) 
				{
					Point pt = (Point) obj;

					Block block = worldRef.getBlock(pt.x, pt.y, pt.z);

					if (block == Blocks.air)
						return false;

					ItemStack s = new ItemStack(block);
					int[] oreIds = OreDictionary.getOreIDs(s);
					
					if (oreIds.length == 0)
						return false;
					
					String oreName = OreDictionary.getOreName(oreIds[0]);
					
					return oreName.equals("logWood") || oreName.equals("treeLeaves");
				}

				@Override
				public List<Point> near(Object obj) 
				{
					Point pt = (Point) obj;

					return pt.near();
				}
			};

			Point origin = new Point(
				(int) mop.blockX,
				(int) mop.blockY,
				(int) mop.blockZ
			);

			Collection<Point> targets = traversal.runFromWithCapacity(
				origin,
				BLOCKS_PER_CHARGE_POINT * (charge + 1)
			);

			List<ItemStack> drops = new ArrayList();

			for (Point target : targets) {
				int x = target.x, y = target.y, z = target.z;

				Block block = world.getBlock(x, y, z);

				drops.addAll( 
					Utils.getBlockDrops(world, player, block, stack, x, y, z)
				);

				world.setBlockToAir(x, y, z);
			}
			
			if (!drops.isEmpty())
			{
				world.spawnEntityInWorld(new EntityLootBall(world, drops, player.posX, player.posY, player.posZ));
				PacketHandler.sendTo(new SwingItemPKT(), (EntityPlayerMP) player);
			}
		}
		
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(this.getTexture("dm_tools", "axe"));
	}
}
