package game.trade;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import game.Game;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import interfaces.Controller;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;
import network.server.Server;

/**
 * Represents the controller responsible for controlling the trade offer players receive
 * when the active player starts trading.
 * @author Svenja Schoett
 * @author Christoph Hermann
 */
public class OfferedTradeController implements Controller<Game> {

	/**
	 * The width of a card.
	 */
	private final static int CARD_WIDTH = 40;

	/**
	 * The distance between two resource cards of the same type that partly overlap.
	 */
	private final static int TRANSLATION_OVERLAPPING_CARDS = 10;

	/**
	 * The specific game this controller controls the trade for
	 */
	private Game game; 

	/**
	 * The client controller used to send messages to {@code Server}
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client;

	/**Label that contains the text that explains to the player what the incoming offer is*/
	@FXML private Label playerName; 

	/**
	 * The file path to the brick card image.
	 */
	private final static String BRICK_IMG_PATH = "game/resources/pictures/brickcard.png";

	/**
	 * The file path to the grain card image.
	 */
	private final static String GRAIN_IMG_PATH = "game/resources/pictures/graincard.png";

	/**
	 * The file path to the ore card image.
	 */
	private final static String ORE_IMG_PATH = "game/resources/pictures/orecard.png";

	/**
	 * The file path to the lumber card image.
	 */
	private final static String LUMBER_IMG_PATH = "game/resources/pictures/lumbercard.png";

	/**
	 * The file path to the wool card image.
	 */
	private final static String WOOL_IMG_PATH = "game/resources/pictures/woolcard.png";

	/**
	 * The anchor pane that contains the images of the offered cards
	 */
	@FXML
	private AnchorPane offeredCards;

	/**
	 * The label which indicates to the {@code Player} which {@code Resources} are offered.
	 * @see Player
	 * @see Resources
	 */
	@FXML
	private Label offerLabel;

	/**
	 * The label which indicates to the {@code Player} which {@code Resources} are requested.
	 * @see Player
	 * @see Resources
	 */
	@FXML
	private Label requestLabel;

	/**
	 * The anchor pane that contains the images of the requested cards
	 */
	@FXML
	private AnchorPane requestedCards;

	/**
	 * The button used to accept the {@code Trade}.
	 * @see Trade
	 */
	@FXML
	private Button acceptButton;

	/**
	 * The button used to decline the {@code Trade}.
	 * @see Trade
	 */
	@FXML
	private Button declineButton;

	/**
	 * The button used to take back the acceptance of the trade {@code Trade}.
	 * @see Trade
	 */
	@FXML
	private Button cancelButton;

	/**
	 * The map specifying which {@code ResourceType} uses witch image.
	 * @see ResourceType
	 */
	private Map<ResourceType, String> imagePaths = getImagePathsMap();
	
