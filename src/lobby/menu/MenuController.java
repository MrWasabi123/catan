package lobby.menu;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import lobby.Lobby;
import lobby.chat.ChatController;
import main.ApplicationController;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.client.ClientController;
import network.server.Server;
import network.server.ServerController;
import sounds.AudioPlayer;
import users.AIUser;
import users.User;

/**
 * Represents a controller responsible for managing all menu interactions in the lobby.
 * @author Christoph Hermann
 */
public class MenuController {

	/**
	 * The audio player.
	 */
	private AudioPlayer audioPlayer = new AudioPlayer();

	/**
	 * The hostname of a locally started server. 
	 */
	private static final String LOCAL_SERVER_HOSTNAME = "localhost";

	/**
	 * The maximum length of a username.
	 */
	private static final int MAX_NAME_LENGTH = 20;

	/**
	 * The controller responsible of managing the different scenes of this application.
	 */
	private ApplicationController applicationController;

	/**
	 * The {@code Lobby} being controlled by this controller.
	 * @see Lobby
	 */
	private Lobby lobby;

	/**
	 * The {@code Server} being controlled by this controller.
	 * @see Server
	 */
	private ObjectProperty<Server> server = new SimpleObjectProperty<>();

	/**
	 * The client being controlled by this controller.
	 * @see ClientConnection
	 */
	private ObjectProperty<ClientConnection> client = new SimpleObjectProperty<>();

	/**
	 * The list of all AI clients started by this controller.
	 * @see ClientConnection
	 * @see AIUser
	 */
	private List<ClientConnection> aiClients = new ArrayList<>();

	/**
	 * The {@code ChatController} that is used to display messages.
	 */
	private ChatController chatController;

	/**
	 * The element in the user interface containing all information concerning the server.
	 */
	@FXML
	private StackPane serverPane;

	/**
	 * The {@code VBox} containing all elements to be displayed, when no {@code Server} is active.
	 * @see VBox
	 * @see Server
	 */
	@FXML
	private VBox serverNotActiveBox;

	/**
	 * The {@code TextField} used to enter the port for the {@code Server} to be created.
	 * @see TextField
	 * @see Server
	 */
	@FXML
	private TextField serverPortField;

	/**
	 * The button used to start a {@code Server}.
	 * @see Server
	 */
	@FXML
	private Button launchButton;

	/**
	 * The {@code VBox} containing all elements to be displayed, when a {@code Server} is active.
	 * @see VBox
	 * @see Server
	 */
	@FXML
	private VBox serverActiveBox;

	/**
	 * The element displaying the ip and port of the currently active {@code Server}.
	 * @see Server
	 */
	@FXML
	private TextField activeServerAddressLabel;

	/**
	 * The button used to terminate the {@code Server}.
	 * @see Server
	 */
	@FXML
	private Button terminateServerButton;

	/**
	 * The element in the user interface containing all information concerning the client.
	 */
	@FXML
	private StackPane clientPane;

	/**
	 * The {@code VBox} containing all elements to be displayed, when not connected to a {@code Server}.
	 * @see VBox
	 * @see Server
	 */
	@FXML
	private VBox clientNotConnectedBox;

	/**
	 * The element responsible for displaying the IP of the {@code Server} that the client wants to connected to.
	 * @see Server
	 * @see ClientConnection
	 */
	@FXML
	private TextField clientHostnameField;

	/**
	 * The element responsible for displaying the port of the {@code Server} that the client wants to connected to.
	 * @see Server
	 * @see ClientConnection
	 */
	@FXML
	private TextField clientPortField;

	/**
	 * The element responsible for displaying the name of the {@code User}.
	 * @see User
	 */
	@FXML
	private TextField usernameField;

	/**
	 * The button used for connecting to a {@code Server}.
	 * @see Server
	 */
	@FXML
	private Button connectButton;

	/**
	 * The {@code VBox} containing all elements to be displayed, when connected to a {@code Server}.
	 * @see VBox
	 * @see Server
	 */
	@FXML
	private VBox clientConnectedBox;

	/**
	 * The element displaying the ip and port of the {@code Server} currently connected to.
	 * @see Server
	 */
	@FXML
	private TextField clientConnectedAddressLabel;

