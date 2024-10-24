package network.protocol;

import game.board.construction.Construction;
import game.board.robber.Robber;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.cards.specialcards.SpecialCard;
import game.dice.Dice;
import game.player.Player;
import game.resources.Resources;
import game.trade.Trade;
import network.client.ClientConnection;
import network.server.Server;
import users.User;

/**
 * Enum of all protocol strings which can be received by the client.
 * @author Christoph Hermann
 * @see ClientConnection
 */
public enum ClientTypes {

	/**
	 * The type of message a client receives when connecting to a {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	HELLO("Hallo"),

	/**
	 * The type of message a client receives when its protocol version has been accepted by the {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	WELCOME("Willkommen"),

	/**
	 * The type of message a client receives after each request to the {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	SERVER_REPLY("Serverantwort"),

	/**
	 * The type of message a client receives when a chat message has been send to the {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	CHATMESSAGE("Chatnachricht"),

	/**
	 * The type of message a client receives when the selected color is not available.
	 * @see ClientConnection
	 */
	ERROR("Fehler"),

	/**
	 * The type of message a client receives when the game starts.
	 * @see ClientConnection
	 */
	GAME_STARTED("Spiel gestartet"),

	/**
	 * The type of message a client receives when the game ends.
	 * @see ClientConnection
	 */
	GAME_FINISHED("Spiel beendet"),

	/**
	 * The type of message a client receives when the state of a {@code User} or {@code Player} has been changed.
	 * @see ClientConnection
	 * @see User
	 * @see Player
	 */
	STATE_UPDATE("Statusupdate"),

	/**
	 * The type of message a client receives when the {@code Dice} have been rolled.
	 * @see ClientConnection
	 * @see Dice
	 */
	DICE("Wuerfelwurf"),

	/**
	 * The type of message a client receives when a {@code Player} gains some {@code Resources}.
	 * @see ClientConnection
	 * @see Player
	 * @see Resources
	 */
	RESOURCE_QUANTITY("Ertrag"),

	/**
	 * The type of message a client receives when a {@code Player} looses some {@code Resources}.
	 * @see ClientConnection
	 * @see Player
	 * @see Resources
	 */
	PRICE("Kosten"),

	/**
	 * The type of message a client receives when the {@code Robber} has been moved.
	 * @see ClientConnection
	 * @see Robber
	 */
	ROBBER_MOVED("Raeuber versetzt"),

	/**
	 * The type of message a client receives when a {@code Construction} has been built.
	 * @see ClientConnection
	 * @see Construction
	 */
	BUILDING("Bauvorgang"),

	/**
	 * The type of message a client receives when a {@code DevelopmentCard} has been bought.
	 * @see ClientConnection
	 * @see DevelopmentCard
	 */
	DEVELOMENTCARD_BOUGHT("Entwicklungskarte gekauft"),

	/**
	 * The type of message a client receives when the owner of the longest road changes.
	 * @see ClientConnection
	 * @see SpecialCard
	 */
	LONGEST_ROAD("Laengste Handelsstrasse"),

	/**
	 * The type of message a client receives when the owner of the largest army changes.
	 * @see ClientConnection
	 * @see SpecialCard
	 */
	LARGEST_ARMY("Groesste Rittermacht"),

	/**
	 * The type of message a client receives when a {@code Player} makes a trading offer.
	 * @see ClientConnection
	 * @see Player
	 * @see Trade
	 */
	TRADE_OFFER("Handelsangebot"),

	/**
	 * The type of message a client receives when a {@code Player} accepts a trading offer.
	 * @see ClientConnection
	 * @see Player
	 * @see Trade
	 */
	TRADE_ACCEPTED("Handelsangebot angenommen"),

	/**
	 * The type of message a client receives when a {@code Player} finishes a trade.
	 * @see ClientConnection
	 * @see Player
	 * @see Trade
	 */
	TRADE_EXECUTED("Handel ausgefuehrt"),

	/**
	 * The type of message a client receives when a {@code Player} cancels a trading offer.
	 * @see ClientConnection
	 * @see Player
	 * @see Trade
	 */
	TRADE_CANCELED("Handelsangebot abgebrochen"),

	/**
	 * The type of message a client receives when a {@code Player} wants to play a Year Of Plenty card.
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	PLAY_YEAR_OF_PLENTY("Erfindung"),

	/**
	 * The type of message a client receives when a {@code Player} wants to play a knight card.
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	PLAY_KNIGHT("Ritter ausspielen"),

	/**
	 * The type of message a client receives when a {@code Player} wants to play a Road Building card.
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	PLAY_ROAD_BUILDING("Strassenbaukarte ausspielen"),

	/**
	 * The type of message a client receives when a {@code Player} wants to play a Monopoly card.
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	PLAY_MONOPOLY("Monopol");

	/**
	 * The string representation of this type.
	 */
	private final String string;

	/**
	 * Creates a new client type.
	 * @param string the string representation of this protocol information.
	 */
	private ClientTypes(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

}
