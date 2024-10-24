package network.protocol;

import game.bank.Bank;
import game.board.construction.localities.City;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.robber.Robber;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import game.trade.Trade;
import network.server.Server;
import users.HumanUser;

/**
 * Contains all possible error messages send by the {@code Server}.
 * @author Christoph Hermann
 * @author Svenja Schoett
 * @see Server
 */
public class ErrorMessagesEnglish {

	/**
	 * An error message indicating, that the number of dropped resources cards is not the half.
	 */
	private static final String WRONG_DROPPED_RESOURCES = "Discard half of your resources.";

	/**
	 * An error message indicating, that the {@code Robber} may not be moved.
	 * @see Robber
	 */
	private static final String CANT_MOVE_ROBBER = "You're not allowed to move the robber now.";

	/**
	 * An error message indicating, that the {@code HumanUser} haven't chosen a valid color.
	 * @see HumanUser
	 */
	private static final String NO_VALID_COLOR = "Choose a valid color.";

	/**
	 * An error message indicating, that the {@code HumanUser} haven't chosen a valid Name.
	 * @see HumanUser
	 */
	private static final String NO_VALID_NAME = "Enter a name.";

	/**
	 * An error message indicating, that the {@code Player} can not roll.
	 */
	private static final String CANT_ROLL_DICE = "You can't roll the dice right now.";

	/**
	 * An error message indicating, that the {@code Robber} isn't in right position.
	 * @see Robber
	 */
	private static final String WRONG_ROBBER_POSITION = "Choose a different position for the robber.";

	/**
	 * An error message indicating, that the {@code Player} has no settlement near this {@code Robber}.
	 */
	private static final String WRONG_TARGET_PLAYER = "Choose a player whose building is close to this position.";

	/**
	 * An error message indicating, that the {@code Player} can not build.
	 */
	private static final String CANT_BUILD = "You can't build right now.";

	/**
	 * An error message indicating, that the {@code Player} can not build here.
	 */
	private static final String CANT_BUILD_HERE = "You can't build here.";

	/**
	 * An error message indicating, that the {@code Player} has not enough {@code Resources}.
	 * @see Player
	 */
	private static final String NO_RESOURCES = "You don't have sufficient resources.";

	/**
	 * An error message indicating, that the {@code Player} has no more {@code Settlement}, which can be builded.
	 * @see Player
	 * @see Settlement
	 */
	private static final String NO_SETTLEMENT = "You can't build more settlements.";

	/**
	 * An error message indicating, that the {@code Player} has no more {@code Road}, which can be builded.
	 * @see Player
	 * @see Road
	 */
	private static final String NO_ROAD = "You can't build more roads.";

	/**
	 * An error message indicating, that the {@code Player} has no more {@code City}, which can be builded.
	 * @see Player
	 * @see City
	 */
	private static final String NO_CITY = "You can't build more cities.";

	/**
	 * An error message indicating, that the {@code Bank} has no more {@code DevelopmentCard}.
	 * @see Bank
	 */
	private static final String NO_DEVELOPMENTCARD_IN_BANK = "There's no developement cards left in the bank.";

	/**
	 * An error message indicating, that the {@code Player} can not buy {@code DevelopmentCard}.
	 * @see Player
	 */
	private static final String CANT_BUY_CARD = "You can't buy cards right now.";

	/**
	 * An error message indicating, that the trade ratio is wrong.
	 */
	private static final String WRONG_RATIO = "Give resource cards in the correct ratio.";

	/**
	 * An error message indicating, that the {@code Bank} has no {@code Resources}.
	 * @see Bank
	 * @see Resources
	 */
	private static final String NO_RESOURCES_IN_BANK = "The bank doesn't have sufficient resources anymore.";

	/**
	 * An error message indicating, that the {@code Player} can not sea trade.
	 */
	private static final String CANT_SEA_TRADE = "You can't sea-trade.";

	/**
	 * An error message indicating, that the {@code Player} can not finish the round.
	 */
	private static final String CANT_FINISH_ROUND = "You can't finish the round.";

	/**
	 * An error message indicating, that the {@code Player} has no valid knight card.
	 */
	private static final String NO_KNIGHT_CARD = "You don't have a valid knight card.";

