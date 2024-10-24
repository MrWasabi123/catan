package mainmenu;

import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import main.ApplicationInstance;
import sounds.AudioPlayer;
import sounds.BackgroundMusicPlayer;

/**
 * The controller of the settings. It updates the values in the {@code ApplicationInstance} 
 * according to the selected settings. 
 * 
 * @author Paula Wikidal
 * @author Christoph Hermann
 */
public class SettingsController {

	/**
	 * The audio player.
	 */
	private AudioPlayer audioPlayer = new AudioPlayer();

	/**The pane containing the settings*/
	@FXML private Pane settings;

	/**The button to select English as a language*/
	@FXML private RadioButton enButton;

	/**The button to select German as a language*/
	@FXML private RadioButton deButton;

	/**The button to select Chinese as a language*/
	@FXML private RadioButton cnButton;

	/**The header of the language selection box*/
	@FXML private Label languagesLabel;

	/**The label for the sound effects volume slider*/
	@FXML private Label soundEffectsVolumeLabel;

	/**The slider to select the volume of the sound effects*/
	@FXML private Slider soundEffectsVolumeSlider;

	/**The label for the music volume slider*/
	@FXML private Label musicVolumeLabel;

	/**The slider to select the volume of the sound effects*/
	@FXML private Slider musicVolumeSlider;

	/**The button to close the settings*/
	@FXML private Button okButton;

	/**The check box used for turning the background music on an off*/
	@FXML private CheckBox musicOnOffBox;

	/**The check box used for turning the background music on an off*/
	@FXML private CheckBox soundEffectsOnOffBox;

	/**
	 * Initializes the style of the buttons, changes the settings according to the 
	 * options selected in the settings menu and binds the language of the headers to the
	 * selected language. 
	 */
	@FXML 
	private void initialize() {
		enButton.getStyleClass().remove("radio-button");
		deButton.getStyleClass().remove("radio-button");
		cnButton.getStyleClass().remove("radio-button");
		okButton.getStyleClass().remove("button");

		languagesLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("LANGUAGE"));
		soundEffectsVolumeLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("FX_VOLUME"));
		musicVolumeLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("MUSIC_VOLUME"));
		okButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("OK"));

		configureAudio();
	}

	/**
	 * Configures the sound effects and the background music.
	 */
	private void configureAudio() {
		configureBackgroundMusic();
		configureSoundEffects();
	}

	/**
	 * Creates listeners and bindings for the sound effects.
	 */
	private void configureSoundEffects() {
		soundEffectsOnOffBox.selectedProperty().set(!AudioPlayer.muteProperty().get());

		soundEffectsOnOffBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			AudioPlayer.muteProperty().set(!newValue);
			audioPlayer.playButtonSound();
		});

		AudioPlayer.muteProperty().addListener((observable, oldValue, newValue) -> {
			soundEffectsOnOffBox.setSelected(!newValue);
		});

		AudioPlayer.volumeProperty().bind(soundEffectsVolumeSlider.valueProperty());
		AudioPlayer.introVolumeProperty().bind(musicVolumeSlider.valueProperty().multiply(10));
	}

	/**
	 * Creates listeners and bindings for the background music.
	 */
	private void configureBackgroundMusic() {
		musicOnOffBox.selectedProperty().set(!BackgroundMusicPlayer.muteProperty().get());

		musicOnOffBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			BackgroundMusicPlayer.setMute(!newValue);
			audioPlayer.playButtonSound();
		});

		BackgroundMusicPlayer.muteProperty().addListener((observable, oldValue, newValue) -> {
			musicOnOffBox.setSelected(!newValue);
		});

		BackgroundMusicPlayer.volumeProperty().bind(musicVolumeSlider.valueProperty());
	}

	/**
	 * Sets the language to English.
	 */
	@FXML
	private void setEN() {
		audioPlayer.playButtonSound();
		ApplicationInstance.getInstance().changeLocale(new Locale("en","US"));
	}

	/**
	 * Sets the language to German.
	 */
	@FXML
	private void setDE() {
		audioPlayer.playButtonSound();
		ApplicationInstance.getInstance().changeLocale(new Locale("de","DE"));
	}

	/**
	 * Sets the language to Chinese.
	 */
	@FXML
	private void setCN() {
		audioPlayer.playButtonSound();
		ApplicationInstance.getInstance().changeLocale(new Locale("zh","CN"));
	}

	/**
	 * Closes the settings.
	 */
	@FXML
	private void close() {
		audioPlayer.playButtonSound();
		settings.setVisible(false);
	}
}
