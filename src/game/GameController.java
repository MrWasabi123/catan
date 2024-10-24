package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import game.board.BoardController;
import game.board.construction.Construction;
import game.board.construction.localities.City;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.cards.DevelopmentCard;
import game.dice.DiceController;
import game.player.Player;
import game.player.PlayerInfoController;
import game.player.PlayerState;
import game.resources.ResourceCardsController;
import game.resources.Resources;
import game.trade.TradeController;
import interfaces.Controller;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lobby.chat.ChatController;
import main.ApplicationController;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.ServerTypes;
import network.server.Server;
import sounds.AudioPlayer;

/**
 * Creates the game view
 * @author Paula Wikidal
 * @author Svenja Schoett
 * @author Christoph Hermann
 */
public class GameController implements Controller<Game> {

	/**The ApplicationController*/
	private ApplicationController applicationController;

	/**
	 * Connection to the Client
	 */
	private ClientConnection client; 

	/**The Game*/
	private Game game;

	/**The audio player that plays the sounds */
	private AudioPlayer audioPlayer = new AudioPlayer();

	/**The root element of the board view*/
	@FXML private Pane rootPane;

	/**The Pane containing the panes of the hexes, corners and edges*/
	@FXML private Pane board;

	/**The BoardController*/
	@FXML private BoardController boardController;

	/** The pane containing the chat*/
	@FXML private Pane chatPane;

	/**The Controller of the dice*/
	@FXML private DiceController diceController;

	/**The HBox containing the playerInfos*/
	@FXML private VBox playersPane;

	/**The HBox containing the main player Info*/
	@FXML private Pane mainPlayerInfoPane;

	/**The HBox containing the resource cards*/
	@FXML private Pane resourceCardsPane;

	/**The HBox containing the development cards*/
	@FXML private Pane developmentCardsPane;
	
	/**The Label that that displays the word 'Action'.*/
	@FXML private Label actionLabel;

	/**The Label that shows which action the active player has to perform.*/
	@FXML private Label actionDisplay;

	/**The Label that shows what round of the game is played*/
	@FXML private Label roundDisplay; 

	/**The Pane that contains the optional trade window if it's opened*/
	@FXML private Pane tradePane;

	/**The Pane that contains the window containing the trade offer*/
	@FXML private Pane offeredTradePane;

	/**The Pane showing the construction costs*/
	@FXML private Pane constructionCostsPane;

	/**The Pane showing the developmentCard costs*/
	@FXML private Pane developmentCardCostsPane;

	/**The ImageView to click on to buy a development card*/
	@FXML private Label buyDevelopmentCard;

	/**The menu that appears when the player has to discard of some of some resource cards*/
	@FXML private Pane discardResources;

	/**The controller class to the discard resources menu.*/
	@FXML private DiscardResourcesController discardResourcesController;

	/**The Label that reads who is the active player now*/
	@FXML private Label notificationLabel;

	/**Button that shows the prices of development cards and constructions*/
	@FXML private Button infoButton;
	
	/**The tooltip of the build button */
	@FXML private Tooltip infoTTip;

	/**Button that needs to be clicked to buy a development card*/
	@FXML private Button developmentButton;
	
	/**The tooltip of the development cards button */
	@FXML private Tooltip developmentTTip;

	/**The Button that needs to be clicked to trade*/
	@FXML private Button tradeButton;
	
	/**The tooltip of the trade button */
	@FXML private Tooltip tradeTTip;

	/**The Button that needs to be clicked to end the turn*/
	@FXML private Button endTurnButton;
	
	/**The tooltip of the end turn button */
	@FXML private Tooltip endTurnTTip;
	
	/**The menu containing the settings*/
	@FXML private Pane settingsMenu;
	
	/**The tooltip of the settings button */
	@FXML private Tooltip settingsTTip;
	
	/**The menu containing the exit menu*/
	@FXML private Pane exitMenu;
	
	/**The Label containing the request to confirm the exit*/
	@FXML private Label exitConfirmationLabel;
	
	/**The Button that closes the exit game menu again*/
	@FXML private Button cancelExitButton;
	
	/**The Button that confirms the request to return to the main menu*/
	@FXML private Button confirmExitButton;
	
	/**The tooltip of the exit button */
	@FXML private Tooltip exitTTip;
	
	/**The header of the construction cost info */
	@FXML private Label lBuildCost;
	
	/**The display of the building costs of a city */
	@FXML private Pane costCityPane;
	
	/**The display of the building costs of a settlement */
	@FXML private Pane costSettlementPane;
	
	/**The display of the building costs of a road */
	@FXML private Pane costRoadPane;
	
