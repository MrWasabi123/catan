package network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Game;
import game.TradingRatios;
import game.ai.playertracker.PlayerTracker;
import game.board.Board;
import game.board.construction.Construction;
import game.board.construction.localities.City;
import game.board.construction.localities.Locality;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.corners.Corner;
import game.board.edges.Edge;
import game.board.harbors.HarborType;
import game.board.hexes.Hex;
import game.board.hexes.HexType;
import game.board.tokens.Token;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.cards.specialcards.SpecialCard;
import game.cards.victorypointcards.VictoryPointCard;
import game.dice.Dice;
import game.player.Player;
import game.player.PlayerState;
import game.resources.ResourceType;
import game.resources.Resources;
import game.trade.Trade;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import lobby.Lobby;
import lobby.chat.ChatController;
import main.ApplicationController;
import main.ApplicationInstance;
import network.protocol.Attributes;
import network.protocol.ClientTypes;
import network.protocol.ProtocolJSONObjectConverter;
import network.protocol.ProtocolStringConverter;
import network.protocol.ServerTypes;
import network.server.Server;
import sounds.AudioPlayer;
import sounds.BackgroundMusicPlayer;
import users.AIUser;
import users.HumanUser;
import users.User;

/**
 * Is responsible for handling the incoming Messages
 * 
 * @author Wanja Sajko
 * @author Christoph Hermann
 * @author Yize Sun
 */
public class ClientController {

	/**
	 * The audio player.
	 */
	private AudioPlayer audioPlayer = new AudioPlayer();

	/**
	 * The {@link Lobby} used by this controller.
	 */
	private Lobby lobby;

	/**
	 * The ClientConnection of this Controller
	 */
	private ClientConnection client;

	/**
	 * Game on which the Client runs
	 */
	private Game game;

	/**
	 * The bundle containing all error messages.
	 */
	private ObjectProperty<ResourceBundle> bundleProperty = ApplicationInstance.getInstance().getBundleProperty();

	/**
	 * The {@code ChatController} that is used to display messages.
	 */
	private ChatController chatController;

	/**
	 * The controller responsible of managing the different scenes of this
	 * application.
	 * 
	 * @see ApplicationController
	 */
	private ApplicationController applicationController;

	/**
	 * The player for the background music.
	 */
	private BackgroundMusicPlayer backgroundMusicPlayer = new BackgroundMusicPlayer();

	/**
	 * Creates a new ClientController.
	 * 
	 * @param client
	 *            the {@link ClientConnection} this controller uses to send messages
	 *            to the {@link Server}.
	 * @param chatController
	 *            the controller responsible for displaying chat messages received
	 *            by this client.
	 * @param lobby
	 *            the {@link Lobby} used by this controller.
	 * @param applicationController
	 *            the controller responsible of managing the different scenes of
	 *            this application.
	 */
	public ClientController(ClientConnection client, ChatController chatController, Lobby lobby,
			ApplicationController applicationController) {
		this.client = client;
		this.chatController = chatController;
		this.lobby = lobby;
		this.applicationController = applicationController;
	}

	/**
	 * Creates a new ClientController.
	 * 
	 * @param client
	 *            the {@link ClientConnection} this controller uses to send messages
	 *            to the {@link Server}.
	 * @param lobby
	 *            the {@link Lobby} used by this controller.
	 */
	public ClientController(ClientConnection client, Lobby lobby) {
		this.client = client;
		this.lobby = lobby;
	}

