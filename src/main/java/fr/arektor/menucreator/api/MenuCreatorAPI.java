package fr.arektor.menucreator.api;

import org.bukkit.entity.Player;

import fr.arektor.common.utils.Version;

public class MenuCreatorAPI {

	public static TextInput createTextInput(Player owner) { return createTextInput(owner, ""); }
	public static TextInput createTextInput(Player owner, String title) { return createTextInput(owner, title, null); }
	public static TextInput createTextInput(Player owner, String title, String defaultInput) {
		switch(Version.getVersion()) {
		case V1_16_R2:
			return new fr.arektor.nms.v1_16_R2.ContainerAnvil_TextInput(owner, title, defaultInput);
		default:
			return null;
		}
	}

	public static CustomGui createCustomGui(Player owner) { return createCustomGui(owner, ""); }
	public static CustomGui createCustomGui(Player owner, String title) { return createCustomGui(owner, title, InventorySize.CHEST); }
	public static CustomGui createCustomGui(Player owner, InventorySize size) { return createCustomGui(owner, "", size); }
	public static CustomGui createCustomGui(Player owner, String title, InventorySize size) {
		if (!size.isSpecial()) return createCustomGui(owner, title, size.getSize());
		else {
			switch(Version.getVersion()) {
			case V1_16_R2:
				if (size == InventorySize.DISPENSER) return new fr.arektor.nms.v1_16_R2.ContainerDispenser_Custom(owner, title);
				else if (size == InventorySize.HOPPER) return new fr.arektor.nms.v1_16_R2.ContainerHopper_Custom(owner, title);
				else if (size == InventorySize.PLAYER) return new fr.arektor.nms.v1_16_R2.ContainerPlayer_Custom(owner);
			default:
				return null;
			}
		}
	}
	public static CustomGui createCustomGui(Player owner, String title, int size) {
		switch(Version.getVersion()) {
		case V1_16_R2:
			return new fr.arektor.nms.v1_16_R2.ContainerChest_Custom(owner, title, size);
		default:
			return null;
		}
	}
	


	public static CustomGui createStaticGui() { return createStaticGui("Default Title"); }
	public static CustomGui createStaticGui(String title) { return createStaticGui(title, InventorySize.CHEST); }
	public static CustomGui createStaticGui(InventorySize size) { return createStaticGui("Default Title", size); }
	public static CustomGui createStaticGui(String title, InventorySize size) {
		if (!size.isSpecial()) return createStaticGui(title, size.getSize());
		else {
			switch(Version.getVersion()) {
			case V1_16_R2:
				if (size == InventorySize.DISPENSER) return new fr.arektor.nms.v1_16_R2.TileEntityCustomDispenser(title);
				else if (size == InventorySize.HOPPER) return new fr.arektor.nms.v1_16_R2.TileEntityCustomHopper(title);
				else if (size == InventorySize.PLAYER) throw new IllegalArgumentException("Cannot create static player inventory.");
			default:
				return null;
			}
		}
	}
	public static CustomGui createStaticGui(String title, int size) {
		switch(Version.getVersion()) {
		case V1_16_R2:
			return new fr.arektor.nms.v1_16_R2.TileEntityCustomChest(title, size/9);
		default:
			return null;
		}
	}

	public static CustomPlayerGui createCustomPlayerGui(Player owner) {
		switch(Version.getVersion()) {
		case V1_16_R2:
			return new fr.arektor.nms.v1_16_R2.ContainerPlayer_Custom(owner);
		default:
			return null;
		}
	}
	
}
