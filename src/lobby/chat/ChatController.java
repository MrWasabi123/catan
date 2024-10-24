package lobby.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;
import network.server.Server;

/**
 * Represents a controller responsible for managing the chat area in the lobby. 
 * @author Christoph Hermann
 */
public class ChatController {

	/**
	 * The client connection property used by this controller to send messages to the {@code Server}.
	 * @see Server
	 */
	private ObjectProperty<ClientConnection> clientConnection = new SimpleObjectProperty<>();

	/**
	 * The BorderPane that is the root element of the chat.
	 */
	@FXML
	private Pane root;

	/**
	 * The TextArea responsible for displaying messages.
	 */
	@FXML
	private TextArea messageArea;

	/**
	 * The TextField used for writing chat messages.
	 */
	@FXML
	private TextField writingField;

	/**
	 * Binds the prompt text.
	 */
	@FXML
	private void initialize() {
		writingField.promptTextProperty().bind(ApplicationInstance.getInstance().createStringBinding("CHAT_PROMPT"));
	}

	/**
	 * Sends a chat message to the {@code Server}.
	 * @see Server
	 */
	@FXML
	private void send() {
		String message = writingField.getText();

		JSONObject object = new JSONObject();
		object.put(Attributes.MESSAGE.toString(), message);

		clientConnection.get().sendToServer(ServerTypes.SEND_CHATMESSAGE.toString(), object);

		writingField.clear();
	}

	/**
	 * Displays a message in the chat area.
	 * @param playerName The name of the player who sent the message.
	 * @param message The message.
	 */
	public void displayChatMessage(String playerName, String message) {
		String time = determineTime(); 
		Platform.runLater(() -> messageArea.appendText("[" + time + "] " + playerName + ": " + message + "\n"));
	}

	/**
	 * Displays a error message in the chat area.
	 * @param message The message.
	 */
	public void displayErrorMessage(String message) {
		Platform.runLater(() -> messageArea.appendText("ERROR: " + message + "\n"));
	}

	/**
	 * Displays a message in the chat area.
	 * @param message The message.
	 */
	public void displayMessage(String message) {
		Platform.runLater(() -> messageArea.appendText(message + "\n"));
	}

	/**
	 * Returns the current time.
	 * @return the current time as a String.
	 */
	private String determineTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String time = formatter.format(LocalDateTime.now());

		return time;
	}

	/**
	 * Returns the TextField used for writing chat messages..
	 * @return the writingField.
	 */
	public TextField getWritingField() {
		return writingField;
	}

	/**
	 * Returns the area responsible for displaying messages.
	 * @return the messageArea.
	 */
	public TextArea getMessageArea() {
		return messageArea;
	}

	/**
	 * Returns the root element of the chat view.
	 * @return the Pane which is the root element.
	 */
	public Pane getRoot() {
		return root;
	}

	/**
	 * Sets the value of the {@code ClientConnection} property. 
	 * @param client the {@link ClientConnection}.
	 */
	public void setClientConnection(ClientConnection client) {
		clientConnection.set(client);
	}

	/**
	 * Sets the client connection property and binds GUI-elements to it.
	 * @param clientProperty the {@link ClientConnection} property to set.
	 */
	public void setClientConnection(ObjectProperty<ClientConnection> clientProperty) {
		this.clientConnection = clientProperty;
	}

}
