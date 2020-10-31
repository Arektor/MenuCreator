package fr.arektor.menucreator.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.arektor.menucreator.Config;

public class CMD_MenuCreator {

	public static boolean handle(CommandSender sender, String label, String[] args) {
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("config")) handleConfigSubcmd(sender, Arrays.copyOfRange(args, 1, args.length));
		} else {
			//TODO print help?
		}
		return true;
	}
	
	private static void handleConfigSubcmd(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				Config.reload();
				sender.sendMessage(ChatColor.GREEN+"MenuCreator's config successfully reloaded.");
			}
		} else {
			//TODO print help
		}
	}
}
