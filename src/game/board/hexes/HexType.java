package game.board.hexes;

import game.resources.ResourceType;
import game.resources.Resources;

/**
 * The different terrain types of the hexes.
 * @author Paula Wikidal
 * @author Christoph Hermann
 * @see Hex
 */
public enum HexType {

	/**
	 * Represents a type of {@code Hex} which functions as a Desert Hex.
	 * @see Hex
	 */
	DESERT(new Resources(), "Wueste"),

	/**
	 * Represents a type of {@code Hex} which functions as a Fields Hex.
	 * @see Hex
	 */
	FIELDS(new Resources(ResourceType.GRAIN, 1), "Ackerland"),

	/**
	 * Represents a type of {@code Hex} which functions as a Forest Hex.
	 * @see Hex
	 */
	FOREST(new Resources(ResourceType.LUMBER, 1), "Wald"),

	/**
	 * Represents a type of {@code Hex} which functions as a Hills Hex.
	 * @see Hex
	 */
	HILLS(new Resources(ResourceType.BRICK, 1), "Huegelland"),

	/**
	 * Represents a type of {@code Hex} which functions as a Mountain Hex.
	 * @see Hex
	 */
	MOUNTAIN(new Resources(ResourceType.ORE, 1), "Gebirge"),

	/**
	 * Represents a type of {@code Hex} which functions as a Pasture Hex.
	 * @see Hex
	 */
	PASTURE(new Resources(ResourceType.WOOL, 1), "Weideland"),

	/**
	 * Represents a type of {@code Hex} which functions as a Water Hex.
	 * @see Hex
	 */
	WATER(new Resources(), "Meer");

	/**
	 * The {@code Resources} a {@code Hex} of this type produces.
	 * @see Resources
	 * @see Hex
	 */
	private final Resources resources;

	/**
	 * The string representation of this type.
	 */
	private final String string;

	/**
	 * Creates a new HexType producing the given {@code Resources}.
	 * @param resources the {@link Resources}.
	 * @param string the string representation of this type.
	 */
	private HexType(Resources resources, String string) {
		this.resources = resources;
		this.string = string;
	}

	/**
	 * Returns the {@code Resources} a {@code Hex} of this type produces.
	 * @return the {@link Resources}.
	 * @see Hex
	 */
	public Resources getResources() {
		return resources;
	}

	@Override
	public String toString() {
		return string;
	}
	
	/**
	 * Converts the HexType to its associated ResourceType.
	 * @return the {@code ResourceType}
	 * @see ResourceType
	 */
	public ResourceType toResourceType(){
		switch(this){
		case DESERT: return null;
		case FIELDS: return ResourceType.GRAIN;
		case FOREST: return ResourceType.LUMBER;
		case HILLS: return ResourceType.BRICK;
		case MOUNTAIN: return ResourceType.ORE;
		case PASTURE: return ResourceType.WOOL;
		case WATER: return null;
		}
		return null;
	}

}
