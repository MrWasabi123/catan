package network.protocol;

import game.board.Board;
import game.board.construction.Construction;
import game.board.construction.localities.City;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.harbors.Harbor;
import game.board.hexes.Hex;
import game.board.robber.Robber;
import game.board.tokens.Token;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.cards.victorypointcards.VictoryPointCard;
import game.dice.Dice;
import game.player.Player;
import game.player.PlayerState;
import game.resources.ResourceType;
import game.resources.Resources;
import game.trade.Trade;
import network.server.ServerConnection;
import users.AIUser;
import users.User;

/**
 * Enum of all attributes used in the protocol.
 * @author Wanja Sajko
 * @author Christoph Hermann
 */
public enum Attributes {

	/**
	 * The protocol attribute representing the protocol version.
	 */
	VERSION("Version"),

	/**
	 * The protocol attribute representing the protocol version value.
	 */
	VERSION_VALUE("JavaFXClient 1.0 (SchickeYaks)"),

	/**
	 * The protocol attribute representing the protocol version value of an AI.
	 * @see AIUser
	 */
	VERSION_VALUE_KI("JavaFXClient 1.0 (SchickeYaks) (KI)"),

	/**
	 * The protocol attribute representing the protocol information.
	 */
	PROTOCOL("Protokoll"),

	/**
	 * The protocol attribute representing the protocol information value.
	 */
	PROTOCOL_VALUE("1.0"),

	/**
	 * The protocol attribute representing the text of a message send, when an action was successfully performed by the
	 * server.
	 * @see ServerConnection
	 */
	OK("OK"),

	/**
	 * The protocol attribute representing the text of a chat message.
	 */
	MESSAGE("Nachricht"),

	/**
	 * The protocol attribute representing the id of the {@code Player} who sent a chat message.
	 * @see Player
	 */
	SENDER("Absender"),

	/**
	 * The protocol attribute representing {@code Resources}.
	 * @see Resources
	 */
	RESOURCES("Rohstoffe"),

	/**
	 * The protocol attribute representing a {@code ResourceType}.
	 * @see ResourceType
	 */
	RESOURCE("Rohstoff"),

	/**
	 * The protocol attribute representing the {@code Board}.
	 * @see Board
	 */
	BOARD("Karte"),

	/**
	 * The protocol attribute representing the text of a message received, when the protocol version is not supported.
	 */
	PROTOCOL_ERROR_VALUE("Protokollversion nicht unterstuezt"),

	/**
	 * The protocol attribute representing the type of a game object.
	 */
	TYPE("Typ"),

	/**
	 * The protocol attribute representing the position of a game object.
	 */
	POSITION("Ort"),

	/**
	 * The protocol attribute representing the id of the {@code Player} owning a {@code Construction}.
	 * @see Player
	 * @see Construction
	 */
	OWNER("Eigentuemer"),

	/**
	 * The protocol attribute representing the position of the {@code Robber}.
	 * @see Robber
	 */
	ROBBER("Raeuber"),

	/**
	 * The protocol attribute representing a group of {@code Hexes}.
	 * @see Hex
	 */
	HEXES("Felder"),

	/**
	 * The protocol attribute representing one or more {@code Constructions}.
	 * @see Construction
	 */
	CONSTRUCTIONS("Gebaeude"),

	/**
	 * The protocol attribute representing several {@code Harbors}.
	 * @see Harbor
	 */
	HARBORS("Haefen"),

	/**
	 * The protocol attribute representing the value of the {@code Token} on a {@code Hex}.
	 * @see Token
	 * @see Hex
	 */
	NUMBER("Zahl"),

	/**
	 * The protocol attribute representing the {@code ResourceType} lumber.
	 * @see ResourceType
	 */
	LUMBER("Holz"),

	/**
	 * The protocol attribute representing the {@code ResourceType} brick.
	 * @see ResourceType
	 */
	BRICK("Lehm"),

	/**
	 * The protocol attribute representing the {@code ResourceType} wool.
	 * @see ResourceType
	 */
	WOOL("Wolle"),

	/**
	 * The protocol attribute representing the {@code ResourceType} grain.
	 * @see ResourceType
	 */
	GRAIN("Getreide"),

	/**
	 * The protocol attribute representing the {@code ResourceType} ore.
	 * @see ResourceType
	 */
	ORE("Erz"),

	/**
	 * The protocol attribute representing an amount of {@code Resources} without knowing the {@code ResourceTypes}.
	 * @see Resources
	 * @see ResourceType 
	 */
	UNKNOWN("Unbekannt"),

	/**
	 * The protocol attribute representing an id.
	 */
	ID("id"),

	/**
	 * The protocol attribute representing a color.
	 */
	COLOR("Farbe"),

	/**
	 * The protocol attribute representing a {@code Player} or {@code User} name.
	 * @see Player
	 * @see User
	 */
	NAME("Name"),

	/**
	 * The protocol attribute representing a {@code PlayerState}.
	 * @see PlayerState
	 */
	STATE("Status"),

