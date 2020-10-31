package fr.arektor.menucreator.api;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import fr.arektor.menucreator.api.Slot.HumanAction;

public interface TextInput {
	
	public void setTextCondition(InputCondition cond);
	public void setCompletion(InputCompletion completion);
	public void setValidInputIcon(Material mat, List<String> lore);
	public void setInvalidInputIcon(Material mat, List<String> lore);
	public void setCloseAction(HumanAction action);
	
	public String getDefaultInput();
	public void setDefaultInput(String input);
	
	public String getInput();
	public boolean isInputValid();
	public InputCompletion getCompletion();
	
	public Player getOwner();
	public void setOwner(Player p);
	
	public CustomGui getParent();
	public void setParent(CustomGui parent);

	public interface InputCondition {
		boolean check(String s);
	}
	
	public interface InputCompletion {
		void completion(Player p, String input);
	}
	
	public InventoryView show();
}
