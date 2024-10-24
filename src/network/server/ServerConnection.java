package network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import main.ApplicationInstance;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ClientTypes;
import users.User;

/**
 * Represents a thread on the server-side which handles the communication with a single client.
 * @author Christoph Hermann
 * @see ClientConnection
 */
public class ServerConnection extends Thread {

	/**
	 * The id of the {@code User} associated with this connection.
	 */
	private int userId;

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = LogManager.getLogger(ServerConnection.class.getName());

	/**
	 * A status flag which determines if the thread should continue running or
	 * stop running. The volatile keyword guarantees, that the correct value
	 * will be read at all times even, if a different thread changes it.
	 */
	private volatile boolean running = true;

	/**
	 * The socket of the client this thread is communicating with.
	 */
	private Socket socket;

	/**
	 * The writer used to send messages to the client.
	 */
	private OutputStreamWriter writer;

	/**
	 * The reader used to read messages from the client.
	 */
	private BufferedReader reader;

	/**
	 * The controller responsible for handling all messages received from the client.
	 */
	private ServerController serverController;

	/**
	 * A List of all threads handling the communication with the clients which are connected to this server.
	 */
	private List<ServerConnection> clients;

	/**
	 * Whether the {@code Server} is full or not.
	 * @see Server
	 */
	private boolean serverFull;

	/**
	 * The bundle containing all error messages.
	 */
	private ResourceBundle errorMessages = ResourceBundle.getBundle("internationalization.MessagesBundle",
			ApplicationInstance.getInstance().getLocale());

	/**
	 * Creates a new server thread communicating with a user.
	 * @param socket the socket on the user's end.
	 * @param clients a list of all ServerThreads.
	 * @param serverController the {@link ServerController} handling all messages received from the client.
	 */
	public ServerConnection(Socket socket, List<ServerConnection> clients, ServerController serverController) {
		this.socket = socket;
		this.clients = clients;
		this.serverController = serverController;
	}

	@Override
	public void run() {
		if (couldStreamsBeOpenedSuccessfully()) {
			if (serverController.hasGameStarted()) {
				sendGameStartedMessage();
				stopRunning();
			} else if (serverFull) {
				sendServerFullMessage();
				stopRunning();
			} else {
				checkRegularlyIfConnectionLost();
				sendProtocolInformation();
				listenForMessages();
			}
		}

		closeConnection();
	}

	/**
	 * Sends a message to the client saying that the game has already started.
	 * @see ClientConnection
	 */
	private void sendGameStartedMessage() {
		JSONObject message = new JSONObject();
		message.put(ClientTypes.SERVER_REPLY.toString(), errorMessages.getString("GAME_ALREADY_STARTED"));
		sendToClient(message);
	}

	/**
	 * Sends a message to the client saying that the {@code Server} is already full.
	 * @see ClientConnection
	 * @see Server
	 */
	private void sendServerFullMessage() {
		JSONObject message = new JSONObject();
		message.put(ClientTypes.SERVER_REPLY.toString(), errorMessages.getString("SERVER_FULL"));
		sendToClient(message);
	}

	/**
	 * Checks regularly if the connection to the client is still open. If not, this ServerConnection closes.
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
	 * Opens the input and output streams to the client.
	 * @return true, if the streams could successfully be opened. false, otherwise.
	 */
	private boolean couldStreamsBeOpenedSuccessfully() {
		try	{
			writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			return true;
		} catch (IOException exception) {
			serverController.getChatController().displayErrorMessage(errorMessages.getString("CONNECTION_FAILED"));
			return false;
		}
	}

	/**
	 * Sends the first message containing the protocol information to the CLient to start the communication.
	 */
	private void sendProtocolInformation() {
		JSONObject object = new JSONObject();
		JSONObject message = new JSONObject();
		message.put(Attributes.VERSION.toString(), Attributes.VERSION_VALUE.toString());
		message.put(Attributes.PROTOCOL.toString(), Attributes.PROTOCOL_VALUE.toString());
		object.put(ClientTypes.HELLO.toString(), message);

		sendToClient(object);
	}

	/**
	 * Waits for incoming messages and gives them to the controller to handle.
	 * @see ServerController
	 */
	private void listenForMessages() {
		JSONObject message = new JSONObject();

		while (running) {
			try {
				try {
					// Read incoming message.
					String line = reader.readLine();
					message = new JSONObject(line);
				} catch (SocketTimeoutException exception) {
					throw exception;
				} catch (IOException | NullPointerException exception) {
					logger.error("Connection to client " + userId + " lost.");
					stopRunning();
				}

				if (running) {
					// Log the message.
					logger.info("Received: " + message.toString());

					// Handle the message.
					serverController.handle(message, this);
				}
			} catch (SocketTimeoutException exception) {
				// This exception is thrown regularly so that the while condition is checked regularly.
			}
		}
	}