	/**
	 * An error message indicating, that the {@code Player} can not play development card.
	 */
	private static final String CANT_PLAY_CARD = "You can't play this card.";

	/**
	 * An error message indicating, that the both positions of roads are not allowed.
	 */
	private static final String WRONG_ROAD_POSITIONS = "The position is not allowed.";

	/**
	 * An error message indicating, that the {@code Player} has no more build road card.
	 */
	private static final String NO_BUILD_ROAD_CARD = "You don't have more valid road building cards.";

	/**
	 * An error message indicating, that the {@code Player} has no monopoly card.
	 */
	private static final String NO_MONOPOL_CARD = "You don't have a valid monopoly card.";

	/**
	 * An error message indicating, that the {@code Player} has no plenty year card.
	 */
	private static final String NO_PLENTY_YEAR_CARD = "You con't have a valid year of plenty card.";

	/**
	 * An error message indicating, that the {@code Player} can choose one more resource card.
	 */
	private static final String CAN_MORE_RESOURCES = "You can choose two resource cards.";

	/**
	 * An error message indicating, that the {@code Player} can choose one more resource card.
	 */
	private static final String PLEASE_WAIT = "It's not your turn. Please wait.";

	/**
	 * An error message indicating, that a {@code Player} has lost their connection to the {@code Server}.
	 * @see Player
	 * @see Server
	 */
	private static final String CONNECTION_LOST_MESSAGE = " has disconnected.";

	/**
	 * An error message indicating, that the supplied and demanded {@code Resources} of a {@code Trade} share a
	 * {@code ResourceType}.
	 * @see Resources
	 * @see Trade
	 * @see ResourceType
	 */
	private static final String SUPPLY_AND_DEMAND_CONTAIN_SAME_RESOURCE_TYPES = "You can't request the same type of "
			+ "resources you offer.";

	/**
	 * An error message indicating, that {@code Resources} can not be given or received as gifts.
	 * @see Resources
	 */
	private static final String CANT_GIVE_OR_RECEIVE_RESOURCES_AS_GIFTS = "You can't offer or request resources as "
			+ "gifts.";

