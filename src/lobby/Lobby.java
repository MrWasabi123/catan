package lobby;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import users.User;

/**
 * Represents a lobby.
 * @author Christoph Hermann
 */
public class Lobby {

	/**
	 * The maximum amount of {@code Users} allowed to join this lobby.
	 * @see User
	 */
	private final static int MAX_USERS = 4;

	/**
	 * The {@code Users} currently in this lobby.
	 * @see User
	 */
	private ObservableList<User> users = FXCollections.observableArrayList(new ArrayList<User>());

	/**
	 * The list of all possible colors available to the {@code Users}.
	 * @see User
	 */
	private final ObservableList<Color> colors = FXCollections.observableArrayList(Color.RED, Color.WHITE,
			Color.BLUE, Color.ORANGE);

	/**
	 * Returns the {@code User} with the specified id.
	 * @param id the id of the {@link User}
	 * @return the user with the specified id. Returns null, if no user with the given id exists.
	 */
	public User getUser(int id) {
		for (User user : users) {
			if (user.getId() == id) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Returns the maximum amount of users allowed to join this lobby.
	 * @return the maxUsersInLobby.
	 * @see User
	 */
	public int getMaxUsers() {
		return MAX_USERS;
	}

	/**
	 * Returns the list of users in this lobby.
	 * @return the {@link User Users}.
	 */
	public ObservableList<User> getUsers() {
		return users;
	}

	/**
	 * Adds the specified {@code User} to this lobby
	 * @param user the {@link User}.
	 */
	public void addUser(User user) {
		users.add(user);
	}

	/**
	 * Returns a list of all possible colors available to the users.
	 * @return a list of all colors.
	 * @see User
	 */
	public ObservableList<Color> getAllColors() {
		return colors;
	}

}