	/**
	 * The button used for disconnecting from a {@code Server}.
	 * @see Server
	 */
	@FXML
	private Button disconnectButton;

	/**
	 * The element in the user interface containing all information concerning ai players.
	 */
	@FXML
	private VBox aiBox;

	/**
	 * The element responsible for displaying the IP of the {@code Server} that the AI wants to connected to.
	 * @see Server
	 * @see AIUser
	 */
	@FXML
	private TextField aiHostNameField;

	/**
	 * The element responsible for displaying the port of the {@code Server} that the AI wants to connected to.
	 * @see Server
	 * @see AIUser
	 */
	@FXML
	private TextField aiPortField;

	/**
	 * The button used to add an {@code AIUser} to the selected {@code Server}.
	 * AIUser
	 * Server
	 */
	@FXML
	private Button addAIButton;

	/** The header of the menu section to start a server.*/
	@FXML private Label startServerLabel;

	/** The header of the menu section to connect to a server.*/
	@FXML private Label connectToServerLabel;

	/** The header of the menu section to add an AI player.*/
	@FXML private Label addComputerPlayerLabel;

	/** The header of the menu section that is visible when the server is active.*/
	@FXML private Label activeServerLabel;

	/** The header of the menu section that is visible when the client is connected.*/
	@FXML private Label connectedToServerLabel;

	/**
	 * Initializes this controller.
	 */
	@FXML
	private void initialize() {
		configureMenuElements();
		addListeners();
		bindLanguage();
		bindGUI();
	}

