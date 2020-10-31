package fr.arektor.menucreator;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import fr.arektor.menucreator.api.CustomPlayerGui;

public interface GuiHandler {

	void handle(InventoryClickEvent evt);
	void handle(InventoryDragEvent evt);
	
	void setPlayerGui(Player p, CustomPlayerGui gui);
	CustomPlayerGui getPlayerGui(Player p);
	void restorePlayerInventory(Player p);
	
}
