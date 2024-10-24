package interfaces;

import network.client.ClientConnection;
import network.server.Server;

/**
 * An interface specifying some functionality which is shared between most controllers in this application.
 * @author Paula Wikidal
 * @author Christoph Hermann
 * @param <Type> the type of Object the controller needs for initialization.
 */
public interface Controller<Type> {

	/**
	 * Method for initializing everything, that needs to be done right after 
	 * a FXML file and its controller have been loaded. 
	 * It provides the possibility to pass an Object to the controller, e.g.
	 * the model object the view represents.
	 * @param object the object to pass to the controller. 
	 * @param client the {@link ClientConnection} used to send messages to the {@link Server}.
	 */
	void doInitializations(Type object, ClientConnection client);
}
