package fr.arektor.nms.v1_16_R2;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_16_R2.ChatComponentText;
import net.minecraft.server.v1_16_R2.Container;
import net.minecraft.server.v1_16_R2.ContainerChest;
import net.minecraft.server.v1_16_R2.ContainerPlayer;
import net.minecraft.server.v1_16_R2.Containers;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.IInventory;
import net.minecraft.server.v1_16_R2.ITileInventory;
import net.minecraft.server.v1_16_R2.InventorySubcontainer;
import net.minecraft.server.v1_16_R2.ItemStack;
import net.minecraft.server.v1_16_R2.PlayerInventory;
import net.minecraft.server.v1_16_R2.Slot;
import net.minecraft.server.v1_16_R2.TileInventory;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import fr.arektor.common.utils.Reflector;
import fr.arektor.menucreator.Config;
import fr.arektor.menucreator.MenuCreator;
import fr.arektor.menucreator.api.CustomGui;
import fr.arektor.menucreator.api.CustomPlayerGui.PlayerSlotType;
import fr.arektor.menucreator.api.Slot.HumanAction;
import fr.arektor.menucreator.api.StaticSlot;
import fr.arektor.menucreator.api.TextInput;
import fr.arektor.nms.v1_16_R2.ContainerPlayer_Custom.CustomSlotPlayer;

public class ContainerChest_Custom extends ContainerChest implements CustomGui {

	private Reflector reflector;
	private HumanAction closeAction;
	private DataChangeReaction dataChangeReaction;
	private String ctitle;
	private Player owner;
	private CustomGui parent = null, mirrorOf = null;
	private int length = 3;

	public ContainerChest_Custom(Player owner) {
		this(owner, "Default Title");
	}

	public ContainerChest_Custom(Player owner, String title) {
		this(owner, title, 27);
	}

	public ContainerChest_Custom(Player owner, String title, int size) {
		this((owner == null ? null : (((CraftPlayer)owner).getHandle())), title, size);
		this.owner = owner;
	}

	public ContainerChest_Custom(EntityPlayer owner) {
		this(owner, "Default Title");
	}

	public ContainerChest_Custom(EntityPlayer owner, String title) {
		this(owner, title, 27);
	}

	public ContainerChest_Custom(EntityPlayer owner, String title, int size) {
		this(getGenericBySize(size), (owner == null ? MenuCreator.nextContainerCounter() : owner.nextContainerCounter()), owner.inventory, new InventorySubcontainer(size), size/9, null);
		this.ctitle = title;
	}
	
	public ContainerChest_Custom(Player owner, int i, PlayerInventory playerinventory, int size, CustomGui gui) {
		this(getGenericBySize(size), i, playerinventory, new InventorySubcontainer(size), size/9, gui);
		this.owner = owner;
	}

	public ContainerChest_Custom(Containers<?> containers, int i, PlayerInventory playerinventory, IInventory iinventory, int j, CustomGui gui) {
		super(containers, i, playerinventory, iinventory, j);
		int k = (j - 4) * 18;

		this.reflector = new Reflector(Container.class, this);
		this.items.clear();
		this.slots.clear();
		
		this.length = j;

		if (gui != null) {
			for(int l = 0; l < j; ++l) {
				for(int i1 = 0; i1 < 9; ++i1) {
					//this.a(new CustomSlot(this, iinventory, i1 + l * 9, 8 + i1 * 18, 18 + l * 18, i1, l));
					this.a(new CustomSlot(iinventory, i1 + l * 9, 8 + i1 * 18, 18 + l * 18, i1, l, (StaticSlot)gui.getCustomSlot(i1+9*l)));
				}
			}
		} else {
			for(int l = 0; l < j; ++l) {
				for(int i1 = 0; i1 < 9; ++i1) {
					this.a(new CustomSlot(this, iinventory, i1 + l * 9, 8 + i1 * 18, 18 + l * 18, i1, l));
				}
			}
		}

		ContainerPlayer dContainer = playerinventory.player.defaultContainer;
		if (dContainer instanceof ContainerPlayer_Custom) {
			ContainerPlayer_Custom container = (ContainerPlayer_Custom)dContainer;
			for(int l = 0; l < 3; ++l) {
				for(int i1 = 0; i1 < 9; ++i1) {
					CustomSlot cloneTarget = container.getSlots(PlayerSlotType.INVENTORY).get(i1+(9*l));
					this.a(new CustomSlotPlayer(this, playerinventory, i1 + l * 9 + 9, 8 + i1 * 18, 103 + l * 18 + k, i1, l, PlayerSlotType.INVENTORY).copyDataOf(cloneTarget));
				}
			}
	
			for(int l = 0; l < 9; ++l) {
				CustomSlot cloneTarget = container.getSlots(PlayerSlotType.HOTBAR).get(l);
				this.a(new CustomSlotPlayer(this, playerinventory, l, 8 + l * 18, 161 + k, l, 0, PlayerSlotType.HOTBAR).copyDataOf(cloneTarget));
			}
		} else {
			for(int l = 0; l < 3; ++l) {
				for(int i1 = 0; i1 < 9; ++i1) {
					this.a(new Slot(playerinventory, i1 + l * 9 + 9, 8 + i1 * 18, 103 + l * 18 + k));
				}
			}
	
			for(int l = 0; l < 9; ++l) {
				this.a(new Slot(playerinventory, l, 8 + l * 18, 161 + k));
			}
		}
	}
	
	@Override
	public int getWidth() {
		return 9;
	}
	
	@Override
	public int getLength() {
		return this.length;
	}

	@Override
	public Player getOwner() {
		return this.owner;
	}

