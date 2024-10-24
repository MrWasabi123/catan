package lobby.playerarea;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import lobby.Lobby;
import lobby.playerarea.playerareacell.PlayerAreaCell;
import network.client.ClientConnection;
import network.protocol.ServerTypes;
import network.server.Server;
import users.AIUser;
import users.User;

/**
 * Represents a controller responsible for controlling a player area inside a {@code Lobby}.
 * @author Christoph Hermann
 * @see Lobby
 */
public class PlayerAreaController {

	/**
	 * The height of a cell in the {@code ListView} containing the users.
	 * @see ListView
	 * @see User
	 */
	private static final int PLAYER_CELL_HEIGHT = 50;

	/**
	 * The combined height of the top and bottom edges between a list view and its cells.
	 */
	private static final int LIST_VIEW_EDGE_HEIGHT = 2;

	/**
	 * The {@code clientConnection} this controller uses to sends messages to the {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client;

	/**
	 * The {@code ListView} containing the information of all the users in the {@code Lobby}.
	 * @see ListView
	 * @see User
	 * @see Lobby
	 */
	@FXML
	private ListView<User> userListView;

	/**
	 * Signals the {@code Server} that the user is done configuring the {@code AIUser}.
	 * @see Server
	 * @see AIUser
	 */
	@FXML
	private void ready() {
		JSONObject message = new JSONObject();
		client.sendToServer(ServerTypes.GAME_START.toString(), message);
	}

	/**
	 * Passes the specified {@code Lobby} to the {@code ListView} containing the information of all the users.
	 * @param lobby the {@link Lobby} to set.
	 * @see ListView
	 * @see User
	 */
	public void setLobby(Lobby lobby) {
		// Sets the cells inside the userListView to be PlayerAreaCells.
		userListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {

			@Override
			public ListCell<User> call(ListView<User> listView) {
				return new PlayerAreaCell(lobby, PLAYER_CELL_HEIGHT);
			}

		});

		userListView.setItems(lobby.getUsers());
		userListView.setMaxHeight(lobby.getMaxUsers() * PLAYER_CELL_HEIGHT + LIST_VIEW_EDGE_HEIGHT);
	}

	/**
	 * Sets the {@code ClientConnection} used by this controller.
	 * @param client the {@link ClientConnection}.
	 */
	public void setClient(ClientConnection client) {
		this.client = client;
	}

}
