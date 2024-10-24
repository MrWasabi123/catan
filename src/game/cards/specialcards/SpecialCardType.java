package game.cards.specialcards;

import game.player.Player;

/**
 * Represents the different types of {@code SpecialCards}.
 * @author Christoph Hermann
 * @see SpecialCard
 */
public enum SpecialCardType {

	/**
	 * Represents a type of development card which functions as a Longest Road card.
	 */
	LONGEST_ROAD(2),

	/**
	 * Represents a type of development card which functions as a Largest Army card.
	 */
	LARGEST_ARMY(2);

	/**
	 * The amount of victory points a {@code Player} gets for a card of this type.
	 * @see Player
	 */
	private final int victoryPoints;

	/**
	 * Creates a new SpecialCardType with the given amount of victory points.
	 * @param victoryPoints the amount of victory points.
	 */
	private SpecialCardType(int victoryPoints) {
		this.victoryPoints = victoryPoints;
	}

	/**
	 * Returns the amount of victory points a {@code Player} gets for a card of this type. 
	 * @return the victoryPoints.
	 * @see Player
	 */
	public int getVictoryPoints() {
		return victoryPoints;
	}

}
