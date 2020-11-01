package fr.arektor.menucreator.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.arektor.menucreator.Config;

public class CMD_MenuCreator {

	public static boolean handle(CommandSender sender, String label, String[] args) {
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("config")) handleConfigSubcmd(sender, label, Arrays.copyOfRange(args, 1, args.length));
			if (args[0].equalsIgnoreCase("create")) handleCreateSubcmd(sender, label, Arrays.copyOfRange(args, 1, args.length));
		} else {
			sender.sendMessage(ChatColor.AQUA + "MenuCreator commands:");
			sender.sendMessage(ChatColor.DARK_BLUE + " - " + ChatColor.BLUE + " config <open/reload>");
			sender.sendMessage(ChatColor.DARK_BLUE + " - " + ChatColor.BLUE + " create [internal name]");
		}
		return true;
	}
	
	private static void handleConfigSubcmd(CommandSender sender, String label, String[] args) {
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				Config.reload();
				sender.sendMessage(ChatColor.GREEN+"MenuCreator's config successfully reloaded.");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "/"+label+" config <open/reload>");
		}
	}

	private static void handleCreateSubcmd(CommandSender sender, String label, String[] args) {
		
	}
}
