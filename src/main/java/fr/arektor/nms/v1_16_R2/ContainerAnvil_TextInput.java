package fr.arektor.nms.v1_16_R2;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.ChatComponentText;
import net.minecraft.server.v1_16_R2.ContainerAnvil;
import net.minecraft.server.v1_16_R2.ContainerAnvilAbstract;
import net.minecraft.server.v1_16_R2.ContainerPlayer;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.IInventory;
import net.minecraft.server.v1_16_R2.ITileInventory;
import net.minecraft.server.v1_16_R2.ItemStack;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagList;
import net.minecraft.server.v1_16_R2.NBTTagString;
import net.minecraft.server.v1_16_R2.PlayerInventory;
import net.minecraft.server.v1_16_R2.Slot;
import net.minecraft.server.v1_16_R2.TileInventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;

import fr.arektor.common.utils.Reflector;
import fr.arektor.menucreator.MenuCreator;
import fr.arektor.menucreator.api.CustomGui;
import fr.arektor.menucreator.api.CustomGui.HumanAction;
import fr.arektor.menucreator.api.CustomPlayerGui.PlayerSlotType;
import fr.arektor.menucreator.api.TextInput;
import fr.arektor.nms.v1_16_R2.ContainerPlayer_Custom.CustomSlotPlayer;

public class ContainerAnvil_TextInput extends ContainerAnvil implements TextInput {

	private Reflector ref;
	private boolean validInput = true;

	private CustomGui parent = null;
	private Player owner = null;
	private Material validInput_material = Material.LIME_CONCRETE;
	private List<String> validInput_lore = new ArrayList<String>();
	private Material invalidInput_material = Material.RED_CONCRETE;
	private List<String> invalidInput_lore = new ArrayList<String>();
	private InputCondition condition = (s) -> { return true; };
	private InputCompletion completion = null;
	private String title,defaultInput;
	private HumanAction closeAction;
	private int jebait = Integer.MAX_VALUE-1;

	public ContainerAnvil_TextInput(Player owner) {
		this(owner, "");
	}

	public ContainerAnvil_TextInput(Player owner, String title) {
		this(owner, title, "");
	}

	public ContainerAnvil_TextInput(Player owner, String title, String defaultInput) {
		this((owner == null ? null : (((CraftPlayer)owner).getHandle())), title, defaultInput);
	}

	public ContainerAnvil_TextInput(EntityPlayer entityHuman) {
		this(entityHuman, "");
	}

	public ContainerAnvil_TextInput(EntityPlayer entityHuman, String title) {
		this(entityHuman, (entityHuman == null ? MenuCreator.nextContainerCounter() : entityHuman.nextContainerCounter()), title, "");
	}

	public ContainerAnvil_TextInput(EntityPlayer entityHuman, String title, String defaultInput) {
		this(entityHuman, (entityHuman == null ? MenuCreator.nextContainerCounter() : entityHuman.nextContainerCounter()), title, defaultInput);
	}