	/**
	 * The protocol attribute representing victory points.
	 */
	VICTORY_POINTS("Siegpunkte"),

	/**
	 * The protocol attribute representing all knight cards played by a {@code Player}.
	 * @see Player
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	ARMY("Rittermacht"),

	/**
	 * The protocol attribute representing whether {@code Player} has the largest army.
	 * @see Player
	 */
	LARGEST_ARMY("Groesste Rittermacht"),

	/**
	 * The protocol attribute representing whether {@code Player} has the longest road.
	 * @see Player
	 */
	LONGEST_ROAD("Laengste Handelsstrasse"),

	/**
	 * The protocol attribute representing {@code DevelopmentCards}.
	 * @see DevelopmentCard
	 */
	DEVELOMENTCARDS("Entwicklungskarten"),

	/**
	 * The protocol attribute representing a single {@code DevelopmentCard}.
	 * @see DevelopmentCard
	 */
	DEVELOMENTCARD("Entwicklungskarte"),

	/**
	 * The protocol attribute representing a knight card.
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	KNIGHT("Ritter"),

	/**
	 * The protocol attribute representing a road building card.
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	ROAD_BUILDING("Strassenbau"),

	/**
	 * The protocol attribute representing a monopoly card.
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	MONOPOLY("Monopol"),

	/**
	 * The protocol attribute representing a year of plenty card.
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	YEAR_OF_PLENTY("Erfindung"),

	/**
	 * The protocol attribute representing a {@code VictoryPointCard}.
	 * @see VictoryPointCard
	 */
	VICTORY_POINT("Siegpunkt"),

	/**
	 * The protocol attribute representing a {@code Player}.
	 * @see Player
	 */
	PLAYER("Spieler"),

	/**
	 * The protocol attribute representing the message that a chosen color is not available.
	 */
	REPORT("Meldung"),

	/**
	 * The protocol attribute representing the text of message that a chosen color is not available.
	 */
	REPORT_VALUE("Farbe bereits vergeben"),

	/**
	 * The protocol attribute representing the rolled numbers on the {@code Dice}.
	 * @see Dice
	 */
	DICE_VALUE("Wurf"),

	/**
	 * The protocol attribute representing the winning {@code Player}.
	 * @see Player
	 */
	WINNER("Sieger"),

	/**
	 * The protocol attribute representing the target {@code Player} being robbed.
	 * @see Player
	 */
	TARGET("Ziel"),

	/**
	 * The protocol attribute representing the {@code Resources} a {@code Player} has to hand over.
	 * @see Resources
	 * @see Player
	 */
	SUBMIT("Abgeben"),

	/**
	 * The protocol attribute representing the offered {@code Resources} during a {@code Trade}.
	 * @see Resources
	 * @see Trade
	 */
	SUPPLY("Angebot"),

	/**
	 * The protocol attribute representing the requested {@code Resources} during a {@code Trade}.
	 * @see Resources
	 * @see Trade
	 */
	DEMAND("Nachfrage"),

	/**
	 * The protocol attribute representing the id of a {@code Trade}.
	 * @see Trade
	 */
	TRADING_ID("Handel id"),

	/**
	 * The protocol attribute representing the id of the {@code Player} who the active player wants to trade with.
	 * @see Player
	 * @see Trade
	 */
	FELLOW_PLAYER("Mitspieler"),

	/**
	 * The protocol attribute representing the first {@code Road} to be built after playing the road building card.
	 * @see Road
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	STREAT_ONE("Strasse 1"),

	/**
	 * The protocol attribute representing the second {@code Road} to be built after playing the road building card.
	 * @see Road
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	STREAT_TWO("Strasse 2"),

	/**
	 * The protocol attribute representing a {@code City}.
	 * @see City
	 */
	CITY("Stadt"),

	/**
	 * The protocol attribute representing a {@code Settlement}.
	 * @see Settlement
	 */
	SETTLEMENT("Dorf"),

	/**
	 * The protocol attribute representing a {@code Road}.
	 * @see Road
	 */
	ROAD("Strasse"),

	/**
	 * The protocol attribute representing the color red.
	 */
	RED("Rot"),

	/**
	 * The protocol attribute representing the color orange.
	 */
	ORANGE("Orange"),

	/**
	 * The protocol attribute representing the color white.
	 */
	WHITE("Weiss"),

	/**
	 * The protocol attribute representing the color blue.
	 */
	BLUE("Blau"),

	/**
	 * The protocol attribute representing if a {@code Player} wants to accept a {@code Trade} or not.
	 * @see Player
	 * @see Trade
	 */
	ACCEPT("Annehmen"),

	/**
	 * The protocol attribute representing the x position.
	 */
	X("x"),

	/**
	 * The protocol attribute representing the y position.
	 */
	Y("y");

	/**
	 * The string representation of this attributes.
	 */
	private final String string;

	/**
	 * Creates a new attributes.
	 * @param string the string representation of this protocol information.
	 */
	private Attributes(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

}
