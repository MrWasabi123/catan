package network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import lobby.menu.MenuController;
import main.ApplicationInstance;
import network.server.Server;
import users.HumanUser;
import users.User;

/**
 * Represents a thread on the client-side which handles the communication with a server.
 * @author Christoph Hermann
 * @see Server
 */
public class ClientConnection extends Thread {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = LogManager.getLogger(ClientConnection.class.getName());

	/**
	 * A status flag which determines if the thread should continue running or stop running. The volatile keyword
	 * guarantees, that the correct value will be read at all times - even if a different thread changes it.
	 */
	private volatile boolean running;

	/**
	 * The socket of this client.
	 */
	private Socket socket;

	/**
	 * The writer used to send messages to the server.
	 */
	private OutputStreamWriter writer;

	/**
	 * The reader used to read messages from the server.
	 */
	private BufferedReader reader;

	/**
	 * The controller handling all messages received by this client.
	 */
	private ClientController clientController;

	/**
	 * The port of the server this client wants to connect to.
	 */
	private final int port;

	/**
	 * The host name of the server this client wants to connect to.
	 */
	private final String hostName;

	/**
	 * The {@code User} connected to this client.
	 */
	private final User user;

	/**
	 * The controller responsible for starting this client.
	 */
	private MenuController menuController;

	/**
	 * The bundle containing all error messages.
	 */
	private ResourceBundle errorMessages = ResourceBundle.getBundle("internationalization.MessagesBundle",
			ApplicationInstance.getInstance().getLocale());

	/**
	 * Creates a new ClientConnection trying to connect to a server.
	 * @param hostName the host name of the server.
	 * @param port the port of the server.
	 * @param user the {@link User} connected to this client.
	 * @param menuController the controller responsible for starting this client.
	 */
	public ClientConnection(String hostName, int port, User user, MenuController menuController) {
		this.hostName = hostName;
		this.port = port;
		this.user = user;
		this.menuController = menuController;
	}

	@Override
	public void run() {
		running= true;

		connect(hostName, port);
		checkRegularlyIfConnectionLost();
		listenForMessages();
	}

	/**
	 * Connects to a server and opens input and output streams.
	 * @param hostName the host name of the server.
	 * @param port the port of the server.
	 */
	private void connect(String hostName, int port) {
		try {
			// Create and configure socket.
			socket = new Socket(hostName, port);
			socket.setSoTimeout(1000);

			// Open streams.
			writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			// Log message.
			logger.info("Successfully connected to server.");
		} catch (NullPointerException | IOException | IllegalArgumentException exception) {
			// Display and log messages.
			if(user instanceof HumanUser) {
				clientController.getChatController().displayErrorMessage(errorMessages.getString("CONNECTION_FAILED"));
			}
			logger.error(exception.getMessage());

			stopRunning();
		}
	}

	/**
	 * Checks regularly if the connection to the server is still open. If not, this ClientConnection closes.
	 * @see ClientConnection
	 */
	private void checkRegularlyIfConnectionLost() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

		Runnable checkReaderOpen = () -> {
			try {
				reader.ready();
			} catch (IOException exception) {
				executor.shutdown();
				stopRunning();
			}
		};

		executor.scheduleAtFixedRate(checkReaderOpen, 0, 2, TimeUnit.SECONDS);
	}

	/**
	 * Waits for incoming messages and gives them to the controller to handle.
	 */
	private void listenForMessages() {
		while (running) {
			try {
				JSONObject message = new JSONObject();

				try {
					// Read incoming message.
					String line = reader.readLine();
					message = new JSONObject(line);
				} catch (SocketTimeoutException exception) {
					throw exception;
				} catch (IOException | NullPointerException exception) {
					// Log message.
					logger.error("Connection to server lost.");

					stopRunning();
				}

				if (running) {
					// Log the message.
					logger.info("Received: " + message.toString());

					// Handle the message.
					clientController.handle(message);
				}
			} catch (SocketTimeoutException exception) {
				// This exception is thrown regularly so that the while condition is checked regularly.
			}
		}

		closeConnection();
	}

	/**
	 * Closes the connection between this client and the server.
	 */
	private void closeConnection() {
		// Remove the reference to this client.
		menuController.clientProperty().set(null);

		closeReader();
		closeWriter();
		closeSocket();
	}

	/**
	 * Closes the reader.
	 */
	private void closeReader() {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException exception) {
			logger.error("Client reader could not be closed properly.");
		}
	}

	/**
	 * Closes the writer.
	 */
	private void closeWriter() {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException exception) {
			logger.error("Client writer could not be closed properly.");
		}
	}

	/**
	 * Closes the socket.
	 */
	private void closeSocket() {
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException exception) {
			logger.error("Client socket could not be closed properly.");
		}
	}

	/**
	 * Sends the specified {@code JSONObject} to the {@code Server}.
	 * @param type Type of the Message 
	 * @param jsonObject Content of the Message
	 * @see Server
	 */
	public synchronized void sendToServer(String type, JSONObject jsonObject) {
		// Complete the message.
		JSONObject completeMessage = new JSONObject();
		completeMessage.put(type, jsonObject);

		try {
			// Send the message.
			writer.write(completeMessage + "\n");
			writer.flush();

			logger.info("Send: " + completeMessage);
		} catch (NullPointerException | IOException exception) {
			logger.error("Writer could not write the message.");

			stopRunning();
		}
	}

	/**
	 * Tells this thread to stop running.
	 */
	public void stopRunning() {
		running = false;
	}

	/**
	 * Returns the controller responsible for handling all messages received by this client.
	 * @return the client Controller.
	 */
	public ClientController getClientController() {
		return clientController;
	}

	/**
	 * Sets the controller responsible for handling all messages received by this client.
	 * @param clientController the clientController.
	 */
	public void setClientController(ClientController clientController) {
		this.clientController = clientController;
	}

	/**
	 * Returns the {@code User} connected to this client.
	 * @return the {@link User}.
	 */
	public User getUser() {
		return user;
	}

}
