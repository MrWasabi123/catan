package network.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Game;
import game.GamePhases;
import game.TradingRatios;
import game.bank.Bank;
import game.board.Board;
import game.board.construction.Construction;
import game.board.construction.localities.City;
import game.board.construction.localities.Locality;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.corners.Corner;
import game.board.edges.Edge;
import game.board.harbors.Harbor;
import game.board.harbors.HarborType;
import game.board.hexes.Hex;
import game.board.hexes.HexType;
import game.board.robber.Robber;
import game.board.tokens.Token;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.cards.specialcards.SpecialCard;
import game.cards.specialcards.SpecialCardType;
import game.cards.victorypointcards.VictoryPointCard;
import game.dice.Dice;
import game.player.Player;
import game.player.PlayerState;
import game.resources.ResourceType;
import game.resources.Resources;
import game.trade.Trade;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import lobby.Lobby;
import lobby.chat.ChatController;
import main.ApplicationInstance;
import network.Mapper;
import network.client.ClientController;
import network.protocol.Attributes;
import network.protocol.CheatCodes;
import network.protocol.ClientTypes;
import network.protocol.ProtocolJSONObjectConverter;
import network.protocol.ProtocolStringConverter;
import network.protocol.ServerTypes;
import users.AIUser;
import users.HumanUser;
import users.User;

/**
 * Represents a controller responsible for handling messages received by a
 * {@code ServerConnection}.
 * 
 * @author Yize Sun
 * @author Christoph Hermann
 * @see ServerConnection
 */
public class ServerController {

	/**
	 * ServerConnection ID for Client
	 */
	private int serverConnectionId;

	/**
	 * ServerConnection {@code User}
	 */
	private User serverConnectionUser;

	/**
	 * The number of players in {@code Game}
	 * 
	 * @see Player
	 */
	private int playersNumberInGame;

	/**
	 * The ID of {@code Trade}
	 */
	private int tradeID = 0;

	/**
	 * The {@code Player}, who has rolled 7
	 */
	private int savePlayerId;

	/**
	 * The {@code Player} whose JSONObject message should be handled.
	 */
	private Player activePlayer;

	/**
	 * The ID of {@code Player} who is the active player in {@code Game}
	 */
	private int gameActivePlayerId;

	/**
	 * List of ID from every {@code Player}, who has more then 7 resource cards
	 */
	private List<Player> playerIdsWhohasMoreThanSevenResourceCards = new ArrayList<>();

	/**
	 * If {@code Game} starts or not
	 */
	private boolean gameStarted = false;
	// !!!5 or 6 when 5 or 6 players in gamemodel
	/**
	 * The maximal number of {@code Player} in {@code Game}
	 */
	private final int MAX_PLAYERS = 4;

	/**
	 * The minimal number of {@code User} for game start
	 */
	private final int MIN_PLAYERS = MAX_PLAYERS - 1;

	/**
	 * The tool to count round, to be used to help checking how many players in one
	 * list have been handled.
	 */
	private int countRound = 1;

	/**
	 * Reset the round number for next time
	 */
	private final int COUNTROUND_RESET = 1;

	/**
	 * The upper limit by counting round, usually the number of players in game
	 */
	private int maxCountRound;

	/**
	 * The variable limit by counting round, usually the size of list of players.
	 */
	private int variableRoundLimit;

	/**
	 * The list of players, who has rolled maximal sum of dice result
	 */
	private List<Player> playersWithDiceMaxValue = new ArrayList<>();

	/**
	 * Save the list of players, who has rolled maximal sum of dice result
	 */
	private List<Player> savePlayersWithDiceMaxValue = new ArrayList<>();

	/**
	 * The maximal sum of dice result
	 */
	private int maxDiceSum = 0;

	/**
	 * Reset the maximal sum of dice result
	 */
	private final int MAX_DICESUM_RESET = 0;

	/**
	 * If the second round to build free {@code Settlement} or {@code Road}
	 */
	private boolean secondRoundToFreeBuild = false;

	/**
	 * If there are more than on player have rolled the maximal sum of dice result
	 */
	private boolean moreRoundToChooseBeginner = false;

	/**
	 * ServerConnection ids for Client
	 */
	private int nextUserId = 1;

	/**
	 * The {@code Lobby} being controlled by this controller.
	 * 
	 * @see Lobby
	 */
	private Lobby lobby;

	/**
	 * The ServerConnection of this Controller
	 */
	private ServerConnection serverConnection;

	/**
	 * Game that in server runs for all player
	 */
	private Game game;

	/**
	 * The bundle containing all error messages.
	 */
	private ObjectProperty<ResourceBundle> bundleProperty = ApplicationInstance.getInstance().getBundleProperty();

	/**
	 * The {@code ChatController} that is used to display messages.
	 * 
	 * @see ChatController
	 */
	private ChatController chatController;

	/**
	 * The server this controller runs on.
	 */
	private final Server server;

	/**
	 * Creates a new server controller.
	 * 
	 * @param chatController
	 *            the {@link ChatController} that is used to display messages.
	 * @param lobby
	 *            the {@link Lobby} being controlled by this controller.
	 * @param server
	 *            the {@link Server} this controller runs on.
	 */
	public ServerController(ChatController chatController, Lobby lobby, Server server) {
		this.chatController = chatController;
		this.lobby = lobby;
		this.server = server;
	}

