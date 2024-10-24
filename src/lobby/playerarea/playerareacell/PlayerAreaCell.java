package lobby.playerarea.playerareacell;

import java.io.IOException;

import game.player.PlayerState;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import lobby.Lobby;
import main.ApplicationInstance;
import users.User;

/**
 * Represents a list cell containing information about a {@code User} inside a {@code Lobby}.
 * @author Christoph Hermann
 * @see User
 * @see Lobby
 */
public class PlayerAreaCell extends ListCell<User> {

	/**
	 * The height of this cell including its padding.
	 */
	private int cellHeightWithPadding;

	/**
	 * The {@code Lobby} containing information needed by this cell.
	 * @see Lobby
	 */
	private Lobby lobby;

	/**
	 * Creates a new PlayerAreaCell.
	 * @param lobby the {@link Lobby} containing information needed by this cell.
	 * @param cellHeightWithPadding the height of this cell including its padding.
	 */
	public PlayerAreaCell(Lobby lobby, int cellHeightWithPadding) {
		this.lobby = lobby;
		this.cellHeightWithPadding = cellHeightWithPadding;
	}

	@Override
	protected void updateItem(User user, boolean empty) {
		super.updateItem(user, empty);

		if (empty || user == null) {
			setText(null);

			// Use an empty box to give an empty cell the same height as a non-empty one.
			HBox box = new HBox();
			box.setPrefHeight(cellHeightWithPadding - snappedTopInset() - snappedBottomInset());
			setGraphic(box);
		} else {
			setText(null);

			FXMLLoader loader = new FXMLLoader();
			try {
				// Load the player area cell view.
				HBox playerAreaCellView = loader.load(getClass().getResourceAsStream("/lobby/playerarea/playerareacell/PlayerAreaCellView.fxml"));
				PlayerAreaCellController controller = loader.getController();
				playerAreaCellView.setPrefHeight(cellHeightWithPadding - snappedTopInset() - snappedBottomInset());
				Platform.runLater(() -> setGraphic(playerAreaCellView));

				// Pass the client, user and lobby to the controller.
				controller.setClient(user.getClient());
				controller.setUserAndLobby(user, lobby);

				if (user != ApplicationInstance.getInstance().getUser()) {
					// Disable the color chooser inside the player area cell view if the user is someone else.
					controller.getColorChooser().setDisable(true);
				} else {
					// Otherwise, disable the color chooser if the user is ready.
					controller.getColorChooser().disableProperty().bind((Bindings.createBooleanBinding(() -> {
						return user.getState() != PlayerState.START_GAME;
					}, user.stateProperty())));
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

}