	/**
	 * Binds the text on the buttons to the language selected in the settings.
	 */
	@FXML
	private void initialize() {
		acceptButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ACCEPT"));
		declineButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("DECLINE"));
		cancelButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("REVERT_ACCEPT"));
		offerLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("OFFER"));
		requestLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("REQUEST"));
	}

	/**
	 * Returns a map with the different {@code ResourceTypes} and their images.
	 * @return the map specifying which {@link ResourceType} uses witch image.
	 * @see ResourceType
	 */
	private Map<ResourceType, String> getImagePathsMap() {
		Map<ResourceType, String> imagePaths = new HashMap<>();

		imagePaths.put(ResourceType.BRICK, BRICK_IMG_PATH);
		imagePaths.put(ResourceType.GRAIN, GRAIN_IMG_PATH);
		imagePaths.put(ResourceType.LUMBER, LUMBER_IMG_PATH);
		imagePaths.put(ResourceType.ORE, ORE_IMG_PATH);
		imagePaths.put(ResourceType.WOOL, WOOL_IMG_PATH);

		return imagePaths;
	}

	/**
	 * Method that fills the explanatoryText Label with the name of the player who offered the trade 
	 * and additional information on what the content of the window means. 
	 */
	public void addBindings() {
		// Bind the name of the player who offered the trade (= the active player).
		playerName.textProperty().bind(Bindings.createStringBinding(() -> {
			return game.getActivePlayer().getName();
		}, game.activePlayerProperty()));

		// Bind the image of the player who offered the trade (= the active player).
		playerName.graphicProperty().bind(Bindings.createObjectBinding(() -> {
			ImageView playerPicture = new ImageView(new Image(game.getActivePlayer().getImageLocation()));
			playerPicture.setPreserveRatio(true);
			playerPicture.setFitWidth(30);

			return playerPicture;
		}, game.activePlayerProperty()));
	}

	/**
	 * Updates the displayed resource cards of the player by binding them to the resources of the player.
	 */
	private void fillOfferAndRequest() {
		game.tradeProperty().addListener(observable -> {
			if (game.getTrade() != null) {
				// Update offered resources pane.
				offeredCards.getChildren().clear();
				Resources offer = game.getTrade().getTradeOffer();
				fillPane(offeredCards, offer);

				// Update requested resources pane.
				requestedCards.getChildren().clear();
				Resources request = game.getTrade().getTradeRequest();
				fillPane(requestedCards, request);

				// Only enable the cancel button, if the player has accepted the trade.
				cancelButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
					Player playerOfThisGameInstance = client.getUser().getPlayer();
					return !game.getTrade().getAcceptedList().contains(playerOfThisGameInstance);
				}, game.getTrade().getAcceptedList()));

				// Only enable the accept button, if the player has enough resources and hasn't already accepted.
				acceptButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
					Player playerOfThisGameInstance = client.getUser().getPlayer();
					boolean playerHasAccepted = game.getTrade().getAcceptedList().contains(playerOfThisGameInstance);
					boolean playerHasEnoughResources = playerOfThisGameInstance.getResources().
							isGreaterThanOrEqualTo(game.getTrade().getTradeRequest());
					return !playerHasEnoughResources || playerHasAccepted;
				}, game.getTrade().getAcceptedList()));
				
				// Only enable the decline button if the cancel button is disabled and therefore the trade hasn't been
				// accepted.
				declineButton.disableProperty().bind(cancelButton.disableProperty().not());
			}
		});
	}

	/**
	 * Displays the specified {@code Resources} in the {@code Pane}. 
	 * @param pane the {@link Pane} displaying the {@link Resources}.
	 * @param resources the Resources to display.
	 */
	public void fillPane(AnchorPane pane, Resources resources) {
		SimpleMapProperty<ResourceType, Integer> map = resources.getResources();

		for (ResourceType resourceType : ResourceType.values()) {
			String imagePath = imagePaths.get(resourceType);

			for (int i=0; i<map.get(resourceType); i++) {
				addCard(imagePath, pane.getChildren());
			}
		}
	}

	/**
	 * Adds an new card to the display of resource cards.
	 * @param imagePath the path to the image of the card.
	 * @param list the list the image is added to.
	 * @see Resources
	 */
	private void addCard(String imagePath, ObservableList<Node> list) {
		// Create image view.
		Image image = new Image(imagePath);
		ImageView cardView = new ImageView(image);

		// Configure image view.
		cardView.setPreserveRatio(true);
		cardView.setFitWidth(CARD_WIDTH);
		AnchorPane.setBottomAnchor(cardView, 0.0);
		AnchorPane.setRightAnchor(cardView, (double) (list.size()*(TRANSLATION_OVERLAPPING_CARDS)));

		// Add image view to parent.
		list.add(cardView);
	}

	/**
	 * Method that handles accepting the trade.
	 */
	@FXML
	public void acceptTrade() {
		JSONObject message = new JSONObject();
		message.put(Attributes.TRADING_ID.toString(), game.getTrade().getTradeID());
		message.put(Attributes.ACCEPT.toString(), true);
		client.sendToServer(ServerTypes.ACCEPT_TRADE.toString(), message);
	}

	/**
	 * Tells the {@code Server} that the {@code Player} wants to decline the {@code Trade}.
	 * @see Server
	 * @see Player
	 * @see Trade
	 */
	@FXML
	public void declineTrade() {
		JSONObject message = new JSONObject();
		message.put(Attributes.TRADING_ID.toString(), game.getTrade().getTradeID());
		message.put(Attributes.ACCEPT.toString(), false);
		client.sendToServer(ServerTypes.ACCEPT_TRADE.toString(), message);
	}

	/**
	 * Tells the {@code Server} that the {@code Player} wants to revert the acceptance of the {@code Trade}.
	 * @see Server
	 * @see Player
	 * @see Trade
	 */
	@FXML
	public void cancelTrade() {
		JSONObject message = new JSONObject();
		message.put(Attributes.TRADING_ID.toString(), game.getTrade().getTradeID());
		client.sendToServer(ServerTypes.CANCEL_TRADE.toString(), message);
	}

	@Override
	public void doInitializations(Game game, ClientConnection client) {
		this.game=game;
		this.client=client;

		fillOfferAndRequest();
		addBindings();
	}

}
