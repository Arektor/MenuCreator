package fr.arektor.menucreator.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.arektor.common.utils.ItemStackBuilder;
import fr.arektor.menucreator.Config;
import fr.arektor.menucreator.api.CustomGui;
import fr.arektor.menucreator.api.InventorySize;
import fr.arektor.menucreator.api.MenuCreatorAPI;
import fr.arektor.menucreator.api.Slot;
import fr.arektor.menucreator.api.TextInput;

public class CMD_MenuCreator {

	public static boolean handle(CommandSender sender, String label, String[] args) {
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("config")) handleConfigSubcmd(sender, label, Arrays.copyOfRange(args, 1, args.length));
			if (args[0].equalsIgnoreCase("create")) handleCreateSubcmd(sender, label, Arrays.copyOfRange(args, 1, args.length));
		} else {
			sender.sendMessage(ChatColor.AQUA + "MenuCreator commands:");
			sender.sendMessage(ChatColor.DARK_BLUE + " - " + ChatColor.BLUE + " config <open/reload>");
			sender.sendMessage(ChatColor.DARK_BLUE + " - " + ChatColor.BLUE + " create <internal name>");
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
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This action cannot be performed by the console.");
			return;
		}
		
		if (args.length >= 1) {
			String name = args[0];
			//TODO add check if name is already used
			createMenu((Player)sender, name);
		} else {
			sender.sendMessage(ChatColor.RED + "/"+label+" create <internal name>");
		}
	}
	
	private static void createMenu(Player p, String name) {
		CustomGui main = MenuCreatorAPI.createCustomGui(p, ChatColor.BOLD+"Creating: "+ChatColor.RESET+name, InventorySize.HOPPER);
		main.lock();
		
		Slot nameSlot = main.getSlotAt(1, 1);
		nameSlot.set(new ItemStackBuilder()
			.withMaterial(Material.NAME_TAG)
			.withDisplayName("§6Display Name")
			.withLore(Arrays.asList("§7Set to: §fUnnamed"))
			.build());
		nameSlot.setClickAction((who,clickType) -> {
			TextInput input = MenuCreatorAPI.createTextInput(p, "Name your menu");
			input.setCompletion((human,text) -> main.setData(0, text.length() > 0 ? text : null));
			input.setParent(main);
		});
		
		Slot typeSlot = main.getSlotAt(2, 1);
		typeSlot.set(new ItemStackBuilder()
			.withMaterial(Material.CHEST)
			.withDisplayName("§6Inventory Type")
			.withLore(Arrays.asList("§7Set to: §fChest (9*3)"))
			.build());
		typeSlot.setClickAction((who,clickType) -> {
			CustomGui typeSelector = MenuCreatorAPI.createCustomGui(who, "Select an inventory type", 9);
			typeSelector.lock();
			
			Slot slot = typeSelector.getSlotAt(1, 1);
			slot.set(null);
			slot.setClickAction((who1,clickType1) -> {
				
			});
		});
		
		main.setDataChangeReaction((index, oldData) -> {
			if (index == 0) {
				Object newName = main.getData(0);
				if (newName == null) newName = "Unnamed";
				nameSlot.set(new ItemStackBuilder(nameSlot.get())
							.withLore(Arrays.asList("§7Set to: §f"+newName))
							.build());
			} else if (index == 1) {
				
			}
		});
	}
}
