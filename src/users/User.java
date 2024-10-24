package users;

import game.player.Player;
import game.player.PlayerState;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import network.client.ClientConnection;

/**
 * Represents a user of this program.
 * @author Christoph Hermann
 */
public abstract class User {

	/**
	 * The name of this user.
	 */
	StringProperty name = new SimpleStringProperty();

	/**
	 * The location of this users profile picture.
	 */
	StringProperty imageLocation = new SimpleStringProperty();

	/**
	 * The id of this user.
	 */
	int id;

	/**
	 * The state of this user.
	 * @see PlayerState
	 */
	ObjectProperty<PlayerState> state = new SimpleObjectProperty<>();

	/**
	 * The color of this user.
	 */
	ObjectProperty<Color> color = new SimpleObjectProperty<>();

	/**
	 * The {@code Player} controlled by this user.
	 * @see Player
	 */
	Player player;

	/**
	 * This user's {@code ClientConnection}.
	 */
	ClientConnection client;

	/**
	 * Creates a new User.
	 */
	public User() {
		state.set(PlayerState.START_GAME);

		imageLocation.bind(Bindings.createStringBinding(() -> {
			String location = "/game/player/images/profilepic.png";

			if (color.get() != null) {
				switch (color.get().toString()) {
				case "0xff0000ff":
					location = "/game/player/images/red.png";
					break;
				case "0xffa500ff":
					location = "/game/player/images/orange.png";
					break;
				case "0xffffffff":
					location = "/game/player/images/white.png";
					break;
				case "0x0000ffff":
					location = "/game/player/images/blue.png";
					break;
				}
			}

			return location;
		}, color));
	}

	/**
	 * Returns the name of this user.
	 * @return the name.
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * Returns the location of the image of this user.
	 * @return the image location.
	 */
	public String getImageLocation() {
		return imageLocation.get();
	}

	/**
	 * Sets the name of this user.
	 * @param name the name.
	 */
	public void setName(String name) {
		this.name.set(name);
	}

	/**
	 * Returns the id of this user.
	 * @return the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this user.
	 * @param id the id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the color of this user.
	 * @return the color.
	 */
	public Color getColor() {
		return color.get();
	}

	/**
	 * Returns the color property of this user.
	 * @return the color property.
	 */
	public ObjectProperty<Color> colorProperty() {
		return color;
	}

	/**
	 * Sets the color of this user.
	 * @param color the color to set.
	 */
	public void setColor(Color color) {
		this.color.set(color);
	}

	/**
	 * Sets the state of this user.
	 * @param state the {@link PlayerState}.
	 */
	public void setState(PlayerState state) {
		this.state.set(state);
	}

	/**
	 * Returns the state of this user.
	 * @return state the {@link PlayerState}.
	 */
	public PlayerState getState() {
		return state.get();
	}

	/**
	 * Returns the state property of this user.
	 * @return state the {@link PlayerState} property.
	 */
	public ObjectProperty<PlayerState> stateProperty() {
		return state;
	}

	/**
	 * Returns the name property of this user
	 * @return the name property.
	 */
	public StringProperty nameProperty() {
		return name;
	}

	/**
	 * Returns the image location property of this user.
	 * @return the image location property.
	 */
	public StringProperty ImageLocationProperty() {
		return imageLocation;
	}

	/**
	 * Returns the {@code Player} controlled by this user.
	 * @return the {@link Player}.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the {@code Player} controlled by this user.
	 * @param player the {@link Player} to set.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Returns the {@code ClientConnection} of this user.
	 * @return the ClientConnection.
	 */
	public ClientConnection getClient() {
		return client;
	}

	/**
	 * Sets the {@code ClientConnection} of this user.
	 * @param client the ClientConnection to set.
	 */
	public void setClient(ClientConnection client) {
		this.client = client;
	}

}
