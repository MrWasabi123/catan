package game.cards.victorypointcards;

import game.cards.DevelopmentCard;
import game.player.Player;

/**
 * Represents a victory point card.
 * @author Christoph Hermann
 */
public class VictoryPointCard extends DevelopmentCard {

	/**
	 * The amount of victory points {@code Player} gets for owning this card.
	 * @see Player
	 */
	private final int victoryPoints = 1;

	/**
	 * Returns the amount of victory points a {@code Player} gets for owning this card. 
	 * @return the victoryPoints.
	 * @see Player
	 */
	public int getVictoryPoints() {
		return victoryPoints;
	}

}
