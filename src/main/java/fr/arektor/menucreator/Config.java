package fr.arektor.menucreator;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

	private static FileConfiguration config;
	
	private static boolean unsafeMode = false;
	private static int dataArraySize = 0;
	

	protected static void load() {
		if (MenuCreator.getPluginInstance() == null) return;
		config = MenuCreator.getPluginInstance().getConfig();

		unsafeMode = config.getBoolean("unsafe-mode");
		dataArraySize = config.getInt("data-array-size");
	}
	
	public static void reload() {
		MenuCreator.getPluginInstance().reloadConfig();
		load();
	}
	
	protected static void save() {
		MenuCreator.getPluginInstance().saveConfig();
	}
	
	public static boolean isUnsafeMode() {
		return unsafeMode;
	}
	
	public static int getCustomInventoriesDataArraySize() {
		return dataArraySize;
	}
}
