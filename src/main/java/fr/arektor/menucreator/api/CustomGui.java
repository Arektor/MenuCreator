package fr.arektor.menucreator.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public interface CustomGui {

	public void setCloseAction(HumanAction action);
	public HumanAction getCloseAction();
	
	public void setDataChangeReaction(DataChangeReaction reaction);
	public DataChangeReaction getDataChangeReaction();
	
	public void setDragReaction(DragReaction reaction);
	public DragReaction getDragReaction();

	/*
	public List<? extends Slot> getRawSlots();
	default Slot getRawCustomSlot(int i) {
		List<? extends Slot> slots = getRawSlots();
		if (i >= slots.size()) i = slots.size()-1;
		else if (i < 0) i = 0;
		
		return slots.get(i);
	}
	*/
	
	//public Slot createSlot();
	public List<? extends Slot> getSlots();
	public List<? extends Slot> getRawSlots();
	default Slot getCustomSlot(int i) {
		List<? extends Slot> slots = getSlots();
		if (i >= slots.size()) i = slots.size()-1;
		else if (i < 0) i = 0;
		
		return slots.get(i);
	}
	default Slot getRawCustomSlot(int i) {
		List<? extends Slot> slots = getRawSlots();
		if (i >= slots.size()) i = slots.size()-1;
		else if (i < 0) i = 0;
		
		return slots.get(i);
	}
	default Slot getSlotAt(int x, int y) {
		if (x > getWidth()) x = getWidth();
		if (x <= 0) x = 1;
		if (y <= 0) y = 1;
		x--; y--;
		return getCustomSlot(x + (y*getWidth()));
	}
	
	default Collection<? extends Slot> getSlotsAt(int x1, int y1, int x2, int y2) {
		if (x1 > getWidth()) x1 = getWidth();
		if (x2 > getWidth()) x2 = getWidth();
		List<Slot> returned = new ArrayList<>();
		int topLeftX = Math.min(x1, x2);
		int topLeftY = Math.min(y1, y2);
		int botRightX = Math.max(x1, x2);
		int botRightY = Math.max(y1, y2);

		for (int x = topLeftX; x <= botRightX; x++) {
			for (int y = topLeftY; y <= botRightY; y++) {
				returned.add(this.getSlotAt(x, y));
			}
		}
		
		return returned;
	}
	
	default Slot getFirstEmptySlot() {
		for (Slot slot : getSlots()) {
			if (slot.isEmpty()) return slot;
		}
		return null;
	}
	
	/**
	 * Locks all slots and prevents any further player modification.
	 * WARNING: This will override any previously set access and item conditions.
	 */
	default void lock() {
		getSlots().forEach((slot) -> slot.lock());
	}

	/**
	 * Unlocks all slots and allows new player modifications.
	 * WARNING: This will override any previously set access and item conditions.
	 */
	default void unlock() {
		getSlots().forEach((slot) -> slot.unlock());
	}

	/**
	 * Fills the whole inventory with copies of the specified item.
	 * Each slot will receive a clone of the provided item (resulting of {@link ItemStack#clone()})
	 * @param is The item to fill the inventory with
	 */
	default void fill(ItemStack is) {
		getSlots().forEach((slot) -> slot.set(is.clone()));
	}

	/**
	 * Works exactly like {@link #fill(ItemStack)} but for the specified part of the inventory.
	 * 
	 * @param x1 X Position of the first corner
	 * @param y1 Y Position of the first corner
	 * @param x2 X Position of the second corner
	 * @param y2 Y Position of the second corner
	 * @param is The item to fill the section with
	 */
	default List<Slot> fill(int x1, int y1, int x2, int y2, ItemStack is) {
		LinkedList<Slot> affectedSlots = new LinkedList<Slot>();
		if (x1 > getWidth()) x1 = getWidth();
		if (x2 > getWidth()) x2 = getWidth();
		int topLeftX = Math.min(x1, x2);
		int topLeftY = Math.min(y1, y2);
		int botRightX = Math.max(x1, x2);
		int botRightY = Math.max(y1, y2);

		for (int x = topLeftX; x <= botRightX; x++) {
			for (int y = topLeftY; y <= botRightY; y++) {
				Slot slot = this.getSlotAt(x, y);
				slot.set((is == null ? null : is.clone()));
				affectedSlots.add(slot);
			}
		}
		return affectedSlots;
	}
	
	default List<Slot> fillRow(int y, ItemStack is) {
		return fill(1, y, getWidth(), y, is);
	}
	
	default List<Slot> fillColumn(int x, ItemStack is) {
		return fill(x, 1, x, getLength(), is);
	}

	/**
	 * Clears the whole inventory, regardless of any slot lock state.
	 */
	default void clear() {
		getSlots().forEach((slot) -> {
			slot.set(null);
			slot.setClickAction(null);
		});
	}
	
	/**
	 * Clears the specified part of the inventory.
	 * Is the same as calling {@link #fill(x1, y1, x2, y2, null)}
	 * 
	 * @param x1 X Position of the first corner
	 * @param y1 Y Position of the first corner
	 * @param x2 X Position of the second corner
	 * @param y2 Y Position of the second corner
	 */
	default void clear(int x1, int y1, int x2, int y2) {
		fill(x1, y1, x2, y2, null).forEach((slot) -> slot.setClickAction(null));
	}

	public void changeTitle(String newTitle);
	public String getGUITitle();

	public InventoryView show(HumanEntity human);
	public InventoryView show();

	public CustomGui getParent();
	public void setParent(CustomGui parent);
	
	public Player getOwner();

	public int getWidth();
	public int getLength();
	default public int getSize() {
		return getSlots().size();
	}
	
	public CustomGui getMirrorOf();
	public void setMirrorOf(CustomGui mirrorOf);
	
	public List<CustomGui> getMirrors();
	default public void addMirror(CustomGui mirror) { 
		getMirrors().add(mirror);
		mirror.setCloseAction(this.getCloseAction());
		mirror.setDataChangeReaction(null);
		for (int i = 0; i < mirror.getData().length; i++) mirror.setData(i, this.getData(i));
		mirror.setDataChangeReaction(this.getDataChangeReaction());
		mirror.setParent(this.getParent());
		mirror.setMirrorOf(this);
	}
	default public void removeMirror(CustomGui mirror) { getMirrors().remove(mirror); }
	
	default public void addItem(ItemStack... items) {
		addItems(Arrays.asList(items));
	}
	
	default public void addItems(List<ItemStack> items) {
		List<? extends fr.arektor.menucreator.api.Slot> slots = this.getSlots();
		for (org.bukkit.inventory.ItemStack is : items) {
			for (fr.arektor.menucreator.api.Slot slot : slots) {
				if (slot.isEmpty()) {
					slot.set(is);
					break;
				}
			}
		}
	}
	
	public Object[] getData();
	default public Object getData(int index) {
		if (index >= getData().length) return null;
		else return getData()[index];
	}
	default public void setData(int index, Object data) {
		if (index >= getData().length) return;
		else {
			if (this.getDataChangeReaction() != null) {
				Object oldData = getData()[index];
				getData()[index] = data;
				this.getDataChangeReaction().run(index, oldData);
			} else getData()[index] = data;
		}
	}
	default public boolean hasData(int index) {
		return getData(index) != null;
	}
	
	public interface DataChangeReaction {
		void run(int index, Object oldData);
	}
	
	public interface DragReaction {
		void run(InventoryDragEvent event);
	}
	
	public interface HumanAction {
		void run(HumanEntity who);
	}
}
