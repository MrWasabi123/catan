package game.board.construction.localities;

import game.board.corners.Corner;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;

/**
 * Represents a Settlement.
 * @author Yize Sun
 * @author Christoph Hermann
 */
public class Settlement extends Locality {
	
	/**
	 * If the settlement was builded in second free building round.
	 */
	private boolean isSecondRoundBuilded = false;

	/**
	 * The amount of victory points a {@code Player} gets for owning a settlement.
	 * @see Player
	 */
	private static final int VICTORY_POINTS = 1;

	/**
	 * The multiplier used to determine the amount of {@code Resources} a settlement produces.
	 * @see Resources
	 */
	private static final int RESOURCE_MULTIPLIER = 1;

	/**
	 * The cost of building a settlement.
	 */
	private static final Resources cost = new Resources(
			ResourceType.BRICK, 1,
			ResourceType.LUMBER, 1,
			ResourceType.WOOL, 1,
			ResourceType.GRAIN, 1);

	/**
	 * Creates a new settlement belonging to the specified {@code Player}.
	 * @param player the {@link Player} owning this settlement.
	 * @param corner the {@link Corner} this settlement is positioned at.
	 */
	public Settlement(Player player, Corner corner) {
		super(player, corner, VICTORY_POINTS, RESOURCE_MULTIPLIER);
	}

	/**
	 * Sets this settlements position to null.
	 */
	public void removeFromBoard() {
		positionProperty.set(null);
	}
	
	/**
	 * Gets cost to build a settlement
	 * @return {@link Resources}
	 */
    public static Resources getCost() {
    	return cost;
    }
    
    /**
     * Get multiplier of this settlement
     * @return RESOURCE_MULTIPLIER
     */
    public static int getMultiplier() {
    	return RESOURCE_MULTIPLIER;
    }

	/**
	 * Getter, if this settlement was builded in second free building round.
	 * @return true, if the settlement was builded in secound free building round.
	 */
	public boolean getisSecondRoundBuilded() {
		return  isSecondRoundBuilded ;
	}

	/**
	 * Set true, when this settlement was builded in second free building round.
	 * @param isSecondRound the buildRecord to set
	 */
	public void setisSecondRoundBuilded(boolean isSecondRound) {
		this. isSecondRoundBuilded  =  isSecondRound;
	}
}