	/**
	 * Returns the String which indicates that the {@code Player} can choose one more resource card.
	 * @return String
	 */
	public static String getCanMoreResources() {
		return CAN_MORE_RESOURCES;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has no plenty year card.
	 * @return String
	 */
	public static String getNoPlentyYearCard() {
		return NO_PLENTY_YEAR_CARD;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has no monopoly card.
	 * @return String
	 */
	public static String getNoMonopolCard() {
		return NO_MONOPOL_CARD;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has no more build road card.
	 * @return String
	 */
	public static String getNoBuildRoadCard() {
		return NO_BUILD_ROAD_CARD;
	}

	/**
	 * Returns the String which indicates that the both positions of roads are not allowed.
	 * @return String
	 */
	public static String getWrongRoadPositions() {
		return WRONG_ROAD_POSITIONS;
	}

	/**
	 * Returns the String which indicates that the {@code Player} can not play development card.
	 * @return String
	 */
	public static String getCantPlayCard() {
		return CANT_PLAY_CARD;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has no valid knight card.
	 * @return String
	 */
	public static String getNoKnightCard() {
		return NO_KNIGHT_CARD;
	}

	/**
	 * Returns the String which indicates that the {@code Player} can not finish the round.
	 * @return String
	 */
	public static String getCantFinishRound() {
		return CANT_FINISH_ROUND;
	}

	/**
	 * Returns the String which indicates that the {@code Player} can not sea trade.
	 * @return String
	 */
	public static String getCantSeaTrade() {
		return CANT_SEA_TRADE;
	}

	/**
	 * Returns the String which indicates that the {@code Bank} has no {@code Resources}.
	 * @return String
	 * @see Bank
	 * @see Resources
	 */
	public static String getNoResourcesInBank() {
		return NO_RESOURCES_IN_BANK;
	}

	/**
	 * Returns the String which indicates that the trade ratio is wrong.
	 * @return String
	 */
	public static String getWrongRatio() {
		return WRONG_RATIO;
	}

	/**
	 * Returns the String which indicates that the {@code Player} can not buy {@code DevelopmentCard}.
	 * @return String
	 * @see Player
	 */
	public static String getCantBuyCard() {
		return CANT_BUY_CARD;
	}

	/**
	 * Returns the String which indicates that the {@code Bank} has no more {@code DevelopmentCard}.
	 * @return String
	 * @see Bank
	 */
	public static String getNoDevelopmentCardInBank() {
		return NO_DEVELOPMENTCARD_IN_BANK;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has no more {@code City}, which can be builded.
	 * @return String
	 * @see Player
	 * @see City
	 */
	public static String getNoCity() {
		return NO_CITY;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has no more {@code Road}, which can be builded.
	 * @return String
	 * @see Player
	 * @see Road
	 */
	public static String getNoRoad() {
		return NO_ROAD;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has no more {@code Settlement}, which can be builded.
	 * @return String
	 * @see Player
	 * @see Settlement
	 */
	public static String getNoSettlement() {
		return NO_SETTLEMENT;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has not enough {@code Resources}.
	 * @return the String
	 * @see Player
	 */
	public static String getNoResources() {
		return NO_RESOURCES;
	}

	/**
	 * Returns the String which indicates that the {@code Player} can not build.
	 * @return String
	 */
	public static String getCantBuild() {
		return CANT_BUILD;
	}

	/**
	 * Returns the String which indicates that the {@code Player} can not build.
	 * @return String
	 */
	public static String getCantBuildHere() {
		return CANT_BUILD_HERE;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has no settlement near this {@code Robber}.
	 * @return the String
	 */
	public static String getWrongTargetPlayer() {
		return WRONG_TARGET_PLAYER;
	}

	/**
	 * Returns the String which indicates that the {@code Robber} isn't in right position.
	 * @return the String
	 * @see Robber
	 */
	public static String getWrongRobberPosition(){
		return WRONG_ROBBER_POSITION;
	}

	/**
	 * Returns the String which indicates that the {@code Player} can not roll dice.
	 * @return the String
	 * @see Player
	 */
	public static String getCantRollDice() {
		return CANT_ROLL_DICE;
	};

	/**
	 * Returns the String which indicates that the {@code HumanUser} haven't chosen a valid name.
	 * @return the String
	 * @see HumanUser
	 */
	public static String getNoValidName() {
		return NO_VALID_NAME;
	}

	/**
	 * Returns the String which indicates that the {@code HumanUser} haven't chosen a valid color.
	 * @return the String
	 * @see HumanUser
	 */
	public static String getNoValidColor() {
		return NO_VALID_COLOR;
	}

	/**
	 * Returns the String which indicates that the {@code Robber} may not be moved.
	 * @return the String.
	 * @see Robber
	 */
	public static String getCantMoveRobber() {
		return CANT_MOVE_ROBBER;
	}

	/**
	 * Returns the String which indicates that the dropped {@code Resources} cards to much or too few.
	 * @return the String.
	 */
	public static String getWrongDroppedResources() {
		return WRONG_DROPPED_RESOURCES;
	}
	/**
	 * Returns the String which indicates that the {@code Player} has to wait.
	 * @return the String.
	 * @see Player
	 */
	public static String getPleaseWait() {
		return PLEASE_WAIT;
	}

	/**
	 * Returns the String which indicates that the {@code Player} has lost their connection to the {@code Server}.
	 * @param player the {@link Player} who lost their connection to the {@link Server}.
	 * @return the String.
	 */
	public static String getConnectionLostMessage(Player player) {
		return player.getName() + CONNECTION_LOST_MESSAGE;
	}

	/**
	 * Returns the String which indicates that the supplied and demanded {@code Resources} of a {@code Trade} share a
	 * {@code ResourceType}.
	 * @return the String.
	 * @see Resources
	 * @see Trade
	 * @see ResourceType
	 */
	public static String getSupplyAndDemandContainSameResourceTypes() {
		return SUPPLY_AND_DEMAND_CONTAIN_SAME_RESOURCE_TYPES;
	}

	/**
	 * Returns the String which indicates that {@code Resources} can not be given or received as gifts.
	 * @return the String.
	 * @see Resources
	 */
	public static String getCantGiveOrReceiveResourcesAsGifts() {
		return CANT_GIVE_OR_RECEIVE_RESOURCES_AS_GIFTS;
	}

}
