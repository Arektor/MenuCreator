package fr.arektor.nms.v1_16_R2;

import java.util.List;

import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.IInventory;
import net.minecraft.server.v1_16_R2.ItemStack;
import net.minecraft.server.v1_16_R2.Slot;

import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;

import fr.arektor.menucreator.api.CustomGui;
import fr.arektor.menucreator.api.StaticSlot;

public class CustomSlot extends Slot implements fr.arektor.menucreator.api.Slot {
	
	//private boolean internal = true;

	private CustomGui gui = null;
	private AccessCondition playerCondition = (human) -> {return true;};
	private ItemCondition itemCondition = (item) -> {return true;};
	private ClickAction action = ClickAction.NOTHING;
	private ItemChangeReaction itemChangeReaction = ItemChangeReaction.NOTHING;
	private int x,y;
	private fr.arektor.menucreator.api.Slot mirrorOf;
	
	public CustomSlot(CustomGui gui, IInventory var1, int var2, int var3, int var4, int posX, int posY) {
		super(var1, var2, var3, var4);
		this.gui = gui;
		this.x = posX+1;
		this.y = posY+1;
	}
	
	public CustomSlot(IInventory var1, int var2, int var3, int var4, int posX, int posY, StaticSlot reference) {
		super(var1, var2, var3, var4);
		this.x = posX+1;
		this.y = posY+1;
		reference.addMirror(this);
	}

	public boolean isAllowed(ItemStack itemstack) {
		//System.out.println("isAllowed(ItemStack: "+itemstack.getItem().getName()+")");
		if (this.itemCondition != null) return this.itemCondition.check(CraftItemStack.asCraftMirror(itemstack));
		else return true;
	}

	public boolean isAllowed(EntityHuman entityhuman) {
		//System.out.println("isAllowed(EntityHuman)");
		if (this.playerCondition != null) return this.playerCondition.check(((EntityPlayer)entityhuman).getBukkitEntity());
		else return true;
	}

	public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
		//System.out.println("a(EntityHuman, ItemStack: "+itemstack.getItem().getName()+")");
		return itemstack;
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public AccessCondition getAccessCondition() {
		return this.playerCondition;
	}

	@Override
	public void setAccessCondition(AccessCondition condition) {
		if (condition == null) condition = (human) -> {return true;};
		this.playerCondition = condition;
	}

	@Override
	public ItemCondition getItemCondition() {
		return this.itemCondition;
	}

	@Override
	public void setItemCondition(ItemCondition condition) {
		if (condition == null) condition = (item) -> {return true;};
		this.itemCondition = condition;
	}

	@Override
	public ItemChangeReaction getItemChangeReaction() {
		return this.itemChangeReaction;
	}

	@Override
	public void setItemChangeReaction(ItemChangeReaction reaction) {
		if (reaction == null) reaction = ItemChangeReaction.NOTHING;
		this.itemChangeReaction = reaction;
	}

	@Override
	public void set(org.bukkit.inventory.ItemStack is) {
		ItemStack oldItem = super.getItem();
		
		if (is == null) super.set(ItemStack.b);
		else super.set(CraftItemStack.asNMSCopy(is));
		
		if (this.itemChangeReaction != null) this.itemChangeReaction.run(CraftItemStack.asBukkitCopy(oldItem));
	}

	@Override
	public org.bukkit.inventory.ItemStack get() {
		ItemStack item = getItem();
		if (item == null) return null;
		else return CraftItemStack.asCraftMirror(getItem());
	}
	
	@Override
	public void set(ItemStack is) {
		ItemStack oldItem = super.getItem();
		
		super.set(is);
		if (this.mirrorOf == null) {
			if (this.itemChangeReaction != null) this.itemChangeReaction.run(CraftItemStack.asBukkitCopy(oldItem));
		} else {
			this.mirrorOf.set(CraftItemStack.asCraftMirror(is));
		}
	}

	@Override
	public ClickAction getClickAction() {
		return this.action;
	}

	@Override
	public void setClickAction(ClickAction action) {
		if (action == null) action = ClickAction.NOTHING;
		this.action = action;
	}
	
	@Override
	public CustomGui getGui() {
		return this.gui;
	}
	
	public CustomSlot copyDataOf(CustomSlot cloneTarget) {
		if (cloneTarget != null) {
			//System.out.println("clone target is "+cloneTarget.getX() + " " + cloneTarget.getY());
			/*
			if (cloneTarget.getClickAction() != null) {
				System.out.println("clone target click action is not null");
			} else {
				System.out.println("clone target click action is null");
			}
			*/
			
			/*
			this.setAccessCondition(cloneTarget.getAccessCondition());
			this.setClickAction(cloneTarget.getClickAction());
			this.setItemChangeReaction(cloneTarget.getItemChangeReaction());
			this.setItemCondition(cloneTarget.getItemCondition());
			*/

			this.setAccessCondition((human) -> {
				return cloneTarget.getAccessCondition().check(human);
			});
			this.setClickAction((who,clickType) -> {
				cloneTarget.getClickAction().run(who,clickType);
			});
			this.setItemChangeReaction((oldItem) -> {
				cloneTarget.getItemChangeReaction().run(oldItem);
			});
			this.setItemCondition((item) -> {
				return cloneTarget.getItemCondition().check(item);
			});
		}
		return this;
	}
	
	public List<fr.arektor.menucreator.api.Slot> getMirrors() {
		throw new IllegalStateException("A non-static slot cannot have mirror slots.");
	}

	@Override
	public fr.arektor.menucreator.api.Slot getMirrorOf() {
		return this.mirrorOf;
	}
	
	@Override
	public void setMirrorOf(fr.arektor.menucreator.api.Slot mirrorOf) {
		this.mirrorOf = mirrorOf;
	}
}