	/**
	 * Checks the Key of the JSONObject and specifies the according actions
	 * 
	 * @param jsonObject
	 *            received message as JSONObject
	 * @param serverConnection
	 *            the {@link ServerConnection} which forwarded the JSONObject to
	 *            this controller.
	 */
	public synchronized void handle(JSONObject jsonObject, ServerConnection serverConnection) {
		this.serverConnection = serverConnection;
		this.serverConnectionId = serverConnection.getUserId();
		List<User> serverConnectionUsers = lobby.getUsers();

		for (User serverConnectionUser : serverConnectionUsers) {
			if (serverConnectionUser.getId() == serverConnectionId) {
				this.serverConnectionUser = serverConnectionUser;
				break;
			}
		}

		String type = jsonObject.firstKey();
		JSONObject obj = (JSONObject) jsonObject.get(type);
		ServerTypes messageType = determineMessageType(type);
		// before gamestart every message will be solved
		// if game started
		// message will be solved, if message is a chatMessage
		// or if player is not in PlayerState.WAIT.
		boolean haveRightSolveJSON = haveRightSolveJSON(messageType);
		// message will be solved
		if (haveRightSolveJSON) {
			if (game != null) {
				gameActivePlayerId = game.getActivePlayer().getId();
			}
			switch (messageType) {

			case HELLO:
				User user = addNewUser(serverConnection);
				sendWelcomeMessage(user);
				sendStatusMessages(user);
				break;

			case PLAYER: {

				if (obj.has(Attributes.NAME.toString())) {
					String name = (String) obj.get(Attributes.NAME.toString());
					serverConnectionUser.setName(name);
				}
				if (obj.has(Attributes.COLOR.toString())) {
					String colorName = obj.getString(Attributes.COLOR.toString());
					Color color = ProtocolStringConverter.getColor(colorName);
					serverConnectionUser.setColor(color);
				}
				serverConnectionUser.setState(PlayerState.START_GAME);
				sendStatusMessages(serverConnectionUser);
				sendOK();
			}
			break;

			case GAME_START: {
				String name = serverConnectionUser.getName();
				Color color = serverConnectionUser.getColor();

				boolean nameAvailable = checkNameAvailable(name);
				boolean colorAvailable = checkColorAvailable(color);
				boolean haveMinPlayer = haveMinPlayer();

				if (nameAvailable && colorAvailable) {
					// set user state as "Wartet auf Spielbeginn"
					serverConnectionUser.setState(PlayerState.WAIT_FOR_GAME_START);

					JSONObject message = new Mapper().writeValueAsJson(serverConnectionUser);
					serverConnection.broadcast(ClientTypes.STATE_UPDATE.toString(), message);
					sendOK();
				}
				// name or color not available
				else {
					// name not available
					if (!nameAvailable) {
						sendEmptyNameMessage();
					}
					// color not available
					if (!colorAvailable) {
						sendNoAvaliableColorMessage();
					}
				}
				// 3(or 5) users in lobby
				if (haveMinPlayer) {
					// users in lobby all ready
					if (playersAllready()) {
						// game start
						gameStart();
					}
				}
			}
			break;

			case ROLL_DICE: {
				int diceSum = 0;
				// is playerstate roll dice
				// is gamePhase to choose beginner
				// is diceSum 7
				boolean hasRightToRollDice = checkRightToRollDice();
				boolean phaseToChooseBeginner = isPhaseToChooseBeginner();
				boolean diceSumToDropCards;

				// playerstate roll dice
				// (if playerstate not roll dice, player is not allowed to dice)
				if (hasRightToRollDice) {
					// dice result e.g.[3,6]
					// send result to every players
					// calculate dice sum
					int[] diceNumbers = rollDice();
					sendDiceNumbertoEveryone(diceNumbers);
					diceSum = calculateDiceSum(diceNumbers);

					// phase to choose a beginner
					if (phaseToChooseBeginner) {

						if (diceSum > maxDiceSum) {
							maxDiceSum = diceSum;
							playersWithDiceMaxValue.clear();
							// player who first dice maxValue
							playersWithDiceMaxValue.add(activePlayer);
						} else if (diceSum == maxDiceSum) {
							// player who dice maxValue after
							playersWithDiceMaxValue.add(activePlayer);
						}
						// everyone roll dice once, (countRound < variableCountRound(= 4) )means first
						// three players have rolled dice
						// if m players has rolled maxValue, variableCountRound = m, then these m
						// players roll dice one by one
						if (countRound < variableRoundLimit) {
							setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
							sendOK();
							int nextPlayerId;
							if (!moreRoundToChooseBeginner) {
								nextPlayerId = game.getNextPlayerId();
							} else {
								// here the nextPlayer should be the second player in list
								int nextPlayer = countRound;
								nextPlayerId = savePlayersWithDiceMaxValue.get(nextPlayer).getId();
								game.giveControlToPlayer(nextPlayerId);
							}
							countRound++;
							setPlayerStateAndSendMessage(PlayerState.ROLL_DICE, nextPlayerId);
						}
						// after last player had rolled dice
						else {
							countRound = COUNTROUND_RESET;
							sendOK();

							boolean moreThanOnePlayerHasMaxValue = playersWithDiceMaxValue.size() > 1;

							setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
							// after last player has rolled dice, there are more than one player has rolled
							// the same max value
							if (moreThanOnePlayerHasMaxValue) {
								// select first player
								// set this player as active player in game
								// set number of players, who need to roll dice again
								// remove this player from list
								// reset maxDiceSum
								// tell nextPlayer to roll dice
								Player nextPlayer = playersWithDiceMaxValue.get(0);
								game.giveControlToPlayer(nextPlayer.getId());
								variableRoundLimit = playersWithDiceMaxValue.size();

								List<Player> playerList = new ArrayList<>();

								for (Player player : playersWithDiceMaxValue) {
									playerList.add(player);
								}
								savePlayersWithDiceMaxValue = playerList;

								playersWithDiceMaxValue.clear();
								maxDiceSum = MAX_DICESUM_RESET;
								moreRoundToChooseBeginner = true;
								setPlayerStateAndSendMessage(PlayerState.ROLL_DICE, nextPlayer.getId());
							}
							// after last player has rolled dice, there is only one player has rolled the
							// max value
							else {
								int nextPlayerId = playersWithDiceMaxValue.get(0).getId();

								if (nextPlayerId != gameActivePlayerId) {
									setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
									game.giveControlToPlayer(nextPlayerId);
								}
								game.setPhase(GamePhases.FREE_BUILD);
								setPlayerStateAndSendMessage(PlayerState.BUILD_FREE_SETTLEMENT, nextPlayerId);
							}
						}
					} else {
						diceSumToDropCards = isDiceSumToDropCards(diceSum);
						savePlayerId = game.getActivePlayer().getId();
						// dicesum = 7
						if (diceSumToDropCards) {
							playerIdsWhohasMoreThanSevenResourceCards = findPlayerIdsWhohasMoreThanSevenResourceCards();
							// one player has more than 7 resource cards
							if (playerIdsWhohasMoreThanSevenResourceCards.size() > 0) {
								// the first player in list is player, who has rolled 7
								if (playerIdsWhohasMoreThanSevenResourceCards.get(0).equals(game.getActivePlayer())) {
									// tell active player to drop resources cards
									setPlayerStateAndSendMessage(PlayerState.DISCARD_RESOURCES, gameActivePlayerId);
								}
								// the first player in list is not the player, who has rolled 7
								else {
									setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
									int nextPlayerId = findNextPlayerIdFromList(
											playerIdsWhohasMoreThanSevenResourceCards);
									setPlayerStateAndSendMessage(PlayerState.DISCARD_RESOURCES, nextPlayerId);
								}
							}
							// no one has more than 7 resource cards
							else {
								setPlayerStateAndSendMessage(PlayerState.MOVE_ROBBER, gameActivePlayerId);
							}

						}
						// dicesum != 7
						else {
							// check token number
							List<Token> tokens = game.getBoard().getTokens();
							List<Token> resourcesTokens = new ArrayList<>();

							for (int i = 0; i < tokens.size(); i++) {
								Token token = tokens.get(i);
								if (token.getNumber() != 0) {
									if (diceSum == token.getNumber()) {
										boolean robberIsNotOnToken = checkRobberIsNotOnToken(token);
										// there is no robber on this token
										if (robberIsNotOnToken) {
											if (!resourcesTokens.contains(token)) {
												resourcesTokens.add(token);
											}
										}
									}
								}
							}
							AddResourceToPlayersOn(resourcesTokens);
							game.setPhase(GamePhases.BIULD_TRADE_PHASE);
							setPlayerStateAndSendMessage(PlayerState.TRADE_OR_BUILD, gameActivePlayerId);
						}
					}
					sendOK();
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_ROLL_DICE"));
				}
			}
			break;

			case LOSE_RESOURCE: {
				JSONObject j_resources = obj.getJSONObject(Attributes.SUBMIT.toString());
				Resources resources = ProtocolJSONObjectConverter.getResources(j_resources);

				// if activePlayer the player who has rolled dice 7
				boolean activePlayerTheDicePlayer = isactivePlayerTheDicePlayer();
				;
				boolean hasDroppedRightNumberOfCards = checkDroppedCardsNumber(resources);

				if (hasDroppedRightNumberOfCards) {
					// activePlayer drop resource cards
					// delete active player from the list
					minusResources(resources);
					playerIdsWhohasMoreThanSevenResourceCards.remove(game.getActivePlayer());

					// if there more than one player need drop resources cards
					boolean stillHavePlayerNeedDropResources = playerIdsWhohasMoreThanSevenResourceCards.size() > 0;

					// activePlayer is the player who has rolled dice 7
					if (activePlayerTheDicePlayer) {
						// there is still players in list, who need to drop resource cards
						if (stillHavePlayerNeedDropResources) {
							// tell activePlayer his cards are dropped
							// set activePlayer wait
							// find next player, who need drop cards
							// change activePlayer and told others
							sendOK();
							setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
							int nextPlayerId = findNextPlayerIdFromList(playerIdsWhohasMoreThanSevenResourceCards);
							setPlayerStateAndSendMessage(PlayerState.DISCARD_RESOURCES, nextPlayerId);
						}
						// there is no other players in list
						else {

							// tell active player his cards are dropped
							// tell active player its time to move robber
							sendOK();
							setPlayerStateAndSendMessage(PlayerState.MOVE_ROBBER, gameActivePlayerId);
						}
					}
					// activePlayer is not the player who has rolled dice 7
					else {
						// there is still players in list, who need to drop resource cards
						if (stillHavePlayerNeedDropResources) {
							// tell active player his cards are dropped
							// set active player wait
							// find next player, who need drop cards
							// tell next player, its time to drop cards
							sendOK();
							setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
							int nextPlayerId = findNextPlayerIdFromList(playerIdsWhohasMoreThanSevenResourceCards);
							setPlayerStateAndSendMessage(PlayerState.DISCARD_RESOURCES, nextPlayerId);
						}
						// no one need to drop resource card
						else {
							// tell active player his cards are dropped
							// set active player wait
							// give the control back to player who has diced 7
							// tell player who has rolled 7, please move robber
							sendOK();
							setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
							game.giveControlToPlayer(savePlayerId);
							setPlayerStateAndSendMessage(PlayerState.MOVE_ROBBER, savePlayerId);
						}
					}
				} else {
					//					ResourceBundle bundle = ResourceBundle.getBundle("network.ErrorMessages", locale);
					sendNoRightErrorInfoToClient(getBundle().getString("WRONG_DROPPED_RESOURCES"));
				}
			}
			break;

			case MOVE_ROBBER: {
				if (activePlayer.getState() == PlayerState.MOVE_ROBBER) {
					// take out the new robber (string) Position from JSONObject
					// find out the new robber (hex)position
					// current (hex) position of robber
					// is this (hex)position a sea Hex, return true, if this (hex)position is a hex
					// in hexList of game and is not a sea hex
					// String stringPosition = obj.getString(Attributes.POSITION.toString());

					String stringPosition = ProtocolJSONObjectConverter.getHexPosition(obj, game.getBoard());
					JSONObject xyPosition = obj.getJSONObject(Attributes.POSITION.toString());
					Hex newRobberPosition = findHexPositionFromString(stringPosition);
					Hex oldRobberPosition = game.getBoard().getRobber().getPosition();
					boolean positionOneValidPosition = isPositionInGameArea(newRobberPosition);

					// robber must be moved in a different (hex)position than before
					// robber is not moved or new (hex)position is not a valid position
					if (oldRobberPosition == newRobberPosition || (!positionOneValidPosition)) {
						// send error info to activePlayer
						// send PlayerState update to this player
						// pls move robber
						sendNoRightErrorInfoToClient(getBundle().getString("WRONG_ROBBER_POSITION"));
						setPlayerStateAndSendMessage(PlayerState.MOVE_ROBBER, gameActivePlayerId);
					}
					// robber is moved and new (hex)position is a valid position
					else {
						// If a player has been selected as the target of the robbing.
						if (obj.has(Attributes.TARGET.toString())) {
							int targetPlayerId = obj.getInt(Attributes.TARGET.toString());
							// get (int[]) id of all players, who has settlement or city near by this
							// (hex)position,which is new occupied by robber
							// is target player near by this robber
							// has target player at least one resource card or not
							List<Player> relativePlayers = getRelativePlayerListOfOneHex(newRobberPosition);
							boolean tragetPlayerIsNearby = isListIncludTargetPlayer(relativePlayers, targetPlayerId);
							boolean targetPlayerHasEnoughResources = hasTargetPlayerEnoughResources(targetPlayerId);

							// if robber can be moved in a empty area, new case should be added
							// e.g.player id is from 1 to 4, when playerId is 0, then server know robber
							// will be moved but no one will be grabbed(if())
							// target player is near by robber
							if (tragetPlayerIsNearby) {
								// if no resources player can be grabbed,this case should be fit
								// target player have at least one resource card
								if (targetPlayerHasEnoughResources) {
									grabResourceFromTargetPlayerAndSendMessageToAll(targetPlayerId);
								}
								// move robber in new (hex) position
								// tell all players the new position of robber
								// update playerState as trade or build
								// set game phase in build Trade
								// tell activePlayer robber is moved successfully
								game.getBoard().getRobber().move(newRobberPosition);

								sendRobberInfoToAll(xyPosition, targetPlayerId);
								setPlayerStateAndSendMessage(PlayerState.TRADE_OR_BUILD, this.gameActivePlayerId);
								game.setPhase(GamePhases.BIULD_TRADE_PHASE);
								sendOK();
							}
							// target player is not near by robber
							else {
								sendNoRightErrorInfoToClient(getBundle().getString("WRONG_TARGET_PLAYER"));
								setPlayerStateAndSendMessage(PlayerState.MOVE_ROBBER, this.gameActivePlayerId);
							}
						} else {
							// Move the robber to the new position.
							Robber robber = game.getBoard().getRobber();
							robber.move(newRobberPosition);

							// Send a message to all players.
							sendRobberMovedMessage(xyPosition);
							setPlayerStateAndSendMessage(PlayerState.TRADE_OR_BUILD, this.gameActivePlayerId);
							game.setPhase(GamePhases.BIULD_TRADE_PHASE);
							sendOK();
						}
					}
				} else {
					// The player is not allowed to move the robber right now
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_MOVE_ROBBER"));
				}
			}
			break;

			case BUILD: {
				String construction = obj.getString(Attributes.TYPE.toString());
				String stringPosition = ProtocolJSONObjectConverter.getPosition(obj, game.getBoard());
				boolean hasRightToBuilding = checkRightToBuilding();
				boolean positionOneValidPosition;
				boolean hasEnoughResources;
				boolean free_Build = isA_Free_Build();
				boolean building_Settlement = isBuilding_Settlement(construction);
				boolean building_Road = isBuilding_Road(construction);

				if (!hasRightToBuilding) {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_BUILD"));
				} else {
					// build not free construction
					if (!free_Build) {
						// build settlement
						if (building_Settlement) {
							boolean hasMaxSettlement = hasMaxSettlement();
							// player has builded less than 4 settlements
							if (!hasMaxSettlement) {
								Corner settlementPosition = findsettlementPositionFromString(stringPosition);
								positionOneValidPosition = isPositionOneValidSettlementPosition(settlementPosition);
								// near this corner there is one own road
								if (positionOneValidPosition) {
									hasEnoughResources = hasEnoughResourcesToBuild(construction);
									if (hasEnoughResources) {
										boolean isHarbor = isHarbor(settlementPosition);
										// this corner is one side of harbor
										if (isHarbor) {
											buildHarbor(settlementPosition);
											buildSettlementAndSendMessage(settlementPosition);
										} else {
											buildSettlementAndSendMessage(settlementPosition);
										}
										sendOK();

										boolean longestRoadIsBroken = isLongestRoadBroken(settlementPosition);
										int longestRoad = 5;
										List<Player> playerWhoHasLongestRoad = new ArrayList<>();

										// *** only when a settlement is builded, can a longest linked road be broken.
										// ***
										// *** rules for choose next player, who can get the longest road card
										// *** check, who has the longest length 
										// *** if two player has the same longest length, then no one will get the
										// *** longest card

										// after the settlement has been builded, the longest road is broken and the
										// longest road card allowed not to be kept
										if (longestRoadIsBroken) {
											for (Player player : game.getPlayers()) {
												// player has linked roads length > 5 or longest road length
												if (player.getLongestRoadLength() > longestRoad) {
													longestRoad = player.getLongestRoadLength();
													playerWhoHasLongestRoad.clear();
													// every player in list playerWhoHasLongestRoad has roads >= 5
													playerWhoHasLongestRoad.add(player);
												}
												// player has linked roads length = 5 or longest road length
												else if (player.getLongestRoadLength() == longestRoad) {
													// every player in list playerWhoHasLongestRoad has roads >= 5
													playerWhoHasLongestRoad.add(player);
												}
											}
											// only one player has the longest road length
											if (playerWhoHasLongestRoad.size() == 1) {
												giveLongestRoadCardTo(playerWhoHasLongestRoad.get(0));
												sendLongestRoadMessage(playerWhoHasLongestRoad.get(0).getId());
												checkWin(playerWhoHasLongestRoad.get(0));
											}
											// no one has the linked roads with more than length 5
											else {
												sendLoseLongestRoadCardMessage(
														game.getLongestRoadCard().getOwner().getId());
												Player previousOwner = game.getLongestRoadCard().getOwner();
												game.getLongestRoadCard().resetOwnership();
												previousOwner.updateVictoryPoints();
											}
										}
									} else {
										sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES"));
									}
								} else {
									sendNoRightErrorInfoToClient(getBundle().getString("CANT_BUILD_HERE"));
								}
							} else {
								sendNoRightErrorInfoToClient(getBundle().getString("NO_SETTLEMENT"));
							}
						}
						// build road
						else if (building_Road) {
							boolean hasMaxRoad = hasMaxRoad();

							// has builded less than 15 roads
							if (!hasMaxRoad) {
								Edge roadPosition = findRoadPositionFromString(stringPosition);
								positionOneValidPosition = isPositionOneValidRoadPosition(roadPosition);
								// there is one own settlement or own road that is linked with this position
								if (positionOneValidPosition) {
									hasEnoughResources = hasEnoughResourcesToBuild(construction);
									// player has enough resources
									if (hasEnoughResources) {
										buildRoadAndSendMessage(roadPosition);
										sendOK();

										boolean hasLongestRoad = checkLongestRoad();

										if (hasLongestRoad) {
											giveLongestRoadCardTo(game.getActivePlayer());
											sendLongestRoadMessage(gameActivePlayerId);
											checkWin();
										}
									} else {
										sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES"));
									}
								} else {
									sendNoRightErrorInfoToClient(getBundle().getString("CANT_BUILD_HERE"));
								}
							} else {
								sendNoRightErrorInfoToClient(getBundle().getString("NO_ROAD"));
							}
						}
						// build city
						else {
							boolean hasMaxCity = hasMaxCity();
							// has builded less than 3 cities
							if (!hasMaxCity) {
								Corner cityPosition = findCityPositionFromString(stringPosition);
								positionOneValidPosition = isPositionOneValidCityPosition(cityPosition);
								// on position there is one own settlement
								if (positionOneValidPosition) {
									hasEnoughResources = hasEnoughResourcesToBuild(construction);
									if (hasEnoughResources) {
										buildCityAndSendMessage(cityPosition);
										sendOK();
									} else {
										sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES"));
									}
								} else {
									sendNoRightErrorInfoToClient(getBundle().getString("CANT_BUILD_HERE"));
								}
							} else {
								sendNoRightErrorInfoToClient(getBundle().getString("NO_CITY"));
							}
						}
					}

					// build_free_construction
					else {
						// build settlement
						if (building_Settlement) {
							Corner settlementPosition = findsettlementPositionFromString(stringPosition);
							boolean isValidPositionForBuildFreeSettlement = isValidPositionForBuildFreeSettlement(
									settlementPosition);
							// position in a game area
							if (isValidPositionForBuildFreeSettlement) {
								boolean isHarbor = isHarbor(settlementPosition);

								if (isHarbor) {
									buildHarbor(settlementPosition);
									buildFreeSettlementAndSendMessage(settlementPosition);
								} else {
									buildFreeSettlementAndSendMessage(settlementPosition);
								}
								// get resources when build a settlement in second free building round
								if (secondRoundToFreeBuild) {
									getResourceFromCornerAndSendMessage(settlementPosition);
								}
								sendOK();
								setPlayerStateAndSendMessage(PlayerState.BUILD_FREE_ROAD, gameActivePlayerId);
							} else {
								sendNoRightErrorInfoToClient(getBundle().getString("CANT_BUILD_HERE"));
							}
						}
						// build road
						// We still have to check if the player tries to build a road. If they try to build a city, we
						// would get a NullPointerException.
						else if (building_Road) {
							Edge roadPosition = findRoadPositionFromString(stringPosition);
							// check if own settlement is nearby
							boolean isValidPositionForBuildFreeRoad = isValidPositionForBuildFreeRoad(roadPosition);
							// own settlement is nearby
							if (isValidPositionForBuildFreeRoad) {
								buildFreeRoadAndSendMessage(roadPosition);
								sendOK();
								// round for 4 players
								// e.g.for the first free building round:
								// player1->player2->player3, countRound:1->2->3->4
								if (countRound < maxCountRound) {
									if (!secondRoundToFreeBuild) {
										// set active player wait
										// find next player as active player
										// for first free building round:
										// (e.g.player1->player2->player3->player4)
										setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
										int nextPlayerId = findNextPlayerIdForFreeBuild();
										setPlayerStateAndSendMessage(PlayerState.BUILD_FREE_SETTLEMENT, nextPlayerId);
									} else {
										// set active player wait
										// find next player as active player
										// e.g.for second free building round:
										// (e.g.player4->player3->player2->player1)
										setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
										int nextPlayerId = findNextPlayerIdForFreeBuildInSecondRound();
										setPlayerStateAndSendMessage(PlayerState.BUILD_FREE_SETTLEMENT, nextPlayerId);
									}
								}
								// round for player
								// e.g.for the first free building round:
								// (e.g.player4, countRound=conuntRoundReset=1)
								else {
									countRound = COUNTROUND_RESET;
									if (!secondRoundToFreeBuild) {
										// get resources when build a settlement in second free building round
										secondRoundToFreeBuild = !secondRoundToFreeBuild;
										setPlayerStateAndSendMessage(PlayerState.BUILD_FREE_SETTLEMENT,
												gameActivePlayerId);
									} else {
										// game start with this active player
										game.setPhase(GamePhases.ROLL_DICE_PHASE);
										setPlayerStateAndSendMessage(PlayerState.ROLL_DICE, gameActivePlayerId);
									}
								}
							} else {
								sendNoRightErrorInfoToClient(getBundle().getString("CANT_BUILD_HERE"));
							}
						}
					}
				}
			}
			break;

			case BUY_DEVLOPMENTCARD: {
				boolean isThereStillAnyDevelopmentcard = isThereStillAnyDevelopmentcard();
				boolean hasEnoughResources = hasEnoughResourcesToBuyDevelopmentCards();
				boolean hasRightToBuyDevelopmentCard = hasRightToBuyDevelopmentCard();

				if (hasRightToBuyDevelopmentCard) {
					// there is still development card in bank
					if (isThereStillAnyDevelopmentcard) {
						// player has enough resources to buy development cards
						if (hasEnoughResources) {
							buyDevelopmentCardAndSendMessage();
							sendOK();
							checkWin();
						} else {
							sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES"));
						}
					} else {
						sendNoRightErrorInfoToClient(getBundle().getString("NO_DEVELOPMENTCARD_IN_BANK"));
					}
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_BUY_CARD"));
				}
			}
			break;

			case SEA_TRADING: {
				JSONObject j_supply = obj.getJSONObject(Attributes.SUPPLY.toString());
				JSONObject j_demand = obj.getJSONObject(Attributes.DEMAND.toString());
				Resources supply = ProtocolJSONObjectConverter.getResources(j_supply);
				Resources demand = ProtocolJSONObjectConverter.getResources(j_demand);

				boolean hasRightForSeaTrad = hasRightForSeaTrad();
				boolean playerHasEnoughResourcesToTrad = checkPlayerHasEnoughrResourceToTrad(supply);
				boolean bankHasEnoughResoucesToTrad = checkBankHasEnoughResourceToTrad(demand);
				// !!in this case, there is situation to check if more than two types of
				// resources as supply
				if (hasRightForSeaTrad) {
					if (!haveSameResourceType(supply, demand)) {
						if (!(supply.getSum() == 0 || demand.getSum() == 0)) {
							if (playerHasEnoughResourcesToTrad) {
								if (bankHasEnoughResoucesToTrad) {
									if (isRatioCorrect(game.getActivePlayer().getTradingRatios(), supply, demand)) {
										seaTrad(supply, demand);
										sendOK();
										broadcastLoseResourcesMessage(supply, gameActivePlayerId);
										broadcastGetResourcesMessage(demand, gameActivePlayerId);
									} else {
										sendNoRightErrorInfoToClient(getBundle().getString("WRONG_RATIO"));
									}
								} else {
									sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES_IN_BANK"));
								}
							} else {
								sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES"));
							}
						} else {
							sendNoRightErrorInfoToClient(getBundle().getString("CANT_GIVE_OR_RECEIVE_RESOURCES_AS_GIFTS"));
						}
					} else {
						sendNoRightErrorInfoToClient(getBundle().getString("SUPPLY_AND_DEMAND_CONTAIN_SAME_RESOURCE_TYPES"));
					}
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_SEA_TRADE"));
				}
			}
			break;

			case END_TURN: {
				boolean hasRightToFinishRound = checkRightToFinishRound();

				if (hasRightToFinishRound) {
					setPlayerStateAndSendMessage(PlayerState.WAIT, gameActivePlayerId);
					sendOK();
					int nextPlayerId = findNextPlayerIdFinishRound();
					setPlayerStateAndSendMessage(PlayerState.ROLL_DICE, nextPlayerId);
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_FINISH_ROUND"));
				}
			}
			break;

			case OFFER_TRADE: {
				JSONObject j_supply = obj.getJSONObject(Attributes.SUPPLY.toString());
				JSONObject j_demand = obj.getJSONObject(Attributes.DEMAND.toString());
				Resources supply = ProtocolJSONObjectConverter.getResources(j_supply);
				Resources demand = ProtocolJSONObjectConverter.getResources(j_demand);

				boolean hasEnoughResources = hasEnoughResources(game.getActivePlayer().getResources(), supply);

				if (!haveSameResourceType(supply, demand)) {
					if (!(supply.getSum() == 0 || demand.getSum() == 0)) {
						if (hasEnoughResources) {
							int tradeId = idMaker();
							// set trade in game and in player, subtract supply from player's resources
							makeTradeOffer(tradeId, supply, demand);
							sendSupplyMessage(tradeId, supply, demand);
							sendOK();
						} else {
							sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES"));
						}
					} else {
						sendNoRightErrorInfoToClient(getBundle().getString("CANT_GIVE_OR_RECEIVE_RESOURCES_AS_GIFTS"));
					}
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES"));
				}
			}
			break;

			case ACCEPT_TRADE: {
				int tradeId = obj.getInt(Attributes.TRADING_ID.toString());

				if (obj.getBoolean(Attributes.ACCEPT.toString())) {
					// The player accepted the trade.
					Resources demand = game.getTrade().getTradeRequest();
					// dont need check supply, because supply has been checked by offering a trade
					boolean hasEnoughResources = hasEnoughResources(activePlayer.getResources(), demand);
					// player's resources >= demand resources
					if (hasEnoughResources) {
						if (game.getTrade() != null) {
						game.getTrade().getAcceptedList().add(game.getPlayerWithId(activePlayer.getId()));
						sendAcceptMessage(tradeId, true);
						sendOK();
						}
					} else {
						sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES"));
					}
				} else {
					// The player declined the trade.
                    if(game.getTrade() != null) {
					game.getTrade().addDecliningPlayer(activePlayer);
					sendAcceptMessage(tradeId, false);
					sendOK();
					}
				}
			}
			break;

			case FINISH_TRADE: {
				int tradeId = obj.getInt(Attributes.TRADING_ID.toString());
				int fellowPlayerId = obj.getInt(Attributes.FELLOW_PLAYER.toString());

				executeTrade(tradeId, fellowPlayerId);
				sendOK();
			}
			break;

			case CANCEL_TRADE: {
				int tradeId = obj.getInt(Attributes.TRADING_ID.toString());
				if(game.getTrade()!=null) {
					if (game.getTrade().getPlayer().getId() != activePlayer.getId()) {
						game.getTrade().getAcceptedList().remove(activePlayer);
					} else {
						game.setTrade(null);
					}
					sendCancelMessage(tradeId, activePlayer.getId());
					sendOK();
				}}
			break;

			case PLAY_KNIGHT: {
				// String stringPosition = obj.getString(Attributes.POSITION.toString());
				String stringPosition = ProtocolJSONObjectConverter.getHexPosition(obj, game.getBoard());
				JSONObject xyPosition = obj.getJSONObject(Attributes.POSITION.toString());
				Hex newRobberPosition = findHexPositionFromString(stringPosition);
				Hex oldRobberPosition = game.getBoard().getRobber().getPosition();
				PlayerState savePlayerState = game.getActivePlayer().getState();
				boolean positionOneValidPosition = isPositionInGameArea(newRobberPosition);
				boolean hasRightToPlayDevelopmentCard = checkRightToPlayCard();
				boolean hasValidKnightCard = checkPlayableDevelopmentCard(PlayableDevelopmentCardType.KNIGHT);

				if (hasRightToPlayDevelopmentCard) {
					// has knight card, and this card has been already bought before
					if (hasValidKnightCard) {
						// robber has not been moved or robber is not in game area
						if (oldRobberPosition == newRobberPosition || (!positionOneValidPosition)) {
							sendNoRightErrorInfoToClient(getBundle().getString("CANT_MOVE_ROBBER"));
						}
						// robber is moved and new (hex)position is a valid position
						else {
							if (obj.has(Attributes.TARGET.toString())) {
								int targetPlayerId = obj.getInt(Attributes.TARGET.toString());
								// get (int[]) id of all players, who has settlement or city near by this
								// (hex)position,which is new occupied by robber
								// is target player near by this robber
								// has target player at least one resource card or not
								List<Player> relativePlayers = getRelativePlayerListOfOneHex(newRobberPosition);
								boolean tragetPlayerIsNearby = isListIncludTargetPlayer(relativePlayers,
										targetPlayerId);
								boolean targetPlayerHasEnoughResources = hasTargetPlayerEnoughResources(targetPlayerId);

								// target player is near by robber
								if (tragetPlayerIsNearby) {
									// if no resources player can be grabbed,this case should be fit
									// target player have at least one resource card
									if (targetPlayerHasEnoughResources) {
										grabResourceFromTargetPlayerAndSendMessageToAll(targetPlayerId);
									}
									// useKnightCard
									// move robber in new (hex) position
									// tell all players Knightcard message
									// grab one resource card from target player and send lose_resource(Kosten)
									// message
									// tell activePlayer robber is moved successfully
									// update playerState as trade or build
									playKnightCard();
									game.getBoard().getRobber().move(newRobberPosition);
									sendKnightCardMessageToAll(xyPosition, targetPlayerId);
									// ??im not very sure, whether move Robber message will be send here
									sendRobberInfoToAll(xyPosition, targetPlayerId);
									sendOK();

									boolean hasLargestArmy = checkLargestArmy();

									if (hasLargestArmy) {
										giveLargestArmyCardTo(game.getActivePlayer());
										sendLargestArmyMessage();
										checkWin();
									}
									setPlayerStateAndSendMessage(savePlayerState, gameActivePlayerId);
								}
								// target player is not near by robber
								else {
									sendNoRightErrorInfoToClient(getBundle().getString("WRONG_ROBBER_POSITION"));
								}
							} else {
								playKnightCard();
								game.getBoard().getRobber().move(newRobberPosition);
								sendKnightCardWithoutTargetPlayerMessageToAll(xyPosition);
								sendRobberMovedMessage(xyPosition);
								sendOK();
								setPlayerStateAndSendMessage(savePlayerState, gameActivePlayerId);
							}
						}
					} else {
						sendNoRightErrorInfoToClient(getBundle().getString("NO_KNIGHT_CARD"));
					}
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_PLAY_CARD"));
				}
			}
			break;

			case PLAY_ROAD_BUILDING: {
				//if positions are sent by Strings
				// String stringPosition_1 = obj.getString(Attributes.STREAT_ONE.toString());
				// String stringPosition_2 = obj.getString(Attributes.STREAT_TWO.toString());
				//
				//				String stringPos_1 = obj.getString(Attributes.STREAT_ONE.toString());
				//				String stringPos_2 = obj.getString(Attributes.STREAT_TWO.toString());

				String stringPosition_1 = ProtocolJSONObjectConverter.getStreetOnePosition(obj, game.getBoard());
				String stringPosition_2 = ProtocolJSONObjectConverter.getStreetTwoPosition(obj, game.getBoard());
				JSONArray xyPosition_1 = obj.getJSONArray(Attributes.STREAT_ONE.toString());
				JSONArray xyPosition_2 = obj.getJSONArray(Attributes.STREAT_TWO.toString());

				Edge roadPosition_1 = findRoadPositionFromString(stringPosition_1);
				Edge roadPosition_2 = findRoadPositionFromString(stringPosition_2);

				PlayerState savePlayerState = game.getActivePlayer().getState();

				boolean hasRightToPlayDevelopmentCard = checkRightToPlayCard();
				boolean hasValidRoadCard = checkPlayableDevelopmentCard(PlayableDevelopmentCardType.ROAD_BUILDING);
				boolean hasRoadToBeBuilded = checkRoadNumber();
				boolean position_1_Valid = isPositionOneValidRoadPosition(roadPosition_1);
				boolean position_2_Valid = isPositionOneValidRoadPosition(roadPosition_2);
				// roadPosition_1 is next to roadPosition_2 and roadPosition_2 is valid.
				position_1_Valid = position_1_Valid || position_2_Valid && roadPosition_2.getAdjacentEdgesOfEdge().contains(roadPosition_1);
				// roadPosition_2 is next to roadPosition_1 and roadPosition_1 is valid.
				position_2_Valid = position_2_Valid || position_1_Valid && roadPosition_1.getAdjacentEdgesOfEdge().contains(roadPosition_2);
				boolean twoRoadsCanBeBuilded = checkMoreThanTwoRoadsCanBeBuilded();

				if (hasRightToPlayDevelopmentCard) {
					if (hasValidRoadCard) {
						// player has builded less than 15 roads(e.g. 1,2,...,13,14)
						if (hasRoadToBeBuilded) {
							// position_1 or position_2 not valid
							if (!position_1_Valid || !position_2_Valid) {
								if (!position_1_Valid && !position_2_Valid) {
									sendNoRightErrorInfoToClient(getBundle().getString("WRONG_ROAD_POSITIONS"));
								} // position_1 valid
								else if (position_1_Valid) {
									playRoadCard();
									sendPlayRoadCardMessage(xyPosition_1);
									buildRoadWithCardAndSendMessage(roadPosition_1);
									sendOK();
									checkWin();

									boolean hasLongestRoad = checkLongestRoad();

									if (hasLongestRoad) {
										giveLongestRoadCardTo(game.getActivePlayer());
										sendLongestRoadMessage(gameActivePlayerId);
										checkWin();
									}
									setPlayerStateAndSendMessage(savePlayerState, gameActivePlayerId);
								}
								// position_2 valid
								else {
									playRoadCard();
									sendPlayRoadCardMessage(xyPosition_2);
									buildRoadWithCardAndSendMessage(roadPosition_2);
									sendOK();
									checkWin();

									boolean hasLongestRoad = checkLongestRoad();

									if (hasLongestRoad) {
										giveLongestRoadCardTo(game.getActivePlayer());
										sendLongestRoadMessage(gameActivePlayerId);
										checkWin();
									}
									setPlayerStateAndSendMessage(savePlayerState, gameActivePlayerId);
								}
							}
							// position_1 and position_2 both are valid
							else {
								// has builded less than 14 roads(e.g.1,2,..,12,13)
								if (twoRoadsCanBeBuilded) {
									playRoadCard();
									buildRoadWithCardAndSendMessage(roadPosition_1);
									buildRoadWithCardAndSendMessage(roadPosition_2);
									sendPlayRoadCardMessage(xyPosition_1, xyPosition_2);
									sendOK();
									checkWin();

									boolean hasLongestRoad = checkLongestRoad();

									if (hasLongestRoad) {
										giveLongestRoadCardTo(game.getActivePlayer());
										sendLongestRoadMessage(gameActivePlayerId);
										checkWin();
									}
									setPlayerStateAndSendMessage(savePlayerState, gameActivePlayerId);
								}
								// has builded 14 roads
								else {
									playRoadCard();
									sendPlayRoadCardMessage(xyPosition_1);
									buildRoadWithCardAndSendMessage(roadPosition_1);
									sendOK();
									checkWin();

									boolean hasLongestRoad = checkLongestRoad();

									if (hasLongestRoad) {
										giveLongestRoadCardTo(game.getActivePlayer());
										sendLongestRoadMessage(gameActivePlayerId);
										checkWin();
									}
									setPlayerStateAndSendMessage(savePlayerState, gameActivePlayerId);
								}
							}
						} else {
							sendNoRightErrorInfoToClient(getBundle().getString("NO_ROAD"));
						}
					} else {
						sendNoRightErrorInfoToClient(getBundle().getString("NO_BUILD_ROAD_CARD"));
					}
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_PLAY_CARD"));
				}
			}
			break;

			case PLAY_MONOPOLY: {
				String j_resourceType = obj.getString(Attributes.RESOURCE.toString());
				ResourceType resourceType = ProtocolStringConverter.getResourceType(j_resourceType);

				PlayerState savePlayerState = game.getActivePlayer().getState();

				boolean hasRightToPlayDevelopmentCard = checkRightToPlayCard();
				boolean hasValidMonopolyCard = checkPlayableDevelopmentCard(PlayableDevelopmentCardType.MONOPOLY);

				if (hasRightToPlayDevelopmentCard) {
					if (hasValidMonopolyCard) {
						playMonopolyCard();
						sendPlayMonopolyCardMessage(j_resourceType);
						monopolizeMarket(resourceType);
						sendOK();
						setPlayerStateAndSendMessage(savePlayerState, gameActivePlayerId);
					} else {
						sendNoRightErrorInfoToClient(getBundle().getString("NO_MONOPOL_CARD"));
					}
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_PLAY_CARD"));
				}
			}
			break;

			case PLAY_YEAR_OF_PLENTY: {
				JSONObject j_resources = obj.getJSONObject(Attributes.RESOURCES.toString());
				Resources resources = ProtocolJSONObjectConverter.getResources(j_resources);

				PlayerState savePlayerState = game.getActivePlayer().getState();

				boolean hasRightToPlayDevelopmentCard = checkRightToPlayCard();
				boolean hasValidMonopolyCard = checkPlayableDevelopmentCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY);
				boolean rightPlentyValue = rightPlentyValue(resources);
				boolean bankHasEnoughResources = checkBankResources(resources);

				if (hasRightToPlayDevelopmentCard) {
					if (hasValidMonopolyCard) {
						// 0<resources<=2
						if (rightPlentyValue) {
							if (bankHasEnoughResources) {
								playPlentyCard();
								sendPlentyCardMessage(j_resources);
								plentyYearComes(resources);
								sendOK();
								setPlayerStateAndSendMessage(savePlayerState, gameActivePlayerId);
							} else {
								sendNoRightErrorInfoToClient(getBundle().getString("NO_RESOURCES_IN_BANK"));
							}
						} else {
							sendNoRightErrorInfoToClient(getBundle().getString("CAN_MORE_RESOURCES"));
						}
					} else {
						sendNoRightErrorInfoToClient(getBundle().getString("NO_PLENTY_YEAR_CARD"));
					}
				} else {
					sendNoRightErrorInfoToClient(getBundle().getString("CANT_PLAY_CARD"));
				}
			}
			break;

			case SEND_CHATMESSAGE: {
				String message = (String) obj.get(Attributes.MESSAGE.toString());

				if (isCheatCode(message) && gameStarted) {
					CheatCodes cheatCode = getCheatCode(message);
					applyCheatCode(cheatCode);
					sendOK();
				} else {
					JSONObject j_message = new JSONObject();
					j_message.put(Attributes.SENDER.toString(), serverConnectionId);
					j_message.put(Attributes.MESSAGE.toString(), message);
					serverConnection.broadcast(ClientTypes.CHATMESSAGE.toString(), j_message);
					sendOK();
				}
			}
			break;
			}
		}
	}

	/**
	 * Buy one {@code DevelopmentCard} from {@code Bank} and send message to all players
	 * @see DevelopmentCard
	 * @see PlayableDevelopmentCard
	 * @see Player
	 */
	private void buyDevelopmentCardAndSendMessage() {
		DevelopmentCard developmentCard = game.getBank().drawDevelopmentCard();

		if (developmentCard instanceof PlayableDevelopmentCard) {
			// the turnNumber will be saved and used for checking, whether the development
			// card has been bought before
			// e.g.: development card has been bought in turnNumber = 1, then this card can
			// be used only in turnNumber > 1.
			((PlayableDevelopmentCard) developmentCard).setDrawnTurnNumber(game.getRoundCount());
		}

		Resources cost = DevelopmentCard.getCost();

		game.getActivePlayer().getResources().subtract(cost);
		game.getBank().getResources().add(cost);
		sendLoseResourcesInfoToAll(cost, gameActivePlayerId);
		game.getActivePlayer().addDevelopmentCard(developmentCard);
		game.getActivePlayer().calculateNumberOfDevelopmentCards();
		sendDevelopmentCardMessageToAll(developmentCard, serverConnectionId);
	}

	/**
	 * Checks whether the supply and demand share a {@code ResourceType}.
	 * @param supply the supplied {@link Resources} of a {@link Trade}.
	 * @param demand the demanded resources of a trade.
	 * @return true, if the supply and demand share a {@link ResourceType}. false, otherwise.
	 */
	private boolean haveSameResourceType(Resources supply, Resources demand) {
		for (ResourceType type : ResourceType.values()) {
			boolean supplyContainsResourceType = supply.getResources().get(type) != 0;
			boolean demandContainsResourceType = demand.getResources().get(type) != 0;

			if (supplyContainsResourceType && demandContainsResourceType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the supplied and demanded {@code Resources} are in the correct {@code TradingRatios}.
	 * @param tradingRatios 
	 *                    the {@link TradingRatios} to compare the supply and demand to.
	 * @param supply 
	 *                    the supplied {@link Resources}.
	 * @param demand 
	 *                    the demanded resources.
	 * @return true, if the supplied and demanded resources are in the correct ratio. false, otherwise.
	 */
	private boolean isRatioCorrect(TradingRatios tradingRatios, Resources supply, Resources demand) {
		int nDemandedResources = demand.getSum();
		int nDemandedResourcesForCorrectRatio = 0;

		// Check for each individual resource if it was supplied in the correct ratio.
		for (ResourceType type : supply.getResources().keySet()) {
			if(supply.getResources().get(type)!=0) {
				if (tradingRatios.isCorrectRatio(type, supply.getResources().get(type))) {
					int amountOfTrades = supply.getResources().get(type) / tradingRatios.getRatios().get(type);
					nDemandedResourcesForCorrectRatio += amountOfTrades;
				} else {
					return false;
				}
			}
		}

		// Check for all resources combined if they were supplied in the correct ratio.
		return nDemandedResources == nDemandedResourcesForCorrectRatio;
	}

	/**
	 * Determines the cheat code represented by the specified message.
	 * 
	 * @param message
	 *            the message representing a cheat code.
	 * @return the cheat code.
	 * @see CheatCodes
	 */
	private CheatCodes getCheatCode(String message) {
		for (CheatCodes cheatCode : CheatCodes.values()) {
			if (message.equals(cheatCode.toString())) {
				return cheatCode;
			}
		}
		return null;
	}

	/**
	 * Applies the specified cheat.
	 * 
	 * @param cheat
	 *            the cheat to apply.
	 * @see CheatCodes
	 */
	private void applyCheatCode(CheatCodes cheat) {
		switch (cheat) {
		case RESOURCES:
			Resources newResources = new Resources(ResourceType.BRICK, 5, ResourceType.GRAIN, 5, ResourceType.LUMBER, 5,
					ResourceType.ORE, 5, ResourceType.WOOL, 5);
			activePlayer.getResources().add(newResources);
			sendGetResourcesInfoToAll(newResources, activePlayer.getId());
			break;

		case WIN:
			winTheGameAndsendWinMessageToALL();
			server.stopRunning();
			break;

		case LOSE:
			Player winner = game.getPlayerWithMostPointsOtherThan(activePlayer);
			winTheGameAndsendWinMessageToALL(winner);
			server.stopRunning();
			break;

		case DEVELOPMENT_CARDS:
			// Adds one playable development card of each type.
			for (PlayableDevelopmentCardType cardType : PlayableDevelopmentCardType.values()) {
				PlayableDevelopmentCard card = new PlayableDevelopmentCard(cardType);
				activePlayer.addDevelopmentCard(card);
				sendDevelopmentCardMessageToAll(card, activePlayer.getId());
			}
			break;

		case ROBBER:
			setPlayerStateAndSendMessage(PlayerState.MOVE_ROBBER, gameActivePlayerId);
			break;

		case DEVELOPMENT_CARDS_FOR_ALL:
			// Adds one playable development card of each type to all players.
			for (Player player : game.getPlayers()) {
				for (PlayableDevelopmentCardType cardType : PlayableDevelopmentCardType.values()) {
					PlayableDevelopmentCard card = new PlayableDevelopmentCard(cardType);
					player.addDevelopmentCard(card);
					sendDevelopmentCardMessageToAll(card, player.getId());
				}
			}
			break;
		}
	}

	/**
	 * Checks if a message is a cheat code or not.
	 * 
	 * @param message
	 *            the message which is potentially a cheat code.
	 * @return true, if the message is a cheat code. false, otherwise.
	 * @see CheatCodes
	 */
	private boolean isCheatCode(String message) {
		for (CheatCodes cheatCode : CheatCodes.values()) {
			if (message.equals(cheatCode.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the longest {@code Road} in game is broken.
	 * 
	 * @param settlementPosition
	 *            {@code Corner} which the settlement is builded.
	 * @return true, if the current longest road is broken
	 */
	private boolean isLongestRoadBroken(Corner settlementPosition) {
		int countRoads = 0;
		Map<Integer, Integer> roads = new HashMap<>();

		for (Edge edge : settlementPosition.getAdjacentEdgesOfCorner()) {
			if (edge.getRoad() != null) {
				int playerId = edge.getRoad().getOwner().getId();
				if (playerId != gameActivePlayerId) {
					countRoads = countRoads + 1;
					roads.put(playerId, countRoads);
					// if one player in list has more than one roads near this corner, then the road
					// will be broken
					// only one player's road can be broken, because there are max three roads near
					// one corner
					// e.g. player 3 has builded a settlement on corner then the road of player 1 is
					// broken
					// *
					// * road of player 1 road of player 1
					// * | |
					// * | |
					// * corner(no settlement) -------> settlement of player 3
					// * / \ / \
					// * / \ / \
					// * road of player 1 road of player 2 road of player 1 road of player 2
					// *
					if (roads.get(playerId) > 1) {
						game.getPlayerWithId(playerId).updateLongestRoadLengthProperty();
						if (game.getLongestRoadCard().getOwner() != null) {
							if (game.getLongestRoadCard().getOwner().getId() == playerId) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Sends a message to the all client, saying, active {@code Player} has got the
	 * longest road card.
	 * 
	 * @param playerId
	 *            the Id of player, who get the longest card in {@code Game}.
	 * @see SpecialCardType
	 */
	private void sendLongestRoadMessage(int playerId) {
		JSONObject j_longestRoad = new JSONObject();
		j_longestRoad.put(Attributes.PLAYER.toString(), playerId);
		serverConnection.broadcast(ClientTypes.LONGEST_ROAD.toString(), j_longestRoad);
	}

	/**
	 * Sends a message to the all client, saying, the {@code Player} with id lose
	 * the longest road card.
	 * 
	 * @param id
	 *            the id of player, who lose the longest road card.
	 * @see SpecialCardType
	 */
	private void sendLoseLongestRoadCardMessage(int id) {
		JSONObject j_loseLongestRoad = new JSONObject();
		serverConnection.broadcast(ClientTypes.LONGEST_ROAD.toString(), j_loseLongestRoad);
	}

	/**
	 * Check whether active {@code Player} has the longest {@code Road} in
	 * {@code Game}.
	 * 
	 * @return true if this player has builded the longest road in game.
	 * @see Game
	 * @see Player
	 */
	private boolean checkLongestRoad() {
		int longestRoadLength = game.getActivePlayer().getLongestRoadLength();
		int minBuildedRoadsForLongestRoad = 5;
		// the linked roads length >=5
		if (longestRoadLength >= minBuildedRoadsForLongestRoad) {
			// ??is a leer property null?
			// there is one player, who has got the longest road card before
			if (game.getLongestRoadCard().getOwner() != null) {
				// this player is not the active player
				if (!game.getActivePlayer().hasLongestRoadCard()) {
					// active player has the longest road in game
					if (game.getLongestRoadCard().getOwner().getLongestRoadLength() < longestRoadLength) {
						return true;
					}
					// active player has not the longest road in game
					else {
						return false;
					}
				} else {
					// here return false means server do not change the player, who has the largest
					// army
					return false;
				}
			}
			// active player has first builded the longest road
			else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * Sends a message to the all client, saying, active {@code Player} has got the
	 * largest army card in {@code Game}.
	 */
	private void sendLargestArmyMessage() {
		JSONObject j_largestArmy = new JSONObject();
		j_largestArmy.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		serverConnection.broadcast(ClientTypes.LARGEST_ARMY.toString(), j_largestArmy);
	}

	/**
	 * Check whether active {@code Player} has played the most knight cards in
	 * {@code Game}.
	 * 
	 * @return true if the active {@code Player} has played the most knight cards in
	 *         {@code Game}.
	 * @see Player
	 * @see Game
	 * @see SpecialCardType
	 */
	private boolean checkLargestArmy() {
		int playedKnightCards = game.getActivePlayer().getPlayedKnightCards();
		int minPlayedKnightCardsForLargestArmy = 3;
		// active player has played three or more knight cards
		if (playedKnightCards >= minPlayedKnightCardsForLargestArmy) {
			// ??is a leer property null,or a Player?
			// there is one player, who has already got the largest army card in game
			if (game.getLargestArmyCard().getOwner() != null) {
				// active player has not the largest army card
				if (!game.getActivePlayer().hasLargestArmyCard()) {
					// active player has played more knight cards in game
					if (game.getLargestArmyCard().getOwner().getPlayedKnightCards() < playedKnightCards) {
						return true;
					} else {
						return false;
					}
				} else {
					// here return false means server do not change the player, who has the largest
					// army
					return false;
				}
			}
			// active player is the first player, who has played three knight cards in game
			else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * Gives the longest road card to the specified {@code Player}.
	 * 
	 * @param newOwner
	 *            the {@link Player} receiving the longest road card.
	 * @see SpecialCard
	 * @see SpecialCardType
	 */
	private void giveLongestRoadCardTo(Player newOwner) {
		Player previousOwner = game.getLongestRoadCard().getOwner();

		// Change ownership of the Longest Road Card.
		game.getLongestRoadCard().giveTo(newOwner);

		// Update victory points.
		if (previousOwner != null) {
			previousOwner.updateVictoryPoints();
		}
		// there is method checkWin() follows this method and victoryPoints will update.
		// newOwner.updateVictoryPoints();
	}

	/**
	 * Gives the largest army card to the specified {@code Player}.
	 * 
	 * @param newOwner
	 *            the {@link Player} receiving the largest army card.
	 * @see SpecialCard
	 * @see SpecialCardType
	 */
	private void giveLargestArmyCardTo(Player newOwner) {
		Player previousOwner = game.getLargestArmyCard().getOwner();

		// Change ownership of the Longest Road Card.
		game.getLargestArmyCard().giveTo(newOwner);

		// Update victory points.
		if (previousOwner != null) {
			previousOwner.updateVictoryPoints();
		}
		// there is method checkWin() follow this method, don't need double check.
		/* newOwner.updateVictoryPoints(); */
	}

	/**
	 * Trigger the effect of plenty year card.
	 * 
	 * @param resources
	 *            the resources to be added for active player
	 */
	private void plentyYearComes(Resources resources) {
		game.getBank().getResources().subtract(resources);
		game.getActivePlayer().getResources().add(resources);
		sendGetResourcesInfoToAll(resources, gameActivePlayerId);
	}

	/**
	 * Check whether the {@code Bank} has enough {@code Resources}.
	 * 
	 * @param resources
	 *            the resources to be subtracted from {@code Bank}
	 * @return true if the bank has enough resources
	 */
	private boolean checkBankResources(Resources resources) {
		return game.getBank().getResources().isGreaterThanOrEqualTo(resources);
	}

	/**
	 * Check whether the resources value follow the rules.
	 * 
	 * @param resources
	 *            the quantity of this resources should greater than zero and less
	 *            or equal to two
	 * @return true if the quantity of resources greater than zero and less or equal
	 *         to two
	 */
	private boolean rightPlentyValue(Resources resources) {
		int rightPlentyValue = 2;
		return (resources.getSum() <= rightPlentyValue) && (resources.getSum() > 0);
	}

	/**
	 * Sends a message to the all client, saying, active {@code Player} has played
	 * the plenty year card in {@code Game} and get resources from {@code Bank}.
	 * 
	 * @param j_resources
	 *            the JSONObject of resources, which should be given to active
	 *            {@code Player}.
	 */
	private void sendPlentyCardMessage(JSONObject j_resources) {
		JSONObject j_plenty_card = new JSONObject();
		j_plenty_card.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		j_plenty_card.put(Attributes.RESOURCES.toString(), j_resources);
		serverConnection.broadcast(ClientTypes.PLAY_YEAR_OF_PLENTY.toString(), j_plenty_card);
	}

	/**
	 * Remove plenty year card from list of development card of active
	 * {@code Player}.
	 */
	private void playPlentyCard() {
		List<DevelopmentCard> cards = game.getActivePlayer().getDevelopmentCards();
		for (int i = 0; i < cards.size(); i++) {
			DevelopmentCard card = cards.get(i);
			if (card instanceof PlayableDevelopmentCard) {
				if (((PlayableDevelopmentCard) card).getType() == PlayableDevelopmentCardType.YEAR_OF_PLENTY) {
					if (((PlayableDevelopmentCard) card).canPlayAtThisTurn(game.getRoundCount())) {
						game.getActivePlayer().setHasPlayedDevelopmentCardThisTurn();
						cards.remove(card);
						game.getActivePlayer().minusDevelopmentCardNumber();
						game.getActivePlayer().minusCountYearOfPlentyCards();
					}
				}
			}
		}
	}

	/**
	 * Sends a message to the all client, saying, active {@code Player} has played
	 * monopoly card in {@code Game}.
	 * 
	 * @param resourceType
	 *            the type to be chosen by active {@code Player}
	 */
	private void sendPlayMonopolyCardMessage(String resourceType) {
		JSONObject j_monopoly_card = new JSONObject();
		j_monopoly_card.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		j_monopoly_card.put(Attributes.RESOURCE.toString(), resourceType);
		serverConnection.broadcast(ClientTypes.PLAY_MONOPOLY.toString(), j_monopoly_card);
	}

	/**
	 * Remove monopoly card from list of development card of active {@code Player}.
	 */
	private void playMonopolyCard() {
		List<DevelopmentCard> cards = game.getActivePlayer().getDevelopmentCards();
		for (int i = 0; i < cards.size(); i++) {
			DevelopmentCard card = cards.get(i);
			if (card instanceof PlayableDevelopmentCard) {
				if (((PlayableDevelopmentCard) card).getType() == PlayableDevelopmentCardType.MONOPOLY) {
					if (((PlayableDevelopmentCard) card).canPlayAtThisTurn(game.getRoundCount())) {
						game.getActivePlayer().setHasPlayedDevelopmentCardThisTurn();
						cards.remove(card);
						game.getActivePlayer().minusDevelopmentCardNumber();
						game.getActivePlayer().minusCountMonopolyCards();
					}
				}
			}
		}
	}

	/**
	 * Trigger the effect of monopoly card.
	 * 
	 * @param resourceType
	 *            the type to be chosen by active {@code Player}
	 */
	private void monopolizeMarket(ResourceType resourceType) {
		Integer gain = 0;
		Map<Integer, Integer> resourceList = new HashMap<>();
		for (Player player : game.getPlayers()) {
			if (player.getId() != gameActivePlayerId) {
				Integer subtractResource = player.getResources().getResources().get(resourceType);
				if (subtractResource != 0) {
					player.getResources().subtractOneTypeResource(subtractResource, resourceType);
					resourceList.put(player.getId(), subtractResource);
					gain = gain + subtractResource;
				}
			}
		}
		for (Integer i : resourceList.keySet()) {
			broadcastLoseResourcesMessage(new Resources(resourceType, resourceList.get(i)), i);
		}
		game.getActivePlayer().getResources().addOneTypeResource(gain, resourceType);
		broadcastGetResourcesMessage(new Resources(resourceType, gain), gameActivePlayerId);
	}

	/**
	 * Sends a message to all client, saying, active {@code Player} build two roads
	 * with build road card.
	 * 
	 * @param position_1
	 *            the first position of builded {@code Road}
	 * @param position_2
	 *            the second position of builded {@code Road}
	 * @see Edge
	 * @see Road
	 */
	private void sendPlayRoadCardMessage(JSONArray position_1, JSONArray position_2) {
		JSONObject j_road_card = new JSONObject();
		j_road_card.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		j_road_card.put(Attributes.STREAT_ONE.toString(), position_1);
		j_road_card.put(Attributes.STREAT_TWO.toString(), position_2);
		serverConnection.broadcast(ClientTypes.PLAY_ROAD_BUILDING.toString(), j_road_card);
	}

	/**
	 * Sends a message to all client, saying, active {@code Player} build one
	 * {@code Road} with build road card.
	 * 
	 * @param position
	 *            the position of builded {@code Road}
	 * @see Edge
	 * @see Road
	 */
	private void sendPlayRoadCardMessage(JSONArray position) {
		JSONObject j_road_card = new JSONObject();
		j_road_card.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		j_road_card.put(Attributes.STREAT_ONE.toString(), position);
		serverConnection.broadcast(ClientTypes.PLAY_ROAD_BUILDING.toString(), j_road_card);
	}

	/**
	 * Build a {@code Settlement} with no cost and send the build message to all.
	 * 
	 * @param settlementPosition
	 *            the position of builded {@code Settlement}
	 * @see Corner
	 * @see Settlement
	 */
	private void buildFreeSettlementAndSendMessage(Corner settlementPosition) {
		Settlement newSettlement = new Settlement(game.getActivePlayer(), settlementPosition);
		newSettlement.setPosition(settlementPosition);
		settlementPosition.setLocality(newSettlement);

		if (secondRoundToFreeBuild) {
			newSettlement.setisSecondRoundBuilded(true);
		}

		game.getBoard().addConstruction(newSettlement);
		game.getActivePlayer().addLocality(newSettlement);
		game.getActivePlayer().updateVictoryPoints();

		sendBuildInfoToAll(newSettlement);
	}

	/**
	 * Build a {@code Road} with build road card and send the build message to all.
	 * 
	 * @param roadPosition
	 *            the position of builded {@code Road}
	 * @see Edge
	 * @see Road
	 * @see PlayableDevelopmentCardType
	 */
	private void buildRoadWithCardAndSendMessage(Edge roadPosition) {
		Road newRoad = new Road(game.getActivePlayer(), roadPosition);
		game.getBoard().addConstruction(newRoad);
		game.getActivePlayer().addRoad(newRoad);
		roadPosition.setRoad(newRoad);
		game.getActivePlayer().updateLongestRoadLengthProperty();

		sendBuildInfoToAll(newRoad);
	}

	/**
	 * Build a {@code Road} with no cost and send the build message to all.
	 * 
	 * @param roadPosition
	 *            the position of builded {@code Road}
	 * @see Edge
	 * @see Road
	 */
	private void buildFreeRoadAndSendMessage(Edge roadPosition) {
		Road newRoad = new Road(game.getActivePlayer(), roadPosition);
		game.getBoard().addConstruction(newRoad);
		game.getActivePlayer().addRoad(newRoad);
		roadPosition.setRoad(newRoad);

		sendBuildInfoToAll(newRoad);
	}

	/**
	 * Check whether {@code Player} has one more road to be builded.
	 * 
	 * @return true, if player has builded less than 15 roads.
	 */
	private boolean checkRoadNumber() {
		if (game.getActivePlayer().getCountRoad() < Player.getMaxRoad()) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether {@code Player} can build two or more roads.
	 * 
	 * @return true, if player has builded less than 14 roads.
	 */
	private boolean checkMoreThanTwoRoadsCanBeBuilded() {
		if (game.getActivePlayer().getCountRoad() <= Player.getMaxRoad() - 2) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether {@code Player} has playable development card.
	 * 
	 * @param type
	 *            the type of development card
	 * @return true, if the player has one playable development card.
	 */
	private boolean checkPlayableDevelopmentCard(PlayableDevelopmentCardType type) {
		if (game.getActivePlayer().developmentCardsProperty().getSize() > 0) {
			for (DevelopmentCard developmentCard : game.getActivePlayer().getDevelopmentCards()) {
				if (developmentCard instanceof PlayableDevelopmentCard) {
					if (((PlayableDevelopmentCard) developmentCard).getType() == type) {
						if (((PlayableDevelopmentCard) developmentCard).canPlayAtThisTurn(game.getRoundCount())) {
							if (!(((PlayableDevelopmentCard) developmentCard).hasBeenPlayed())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Remove build road card from list of development card of active
	 * {@code Player}.
	 */
	private void playRoadCard() {
		List<DevelopmentCard> cards = game.getActivePlayer().getDevelopmentCards();
		for (int i = 0; i < cards.size(); i++) {
			DevelopmentCard card = cards.get(i);
			if (card instanceof PlayableDevelopmentCard) {
				if (((PlayableDevelopmentCard) card).getType() == PlayableDevelopmentCardType.ROAD_BUILDING) {
					if (((PlayableDevelopmentCard) card).canPlayAtThisTurn(game.getRoundCount())) {
						game.getActivePlayer().setHasPlayedDevelopmentCardThisTurn();
						((PlayableDevelopmentCard) card).setHasBeenPlayed();
						cards.remove(card);
						game.getActivePlayer().minusDevelopmentCardNumber();
						game.getActivePlayer().minusCountRoadBuildingCards();
					}
				}
			}
		}
	}

	/**
	 * Sends a message to all client, saying, active {@code Player} has played
	 * knight card and set one target player.
	 * 
	 * @param position
	 *            the new robber position
	 * @param targetPlayerId
	 *            the id of player who is set as target player.
	 */
	private void sendKnightCardMessageToAll(JSONObject position, int targetPlayerId) {
		JSONObject j_KnightCard = new JSONObject();
		j_KnightCard.put(Attributes.POSITION.toString(), position);
		j_KnightCard.put(Attributes.TARGET.toString(), targetPlayerId);
		j_KnightCard.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		serverConnection.broadcast(ClientTypes.PLAY_KNIGHT.toString(), j_KnightCard);
	}

	/**
	 * Sends a message to all client, saying, active {@code Player} has played
	 * knight card without target player.
	 * 
	 * @param position
	 *            the new robber position
	 */
	private void sendKnightCardWithoutTargetPlayerMessageToAll(JSONObject position) {
		JSONObject j_KnightCard = new JSONObject();
		j_KnightCard.put(Attributes.POSITION.toString(), position);
		j_KnightCard.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		serverConnection.broadcast(ClientTypes.PLAY_KNIGHT.toString(), j_KnightCard);
	}

	/**
	 * Remove knight card from list of development card of active {@code Player}.
	 */
	private void playKnightCard() {
		List<DevelopmentCard> cards = game.getActivePlayer().getDevelopmentCards();
		for (int i = 0; i < cards.size(); i++) {
			DevelopmentCard card = cards.get(i);
			if (card instanceof PlayableDevelopmentCard) {
				if (((PlayableDevelopmentCard) card).getType() == PlayableDevelopmentCardType.KNIGHT) {
					if (((PlayableDevelopmentCard) card).canPlayAtThisTurn(game.getRoundCount())) {
						game.getActivePlayer().setHasPlayedDevelopmentCardThisTurn();
						cards.remove(card);
						game.getActivePlayer().playedOneMoreKnightCard();
						game.getActivePlayer().minusDevelopmentCardNumber();
						game.getActivePlayer().minusCountKnightCards();
					}
				}
			}
		}
	}

	/**
	 * Check whether the active {@code Player} can play the knight card.
	 * 
	 * @return true, if the player in state build and trade.
	 */
	private boolean checkRightToPlayCard() {
		if (game.getActivePlayer().getState() == PlayerState.TRADE_OR_BUILD || game.getActivePlayer().getState() == PlayerState.ROLL_DICE) {
			if (!game.getActivePlayer().hasPlayedDevelopmentCardThisTurn()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sends a message to all clients, saying, the {@code Trade} has been canceled.
	 * 
	 * @param tradeId
	 *            the ID of {@link Trade}.
	 * @param playerId
	 *            the ID of the {@link Player} who started the trade.
	 */
	private void sendCancelMessage(int tradeId, int playerId) {
		JSONObject j_cancelMessage = new JSONObject();
		j_cancelMessage.put(Attributes.PLAYER.toString(), playerId);
		j_cancelMessage.put(Attributes.TRADING_ID.toString(), tradeId);
		serverConnection.broadcast(ClientTypes.TRADE_CANCELED.toString(), j_cancelMessage);
	}

	/**
	 * Sends a message to all client, saying, the {@code Trade} has been executed.
	 * 
	 * @param fellowPlayerId
	 *            the player who make trade with active {@code Player}
	 */
	private void sendExecuteTradeMessage(int fellowPlayerId) {
		JSONObject j_executeTradeMessage = new JSONObject();
		j_executeTradeMessage.put(Attributes.TRADING_ID.toString(), game.getTrade().getTradeID());
		j_executeTradeMessage.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		j_executeTradeMessage.put(Attributes.FELLOW_PLAYER.toString(), fellowPlayerId);

		JSONObject message = new JSONObject();
		message.put(ClientTypes.TRADE_EXECUTED.toString(), j_executeTradeMessage);
		serverConnection.broadcast(message);
	}

	/**
	 * The {@code Trade} is executed between active {@code Player} and fellow
	 * player.
	 * 
	 * @param tradeId
	 *            the ID of this Trade
	 * @param fellowPlayerId
	 *            the ID of fellow player
	 */
	private void executeTrade(int tradeId, int fellowPlayerId) {
		if (game.getTrade() != null) {
			if (game.getTrade().getAcceptedList().contains(game.getPlayerWithId(fellowPlayerId))) {
				if ((!game.getTrade().getDeclinedList().contains(game.getPlayerWithId(fellowPlayerId)))) {
					Trade tradeInGame = game.getTrade();
					Resources supply = tradeInGame.getTradeOffer();
					Resources demand = tradeInGame.getTradeRequest();
					game.getActivePlayer().getResources().subtract(supply);
					broadcastLoseResourcesMessage(supply, gameActivePlayerId);
					game.getPlayerWithId(fellowPlayerId).getResources().add(supply);
					broadcastGetResourcesMessage(supply, fellowPlayerId);
					game.getPlayerWithId(fellowPlayerId).getResources().subtract(demand);
					broadcastLoseResourcesMessage(demand, fellowPlayerId);
					game.getActivePlayer().getResources().add(demand);
					broadcastGetResourcesMessage(demand, gameActivePlayerId);

					sendExecuteTradeMessage(fellowPlayerId);
					game.setTrade(null);
				} else {
					String name = game.getPlayerWithId(fellowPlayerId).getName();
					sendNoRightErrorInfoToClient("Der Spieler " + name + " hat dieses Handel abgebrochen");
				}
			} else {
				String name = game.getPlayerWithId(fellowPlayerId).getName();
				sendNoRightErrorInfoToClient("Der Spieler " + name + " hat dieses Handel nicht akzeptiert.");
			}
		} else {
			sendNoRightErrorInfoToClient("Es gibt kein Handel");
		}
	}

	/**
	 * Make a {@code Trade} offer.
	 * 
	 * @param tradeId
	 *            the ID of this trade
	 * @param supply
	 *            the suppled {@code Resources} of this trade
	 * @param demand
	 *            the demanded {@code Resources} of this trade
	 * @see Trade
	 * @see Resources
	 * @see ResourceType
	 */
	private void makeTradeOffer(int tradeId, Resources supply, Resources demand) {
		Trade trade = new Trade(tradeId, supply, demand, game.getActivePlayer());
		game.setTrade(trade);
	}

	/**
	 * Sends a message to all client, saying if the {@code Trade} has been accepted
	 * or not.
	 * 
	 * @param trade_id
	 *            the ID of this trade
	 * @param accepted
	 *            true, if the {@link Trade} has been accepted. false, otherwise.
	 */
	private void sendAcceptMessage(int trade_id, boolean accepted) {
		JSONObject j_accept = new JSONObject();
		j_accept.put(Attributes.FELLOW_PLAYER.toString(), activePlayer.getId());
		j_accept.put(Attributes.TRADING_ID.toString(), trade_id);
		j_accept.put(Attributes.ACCEPT.toString(), accepted);
		serverConnection.broadcast(ClientTypes.TRADE_ACCEPTED.toString(), j_accept);
	}

	/**
	 * Sends a message to all client, saying, the details of one {@code Trade}.
	 * 
	 * @param id
	 *            the ID of this Trade
	 * @param supply
	 *            the supplied {@code Resources} of this trade
	 * @param demand
	 *            the demanded {@code Resources} of this trade
	 * @see Trade
	 * @see Resources
	 * @see ResourceType
	 */
	private void sendSupplyMessage(int id, Resources supply, Resources demand) {
		JSONObject j_Trade_offer = new JSONObject();
		JSONObject j_supply = new Mapper().writeValueAsJson(supply);
		JSONObject j_demand = new Mapper().writeValueAsJson(demand);

		j_Trade_offer.put(Attributes.PLAYER.toString(), gameActivePlayerId);
		j_Trade_offer.put(Attributes.TRADING_ID.toString(), id);
		j_Trade_offer.put(Attributes.DEMAND.toString(), j_demand);
		j_Trade_offer.put(Attributes.SUPPLY.toString(), j_supply);
		serverConnection.broadcast(ClientTypes.TRADE_OFFER.toString(), j_Trade_offer);

	}

	/**
	 * Make ID of one {@code Trade}
	 * 
	 * @return the ID of trade
	 * @see Trade
	 */
	private int idMaker() {
		tradeID++;
		return tradeID;
	}

	/*
	 * private void setBackPlayerState() {
	 * activePlayer.setBackhasAsedFinischRoundTwice();
	 * activePlayer.setBackHasUsedDevelopmentCardThisTurn();
	 * setPlayerStateAndSendMessage(PlayerState.WAIT,gameActivePlayerId); }
	 * 
	 * private boolean checkIfPlayerhasUsedDevelopmentCardThisTurn() {
	 * if(activePlayer.getNumberOfAllDevelopmentCard()!=0) { return
	 * activePlayer.hasUsedDevelopmentCardThisTurn(); } return false; }
	 */

	// this can be used for ai
	/*
	 * private boolean checkStillResourceToUse() { Resources playerResource =
	 * activePlayer.getResources(); Resources costTobuildRoad = Road.getCost();
	 * Resources costTobuildCity = City.getCost(); Resources costBuyDevelopmentCard
	 * = DevelopmentCard.getCost(); Resources costTobuildSettlement =
	 * Settlement.getCost();
	 * 
	 * boolean output = false;
	 * 
	 * if(playerResource.isGreaterThanOrEqualTo(costTobuildRoad)&&activePlayer.
	 * getCountRoad()<=activePlayer.getMaxRoad()) { output = true; }
	 * if(playerResource.isGreaterThanOrEqualTo(costTobuildCity)&&activePlayer.
	 * getCountCity()<=activePlayer.getMaxCity()) { output = true; }
	 * if(playerResource.isGreaterThanOrEqualTo(costTobuildSettlement)&&activePlayer
	 * .getCountSettlement()<=activePlayer.getMaxSettlement()) { output = true; }
	 * if(playerResource.isGreaterThanOrEqualTo(costBuyDevelopmentCard)&&game.
	 * getBank().getCountDevelopmentCards()>0) { output = true; } return output; }
	 */

	/**
	 * Check whether active {@code Player} can finish the round.
	 * 
	 * @return true, if the active player can finish the round.
	 */
	private boolean checkRightToFinishRound() {
		return activePlayer.getState() == PlayerState.TRADE_OR_BUILD && game.getTrade() == null;
	}

	/**
	 * Change {@code Resources} with {@code Bank} in {@code Game}.
	 * 
	 * @param supply
	 *            the supplied resources
	 * @param demand
	 *            the demanded resources
	 * @see Resources
	 * @see ResourceType
	 * @see Bank
	 * @see Game
	 */
	private void seaTrad(Resources supply, Resources demand) {
		game.getActivePlayer().getResources().subtract(supply);
		game.getBank().getResources().add(supply);
		game.getBank().getResources().subtract(demand);
		game.getActivePlayer().getResources().add(demand);
	}

	/**
	 * Check whether {@code Player} has enough {@code Resources}.
	 * 
	 * @param resourceOwned
	 *            the resources which is owned by active {@code Player}
	 * @param cost
	 *            the resources which is needed to be used
	 * @return true, if player has enough resource to pay the cost.
	 */
	private boolean hasEnoughResources(Resources resourceOwned, Resources cost) {
		return resourceOwned.isGreaterThanOrEqualTo(cost);
	}

	/**
	 * Check whether {@code Bank} has enough resource for one sea trade.
	 * 
	 * @param demand
	 *            the demanded {@code Resources} which {@code Player} want get from
	 *            bank
	 * @return true, if the {@code Bank} has enough resources.
	 * @see Bank
	 * @see Player
	 * @see Resources
	 */
	private boolean checkBankHasEnoughResourceToTrad(Resources demand) {
		if (hasEnoughResources(game.getBank().getResources(), demand)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether {@code Player} has enough {@code Resources} to make a sea
	 * trade.
	 * 
	 * @param supply
	 *            the supplied {@code Resources} from one sea trade
	 * @return true, if the {@code Player} has enough resources to make a offer.
	 * @see Bank
	 * @see Player
	 * @see Resources
	 */
	private boolean checkPlayerHasEnoughrResourceToTrad(Resources supply) {

		if (hasEnoughResources(activePlayer.getResources(), supply)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether the active {@code Player} can make a sea trade.
	 * 
	 * @return true, if player state is "Build and Trade".
	 */
	private boolean hasRightForSeaTrad() {
		if (activePlayer.getState() == PlayerState.TRADE_OR_BUILD) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sends a message to all client, saying, which development card has been bought
	 * by active {@code Player}.
	 * 
	 * @param developmentCard
	 *            the development card which has been bought by active
	 *            {@link Player}
	 * @param playerId the ID of the player
	 * @see VictoryPointCard
	 * @see SpecialCardType
	 * @see PlayableDevelopmentCardType
	 */
	private void sendDevelopmentCardMessageToAll(DevelopmentCard developmentCard, int playerId) {
		JSONObject j_developmentCard = new JSONObject();
		if (developmentCard instanceof PlayableDevelopmentCard) {
			j_developmentCard.put(Attributes.DEVELOMENTCARD.toString(),
					((PlayableDevelopmentCard) developmentCard).getType().toString());
		} else {
			j_developmentCard.put(Attributes.DEVELOMENTCARD.toString(), Attributes.VICTORY_POINT.toString());
		}
		j_developmentCard.put(Attributes.PLAYER.toString(), playerId);

		JSONObject j_unknowndevelopmentCard = new JSONObject();
		j_unknowndevelopmentCard.put(Attributes.PLAYER.toString(), playerId);
		j_unknowndevelopmentCard.put(Attributes.DEVELOMENTCARD.toString(), Attributes.UNKNOWN.toString());

		serverConnection.sendToClient(ClientTypes.DEVELOMENTCARD_BOUGHT.toString(), j_developmentCard);
		serverConnection.sendToEverybodyElse(ClientTypes.DEVELOMENTCARD_BOUGHT.toString(), j_unknowndevelopmentCard);
	}

	/**
	 * Check whether active {@code Player} can buy one development card.
	 * 
	 * @return true, if active {@code Player} can buy one development card
	 * @see VictoryPointCard
	 * @see SpecialCardType
	 * @see PlayableDevelopmentCardType
	 */
	private boolean hasRightToBuyDevelopmentCard() {
		if (activePlayer.getState() == PlayerState.TRADE_OR_BUILD) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether active {@code Player} has enough {@code Resources} to buy one
	 * development card.
	 * 
	 * @return true, if active player has enough to buy one development card
	 * @see VictoryPointCard
	 * @see SpecialCardType
	 * @see PlayableDevelopmentCardType
	 */
	private boolean hasEnoughResourcesToBuyDevelopmentCards() {
		return activePlayer.getResources().isGreaterThanOrEqualTo(DevelopmentCard.getCost());
	}

	/**
	 * Check whether {@code Bank} has still development card.
	 * 
	 * @return true, if {@code Bank} has still development card
	 * @see VictoryPointCard
	 * @see SpecialCardType
	 * @see PlayableDevelopmentCardType
	 */
	private boolean isThereStillAnyDevelopmentcard() {
		return game.getBank().getDevelopmentCards().size() > 0;
	}

	/**
	 * Check whether {@code Player} has builded already 4 settlements.
	 * 
	 * @return true, if player has builded already 4 settlements
	 * @see Settlement
	 */
	private boolean hasMaxSettlement() {
		if (activePlayer.getCountSettlement() >= Player.getMaxSettlement()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether {@code Player} has builded already 3 cities.
	 * 
	 * @return true, if player has builded already 3 cities
	 * @see City
	 */
	private boolean hasMaxCity() {
		if (activePlayer.getCountCity() >= Player.getMaxCity()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether {@code Player} has builded already 15 roads.
	 * 
	 * @return true, if player has builded already 15 roads
	 * @see Road
	 */
	private boolean hasMaxRoad() {
		if (activePlayer.getCountRoad() >= Player.getMaxRoad()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Find ID of next {@code Player} when active player finish round.
	 * 
	 * @return ID of next player
	 */
	private int findNextPlayerIdFinishRound() {
		game.getActivePlayer().setBackHasPlayedDevelopmentCardThisTurn();
		game.setPhase(GamePhases.ROLL_DICE_PHASE);
		// game set the next player as active player and return active player id.
		// only here the round of game will be calculated, because only this methode
		// "game.giveControlToNextPlayer()" can calculate the round of game
		game.giveControlToNextPlayer();
		return game.getActivePlayer().getId();
	}

	/**
	 * Find ID of next {@code Player} after active player has builded a free
	 * {@code Road} in first build round.
	 * 
	 * @return ID of next player
	 */
	private int findNextPlayerIdForFreeBuild() {
		int nextPlayerId = game.getNextPlayerId();
		countRound++;
		return nextPlayerId;
	}

	/**
	 * Find ID of previous {@code Player} after active player has builded a free
	 * {@code Road} in second round.
	 * 
	 * @return ID of previous player
	 */
	private int findNextPlayerIdForFreeBuildInSecondRound() {
		int nextPlayerId = game.getPreviousPlayerId();
		countRound++;
		return nextPlayerId;
	}

	/**
	 * Set one {@code Harbor} for active {@code Player}.
	 * 
	 * @param settlementPosition
	 *            the position of {@code Settlement} which has been builded by
	 *            active {@code Player}
	 * @see Harbor
	 * @see Settlement
	 * @see Player
	 * @see Corner
	 */
	private void buildHarbor(Corner settlementPosition) {
		HarborType harborType = HarborType.NONE;
		for (Edge edge : settlementPosition.getAdjacentEdgesOfCorner()) {
			if (edge.getHarbor().getType() != HarborType.NONE) {
				harborType = edge.getHarbor().getType();
				break;
			}
		}
		Harbor harbor = new Harbor(harborType);
		game.getActivePlayer().addHarbor(harbor);
		game.getActivePlayer().getTradingRatios().combine(harborType.getTradingRatio());
	}

	/**
	 * Set a {@code City} for active {@code Player} and send message, saying, the
	 * cost and position of {@code City}.
	 * 
	 * @param cityPosition
	 *            the position of builded {@code City}
	 * @see City
	 * @see Player
	 * @see Corner
	 */
	private void buildCityAndSendMessage(Corner cityPosition) {
		City city = new City(activePlayer, cityPosition);
		Resources cost = City.getCost();
		game.getActivePlayer().getResources().subtract(cost);
		game.getBank().getResources().add(cost);
		sendLoseResourcesInfoToAll(cost, gameActivePlayerId);
		// replace settlement with city
		game.getBoard().addCityinBoard(city);
		cityPosition.setLocality(city);
		// replace settlement with city
		game.getActivePlayer().addCity(city);
		sendBuildInfoToAll(city);

		checkWin();
	}

	/**
	 * Set a {@code Settlement} for active {@code Player} and send message, saying,
	 * the cost and position of {@code Settlement}.
	 * 
	 * @param settlementPosition
	 *            the position of builded {@code Settlement}
	 * @see Settlement
	 * @see Player
	 * @see Corner
	 */
	private void buildSettlementAndSendMessage(Corner settlementPosition) {
		Settlement newSettlement = new Settlement(activePlayer, settlementPosition);
		newSettlement.setPosition(settlementPosition);
		settlementPosition.setLocality(newSettlement);

		Resources cost = Settlement.getCost();
		game.getActivePlayer().getResources().subtract(cost);
		game.getBank().getResources().add(cost);
		sendLoseResourcesInfoToAll(cost, gameActivePlayerId);
		game.getBoard().addConstruction(newSettlement);
		game.getActivePlayer().addLocality(newSettlement);

		sendBuildInfoToAll(newSettlement);
		checkWin();
	}

	/**
	 * Set a {@code Road} for active {@code Player} and send message, saying, the
	 * cost and position of {@code Road}.
	 * 
	 * @param roadPosition
	 *            the position of builded {@code Road}
	 * @see Road
	 * @see Player
	 * @see Edge
	 */
	private void buildRoadAndSendMessage(Edge roadPosition) {
		Road newRoad = new Road(activePlayer, roadPosition);
		Resources cost = Road.getCost();
		game.getActivePlayer().getResources().subtract(cost);
		game.getBank().getResources().add(cost);
		sendLoseResourcesInfoToAll(cost, gameActivePlayerId);
		roadPosition.setRoad(newRoad);
		game.getBoard().addConstruction(newRoad);
		game.getActivePlayer().addRoad(newRoad);
		// will be used for checking whether player has builded the longest road
		game.getActivePlayer().updateLongestRoadLengthProperty();
		sendBuildInfoToAll(newRoad);

		checkWin();
	}

	/**
	 * Sends a message to all client, saying, position and {@code Road},
	 * {@code Settlement} and {@code City} has been builded.
	 * 
	 * @param construction the specific {@code Construction}
	 */
	private void sendBuildInfoToAll(Construction construction) {
		JSONObject j_build = new Mapper().writeValueAsJson(construction);
		JSONObject message = new JSONObject();
		message.put(Attributes.CONSTRUCTIONS.toString(), j_build);

		serverConnection.broadcast(ClientTypes.BUILDING.toString(), message);
	}

	/**
	 * Check if the active {@code Player} get 10 or more than 10 points. If he get
	 * 10 or more than 10 points, he wins.
	 */
	private void checkWin() {
		int win = 10;

		game.getActivePlayer().updateVictoryPoints();
		if (game.getActivePlayer().getVictoryPoints() >= win) {

			updateVictoryPointsOfAllAndSendMessageToAll();
			winTheGameAndsendWinMessageToALL();

			// Shut the server down.
			server.stopRunning();
		}
	}

	/**
	 * Check if the active {@code Player} get 10 or more than 10 points. If he get
	 * 10 or more than 10 points, he wins.
	 * @param player the active {@link Player} 
	 */
	private void checkWin(Player player) {
		int win = 10;
		player.updateVictoryPoints();
		if (player.getVictoryPoints() >= win) {
			winTheGameAndsendWinMessageToALL(player);
		}
	}

	/**
	 * Get {@code Resources} from {@code Hex} which are near by one
	 * {@code Settlement}
	 * 
	 * @param settlementPosition
	 *            the position of the settlement
	 * @see Corner
	 * @see ResourceType
	 */
	private void getResourceFromCornerAndSendMessage(Corner settlementPosition) {
		Resources resources = game.getActivePlayer().getResources();
		for (Hex hex : settlementPosition.getAdjacentHexesOfCorner()) {
			if (hex.getResources() != null) {
				game.getBank().getResources().subtract(hex.getResources());
				resources.add(hex.getResources());
				game.getActivePlayer().updateResourceQuantity();
			}
		}
		sendGetResourcesInfoToAll(resources, gameActivePlayerId);
	}

	/**
	 * Check if the {@code Corner} is one side of the {@code Harbor}.
	 * 
	 * @param settlementPosition the {@link Corner} at which the settlement is located settlementPosition
	 * @return true, if the position of {@link Settlement} is one side of the {@link Harbor}.
	 * @see Edge
	 * @see HarborType
	 */
	private boolean isHarbor(Corner settlementPosition) {
		for (Edge edge : settlementPosition.getAdjacentEdgesOfCorner()) {
			if (edge.getHarbor().getType() != HarborType.NONE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether active {@code Player} has enough {@code Resources} to build one
	 * {@code Settlement}, one {@code City} or one {@code Road}.
	 * 
	 * @param construction
	 *            the construction to be builded
	 * @return true, if the active player has enough resources.
	 * @see Settlement
	 * @see City
	 * @see Road
	 * @see Resources
	 * @see ResourceType
	 */
	private boolean hasEnoughResourcesToBuild(String construction) {
		if (construction.equals(Attributes.SETTLEMENT.toString())) {
			Resources cost = Settlement.getCost();
			Resources activePlayerResources = activePlayer.getResources();
			return activePlayerResources.isGreaterThanOrEqualTo(cost);
		} else if (construction.equals(Attributes.CITY.toString())) {
			Resources cost = City.getCost();
			Resources activePlayerResources = activePlayer.getResources();
			return activePlayerResources.isGreaterThanOrEqualTo(cost);
		} else if (construction.equals(Attributes.ROAD.toString())) {
			// Danke Chris~
			Resources cost = Road.getCost();
			Resources activePlayerResources = activePlayer.getResources();
			return activePlayerResources.isGreaterThanOrEqualTo(cost);
		} else {
			return false;
		}
	}

	/**
	 * Check whether the active {@code Player} own one {@code Settlement} on this
	 * {@code Corner}.
	 * 
	 * @param cityPosition
	 *            the own settlement where the city will be builded
	 * @return true, if the active {@code Player} has one settlement on this corner.
	 * @see Settlement
	 * @see Corner
	 * @see City
	 */
	private boolean isPositionOneValidCityPosition(Corner cityPosition) {
		if (cityPosition.isCornerOccupied()) {
			if ((cityPosition.getLocality().getOwner().getId()) == (gameActivePlayerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether one own {@code Settlement}, {@code City} or {@code Road} is
	 * linked.
	 * 
	 * @param roadPosition
	 *            the {@code Edge} where the road will be builded
	 * @return true, if active {@code Player} own one construction nearby.
	 * @see Settlement
	 * @see City
	 * @see Road
	 * @see Edge
	 */
	private boolean isPositionOneValidRoadPosition(Edge roadPosition) {
		boolean surroundingRoad = false;
		boolean surroundingLocality = false;

		if (isPositionInGameArea(roadPosition)) {
			for (Edge edge : roadPosition.getAdjacentEdgesOfEdge()) {
				if (edge.getRoad() != null) {
					if (edge.getRoad().getOwner().getId() == gameActivePlayerId) {
						surroundingRoad = true;
					}
				}
			}
		}

		for (Corner corner : roadPosition.getAdjacentCornersOfEdge()) {
			if (corner.getLocality() != null) {
				if (corner.getLocality().getOwner().getId() == gameActivePlayerId) {
					surroundingLocality = true;
				}
			}
		}

		if (surroundingRoad || surroundingLocality) {
			return !roadPosition.isOccupied();
		} else {
			return false;
		}
	}

	/**
	 * Check whether one {@code Settlement} of other {@code Player} is builded near
	 * this position.
	 * 
	 * @param settlementPosition
	 *            {@code Corner} where the settlement will be builded
	 * @return true, if there is no Localities near this corner.
	 * @see Corner
	 * @see Settlement
	 */
	private boolean isValidPositionForBuildFreeSettlement(Corner settlementPosition) {
		List<Corner> cornersNearTheSettlementPosition = settlementPosition.getAdjacentCornersOfCorner();
		if (settlementPosition.isCornerOccupied()) {
			return false;
		} else {
			for (Corner corner : cornersNearTheSettlementPosition) {
				if (corner.isCornerOccupied()) {
					// return false
					return false;
				}
			}
		}
		return isPositionInGameArea(settlementPosition);
	}

	/**
	 * Check whether the {@code Road} is linked with own {@code Settlement}.
	 * 
	 * @param roadPosition
	 *            the {@code Edge} where the road will be builded
	 * @return true, if there is one own settlement nearby.
	 * @see Road
	 * @see Settlement
	 * @see Edge
	 */
	private boolean isValidPositionForBuildFreeRoad(Edge roadPosition) {
		boolean isOwnSettlementNearBy = isOwnSettlementNearBy(roadPosition);
		if (isPositionInGameArea(roadPosition)) {
			if (isOwnSettlementNearBy) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether own {@code Settlement} is nearby.
	 * 
	 * @param roadPosition
	 *            the {@code Edge} which should be linked to settlement
	 * @return true, if one own settlement is near this edge.
	 * @see Settlement
	 * @see Edge
	 */
	private boolean isOwnSettlementNearBy(Edge roadPosition) {
		List<Corner> corners = roadPosition.getAdjacentCornersOfEdge();
		for (Corner corner : corners) {
			if (corner.getLocality() != null) {
				if (corner.getLocality().getOwner().getId() == gameActivePlayerId) {
					if (secondRoundToFreeBuild) {
						if (((Settlement) corner.getLocality()).getisSecondRoundBuilded()) {
							return true;
						}
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check whether one {@code Road} is and no other {@code Settlement} is nearby.
	 * 
	 * @param settlementPosition
	 *            the{Corner} where the settlement will be builded
	 * @return true, if one road and no other settlement is nearby.
	 * @see Settlement
	 * @see Road
	 * @see Corner
	 */
	private boolean isPositionOneValidSettlementPosition(Corner settlementPosition) {
		List<Corner> cornersNearTheSettlementPosition = settlementPosition.getAdjacentCornersOfCorner();
		List<Edge> edgesNearTheSettlementPosition = settlementPosition.getAdjacentEdgesOfCorner();

		if (settlementPosition.getLocality() != null) {
			return false;
		}

		// near this corner there is no settlement
		for (Corner corner : cornersNearTheSettlementPosition) {
			if (corner.isCornerOccupied()) {
				return false;
			}
		}
		// All edges near this corner
		for (Edge edge : edgesNearTheSettlementPosition) {
			// find Roads at these edges
			if (edge.getRoad() != null) {
				// activePlayer have one Road near this corner and this corner is in gamearea
				if ((edge.getRoad().getOwner().getId() == gameActivePlayerId)
						&& (isPositionInGameArea(settlementPosition))) {
					// than this corner is a valid position to build settlement
					// return true otherwise return false
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the {@code Player} can build a construction.
	 * 
	 * @return true, if the player can build a construction
	 * @see Road
	 * @see Settlement
	 * @see City
	 */
	private boolean checkRightToBuilding() {
		PlayerState playerState = this.activePlayer.getState();
		if ((playerState == PlayerState.BUILD_FREE_ROAD) || (playerState == PlayerState.BUILD_FREE_SETTLEMENT)
				|| (playerState == PlayerState.TRADE_OR_BUILD)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether the player build a {@code Settlement}.
	 * 
	 * @param construction
	 *            the {@code Settlement}
	 * @return true, if the player will build a {@code Settlement}.
	 */
	private boolean isBuilding_Settlement(String construction) {

		if (construction.equals(Attributes.SETTLEMENT.toString())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether the player is building a {@code Settlement}.
	 * 
	 * @param construction
	 *            the{@code Road}
	 * @return true, if the player is building a {@code Road}.
	 */
	private boolean isBuilding_Road(String construction) {

		if (construction.equals(Attributes.ROAD.toString())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check whether the player state in Build_free_road or Build_free_Settlement.
	 * 
	 * @return true, if the player can build a free {@code Road} or free
	 *         {@code Settlement}.
	 * @see Road
	 * @see Settlement
	 */
	private boolean isA_Free_Build() {
		PlayerState playerState = this.activePlayer.getState();
		if ((playerState == PlayerState.BUILD_FREE_ROAD) || (playerState == PlayerState.BUILD_FREE_SETTLEMENT)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sends a message to all client, saying, the robber is moved in new position
	 * and the target player should be robbed.
	 * 
	 * @param xyPosition
	 *            the new {@code Robber} position
	 * @param targetPlayerId
	 *            the ID of target {@code Player}
	 * @see Robber
	 * @see Player
	 */
	private void sendRobberInfoToAll(JSONObject xyPosition, int targetPlayerId) {
		int playerIdWhoMoveRobber = this.gameActivePlayerId;
		JSONObject j_Robber_Moved = new JSONObject();
		j_Robber_Moved.put(Attributes.PLAYER.toString(), playerIdWhoMoveRobber);
		j_Robber_Moved.put(Attributes.POSITION.toString(), xyPosition);
		j_Robber_Moved.put(Attributes.TARGET.toString(), targetPlayerId);
		serverConnection.broadcast(ClientTypes.ROBBER_MOVED.toString(), j_Robber_Moved);
	}

	/**
	 * Robbed the resources from target {@code Player} and Sends a message to all
	 * client, saying, the {@code Resources} has been robbed.
	 * 
	 * @param targetId
	 *            the ID of target player
	 * @see Player
	 * @see Resources
	 */
	private void grabResourceFromTargetPlayerAndSendMessageToAll(int targetId) {
		int resourceTypeNumber = 5;
		int[] vaildResourcesList = new int[resourceTypeNumber];
		ResourceType[] resourceTypeList = ResourceType.values();
		Random random = new Random();
		int randomNumber = random.nextInt(resourceTypeNumber);
		Resources grabResources = new Resources();

		for (Player player : game.getPlayers()) {
			if (player != null) {
				if (player.getId() == targetId) {

					Resources resources = player.getResources();
					for (int i = 0; i < resourceTypeNumber; i++) {
						int amount = resources.getResources().get(resourceTypeList[i]);
						if (amount > 0) {
							vaildResourcesList[i % resourceTypeNumber] = 1;
						}
					}

					while (vaildResourcesList[randomNumber] == 0) {
						randomNumber = random.nextInt(resourceTypeNumber);
					}
					switch (randomNumber) {
					case 0:
						grabResources = new Resources(ResourceType.WOOL, 1, ResourceType.BRICK, 0, ResourceType.LUMBER,
								0, ResourceType.GRAIN, 0, ResourceType.ORE, 0);

						break;
					case 1:
						grabResources = new Resources(ResourceType.WOOL, 0, ResourceType.BRICK, 1, ResourceType.LUMBER,
								0, ResourceType.GRAIN, 0, ResourceType.ORE, 0);

						break;
					case 2:
						grabResources = new Resources(ResourceType.WOOL, 0, ResourceType.BRICK, 0, ResourceType.LUMBER,
								1, ResourceType.GRAIN, 0, ResourceType.ORE, 0);

						break;
					case 3:
						grabResources = new Resources(ResourceType.WOOL, 0, ResourceType.BRICK, 0, ResourceType.LUMBER,
								0, ResourceType.GRAIN, 1, ResourceType.ORE, 0);

						break;
					case 4:
						grabResources = new Resources(ResourceType.WOOL, 0, ResourceType.BRICK, 0, ResourceType.LUMBER,
								0, ResourceType.GRAIN, 0, ResourceType.ORE, 1);

						break;
					}
					resources.subtract(grabResources);
					sendLoseResourcesInfoToAll(grabResources, targetId);
					game.getActivePlayer().getResources().add(grabResources);
					sendGetResourcesInfoToAll(grabResources, this.gameActivePlayerId);
					break;
				}
			}
		}
	}

	/**
	 * Make a JSONObject of resources.
	 * 
	 * @param resources
	 *            the {@code Resources}
	 * @param playerId
	 *            the ID of player
	 * @return JSONObject {Player:playerId, Resources:resources}
	 */
	private JSONObject makeJSONObjectOfResource(Resources resources, int playerId) {
		JSONObject j_resources = new Mapper().writeValueAsJson(resources);

		JSONObject j_Resources = new JSONObject();
		j_Resources.put(Attributes.RESOURCES.toString(), j_resources);
		j_Resources.put(Attributes.PLAYER.toString(), playerId);

		return j_Resources;
	}

	/**
	 * Make unknown JSONObject of {@code Resources}
	 * 
	 * @param resources
	 *            the resources
	 * @param playerId
	 *            the ID of player
	 * @return unknown JSONObject {Player:playerId, Resources: sum}
	 */
	private JSONObject makeJSONObjectOfUnknownResource(Resources resources, int playerId) {
		JSONObject j_resources = new Mapper().writeValueAsJsonUnknownResources(resources);

		JSONObject cost = new JSONObject();
		cost.put(Attributes.RESOURCES.toString(), j_resources);
		cost.put(Attributes.PLAYER.toString(), playerId);

		return cost;
	}

	/**
	 * Sends a message to all client, saying, the {@code Player} has lost the
	 * {@code Resources}.
	 * 
	 * @param resources
	 *            the resources to be lost
	 * @param playerId
	 *            the ID of player
	 * @see Player
	 * @see Resources
	 */
	private void sendLoseResourcesInfoToAll(Resources resources, int playerId) {
		JSONObject j_resource = new JSONObject();
		JSONObject j_UnknownResourcesToElse = new JSONObject();
		j_resource = makeJSONObjectOfResource(resources, playerId);
		j_UnknownResourcesToElse = makeJSONObjectOfUnknownResource(resources, playerId);

		serverConnection.sendToSomeone(ClientTypes.PRICE.toString(), j_resource, playerId);
		serverConnection.sendToSomeoneElse(ClientTypes.PRICE.toString(), j_UnknownResourcesToElse, playerId);
	}

	/**
	 * Broadcast the message, saying, the {@code Player} has payed the
	 * {@code Resources} to the {@code Bank}.
	 * 
	 * @param resources
	 *            the supply by sea trade
	 * @param playerId
	 *            the ID of player, who make a sea trade
	 */
	private void broadcastLoseResourcesMessage(Resources resources, int playerId) {
		JSONObject j_resource = new JSONObject();
		j_resource = makeJSONObjectOfResource(resources, playerId);

		serverConnection.broadcast(ClientTypes.PRICE.toString(), j_resource);
	}

	/**
	 * Broadcast the message, saying, the {@code Player} has got the
	 * {@code Resources} from the {@code Bank}.
	 * 
	 * @param resources
	 *            the demand by sea trade
	 * @param playerId
	 *            the ID of player, who make a sea trade
	 */
	private void broadcastGetResourcesMessage(Resources resources, int playerId) {
		JSONObject j_resource = new JSONObject();
		j_resource = makeJSONObjectOfResource(resources, playerId);

		serverConnection.broadcast(ClientTypes.RESOURCE_QUANTITY.toString(), j_resource);
	}

	/**
	 * Sends a message to all client, saying, the {@code Player} has gotten the
	 * {@code Resources}.
	 * 
	 * @param resources
	 *            the resources to be gotten
	 * @param playerId
	 *            the ID of player
	 */
	private void sendGetResourcesInfoToAll(Resources resources, int playerId) {
		JSONObject j_resource = new JSONObject();
		JSONObject j_UnknownResourcesToElse = new JSONObject();
		j_resource = makeJSONObjectOfResource(resources, playerId);
		j_UnknownResourcesToElse = makeJSONObjectOfUnknownResource(resources, playerId);

		serverConnection.sendToSomeone(ClientTypes.RESOURCE_QUANTITY.toString(), j_resource, playerId);
		serverConnection.sendToSomeoneElse(ClientTypes.RESOURCE_QUANTITY.toString(), j_UnknownResourcesToElse,
				playerId);
	}

	/**
	 * Finish {@code Game} and Sends a message to all client, saying, the active
	 * {@code Player} has won the game.
	 */
	private void winTheGameAndsendWinMessageToALL() {
		JSONObject j_win = new JSONObject();

		j_win.put(Attributes.MESSAGE.toString(),
				"Der Spieler " + game.getActivePlayer().getName() + " hat das Spiel gewonnen.");
		j_win.put(Attributes.WINNER.toString(), gameActivePlayerId);
		serverConnection.broadcast(ClientTypes.GAME_FINISHED.toString(), j_win);
	}

	/**
	 * Send victory points of every {@code Player} to everyone else. 
	 */
	private void updateVictoryPointsOfAllAndSendMessageToAll() {
		for(Player player:game.getPlayers()) {
				player.updateVictoryPoints();
				JSONObject jsonPlayer = new Mapper().writeValueAsJson(player);
				JSONObject message = new JSONObject();
				message.put(Attributes.PLAYER.toString(), jsonPlayer);
				serverConnection.broadcast(ClientTypes.STATE_UPDATE.toString(),message);
		}
	}

	/**
	 * Finish {@code Game} and Sends a message to all client, saying, this
	 * {@code Player} has won the game.
	 * @param player the {@link Player}
	 */
	private void winTheGameAndsendWinMessageToALL(Player player) {
		JSONObject j_win = new JSONObject();
		j_win.put(Attributes.MESSAGE.toString(), "Spieler " + player.getName() + " hat das Spiel gewonnen.");
		j_win.put(Attributes.WINNER.toString(), player.getId());
		serverConnection.broadcast(ClientTypes.GAME_FINISHED.toString(), j_win);
	}

	/**
	 * Find a {@code Hex} from String.
	 * 
	 * @param stringPosition
	 *            the string position of this hex
	 * @return the hex of this position
	 */
	private Hex findHexPositionFromString(String stringPosition) {
		return game.getBoard().searchHex(stringPosition);
	}

	/**
	 * Find a {@code Corner} from String.
	 * 
	 * @param stringPosition
	 *            the string position of this corner
	 * @return the corner of this position
	 */
	private Corner findsettlementPositionFromString(String stringPosition) {
		return game.getBoard().searchCorner(stringPosition);
	}

	/**
	 * Find a {@code Edge} from String.
	 * 
	 * @param stringPosition
	 *            the string position of this edge
	 * @return the edge of this position
	 */
	private Edge findRoadPositionFromString(String stringPosition) {
		return game.getBoard().searchEdge(stringPosition);
	}

	/**
	 * Find a {@code Corner} from String.
	 * 
	 * @param stringPosition
	 *            the string position of this corner
	 * @return the corner of this position
	 */
	private Corner findCityPositionFromString(String stringPosition) {
		return game.getBoard().searchCorner(stringPosition);
	}

	/**
	 * Check whether the {@code Hex} is in game area.
	 * 
	 * @param hex
	 *            the {@code Hex}
	 * @return true, if the hex is in game area.
	 */
	private boolean isPositionInGameArea(Hex hex) {
		if (hex.getType() == HexType.WATER || hex == null) {
			return false;
		}
		return game.getBoard().getHexes().contains(hex);
	}

	/**
	 * Check whether the corner is in game area.
	 * 
	 * @param corner
	 *            the {@code Corner}
	 * @return true, if the corner is in game area.
	 * @see Corner
	 */
	private boolean isPositionInGameArea(Corner corner) {
		int countWaterHex = 0;
		int countOtherHex = 0;
		int maxWaterHexNumbernearCorner = 2;
		int hexNumbernearCorner = 3;
		for (Hex hex : corner.getAdjacentHexesOfCorner()) {
			if (hex != null) {
				if (hex.getType() == HexType.WATER) {
					countWaterHex++;
				} else {
					countOtherHex++;
				}
			}
		}
		if (countWaterHex > maxWaterHexNumbernearCorner) {
			return false;
		} else if (countOtherHex + countWaterHex == hexNumbernearCorner) {
			return game.getBoard().getCorners().contains(corner);
		} else {
			return false;
		}
	}

	/**
	 * Check whether the {@code Edge} is in game area.
	 * 
	 * @param edge
	 *            the {@code Edge}
	 * @return true, if the edge is in game area.
	 */
	private boolean isPositionInGameArea(Edge edge) {
		int countWaterHex = 0;
		int countOtherHex = 0;
		int maxWaterHexNumbernearEdge = 1;
		int hexNumbernearEdge = 2;
		for (Hex hex : edge.getAdjacentHexesOfEdge()) {
			if (hex != null) {
				if (hex.getType() == HexType.WATER) {
					countWaterHex++;
				} else {
					countOtherHex++;
				}
			}
		}
		if (countWaterHex > maxWaterHexNumbernearEdge) {
			return false;
		} else if (countOtherHex + countWaterHex == hexNumbernearEdge) {
			return game.getBoard().getEdges().contains(edge);
		} else {
			return false;
		}
	}

	/**
	 * Check whether the target {@code Player} has enough {@code Resources}.
	 * 
	 * @param targetPlayerId
	 *            the ID of target player
	 * @return true, if the target player has enough resources.
	 */
	private boolean hasTargetPlayerEnoughResources(int targetPlayerId) {
		int resourcesSum;
		int minResourcesSum = 1;
		for (Player player : game.getPlayers()) {
			if (player.getId() == targetPlayerId) {
				resourcesSum = player.getResources().getSum();
				if (resourcesSum >= minResourcesSum) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check whether the list include the target {@code Player}.
	 * 
	 * @param players relative Player Id List
	 *            the list of player
	 * @param targetId
	 *            the ID of target player
	 * @return true, if the target player is in the list.
	 */
	private boolean isListIncludTargetPlayer(List<Player> players, int targetId) {
		for (Player player : players) {
			if (player.getId() == targetId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Subtract {@code Resources} from active {@code Player} in game.
	 * 
	 * @param resources
	 *            the resources to be subtracted
	 */
	private void minusResources(Resources resources) {
		game.getActivePlayer().getResources().subtract(resources);
		game.getBank().getResources().add(resources);
	}

	/**
	 * Check whether the active {@code Player} is the player, who has rolled 7.
	 * 
	 * @return true, if the active player is the player, who has rolled 7.
	 */
	private boolean isactivePlayerTheDicePlayer() {
		return savePlayerId == gameActivePlayerId;
	}

	/**
	 * Check whether the {@code PlayerState} is not "wait" or is SEND_CHATMESSAGE or
	 * ACCEPT_TRADE.
	 * 
	 * @param messageType
	 *            the type of message
	 * @return true, if the {@code Player} want send a chat message, accept one
	 *         trade or player is not in state "wait".
	 * @see PlayerState
	 * @see ServerTypes
	 */
	private boolean haveRightSolveJSON(ServerTypes messageType) {
		boolean haveRightSolveJSON = true;
		if (gameStarted) {
			Player player = findPlayerWithId(serverConnectionId);
			if (player.getState() != PlayerState.WAIT) {
				this.activePlayer = player;
			} else {
				if (messageType != ServerTypes.SEND_CHATMESSAGE && messageType != ServerTypes.ACCEPT_TRADE && messageType != ServerTypes.CANCEL_TRADE) {
					haveRightSolveJSON = false;
					sendNoRightErrorInfoToClient(getBundle().getString("PLEASE_WAIT"));
				} else {
					this.activePlayer = player;
				}
			}
		}
		return haveRightSolveJSON;
	}

	/**
	 * Sends a message to the client, saying, that the action of player is wrong or
	 * not allowed.
	 * 
	 * @param message the message to send.
	 */
	private void sendNoRightErrorInfoToClient(String message) {
		JSONObject j_erro = new JSONObject();
		j_erro.put(ClientTypes.SERVER_REPLY.toString(), message);

		serverConnection.sendToClient(j_erro);
	}

	/**
	 * Sends a message to the client, saying, that the requested action has been
	 * approved.
	 * 
	 * @see ClientController
	 */
	private void sendOK() {
		JSONObject message = new JSONObject();
		message.put(ClientTypes.SERVER_REPLY.toString(), Attributes.OK.toString());
		serverConnection.sendToClient(message);
	}

	/**
	 * Sends status update messages to all {@code Users}. This happens in two steps:
	 * First, the specified {@link User Users} {@link PlayerState} is send to all
	 * other users. Second, the states of all users are send to the new user.
	 * 
	 * @param newUser
	 *            the newly connected user.
	 */
	private void sendStatusMessages(User newUser) {
		// Send status info of new user to everybody else.
		JSONObject message = new Mapper().writeValueAsJson(newUser);
		serverConnection.sendToEverybodyElse(ClientTypes.STATE_UPDATE.toString(), message);

		// Send status info of all users to the new user.
		for (User user : lobby.getUsers()) {
			message = new Mapper().writeValueAsJson(user);
			serverConnection.sendToClient(ClientTypes.STATE_UPDATE.toString(), message);
		}
	}

	/**
	 * Sends a welcome message to the specified {@code User}.
	 * 
	 * @param user
	 *            the {@link User} to whom the message is sent.
	 */
	private void sendWelcomeMessage(User user) {
		// Create message.
		JSONObject message = new JSONObject();
		message.put(Attributes.ID.toString(), user.getId());

		// Send message.
		serverConnection.sendToClient(ClientTypes.WELCOME.toString(), message);
	}

	/**
	 * Sends a message notifying all {@code Players} that the {@code Robber} has
	 * been moved to a new {@code Hex}.
	 * 
	 * @param hexPosition the {@link Hex} the {@link Robber} has been moved to.
	 * @see Player
	 */
	private void sendRobberMovedMessage(JSONObject hexPosition) {
		// Create message.
		JSONObject message = new JSONObject();
		message.put(Attributes.POSITION.toString(), hexPosition);

		// Send message.
		serverConnection.broadcast(ClientTypes.ROBBER_MOVED.toString(), message);
	}

	/**
	 * Sends a error message to the specified {@code User}, saying, he has no valid
	 * name.
	 * 
	 * @see HumanUser
	 * @see User
	 */
	private void sendEmptyNameMessage() {
		JSONObject emptyName = new JSONObject();
		emptyName.put(Attributes.REPORT.toString(), getBundle().getString("NO_VALID_NAME"));
		serverConnection.sendToClient(ClientTypes.ERROR.toString(), emptyName);
	}

	/**
	 * Sends a error message to the specified {@code User}, saying, he has no valid
	 * color.
	 * 
	 * @see HumanUser
	 * @see User
	 */
	private void sendNoAvaliableColorMessage() {
		JSONObject colorUsed = new JSONObject();
		colorUsed.put(Attributes.REPORT.toString(), getBundle().getString("NO_VALID_COLOR"));
		serverConnection.sendToClient(ClientTypes.ERROR.toString(), colorUsed);
	}

	/**
	 * Find ID of next {@code Player} from list.
	 * 
	 * @param players
	 *            list of players
	 * @return ID of next player in list
	 */
	private int findNextPlayerIdFromList(List<Player> players) {
		int nextPlayerId = players.get(0).getId();
		playerIdsWhohasMoreThanSevenResourceCards.remove(players.get(0));
		game.giveControlToPlayer(nextPlayerId);
		return nextPlayerId;
	}

	/**
	 * Sends dice number messages to all, saying, active {@code Player} has rolled
	 * dice number e.g.[3,6].
	 * 
	 * @param diceNumbers
	 *            dice number that active {@code Player} has rolled
	 */
	private void sendDiceNumbertoEveryone(int[] diceNumbers) {
		JSONObject j_dice = new JSONObject();
		j_dice.put(Attributes.PLAYER.toString(), serverConnectionId);
		j_dice.put(Attributes.DICE_VALUE.toString(), diceNumbers);
		serverConnection.broadcast(ClientTypes.DICE.toString(), j_dice);
	}

	/**
	 * Find all players who has more than 7 resources cards.
	 * 
	 * @return list of {@code Player} who has more than 7 resources cards.
	 * @see ResourceType
	 */
	private List<Player> findPlayerIdsWhohasMoreThanSevenResourceCards() {
		int minResourceCardsNumberByDroping = 7;
		List<Player> playersWhoNeedDropResourcesCards = new ArrayList<>();

		for (Player player : game.getPlayers()) {
			int resourceSumofOnePlayer = player.getResources().getSum();
			if (resourceSumofOnePlayer > minResourceCardsNumberByDroping) {
				playersWhoNeedDropResourcesCards.add(player);
			}
		}
		return playersWhoNeedDropResourcesCards;
	}

	/**
	 * Check whether every linked {@code User} are ready.
	 * 
	 * @return true, if all linked user are ready.
	 * @see HumanUser
	 * @see AIUser
	 */
	private boolean playersAllready() {
		for (User user : lobby.getUsers()) {
			if (user.getState() != PlayerState.WAIT_FOR_GAME_START) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether linked {@code User} are three or more than three.
	 * 
	 * @return true, if three or more than three user in the lobby.
	 * @see HumanUser
	 * @see AIUser
	 */
	private boolean haveMinPlayer() {

		int minPlayerNumber = MIN_PLAYERS;
		int playerNumber = lobby.getUsers().size();

		if (playerNumber >= minPlayerNumber) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Roll dice.
	 * 
	 * @return dice number e.g. [3,6]
	 */
	private int[] rollDice() {

		int diceOne;
		int diceTwo;
		int[] diceNumbers = new int[2];
		Dice dice = game.getDice();
		// Danke~ Chris
		dice.roll();
		diceOne = dice.getDieOneNumber();
		diceTwo = dice.getDieTwoNumber();

		diceNumbers[0] = diceOne;
		diceNumbers[1] = diceTwo;
		return diceNumbers;
	}

	/**
	 * Calculate the sum of dice result.
	 * 
	 * @param diceNumbers
	 *            the dice result
	 * @return sum of all dice result
	 */
	private int calculateDiceSum(int[] diceNumbers) {
		int diceSum = 0;
		for (int i : diceNumbers) {
			diceSum = i + diceSum;
		}
		return diceSum;
	}

	/**
	 * Check whether the sum of dice result is 7.
	 * 
	 * @param diceSum
	 *            sum of dice result
	 * @return true, if the sum is equal to 7.
	 */
	private boolean isDiceSumToDropCards(int diceSum) {
		int diceSumtoDropCards = 7;
		return (diceSum == diceSumtoDropCards);
	}

	/**
	 * Check whether the {@code Player} can roll dice.
	 * 
	 * @return true, if the player can roll dice.
	 */
	private boolean checkRightToRollDice() {
		if (this.activePlayer.getState() == PlayerState.ROLL_DICE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set the {@code PlayerState} and send change state message to all, saying,
	 * this {@code Player} has a new state.
	 * 
	 * @param playerState
	 *            {@code PlayerState}
	 * @param id
	 *            the ID of player who has a new state
	 * @see PlayerState
	 */
	private void setPlayerStateAndSendMessage(PlayerState playerState, int id) {
		for (Player player : game.getPlayers()) {
			if (player != null) {
				if (player.getId() == id) {
					player.setState(playerState);

					// Generate message
					JSONObject jsonPlayer = new Mapper().writeValueAsJson(player);
					JSONObject message = new JSONObject();
					message.put(Attributes.PLAYER.toString(), jsonPlayer);

					// Send playerstate info to the player whose state changed.
					serverConnection.sendToSomeone(ClientTypes.STATE_UPDATE.toString(), message, id);

					// Generate message
					JSONObject jsonUnknownPlayer = new Mapper().writeUnknownValueAsJson(player);
					JSONObject unknownmessage = new JSONObject();
					unknownmessage.put(Attributes.PLAYER.toString(), jsonUnknownPlayer);

					// Send playerstate info to everyone else.
					serverConnection.sendToSomeoneElse(ClientTypes.STATE_UPDATE.toString(), unknownmessage, id);
					break;
				}
			}
		}
	}

	/**
	 * Find a {@code Player} in {@code Game} with ID.
	 * 
	 * @param id
	 *            ID of one player
	 * @return player, who has this ID.
	 */
	private Player findPlayerWithId(int id) {
		for (Player player : game.getPlayers()) {
			if (player.getId() == id) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Game start.
	 * 
	 * @see Game
	 */
	private void gameStart() {
		gameStarted = true;
		List<Player> players = new ArrayList<>();
		players = makeAllPlayers();
		this.game = new Game(players);
		// this.tokens = getTokensFromBoard();
		this.playersNumberInGame = players.size();
		game.setPhase(GamePhases.CHOOSE_BEGINNER);
		game.setLongestRoadCard(new SpecialCard(SpecialCardType.LONGEST_ROAD));
		game.setLargestArmyCard(new SpecialCard(SpecialCardType.LARGEST_ARMY));
		maxCountRound = playersNumberInGame;
		variableRoundLimit = maxCountRound;
		changeAllPlayerStateToWaitSendMapAndThenFindOneBeginner();

	}

	/**
	 * Check whether the {@code GamePhase} to choose a beginner.
	 * 
	 * @return true, if the {@code GamePhase} is choose_beginner.
	 */
	private boolean isPhaseToChooseBeginner() {
		return game.getPhase() == GamePhases.CHOOSE_BEGINNER;
	}

	/**
	 * Set {@code PlayerState} of every {@code Player} to "wait" and tell every
	 * {@code Player} the own {@code PlayerState} and the {@code PlayerState} of
	 * each other.
	 */
	private void changeAllPlayerStateToWaitSendMapAndThenFindOneBeginner() {
		Random random = new Random();
		int randomGameBeginner = random.nextInt(playersNumberInGame) + 1;
		for (Player player : game.getPlayers()) {
			setPlayerStateAndSendMessage(PlayerState.WAIT, player.getId());
		}
		game.setActivePlayer(game.getPlayerWithId(randomGameBeginner));
		sendMapToAllAtBeginnOfTheGame();
		setPlayerStateAndSendMessage(PlayerState.ROLL_DICE, randomGameBeginner);
	}

	/**
	 * Send map of {@code Game} to every client.
	 * 
	 * @see Board
	 */
	private void sendMapToAllAtBeginnOfTheGame() {
		JSONObject map = new Mapper().writeValueAsJson(game.getBoard());
		JSONObject message = new JSONObject();
		message.put(Attributes.BOARD.toString(), map);
		serverConnection.broadcast(ClientTypes.GAME_STARTED.toString(), message);
	}

	/**
	 * Make {@code Player} from {@code User} for {@code Game}.
	 * 
	 * @return list of all make players
	 */
	private List<Player> makeAllPlayers() {
		List<Player> players = new ArrayList<>();
		List<User> users = lobby.getUsers();
		for (User user : users) {
			if (user != null) {
				int userId = user.getId();
				String userName = user.getName();
				String imageLocation = user.getImageLocation();
				Color userColor = user.getColor();
				PlayerState playerState = user.getState();
				Player player = new Player(userId, userName, imageLocation, userColor, playerState);
				players.add(player);
				player.setUser(user);
				user.setPlayer(player);
			}
		}
		return players;
	}

	/**
	 * Add {@code Resources} for all players, who has a locality on this token, and
	 * send message to them.
	 * 
	 * @param tokens over the {@code Hex} for view.
	 */
	private void AddResourceToPlayersOn(List<Token> tokens) {
		Map<Integer, Resources> resourcesOfPlayer = new HashMap<>();

		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			Hex hex = token.getHex();
			List<Locality> localities = getLocalitiesOfOneHex(hex);

			for (int j = 0; j < localities.size(); j++) {
				Locality locality = localities.get(j);
				int playerId = locality.getOwner().getId();

				Resources resources = new Resources();
				for (ResourceType resourceType : hex.getResources().getResources().keySet()) {
					resources.getResources().put(resourceType, hex.getResources().getResources().get(resourceType));
				}

				if (locality instanceof Settlement) {
					if (game.getBank().getResources().isGreaterThanOrEqualTo(resources)) {
						game.getBank().getResources().subtract(resources);
						if (resourcesOfPlayer.containsKey(playerId)) {
							resources.add(resourcesOfPlayer.get(playerId));
						}
						resourcesOfPlayer.put(playerId, resources);
					}
				} else if (locality instanceof City) {
					Resources saveResources = new Resources();
					for (ResourceType resourceType : resources.getResources().keySet()) {
						saveResources.getResources().put(resourceType, resources.getResources().get(resourceType));
					}

					resources.multiply(City.getMultiplier());
					if (game.getBank().getResources().isGreaterThanOrEqualTo(resources)) {
						game.getBank().getResources().subtract(resources);
						if (resourcesOfPlayer.containsKey(playerId)) {
							resources.add(resourcesOfPlayer.get(playerId));
						}
						resourcesOfPlayer.put(playerId, resources);
					} else if (game.getBank().getResources().isGreaterThanOrEqualTo(saveResources)) {
						game.getBank().getResources().subtract(saveResources);
						if (resourcesOfPlayer.containsKey(playerId)) {
							saveResources.add(resourcesOfPlayer.get(playerId));
						}
						resourcesOfPlayer.put(playerId, saveResources);
					}
				}
			}
		}

		for (int playerId : resourcesOfPlayer.keySet()) {
			Resources resources = resourcesOfPlayer.get(playerId);
			game.getPlayerWithId(playerId).getResources().add(resources);
			sendGetResourcesInfoToAll(resources, playerId);
		}
	}

	/**
	 * Check whether the number of dropped resource cards is right.
	 * 
	 * @param resources
	 *            the dropped {@code Resources} cards
	 * @return true, if the half of resources cards are dropped.
	 */
	private boolean checkDroppedCardsNumber(Resources resources) {
		if (resources != null) {
			int halfOfResourcesCards = (int) Math.floor(game.getActivePlayer().getResources().getSum() / 2);
			if (resources.getSum() == halfOfResourcesCards) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get {@code Settlement} and {@code City} which are on this hex.
	 * 
	 * @param hex the {@link Hex}.
	 * @return list of localities
	 */
	private List<Locality> getLocalitiesOfOneHex(Hex hex) {
		List<Corner> corners = hex.getAdjCornersOfHex();
		List<Locality> localities = new ArrayList<>();

		for (Corner corner : corners) {
			if (corner.isCornerOccupied()) {
				localities.add(corner.getLocality());
			}
		}
		return localities;
	}

	/**
	 * Get a list of the IDs of all players who have a {@code Locality} on this {@code Hex}.
	 * 
	 * @param hex the {@link Hex}.
	 * @return list of IDs of players.
	 * @see Player
	 * @see Settlement
	 * @see City
	 */
	private List<Player> getRelativePlayerListOfOneHex(Hex hex) {
		List<Player> relativePlayers = new ArrayList<>();
		List<Locality> localities = getLocalitiesOfOneHex(hex);

		for (Locality locality : localities) {
			relativePlayers.add(locality.getOwner());
		}
		return relativePlayers;
	}

	/**
	 * Creates and initializes a new {@code User} and adds them to the
	 * {@code Lobby}.
	 * 
	 * @param serverConnection
	 *            the {@link ServerConnection} used bye this controller.
	 * @return the new {@link User}.
	 * @see User
	 * @see Lobby
	 */
	private User addNewUser(ServerConnection serverConnection) {
		User user = new HumanUser();

		serverConnection.setUserId(nextUserId);
		user.setId(nextUserId);
		user.setState(PlayerState.START_GAME);
		lobby.addUser(user);

		// Ensures that all userId's are the unique.
		nextUserId++;

		return user;
	}

	/**
	 * Returns the {@code ChatController} which is being used to display messages.
	 * 
	 * @return the {@link ChatController}.
	 */
	public ChatController getChatController() {
		return chatController;
	}

	/**
	 * Determine the type of message.
	 * 
	 * @param type
	 *            type of message
	 * @return type of message
	 * @see ServerTypes
	 */
	private ServerTypes determineMessageType(String type) {
		ServerTypes messageType = null;
		for (ServerTypes serverType : ServerTypes.values()) {
			if (type.equals(serverType.toString())) {
				messageType = serverType;
				break;
			}
		}
		return messageType;
	}

	/**
	 * Check whether the {@code User} has a valid color.
	 * 
	 * @param color the color the user has chosen
	 * @return true, if the color is selected and different with color of other
	 *         users.
	 * @see HumanUser
	 * @see AIUser
	 */
	private boolean checkColorAvailable(Color color) {
		if (color == null) {
			return false;
		}
		for (User user : lobby.getUsers()) {
			if (user.getId() != serverConnection.getUserId()) {
				if (user.getColor() != null && user.getColor().equals(color)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check whether the {@code User} has a valid name.
	 * 
	 * @param name the name the user has chosen 
	 * @return true, if the name is written.
	 * @see User
	 * @see HumanUser
	 * @see AIUser
	 */
	private boolean checkNameAvailable(String name) {
		if (name != null) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether the {@code Robber} is not on this token.
	 * 
	 * @param token this token 
	 * @return true, if the robber is not on this token.
	 */
	private boolean checkRobberIsNotOnToken(Token token) {
		return token.getHex() != game.getBoard().getRobber().getPosition();
	}

	/**
	 * Removes the {@code User} and/or {@code Player} associated with the specified
	 * {@code ServerConnection}.
	 * 
	 * @param serverConnection
	 *            the {@link ServerConnection}.
	 */
	public synchronized void removeUserAndOrPlayer(ServerConnection serverConnection) {
		int userId = serverConnection.getUserId();

		if (gameStarted) {
			setPlayerStateAndSendMessage(PlayerState.CONNECTION_LOST, userId);
			server.stopRunning();
		} else {
			User disconnectedUser = lobby.getUser(userId);

			// Change the user's state to "connection lost".
			disconnectedUser.setState(PlayerState.CONNECTION_LOST);

			// Broadcast to all clients that the user has lost their connection.
			JSONObject message = new Mapper().writeValueAsJson(disconnectedUser);
			serverConnection.broadcast(ClientTypes.STATE_UPDATE.toString(), message);

			// Remove the user.
			lobby.getUsers().remove(disconnectedUser);
		}
	}

	/**
	 * Returns whether the game has started or not.
	 * @return true, if the game has started. false, otherwise.
	 */
	public boolean hasGameStarted() {
		return gameStarted;
	}

	/**
	 * Getter for the bundle of the current language.
	 * @return the ResourceBundle
	 */
	private ResourceBundle getBundle() {
		return bundleProperty.get();
	}

}