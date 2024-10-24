package game.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import game.Animations;
import game.board.hexes.Hex;
import game.board.robber.Robber;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.dice.Dice;
import interfaces.Controller;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;
import network.server.Server;

/**
 * Controls the content of the Info Sheet about the {@code Players} 
 * @author Svenja Schoett
 * @author Paula Wikidal
 * @see Player
 */
public class PlayerInfoController implements Controller<Player> {

	/** The root pane of the player info */
	@FXML private StackPane playerInfo;

	/** The label containing the text 'active' */
	@FXML private Label activeLabel;

	/** The pane containing the marker that signals if the player is active*/
	@FXML private BorderPane activePlayerMarkerPane;

	/**The ImageView containing the profile picture. */
	@FXML private ImageView profilePicture;

	/**The Label with the name of the player. */
	@FXML private Label nameLabel;

	/**The Label that shows the number of resource cards. */
	@FXML private Label resourceCardsLabel;

	/**The Label that shows the number of development cards. */
	@FXML private Label developmentCardsLabel;

	/**The Label that shows the number of played knight cards. */
	@FXML private Label knightsLabel;

	/**The Image of the knights label.*/
	@FXML private ImageView knightsImage;

	/**The Image of the longest road label if the player has the largest army.*/
	@FXML private ImageView largestArmyImage;

	/**The Label that shows the length of the players longest road. */
	@FXML private Label longestRoadLabel;

	/**The Image of the longest road label.*/
	@FXML private ImageView longestRoadImage;

	/**The Image of the longest road label if the player has the longest road.*/
	@FXML private ImageView longestRoadVictoryPointImage;

	/**The Label that shows the number of victory point. */
	@FXML private Label victoryPointsLabel;

	/**The Label that shows how many roads the player still has left to build.*/
	@FXML private Label roadsLabel;

	/**The Label that shows how many settlements the player has left to build overall.*/
	@FXML private Label settlementsLabel;

	/**The Label that shows how many cities the player has left to build.*/
	@FXML private Label citiesLabel;

	/** The VBox containing the displays of lost and received resources */
	@FXML private VBox newAndLostResources;

	/** The clickable part of the player info */
	@FXML private VBox clickablePart;

	/** The tooltip of the resource cards label */
	@FXML private Tooltip resourcesTTip;

	/** The tooltip of the development cards label */
	@FXML private Tooltip developmentTTip;

	/** The tooltip of the played knight cards label */
	@FXML private Tooltip knightsTTip;

	/** The tooltip of the longest road label */
	@FXML private Tooltip longestRoadTTip;

	/** The tooltip of the roads label */
	@FXML private Tooltip roadsTTip;

	/** The tooltip of the settlements label */
	@FXML private Tooltip settlementsTTip;

	/** The tooltip of the cities label */
	@FXML private Tooltip citiesTTip;
	
	/**The imageView showing the number on the first die*/
	@FXML private ImageView diceImageOne;
	
	/**The imageView showing the number on the second die*/
	@FXML private ImageView diceImageTwo;

	/**
	 * The {@code Player} whose information is used by the view controlled by this controller.
	 * @see Player
	 */
	private Player player;

	/**
	 * The {@code ClientConnection} used to send messages to the {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client;

	/**
	 * The {@code Hex} which has been selected to move the {@code Robber} to.
	 * @see Hex
	 * @see Robber
	 */
	private Hex hex;

	/**
	 * The {@code PlayerInfoControllers} required to select the target when the {@code Robber} moves.
	 * @see PlayerInfoController
	 * @see Robber
	 */
	private List<PlayerInfoController> playerInfos = new ArrayList<>();

