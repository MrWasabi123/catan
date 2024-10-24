package game.player;

import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.robber.Robber;
import game.dice.Dice;
import game.resources.Resources;
import network.server.Server;

/**
 * Represents the different states a {@code Player} can be in.
 * @author Christoph Hermann
 * @see Player
 */
public enum PlayerState {

	/**
	 * The state which requires the {@code Player} to choose a name and a color and start the game.
	 * @see Player
	 */
	START_GAME("Spiel starten", "START_GAME"),

	/**
	 * The state which requires the {@code Player} to wait until the game starts.
	 * @see Player
	 */
	WAIT_FOR_GAME_START("Wartet auf Spielbeginn", "WAIT_FOR_GAME_START"),

	/**
	 * The state which requires the {@code Player} to build a free {@code Settlement}.
	 * @see Player
	 * @see Settlement
	 */
	BUILD_FREE_SETTLEMENT("Dorf bauen", "BUILD_SETTLEMENT"),

	/**
	 * The state which requires the {@code Player} to build a free {@code Road}.
	 * @see Player
	 * @see Road
	 */
	BUILD_FREE_ROAD("Strasse bauen", "BUILD_ROAD"),

	/**
	 * The state which requires the {@code Player} to roll the {@code Dice}.
	 * @see Player
	 * @see Dice
	 */
	ROLL_DICE("Wuerfeln", "ROLL_DICE"),

	/**
	 * The state which requires the {@code Player} to discard a resource card.
	 * @see Player
	 * @see Resources
	 */
	DISCARD_RESOURCES("Karten wegen Raeuber abgeben", "DISCARD_RESOURCES"),

	/**
	 * The state which requires the {@code Player} to move a {@code Robber}.
	 * @see Player
	 * @see Robber
	 */
	MOVE_ROBBER("Raeuber versetzen", "MOVE_ROBBER"),

	/**
	 * The state which requires the {@code Player} to trade or build something.
	 * @see Player
	 */
	TRADE_OR_BUILD("Handeln oder Bauen", "TRADE_OR_BUILD"),

	/**
	 * The state which requires the {@code Player} to wait.
	 * @see Player
	 */
	WAIT("Warten", "WAIT"),

	/**
	 * The state which indicates that the {@code Player} has lost their connection to the {@code Server}.
	 * @see Player
	 * @see Server
	 */
	CONNECTION_LOST("Verbindung verloren", "CONNECTION_LOST");

	/**
	 * The German string representation of this type.
	 */
	private final String string;

	/**
	 * The English string representation of this type.
	 */
	private final String displayText;

	/**
	 * Creates a new server type.
	 * @param string the German string representation of this protocol information.
	 * @param displayText the English string representation of this protocol information.
	 */
	private PlayerState(String string, String displayText) {
		this.string = string;
		this.displayText = displayText;
	}

	@Override
	public String toString() {
		return string;
	}

	/**
	 * Returns the string representation of this type for the languages bundle.
	 * @return the display text.
	 */
	public String getDisplayText() {
		return displayText;
	}

}
