package sounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * The music player playing the background music.
 * @author Christoph Hermann
 */
public class BackgroundMusicPlayer {

	/**
	 * The folder location containing the music files.
	 */
	private static final String MUSIC_LOCATION = "sounds/backgroundmusic";

	/**
	 * The volume of the background music.
	 */
	private static DoubleProperty volume = new SimpleDoubleProperty();

	/**
	 * Whether this music player is muted or not.
	 */
	private static BooleanProperty mute = new SimpleBooleanProperty();

	/**
	 * The list of background songs.
	 */
	private List<Media> songs = createSongs(MUSIC_LOCATION);

	/**
	 * The song which is currently playing.
	 */
	private Media currentSong;

	/**
	 * The media player used to play the background music.
	 */
	private static MediaPlayer musicPlayer;

	/**
	 * Starts the background music.
	 */
	public void startBackgroundMusic() {
		if (musicPlayer == null) {
			playSong(songs.get(0));
		} else {
			musicPlayer.play();
		}
	}

	/**
	 * Plays the specified song.
	 * @param song the music piece to play.
	 */
	private void playSong(Media song) {
		currentSong = song;

		musicPlayer = createNewMusicPlayer(song);
		musicPlayer.play();
	}

	/**
	 * Plays the next song in the list of songs. If the end of the list is reached, the first one will be played.
	 */
	private void playNextSong() {
		try {
			musicPlayer.stop();
			musicPlayer.volumeProperty().unbind();
			musicPlayer.muteProperty().unbind();
			musicPlayer.dispose();

			Media nextSong = findNextSong(currentSong);
			musicPlayer = createNewMusicPlayer(nextSong);

			currentSong = nextSong;

			musicPlayer.play();
		} catch (SongNotInListException exception){
			musicPlayer.stop();
		}
	}

	/**
	 * Creates a new {@code MediaPlayer} playing the specified song.
	 * @param song the song played by the {@link MediaPlayer}.
	 * @return the newly created music player.
	 */
	private MediaPlayer createNewMusicPlayer(Media song) {
		MediaPlayer newMediaPlayer = new MediaPlayer(song);

		newMediaPlayer.volumeProperty().bind(volume);
		newMediaPlayer.muteProperty().bind(mute);

		newMediaPlayer.setOnEndOfMedia(() -> {
			newMediaPlayer.stop();
			playNextSong();
		});

		return newMediaPlayer;
	}

	/**
	 * Finds the song which comes after the specified song in the list of all songs.
	 * @param currentSong the current song.
	 * @return the next song.
	 * @throws SongNotInListException thrown, if the current song is not in the list of all songs.
	 */
	private Media findNextSong(Media currentSong) throws SongNotInListException {
		int currentSongIndex = getIndexOf(currentSong);
		int nextSongIndex = (currentSongIndex + 1) % songs.size();
		Media nextSong = songs.get(nextSongIndex);

		return nextSong;
	}

	/**
	 * Returns the index of the specified song within the list of all songs.
	 * @param song the song of which the index is needed.
	 * @return the index of the song.
	 * @throws SongNotInListException thrown, if the current song is not in the list of all songs.
	 */
	private int getIndexOf(Media song) throws SongNotInListException {
		for (int i=0; i<songs.size(); i++) {
			if (songs.get(i) == song) {
				return i;
			}
		}
		throw new SongNotInListException();
	}

	/**
	 * Represents an exception which indicates that a specified song is not in a certain list.
	 */
	private class SongNotInListException extends Exception {

		/**
		 * The serial version UID.
		 */
		private static final long serialVersionUID = 1L;

	}

	/**
	 * Creates the list of songs used by this BackgroundMusicPlayer.
	 * @param musicLocation the location of the music files.
	 * @return the list of songs.
	 */
	private List<Media> createSongs(String musicLocation) {
		List<Media> songs = new ArrayList<>();

		songs.add(new Media(this.getClass().getResource("/sounds/backgroundmusic/Teddy_Bear_Waltz.mp3").toString()));
		songs.add(new Media(this.getClass().getResource("/sounds/backgroundmusic/Village_Consort.mp3").toString()));
		songs.add(new Media(this.getClass().getResource("/sounds/backgroundmusic/Court_of_the_Queen.mp3").toString()));
		songs.add(new Media(this.getClass().getResource("/sounds/backgroundmusic/Rainbows.mp3").toString()));
		songs.add(new Media(this.getClass().getResource("/sounds/backgroundmusic/Rumination.mp3").toString()));
		songs.add(new Media(this.getClass().getResource("/sounds/backgroundmusic/Ascending_the_Vale.mp3").toString()));

		Collections.shuffle(songs);

		return songs;
	}

	/**
	 * Stops the background music.
	 */
	public void stopBackgroundMusic() {
		if (musicPlayer != null) {
			musicPlayer.stop();
		}
	}

	/**
	 * Returns the volumeProperty.
	 * @return the volumeProperty.
	 */
	public static DoubleProperty volumeProperty() {
		return volume;
	}

	/**
	 * Mutes and/or unmutes this music player.
	 * @param newMuteValue whether this music player should be muted or not.
	 */
	public static void setMute(boolean newMuteValue) {
		mute.set(newMuteValue);
	}

	/**
	 * Returns the mute property.
	 * @return the mute property.
	 */
	public static BooleanProperty muteProperty() {
		return mute;
	}

}
