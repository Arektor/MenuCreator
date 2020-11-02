package fr.arektor.nms.v1_16_R2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_16_R2.Container;
import net.minecraft.server.v1_16_R2.ContainerPlayer;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.EnumItemSlot;
import net.minecraft.server.v1_16_R2.IInventory;
import net.minecraft.server.v1_16_R2.InventoryCraftResult;
import net.minecraft.server.v1_16_R2.InventoryCrafting;
import net.minecraft.server.v1_16_R2.ItemStack;
import net.minecraft.server.v1_16_R2.PlayerInventory;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import fr.arektor.common.utils.Reflector;
import fr.arektor.menucreator.Config;
import fr.arektor.menucreator.MenuCreator;
import fr.arektor.menucreator.api.CustomGui;
import fr.arektor.menucreator.api.CustomPlayerGui;
import fr.arektor.menucreator.api.TextInput;

public class ContainerPlayer_Custom extends ContainerPlayer implements CustomPlayerGui {

	private static final EnumItemSlot[] e = new EnumItemSlot[]{EnumItemSlot.HEAD, EnumItemSlot.CHEST, EnumItemSlot.LEGS, EnumItemSlot.FEET};
	private Reflector ref;
	private HumanAction closeAction;
	private DataChangeReaction dataChangeReaction;
	private DragReaction dragReaction;
	private Player owner;
	private Map<PlayerSlotType,List<CustomSlotPlayer>> customSlots = new HashMap<>();
	private PlayerInventory ownerInventory;

	public ContainerPlayer_Custom(Player owner) {
		this(((CraftPlayer)owner).getHandle());
		this.owner = owner;
	}

	public ContainerPlayer_Custom(EntityPlayer owner) {
		this(new PlayerInventory(owner), false, owner);
	}

	public ContainerPlayer_Custom(PlayerInventory playerinventory, boolean flag, EntityHuman entityhuman) {
		super(playerinventory, flag, entityhuman);

		this.ownerInventory = playerinventory;
		this.ref = new Reflector(ContainerPlayer.class, this);
		this.items.clear();
		this.slots.clear();
		
		for (PlayerSlotType slotType : PlayerSlotType.values()) {
			this.customSlots.put(slotType, new LinkedList<CustomSlotPlayer>());
		}

		int i;
		int j;

		// Crafting Grid
		//this.a(new SlotResult(playerinventory.player, this.getCraftInventory(), this.getResultInventory(), 0, 154, 28));
		this.registerSlot(new CustomSlotPlayer(this, playerinventory, 0, 154, 28, 0, 0, PlayerSlotType.CRAFT_RESULT));

		for(i = 0; i < 2; ++i) {
			for(j = 0; j < 2; ++j) {
				this.registerSlot(new CustomSlotPlayer(this, this.getCraftInventory(), j + i * 2, 98 + j * 18, 18 + i * 18, j, i, PlayerSlotType.CRAFT));
			}
		}

		// Armor Slots
		for(i = 0; i < 4; ++i) {
			EnumItemSlot enumitemslot = e[i];
			this.registerSlot(new CustomSlotArmor(this, playerinventory, 39 - i, 8, 8 + i * 18, 0, i, enumitemslot));
		}

		// Inventory
		for(i = 0; i < 3; ++i) {
			for(j = 0; j < 9; ++j) {
				this.registerSlot(new CustomSlotPlayer(this, playerinventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18, j, i, PlayerSlotType.INVENTORY));
			}
		}

		// Hotbar
		for(i = 0; i < 9; ++i) {
			this.registerSlot(new CustomSlotPlayer(this, playerinventory, i, 8 + i * 18, 142, i, 0, PlayerSlotType.HOTBAR));
		}

		// Offhand most likely
		this.registerSlot(new CustomSlotPlayer(this, playerinventory, 40, 77, 62, 0, 0, PlayerSlotType.OFFHAND));

	}

	private CustomSlotPlayer registerSlot(CustomSlotPlayer slot) {
		this.a(slot);
		customSlots.get(slot.getType()).add(slot);
		return slot;
	}

	@Override
	public Player getOwner() {
		return this.owner;
	}

	@Override
	public void setCloseAction(HumanAction action) {
		this.closeAction = action;
	}
	
	@Override
	public HumanAction getCloseAction() {
		return this.closeAction;
	}

	@Override
	public void c() {
		super.c();
		//Bukkit.getScheduler().runTaskLater(NMSAPI.instance, () -> owner.updateInventory(), 1L);
	}

