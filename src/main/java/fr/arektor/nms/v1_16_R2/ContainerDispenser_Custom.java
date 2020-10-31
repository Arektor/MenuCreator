package fr.arektor.nms.v1_16_R2;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_16_R2.ChatComponentText;
import net.minecraft.server.v1_16_R2.Container;
import net.minecraft.server.v1_16_R2.ContainerDispenser;
import net.minecraft.server.v1_16_R2.ContainerPlayer;
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

public class ContainerDispenser_Custom extends ContainerDispenser implements CustomGui {

	private Reflector reflector;
	private HumanAction closeAction;
	private DataChangeReaction dataChangeReaction;
	private DragReaction dragReaction;
	private String ctitle;
	private Player owner;
	private CustomGui parent = null, mirrorOf = null;

	public ContainerDispenser_Custom(Player owner) {
		this(owner, "Default Title");
	}

	public ContainerDispenser_Custom(Player owner, String title) {
		this((owner == null ? null : (((CraftPlayer)owner).getHandle())), title);
		this.owner = owner;
	}

	public ContainerDispenser_Custom(EntityPlayer owner) {
		this(owner, "Default Title");
	}

	public ContainerDispenser_Custom(EntityPlayer owner, String title) {
		this((owner == null ? MenuCreator.nextContainerCounter() : owner.nextContainerCounter()), owner.inventory, new InventorySubcontainer(9), null);
		this.ctitle = title;
	}
	
	public ContainerDispenser_Custom(Player owner, int i, PlayerInventory playerinventory, CustomGui gui) {
		this(i, playerinventory, new InventorySubcontainer(9), gui);
		this.owner = owner;
	}

	public ContainerDispenser_Custom(int i, PlayerInventory playerinventory, IInventory iinventory, CustomGui gui) {
		super(i, playerinventory, iinventory);

		this.reflector = new Reflector(Container.class, this);
		this.items.clear();
		this.slots.clear();

		
		if (gui != null) {
			for(int j = 0; j < 3; ++j) {
				for(int k = 0; k < 3; ++k) {
					this.a(new CustomSlot(iinventory, k + j * 3, 62 + k * 18, 17 + j * 18, k, j, (StaticSlot)gui.getCustomSlot(k+3*j)));
				}
			}
		} else {
			for(int j = 0; j < 3; ++j) {
				for(int k = 0; k < 3; ++k) {
					this.a(new CustomSlot(this, iinventory, k + j * 3, 62 + k * 18, 17 + j * 18, k, j));
				}
			}
		}

		ContainerPlayer dContainer = playerinventory.player.defaultContainer;
		if (dContainer instanceof ContainerPlayer_Custom) {
			ContainerPlayer_Custom container = (ContainerPlayer_Custom)dContainer;
			for(int j = 0; j < 3; ++j) {
				for(int k = 0; k < 9; ++k) {
					CustomSlot cloneTarget = container.getSlots(PlayerSlotType.INVENTORY).get(k+(9*j));
					this.a(new CustomSlotPlayer(this, playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18, k, j, PlayerSlotType.INVENTORY).copyDataOf(cloneTarget));
				}
			}
	
			for(int j = 0; j < 9; ++j) {
				CustomSlot cloneTarget = container.getSlots(PlayerSlotType.HOTBAR).get(j);
				this.a(new CustomSlotPlayer(this, playerinventory, j, 8 + j * 18, 142, j, 0, PlayerSlotType.HOTBAR).copyDataOf(cloneTarget));
			}
		} else {
			for(int j = 0; j < 3; ++j) {
				for(int k = 0; k < 9; ++k) {
					this.a(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
				}
			}
	
			for(int j = 0; j < 9; ++j) {
				this.a(new Slot(playerinventory, j, 8 + j * 18, 142));
			}
		}

	}
	
	@Override
	public int getWidth() {
		return 3;
	}
	
	@Override
	public int getLength() {
		return 3;
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
					return ContainerDispenser_Custom.this;
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

	@Override
	public boolean canUse(EntityHuman entityhuman) {
		return true;
	}
	
	@Override
	public fr.arektor.menucreator.api.Slot getSlotAt(int x, int y) {
		if (x <= 0) x = 1;
		if (y <= 0) y = 1;
		x--; y--;
		return getCustomSlot(x + (y*3));
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

	public void setDragReaction(DragReaction reaction) {
		this.dragReaction = reaction;
	}
	
	public DragReaction getDragReaction() {
		return this.dragReaction;
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
