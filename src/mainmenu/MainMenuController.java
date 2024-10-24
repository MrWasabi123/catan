package mainmenu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import lobby.Lobby;
import main.ApplicationController;
import main.ApplicationInstance;
import sounds.AudioPlayer;

/**
 * Represents the controller responsible for controlling the main menu.
 * @author Christoph Hermann
 */
public class MainMenuController {

	/**
	 * The audio player.
	 */
	private AudioPlayer audioPlayer = new AudioPlayer();

	/**
	 * The controller responsible of managing the different scenes of this application.
	 */
	private ApplicationController applicationController;

	/**
	 * The settings menu.
	 */
	@FXML
	private Pane settings;

	/**
	 * The button for entering the {@code Lobby}
	 * @see Lobby
	 */
	@FXML
	private Button playButton;

	/**
	 * The button for opening the settings menu.
	 */
	@FXML
	private Button settingsButton;

	/**
	 * The button for quitting the game.
	 */
	@FXML
	private Button quitButton;

	/**
	 * The label showing the title of this application.
	 */
	@FXML
	private Label lTitle;

	/**
	 * The label showing the name of the creators of this application.
	 */
	@FXML
	private Label lAutoren;

	/**
	 * Binds the text of the labels and buttons to the selected locale.
	 */
	@FXML
	private void initialize() {
		playButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("PLAY"));
		settingsButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("SETTINGS"));
		quitButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("QUIT"));
		lTitle.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("TITLE"));
		lAutoren.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("AUTHOR"));
	}

	/**
	 * Loads the lobby scene.
	 */
	@FXML
	private void goToLobby() {
		audioPlayer.playButtonSound();
		applicationController.loadLobbyView();
		audioPlayer.stopIntroSound();
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void quit() {
		audioPlayer.playButtonSound();
		System.exit(0);
	}

	/**
	 * Opens the settings menu.
	 */
	@FXML
	private void openSettings() {
		audioPlayer.playButtonSound();
		settings.setVisible(true);
	}

	/**
	 * Sets the controller responsible of managing the different scenes of this application.
	 * @param applicationController the {@link ApplicationController} to set.
	 */
	public void setApplicationController(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

}
