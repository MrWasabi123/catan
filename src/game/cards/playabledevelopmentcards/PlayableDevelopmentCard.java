package game.cards.playabledevelopmentcards;

import game.cards.DevelopmentCard;

/**
 * Represents a development card which can be played.
 * @author Christoph Hermann
 */
public class PlayableDevelopmentCard extends DevelopmentCard {

	/**
	 * The number of Turn, when this card has been drawn
	 */
	private int drawnAtTurn = 0;

	/**
	 * Whether this card has been played or not.
	 */
	private boolean played = false;

	/**
	 * The type of this playable development card.
	 */
	private final PlayableDevelopmentCardType type;

	/**
	 * Creates a new playable development card of the specified type.
	 * @param type the {@link PlayableDevelopmentCardType PlayableDevelopmentCardType}.
	 */
	public PlayableDevelopmentCard(PlayableDevelopmentCardType type) {
		this.type = type;
	}

	/**
	 * Returns the type of this card.
	 * @return the {@link PlayableDevelopmentCardType PlayableDevelopmentCardType}.
	 */
	public PlayableDevelopmentCardType getType() {
		return type;
	}

	/**
	 * Returns if this card can be used in this turn or not.
	 * @param turnNumber the number of the current turn.
	 * @return true, if this card can be used in this turn
	 */
	public boolean canPlayAtThisTurn(int turnNumber) {
		return turnNumber!= getDrawnTurnNumber();
	}

	/**
	 * Set number of turn, when this card has been bought
	 * @param turnNumber the number of turn, when this card has been bought
	 */
	public void setDrawnTurnNumber(int turnNumber) {
		this.drawnAtTurn = turnNumber;
	}

	/**
	 * Get the number of turn, when this card has been drawn 
	 * @return drawnAtTurn the number of turn, when this card has been drawn
	 */
	public int getDrawnTurnNumber() {
		return this.drawnAtTurn;
	}

	/**
	 * Return true, if this card has been played
	 * @return the hasBeenPlayed
	 */
	public boolean hasBeenPlayed() {
		return played;
	}

	/**
	 * Set true, if this card has been played 
	 */
	public void setHasBeenPlayed() {
		this.played = true;
	}
}
