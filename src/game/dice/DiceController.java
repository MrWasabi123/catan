package game.dice;

import org.json.JSONObject;

import game.player.PlayerState;
import interfaces.Controller;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.ServerTypes;
import network.server.Server;
import sounds.AudioPlayer;

/**
 * Represents the controller responsible for controlling dice.
 * @author Christoph Hermann
 */
public class DiceController implements Controller<Dice> {

	/**
	 * The {@code Dice} model used by this controller.
	 * @see Dice
	 */
	private Dice dice;

	/**
	 * The client used by this controller.
	 * @see ClientConnection
	 */
	private ClientConnection client;

	/**
	 * The ImageView responsible for drawing the interface which can be used to roll the dice.
	 */
	@FXML
	private Button rollButton;

	/**
	 * The tooltip for the button that rolls the dice.
	 */
	@FXML
	private Tooltip rollTTip;

	/**
	 * The ImageView responsible for drawing the first die.
	 */
	@FXML
	private ImageView imageViewOne;

	/**
	 * The ImageView responsible for drawing the first die.
	 */
	@FXML
	private ImageView imageViewTwo;

	/**
	 * The audio player.
	 */
	private AudioPlayer audioPlayer = new AudioPlayer();

	/**
	 * Binds the tooltip of the roll dice button to the selected locale.
	 */
	@FXML
	private void initialize() {
		rollTTip.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ROLL_DICE"));
	}

	/**
	 * Requests the {@code Server} to roll the {@code Dice}.
	 * @see Server
	 * @see Dice
	 */
	@FXML
	private void roll() {
		dice.roll();
		client.sendToServer(ServerTypes.ROLL_DICE.toString(), new JSONObject());
		audioPlayer.playDiceSound();
	}

	/**
	 * Connects the {@code Dice} model to this controller and its view.
	 * @see Dice
	 */
	public void addBindings() {
		imageViewOne.imageProperty().bind(Bindings.createObjectBinding(() -> {
			int diceOneNumber = dice.dieOneNumberProperty().get();
			String imageLocation = "/game/dice/images/dice" + diceOneNumber + ".png";
			return new Image(getClass().getResourceAsStream(imageLocation));
		}, dice.dieOneNumberProperty()));

		imageViewTwo.imageProperty().bind(Bindings.createObjectBinding(() -> {
			int diceTwoNumber = dice.dieTwoNumberProperty().get();
			String imageLocation = "/game/dice/images/dice" + diceTwoNumber + ".png";
			return new Image(getClass().getResourceAsStream(imageLocation));
		}, dice.dieTwoNumberProperty()));

		rollButton.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			return client.getUser().getPlayer().getState() == PlayerState.ROLL_DICE;
		}, client.getUser().getPlayer().stateProperty()));

		imageViewOne.visibleProperty().bind(rollButton.visibleProperty().not());
		imageViewTwo.visibleProperty().bind(rollButton.visibleProperty().not());
	}

	@Override
	public void doInitializations(Dice dice, ClientConnection client) {
		this.dice = dice;
		this.client = client;
		addBindings();
	}

	/**
	 * Getter for the Button to roll the dice.
	 * @return the roll dice button
	 */
	public Button getRollButton() {
		return rollButton;
	}

}
