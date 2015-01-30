package peaa.gameObjs.tiles;

import moze_intel.projecte.gameObjs.blocks.CondenserMK2;
import moze_intel.projecte.gameObjs.blocks.MatterFurnace;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import peaa.gameObjs.blocks.AEGU;

public class CondenserMK2TilePEAA extends CondenserMK2Tile
{
	private static final int INPUT_SLOTS[] = {1, 42};
	private static final int OUTPUT_SLOTS[] = {43, 84};

	//----
	// tierに合わせて番号を一つずらしています 最初に0が入っているのはそのためです
	private final int[] generateEmcOfAEGU = {0, 40, 1000, 20000};
	private String[][] coordAEGU = new String[3][26];	// AEGUの座標を記録
	private int numAEGU = 0;
	public boolean isGenerate = false;
	int generateEmc = 0;

	public CondenserMK2TilePEAA()
	{
		super();
	}

	@Override
	public void updateEntity()
	{

		super.updateEntity();

		if(!worldObj.isRemote) {
			//----作成したアイテムを隣接チェストに送る(MK2のみの能力)---------------------------------------------
			if (this.getWorldObj().getBlock(xCoord, yCoord, zCoord) instanceof CondenserMK2)
				pushToInventories();
			checkGenerate();

			if (isGenerate) {
				//System.out.println("generateEmc : " + ((double)generateEmc / 20));
				this.addEmc((double)generateEmc / 20);

			}
		}
	}

	@Override
	protected void condense()
	{
		for (int i = INPUT_SLOTS[0]; i <= INPUT_SLOTS[1]; i++)
		{
			ItemStack stack = inventory[i];

			if (stack == null || isStackEqualToLock(stack))
			{
				continue;
			}

			this.addEmc(Utils.getEmcValue(stack) * stack.stackSize);

			inventory[i] = null;
			break;
		}

		while (this.hasSpace() && this.getStoredEmc() >= requiredEmc)
		{
			pushStack();
			this.removeEmc(requiredEmc);
		}
	}

	@Override
	protected boolean hasSpace()
	{
		for (int i = OUTPUT_SLOTS[0]; i <= OUTPUT_SLOTS[1]; i++)
		{
			ItemStack stack = inventory[i];

			if (stack == null)
			{
				return true;
			}

			if (isStackEqualToLock(stack) && stack.stackSize < stack.getMaxStackSize())
			{
				return true;
			}
		}

		return false;
	}

	@Override
	protected int getSlotForStack()
	{
		for (int i = OUTPUT_SLOTS[0]; i <= OUTPUT_SLOTS[1]; i++)
		{
			ItemStack stack = inventory[i];

			if (stack == null)
			{
				return i;
			}

			if (isStackEqualToLock(stack) && stack.stackSize < stack.getMaxStackSize())
			{
				return i;
			}
		}

		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		if (slot == 0 || slot >= OUTPUT_SLOTS[0])
		{
			return false;
		}

		return !isStackEqualToLock(stack) && Utils.doesItemHaveEmc(stack);
	}

	//-----------------------------------------------------------------------------
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		//System.out.println("loaaaaaaaaaaaaaaaaaaaaaad");
		isGenerate = nbt.getBoolean("IsGenerate");
		numAEGU = nbt.getInteger("NumAEGU");
		generateEmc = nbt.getInteger("GenerateEmc");

		for (int i = 0; i < coordAEGU[0].length; i++)
		{
			coordAEGU[0][i] = nbt.getString("coordAEGUX" + i);
			coordAEGU[1][i] = nbt.getString("coordAEGUY" + i);
			coordAEGU[2][i] = nbt.getString("coordAEGUZ" + i);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		//System.out.println("saaaaaaaaaaaaaaaaaaaaaave");
		nbt.setBoolean("IsGenerate", isGenerate);
		nbt.setInteger("NumAEGU", numAEGU);
		nbt.setInteger("GenerateEmc", generateEmc);

		for (int i = 0; i < coordAEGU[0].length; i++)
		{
			if (coordAEGU[0][i] == null && coordAEGU[1][i] == null &&
				coordAEGU[2][i] == null)
				continue;

			nbt.setString("coordAEGUX" + i, coordAEGU[0][i]);
			nbt.setString("coordAEGUY" + i, coordAEGU[1][i]);
			nbt.setString("coordAEGUZ" + i, coordAEGU[2][i]);
		}

	}

	public int[] outputStorage = new int[] {43, 84};

