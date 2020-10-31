package fr.arektor.nms.v1_16_R2;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.server.v1_16_R2.ChatComponentText;
import net.minecraft.server.v1_16_R2.Container;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.PlayerInventory;
import net.minecraft.server.v1_16_R2.TileEntityHopper;

import org.bukkit.craftbukkit.v1_16_R2.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import fr.arektor.menucreator.Config;
import fr.arektor.menucreator.api.CustomGui;
import fr.arektor.menucreator.api.Slot;
import fr.arektor.menucreator.api.Slot.HumanAction;
import fr.arektor.menucreator.api.StaticSlot;

public class TileEntityCustomHopper extends TileEntityHopper implements CustomGui {

	private HumanAction closeAction;
	private DataChangeReaction dataChangeReaction;
	private DragReaction dragReaction;
	private List<StaticSlot> slots = new ArrayList<StaticSlot>();
	private List<CustomGui> mirrors = new ArrayList<CustomGui>();
	private CustomGui parent,mirrorOf;
	private String title;

	protected TileEntityCustomHopper() {
		super();
		for (int i = 0; i < 5; i++) {
			this.slots.add(new StaticSlot(this, i, 1));
		}
	}
	
	public TileEntityCustomHopper(String title) {
		this();
		this.changeTitle(title);
	}

	@Nullable
	public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
		return this.createContainer(i, playerinventory, entityhuman.getBukkitEntity());
	}
	
	protected Container createContainer(int i, PlayerInventory playerinventory, HumanEntity owner) {
		CustomGui gui = new ContainerHopper_Custom((Player) owner, i, playerinventory, this);
		this.addMirror(gui);
		return (Container)gui;
	}
	
	@Override
	public Player getOwner() { return null; }

	@Override
	public void setCloseAction(HumanAction action) {
		this.closeAction = action;
	}
	
	@Override
	public HumanAction getCloseAction() {
		return this.closeAction;
	}

	@Override
	public void setDataChangeReaction(DataChangeReaction reaction) {
		this.dataChangeReaction = reaction;
	}

	@Override
	public DataChangeReaction getDataChangeReaction() {
		return this.dataChangeReaction;
	}

	@Override
	public void setDragReaction(DragReaction reaction) {
		this.dragReaction = reaction;
	}

	@Override
	public DragReaction getDragReaction() {
		return this.dragReaction;
	}

	@Override
	public List<? extends Slot> getSlots() {
		return this.slots;
	}

	@Override
	public List<? extends Slot> getRawSlots() {
		return this.getSlots();
	}

	@Override
	public void changeTitle(String newTitle) {
		this.setCustomName(new ChatComponentText(newTitle));
		this.title = newTitle;
	}

	@Override
	public String getGUITitle() {
		return this.title;
	}

	@Override
	public InventoryView show(HumanEntity human) {
		if (human == null) return null;
		CraftHumanEntity p = (CraftHumanEntity)human;
		p.getHandle().openContainer(this);
		if (human instanceof Player) ((Player)p).updateInventory();

		return p.getOpenInventory();
	}

	@Override
	public InventoryView show() {
		throw new IllegalStateException("Cannot invoke CustomGui#show() on a static gui - use CustomGui#show(HumanEntity) instead");
	}

	@Override
	public CustomGui getParent() {
		return this.parent;
	}

	@Override
	public void setParent(CustomGui parent) {
		this.parent = parent;
	}

	@Override
	public int getWidth() {
		return 5;
	}
	
	@Override
	public int getLength() {
		return 1;
	}

	private final Object[] data = new Object[Config.getCustomInventoriesDataArraySize()];
	@Override
	public Object[] getData() {
		return this.data;
	}

	@Override
	public List<CustomGui> getMirrors() {
		return this.mirrors;
	}
	
	@Override
	public CustomGui getMirrorOf() {
		return this.mirrorOf;
	}
	
	@Override
	public void setMirrorOf(CustomGui mirrorOf) {
		this.mirrorOf = mirrorOf;
	}
}
