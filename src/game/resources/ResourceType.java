package game.resources;

import game.board.harbors.HarborType;

/**
 * Represents the different types of resources in the game.
 * @author Christoph Hermann
 */
public enum ResourceType {

	/**
	 * Represents the wool resource.
	 */
	WOOL("Wolle"),

	/**
	 * Represents the brick resource.
	 */
	BRICK("Lehm"),

	/**
	 * Represents the lumber resource.
	 */
	LUMBER("Holz"),

	/**
	 * Represents the grain resource.
	 */
	GRAIN("Getreide"),

	/**
	 * Represents the ore resource.
	 */
	ORE("Erz");

	/**
	 * The string representation of this type.
	 */
	private final String string;

	@Override
	public String toString() {
		return string;
	}

	/**
	 * Creates a new ResourceType.
	 * @param string the string representation of this type.
	 */
	private ResourceType(String string) {
		this.string = string;
	}

	/**
	 * Returns the {@code HarborType} associated with this resource type.
	 * @return the {@link HarborType}.
	 */
	public HarborType toHarborType() {
		switch(this){
		case BRICK: return HarborType.BRICK;
		case GRAIN: return HarborType.GRAIN;
		case LUMBER: return HarborType.LUMBER;
		case ORE: return HarborType.ORE;
		case WOOL: return HarborType.WOOL;
		}
		return null;
	}

	/**
	 * Returns an integer representation of this resource type.
	 * @return an integer between 0 and 4. Returns -1 if the type is not {@link #BRICK}, {@link #GRAIN},
	 * {@link #LUMBER}, {@link #ORE} or {@link #WOOL}.
	 */
	public int convertToInt() {
		switch(this){
		case BRICK: return 3;
		case GRAIN: return 2;
		case LUMBER: return 0;
		case ORE: return 4;
		case WOOL: return 1;
		}
		return -1;
	}

}
