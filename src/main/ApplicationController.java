package main;

import java.io.IOException;

import game.Game;
import game.GameController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lobby.LobbyController;
import lobby.endscreen.EndscreenController;
import mainmenu.MainMenuController;
import network.client.ClientConnection;
import sounds.AudioPlayer;

/**
 * The ApplicationController manages the different scenes of the application. 
 * @author Paula Wikidal
 * @author Christoph Hermann
 */
public class ApplicationController {

	/**
	 * The audio player.
	 */
	private AudioPlayer audioPlayer = new AudioPlayer();

	/**The stage*/
	private Stage stage;

	/**
	 * The main scene of this application.
	 */
	private Scene scene;

	/**
	 * The main node which is currently visible.
	 */
	private Node currentlyVisibleView;

	/**
	 * The root container for all views of this application.
	 */
	private StackPane root = new StackPane();

	/**The game, the main class of the model*/
	private Game game;

	/**The Client*/
	private ClientConnection client;

	/**The node that contains the view elements of the game*/
	private Node gameView;

	/**The node that contains the view elements of the lobby*/
	private Node lobbyView;

	/**The node that contains the view elements of the main menu*/
	private Node mainMenuView;

	/**The node that contains the view elements of the end screen*/
	private Node endScreenView;

	/**The Controller of the {@code Lobby}*/
	private LobbyController lobbyController;

	/**
	 * Creates an instance of ApplicationController and loads the first scene.
	 * @param stage The stage of the application
	 */
	public ApplicationController(Stage stage) {
		this.stage = stage;

		createScene();
		loadMainMenuView();
	}

	/**
	 * Creates the main scene of this application.
	 */
	private void createScene() {
		scene = new Scene(root, GeneralSettings.WINDOW_WIDTH, GeneralSettings.WINDOW_HEIGHT);
		root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		stage.setScene(scene);
	}

	/**
	 * Creates a new main menu scene and sets the current scene to the main menu scene.
	 */
	public void loadMainMenuView() {
		if(mainMenuView == null) {
			FXMLLoader loader = new FXMLLoader();
			try {
				// Load view and controller.
				mainMenuView = loader.load(getClass().getResourceAsStream("/mainmenu/MainMenuView.fxml"));
				MainMenuController mainMenuController = loader.getController();

				// Configure controller.
				mainMenuController.setApplicationController(this);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		show(mainMenuView);
		playIntroFade();
		audioPlayer.playIntroSound();
	}

	/**
	 * Creates a new lobby scene and sets the current scene to the lobby scene.
	 */
	public void loadLobbyView() {
		if(lobbyView == null) {
			FXMLLoader loader = new FXMLLoader();
			try {
				// Load view and controller.
				lobbyView = loader.load(getClass().getResourceAsStream("/lobby/LobbyView.fxml"));
				lobbyController = ((LobbyController) loader.getController());

				// Configure controller.
				lobbyController.setApplicationController(this);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		show(lobbyView);
		lobbyController.removeFocusFromImportantElements();
	}

	/**
	 * Creates a new Game and a new games scene if they don't already exist and 
	 * sets the current scene to the game scene.
	 * @see Game
	 */
	public void loadGameView() {
		if(gameView == null) {
			FXMLLoader loader = new FXMLLoader();
			try {
				// Load view and controller.
				gameView = loader.load(getClass().getResourceAsStream("/game/GameView.fxml"));
				GameController gameController = (GameController) loader.getController();

				// Configure controller.
				gameController.setApplicationController(this);
				gameController.doInitializations(game, client);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		show(gameView);
	}

	/**
	 * Shows the end screen with the specified text on it.
	 * @param text the text to show on the end screen.
	 */
	public void loadEndScreenView(String text) {
		if(endScreenView == null) {
			FXMLLoader loader = new FXMLLoader();
			try {
				// Load view and controller.
				endScreenView = loader.load(getClass().getResourceAsStream("/lobby/endscreen/EndscreenView.fxml"));
				EndscreenController controller = loader.getController();

				// Configure controller.
				controller.doInitializations(game, client);
				controller.setApplicationController(this);
				controller.putText(text);
			}
			catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		show(endScreenView);
	}

	/**
	 * Resets everything that is necessary to start a new game.
	 */
	private void resetApplication() {
		// Reset the lobby.
		lobbyController.reset();

		// Reset the views.
		gameView = null;
		endScreenView = null;
	}

	/**
	 * Fades the main menu in.
	 */
	private void playIntroFade() {
		mainMenuView.setOpacity(0);
		FadeTransition transition = new FadeTransition(Duration.millis(300), mainMenuView);

		transition.setFromValue(0);
		transition.setToValue(1);
		transition.setDelay(Duration.millis(2600));

		transition.play();
	}

	/**
	 * Displays the specified node.
	 * @param node the node to show.
	 */
	private void show(Node node) {
		// Configure mouse transparency.
		node.setMouseTransparent(true);
		if (currentlyVisibleView != null) {
			currentlyVisibleView.setMouseTransparent(true);
		}

		// Fade between nodes.
		fadeOut(currentlyVisibleView, node);

		// Configure new node.
		root.getChildren().add(0, node);
		currentlyVisibleView = node;
	}

	/**
	 * Plays a fade out animation on the specified node.
	 * @param oldNode the node to fade out.
	 * @param newNode the previously visible node.
	 */
	private void fadeOut(Node oldNode, Node newNode) {
		FadeTransition fadeOut = new FadeTransition(Duration.millis(2000), oldNode);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);
		fadeOut.setOnFinished(event -> {
			newNode.setMouseTransparent(false);

			root.getChildren().remove(oldNode);
			if (oldNode != null) {
				oldNode.setOpacity(1);
			}
		});
		fadeOut.play();
	}

	/**
	 * Returns back to the main menu.
	 */
	public void returnToMainMenu() {
		resetApplication();
		loadMainMenuView();
	}

	/**
	 * @return The gameScene
	 */
	public Scene getGameView() {
		return scene;
	}

	/**
	 * Setter for the Game
	 * @param game the Game
	 */
	public void setGame(Game game){
		this.game = game;
	}

	/**
	 * Sets the client.
	 * @param client the client to set.
	 * @see ClientConnection
	 */
	public void setClient(ClientConnection client) {
		this.client = client;
	}

	/**
	 * Returns the stage of this application.
	 * @return the stage.
	 */
	public Stage getStage() {
		return stage;
	}

}
