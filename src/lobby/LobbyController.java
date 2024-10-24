package lobby;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lobby.buttons.ButtonsController;
import lobby.chat.ChatController;
import lobby.menu.MenuController;
import lobby.playerarea.PlayerAreaController;
import main.ApplicationController;

/**
 * Represents a controller responsible for controlling a {@code Lobby}.
 * @author Christoph Hermann
 * @see Lobby
 */
public class LobbyController {

	/**
	 * The controller responsible of managing the different scenes of this application.
	 */
	private ApplicationController applicationController;

	/**
	 * The controller responsible of managing the menu area inside the {@code Lobby}.
	 * @see Lobby
	 */
	private MenuController menuController;

	/**
	 * The controller responsible of managing the buttons area inside the {@code Lobby}.
	 * @see Lobby
	 */
	private ButtonsController buttonsController;

	/**
	 * The controller responsible of managing the chat area inside the {@code Lobby}.
	 * @see Lobby
	 */
	private ChatController chatController;

	/**
	 * The controller responsible of managing the player area inside the {@code Lobby}.
	 * @see Lobby
	 */
	private PlayerAreaController playerAreaController;

	/**
	 * The {@code Lobby} controlled by this controller.
	 */
	private Lobby lobby;

	/**
	 * The pane on the right side of the {@code Lobby}.
	 * @see Lobby
	 */
	@FXML
	private BorderPane rightPane;

	/**
	 * The pane in the center of the {@code Lobby}.
	 * @see Lobby
	 */
	@FXML
	private BorderPane centerPane;

	/**
	 * The buttons area inside the {@code Lobby}.
	 * @see Lobby
	 */
	@FXML
	private HBox buttons;

	/**
	 * The chat area inside the {@code Lobby}.
	 * @see Lobby
	 */
	@FXML
	private BorderPane chat;

	/**
	 * The menu area inside the {@code Lobby}.
	 * @see Lobby
	 */
	@FXML
	private VBox menu;

	/**
	 * The player area inside the {@code Lobby}.
	 * @see Lobby
	 */
	@FXML
	private VBox playerArea;

	/**
	 * Loads and configures the contents of the {@code Lobby} during initialization of this controller.
	 * @see Lobby
	 */
	@FXML
	private void initialize() {
		lobby = new Lobby();

		BorderPane playerAreaView = loadPlayerArea();
		loadChat();
		loadMenuIfNecessary();
		loadButtonsIfNecessary();

		configureControllers();
		addBindings(playerAreaView);

		removeFocusFromImportantElements();
	}

	/**
	 * Focuses an irrelevant element so that the focus isn't on a text field which would remove its prompt text. 
	 */
	public void removeFocusFromImportantElements() {
		Platform.runLater(() -> centerPane.requestFocus());
	}

	/**
	 * Loads the player area view.
	 * @return the player area view.
	 */
	private BorderPane loadPlayerArea() {
		try {
			FXMLLoader playerAreaLoader = new FXMLLoader();
			BorderPane playerAreaView = playerAreaLoader.load(getClass().getResourceAsStream("/lobby/playerarea/PlayerAreaView.fxml"));
			playerAreaController = playerAreaLoader.getController();
			centerPane.setTop(playerAreaView);
			return playerAreaView;
		} catch (IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	/**
	 * Loads the chat view.
	 */
	private void loadChat() {
		try {
			FXMLLoader chatLoader = new FXMLLoader();
			Pane chatView = chatLoader.load(getClass().getResourceAsStream("/lobby/chat/ChatView.fxml"));
			chatController = chatLoader.getController();
			centerPane.setCenter(chatView);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Loads the menu view it it doen't already exist.
	 */
	private void loadMenuIfNecessary() {
		try {
			if (menuController == null) {
				FXMLLoader menuLoader = new FXMLLoader();
				VBox menuView = menuLoader.load(getClass().getResourceAsStream("/lobby/menu/MenuView.fxml"));
				menuController = menuLoader.getController();
				rightPane.setTop(menuView);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Loads the buttons view if it doesn't already exist.
	 */
	private void loadButtonsIfNecessary() {
		try {
			if (buttonsController == null) {
				FXMLLoader buttonsLoader = new FXMLLoader();
				HBox buttonsView = buttonsLoader.load(getClass().getResourceAsStream("/lobby/buttons/ButtonsView.fxml"));
				buttonsController = buttonsLoader.getController();
				rightPane.setBottom(buttonsView);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Configures the controllers of the individual view elements.
	 */
	private void configureControllers() {
		menuController.bindWritingField(chatController.getWritingField());
		menuController.setChatController(chatController);
		menuController.setLobby(lobby);
		chatController.setClientConnection(menuController.clientProperty());
		buttonsController.setClientConnection(menuController.clientProperty());
		playerAreaController.setClient(menuController.clientProperty().get());
		playerAreaController.setLobby(lobby);
	}

	/**
	 * Adds all bindings between the individual controllers.
	 * @param playerAreaView the player area which will be used in a binding.
	 */
	private void addBindings(BorderPane playerAreaView) {
		menuController.clientProperty().addListener(observable -> {
			if (menuController.clientProperty().get() == null) {
				playerAreaView.visibleProperty().unbind();
				playerAreaView.setVisible(false);
			} else {
				playerAreaView.visibleProperty().bind(menuController.clientProperty().isNotNull());
			}
		});
	}

	/**
	 * Sets the controller responsible of managing the different scenes of this application.
	 * @param applicationController the {@link ApplicationController} to set.
	 */
	public void setApplicationController(ApplicationController applicationController) {
		this.applicationController = applicationController;

		menuController.setApplicationController(this.applicationController);
		buttonsController.setApplicationController(this.applicationController);
	}

	/**
	 * Resets this controller. This will reload most of the view elements and rebuild its controller structure.
	 */
	public void reset() {
		initialize();
		setApplicationController(applicationController);
	}

	/**
	 * Returns the {@code MenuController}.
	 * @return the {@code MenuController}.
	 */
	public MenuController getMenuController() {
		return menuController;
	}

}