	/**
	 * Binds the language of the tooltips to the language selected in the settings.
	 */
	@FXML
	private void initialize() {
		resourcesTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("RESOURCE_CARDS"));
		developmentTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("DEVELOPMENT_CARDS"));
		knightsTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("KNIGHTS"));
		longestRoadTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("LONGEST_ROAD"));
		roadsTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ROADS_LEFT"));
		settlementsTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("SETTLEMENTS_LEFT"));
		citiesTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("CITIES_LEFT"));	
	}

	/**
	 * Sets the text on the labels and binds it where necessary to the right properties of the player.
	 */
	private void fillPlayerInfo() {
		this.profilePicture.setImage(new Image(player.getImageLocation()));
		this.nameLabel.setText(player.getName());

		this.resourceCardsLabel.textProperty().bind(player.resourceQuantityProperty().asString());
		this.developmentCardsLabel.textProperty().bind(player.developmentCardsProperty().sizeProperty().asString());

		this.knightsLabel.textProperty().bind(player.playedKnightCardsProperty().asString());
		this.knightsLabel.graphicProperty().bind(Bindings.createObjectBinding(() -> {
			if (player.hasLargestArmyCard()) {
				largestArmyImage.setScaleX(1.5);
				largestArmyImage.setScaleY(1.5);

				boolean didPlayerHaveLargestArmyBefore = largestArmyImage.getParent() == knightsLabel;
				if (!didPlayerHaveLargestArmyBefore) {
					Animations.playFadeTransition(largestArmyImage);	
				}

				return largestArmyImage;
			} else {
				largestArmyImage.setScaleX(1);
				largestArmyImage.setScaleY(1);
				return knightsImage;
			}
		}, player.specialCardsProperty()));

		this.longestRoadLabel.textProperty().bind(player.longestRoadLengthProperty().asString());
		this.longestRoadLabel.graphicProperty().bind(Bindings.createObjectBinding(() -> {			
			if (player.hasLongestRoadCard()) {
				longestRoadVictoryPointImage.setScaleX(1.5);
				longestRoadVictoryPointImage.setScaleY(1.5);

				boolean didPlayerHaveLongestRoadBefore = longestRoadVictoryPointImage.getParent() == longestRoadLabel;
				if (!didPlayerHaveLongestRoadBefore) {
					Animations.playFadeTransition(longestRoadVictoryPointImage);	
				}

				return longestRoadVictoryPointImage;
			} else {
				longestRoadVictoryPointImage.setScaleX(1);
				longestRoadVictoryPointImage.setScaleY(1);
				return longestRoadImage;
			}
		}, player.specialCardsProperty()));

		// If the player is the one associated with this application, show all victory points. Otherwise, show only the
		// visible victory points.
		this.victoryPointsLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			if (player == ApplicationInstance.getInstance().getUser().getPlayer()) {
				return player.victoryPointsProperty().asString().get();
			} else {
				return player.visibleVictoryPointsProperty().asString().get();
			}
		}, player.victoryPointsProperty(), player.visibleVictoryPointsProperty()));

		citiesLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			int nRemainingCities = Player.getMaxCity() - player.getCountCity();
			return Integer.toString(nRemainingCities);
		}, player.localitiesProperty()));

		settlementsLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			int nRemainingSettlements = Player.getMaxSettlement() - player.getCountSettlement();
			return Integer.toString(nRemainingSettlements);
		}, player.localitiesProperty()));

		roadsLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			int nRemainingRoads = Player.getMaxRoad() - player.getCountRoad();
			return Integer.toString(nRemainingRoads);
		}, player.roadsProperty()));

		activePlayerMarkerPane.visibleProperty().bind(player.stateProperty().isNotEqualTo(PlayerState.WAIT));
	}
	
	/**
	 * Binds the images of the {@code Dice} to the values of the dice.
	 * @see Dice 
	 */
	public void bindDice() {
		Dice dice = player.getLastRolledDice();
		diceImageOne.imageProperty().bind(Bindings.createObjectBinding(() -> {
			Animations.playFadeTransition(diceImageOne);
			Animations.playFadeTransition(diceImageTwo);
			int diceOneNumber = dice.dieOneNumberProperty().get();
			String imageLocation = "/game/dice/images/dice" + diceOneNumber + ".png";
			return new Image(getClass().getResourceAsStream(imageLocation));
		}, dice.dieOneNumberProperty()));

		diceImageTwo.imageProperty().bind(Bindings.createObjectBinding(() -> {
			Animations.playFadeTransition(diceImageOne);
			Animations.playFadeTransition(diceImageTwo);
			int diceTwoNumber = dice.dieTwoNumberProperty().get();
			String imageLocation = "/game/dice/images/dice" + diceTwoNumber + ".png";
			return new Image(getClass().getResourceAsStream(imageLocation));
		}, dice.dieTwoNumberProperty()));
	}

	@Override
	public void doInitializations(Player player, ClientConnection client) {
		this.player = player;
		this.client = client;
		fillPlayerInfo();
		if(!player.equals(client.getUser().getPlayer())) {
			loadNewAndLostResources();
		}
		bindDice();
	}

	/**
	 * Loads the displays of the new and lost resources of the other players.
	 */
	private void loadNewAndLostResources() {
		FXMLLoader loader = new FXMLLoader();
		try {
			Node view = loader.load(getClass().getResourceAsStream("/game/player/ReceivedAndLostResourceCardsView.fxml"));
			newAndLostResources.getChildren().add(view);
			((LastReceivedAndLostResourcesController)loader.getController()).doInitializations(player, client);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Sends a message to the {@code Server} containing a request to move the {@code Robber}.
	 * @see Robber
	 */
	@FXML
	private void stealResources() {
		if (clickablePart.getStyleClass().contains("clickable")) {
			JSONObject jsonObject = new JSONObject();
			JSONObject position = new JSONObject();
			position.put(Attributes.X.toString(), hex.getxPosAxialHex());
			position.put(Attributes.Y.toString(), hex.getyPosAxialHex());
			jsonObject.put(Attributes.POSITION.toString(), position);
			jsonObject.put(Attributes.TARGET.toString(), player.getId());
			if(client.getUser().getPlayer().currentySelectedDevelopmentCardProperty().get()!=null) {
				if(client.getUser().getPlayer().currentySelectedDevelopmentCardProperty().get().equals(PlayableDevelopmentCardType.KNIGHT)) {
					client.sendToServer(ServerTypes.PLAY_KNIGHT.toString(), jsonObject);
					client.getUser().getPlayer().currentySelectedDevelopmentCardProperty().setValue(null);
				}
			} else {
				client.sendToServer(ServerTypes.MOVE_ROBBER.toString(), jsonObject);
			}

			for (PlayerInfoController playerInfo : playerInfos) {
				playerInfo.makeClickable(false);
			}
		}
	}

	/**
	 * Sets the hex.
	 * @param hex the hex to set.
	 */
	public void setHex(Hex hex) {
		this.hex = hex;
	}

	/**
	 * Returns the {@code Player}.
	 * @return the {@link Player}.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Makes the player info clickable or non-clickable.
	 * @param clickable whether the player info should be clickable or not.
	 */
	public void makeClickable(boolean clickable) {
		if (clickable) {
			DropShadow highlightEffect = new DropShadow();
			highlightEffect.setColor(Color.BLUE);
			clickablePart.setEffect(highlightEffect);

			clickablePart.getStyleClass().add("clickable");
		} else {
			clickablePart.setEffect(null);
			clickablePart.getStyleClass().remove("clickable");
		}

	}

	/**
	 * Sets the playerInfos.
	 * @param playerInfos the playerInfos to set.
	 */
	public void setPlayerInfos(List<PlayerInfoController> playerInfos) {
		this.playerInfos = playerInfos;
	}

}