	/**
	 * Binds the text on the labels to the language selected in the settings.
	 */
	private void bindLanguage() {
		// Buttons
		launchButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("START"));
		terminateServerButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("TERMINATE"));
		connectButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("CONNECT"));
		disconnectButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("DISCONNECT"));
		addAIButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ADD"));

		// Labels
		connectToServerLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("CONNECT_TO_SERVER"));
		activeServerLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ACTIVE_SERVER"));
		startServerLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("START_SERVER"));
		connectedToServerLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("CONNECTED_TO_SERVER"));
		addComputerPlayerLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ADD_AI"));

		// Prompt texts.
		serverPortField.promptTextProperty().bind(ApplicationInstance.getInstance().createStringBinding("SERVER_PORT_PROMPT"));
		clientPortField.promptTextProperty().bind(ApplicationInstance.getInstance().createStringBinding("CLIENT_PORT_PROMPT"));
		clientHostnameField.promptTextProperty().bind(ApplicationInstance.getInstance().createStringBinding("CLIENT_HOSTNAME_PROMPT"));
		usernameField.promptTextProperty().bind(ApplicationInstance.getInstance().createStringBinding("USERNAME_PROMPT"));
		aiPortField.promptTextProperty().bind(ApplicationInstance.getInstance().createStringBinding("AI_PORT_PROMPT"));
		aiHostNameField.promptTextProperty().bind(ApplicationInstance.getInstance().createStringBinding("AI_HOSTNAME_PROMPT"));
	}

	/**
	 * Configures some menu elements in ways which cannot be done in a FXML-file.
	 */
	private void configureMenuElements() {
		activeServerAddressLabel.getStyleClass().remove("text-input");
		clientConnectedAddressLabel.getStyleClass().remove("text-input");

		aiPortField.setTextFormatter(createPortFieldFormatter());
		clientPortField.setTextFormatter(createPortFieldFormatter());
		serverPortField.setTextFormatter(createPortFieldFormatter());
		usernameField.setTextFormatter(createUsernameFieldFormatter());
	}

	/**
	 * Creates a {@code TextFormatter} used to format the text of the username field.
	 * @return the {@link TextFormatter}.
	 */
	private TextFormatter<String> createUsernameFieldFormatter() {
		UnaryOperator<Change> filter = change -> {
			String text = change.getText();

			// Only allow non-empty content changes if the max name length isn't exceeded.
			if (usernameField.getText().length() + text.length() <= MAX_NAME_LENGTH ||
					!change.isContentChange() ||
					text.isEmpty()) {
				return change;
			} else {
				return null;
			}
		};

		return new TextFormatter<>(filter);
	}

	/**
	 * Creates a {@code TextFormatter} used to format the text of a port field.
	 * @return the {@link TextFormatter}.
	 */
	private TextFormatter<String> createPortFieldFormatter() {
		UnaryOperator<Change> filter = change -> {
			String text = change.getText();

			// Only allow non-empty content changes if they are numbers.
			if (text.matches("[0-9]*") || !change.isContentChange() || text.isEmpty()) {
				return change;
			} else {
				return null;
			}
		};

		return new TextFormatter<>(filter);
	}

	/**
	 * Adds Listeners in order to automatically update the user interface when changes occur.
	 */
	private void addListeners() {
		client.addListener(observable -> {
			Platform.runLater(() -> {
				// Automatically fill out ai hostname and port when connecting to a server.
				aiPortField.setText(clientPortField.getText());
				aiHostNameField.setText(clientHostnameField.getText());

				if (client == null) {
					// Empty the lobby.
					lobby.getUsers().clear();
				} else {
					// Display the ip and port of the server currently connected to.
					clientConnectedAddressLabel.setText(clientHostnameField.getText() + ":" + clientPortField.getText());
				}
			});
		});

		server.addListener(observable -> {
			Platform.runLater(() -> {
				// Automatically fill out client hostname and port when creating a server.
				clientHostnameField.setText(LOCAL_SERVER_HOSTNAME);
				clientPortField.setText(serverPortField.getText());

				// Automatically fill out AI hostname and port when creating a server.
				aiHostNameField.setText(LOCAL_SERVER_HOSTNAME);
				aiPortField.setText(serverPortField.getText());
			});
		});
	}

	/**
	 * Adds Bindings in order to automatically update the user interface when changes occur.
	 */
	private void bindGUI() {
		// Show different boxes depending on whether a server exists or not.
		serverActiveBox.visibleProperty().bind(serverProperty().isNotNull());
		serverNotActiveBox.visibleProperty().bind(serverProperty().isNull());

		// Show different boxes depending on whether a client texists or not.
		clientConnectedBox.visibleProperty().bind(clientProperty().isNotNull());
		clientNotConnectedBox.visibleProperty().bind(clientProperty().isNull());

		// Enable launch button only if a port is entered.
		launchButton.disableProperty().bind(serverPortField.textProperty().isEmpty());

		// Enable connect button only if hostname, port and username are specified.
		connectButton.disableProperty().bind(
				clientPortField.textProperty().isEmpty().or(
						clientHostnameField.textProperty().isEmpty().or(
								usernameField.textProperty().isEmpty())));

		// Enable add AI button only if hostname and port are specified.
		addAIButton.disableProperty().bind(
				aiPortField.textProperty().isEmpty().or(
						aiHostNameField.textProperty().isEmpty()));

		// Display the port of the active server.
		activeServerAddressLabel.textProperty().bind(Bindings.createStringBinding(() -> {
			if (server.isNotNull().get()) {
				InetAddress address = InetAddress.getLocalHost();
				String addressString = address.toString();

				// Remove everything in front of the IP address.
				int indexOfStartOfIPAddress = addressString.lastIndexOf("/") + 1;
				String ip = addressString.substring(indexOfStartOfIPAddress);

				return ip + ":" + server.get().getPort();
			} else {
				return "";
			}
		}, server));
	}

	/**
	 * Creates and starts a new {@code Server}.
	 * @see Server
	 */
	@FXML
	private void startServer() {
		// Play button sound.
		audioPlayer.playButtonSound();

		// Create new server.
		String port = serverPortField.getText();
		int portNumber = Integer.parseInt(port);
		server.set(new Server(portNumber, this));

		// Create new server controller.
		ServerController serverController = new ServerController(chatController, new Lobby(), server.get());
		server.get().setServerController(serverController);

		// Starts the new server.
		server.get().start();

		// Add close request.
		createOnCloseRequest();
	}

	/**
	 * Terminates the running {@code Server}.
	 * @see Server
	 */
	@FXML
	private void stopServer() {
		// Play button sound.
		audioPlayer.playButtonSound();

		// If the user is connected to this server, disconnect immediately.
		if (client.get() != null &&
				server.get() != null &&
				clientPortField.getText() == serverPortField.getText() &&
				clientHostnameField.getText() == LOCAL_SERVER_HOSTNAME) {
			client.get().stopRunning();
			client.set(null);

			lobby.getUsers().clear();
		}

		// Close the server.
		if (server.get() != null) {
			server.get().stopRunning();
			server.set(null);
		}
	}

	/**
	 * Creates a new {@code ClientConnection} to the {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	@FXML
	private void connectToServer() {
		// Remove all users from the lobby.
		lobby.getUsers().clear();

		// Play button sound.
		audioPlayer.playButtonSound();

		// Create new client.
		String hostname = clientHostnameField.getText();
		int port = Integer.parseInt(clientPortField.getText());
		client.set(new ClientConnection(hostname, port, ApplicationInstance.getInstance().getUser(), this));
		ApplicationInstance.getInstance().getUser().setClient(client.get());

		// Create new client controller.
		ClientController clientController = new ClientController(client.get(), chatController, lobby, applicationController);
		client.get().setClientController(clientController);

		// Add user to lobby if they aren't already in the lobby.
		if (!lobby.getUsers().contains(client.get().getUser())) {
			lobby.addUser(client.get().getUser());
		}

		// Set username.
		client.get().getUser().setName(usernameField.getText());

		// Start the client.
		client.get().start();

		// Create close request.
		createOnCloseRequest();
	}

	/**
	 * Sets a on close request, so that server and the client(s) close when closing the stage.
	 */
	private void createOnCloseRequest() {
		applicationController.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent event) {
				// Close server.
				if (server.get() != null) {
					server.get().stopRunning();
				}

				// Close client.
				if (client.get() != null) {
					client.get().stopRunning();
				}

				// Close all AI clients.
				for (ClientConnection aiClient : aiClients) {
					if (aiClient != null) {
						aiClient.stopRunning();
					}
				}
			}

		});
	}

	/**
	 * Disconnects the client from the {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	@FXML
	private void disconnectFromServer() {
		// Play button sound.
		audioPlayer.playButtonSound();

		if (client.get() != null) {
			client.get().stopRunning();
			client.set(null);
		}

		lobby.getUsers().clear();
	}

	/**
	 * Adds an {@code AIUser} to the selected {@code Server}.
	 * @see AIUser
	 * @see Server
	 */
	@FXML
	private void addAI() {
		// Play button sound.
		audioPlayer.playButtonSound();

		// Create new lobby for the AIUser.
		Lobby aiLobby = new Lobby();

		// Create new AIUser.
		AIUser aiUser = new AIUser(aiLobby.getAllColors());

		// Create new ClientConnection for the AIUser.
		String hostName = aiHostNameField.getText();
		int port = Integer.parseInt(aiPortField.getText());
		ClientConnection aiClient = new ClientConnection(hostName, port, aiUser, new MenuController());
		aiUser.setClient(aiClient);
		aiClients.add(aiClient);

		// Add user to Lobby.
		aiLobby.addUser(aiUser);

		// Create new ClientController for the AIUser.
		ClientController aiClientController = new ClientController(aiClient, aiLobby);
		aiClient.setClientController(aiClientController);

		// Start the AIUsers connection to the server.
		aiClient.start();

		// Create on close request.
		createOnCloseRequest();
	}

	/**
	 * Sets the {@code ChatController} used by this controller's client and {@code Server} to display messages.
	 * @param chatController the {@link ChatController}.
	 * @see ClientConnection
	 * @see Server
	 */
	public void setChatController(ChatController chatController) {
		this.chatController = chatController;
	}

	/**
	 * Sets the {@code ApplicationController} used by this controller's client.
	 * @param applicationController the {@link ApplicationController} to set.
	 * @see ClientConnection
	 */
	public void setApplicationController(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	/**
	 * Binds a text field's disable property to the existence of a client.
	 * @param writingField the {@link TextField} to bind.
	 */
	public void bindWritingField(TextField writingField) {
		writingField.disableProperty().bind(client.isNull());
	}

	/**
	 * Returns the client property.
	 * @return the clientProperty.
	 */
	public ObjectProperty<ClientConnection> clientProperty() {
		return client;
	}

	/**
	 * Returns the server property.
	 * @return the serverProperty.
	 */
	public ObjectProperty<Server> serverProperty() {
		return server;
	}

	/**
	 * Sets the lobby.
	 * @param lobby the lobby to set.
	 */
	public void setLobby(Lobby lobby) {
		this.lobby = lobby;
	}

}
