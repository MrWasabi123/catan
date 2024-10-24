package main;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lobby.Lobby;
import lobby.menu.MenuController;
import network.client.ClientConnection;
import network.client.ClientController;
import users.AIUser;

/**
 * The main class of this application. It provides the main method and therefore the entry point of this application.
 * @author Christoph Hermann
 */
public class Main extends Application {

	/**
	 * The main method of this application. All it does is launch this application. 
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			launch(args); 
		} else {
			String hostname = args[0];
			int port = Integer.parseInt(args[1]);
			createAIUser(hostname, port);
		}
	}

	/**
	 * Creates an {@code AIUser} which connects to the server with the specified hostname and port.
	 * @param hostname the hostname of the server the {@link AIUser} connects to.
	 * @param port the port of the server the {@link AIUser} connects to.
	 */
	private static void createAIUser(String hostname, int port) {
		// Create new lobby for the AIUser.
		Lobby aiLobby = new Lobby();

		// Create new AIUser.
		AIUser aiUser = new AIUser(aiLobby.getAllColors());

		// Create new ClientConnection for the AIUser.
		ClientConnection aiClient = new ClientConnection(hostname, port, aiUser, new MenuController());
		aiUser.setClient(aiClient);

		// Add user to Lobby.
		aiLobby.addUser(aiUser);

		// Create new ClientController for the AIUser.
		ClientController aiClientController = new ClientController(aiClient, aiLobby);
		aiClient.setClientController(aiClientController);

		// Start the AIUsers connection to the server.
		aiClient.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.titleProperty().bind(ApplicationInstance.getInstance().createStringBinding("WINDOW_TITLE"));
		primaryStage.setMinWidth(GeneralSettings.WINDOW_WIDTH);
		primaryStage.setMinHeight(GeneralSettings.WINDOW_HEIGHT);
		primaryStage.getIcons().add(new Image("/main/yakLogo.png"));

		new ApplicationController(primaryStage);

		primaryStage.show();
	}

}
