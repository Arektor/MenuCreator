package fr.arektor.menucreator.api;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface Slot {

	AccessCondition lockedPlayerAccess = (human) -> { return false; };
	AccessCondition unlockedPlayerAccess = (human) -> { return true; };
	ItemCondition lockedItemAccess = (is) -> { return false; };
	ItemCondition unlockedItemAccess = (is) -> { return true; };
	
	public void set(ItemStack is);
	public ItemStack get();
	
	public List<Slot> getMirrors();
	default public void addMirror(Slot mirror) { 
		getMirrors().add(mirror);
		mirror.set(this.get());
		mirror.setAccessCondition(this.getAccessCondition());
		mirror.setClickAction(this.getClickAction());
		mirror.setItemChangeReaction(this.getItemChangeReaction());
		mirror.setItemCondition(this.getItemCondition());
		mirror.setMirrorOf(this);
	}
	default public void removeMirror(Slot mirror) { getMirrors().remove(mirror); }
	
	public Slot getMirrorOf();
	public void setMirrorOf(Slot slot);
	
	public ClickAction getClickAction();
	public void setClickAction(ClickAction action);

	public AccessCondition getAccessCondition();
	public void setAccessCondition(AccessCondition condition);

	public ItemCondition getItemCondition();
	public void setItemCondition(ItemCondition condition);
	
	public Reaction getItemChangeReaction();
	public void setItemChangeReaction(Reaction reaction);
	
	public int getX();
	public int getY();
	
	public CustomGui getGui();
	default public Slot getRelative(Side side) {
		return this.getGui().getSlotAt(this.getX()+side.getOffsetX(), this.getY()+side.getOffsetY());
	}
	
	/**
	 * Locks the slot and prevents any further player modification.
	 * WARNING: This will override any previously set access and item conditions.
	 */
	default void lock() {
		this.setAccessCondition(lockedPlayerAccess);
		this.setItemCondition(lockedItemAccess);
	}

	/**
	 * Unlocks the slot and allows new player modifications.
	 * WARNING: This will override any previously set access and item conditions.
	 */
	default void unlock() {
		this.setAccessCondition(unlockedPlayerAccess);
		this.setItemCondition(unlockedItemAccess);
	}
	
	
	public interface AccessCondition {
		boolean check(HumanEntity human);
	}
	
	public interface ItemCondition {
		boolean check(ItemStack is);
	}
	
	public interface ClickAction {
		public static final ClickAction NOTHING = (who,clickType) -> {};
		void run(HumanEntity who, ClickType clickType);
	}
	
	public interface Reaction {
		void run();
	}
	
	default boolean isEmpty() {
		ItemStack is = this.get();
		return is == null || is.getType() == Material.AIR;
	}
	
	public static enum Side {
		UP(0,1),
		DOWN(0,-1),
		LEFT(-1,0),
		RIGHT(1,0);
		
		private int xOffset,yOffset;
		
		private Side(int x, int y) {
			this.xOffset = x;
			this.yOffset = y;
		}

		public int getOffsetX() { return this.xOffset; }
		public int getOffsetY() { return this.yOffset; }
	}
}
