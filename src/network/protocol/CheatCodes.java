package network.protocol;

import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.player.Player;
import game.resources.Resources;

/**
 * Represents all cheat codes which can be used in the game.
 * @author Christoph Hermann
 */
public enum CheatCodes {

	/**
	 * The cheat code which puts the {@code Player} in the {@code PlayerState} MOVE_ROBBER.
	 * @see Player
	 */
	ROBBER("robber"),

	/**
	 * The cheat code which adds {@code Resources} to the {@code Player}.
	 * @see Resources
	 * @see Player
	 */
	RESOURCES("money"),

	/**
	 * The cheat code which adds {@code PlayableDevelopmentCards} to the {@code Player}.
	 * @see PlayableDevelopmentCard
	 * @see Player
	 */
	DEVELOPMENT_CARDS("cards"),

	/**
	 * The cheat code which adds {@code PlayableDevelopmentCards} to all {@code Players}.
	 * @see PlayableDevelopmentCard
	 * @see Player
	 */
	DEVELOPMENT_CARDS_FOR_ALL("allcards"),

	/**
	 * The cheat code which makes the {@code Player} win the game.
	 * @see Player
	 */
	WIN("win"),

	/**
	 * The cheat code which makes the {@code Player} lose the game.
	 * @see Player
	 */
	LOSE("lose");

	/**
	 * The string representation of this cheat code.
	 */
	private final String string;

	/**
	 * Creates a new cheat code.
	 * @param string the string representation of this cheat code.
	 */
	private CheatCodes(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

}