	/**
	 * Closes the connection between the server and the client.
	 */
	private synchronized void closeConnection() {
		clients.remove(this);

		if (!serverFull) {
			serverController.removeUserAndOrPlayer(this);
		}

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
			logger.error("Server reader could not be closed properly.");
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
			logger.error("Server writer could not be closed properly.");
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
			logger.error("Server socket could not be closed properly.");
		}
	}

	/**
	 * Sends a message to the client.
	 * @param jsonObject the {@link JSONObject} to send.
	 */
	public void sendToClient(JSONObject jsonObject) {
		try {
			writer.write(jsonObject.toString() + "\n");
			writer.flush();
			logger.info("Send: " + jsonObject.toString());
		} catch (IOException | NullPointerException exception) {
			logger.error("Failed to send the message.");
		}
	}

	/**
	 * Sends a message to the client.
	 * @param type the type of the message.
	 * @param jsonObject the {@link JSONObject} to send.
	 */
	public void sendToClient(String type, JSONObject jsonObject) {
		JSONObject completeMessage = new JSONObject();
		completeMessage.put(type, jsonObject);
		try {
			writer.write(completeMessage.toString() + "\n");
			writer.flush();
			logger.info("Send: " + completeMessage);
		} catch (IOException | NullPointerException exception) {
			logger.error("Failed to send the message.");
		}
	}

	/**
	 * Sends a message to every client but the one that this thread is communicating with.
	 * @param type the type of the message.
	 * @param jsonObject the {@link JSONObject} to send.
	 */
	public synchronized void sendToEverybodyElse(String type, JSONObject jsonObject) {
		JSONObject completeMessage = new JSONObject();
		completeMessage.put(type, jsonObject);

		for (int i=0; i<clients.size(); i++) {
			if (!clients.get(i).equals(this)) {
				clients.get(i).sendToClient(completeMessage);
			}
		}
	}

	/**
	 * Sends a message to one client but the one that this thread can be communicating with or not.
	 * @param type the type of the message.
	 * @param jsonObject the {@link JSONObject} to send.
	 * @param id the ClientId of whom receives this message.
	 */
	public synchronized void sendToSomeone(String type, JSONObject jsonObject, int id) {
		JSONObject completeMessage = new JSONObject();
		completeMessage.put(type, jsonObject);
		for(int i=0; i<clients.size(); i++) {
			if(clients.get(i).getUserId()==id) {
				clients.get(i).sendToClient(completeMessage);
				break;
			}
		}
	}

	/**
	 * Sends a message to all other client but the one that this thread can be communicating with or not.
	 * @param type the type of the message.
	 * @param jsonObject the {@link JSONObject} to send.
	 * @param id the ClientId of whom not receives this message.
	 */
	public synchronized void sendToSomeoneElse(String type, JSONObject jsonObject, int id) {
		JSONObject completeMessage = new JSONObject();
		completeMessage.put(type, jsonObject);

		for (int i=0; i<clients.size(); i++) {
			if (clients.get(i).getUserId()!=id) {
				clients.get(i).sendToClient(completeMessage);
			}
		}
	}

	/**
	 * Sends a message to every client that is connected to the server.
	 * @param message the {@link JSONObject} to broadcast.
	 */
	public synchronized void broadcast(JSONObject message) {
		for (int i=0; i<clients.size(); i++) {
			clients.get(i).sendToClient(message);
		}
	}

	/**
	 * Sends a message to every client that is connected to the server.
	 * @param type the type of the message.
	 * @param jsonObject the {@link JSONObject} to broadcast.
	 */
	public synchronized void broadcast(String type, JSONObject jsonObject) {
		JSONObject completeMessage = new JSONObject();
		completeMessage.put(type, jsonObject);

		for (int i=0; i<clients.size(); i++) {
			clients.get(i).sendToClient(completeMessage);
		}
	}

	/**
	 * Tells this thread to stop running.
	 */
	public void stopRunning() {
		running = false;
	}

	/**
	 * Returns the id of the {@code User} associated with this connection.
	 * @return the id of the {@link User}.
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Sets the id of the {@code User} associated with this connection.
	 * @param userId the id of the {@link User}.
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Tells this serverConnection, that the {@code Server} is full.
	 * @see Server
	 */
	public void setServerFull() {
		serverFull = true;
	}

}
