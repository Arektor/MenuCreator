package fr.arektor.menucreator.api;

public enum InventorySize {

	CHEST(27, false),
	DOUBLE_CHEST(54, false),
	HOPPER(5, true),
	DISPENSER(9, true),
	PLAYER(46, true);
	
	private boolean special;
	private int size;
	
	private InventorySize(int size, boolean special) {
		this.size = size;
		this.special = special;
	}
	
	public int getSize() { return this.size; }
	public boolean isSpecial() { return this.special; }
}
