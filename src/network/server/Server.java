package network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lobby.menu.MenuController;
import main.ApplicationInstance;

/**
 * Represents a server.
 * @author Christoph Hermann
 */
public class Server extends Thread {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = LogManager.getLogger(Server.class.getName());

	/**
	 * The amount if time in milliseconds that the {@code ServerSocket}'s accept()-method will block before throwing a
	 * {@code SocketTimeoutException}.
	 * @see ServerSocket
	 * @see SocketTimeoutException
	 */
	private static final int SERVER_SOCKET_TIMEOUT = 1000;

	/**
	 * A status flag which determines if the thread should continue running or stop running. The volatile keyword
	 * guarantees, that the correct value will be read at all times, even if a different thread changes it.
	 */
	private volatile boolean running = true;

	/**
	 * The socket of this server.
	 */
	private ServerSocket serverSocket;

	/**
	 * The port number of this server.
	 */
	private final int port;

	/**
	 * A List of all threads handling the communication with the clients which are connected to this server.
	 */
	private List<ServerConnection> clients = new ArrayList<>();

	/**
	 * The maximum numbers of clients that can be connected to this server at the same time.
	 */
	private static final int MAX_N_CLIENTS = 4;
	
	/**
	 * The controller responsible for starting this server.
	 */
	private MenuController menuController;

	/**
	 * The controller responsible for handling all messages received from clients.
	 */
	private ServerController serverController;

	/**
	 * The bundle containing all error messages.
	 */
	private ResourceBundle errorMessages = ResourceBundle.getBundle("internationalization.MessagesBundle",
			ApplicationInstance.getInstance().getLocale());

	/**
	 * Creates a new Server using the specified port.
	 * @param port the port.
	 * @param menuController the controller responsible for starting this server.
	 */
	public Server(int port, MenuController menuController) {
		this.port = port;
		this.menuController = menuController;
	}

	@Override
	public void run() {
		openConnection(port);
		listenForClientConnectionRequests();

		closeConnection();
	}

	/**
	 * Opens a socket with a specified port that clients can connect to.
	 * @param port the port.
	 */
	private void openConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);
		} catch (IllegalArgumentException | NullPointerException | IOException excepton) {
			stopRunning();

			logger.error(excepton.getMessage());
			serverController.getChatController().displayErrorMessage(errorMessages.getString("SERVER_START_FAILED"));
		}
	}

	/**
	 * Listens for incoming connection requests and starts a new {@code ServerConnection} for each of them.
	 */
	private void listenForClientConnectionRequests() {
		while (running) {
			try {
				Socket clientSocket = serverSocket.accept();
				clientSocket.setSoTimeout(1000);

				/* 
				 * If the server was told to shut down while it was waiting for connections, it will break here when the
				 * next connection request comes in.
				 */ 
				if(!running) {
					break;
				}

				startNewServerConnection(clientSocket);
			} catch (SocketTimeoutException exception) {
				// This exception is thrown regularly so that the while condition is checked regularly.
			} catch (IOException exception) {
				logger.error("Error while listening for incoming connections.");
			}
		}
	}

	/**
	 * Starts a new {@code ServerConnection} thread for the specified client socket.
	 * @param clientSocket the client socket.
	 * @see ServerConnection
	 */
	private void startNewServerConnection(Socket clientSocket) {
		ServerConnection newClient = new ServerConnection(clientSocket, clients, serverController);

		if (clients.size() < MAX_N_CLIENTS) {
			clients.add(newClient);
		} else {
			newClient.setServerFull();
		}

		newClient.start();
	}

	/**
	 * Closes all connections from the server to the clients.
	 */
	private void closeConnection() {
		try {
			// If the server stops running, then all ServerConnections must stop, too.
			for (ServerConnection client : clients) {
				client.stopRunning();
			}

			// Remove the reference to this server.
			menuController.serverProperty().set(null);

			// Close the socket
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException exception) {
			logger.error("Failed to terminate the server properly.");
		} finally {
			running = false;
		}
	}

	/**
	 * Tells this thread to stop running.
	 */
	public void stopRunning() {
		running = false;
	}

	/**
	 * Sets the controller responsible for handling all messages received from clients.
	 * @param serverController the server controller to set.
	 */
	public void setServerController(ServerController serverController) {
		this.serverController = serverController;
	}

	/**
	 * Returns the port of this server.
	 * @return the port.
	 */
	public int getPort() {
		return port;
	}

}
