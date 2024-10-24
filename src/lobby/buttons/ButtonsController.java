package lobby.buttons;

import org.json.JSONObject;

import game.player.PlayerState;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.ApplicationController;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.ServerTypes;
import network.server.Server;
import users.User;

/**
 * Represents the controller responsible for controlling the buttons area in the lobby.
 * @author Christoph Hermann
 */
public class ButtonsController {

	/**
	 * The {@code ApplicationController} which can be accessed by this controller.
	 * @see ApplicationController
	 */
	private ApplicationController applicationController;

	/**
	 * The client thread used by this controller to send messages to the {@code Server}.
	 * @see Server
	 */
	private ObjectProperty<ClientConnection> client;

	/**
	 * The button used to signal that this {@code User} is ready.
	 * @see User
	 */
	@FXML
	private Button readyButton;

	/**
	 * The button used to return back to the main menu.
	 */
	@FXML
	private Button returnToMainMenuButton;
	
	/**
	 * Binds the texts on the labels to the language selected in the settings
	 */
	@FXML 
	private void initialize() {
		readyButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("READY"));
		returnToMainMenuButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("RETURN_TO_MAIN_MENU"));
	}

	/**
	 * Signal the {@code Server} that this {@code User} is ready.
	 * @see Server
	 * @see User
	 */
	@FXML
	private void ready() {
		JSONObject message = new JSONObject();
		// send an empty JSONObject, so the server knows the player is ready
		client.get().sendToServer(ServerTypes.GAME_START.toString(), message);
	}

	/**
	 * Closes all open connections and returns back to the main menu.
	 */
	@FXML
	private void returnToMainMenu() {
		applicationController.loadMainMenuView();
	}

	/**
	 * Sets the client thread used by this controller to send messages to the {@code Server}.
	 * @param client the {@link ClientConnection}.
	 * @see Server
	 */
	public void setClientConnection(ObjectProperty<ClientConnection> client) {
		this.client = client;
		
		// Disable the ready button if the client is not connected to a server or if the user is already ready.
		this.client.addListener((observable, oldValue, newValue) -> {
			if (client.get() == null) {
				readyButton.disableProperty().unbind();
				readyButton.setDisable(true);
			} else {
				readyButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
					PlayerState state = client.get().getUser().getState();
					return state != PlayerState.START_GAME;
				}, client.get().getUser().stateProperty()));
			}
		});
	}

	/**
	 * Sets the {@code ApplicationController} which can be accessed by this controller.
	 * @param applicationController the {@link ApplicationController}.
	 */
	public void setApplicationController(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

}
