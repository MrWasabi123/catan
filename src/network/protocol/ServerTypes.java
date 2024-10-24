package network.protocol;

import game.bank.Bank;
import game.board.construction.Construction;
import game.board.robber.Robber;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.dice.Dice;
import game.player.Player;
import game.resources.Resources;
import network.client.ClientConnection;
import network.server.Server;
import users.User;

/**
 * Enum of all protocol strings which can be received by the {@code Server}.
 * @author Christoph Hermann
 * @see Server
 */
public enum ServerTypes {

	/**
	 * The type of message a {@code Server} receives when a client connects.
	 * @see Server
	 * @see ClientConnection
	 */
	HELLO("Hallo"),

	/**
	 * The type of message a {@code Server} receives when a client wants to send a chat message.
	 * @see Server
	 * @see ClientConnection
	 */
	SEND_CHATMESSAGE("Chatnachricht senden"),

	/**
	 * The type of message a {@code Server} receives when a {@code User} changes their name or color.
	 * @see Server
	 * @see User
	 */
	PLAYER("Spieler"),

	/**
	 * The type of message a {@code Server} receives when a {@code User} is ready to start the game.
	 * @see Server
	 * @see User
	 */
	GAME_START("Spiel starten"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to roll the {@code Dice}.
	 * @see Server
	 * @see Player
	 * @see Dice
	 */
	ROLL_DICE("Wuerfeln"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to discard resource cards.
	 * @see Server
	 * @see Player
	 * @see Resources
	 */
	LOSE_RESOURCE("Karten abgeben"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to move the {@code Robber}.
	 * @see Server
	 * @see Player
	 * @see Robber
	 */
	MOVE_ROBBER("Raeuber versetzen"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to build a {@code Construction}.
	 * @see Server
	 * @see Player
	 * @see Construction
	 */
	BUILD("Bauen"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to buy a {@code DevelopmentCard}.
	 * @see Server
	 * @see Player
	 * @see DevelopmentCard
	 */
	BUY_DEVLOPMENTCARD("Entwicklungskarte kaufen"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to trade with the {@code Bank}.
	 * @see Server
	 * @see Player
	 * @see Bank
	 */
	SEA_TRADING("Seehandel"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to end their turn.
	 * @see Server
	 * @see Player
	 */
	END_TURN("Zug beenden"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to make a trading offer.
	 * @see Server
	 * @see Player
	 */
	OFFER_TRADE("Handel anbieten"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to accept a trading offer.
	 * @see Server
	 * @see Player
	 */
	ACCEPT_TRADE("Handel annehmen"),

	/**
	 * The type of message a {@code Server} receives when a {@code User} wants to finalize a trade.
	 * @see Server
	 * @see User
	 */
	FINISH_TRADE("Handel abschliessen"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to cancel a trade.
	 * @see Server
	 * @see Player
	 */
	CANCEL_TRADE("Handel abbrechen"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to play a knight card.
	 * @see Server
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	PLAY_KNIGHT("Ritter ausspielen"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to play a Road Building card.
	 * @see Server
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	PLAY_ROAD_BUILDING("Strassenbaukarte ausspielen"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to play a Monopoly card.
	 * @see Server
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	PLAY_MONOPOLY("Monopol"),

	/**
	 * The type of message a {@code Server} receives when a {@code Player} wants to play a Year Of Plenty card.
	 * @see Server
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	PLAY_YEAR_OF_PLENTY("Erfindung");

	/**
	 * The string representation of this message type.
	 */
	private final String string;

	/**
	 * Creates a new server type.
	 * @param string the string representation of this message type.
	 */
	private ServerTypes(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

}
