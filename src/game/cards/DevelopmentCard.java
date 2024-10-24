package game.cards;

import game.resources.ResourceType;
import game.resources.Resources;

/**
 * Represents a development card.
 * @author Christoph Hermann
 */
public abstract class DevelopmentCard {

	/**
	 * The cost of a development Card.
	 */
	static final Resources cost = new Resources(
			ResourceType.ORE, 1,
			ResourceType.WOOL, 1,
			ResourceType.GRAIN, 1
			);

	/**
	 * Returns the cost of a development card.
	 * @return the cost.
	 */
	public static Resources getCost() {
		return cost;
	}

}