	public ContainerAnvil_TextInput(EntityPlayer entityHuman, int containerId, String title, String defaultInput) {
		super(containerId, entityHuman.inventory, net.minecraft.server.v1_16_R2.ContainerAccess.at(entityHuman.getWorld(), new BlockPosition(0, 0, 0)));
		this.ref = new Reflector(ContainerAnvilAbstract.class, this);
		this.title = title;
		if (defaultInput == null) this.defaultInput = "";
		else this.defaultInput = defaultInput;

		this.items.clear();
		this.slots.clear();

		this.a((Slot)(new GhostSlot(this.getRepairInventory(), 0, 27, 47)));
		this.a((Slot)(new GhostSlot(this.getRepairInventory(), 1, 76, 47)));
		this.a((Slot)(new ResultSlot(this.getResultInventory(), 2, 134, 47)));

		ContainerPlayer dContainer = entityHuman.defaultContainer;
		if (dContainer instanceof ContainerPlayer_Custom) {
			ContainerPlayer_Custom container = (ContainerPlayer_Custom)dContainer;
			for(int j = 0; j < 3; ++j) {
				for(int k = 0; k < 9; ++k) {
					CustomSlot cloneTarget = container.getSlots(PlayerSlotType.INVENTORY).get(k+(9*j));
					//cloneTarget.set(new org.bukkit.inventory.ItemStack(Material.values()[k+(9*j)]));
					this.a(new CustomSlotPlayer(container, entityHuman.inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18, k, j, PlayerSlotType.INVENTORY).copyDataOf(cloneTarget));
				}
			}
	
			for(int j = 0; j < 9; ++j) {
				CustomSlot cloneTarget = container.getSlots(PlayerSlotType.HOTBAR).get(j);
				this.a(new CustomSlotPlayer(container, entityHuman.inventory, j, 8 + j * 18, 142, j, 0, PlayerSlotType.HOTBAR).copyDataOf(cloneTarget));
			}
		} else {
			for(int j = 0; j < 3; ++j) {
				for(int k = 0; k < 9; ++k) {
					this.a(new Slot(entityHuman.inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
				}
			}
	
			for(int j = 0; j < 9; ++j) {
				this.a(new Slot(entityHuman.inventory, j, 8 + j * 18, 142));
			}
		}

		ItemStack is = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
		is.a(new ChatComponentText(this.defaultInput));
		this.getSlot(0).set(is);

		is = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(invalidInput_material));
		is.a(new ChatComponentText(this.defaultInput));
		this.getSlot(2).set(is);

		this.setOwner(entityHuman.getBukkitEntity());
		
		this.a(this.defaultInput);
	}

	private IInventory getRepairInventory() {
		return (IInventory) ref.get("repairInventory");
	}

	private IInventory getResultInventory() {
		return (IInventory) ref.get("resultInventory");
	}
	
	@Override
	public void setCloseAction(HumanAction action) {
		this.closeAction = action;
	}

	@Override
	public Player getOwner() {
		return this.owner;
	}

	@Override
	public boolean canUse(EntityHuman entityHuman) {
		return true;
	}
	
	@Override
	public boolean isInputValid() {
		return this.validInput;
	}
	
	@Override
	public InputCompletion getCompletion() {
		return this.completion;
	}
	
	@Override
	public String getInput() {
		if (this.renameText == null) return "";
		else return this.renameText;
	}

	@Override
	public void a(String s) {
		if (s == null) s = "";
		this.renameText = s;
		this.validInput = this.condition.check(s);
		if(this.getSlot(2).hasItem()) {
			/*
			String color = (validInput ? "�a" : "�c");
			this.getSlot(2).getItem().a(new ChatComponentText(color+this.renameText));
			 */
			ItemStack is;
			List<String> list = null;
			if (validInput) {
				is = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(validInput_material));
				is.a(new ChatComponentText("�a"+this.renameText));
				list = validInput_lore;
			} else {
				is = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(invalidInput_material));
				is.a(new ChatComponentText("�c"+this.renameText));
				list = invalidInput_lore;
			}
			if (list != null && !list.isEmpty()) {
				NBTTagCompound tag = is.getTag();
				NBTTagCompound display = tag.getCompound("display");
				NBTTagList lore = new NBTTagList();
				list.forEach((str) -> lore.add(NBTTagString.a(str)));
				display.set("Lore", lore);
				tag.set("display", display);
				is.setTag(tag);
			}
			this.getSlot(2).set(is);
		}