	/**
	 * Checks the Key of the JSONObject and specifies the according actions
	 * 
	 * @param jsonObject
	 *            received message as JSONObject
	 */
	public synchronized void handle(JSONObject jsonObject) {
		JSONObject obj;
		ClientTypes messageType;

		String type = jsonObject.firstKey();
		if (type.equals(ClientTypes.SERVER_REPLY.toString())) {
			messageType = ClientTypes.SERVER_REPLY;
			obj = jsonObject;
		} else {
			messageType = determineMessageType(type);
			obj = (JSONObject) jsonObject.get(type);
		}

		switch (messageType) {
		case HELLO:
			if (usesSupportedProtocol(obj)) {
				sendClientProtocolVersionToServer();
			} else {
				client.stopRunning();
			}
			break;

		case WELCOME:
			setUserID(obj);

			if (client.getUser() instanceof AIUser) {
				((AIUser) client.getUser()).getAi().makeNextMove();
			} else {
				sendUserUpdateToServer();
			}

			break;

		case STATE_UPDATE:
			JSONObject userOrPlayer = obj.getJSONObject(Attributes.PLAYER.toString());
			PlayerState state = getState(userOrPlayer);

			if (gameHasStarted()) {
				if (newRoundOfHumanUserStarted(userOrPlayer)) {
					audioPlayer.playTurnNotificationSound();
				}
				if (state == PlayerState.WAIT && client.getUser() instanceof AIUser && client.getUser().getId() == game.getActivePlayer().getId()) {
					((AIUser) client.getUser()).getAi().setTradeWithPlayer(false);
				}
				if (state == PlayerState.ROLL_DICE && client.getUser() instanceof AIUser && client.getUser().getId() == game.getActivePlayer().getId()) {
					((AIUser) client.getUser()).getAi().setWasTrade(false);
				}

				//update victoryPoints of everyone when someone win
				if (userOrPlayer.has(Attributes.VICTORY_POINTS.toString()) && userOrPlayer.has(Attributes.RESOURCE.toString())) {
					int playerInfoId = userOrPlayer.getInt(Attributes.ID.toString());
					JSONObject resources = obj.getJSONObject(Attributes.RESOURCES.toString());

					if(!resources.has(Attributes.UNKNOWN.toString()) && state == game.searchPlayers(client.getUser().getId()).getState()) {				
						int victoryPoints = userOrPlayer.getInt(Attributes.VICTORY_POINTS.toString());
						//??shuold i use Plattform(()->)?
						game.searchPlayers(playerInfoId).setVictoryPoints(victoryPoints);
					}
				}
				
				updatePlayer(userOrPlayer);

				
				if (state == PlayerState.CONNECTION_LOST) {
					game.setGameOver(true);
					if (client.getUser() instanceof HumanUser) {
						// Stop music.
						backgroundMusicPlayer.stopBackgroundMusic();

						// Play defeat sound.
						audioPlayer.playDefeatSound();

						// Display message.
						client.getClientController().chatController.displayMessage(
								client.getUser().getPlayer().getName() + 
								getBundle().getString("CONNECTION_LOST_MESSAGE"));
					}

					// Close connection to server.
					client.stopRunning();
				}

			} else {
				if (state == PlayerState.CONNECTION_LOST) {
					deleteUser(userOrPlayer);
				} else {
					int userId = userOrPlayer.getInt(Attributes.ID.toString());
					User user = lobby.getUser(userId);

					if (user == null) {
						createNewUser(userId);

						// Play connect sound
						user = lobby.getUser(userId);
						if (user != client.getUser() &&	client.getUser() instanceof HumanUser) {
							audioPlayer.playConnectSound();
						}
					}

					updateUser(userOrPlayer);

					// Play ready sound
					user = lobby.getUser(userId);
					if (user.getState() == PlayerState.WAIT_FOR_GAME_START &&
							user == client.getUser() &&
							user instanceof HumanUser) {
						audioPlayer.playReadySound();
					}
				}
			}
			break;

		case GAME_STARTED: {
			// create new Players and a new Board.
			createNewGame();

			JSONObject jBoard = (JSONObject) obj.get("Karte");
			JSONArray jaHex = (JSONArray) jBoard.get(Attributes.HEXES.toString());
			JSONArray jaCon = (JSONArray) jBoard.get(Attributes.CONSTRUCTIONS.toString());
			JSONArray jaHar = (JSONArray) jBoard.get(Attributes.HARBORS.toString());
			JSONObject jaRobber = (JSONObject) jBoard.get(Attributes.ROBBER.toString());

			setHexes(jaHex);

			setConstructions(jaCon);

			setHarbors(jaHar);
			Hex robberPos = ProtocolJSONObjectConverter.getRobberPosition(jaRobber, game.getBoard());
			game.getBoard().getRobber().move(robberPos);

			if (client.getUser() instanceof AIUser) {
				AIUser aiUser = (AIUser) client.getUser();
				aiUser.getAi().setGame(game);
			} else {
				applicationController.setGame(game);
				applicationController.setClient(client);

				Platform.runLater(() -> {
					applicationController.loadGameView();
				});
			}

			if (client.getUser() instanceof HumanUser) {
				backgroundMusicPlayer.startBackgroundMusic();
			}
		}
		break;
		case BUILDING: {
			JSONObject jConstruction = (JSONObject) obj.get(Attributes.CONSTRUCTIONS.toString());
			int playerID = (int) jConstruction.get(Attributes.OWNER.toString());

			String constructionPos = ProtocolJSONObjectConverter.getPosition(jConstruction, game.getBoard());
			Player player = game.searchPlayers(playerID);

			if (client.getUser() instanceof HumanUser) {
				audioPlayer.playConstructionSound();
			}

			String constructionType = jConstruction.getString(Attributes.TYPE.toString());
			switch (constructionType) {
			case "Dorf": {
				Settlement settlement = new Settlement(player, game.getBoard().searchCorner(constructionPos));
				Platform.runLater(() -> {
					if(player.getLocalities().size()<2) {
						if(player.getLocalities().size()==1) {
							updateLastReceivedResources(settlement);
						}
					} else {
						player.getLastLostResources().set(Settlement.getCost());

						//update the PlayerTracker in ai
						if (client.getUser() instanceof AIUser && client.getUser().getPlayer().getId() != playerID) {
							AIUser aiUser = (AIUser) client.getUser();
							aiUser.getAi().getPlayerTracker(playerID).decreaseResources(player.getLastLostResources());
						}
					}
					player.addLocality(settlement);
					updateAvialableCornersForPlayers(settlement.getPosition());
					game.getBoard().addConstruction(settlement);
					player.updateVictoryPoints();

					// Update trading ratios
					Corner corner = game.getBoard().searchCorner(constructionPos);
					for (Edge edge : corner.getAdjacentEdgesOfCorner()) {
						TradingRatios cornerRatios = edge.getHarbor().getType().getTradingRatio();
						player.getTradingRatios().combine(cornerRatios);
					}
				});
			}
			break;
			case "Stadt": {
				City city = new City(player, game.getBoard().searchCorner(constructionPos));
				Platform.runLater(() -> {
					player.addCity(city);
					game.getBoard().addCityinBoard(city);
					player.updateVictoryPoints();
					player.getLastLostResources().set(City.getCost());

					//update the PlayerTracker in ai
					if (client.getUser() instanceof AIUser && client.getUser().getPlayer().getId() != playerID) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(playerID).decreaseResources(player.getLastLostResources());
					} 
				});
			}
			break;
			case "Strasse": {
				Road road = new Road(player, game.getBoard().searchEdge(constructionPos));
				Platform.runLater(() -> {
					player.addRoad(road);
					updateAvialableEdgesForPlayers(road.getPosition());
					game.getBoard().addConstruction(road);
					player.updateLongestRoadLengthProperty();
					if (player.getRoads().size()>2) {
						if (player.getCardRoadPositions().contains(game.getBoard().searchEdge(constructionPos))) {
							player.getCardRoadPositions().remove(game.getBoard().searchEdge(constructionPos));
						} else {
							player.getLastLostResources().set(Road.getCost());
							//update the PlayerTracker in ai
							if (client.getUser() instanceof AIUser && client.getUser().getPlayer().getId() != playerID) {
								AIUser aiUser = (AIUser) client.getUser();
								if(player.getCardRoadPositions().isEmpty()) {
									aiUser.getAi().getPlayerTracker(playerID).decreaseResources(player.getLastLostResources());
								} else {
									player.getCardRoadPositions().remove(game.getBoard().searchEdge(constructionPos));
								}
							}
						}
					}
				});
			}
			break;
			}
		}
		break;
		case DICE: {
			JSONArray dice = (JSONArray) obj.get(Attributes.DICE_VALUE.toString());

			int dieOne = (int) dice.get(0);
			int dieTwo = (int) dice.get(1);

			//update the PlayerTracker in ai
			if (client.getUser() instanceof AIUser) {
				AIUser aiUser = (AIUser) client.getUser();
				for(PlayerTracker playerTracker : aiUser.getAi().getPlayerTrackers()){
					playerTracker.updateResources(dieOne+dieTwo);
				}
			} 

			game.getDice().setDice(dieOne, dieTwo);
			game.getActivePlayer().getLastRolledDice().setDice(dieOne, dieTwo);
			updateLastReceivedResources(game.getDice());
		}
		break;
		case CHATMESSAGE: {
			if (client.getUser() instanceof HumanUser) {
				if (obj.has(Attributes.SENDER.toString())) {
					int sender = (int) obj.get(Attributes.SENDER.toString());
					String msg = (String) obj.get(Attributes.MESSAGE.toString());
					String playerName = lobby.getUser(sender).getName();

					chatController.displayChatMessage(playerName, msg);
					audioPlayer.playChatSound();
				}
			}
		}
		break;
		case ERROR: {
			String msg = (String) obj.get(Attributes.REPORT.toString());

			User user = client.getUser();
			if (user instanceof AIUser) {
				if (!gameHasStarted() || (gameHasStarted() && !game.isOver())) {
					((AIUser) user).getAi().makeNextMove();
				}
			} else {
				chatController.displayErrorMessage(msg);
				audioPlayer.playErrorSound();
			}
		}
		break;
		case RESOURCE_QUANTITY: {
			int playerID = (int) obj.get(Attributes.PLAYER.toString());
			Player player = game.searchPlayers(playerID);

			JSONObject jResources = obj.getJSONObject(Attributes.RESOURCES.toString());
			if (jResources.has(Attributes.UNKNOWN.toString())) {
				// Case 1: Only resources amounts available.
				Platform.runLater(() -> {
					int oldQuantity = player.getResourceQuantity();
					int additionalQuantity = jResources.getInt(Attributes.UNKNOWN.toString());
					int newQuantity = oldQuantity + additionalQuantity;

					player.setResourceQuantity(newQuantity);
					if (client.getUser() instanceof AIUser) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(player.getId()).increaseMonopoly(additionalQuantity);
						//aiUser.getAi().getPlayerTracker(player.getId()).updateResourcesTrade();
					} 
				});
			} else if (playerID != client.getUser().getId()) {
				// Case 2: Resources and amounts available, but not belong to the connected
				// client.
				Platform.runLater(() -> {
					Resources resourcesToAdd = ProtocolJSONObjectConverter.getResources(jResources);
					int oldQuantity = player.getResourceQuantity();
					int additionalQuantity = resourcesToAdd.getSum();
					int newQuantity = oldQuantity + additionalQuantity;

					player.setResourceQuantity(newQuantity);
					if (client.getUser() instanceof AIUser) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(player.getId()).updateResourcesTrade(resourcesToAdd);
					}
				});
			} else {
				// Case 3: Resources types and amounts available, for the connected client.
				Resources additionalResources = getResources(jResources);

				Platform.runLater(() -> {
					player.getResources().add(additionalResources);
					player.updateResourceQuantity();
				});
			}
		}
		break;
		case PRICE: {
			int playerID = (int) obj.get(Attributes.PLAYER.toString());
			Player player = game.searchPlayers(playerID);

			JSONObject jResources = obj.getJSONObject(Attributes.RESOURCES.toString());
			if (jResources.has(Attributes.UNKNOWN.toString())) {
				// The jResources object only contains the total amount of resources but not the
				// individual types.
				Platform.runLater(() -> {
					int oldQuantity = player.getResourceQuantity();
					int quantityToSubtract = jResources.getInt(Attributes.UNKNOWN.toString());
					int newQuantity = oldQuantity - quantityToSubtract;

					player.setResourceQuantity(newQuantity);
					if (client.getUser() instanceof AIUser) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(player.getId()).decreaseMonopoly(quantityToSubtract);
					} 
					if (client.getUser() instanceof AIUser && client.getUser().getPlayer().getId() != playerID) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(player.getId()).setCost(quantityToSubtract);
					}
				});
			} else if (playerID != client.getUser().getId()) {
				// The jResources object contains the types of resources and their amounts, but
				// not belong to the connected client.
				Platform.runLater(() -> {
					Resources resourcesToSubtract = ProtocolJSONObjectConverter.getResources(jResources);
					int oldQuantity = player.getResourceQuantity();
					int quantityToSubtract = resourcesToSubtract.getSum();
					int newQuantity = oldQuantity - quantityToSubtract;

					player.setResourceQuantity(newQuantity);
					if (client.getUser() instanceof AIUser) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(player.getId()).setCostResources(resourcesToSubtract);
					}
				});
			} else {
				// The jResources object contains the the individual types of resources and
				// their amounts.
				Resources additionalResources = getResources(jResources);

				Platform.runLater(() -> {
					player.getResources().subtract(additionalResources);
					player.updateResourceQuantity();
				});
			}
		}
		break;
		case ROBBER_MOVED: {
			if (client.getUser() instanceof HumanUser) {
				audioPlayer.playRobberSound();
			}

			// set the robber Position
			String position = ProtocolJSONObjectConverter.getHexPosition(obj, game.getBoard());
			game.getBoard().getRobber().move(game.getBoard().searchHex(position));
			if (obj.has(Attributes.PLAYER.toString()) && obj.has(Attributes.TARGET.toString())) {
				int playerId = obj.getInt(Attributes.PLAYER.toString());
				Player player = getPlayer(playerId);

				int targetId = obj.getInt(Attributes.TARGET.toString());
				Player target = getPlayer(targetId);

				if (player == client.getUser().getPlayer() && target != null) {
					if (client.getUser() instanceof AIUser && target.getResourceQuantity()+1 > 0) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(targetId).decreaseResources(1);
					} 
				} else if(target == client.getUser().getPlayer() && target != null){
					if (client.getUser() instanceof AIUser && target.getResourceQuantity()+1 > 0) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(playerId).increaseResources(1);
					} 
				} else {
					if (client.getUser() instanceof AIUser && target != null) {
						if(target.getResourceQuantity()+1 > 0){
							AIUser aiUser = (AIUser) client.getUser();
							aiUser.getAi().getPlayerTracker(targetId).decreaseResources(1);
							aiUser.getAi().getPlayerTracker(playerId).increaseResources(1);
						}
					} 
				}
			}
		}
		break;
		case DEVELOMENTCARD_BOUGHT: {
			int playerID = (int) obj.get(Attributes.PLAYER.toString());
			// searches for the player
			Player player = game.searchPlayers(playerID);
			String stringCardType = obj.getString(Attributes.DEVELOMENTCARD.toString());
			PlayableDevelopmentCardType developmentCardType;
			if ((developmentCardType = determineDevelopmentCardType(stringCardType)) != null) {
				PlayableDevelopmentCard developmentCard = new PlayableDevelopmentCard(developmentCardType);
				Platform.runLater(() -> {
					if (client.getUser() instanceof AIUser) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().setAttempt(0);
						aiUser.getAi().makeNextMove();
					}
					player.addDevelopmentCard(developmentCard);
				});
			} else {
				VictoryPointCard victoryPointCard = new VictoryPointCard();
				Platform.runLater(() -> {
					if (client.getUser() instanceof AIUser) {
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().makeNextMove();
					}
					player.addDevelopmentCard(victoryPointCard);
					player.updateVictoryPoints();
				});
			}
			Platform.runLater(() -> {
				player.getLastLostResources().set(DevelopmentCard.getCost());
				//update the PlayerTracker in ai
				if (client.getUser() instanceof AIUser && client.getUser().getPlayer().getId() != playerID) {
					AIUser aiUser = (AIUser) client.getUser();
					aiUser.getAi().getPlayerTracker(playerID).decreaseResources(player.getLastLostResources());
				} 
			});
		}
		break;
		case LONGEST_ROAD: {
			SpecialCard specialCard = game.getLongestRoadCard();
			handleSpecialCard(obj, specialCard);
		}
		break;
		case LARGEST_ARMY: {
			SpecialCard specialCard = game.getLargestArmyCard();
			handleSpecialCard(obj, specialCard);
		}
		break;
		case SERVER_REPLY: {
			String reply = (String) obj.get(ClientTypes.SERVER_REPLY.toString());

			if (client.getUser() instanceof AIUser) {
				if ((!reply.equalsIgnoreCase("OK"))) {
					if(reply.equals(getBundle().getString("PLEASE_WAIT"))){
						((AIUser) client.getUser()).getPlayer().setState(PlayerState.WAIT);
					} else if (reply.equals(getBundle().getString("NO_RESOURCES"))) {
						((AIUser) client.getUser()).getAi().setWasTrade(true);
						((AIUser) client.getUser()).getAi().setTradeWithPlayer(false);
						((AIUser) client.getUser()).getAi().makeNextMove();
					} else if (reply.equals(getBundle().getString("NO_KNIGHT_CARD"))) {
						((AIUser) client.getUser()).getAi().setAllowdToPlayDevCard(false);
						((AIUser) client.getUser()).getAi().makeNextMove();
					} else if (reply.equals(getBundle().getString("NO_MONOPOL_CARD"))) {
						((AIUser) client.getUser()).getAi().setAllowdToPlayDevCard(false);
						((AIUser) client.getUser()).getAi().makeNextMove();
					} else if (reply.equals(getBundle().getString("NO_PLENTY_YEAR_CARD"))) {
						((AIUser) client.getUser()).getAi().setAllowdToPlayDevCard(false);
						((AIUser) client.getUser()).getAi().makeNextMove();
					} else if (reply.equals(getBundle().getString("NO_BUILD_ROAD_CARD"))) {
						((AIUser) client.getUser()).getAi().setAllowdToPlayDevCard(false);
						((AIUser) client.getUser()).getAi().makeNextMove();
					} else {
						((AIUser) client.getUser()).getAi().makeNextMove();
					}
				}
			}
			if (!reply.equalsIgnoreCase("OK")) {
				if (client.getUser() instanceof HumanUser) {
					chatController.displayErrorMessage(reply);
					audioPlayer.playErrorSound();
				}
			}
		}
		break;
		case PLAY_MONOPOLY: {
			int playerId = obj.getInt(Attributes.PLAYER.toString());
			Player player = getPlayer(playerId);

			ResourceType resourceType = getResourceType(obj.getString(Attributes.RESOURCE.toString()));

			if (player == client.getUser().getPlayer()) {
				removeDevelopmentCard(PlayableDevelopmentCardType.MONOPOLY);
			} else {
				Platform.runLater(() -> player.getDevelopmentCards().remove(player.getDevelopmentCards().size() - 1));
			}
			if (client.getUser() instanceof AIUser) {
				for(Player p : game.getPlayers()){
					if(p.getId() != client.getUser().getPlayer().getId() && p.getId() == playerId){
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(p.getId()).increaseResourcesMonopoly(resourceType);
					}else if((p.getId() != client.getUser().getPlayer().getId() && p.getId() != playerId)){
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().getPlayerTracker(p.getId()).decreaseResourcesMonopoly(resourceType);
					}
				}
			}
		}
		break;
		case PLAY_KNIGHT: {
			int playerId = obj.getInt(Attributes.PLAYER.toString());
			Player player = getPlayer(playerId);
			int targetId = obj.getInt(Attributes.TARGET.toString());
			Player target = getPlayer(targetId);

			if (player == client.getUser().getPlayer()) {
				removeDevelopmentCard(PlayableDevelopmentCardType.KNIGHT);
				if (client.getUser() instanceof AIUser) {
					AIUser aiUser = (AIUser) client.getUser();
					aiUser.getAi().getPlayerTracker(targetId).decreaseResources(1);
				} 
			} else if(target == client.getUser().getPlayer()){
				if (client.getUser() instanceof AIUser) {
					AIUser aiUser = (AIUser) client.getUser();
					aiUser.getAi().getPlayerTracker(playerId).increaseResources(1);
				} 
			} else {
				if (client.getUser() instanceof AIUser) {
					AIUser aiUser = (AIUser) client.getUser();
					aiUser.getAi().getPlayerTracker(targetId).decreaseResources(1);
					aiUser.getAi().getPlayerTracker(playerId).increaseResources(1);
				} 
				Platform.runLater(() -> player.getDevelopmentCards().remove(player.getDevelopmentCards().size() - 1));
			}
		}
		break;
		case PLAY_ROAD_BUILDING: {
			int playerId = obj.getInt(Attributes.PLAYER.toString());
			Player player = getPlayer(playerId);

			String stringPosition_1 = ProtocolJSONObjectConverter.getStreetOnePosition(obj, game.getBoard());
			player.getCardRoadPositions().add(game.getBoard().searchEdge(stringPosition_1));
			if(obj.has(Attributes.STREAT_TWO.toString())) {
				String stringPosition_2 = ProtocolJSONObjectConverter.getStreetTwoPosition(obj, game.getBoard());
				player.getCardRoadPositions().add(game.getBoard().searchEdge(stringPosition_2));
			}

			if (player == client.getUser().getPlayer()) {
				removeDevelopmentCard(PlayableDevelopmentCardType.ROAD_BUILDING);
			} else {
				Platform.runLater(() -> player.getDevelopmentCards().remove(player.getDevelopmentCards().size() - 1));
			}
		}
		break;
		case PLAY_YEAR_OF_PLENTY: {
			int playerId = obj.getInt(Attributes.PLAYER.toString());
			Player player = getPlayer(playerId);

			if (player == client.getUser().getPlayer()) {
				removeDevelopmentCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY);
			} else {
				if (client.getUser() instanceof AIUser) {
					AIUser aiUser = (AIUser) client.getUser();
					aiUser.getAi().getPlayerTracker(playerId).increaseResources(2);
				} 
				Platform.runLater(() -> player.getDevelopmentCards().remove(player.getDevelopmentCards().size() - 1));
			}
		}
		break;
		case TRADE_OFFER: {
			//überprüfe ob spieler eine ai
			//if player == ai then ai.acceptTade()
			Trade trade = new Trade();

			int playerId = obj.getInt(Attributes.PLAYER.toString());
			Player player = getPlayer(playerId);
			trade.setPlayer(player);
			int tradeID = obj.getInt(Attributes.TRADING_ID.toString());
			trade.setTradeID(tradeID);
			Resources supply = getResources(obj.getJSONObject(Attributes.SUPPLY.toString()));
			Resources demand = getResources(obj.getJSONObject(Attributes.DEMAND.toString()));
			trade.setTradeOffer(supply);
			trade.setTradeRequest(demand);

			if (client.getUser() instanceof AIUser) {   //ai
				game.setTrade(trade);
				//other offer trade
				if (client.getUser().getId()!=playerId) {
					//consider whether accept trade
					((AIUser) client.getUser()).getAi().acceptTrade(trade);
				}
			} else {
				Platform.runLater(() -> game.setTrade(trade));
			}
		}
		break;
		case GAME_FINISHED: {
			// Set Winner
			int winnerId = obj.getInt(Attributes.WINNER.toString());
			Platform.runLater(() -> game.setWinner(getPlayer(winnerId)));
			game.setGameOver(true);

			// Stop music.
			backgroundMusicPlayer.stopBackgroundMusic();

			// Play sound
			if (client.getUser() instanceof HumanUser) {
				if (getPlayer(winnerId) == client.getUser().getPlayer()) {
					audioPlayer.playVictorySound();
				} else {
					audioPlayer.playDefeatSound();
				}
			}

			// Close connection to server.
			client.stopRunning();
		}
		break;
		case TRADE_ACCEPTED:{
			int fellowPlayerId = obj.getInt(Attributes.FELLOW_PLAYER.toString());
			Player fellowPlayer = game.searchPlayers(fellowPlayerId);

			boolean hasAccepted = obj.getBoolean(Attributes.ACCEPT.toString());

			if(client.getUser().getId()==game.getActivePlayer().getId()) {
				// Case 1: This player started the trade.
				if (hasAccepted) {
					if (client.getUser() instanceof AIUser && game.getTrade() != null) {
						((AIUser) client.getUser()).getAi().addTradeActor(fellowPlayer);
						((AIUser) client.getUser()).getAi().executeTrade(fellowPlayer, game.getTrade(),game.getPlayersNumberInGame());
					} else {
						Platform.runLater(() -> {
							if (game.getTrade() != null) {
								game.getTrade().addAcceptingPlayer(fellowPlayer);
							}
						});
					}
				} else {
					if (client.getUser() instanceof AIUser && game.getTrade() != null) {//one ai rejects trade
						//trade is offered by this ai
						((AIUser) client.getUser()).getAi().addTradeActor(fellowPlayer);
						if (((AIUser) client.getUser()).getAi().getTradeActor().size()==game.getPlayersNumberInGame()-1) {
							((AIUser) client.getUser()).getAi().cancelTrade(game.getTrade());
						}
					} else {
						Platform.runLater(() -> {
							if (game.getTrade() != null) {
								game.getTrade().addDecliningPlayer(fellowPlayer);

								if (game.haveAllPlayersDeclinedTheTrade() && client.getUser() instanceof AIUser) {
									((AIUser) client.getUser()).getAi().cancelTrade(game.getTrade());
									game.setTrade(null);
								}
							}
						});
					}
				}
			} else if(client.getUser().getId()==fellowPlayerId) {
				if (hasAccepted) {
					// Case 2: This player accepted the trade.
					Platform.runLater(() -> {
						if (game.getTrade() != null) {
							game.getTrade().addAcceptingPlayer(fellowPlayer);
						}
					});
				} else {
					// Case 3: This player declined the trade.
					Platform.runLater(() -> {
						if (game.getTrade() != null) {
							game.getTrade().addDecliningPlayer(fellowPlayer);
						}
					});
				}
			}
		}
		break;
		case TRADE_CANCELED:{
			int playerId = obj.getInt(Attributes.PLAYER.toString());

			if (client.getUser().getId() == playerId) {
				if (game.getActivePlayer().getId()==playerId) {
					if (client.getUser() instanceof AIUser) {
						((AIUser) client.getUser()).getAi().setTradeWithPlayer(false);
						((AIUser) client.getUser()).getAi().resetTradeActor();
						game.setTrade(null);
						((AIUser) client.getUser()).getAi().setWasTrade(true);
						((AIUser) client.getUser()).getAi().makeNextMove();
					} else {
						Platform.runLater(() -> game.setTrade(null));
					}
				} else {
					Platform.runLater(() -> game.getTrade().getAcceptedList().remove(game.searchPlayers(playerId)));
					//*** current AI can not withdraw accept trade(ai decision is not affected by decision from others), 
					//*** if he can, following code should be used
					/*
					if (client.getUser() instanceof AIUser) {
						Player udonPlayer = game.searchPlayers(playerId);
						((AIUser) client.getUser()).getAi().minusTradeActor(udonPlayer);
						game.getTrade().getAcceptedList().remove(game.searchPlayers(playerId));
						((AIUser) client.getUser()).getAi().makeNextMove();
					} else {
						Platform.runLater(() -> game.getTrade().getAcceptedList().remove(game.searchPlayers(playerId)));
					}*/
				}
			} else {
				if (game.getActivePlayer().getId()==client.getUser().getId()) {
					if (client.getUser() instanceof AIUser) {
						Player udonPlayer = game.searchPlayers(playerId);
						((AIUser) client.getUser()).getAi().minusTradeActor(udonPlayer);
					} else {
						Platform.runLater(() -> game.getTrade().getAcceptedList().remove(game.searchPlayers(playerId)));
					}
				} else if (game.getActivePlayer().getId() == playerId) {
					if (client.getUser() instanceof AIUser) {
						game.setTrade(null);
					} else {
						Platform.runLater(() -> game.setTrade(null));
					}
				}
			}
		}
		break;
		case TRADE_EXECUTED:{
			int fellowPlayerId = obj.getInt(Attributes.FELLOW_PLAYER.toString());
			Player fellowPlayer = game.searchPlayers(fellowPlayerId);
			Platform.runLater(() -> {
				if (client.getUser().getId() == game.getActivePlayer().getId()||client.getUser().getId() == fellowPlayerId) {
					if (client.getUser() instanceof HumanUser) {
						audioPlayer.playTradeSound();
					}

					Trade trade = game.getTrade();
					Resources offer = trade.getTradeOffer();
					Resources request = trade.getTradeRequest();
					if(client.getUser().getId() == game.getActivePlayer().getId()) {
						trade.getPlayer().getLastLostResources().set(offer);
						trade.getPlayer().getLastReceivedResources().set(request);
					} else if (client.getUser().getId() == fellowPlayerId){
						fellowPlayer.getLastLostResources().set(trade.getTradeRequest());
						fellowPlayer.getLastReceivedResources().set(trade.getTradeOffer());
					}
					game.setTrade(null);
					if (client.getUser() instanceof AIUser && client.getUser().getPlayer() == game.getActivePlayer()){
						AIUser aiUser = (AIUser) client.getUser();
						aiUser.getAi().setWasTrade(true);
						aiUser.getAi().setTradeWithPlayer(false);
						aiUser.getAi().makeNextMove();
					}
				} else {
					game.setTrade(null);
				}});
			break;
		}
		}
	}

	/**
	 * updates the avialableEdges for all players
	 * @param edge the specific available edge
	 */
	private void updateAvialableEdgesForPlayers(Edge edge) {
		for(Player p : game.getPlayers()){
			p.updateAvialableEdges(edge);
		}
	}

	/**
	 * updates the avialableCorners for all players
	 * @param corner the available corner
	 */
	private void updateAvialableCornersForPlayers(Corner corner) {
		for(Player p : game.getPlayers()){
			p.updateAvialableCorners(corner);
		}
	}

	/**Checks if a new round of human user started
	 * @param jsonPlayer or player as jsonObject
	 * @return true or false
	 */
	private boolean newRoundOfHumanUserStarted(JSONObject jsonPlayer) {
		Player player = getPlayer(jsonPlayer.getInt(Attributes.ID.toString()));
		PlayerState oldState = player.getState();
		PlayerState newState = getState(jsonPlayer);

		return (oldState == PlayerState.WAIT
				&& newState == PlayerState.ROLL_DICE
				&& client.getUser().getPlayer() == player
				&& !(player.getUser() instanceof AIUser));
	}

	/**
	 * Deletes the {@code User} specified by the {@code JSONObject} from the
	 * {@code Lobby}.
	 * 
	 * @param userOrPlayer
	 *            the {@link JSONObject} containing the {@link User}.
	 * @see Lobby
	 */
	private void deleteUser(JSONObject userOrPlayer) {
		int userId = userOrPlayer.getInt(Attributes.ID.toString());
		User disconnectedUser = lobby.getUser(userId);
		Platform.runLater(() -> lobby.getUsers().remove(disconnectedUser));
	}

	/**
	 * Changes the ownership of the {@code SpecialCard} and updates the victory
	 * points of the affected {@code Players}.
	 * 
	 * @param obj
	 *            the {@link JSONObject} containing information about the new owner
	 *            of the {@link SpecialCard}.
	 * @param specialCard
	 *            the {@link SpecialCard}.
	 * @see Player
	 */
	private void handleSpecialCard(JSONObject obj, SpecialCard specialCard) {
		if (obj.has(Attributes.PLAYER.toString())) {
			// Get variables.
			int playerID = (int) obj.get(Attributes.PLAYER.toString());
			Player newOwner = game.searchPlayers(playerID);
			Player previousOwner = specialCard.getOwner();

			Platform.runLater(() -> {
				// Change ownership of the card.
				specialCard.giveTo(newOwner);

				// Play sound.
				if (client.getUser() instanceof HumanUser) {
					audioPlayer.playSpecialCardSound();
				}

				// Update victory points.
				if (previousOwner != null) {
					previousOwner.updateVictoryPoints();
				}
				newOwner.updateVictoryPoints();
			});
		} else {
			// Get variables.
			Player previousOwner = specialCard.getOwner();

			// Change ownership of the card.
			Platform.runLater(() -> {
				specialCard.resetOwnership();

				// Update victory points.
				previousOwner.updateVictoryPoints();
			});
		}
	}

	/**
	 * Sends a message to the {@code Server} to request a color and/or name update
	 * of the {@code User} associated with this instance of the application.
	 * 
	 * @see Server
	 * @see User
	 */
	private void sendUserUpdateToServer() {
		JSONObject message = new JSONObject();
		message.put(Attributes.NAME.toString(), client.getUser().getName());

		String color = ProtocolStringConverter.getName(client.getUser().getColor());
		if (color == null) {
			// The protocol does not allow the user name to be send on its own. In order to
			// send the user name, it is
			// required to send a color, too. If no color has been selected, the color red
			// will be sent.
			message.put(Attributes.COLOR.toString(), ProtocolStringConverter.getName(Color.RED));
		} else {
			message.put(Attributes.COLOR.toString(), color);
		}

		client.sendToServer(Attributes.PLAYER.toString(), message);
	}

	/**
	 * Creates a new {@code User} with the specified id and adds them to the
	 * {@code Lobby}.
	 * 
	 * @param userId
	 *            the id of the {@link User}.
	 * @see Lobby
	 */
	private void createNewUser(int userId) {
		User user = new HumanUser();
		user.setId(userId);
		lobby.addUser(user);
	}

	/**
	 * Uses a string representation of a {@code ClientType} to determine the actual
	 * type of a message.
	 * 
	 * @param typeString
	 *            the string representation of the {@link ClientTypes ClientType}.
	 * @return the actual ClientType of the message.
	 */
	private ClientTypes determineMessageType(String typeString) {
		ClientTypes messageType = null;

		for (ClientTypes info : ClientTypes.values()) {
			if (typeString.equals(info.toString())) {
				messageType = info;
				break;
			}
		}

		return messageType;
	}

	/**
	 * Uses a string representation of a {@code DevelopmentCardType} to determine
	 * the actual type of the {@code DevelopmentCard}.
	 * 
	 * @param typeString
	 *            the string representation of the {@code DevelopmentCardType}.
	 * @return the actual DevelopmentCardType of the {@link DevelopmentCard}.
	 */
	private PlayableDevelopmentCardType determineDevelopmentCardType(String typeString) {
		PlayableDevelopmentCardType cardType = null;

		for (PlayableDevelopmentCardType ct : PlayableDevelopmentCardType.values()) {
			if (typeString.equals(ct.toString())) {
				cardType = ct;
				break;
			}
		}

		return cardType;
	}

	/**
	 * Updates the characteristics of a {@code Player} with the information inside a
	 * {@code JSONObject}.
	 * 
	 * @param object
	 *            the {@link JSONObject}
	 * @see Player
	 */
	private void updatePlayer(JSONObject object) {
		Platform.runLater(() -> {
			// Find the player to update.
			int playerId = object.getInt(Attributes.ID.toString());
			Player player = getPlayer(playerId);

			// Update name.
			if (object.has(Attributes.NAME.toString())) {
				String playerName = object.getString(Attributes.NAME.toString());
				player.setName(playerName);
			}

			// Update color.
			if (object.has(Attributes.COLOR.toString())) {
				String colorName = object.getString(Attributes.COLOR.toString());
				Color color = ProtocolStringConverter.getColor(colorName);
				player.setColor(color);
			}

			// Update victory points.
			if (object.has(Attributes.VICTORY_POINTS.toString())) {
				int victoryPoints = object.optInt(Attributes.VICTORY_POINTS.toString());
				player.setVictoryPoints(victoryPoints);
			}

			// Update resources.
			JSONObject jResources = object.getJSONObject(Attributes.RESOURCES.toString());

			if (jResources.has(Attributes.UNKNOWN.toString())) {
				// The jResources object only contains the total amount of resources but not the
				// individual types.
				int resourceQuantity = jResources.getInt(Attributes.UNKNOWN.toString());
				player.setResourceQuantity(resourceQuantity);
			} else {
				// The jResources object contains the the individual types of resources and
				// their amounts.
				Resources resources = ProtocolStringConverter.getResources(jResources);

				player.getResources().set(resources);
				player.updateResourceQuantity();
			}

			// Update played knight cards.
			if (object.has(Attributes.ARMY.toString())) {
				int playedKnightCards = object.optInt(Attributes.ARMY.toString());
				player.setPlayedKnightCards(playedKnightCards);
			}

			// Update state
			if (object.has(Attributes.STATE.toString())) {
				PlayerState state = getState(object);
				if (state != player.getState()) {
					player.setState(state);
				}

				// Update active player
				if (state != PlayerState.WAIT) {
					game.setActivePlayer(player);
				}
			}
		});
	}

	/**
	 * Returns the {@code Player} with the specified id.
	 * 
	 * @param id
	 *            the id of the {@link Player}
	 * @return the player with the specified id.
	 */
	private Player getPlayer(int id) {
		for (Player player : game.getPlayers()) {
			if (player.getId() == id) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Updates the characteristics of a {@code User} with the information inside a
	 * {@code JSONObject}.
	 * 
	 * @param object
	 *            the {@link JSONObject}
	 * @see User
	 */
	private void updateUser(JSONObject object) {
		int userId = object.getInt(Attributes.ID.toString());
		User user = lobby.getUser(userId);
		PlayerState state = getState(object);
		user.setState(state);

		if (!object.isNull(Attributes.NAME.toString())) {
			String name = object.getString(Attributes.NAME.toString());
			Platform.runLater(() -> user.setName(name));
		}

		if (object.has(Attributes.COLOR.toString())) {
			String colorName = object.getString(Attributes.COLOR.toString());
			Color color = ProtocolStringConverter.getColor(colorName);
			Platform.runLater(() -> {
				user.setColor(color);
			});
		}
	}

	/**
	 * Returns the {@code PlayerState} specified inside a {@code JSONObject}.
	 * 
	 * @param object
	 *            the {@link JSONObject}
	 * @return the {@link PlayerState}
	 */
	private PlayerState getState(JSONObject object) {
		String stateString = object.optString(Attributes.STATE.toString());

		for (PlayerState state : PlayerState.values()) {
			if (state.toString().equals(stateString)) {
				return state;
			}
		}

		return null;
	}

	/**
	 * Specifies whether the {@code Game} has started or not.
	 * 
	 * @return true, if the {@link Game} has started. false, otherwise.
	 */
	private boolean gameHasStarted() {
		return game != null;
	}

	/**
	 * Sets the id of the {@code User} associated with this instance of the
	 * application to the id specified inside a {@code JSONObject}.
	 * 
	 * @param obj
	 *            the {@link JSONObject}.
	 */
	private void setUserID(JSONObject obj) {
		int userId = obj.getInt(Attributes.ID.toString());
		client.getUser().setId(userId);
	}

	/**
	 * Sends the protocol version of this client to the {@code Server}.
	 * 
	 * @see Server
	 */
	private void sendClientProtocolVersionToServer() {
		JSONObject message = new JSONObject();

		if (client.getUser() instanceof HumanUser) {
			message.put(Attributes.VERSION.toString(), Attributes.VERSION_VALUE.toString());
		} else {
			message.put(Attributes.VERSION.toString(), Attributes.VERSION_VALUE_KI.toString());
		}

		client.sendToServer(ServerTypes.HELLO.toString(), message);
	}

	/**
	 * Removes a developmentCard of the specified type from the list of
	 * DevelopmentCards of the player that is associated with this client.
	 * 
	 * @param type
	 *            the {@code PlayableDevelopmentCardType}
	 */
	private void removeDevelopmentCard(PlayableDevelopmentCardType type) {
		Player player = client.getUser().getPlayer();
		for (DevelopmentCard card : player.getDevelopmentCards()) {
			if (card instanceof PlayableDevelopmentCard) {
				if (((PlayableDevelopmentCard) card).getType() == type) {
					Platform.runLater(() -> {
						player.getDevelopmentCards().remove(card);
						player.setHasPlayedDevelopmentCardThisTurn();
						if (client.getUser() instanceof AIUser) {
							AIUser aiUser = (AIUser) client.getUser();
							aiUser.getAi().setAttempt(0);
							aiUser.getAi().makeNextMove();
						}
					});

					break;
				}
			}
		}
	}

	/**
	 * Updates the resources the player got from rolling the dice. 
	 * They are displayed next to the player info. 
	 * @param dice The {@code Dice} that have just been rolled.
	 */
	private void updateLastReceivedResources(Dice dice) {
		for(Player player: game.getPlayers()) {
			Resources resources = new Resources();
			for(Locality locality: player.getLocalities()) {
				for(Hex hex : locality.getPosition().getAdjacentHexesOfCorner()) {
					if(hex.getToken().getNumber() == dice.getDieOneNumber()+dice.getDieTwoNumber()) {
						resources.addOneTypeResource(locality.getResourceMultiplier(), hex.getType().toResourceType());
					}
				}
			}
			Platform.runLater(() -> player.getLastReceivedResources().set(resources));
		}
	}

	/**
	 * Updates the resources the player got from building the second settlement 
	 * in the initial building phase. 
	 * They are displayed next to the player info. 
	 * @param settlement The {@code Settlement} that has just been built.
	 */
	private void updateLastReceivedResources(Settlement settlement) {
		Resources resources = new Resources();
		for (Hex hex : settlement.getPosition().getAdjacentHexesOfCorner()) {
			resources.add(hex.getResources());
		}

		Player player = settlement.getOwner();
		player.getLastReceivedResources().set(resources);
		int playerID = player.getId();

		//update the PlayerTracker in ai
		if (client.getUser() instanceof AIUser && client.getUser().getPlayer().getId() != playerID) {
			AIUser aiUser = (AIUser) client.getUser();
			aiUser.getAi().getPlayerTracker(playerID).updateResources(player.getLastReceivedResources());
		}
	}

	/**
	 * Gets the Values of the Resources which the server send and returns the
	 * corresponding {@code Resource} object.
	 * 
	 * @param jRessources
	 *            JSONObject containing the resources.
	 * @return the {@link Resources}.
	 */
	private Resources getResources(JSONObject jRessources) {

		Resources resources = new Resources();

		int lumber = 0;
		int wool = 0;
		int brick = 0;
		int grain = 0;
		int ore = 0;

		if (jRessources.has("Holz")) {
			lumber = (int) jRessources.get("Holz");
		}
		if (jRessources.has("Wolle")) {
			wool = (int) jRessources.get("Wolle");
		}
		if (jRessources.has("Lehm")) {
			brick = (int) jRessources.get("Lehm");
		}
		if (jRessources.has("Getreide")) {
			grain = (int) jRessources.get("Getreide");
		}
		if (jRessources.has("Erz")) {
			ore = (int) jRessources.get("Erz");
		}
		resources.getResources().put(ResourceType.LUMBER, lumber);
		resources.getResources().put(ResourceType.WOOL, wool);
		resources.getResources().put(ResourceType.BRICK, brick);
		resources.getResources().put(ResourceType.GRAIN, grain);
		resources.getResources().put(ResourceType.ORE, ore);
		return resources;
	}

	/**
	 * Gets the corresponding HexType of the String
	 * 
	 * @param string he description of hexTypes as string
	 * @return the corresponding {@code HexType}
	 */
	private HexType getHexType(String string) {

		switch (string) {
		case "Ackerland":
			return HexType.FIELDS;
		case "Huegelland":
			return HexType.HILLS;
		case "Weideland":
			return HexType.PASTURE;
		case "Wald":
			return HexType.FOREST;
		case "Gebirge":
			return HexType.MOUNTAIN;
		case "Wueste":
			return HexType.DESERT;
		case "Meer":
			return HexType.WATER;
		}
		return null;

	}

	/**
	 * Gets the corresponding HarborType of the String
	 * 
	 * @param string the description of HarborTypes as string
	 * @return the {@code HarborType}
	 */
	private HarborType getHarborType(String string) {

		switch (string) {
		case "Holz Hafen":
			return HarborType.LUMBER;
		case "Lehm Hafen":
			return HarborType.BRICK;
		case "Wolle Hafen":
			return HarborType.WOOL;
		case "Erz Hafen":
			return HarborType.ORE;
		case "Getreide Hafen":
			return HarborType.GRAIN;
		case "Hafen":
			return HarborType.UNIVERSAL;
		}
		return null;

	}

	/**
	 * Gets the corresponding ResourceType of the String
	 * 
	 * @param type the type as a String
	 * @return the corresponding {@code ResourceType}
	 */
	private ResourceType getResourceType(String type){
		switch(type){
		case "Holz": return ResourceType.LUMBER;
		case "Getreide": return ResourceType.GRAIN;
		case "Lehm": return ResourceType.BRICK;
		case "Wolle": return ResourceType.WOOL;
		case "Erz": return ResourceType.ORE;
		}
		return null;
	}

	/**
	 * Getter for the {@code ChatController}, that is used to display messages in
	 * the chat.
	 * 
	 * @return the ChatController
	 */
	public synchronized ChatController getChatController() {
		return chatController;
	}

	/**
	 * Sets the {@code ChatController} which is used to display chat messages.
	 * 
	 * @param chatController
	 *            the {@link ChatController}
	 */
	public synchronized void setChatController(ChatController chatController) {
		this.chatController = chatController;
	}

	/**
	 * creates a new Game with new players, for each Users
	 */
	private void createNewGame() {
		String name;
		Color color;
		int id;
		PlayerState state;
		List<Player> players = new ArrayList<>();

		for (User user : lobby.getUsers()) {
			name = user.getName();
			color = user.getColor();
			id = user.getId();
			state = user.getState();
			String url = user.getImageLocation();
			Player player = new Player(id, name, url, color, state);
			players.add(player);
			if (id == client.getUser().getId()) {
				client.getUser().setPlayer(player);
				player.setUser(client.getUser());
			}
		}

		game = new Game(players);
	}

	/**
	 * Gets a JSONArray of hexes and changes the Type and Token accordingly
	 * 
	 * @param jaHex JSONArray of hexesjaHex
	 */
	private void setHexes(JSONArray jaHex) {
		for (Object object : jaHex) {
			JSONObject joHex = (JSONObject) object;
			//			String hexPos = (String) joHex.get(Attributes.POSITION.toString());
			String hexPos = ProtocolJSONObjectConverter.getHexPosition(joHex, game.getBoard());
			HexType hexType = getHexType(joHex.getString(Attributes.TYPE.toString()));
			int hexToken;
			try {
				hexToken = (int) joHex.get(Attributes.NUMBER.toString());
			} catch (Exception e) {
				hexToken = 0;
			}

			Hex hex = game.getBoard().searchHex(hexPos);
			hex.setToken(new Token(hexToken, hex));
			hex.setHexQuality();
			hex.setType(hexType);
		}
	}

	/**
	 * Gets the JSONArray of constructions and adds the construction to the Player
	 * and the Board
	 * 
	 * @param jaCon JSONArray of constructions jaCon
	 */
	private void setConstructions(JSONArray jaCon) {
		ArrayList<Construction> constructions = new ArrayList<Construction>();
		for (Object object : jaCon) {
			JSONObject joCon = (JSONObject) object;
			int conOwner = (int) joCon.get(Attributes.OWNER.toString());
			Player player = game.searchPlayers(conOwner);
			//			String conPos = (String) joCon.get(Attributes.POSITION.toString());
			String conPos = ProtocolJSONObjectConverter.getPosition(joCon, game.getBoard());
			String conType = (String) joCon.get(Attributes.TYPE.toString());
			switch (conType) {
			case "Dorf": {
				Settlement settlement = new Settlement(player, game.getBoard().searchCorner(conPos));
				Platform.runLater(() -> constructions.add(settlement));
				player.addLocality(settlement);
			}
			break;
			case "Stadt": {
				City city = new City(player, game.getBoard().searchCorner(conPos));
				Platform.runLater(() -> constructions.add(city));
				player.addLocality(city);
			}
			break;
			case "Strasse": {
				Road road = new Road(player, game.getBoard().searchEdge(conPos));
				Platform.runLater(() -> constructions.add(road));
				player.addRoad(road);
			}
			break;
			}
		}
	}

	/**
	 * Gets the JSONArray of harbors and changes the HarborType in {@link Board}
	 * accordingly
	 * 
	 * @param jaHar JSONArray of harbors jaHar
	 */
	private void setHarbors(JSONArray jaHar) {
		for (Edge edge : game.getBoard().getEdges()) {
			edge.getHarbor().setType(HarborType.NONE);
		}
		for (Object object : jaHar) {
			JSONObject joHar = (JSONObject) object;
			//			String harPos = (String) joHar.get(Attributes.POSITION.toString());
			String harPos = ProtocolJSONObjectConverter.getPosition(joHar, game.getBoard());
			HarborType harType = getHarborType(joHar.getString(Attributes.TYPE.toString()));

			Edge edge = game.getBoard().searchEdge(harPos);
			edge.getHarbor().setType(harType);

		}
	}

	/**
	 * Checks if the clients protocol is supported by the {@code Server}.
	 * 
	 * @param obj
	 *            the {@link JSONObject} containing the clients protocol
	 *            information.
	 * @return true, if the protocol is supported. false, otherwise.
	 */
	private boolean usesSupportedProtocol(JSONObject obj) {
		String serverProtocolVersion = Attributes.PROTOCOL_VALUE.toString();
		String clientProtocolVersion = obj.getString(Attributes.PROTOCOL.toString());

		return serverProtocolVersion.equals(clientProtocolVersion);
	}

	/**
	 * Getter for the bundle of the current language.
	 * @return the ResourceBundle
	 */
	private ResourceBundle getBundle() {
		return bundleProperty.get();
	}

	/**
	 * Stops the background music.
	 */
	public void stopBackgroundMusic() {
		backgroundMusicPlayer.stopBackgroundMusic();
	}

}
