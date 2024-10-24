package sounds;

import game.board.construction.Construction;
import game.player.Player;
import game.trade.Trade;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * The audio player responsible for playing the sounds in the game.
 * @author Paula Wikidal
 * @author Christoph Hermann
 */
public class AudioPlayer {

	/**
	 * The volume of the sounds.
	 */
	private static SimpleDoubleProperty volumeProperty = new SimpleDoubleProperty();

	/**
	 * Whether this audio player is muted or not.
	 */
	private static BooleanProperty mute = new SimpleBooleanProperty();

	/**
	 * The media player used to play the intro sound.
	 */
	private static final MediaPlayer INTRO_PLAYER = createIntroPlayer();

	/**
	 * Creates and configures the intro player.
	 * @return the intro player.
	 */
	private static MediaPlayer createIntroPlayer() {
		Media introSound = new Media(AudioPlayer.class.getResource("effects/intro.wav").toString());
		MediaPlayer introPlayer = new MediaPlayer(introSound);
		introPlayer.muteProperty().bind(BackgroundMusicPlayer.muteProperty());
		introPlayer.volumeProperty().bind(BackgroundMusicPlayer.volumeProperty());

		return introPlayer;
	}

	/**
	 * The sound of a button being clicked.
	 */
	private static final AudioClip BUTTON = new AudioClip(AudioPlayer.class.getResource("effects/ButtonSound.wav").toString());

	/**
	 * The sound that is played when the robber is moved.
	 */
	private static final AudioClip ROBBER = new AudioClip(AudioPlayer.class.getResource("effects/RobberSound.m4a").toString());

	/**
	 * The sound that is played when the dice are rolled.
	 */
	private static final AudioClip DICE = new AudioClip(AudioPlayer.class.getResource("effects/DiceSound.m4a").toString());

	/**
	 * The sound that is played when the player receives or loses a card.
	 */
	private static final AudioClip CARD = new AudioClip(AudioPlayer.class.getResource("effects/CardSound.wav").toString());

	/**
	 * The sound that is played when the {@code Player's} turn starts.
	 * @see Player
	 */
	private static final AudioClip TURN_NOTIFICATION = new AudioClip(AudioPlayer.class.getResource("effects/turnNotification.mp3").toString());

	/**
	 * The sound that is played when the an error occurs.
	 */
	private static final AudioClip ERROR = new AudioClip(AudioPlayer.class.getResource("effects/error.wav").toString());

	/**
	 * The sound that is played when finishing a {@code Trade}.
	 * @see Trade
	 */
	private static final AudioClip TRADE = new AudioClip(AudioPlayer.class.getResource("effects/trade.wav").toString());

	/**
	 * The sound that is played when building a {@code Construction}.
	 * @see Construction
	 */
	private static final AudioClip CONSTRUCTION = new AudioClip(AudioPlayer.class.getResource("effects/construction.wav").toString());

	/**
	 * The sound that is played when receiving a chat message.
	 */
	private static final AudioClip CHAT = new AudioClip(AudioPlayer.class.getResource("effects/chat.wav").toString());

	/**
	 * The sound that is played when a {@code User} is ready to start the game.
	 */
	private static final AudioClip READY = new AudioClip(AudioPlayer.class.getResource("effects/ready.mp3").toString());

	/**
	 * The sound that is played when winning the game.
	 */
	private static final AudioClip VICTORY = new AudioClip(AudioPlayer.class.getResource("effects/victory.wav").toString());

	/**
	 * The sound that is played when losing the game.
	 */
	private static final AudioClip DEFEAT = new AudioClip(AudioPlayer.class.getResource("effects/defeat.wav").toString());

	/**
	 * The sound that is played when winning the game.
	 */
	private static final AudioClip CONNECT = new AudioClip(AudioPlayer.class.getResource("effects/connect.wav").toString());

	/**
	 * The sound that is played when winning the game.
	 */
	private static final AudioClip SPECIAL_CARD = new AudioClip(AudioPlayer.class.getResource("effects/specialCard.mp3").toString());

	/**
	 * Plays the button sound.
	 */
	public void playButtonSound() {
		if (!mute.get()) {
			BUTTON.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the robber sound.
	 */
	public void playRobberSound() {
		if (!mute.get()) {
			ROBBER.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the dice sound.
	 */
	public void playDiceSound() {
		if (!mute.get()) {
			DICE.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the card sound.
	 */
	public void playCardSound() {
		if (!mute.get()) {
			CARD.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the turn notification sound.
	 */
	public void playTurnNotificationSound() {
		if (!mute.get()) {
			TURN_NOTIFICATION.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the error sound.
	 */
	public void playErrorSound() {
		if (!mute.get()) {
			ERROR.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the trade sound.
	 */
	public void playTradeSound() {
		if (!mute.get()) {
			TRADE.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the construction sound.
	 */
	public void playConstructionSound() {
		if (!mute.get()) {
			CONSTRUCTION.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the chat sound.
	 */
	public void playChatSound() {
		if (!mute.get()) {
			CHAT.play(volumeProperty.get());
		}	
	}

	/**
	 * Plays the ready sound.
	 */
	public void playReadySound() {
		if (!mute.get()) {
			READY.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the victory sound.
	 */
	public void playVictorySound() {
		if (!mute.get()) {
			VICTORY.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the defeat sound.
	 */
	public void playDefeatSound() {
		if (!mute.get()) {
			DEFEAT.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the connect sound.
	 */
	public void playConnectSound() {
		if (!mute.get()) {
			CONNECT.play(volumeProperty.get());
		}
	}

	/**
	 * Plays the intro sound.
	 */
	public void playIntroSound() {
		INTRO_PLAYER.play();
	}

	/**
	 * Plays the special card sound.
	 */
	public void playSpecialCardSound() {
		if (!mute.get()) {
			SPECIAL_CARD.play(volumeProperty.get());
		}
	}

	/**
	 * Stops the intro sound.
	 */
	public void stopIntroSound() {
		INTRO_PLAYER.volumeProperty().unbind();
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(INTRO_PLAYER.volumeProperty(), 0)));
		timeline.setOnFinished(event -> {
			INTRO_PLAYER.stop();
			INTRO_PLAYER.setVolume(volumeProperty.get());
		});
		timeline.play();
	}

	/**
	 * Getter for the DoubleProperty representing the volume of the sounds.
	 * @return the volume Property.
	 */
	public static SimpleDoubleProperty volumeProperty() {
		return volumeProperty;
	}

	/**
	 * Getter for the DoubleProperty representing the volume of the intro music.
	 * @return the volume Property of the intro.
	 */
	public static DoubleProperty introVolumeProperty() {
		return INTRO_PLAYER.volumeProperty();
	}

	/**
	 * Returns the mute property.
	 * @return the mute property.
	 */
	public static BooleanProperty muteProperty() {
		return mute;
	}

}
