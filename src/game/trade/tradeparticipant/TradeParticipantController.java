package game.trade.tradeparticipant;

import org.json.JSONObject;

import game.player.Player;
import game.trade.Trade;
import game.trade.TradeController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;
import network.server.Server;

/**
 * Represents the controller responsible for controlling a {@code Trade} participant.
 * @see Trade
 * @author Christoph Hermann
 * @author Svenja Schoett
 */
public class TradeParticipantController {

	/**
	 * The {@code Player} associated with this controller.
	 * @see Player
	 */
	private Player player;

	/**
	 * The {@code Trade} controlled by this controller.
	 * @see Trade
	 */
	private ObjectProperty<Trade> trade = new SimpleObjectProperty<>();

	/**
	 * The client controller used to send messages to {@code Server}
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client;

	/**
	 * The picture of the {@code Player} associated with this controller.
	 * @see Player
	 */
	@FXML
	private ImageView playerImageView;

	/**
	 * The name of the {@code Player} associated with this controller.
	 * @see Player
	 */
	@FXML
	private Label nameLabel;

	/**
	 * The label showing that the {@code Player} associated with this controller has accepted the {@code Trade}.
	 * @see Player
	 * @see Trade
	 */
	@FXML
	private Label acceptLabel;

	/**
	 * The label showing that the {@code Player} associated with this controller has declined the {@code Trade}.
	 * @see Player
	 * @see Trade
	 */
	@FXML
	private Label declineLabel;

	/**
	 * The label showing that the {@code Player} associated with this controller hasn't yet accepted or declined  the
	 * {@code Trade}.
	 * @see Player
	 * @see Trade
	 */
	@FXML
	private Label noResponseLabel;

	/**
	 * The button used to finish the {@code Trade} with the {@code Player} associated with this controller.
	 * @see Trade
	 * @see Player
	 */
	@FXML
	private Button finishButton;

	/**
	 * The trade controller used to control this trade
	 * @see TradeController
	 */
	private TradeController tradeController;

	/**Binds the text on the buttons to the language selected in the settings*/
	@FXML
	private void initialize() {
		finishButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("FINISH_TRADE"));
		acceptLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ACCEPTED_TRADE"));
		declineLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("REJECTED_TRADE"));
		noResponseLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("NO_RESPONSE_TRADE"));
	}

	/**
	 * Fills the participantBox controlled by this Controller.
	 * @param player the {@link Player} whose name and picture is being displayed.
	 * @param trade the {@link Trade} controlled by this controller.
	 * @param client the {@link ClientConnection} used by the creator of the trade to communicate with the
	 * {@link Server}.
	 * @param tradeController trade controller used to control this trade.
	 */
	public void fillParticipantBox(Player player, ObjectProperty<Trade> trade, ClientConnection client,
			TradeController tradeController) {
		this.player = player;
		this.trade = trade;
		this.client = client;
		this.tradeController= tradeController;

		playerImageView.setImage(new Image(player.getImageLocation()));
		nameLabel.setText(player.getName());

		bindVisibilityOfNodes();
	}

	/**
	 * Binds the visibility of the labels and the button to whether the {@code Player} has accepted the {@code Trade}
	 * or not.
	 * @see Player
	 * @see Trade
	 */
	private void bindVisibilityOfNodes() {
		trade.addListener(observable -> {
			if (trade.get() == null) {
				// We need to remove the bindings to avoid a NullPointerException.
				finishButton.visibleProperty().unbind();
				acceptLabel.visibleProperty().unbind();
				declineLabel.visibleProperty().unbind();
				noResponseLabel.visibleProperty().unbind();
			} else {
				// Only show finish button if the player accepted the trade.
				finishButton.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
					return trade.get().getAcceptedList().contains(player);
				}, trade.get().getAcceptedList()));

				// Show the accept label if the player has accepted the trade.
				acceptLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
					return trade.get().getAcceptedList().contains(player);
				}, trade.get().getAcceptedList()));

				// Show the decline label if the player has declined the trade.
				declineLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
					return trade.get().getDeclinedList().contains(player);
				}, trade.get().getDeclinedList()));

				// Show the no response label if the player hasn't accepted or declined.
				noResponseLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
					boolean playerHasAccepted = trade.get().getAcceptedList().contains(player);
					boolean playerHasDeclined = trade.get().getDeclinedList().contains(player);
					return !playerHasAccepted && !playerHasDeclined;
				}, trade.get().getAcceptedList(), trade.get().getDeclinedList()));
			}
		});
	}

	/**
	 * Tells the {@code Server} that the {@code Player} who started the {@code Trade} wants to trade with the selected
	 * player.
	 * @see Server
	 * @see Player
	 * @see Trade
	 */
	@FXML
	private void finishTrade() {
		// Create message.
		JSONObject message = new JSONObject();
		message.put(Attributes.TRADING_ID.toString(), trade.get().getTradeID());
		message.put(Attributes.FELLOW_PLAYER.toString(), player.getId());

		// Send message to server.
		client.sendToServer(ServerTypes.FINISH_TRADE.toString(), message);

		//reset the trade offers in the trade window
		tradeController.resetTradeWindow();
	}

}
