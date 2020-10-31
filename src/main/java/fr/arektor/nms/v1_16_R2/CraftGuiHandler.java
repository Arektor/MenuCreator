package fr.arektor.nms.v1_16_R2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.v1_16_R2.ContainerPlayer;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.PlayerInventory;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import fr.arektor.common.utils.Reflector;
import fr.arektor.menucreator.GuiHandler;
import fr.arektor.menucreator.MenuCreator;
import fr.arektor.menucreator.api.CustomGui;
import fr.arektor.menucreator.api.CustomPlayerGui;
import fr.arektor.menucreator.api.CustomPlayerGui.PlayerSlotType;
import fr.arektor.menucreator.api.Slot;
import fr.arektor.menucreator.api.TextInput;

public class CraftGuiHandler implements GuiHandler {

	private static final Map<UUID,PlayerInventoryBackup> defaultContainers = new HashMap<>();
	private static final Reflector ref = new Reflector(EntityHuman.class, null);

	@Override
	public void handle(InventoryClickEvent evt) {
		EntityHuman human = ((CraftHumanEntity)evt.getWhoClicked()).getHandle();
		boolean customGuiClicked = evt.getClickedInventory().equals(evt.getView().getTopInventory());
		boolean playerGuiClicked = false;
		if (!customGuiClicked && human.defaultContainer instanceof CustomPlayerGui) playerGuiClicked = true;
		
		
		if (human.activeContainer instanceof CustomPlayerGui) {
			if (evt.getWhoClicked() instanceof Player) Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> ((Player)evt.getWhoClicked()).updateInventory(), 1L);
			//System.out.println("Slot: "+evt.getSlot());
			//System.out.println("RawSlot: "+evt.getRawSlot());

			System.out.println("Get Raw Custom Slot At RawSlot "+(evt.getRawSlot()-3));
			Slot slot = ((CustomPlayerGui)human.activeContainer).getRawCustomSlot(evt.getRawSlot());
			if (slot != null && slot.getClickAction() != null && evt.getClick() != ClickType.DOUBLE_CLICK) slot.getClickAction().run(evt.getWhoClicked());
		} else if (human.activeContainer instanceof CustomGui) {
			if (evt.getWhoClicked() instanceof Player) Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> ((Player)evt.getWhoClicked()).updateInventory(), 1L);

			if (customGuiClicked || playerGuiClicked) {
				CustomGui gui = (CustomGui)human.activeContainer;
				Slot slot = gui.getRawCustomSlot(evt.getRawSlot());
				if (slot != null && slot.getClickAction() != null && evt.getClick() != ClickType.DOUBLE_CLICK) slot.getClickAction().run(evt.getWhoClicked());
			}
		} else if (human.activeContainer instanceof TextInput) {
			if (evt.getWhoClicked() instanceof Player) Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> ((Player)evt.getWhoClicked()).updateInventory(), 1L);
			if (customGuiClicked && evt.getSlot() == 2) {
				TextInput input = (TextInput)human.activeContainer;
				if (input.isInputValid()) {
					if (input.getCompletion() != null && evt.getClick() != ClickType.DOUBLE_CLICK) input.getCompletion().completion(input.getOwner(), input.getInput());
					human.closeInventory();
				}
			} else if (playerGuiClicked) {
				Slot slot = null;
				if (evt.getRawSlot()-3 >= 27) {
					slot = ((CustomPlayerGui)human.defaultContainer).getCustomSlot(PlayerSlotType.HOTBAR, evt.getRawSlot()-30);
				} else {
					slot = ((CustomPlayerGui)human.defaultContainer).getCustomSlot(PlayerSlotType.INVENTORY, evt.getRawSlot()-3);
				}
				if (slot != null && slot.getClickAction() != null && evt.getClick() != ClickType.DOUBLE_CLICK) slot.getClickAction().run(evt.getWhoClicked());
			}
		}
	}

	@Override
	public void handle(InventoryDragEvent evt) {
		EntityHuman human = ((CraftHumanEntity)evt.getWhoClicked()).getHandle();
		if (human.activeContainer instanceof CustomGui || human.activeContainer instanceof TextInput) {
			if (evt.getWhoClicked() instanceof Player) Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> ((Player)evt.getWhoClicked()).updateInventory(), 1L);
		}
	}

	@Override
	public void setPlayerGui(Player p, CustomPlayerGui gui) {
		setPlayerDefaultContainer(p, (ContainerPlayer_Custom)gui);
	}

	@Override
	public CustomPlayerGui getPlayerGui(Player p) {
		EntityPlayer ep = ((CraftPlayer)p).getHandle();
		if (ep.defaultContainer instanceof ContainerPlayer_Custom) return (ContainerPlayer_Custom)ep.defaultContainer;
		else return null;
	}

	@Override
	public void restorePlayerInventory(Player p) {
		if (MenuCreator.isUnsafeMode()) new IllegalStateException("You cannot invoke player inventory restoration while NMSAPI is in unsafe mode!").printStackTrace();
		else if (!defaultContainers.containsKey(p.getUniqueId())) return;
		else {
			//setPlayerDefaultContainer(p, defaultContainers.remove(p.getUniqueId()));
			//p.getInventory().clear();
			EntityPlayer ep = ((CraftPlayer)p).getHandle();
			ref.setReference(ep);
			PlayerInventoryBackup backup = defaultContainers.remove(p.getUniqueId());
			
			PlayerInventory inventory = backup.getInventory();
			ref.set("inventory", inventory);
			
			chRef.setReference((CraftHumanEntity)p);
			chRef.set("inventory", backup.getCraftInventory());
			
			if (ep.activeContainer == ep.defaultContainer) {
				ep.activeContainer = backup.getContainer();
			}
			ref.set("defaultContainer", backup.getContainer());
			//container.c();
			p.updateInventory();
		}
	}
	
	private static final Reflector chRef = new Reflector(CraftHumanEntity.class, null);
	private void setPlayerDefaultContainer(Player p, ContainerPlayer_Custom container) {
		//p.getInventory().clear();
		EntityPlayer ep = ((CraftPlayer)p).getHandle();
		ref.setReference(ep);
		if (!MenuCreator.isUnsafeMode() && !defaultContainers.containsKey(p.getUniqueId())) defaultContainers.put(p.getUniqueId(), new PlayerInventoryBackup(ep));
		
		PlayerInventory inventory = container.getOwnerInventory();
		ref.set("inventory", inventory);
		
		chRef.setReference((CraftHumanEntity)p);
		chRef.set("inventory", new CraftInventoryPlayer(inventory));
		
		if (ep.activeContainer == ep.defaultContainer) {
			ep.activeContainer = container;
		}
		ref.set("defaultContainer", container);
		//container.c();
		p.updateInventory();
	}



	private static class PlayerInventoryBackup {
		
		CraftInventoryPlayer craftInventory;
		PlayerInventory inventory;
		ContainerPlayer container;
		
		private PlayerInventoryBackup(EntityPlayer ep) {
			this.container = ep.defaultContainer;
			this.inventory = ep.inventory;
			
			chRef.setReference(ep.getBukkitEntity());
			this.craftInventory = (CraftInventoryPlayer) chRef.get("inventory");
		}

		private CraftInventoryPlayer getCraftInventory() { return this.craftInventory; }
		private PlayerInventory getInventory() { return this.inventory; }
		private ContainerPlayer getContainer() { return this.container; }
	}
}