	private void pushToInventories()
	{
		int iSide = 0;

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			if (dir.offsetY > 0)
			{
				continue;
			}

			int x = this.xCoord + dir.offsetX;
			int y = this.yCoord + dir.offsetY;
			int z = this.zCoord + dir.offsetZ;

			TileEntity tile = this.worldObj.getTileEntity(x, y, z);

			if (tile == null)
			{
				continue;
			}

			//「反物質炉」「マターかまど」のいずれでもないことを確認
			Block block = this.worldObj.getBlock(x, y, z);
			if(block instanceof Relay ||
					block instanceof MatterFurnace) {
				continue;
			}

			if (tile != null && tile instanceof ISidedInventory)
			{
				ISidedInventory inv = (ISidedInventory) tile;

				if (inv != null)
				{
					int[] slots = inv.getAccessibleSlotsFromSide(ForgeDirection.OPPOSITES[iSide]);

					if (slots.length > 0)
					{
						for (int j = outputStorage[0]; j < outputStorage[1]; j++)
						{
							ItemStack stack = inventory[j];

							if (stack == null)
							{
								continue;
							}

							for (int k : slots)
							{
								if (inv.canInsertItem(k, stack, Facing.oppositeSide[iSide]))
								{
									ItemStack otherStack = inv.getStackInSlot(k);

									if (otherStack == null)
									{
										inv.setInventorySlotContents(k, stack);
										inventory[j] = null;
										break;
									}
									else if (Utils.areItemStacksEqual(stack, otherStack))
									{
										int remain = otherStack.getMaxStackSize() - otherStack.stackSize;

										if (stack.stackSize <= remain)
										{
											otherStack.stackSize += stack.stackSize;
											inventory[j] = null;
											break;
										}
										else
										{
											otherStack.stackSize += remain;
											inventory[j].stackSize -= remain;
										}
									}
								}
							}
						}
					}
				}
			}
			else if (tile instanceof IInventory)
			{
				for (int j = outputStorage[0]; j <= outputStorage[1]; j++)
				{
					ItemStack stack = inventory[j];

					if (stack != null)
					{
						ItemStack result = Utils.pushStackInInv((IInventory) tile, stack);

						if (result == null)
						{
							inventory[j] = null;
							break;
						}
						else
						{
							inventory[j].stackSize = result.stackSize;
						}
					}
				}
			}
		}
	}

	//---------------------------------------------------------------
	public void storeAEGUCoord(Block block, int x, int y, int z)
	{
		addCoord(x, y, z);
		addGenerateEmc(block, x, y, z);
	}

	public void addGenerateEmc(Block block, int x, int y, int z) {
		AEGU aegu = (AEGU) block;
		// 生成エネルギーをAEGUに応じて追加
		//System.out.println("addEmc : " + generateEmcOfAEGU[aegu.getTier()]);
		generateEmc += this.generateEmcOfAEGU[aegu.getTier()];
		//System.out.println("generateEmc : " + generateEmc);
	}

	public boolean destoreAEGUCoord(Block block, int x, int y, int z)
	{
		if (decCoord(x, y, z)) {
			decGenerateEmc(block, x, y, z);
			return true;
		}
		return false;
	}

	public void decGenerateEmc(Block block, int x, int y, int z) {
		AEGU aegu = (AEGU) block;
		// 生成エネルギーをAEGUに応じて減算
		//System.out.println("decEmc : " + generateEmcOfAEGU[aegu.getTier()]);
		generateEmc -= this.generateEmcOfAEGU[aegu.getTier()];
		//System.out.println("generateEmc : " + generateEmc);
	}

	public void addCoord(int x, int y, int z)
	{
		int i = 0;
		for(; i < coordAEGU[0].length; i++) {
			//System.out.println("coordAEGU[0][" + i + "] " + coordAEGU[0][i]);
			if (coordAEGU[0][i] == null || coordAEGU[0][i].equals("")) {
				coordAEGU[0][i] = String.valueOf(x);
				coordAEGU[1][i] = String.valueOf(y);
				coordAEGU[2][i] = String.valueOf(z);
				this.addAEGUNum();
				//Minecraft.getMinecraft().thePlayer.sendChatMessage("登録しましたあああああ" + coordAEGU[0][i] + " " + coordAEGU[1][i] + " " + coordAEGU[2][i] + " ");
				return;
			}
		}
		if (i == coordAEGU[0].length) {
			Minecraft.getMinecraft().thePlayer.sendChatMessage("おけるブロックの数以上の配列を登録しようとしています。\nソースコードを見直してください。");
		}
	}

	public boolean decCoord(int x, int y, int z)
	{
		int num = checkStore(x, y, z);
		if (num != -1) {
			coordAEGU[0][num] = "";
			coordAEGU[1][num] = "";
			coordAEGU[2][num] = "";
			this.decAEGUNum();
			return true;
		}

		return false;
	}

	public void addAEGUNum()
	{
		this.numAEGU++;
		checkNum();
	}

	public void decAEGUNum()
	{
		this.numAEGU--;
		checkNum();
	}

	public void checkNum() {
		if (this.numAEGU < 0 || this.numAEGU > 26)
			Minecraft.getMinecraft().thePlayer.sendChatMessage("AEGUの数が不正です。ソースコードを見直してください。");
		//System.out.println("AEGUNum : " + this.numAEGU);
	}

	public void checkGenerate() {
		if (this.numAEGU < 0 || this.numAEGU > 26) {}
		else if (this.numAEGU >= 25) {
			//System.out.println("chechGenerate AEGUNum : " + this.numAEGU);
			if (isGenerate == false && checkCanUseAEGU() == false)
					return;

			changeGenerate(true);	// 生成開始
			isGenerate = true;
			//System.out.println("AEGUNum : " + this.numAEGU);
		} else if (isGenerate == true) {
			changeGenerate(false);	// 生成終了
			isGenerate = false;
		}
	}

	/**
	 * 自分(MK2)の周囲のAEGUに既に生成モードになっているものがあった場合
	 * (すでにそのAEGUが他のMK2に使われているということなので)
	 * falseを返す
	 * @return
	 */
	public boolean checkCanUseAEGU() {
		int i = 0;

		for(; i < coordAEGU[0].length; i++) {
			if (coordAEGU[0][i] == null || coordAEGU[0][i].equals("")){continue;}

			int x = Integer.parseInt(coordAEGU[0][i]);
			int y = Integer.parseInt(coordAEGU[1][i]);
			int z = Integer.parseInt(coordAEGU[2][i]);
			Block aegu = this.worldObj.getBlock(x, y, z);
			if (aegu instanceof AEGU) {
				if(((AEGU) aegu).isGenerate() == true)
					return false;
			}
		}

		return true;
	}

	public void changeGenerate(boolean bool) {
		int i = 0;
		for(; i < coordAEGU[0].length; i++) {
			if (coordAEGU[0][i] != null && !coordAEGU[0][i].equals("")) {
				int x = Integer.parseInt(coordAEGU[0][i]);
				int y = Integer.parseInt(coordAEGU[1][i]);
				int z = Integer.parseInt(coordAEGU[2][i]);
				//System.out.println("flag : " + bool + " x " + x + " y " + y + " z " + z);
				Block aegu = this.worldObj.getBlock(x, y, z);
				if (aegu instanceof AEGU) {
					if (((AEGU) aegu).isGenerate() != bool) {
						((AEGU) aegu).changeGenerate(worldObj, x, y, z);
					}
				} else {
					Minecraft.getMinecraft().thePlayer.sendChatMessage("モードチェンジの際によく分からない自体が発生しています。" + aegu);
				}
			}
		}
	}

	/**
	 * そのAEGUの座標が登録されていた場合その座標が登録されている配列の要素番号を、
	 * 登録されていなければ-1を返す
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public int checkStore(int x, int y, int z)
	{
		int i = 0;
		for(; i < coordAEGU[0].length; i++) {
			// このMK2にこのAEGUが登録されているなら
			if (coordAEGU[0][i].equals(String.valueOf(x)) && coordAEGU[1][i].equals(String.valueOf(y)) && coordAEGU[2][i].equals(String.valueOf(z)))
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * 周囲のAEGUを検索して登録
	 * @param world
	 * @param xCoord
	 * @param yCoord
	 * @param zCoord
	 */
	public void checkAroundAEGU(World world, int xCoord, int yCoord, int zCoord) {
		Block block;
		for (int x = xCoord - 1; x <= xCoord + 1; x++)
			for (int y = yCoord - 1; y <= yCoord + 1; y++)
				for (int z = zCoord - 1; z <= zCoord + 1; z++) {
					//System.out.println("x "+ x + " y " + y + " z " + z + " block name : " + world.getBlock(x, y, z));
					block = world.getBlock(x, y, z);
					if (block instanceof AEGU) {
						this.storeAEGUCoord(block, x, y, z);
					}
				}
	}
}