	@Override
	public void setCloseAction(HumanAction action) {
		this.closeAction = action;
	}
	
	@Override
	public HumanAction getCloseAction() {
		return this.closeAction;
	}

	@Override
	public void c() {
		super.c();
		//Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> owner.updateInventory(), 1L);
	}

	/*
	@Override
	public fr.tenebrae.MenuCreator.gui.Slot getCustomSlot(int i) {
		if (i < 0 || this.slots.size() <= i) return null;

		Slot slot = this.getSlot(i);
		if (slot instanceof CustomSlot) return (fr.tenebrae.MenuCreator.gui.Slot)slot;
		else return null;
	}
	*/

	@Override
	public InventoryView show() { return this.show(this.owner); }

	@Override
	public InventoryView show(HumanEntity human) {
		if (human == null) {
			MenuCreator.getPluginInstance().getLogger().warning("Attempted to show inventory to null entity");
			return null;
		}
		CraftHumanEntity p = (CraftHumanEntity)human;
		ITileInventory inv =
				new TileInventory((var2x, var3, var4) -> {
					return ContainerChest_Custom.this;
				}, new ChatComponentText(ctitle));
		reflector.set("title", null);
		p.getHandle().openContainer(inv);
		if (human instanceof Player) ((Player)p).updateInventory();
		//p.getHandle().activeContainer.checkReachable = false;

		return this.getBukkitView();
	}

	@Override
	public List<fr.arektor.menucreator.api.Slot> getSlots() {
		List<fr.arektor.menucreator.api.Slot> returned = new ArrayList<fr.arektor.menucreator.api.Slot>();
		this.slots.forEach((slot) -> { if (slot instanceof CustomSlot && !(slot instanceof CustomSlotPlayer)) returned.add((fr.arektor.menucreator.api.Slot)slot); });

		return returned;
	}

	@Override
	public List<fr.arektor.menucreator.api.Slot> getRawSlots() {
		List<fr.arektor.menucreator.api.Slot> returned = new ArrayList<fr.arektor.menucreator.api.Slot>();
		this.slots.forEach((slot) -> { if (slot instanceof CustomSlot) returned.add((fr.arektor.menucreator.api.Slot)slot); });

		return returned;
	}

	private static Containers<?> getGenericBySize(int size) {
		if (size <= 0) throw new IllegalArgumentException("Inventory size must be positive");
		if (size%9 != 0) throw new IllegalArgumentException("Inventory size must be a multiple of 9");
		int rowCount = size/9;
		if (rowCount <= 1) return Containers.GENERIC_9X1;
		else if (rowCount == 2) return Containers.GENERIC_9X2;
		else if (rowCount == 3) return Containers.GENERIC_9X3;
		else if (rowCount == 4) return Containers.GENERIC_9X4;
		else if (rowCount == 5) return Containers.GENERIC_9X5;
		else return Containers.GENERIC_9X6;
	}

	@Override
	public boolean canUse(EntityHuman entityhuman) {
		return true;
	}

	@Override
	public void b(EntityHuman entityhuman) {
		final Player p;
		if (entityhuman instanceof EntityPlayer) p = ((EntityPlayer)entityhuman).getBukkitEntity();
		else if (owner != null) p = owner;
		else p = null;
		
		PlayerInventory playerinventory = entityhuman.inventory;
		if(!playerinventory.getCarried().isEmpty()) {
			ItemStack carried = playerinventory.getCarried();
			playerinventory.setCarried(ItemStack.b);
			entityhuman.drop(carried, false);
		}
		if (this.closeAction != null) this.closeAction.run(entityhuman.getBukkitEntity());
		if (!MenuCreator.getPluginInstance().isEnabled()) return;
		Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> {
			if (p != null) p.updateInventory();
			Container c = entityhuman.activeContainer;
			if ((!(c instanceof CustomGui) || ((CustomGui)c).getParent() != this) && (!(c instanceof TextInput) || ((TextInput)c).getParent() != this)) {
				if (this.mirrorOf == null) {
					for (fr.arektor.menucreator.api.Slot slot : this.getSlots()) {
						if (slot.getAccessCondition().check(entityhuman.getBukkitEntity())) {
							entityhuman.drop(CraftItemStack.asNMSCopy(slot.get()), false);
							slot.set(null);
						}
					}
				} else this.mirrorOf.removeMirror(this);
			}
		}, 1L);
		if (this.parent != null && p != null) {
			Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> {
				if (!(entityhuman.activeContainer instanceof CustomGui) && !(entityhuman.activeContainer instanceof TextInput)) {
					this.parent.show(p);
					//this.owner.updateInventory();
				}
			}, 1L);
		}
	}

	@Override
	public void changeTitle(String newTitle) {
		this.ctitle = newTitle;
	}

	@Override
	public String getGUITitle() {
		return this.ctitle;
	}

	private final Object[] data = new Object[Config.getCustomInventoriesDataArraySize()];
	@Override
	public Object[] getData() {
		return this.data;
	}

	public CustomGui getParent() {
		return this.parent;
	}

	public void setParent(CustomGui parent) {
		this.parent = parent;
	}

	public void setDataChangeReaction(DataChangeReaction reaction) {
		this.dataChangeReaction = reaction;
	}
	
	public DataChangeReaction getDataChangeReaction() {
		return this.dataChangeReaction;
	}
	
	public List<CustomGui> getMirrors() {
		throw new IllegalStateException("A non-static gui cannot have mirrors.");
	}
	
	public CustomGui getMirrorOf() {
		return this.mirrorOf;
	}
	
	@Override
	public void setMirrorOf(CustomGui mirrorOf) {
		this.mirrorOf = mirrorOf;
	}

}
