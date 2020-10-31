package fr.arektor.menucreator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.arektor.common.utils.Version;
import fr.arektor.menucreator.commands.CMD_MenuCreator;

public class MenuCreator extends JavaPlugin implements Listener {
	
	protected static MenuCreator instance;
	private static GuiHandler guiHandler = null;

	private static int containerCounter = 100;
	
	@Override
	public void onLoad() {
		Version.checkVersion();
		switch(Version.getVersion()) {
		case V1_16_R2:
			guiHandler = new fr.arektor.nms.v1_16_R2.CraftGuiHandler();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		instance = this;
		
		if (Version.getVersion() == Version.UNSUPPORTED) {
			getLogger().severe(ChatColor.DARK_RED+"========= WARNING =========");
			getLogger().severe(ChatColor.RED+"Unsupported Minecraft Version: "+ChatColor.GOLD+Version.getDetectedVersion());
			getLogger().severe(ChatColor.RED+"Your version of this plugin currently supports: ");
			for (Version v : Version.values()) {
				if (v != Version.UNSUPPORTED) getLogger().severe(ChatColor.GREEN+"   - "+v.getIdentifier());
			}
			getLogger().severe(ChatColor.DARK_RED+"===========================");
			this.setEnabled(false);
			pm.disablePlugin(this);
			return;
		}
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("menucreator")) return CMD_MenuCreator.handle(sender, label, args);
		return false;
	}
	
	@EventHandler
	public void click(InventoryClickEvent evt) {
		if (evt.getClickedInventory() != null) guiHandler.handle(evt);
	}

	@EventHandler
	public void drag(InventoryDragEvent evt) {
		guiHandler.handle(evt);
	}

	protected static GuiHandler getGUIHandler() {
		return guiHandler;
	}
	
	public static MenuCreator getPluginInstance() {
		return instance;
	}

	public static boolean isUnsafeMode() {
		return Config.isUnsafeMode();
	}
	
	/**
	 * Notchian-like container counter + 100 to avoid conflict with vanilla window IDs.
	 * Invoking this method too many times in a short timespan might yield unexpected results.
	 * 
	 * @return The next window ID used by the API.
	 */
	public static int nextContainerCounter() {
		containerCounter = Math.min(100, containerCounter % 200 + 1);
		return containerCounter;
	}
}
