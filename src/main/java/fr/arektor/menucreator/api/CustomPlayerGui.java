package fr.arektor.menucreator.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public interface CustomPlayerGui extends CustomGui {

	default InventoryView show(HumanEntity human) { return null; }
	default InventoryView show() { return null; }
	
	default void changeTitle(String newTitle) {}
	default String getGUITitle() { return "container.crafting"; }

	default CustomGui getParent() { return null; }
	default void setParent(CustomGui parent) {}
	
	default int getWidth() { return getWidth(PlayerSlotType.INVENTORY); }
	default int getWidth(PlayerSlotType type) { return type.getWidth(); }
	
	default int getLength() { return getLength(PlayerSlotType.INVENTORY); }
	default int getLength(PlayerSlotType type) { return type.getLength(); }

	default List<? extends Slot> getSlots() { return getSlots(PlayerSlotType.INVENTORY); }
	default Slot getCustomSlot(int i) { return getCustomSlot(PlayerSlotType.INVENTORY, i); }
	default Slot getSlotAt(int x, int y) { return getSlotAt(PlayerSlotType.INVENTORY, x, y); }
	default Collection<? extends Slot> getSlotsAt(int x1, int y1, int x2, int y2) { return getSlotsAt(PlayerSlotType.INVENTORY, x1, y1, x2, y2); }
	
	default Slot getFirstEmptySlot() {
		for (Slot slot : getSlots()) {
			if (slot.isEmpty()) return slot;
		}
		return null;
	}

	public List<? extends Slot> getSlots(PlayerSlotType type);
	default Slot getCustomSlot(PlayerSlotType type, int i) {
		return getSlots(type).get(i);
	}
	default Slot getSlotAt(PlayerSlotType type, int x, int y) {
		if (x <= 0) x = 1;
		if (y <= 0) y = 1;
		x--; y--;
		return getCustomSlot(type, x + (y*type.getWidth()));
	}
	
	default Collection<? extends Slot> getSlotsAt(PlayerSlotType type, int x1, int y1, int x2, int y2) {
		List<Slot> returned = new ArrayList<>();
		if (x1 > type.getWidth()) x1 = type.getWidth();
		if (x2 > type.getWidth()) x2 = type.getWidth();
		int topLeftX = Math.min(x1, x2);
		int topLeftY = Math.min(y1, y2);
		int botRightX = Math.max(x1, x2);
		int botRightY = Math.max(y1, y2);

		for (int x = topLeftX; x <= botRightX; x++) {
			for (int y = topLeftY; y <= botRightY; y++) {
				returned.add(this.getSlotAt(type, x, y));
			}
		}
		
		return returned;
	}
	
	default Slot getFirstEmptySlot(PlayerSlotType type) {
		for (Slot slot : getSlots(type)) {
			if (slot.isEmpty()) return slot;
		}
		return null;
	}
	
	/**
	 * Locks all slots and prevents any further player modification.
	 * WARNING: This will override any previously set access and item conditions.
	 */
	default void lock() {
		getRawSlots().forEach((slot) -> slot.lock());
	}

	/**
	 * Unlocks all slots and allows new player modifications.
	 * WARNING: This will override any previously set access and item conditions.
	 */
	default void unlock() {
		getRawSlots().forEach((slot) -> slot.unlock());
	}
	
	/**
	 * Fills the whole inventory with copies of the specified item.
	 * Each slot will receive a clone of the provided item (resulting of {@link ItemStack#clone()})
	 * @param is The item to fill the inventory with
	 */
	default void fill(ItemStack is) {
		getRawSlots().forEach((slot) -> slot.set(is.clone()));
	}
	
	/**
	 * Clears the whole inventory, regardless of any slot lock state.
	 */
	default void clear() {
		getRawSlots().forEach((slot) -> slot.set(null));
	}
	
	/**
	 * Locks all slots and prevents any further player modification.
	 * WARNING: This will override any previously set access and item conditions.
	 */
	default void lock(PlayerSlotType type) {
		getSlots(type).forEach((slot) -> slot.lock());
	}

	/**
	 * Unlocks all slots and allows new player modifications.
	 * WARNING: This will override any previously set access and item conditions.
	 */
	default void unlock(PlayerSlotType type) {
		getSlots(type).forEach((slot) -> slot.unlock());
	}
	
	/**
	 * Fills the whole inventory with copies of the specified item.
	 * Each slot will receive a clone of the provided item (resulting of {@link ItemStack#clone()})
	 * @param is The item to fill the inventory with
	 */
	default void fill(PlayerSlotType type, ItemStack is) {
		getSlots(type).forEach((slot) -> slot.set(is.clone()));
	}
	
	/**
	 * Clears the whole inventory, regardless of any slot lock state.
	 */
	default void clear(PlayerSlotType type) {
		getSlots(type).forEach((slot) -> slot.set(null));
	}
	
	
	
	public static enum PlayerSlotType {
		ARMOR(1, 4),
		CRAFT(2, 2),
		HOTBAR(9, 1),
		OFFHAND(1, 1),
		CRAFT_RESULT(1, 1),
		INVENTORY(9, 3);
		
		private int width, length;
		private PlayerSlotType(int width, int length) {
			this.width = width;
			this.length = length;
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public int getLength() {
			return this.length;
		}
	}
}
