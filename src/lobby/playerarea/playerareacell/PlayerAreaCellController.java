package lobby.playerarea.playerareacell;

import org.json.JSONObject;

import game.player.Player;
import game.player.PlayerState;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lobby.Lobby;
import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ProtocolStringConverter;
import network.protocol.ServerTypes;
import network.server.Server;
import users.User;

/**
 * Represents a controller responsible for controlling a cell containing the information of a {@code User}.
 * @author Christoph Hermann
 * @see User
 */
public class PlayerAreaCellController {

	/**
	 * The {@code User} whose information is shown in the cell controlled by this controller.
	 * @see User
	 */
	private User user;

	/**
	 * The {@code clientConnection} this controller uses to sends messages to the {@code Server}.
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client;

	/**
	 * The image of the {@code User} whose information is shown in this cell.
	 * @see User
	 */
	@FXML
	private ImageView userImage;

	/**
	 * The name of the {@code User} whose information is shown in this cell.
	 * @see User
	 */
	@FXML
	private Label userName;

	/**
	 * The combo box allowing the {@code User} to select their color.
	 * @see User
	 */
	@FXML
	private ComboBox<Color> colorChooser;

	/**
	 * The label showing if the {@code Player} is ready.
	 * @see Player
	 */
	@FXML
	private Label readyLabel;

	/**
	 * Binds the language of the labels to the language selected in the settings.
	 */
	@FXML
	private void initialize() {
		readyLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("READY"));
	}

	/**
	 * Represents a cell inside the colorChooser.
	 */
	private class ColorChooserCell extends ListCell<Color> {

		/**
		 * The rectangle visually representing the color associated with this cell.
		 */
		private final Rectangle rectangle = new Rectangle(10, 10);

		@Override protected void updateItem(Color color, boolean empty) {
			super.updateItem(color, empty);

			if (color == null || empty) {
				setGraphic(null);
			} else {
				rectangle.setStroke(Color.BLACK);
				rectangle.setFill(color);
				setGraphic(rectangle);
			}
		}
	};

	/**
	 * Requests the {@code Server} to change the color of the {@code User} whose information is shown in this cell.
	 * @param color newly chosen color.
	 * @see Server
	 * @see User
	 */
	private void changeColor(Color color) {
		// Create message.
		JSONObject object = new JSONObject();
		object.put(Attributes.COLOR.toString(), ProtocolStringConverter.getName(color));
		object.put(Attributes.NAME.toString(), user.getName());

		// Send message.
		client.sendToServer(ServerTypes.PLAYER.toString(), object);
	}

	/**
	 * Sets the {@code User} and {@code Lobby} and uses them to configure the view controlled by this controller.
	 * @param user the {@code User} whose information is shown in the cell controlled by this controller.
	 * @param lobby the {@link Lobby} containing the {@link ListView} which in turn contains the view elements
	 * controlled by this
	 */
	public void setUserAndLobby(User user, Lobby lobby) {
		this.user = user;

		// Set values.		
		colorChooser.setValue(user.getColor());
		colorChooser.setItems(lobby.getAllColors());

		addBindings();

		// Set onAction event.
		if (user == ApplicationInstance.getInstance().getUser()) {
			colorChooser.setOnAction((event) -> {
				Color selectedColor = colorChooser.getSelectionModel().getSelectedItem();
				changeColor(selectedColor);
			});
		}

		// Add listeners.
		this.user.colorProperty().addListener((observableValue, oldValue, newValue) -> colorChooser.setValue(user.getColor()));

		// Makes the colorChooser display colored rectangles instead of String representations of the colors.
		colorChooser.setCellFactory(colorListView -> {
			return new ColorChooserCell();
		});
	}

	/**
	 * Binds some of the GUI-elements controlled by this controller to the data model. 
	 */
	private void addBindings() {
		Platform.runLater(() -> {
			// Bind the username field to the name of the user.
			userName.textProperty().bind(user.nameProperty());

			// Bind the user image view to the image of the user.
			userImage.imageProperty().bind(Bindings.createObjectBinding(() -> {
				String imageLocation = user.getImageLocation();
				return new Image(getClass().getResourceAsStream(imageLocation));
			}, user.ImageLocationProperty()));

			// Binds the color in the button cell of the colorChooser to the color of the user.
			colorChooser.buttonCellProperty().bind(Bindings.createObjectBinding(() -> {
				return new ColorChooserCell();
			}, user.colorProperty()));

			// Show the ready label if the user wants to start the game.
			readyLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
				return user.stateProperty().get() != PlayerState.START_GAME;
			}, user.stateProperty()));
		});
	}

	/**
	 * Returns the {@code ComboBox} used for choosing a color.
	 * @return the {@link ComboBox}.
	 */
	public ComboBox<Color> getColorChooser() {
		return colorChooser;
	}

	/**
	 * Sets the {@code ClientConnection}.
	 * @param client the {@code ClientConnection}.
	 */
	public void setClient(ClientConnection client) {
		this.client = client;
	}

}