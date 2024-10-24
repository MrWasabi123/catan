package game.cards.specialcards;

import game.player.Player;

/**
 * Represents special cards that are rewarded to {@code Players} for special feats.
 * @author Christoph Hermann
 * @see Player
 */
public class SpecialCard {

	/**
	 * The {@code Player} in possession of this card.
	 */
	private Player owner;

	/**
	 * The type of this special card.
	 * @see SpecialCardType
	 */
	private final SpecialCardType type;

	/**
	 * Creates a new victory point card of the specified type.
	 * @param type the {@link SpecialCardType SpecialCardType}.
	 */
	public SpecialCard(SpecialCardType type) {
		this.type = type;
	}

	/**
	 * Gives this card to the specified player. If the {@link Player Player} does not exist, the owner will be reset.
	 * @param player the {@code Player} receiving this card.
	 */
	public void giveTo(Player player) {
		if (owner != null) {
			owner.removeSpecialCard(this);
		}

		if (player != null) {
			player.addSpecialCard(this);
			owner = player;
		}
	}

	/**
	 * Resets the ownership of this card by removing it from the player currently owning it.
	 */
	public void resetOwnership() {
		if (owner != null) {
			owner.removeSpecialCard(this);
		}

		owner = null;
	}

	/**
	 * Returns the player owning this special card.
	 * @return the owner.
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Returns the type of this special card.
	 * @return the type.
	 */
	public SpecialCardType getType() {
		return type;
	}

	/**
	 * Returns the amount of victory points a {@code Player} gets for owning this card. 
	 * @return the victoryPoints.
	 * @see Player
	 */
	public int getVictoryPoints() {
		return type.getVictoryPoints();
	}

}
