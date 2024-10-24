package game.board.construction.localities;

import game.board.corners.Corner;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;

/**
 * Represents a City.
 * @author Yize Sun
 * @author Christoph Hermann
 */
public class City extends Locality {

	/**
	 * The amount of victory points a {@code Player} gets for owning a city.
	 * @see Player
	 */
	private static final int VICTORY_POINTS = 2;

	/**
	 * The multiplier used to determine the amount of {@code Resources} a city produces.
	 * @see Resources
	 */
	private static final int RESOURCE_MULTIPLIER = 2;

	/**
	 * The cost of building a city.
	 */
	public static final Resources cost = new Resources(ResourceType.ORE, 3, ResourceType.GRAIN, 2);

	/**
	 * Creates a new city at a specific {@code Corner} belonging to the specified {@code Player}.
	 * @param player the {@link Player} owning this city.
	 * @param corner the {@link Corner} this city is positioned at.
	 */
	public City(Player player, Corner corner) {
		super(player, corner, VICTORY_POINTS, RESOURCE_MULTIPLIER);
	}
	
	/**
	 * Get cost to build a city
	 * @return {@link Resources}
	 */
    public static Resources getCost() {
    	return cost;
    }
    
    /**
     * Get multiplier of city
     * @return RESOURCE_MULTIPLIER
     */
    public static int getMultiplier() {
    	return RESOURCE_MULTIPLIER;
    }
}
