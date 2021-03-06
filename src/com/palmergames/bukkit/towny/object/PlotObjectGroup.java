package com.palmergames.bukkit.towny.object;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Suneet Tipirneni (Siris)
 * A simple class which encapsulates the grouping of townblocks.
 */
public class PlotObjectGroup extends ObjectGroup {
	private Resident resident = null;
	private List<TownBlock> townBlocks;
	private double price = -1;
	private Town town;
	private TownyPermission permissions;

	/**
	 * @param id   A unique identifier for the group id.
	 * @param name An alias for the id used for player in-game interaction via commands.
	 * @param town The town that this group is owned by.   
	 */
	public PlotObjectGroup(UUID id, String name, Town town) {
		super(id, name);
		this.town = town;
	}

	public static PlotObjectGroup fromString(String str) {
		
		//TODO: Figure out if the unused variables are needed - LlmDl.
		
		// Get the fields
		String[] fields = str.split(",");
		String name = fields[0];
		UUID id = UUID.fromString(fields[1]);
		String townName = fields[2];
		double price = Double.parseDouble(fields[3]);
		
		TownyUniverse townyUniverse = TownyUniverse.getInstance();
		
		// Fetch the global town reference.
		Town town = townyUniverse.getTownsMap().get(townName);
		
		// Fetch the global plot group reference.
		PlotObjectGroup newGroup = townyUniverse.getGroup(townName, id);
		newGroup.setPrice(price);
		
		return newGroup;
	}

	/**
	 * Store plot group in format "name,id,town,price"
	 * @return The string in the format described.
	 */
	@Override
	public String toString() {
		return super.toString() + "," + getTown().toString() + "," + getPrice();
	}

	/**
	 * Override the name change method to internally rehash the plot group map.
	 * @param name The name of the group.
	 */
	@Override
	public void setGroupName(String name) {
		if (getGroupName() == null) {
			super.setGroupName(name);
		}
		else {
			String oldName = getGroupName();
			super.setGroupName(name);
			town.renamePlotGroup(oldName, this);
		}
	}
	
	public void setTown(Town town) {
		this.town = town;
		
		try {
			town.addPlotGroup(this);
		} catch (Exception e) {
			TownyMessaging.sendErrorMsg(e.getMessage());
		}
	}
	
	public Town getTown() {
		return town;
	}

	/**
	 *
	 * @return The qualified resident mode string.
	 */
	public String toModeString() {
		return "Group{" + this.toString() + "}";
	}

	/**
	 * @param modeStr The string in the resident mode format.
	 * @return The plot group given from the mode string.
	 */
	public static PlotObjectGroup fromModeString(String modeStr) {
		String objString = StringUtils.substringBetween(modeStr, "{", "}");
		return PlotObjectGroup.fromString(objString);
	}

	public double getPrice() {
		return price;
	}
	
	public void setResident(Resident resident) {
		if (hasResident())
			this.resident = resident;
	}

	public Resident getResident() throws NotRegisteredException {
		if (!hasResident())
			throw new NotRegisteredException("The Group " + this.toString() + "is not registered to a resident.");
		return resident;
	}

	public boolean hasResident() { return resident != null; }
	
	public void addTownBlock(TownBlock townBlock) {
		if (townBlocks == null)
			townBlocks = new ArrayList<>();
		
		townBlocks.add(townBlock);
	}

	public void removeTownBlock(TownBlock townBlock) {
		if (townBlocks != null)
			townBlocks.remove(townBlock);
	}
	
	public List<TownBlock> getTownBlocks() {
		return townBlocks;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public void addPlotPrice(double pPrice) {
		if (getPrice() == -1) {
			this.price = pPrice;
			return;
		}
		
		this.price += pPrice;
	}

	public TownyPermission getPermissions() {
		return permissions;
	}

	public void setPermissions(TownyPermission permissions) {
		this.permissions = permissions;
	}
	
}