	@Override
	public List<CustomSlotPlayer> getRawSlots() {
		List<CustomSlotPlayer> returned = new ArrayList<>();
		this.slots.forEach((slot) -> { if (slot instanceof CustomSlotPlayer) returned.add((CustomSlotPlayer)slot); });

		return returned;
	}

	@Override
	public boolean canUse(EntityHuman entityhuman) {
		return true;
	}

	private InventoryCrafting getCraftInventory() {
		return (InventoryCrafting) ref.get("craftInventory");
	}

	private InventoryCraftResult getResultInventory() {
		return (InventoryCraftResult) ref.get("resultInventory");
	}

	@Override
	public void b(EntityHuman entityhuman) {
		PlayerInventory playerinventory = entityhuman.inventory;
		if(!playerinventory.getCarried().isEmpty()) {
			ItemStack carried = playerinventory.getCarried();
			playerinventory.setCarried(ItemStack.b);
			entityhuman.drop(carried, false);
		}

		this.getResultInventory().clear();
		if (!entityhuman.world.isClientSide) {
			this.a(entityhuman, entityhuman.world, this.getCraftInventory());
		}

		if (this.closeAction != null && entityhuman instanceof EntityPlayer) this.closeAction.run(((EntityPlayer)entityhuman).getBukkitEntity());
		if (!MenuCreator.getPluginInstance().isEnabled()) return;
		Bukkit.getScheduler().runTaskLater(MenuCreator.getPluginInstance(), () -> {
			this.owner.updateInventory();
			Container c = ((CraftPlayer)this.owner).getHandle().activeContainer;
			if ((!(c instanceof CustomGui) || ((CustomGui)c).getParent() != this) && (!(c instanceof TextInput) || ((TextInput)c).getParent() != this)) {
				for (fr.arektor.menucreator.api.Slot slot : this.getSlots()) {
					if (entityhuman instanceof EntityPlayer && slot.getAccessCondition().check(((EntityPlayer)entityhuman).getBukkitEntity())) {
						entityhuman.drop(CraftItemStack.asNMSCopy(slot.get()), false);
						slot.set(null);
					}
				}
			}
		}, 1L);
		/*
		if (this.parent != null && entityhuman.getUniqueID().equals(this.owner.getUniqueId())) {
			Bukkit.getScheduler().runTaskLater(NMSAPI.instance, () -> {
				if (!(((CraftPlayer)this.owner).getHandle().activeContainer instanceof CustomGui)) {
					this.parent.show(this.owner);
				}
			}, 1L);
		}
		 */
	}

	private final Object[] data = new Object[Config.getCustomInventoriesDataArraySize()];
	@Override
	public Object[] getData() {
		return this.data;
	}

	@Override
	public List<CustomSlotPlayer> getSlots(PlayerSlotType type) {
		return this.customSlots.get(type);
	}
	
	public PlayerInventory getOwnerInventory() {
		return this.ownerInventory;
	}

	public void setDataChangeReaction(DataChangeReaction reaction) {
		this.dataChangeReaction = reaction;
	}
	
	public DataChangeReaction getDataChangeReaction() {
		return this.dataChangeReaction;
	}

	public void setDragReaction(DragReaction reaction) {
		this.dragReaction = reaction;
	}
	
	public DragReaction getDragReaction() {
		return this.dragReaction;
	}
	
	public List<CustomGui> getMirrors() {
		throw new IllegalStateException("A player inventory cannot have mirrors.");
	}
	
	public CustomGui getMirrorOf() {
		return null;
	}
	
	public void setMirrorOf(CustomGui mirrorOf) {
		return;
	}

	public static class CustomSlotPlayer extends CustomSlot {

		private PlayerSlotType type;

		public CustomSlotPlayer(CustomGui gui, IInventory var1, int var2, int var3, int var4, int posX, int posY, PlayerSlotType type) {
			super(gui, var1, var2, var3, var4, posX, posY);
			this.type = type;
		}

		public PlayerSlotType getType() {
			return this.type;
		}
	}

	public static class CustomSlotArmor extends CustomSlotPlayer {

		private EnumItemSlot slot;

		public CustomSlotArmor(CustomGui gui, IInventory var1, int var2, int var3, int var4, int posX, int posY, EnumItemSlot slot) {
			super(gui, var1, var2, var3, var4, posX, posY, PlayerSlotType.ARMOR);
			this.slot = slot;
		}

		public EnumItemSlot getArmorType() {
			return this.slot;
		}
	}
}