	/**The display of the building costs of a development card */
	@FXML private Pane costDevelopmentPane;
	
	
	/**
	 * The {@code PlayerInfoControllers} which need to be passed to various controllers.
	 * @see PlayerInfoController
	 */
	private List<PlayerInfoController> playerInfos = new ArrayList<>();
	
	/**
	 * Binds the texts on the labels to the language selected in the settings
	 */
	private void bindLanguage() {
		actionLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ACTION"));
		
		settingsTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("SETTINGS"));
		
		infoTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("INFO"));
		developmentTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("BUY_DEVELOPMENT"));
		tradeTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("OPEN_TRADE"));
		endTurnTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("END_TURN"));
		lBuildCost.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("CONSTRUCTION_COST"));
		
		cancelExitButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("CANCEL"));
		confirmExitButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("OK"));
		exitTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("EXIT"));
		exitConfirmationLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("EXIT_CONFIRMATION"));
	}

	/**
	 * Loads all view elements of the game.
	 */
	private void loadViews() {		
		
		boardController.doInitializations(game.getBoard(), client);
		discardResourcesController.doInitializations(client.getUser().getPlayer(), client);
		diceController.doInitializations(game.getDice(), client);

		loadChat("/lobby/chat/ChatView.fxml", chatPane);

		loadConstructionCosts();

		for (Player player : game.getPlayers()) {
			if (player!=client.getUser().getPlayer()) {
				load("/game/player/PlayerInfoView.fxml", playersPane, player);
			}
		}

		// Pass the playerInfos to the boardController and each playerInfoController.
		boardController.setPlayerInfos(playerInfos);
		for (PlayerInfoController playerInfo : playerInfos) {
			playerInfo.setPlayerInfos(playerInfos);
		}

		load("/game/player/PlayerInfoView.fxml", mainPlayerInfoPane, client.getUser().getPlayer());
		loadResourceCards();
		load("/game/cards/DevelopmentCardsView.fxml", developmentCardsPane, client.getUser().getPlayer().developmentCardsProperty());
		load("/game/trade/OfferedTradeView.fxml", offeredTradePane, game);

		tradeStart();
	}

	/**
	 * Loads the view that displays the resource cards.
	 */
	private void loadResourceCards() {
		ResourceCardsController controller = new ResourceCardsController();
		FXMLLoader loader = new FXMLLoader();
		loader.setController(controller);
		try {
			Node view = loader.load(getClass().getResourceAsStream("/game/resources/ResourceCardsView.fxml"));
			controller.doInitializations(client.getUser().getPlayer().getResources(), client);
			resourceCardsPane.getChildren().add(view);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
	}
	
	/**
	 * Loads the construction costs.
	 */
	private void loadConstructionCosts() {
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		try {
			Node view = loader.load(getClass().getResourceAsStream("/game/ConstructionCosts.fxml"));
	
			constructionCostsPane.getChildren().add(view);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
	}

	/**
	 * Sends a request to buy a {@code DevelopmentCard} to the {@code Server}.
	 * @see DevelopmentCard
	 * @see Server
	 */
	@FXML
	private void buyDevelopmentCard() {
		client.sendToServer(ServerTypes.BUY_DEVLOPMENTCARD.toString(), new JSONObject());
	}

	/**
	 * Sends a message to the server when the endTurn button is clicked.
	 */
	@FXML
	private void endTurn() {
		audioPlayer.playButtonSound();
		client.sendToServer(ServerTypes.END_TURN.toString(), new JSONObject());

		tradePane.setVisible(false);
	}

	/**
	 * Loads a FXML file, adds it to the root Pane and calls the doInitializations method,
	 * that passes an object to the controller of the view to do the initializations.
	 * @param filePath The path to the FXML file
	 * @param parent The parent element the loaded file is added to
	 * @param modelObject The object, that is passed to the controller
	 */
	private void load(String filePath, Parent parent, Object modelObject) {

		Controller<Object> controller = null;

		FXMLLoader loader = new FXMLLoader();
		try {
			Node view = loader.load(getClass().getResourceAsStream(filePath));
			controller = loader.<Controller<Object>>getController();

			controller.doInitializations(modelObject, client);

			if(parent instanceof Group) {
				((Group)parent).getChildren().add(view);
			} else if (parent instanceof Pane) {
				((Pane)parent).getChildren().add(view);
			}

			if (loader.getController() instanceof PlayerInfoController) {
				playerInfos.add(loader.getController());
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Loads the chat from the same FXML file as in the lobby. Then the chatController
	 * of the client is set to the new controller to display server messages in the game chat.  
	 * @param filePath The path to the FXML file
	 * @param rootPane The parent element the loaded file is added to
	 */
	private void loadChat(String filePath, Pane rootPane) {
		FXMLLoader loader = new FXMLLoader();
		try {
			Node view = loader.load(getClass().getResourceAsStream(filePath));
			ChatController chatController = loader.getController();

			chatController.getRoot().getStyleClass().clear();
			chatController.setClientConnection(client);
			client.getClientController().setChatController(chatController);

			rootPane.getChildren().add(view);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Method that fills the Label roundDisplay with the Information on whose turn it
	 * is and which action they are allowed to take.
	 */
	public void fillRoundDisplay() {
		actionDisplay.textProperty().bind(Bindings.createStringBinding(() -> {
			return ApplicationInstance.getInstance().getBundleProperty().get().getString(
					client.getUser().getPlayer().getState().getDisplayText());
		}, client.getUser().getPlayer().stateProperty(), ApplicationInstance.getInstance().getBundleProperty()));
	}

	@Override
	public void doInitializations(Game game, ClientConnection client) {
		this.game = game;
		this.client = client;
		loadViews();
		fillRoundDisplay();
		bindDiscardResources();
		bindTradeOffer();
		bindEndScreen();
		amplifyClickableButtons();
		bindButtons();
		bindBoardScaling();
		bindLanguage();
		bindConstructionCosts();
	}
	
	/**
	 * Binds the opacity of the info on the costs of a construction or development card
	 * to the resources the player has got.
	 */
	private void bindConstructionCosts() {
		Player player = client.getUser().getPlayer();
		player.getResources().getResources().addListener((InvalidationListener) observable -> {
			setOpacityOfCost(costRoadPane, Road.getCost());
			setOpacityOfCost(costSettlementPane, Settlement.getCost());
			setOpacityOfCost(costCityPane, City.getCost());
			setOpacityOfCost(costDevelopmentPane, DevelopmentCard.getCost());
		});
	}
	
	/**
	 * Sets the opacity of a pane to 0.5 if the player can't afford to buy 
	 * something with the given cost.
	 * @param pane The pane to set the opacity of
	 * @param resources The cost of the construction or development card
	 */
	private void setOpacityOfCost(Pane pane, Resources resources) {
		Player player = client.getUser().getPlayer();
		if(player.getResources().isGreaterThanOrEqualTo(resources)) {
			pane.setOpacity(1);
		} else {
			pane.setOpacity(0.5);
		}
	}
	
	/**
	 * Opens and closes the settings menu. 
	 */
	@FXML private void openSettings() {
		settingsMenu.setVisible(!settingsMenu.isVisible());
	}

	/**
	 * Scales the board when the window changes its size.
	 */
	private void bindBoardScaling() {
		board.scaleXProperty().bind(Bindings.createDoubleBinding(() -> {
			double height = boardController.getBackgroundPane().getBoundsInLocal().getHeight();
			double factor = (applicationController.getGameView().getHeight()/height)*0.8;
			board.setScaleY(factor);
			return factor;
		}, applicationController.getGameView().widthProperty()));
	}

	/**
	 * Binds the state of some buttons to the game model.
	 */
	private void bindButtons() {
		// Enable the end turn button only if the player is in TRADE_OR_BUILD state and no trade is currently running.
		endTurnButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			Player thisPlayer = client.getUser().getPlayer();
			boolean playerIsInBuildingState = thisPlayer.getState() == PlayerState.TRADE_OR_BUILD;
			boolean tradeExists = game.getTrade() != null;

			return !playerIsInBuildingState || tradeExists;
		}, client.getUser().getPlayer().stateProperty(), game.tradeProperty()));

		// Enable the trade button only when the player is in TRADE_OR_BUILD state.
		tradeButton.disableProperty().bind(client.getUser().getPlayer()
				.stateProperty().isNotEqualTo(PlayerState.TRADE_OR_BUILD));

		// Enable the development button only when the player is in TRADE_OR_BUILD state and has enough resources to buy
		// a development card.
		developmentButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			Player player = client.getUser().getPlayer();
			boolean isPlayerInBuildingState = player.getState() == PlayerState.TRADE_OR_BUILD;
			boolean hasPlayerEnoughResources = player.getResources().isGreaterThanOrEqualTo(DevelopmentCard.getCost());
			return !isPlayerInBuildingState	|| !hasPlayerEnoughResources;
		}, client.getUser().getPlayer().stateProperty(), client.getUser().getPlayer().getResources().getResources()));
	}

	/**
	 * Binds the visibility of the end screen to the model.
	 */
	private void bindEndScreen() {
		game.winnerProperty().addListener((observable) -> {
			applicationController.loadEndScreenView(game.winnerProperty().get().getName() + " has won!!!");
		});

		for (Player player : game.getPlayers()) {
			player.stateProperty().addListener(observable -> {
				if (player.getState() == PlayerState.CONNECTION_LOST) {
					applicationController.loadEndScreenView(player.getName() + " has disconnected.");
				}
			});
		}
	}
	
	/**
	 * Opens and closes the exit menu.
	 */
	@FXML
	private void openExitGame() {
		exitMenu.setVisible(!exitMenu.isVisible());
	}
	
	/**
	 * Exits the game and returns to the main menu.
	 */
	@FXML
	private void exitGame() {
		applicationController.returnToMainMenu();
		client.stopRunning();
		client.getClientController().stopBackgroundMusic();
	}

	/**
	 * Method that loads the trade window 
	 * and enables the player to trade with other players and the bank
	 */
	private void tradeStart() {

		FXMLLoader loader = new FXMLLoader();
		try {
			Node view = loader.load(getClass().getResourceAsStream("/game/trade/TradeView.fxml"));
			tradePane.getChildren().add(view);
			TradeController controller = loader.getController();
			controller.doInitializations(game, client);
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Method that shows and hides the trade window when you click the button
	 */
	@FXML
	private void setTradeVisible() {
		tradePane.setVisible(!tradePane.isVisible());
		audioPlayer.playButtonSound();
	}

	/**
	 * Shows the discard resources menu when the player is in the {@code PlayerState}
	 * discard resources
	 */
	private void bindDiscardResources() {
		Player player = client.getUser().getPlayer();
		discardResources.visibleProperty().bind(player.stateProperty()
				.isEqualTo(PlayerState.DISCARD_RESOURCES));
	}

	/**
	 * Shows the offered trade menu when the player has received a TRADE_OFFER
	 * by binding the visibility to the clienttype that is being handled
	 */
	private void bindTradeOffer() {
		game.tradeProperty().addListener(object -> {
			if (game.getTrade() != null) {
				offeredTradePane.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
					boolean tradeExists = game.getTrade() != null;
					boolean playerDidStartTheTrade = client.getUser().getPlayer() == game.getTrade().getPlayer();
					boolean playerDidDeclineTheTrade = game.getTrade().getDeclinedList().contains(client.getUser().getPlayer());
					return tradeExists && !playerDidStartTheTrade && !playerDidDeclineTheTrade;
				}, game.getTrade().getDeclinedList()));
			} else {
				offeredTradePane.visibleProperty().unbind();
				offeredTradePane.setVisible(false);
			}
		});
	}

	/**
	 * Shows the prices of {@code Constructions} and {@code DevelopmentCards}.
	 * @see Construction
	 * @see DevelopmentCard
	 */
	@FXML 
	private void infoButtonClicked() {
		constructionCostsPane.setVisible(!constructionCostsPane.isVisible());
		audioPlayer.playButtonSound();
	}

	/**
	 * Shows the discard resources menu when the player is in the {@code PlayerState}
	 * discard resources
	 */
	@FXML 
	private void developmentCardsButtonClicked() {
		constructionCostsPane.setVisible(false);
		developmentCardCostsPane.setVisible(!developmentCardCostsPane.isVisible());
		audioPlayer.playButtonSound();
	}

	/**
	 * Method that puts dropshadow behind currently clickable buttons
	 * to raise intuitive use
	 */
	public void amplifyClickableButtons() {
		DropShadow amplifyShadow = new DropShadow();
		amplifyShadow.setColor(Color.BLUE);
		Player player = client.getUser().getPlayer();

		//when state roll dice
		diceController.getRollButton().effectProperty().bind(Bindings.createObjectBinding(() -> {
			if (player.getState().equals(PlayerState.ROLL_DICE)) {
				return amplifyShadow;
			} else {
				return null;
			}
		},player.stateProperty()));

		//when trade or build
		developmentButton.effectProperty().bind(Bindings.createObjectBinding(() -> {
			if (player.getState().equals(PlayerState.TRADE_OR_BUILD)) {
				return amplifyShadow;
			} else {
				return null;
			}
		},player.stateProperty()));
		tradeButton.effectProperty().bind(Bindings.createObjectBinding(() -> {
			if (!tradeButton.isDisable()) {
				return amplifyShadow;
			} else {
				return null;
			}
		},tradeButton.disableProperty()));
		endTurnButton.effectProperty().bind(Bindings.createObjectBinding(() -> {
			if (!endTurnButton.isDisable()) {
				return amplifyShadow;
			} else {
				return null;
			}
		},endTurnButton.disableProperty()));
	}

	/**
	 * Sets the applicationController that is used to switch between scenes
	 * @param applicationController the applicationController
	 */
	public void setApplicationController(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	/**
	 * Returns the root pane so that it can be scaled according to the window size
	 * in the applicationController
	 * @return the root pane
	 */
	public Pane getRootPane() {
		return board;
	}

}