		this.e();
	}

	@Override
	public void e() {
		this.levelCost.set(0);
		/*
		if (this.owner != null && this.owner.getOpenInventory() != null) {
			this.owner.getOpenInventory().setProperty(Property.REPAIR_COST, 0);
		}
		*/
		this.c();
	}

	@Override
	public void c() {
		super.c();
		if (jebait <= 0) {
			this.owner.getOpenInventory().setProperty(Property.REPAIR_COST, 0);
		} else jebait--;
		/*
		Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> {
			//owner.updateInventory();
			owner.getOpenInventory().setProperty(Property.REPAIR_COST, 0);
			//levelCost.set(0);
		}, 1L);
		*/
		
	}

	@Override
	public void b(EntityHuman entityhuman) {
		final Player p;
		if (entityhuman instanceof EntityPlayer) p = ((EntityPlayer)entityhuman).getBukkitEntity();
		else if (owner != null) p = owner;
		else p = null;
		
		this.jebait = Integer.MAX_VALUE-1;
		PlayerInventory playerinventory = entityhuman.inventory;
		if(!playerinventory.getCarried().isEmpty()) {
			ItemStack carried = playerinventory.getCarried();
			playerinventory.setCarried(ItemStack.b);
			entityhuman.drop(carried, false);
		}
		if (this.closeAction != null) this.closeAction.run(entityhuman.getBukkitEntity());
		if (!MenuCreator.getPluginInstance().isEnabled()) return;
		if (this.parent != null && p != null) {
			Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> {
				p.updateInventory();
				if (!(entityhuman.activeContainer instanceof CustomGui) && !(entityhuman.activeContainer instanceof TextInput)) {
					this.parent.show(p);
					//this.owner.updateInventory();
				}
			}, 1L);
		}
	}

	@Override
	public void setTextCondition(InputCondition cond) {
		this.condition = cond;
		if (cond != null) this.validInput = cond.check(this.getInput());
		else this.validInput = true;
	}

	@Override
	public void setValidInputIcon(Material mat, List<String> lore) {
		this.validInput_material = mat;
		this.validInput_lore = lore;
	}

	@Override
	public void setInvalidInputIcon(Material mat, List<String> lore) {
		this.invalidInput_material = mat;
		this.invalidInput_lore = lore;
	}

	@Override
	public void setCompletion(InputCompletion completion) {
		this.completion = completion;
	}

	@Override
	public void setOwner(Player p) {
		this.owner = p;
	}
	
	public String getDefaultInput() {
		return this.defaultInput;
	}
	
	public void setDefaultInput(String s) {
		this.defaultInput = s;
	}

	private class ResultSlot extends Slot {

		ResultSlot(IInventory var1, int var2, int var3, int var4) {
			super(var1, var2, var3, var4);
		}

		public boolean isAllowed(ItemStack itemstack) {
			//System.out.println("isAllowed(ItemStack: "+itemstack.getItem().getName()+")");
			return false;
		}

		public boolean isAllowed(EntityHuman entityhuman) {
			//System.out.println("isAllowed(EntityHuman: "+entityhuman.getName()+")");
			//return condition.check(renameText);
			return false;
		}

		public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
			//System.out.println("a(EntityHuman: "+entityhuman.getName()+", ItemStack: "+itemstack.getItem().getName()+")");
			itemstack.setCount(0);
			entityhuman.activeContainer.getBukkitView().setCursor(null);
			ContainerAnvil_TextInput.this.setItem(0, ItemStack.b);
			if (completion != null) completion.completion(owner, renameText);
			entityhuman.closeInventory();
			return ItemStack.b;
		}

	}

	private class GhostSlot extends Slot {

		GhostSlot(IInventory var1, int var2, int var3, int var4) {
			super(var1, var2, var3, var4);
		}

		public boolean isAllowed(ItemStack itemstack) {
			return false;
		}

		public boolean isAllowed(EntityHuman entityhuman) {
			return false;
		}

		public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
			return itemstack;
		}

	}

	public InventoryView show() {
		this.jebait = 1;
		CraftPlayer p = (CraftPlayer)owner;
		ITileInventory inv =
				new TileInventory((var2x, var3, var4) -> {
					return ContainerAnvil_TextInput.this;
				}, new ChatComponentText(title));
		p.getHandle().openContainer(inv);
		this.owner.updateInventory();
		//p.getHandle().activeContainer.checkReachable = false;

		return this.getBukkitView();
	}
	
	public CustomGui getParent() {
		return this.parent;
	}
	
	public void setParent(CustomGui parent) {
		this.parent = parent;
	}
}
