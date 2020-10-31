package fr.arektor.menucreator.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class StaticSlot implements Slot {
	
	private ItemStack item;
	private HumanAction clickAction;
	private AccessCondition accessCondition;
	private ItemCondition itemCondition;
	private Reaction itemChangeReaction;
	private CustomGui gui;
	private int posX = 1, posY = 1;
	private List<Slot> mirrors = new ArrayList<Slot>();
	private Slot mirrorOf = null;
	
	public StaticSlot(CustomGui gui, int posX, int posY) {
		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
	}

	@Override
	public void set(ItemStack is) {
		this.item = is;
		this.getMirrors().forEach((slot) -> slot.set(is));
	}

	@Override
	public ItemStack get() {
		return this.item;
	}

	@Override
	public HumanAction getClickAction() {
		return this.clickAction;
	}

	@Override
	public void setClickAction(HumanAction action) {
		this.clickAction = action;
		this.getMirrors().forEach((slot) -> slot.setClickAction(action));
	}

	@Override
	public AccessCondition getAccessCondition() {
		return this.accessCondition;
	}

	@Override
	public void setAccessCondition(AccessCondition condition) {
		this.accessCondition = condition;
		this.getMirrors().forEach((slot) -> slot.setAccessCondition(condition));
	}

	@Override
	public ItemCondition getItemCondition() {
		return this.itemCondition;
	}

	@Override
	public void setItemCondition(ItemCondition condition) {
		this.itemCondition = condition;
		this.getMirrors().forEach((slot) -> slot.setItemCondition(condition));
	}

	@Override
	public Reaction getItemChangeReaction() {
		return this.itemChangeReaction;
	}

	@Override
	public void setItemChangeReaction(Reaction reaction) {
		this.itemChangeReaction = reaction;
		this.getMirrors().forEach((slot) -> slot.setItemChangeReaction(reaction));
	}

	@Override
	public int getX() {
		return this.posX;
	}

	@Override
	public int getY() {
		return this.posY;
	}

	@Override
	public CustomGui getGui() {
		return this.gui;
	}

	@Override
	public List<Slot> getMirrors() {
		return this.mirrors;
	}

	@Override
	public Slot getMirrorOf() {
		return this.mirrorOf;
	}
	
	@Override
	public void setMirrorOf(Slot mirrorOf) {
		this.mirrorOf = mirrorOf;
	}
}
