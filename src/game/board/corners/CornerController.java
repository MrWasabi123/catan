package game.board.corners;

import org.json.JSONArray;
import org.json.JSONObject;

import game.board.construction.localities.Settlement;
import interfaces.Controller;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;
import network.server.Server;

/**
 * Represents the controller responsible for controlling corners.
 * @author Cornelia Sedlmeir-Hofmann
 * @author Christoph Hermann
 * @see Corner
 */
public class CornerController implements Controller<Corner> {

	/**
	 * The {@link Corner} being controlled by this controller.
	 * @see Corner
	 */
	private Corner corner;

	/**
	 * The client this controller uses to send messages to the {@code Server}
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client; 

	/**
	 * The GUI-element representing the {@link Corner} controlled by this controller.
	 * @see Corner
	 */
	@FXML
	private Circle cornerView;

	/**
	 * Requests to build a {@code Settlement} to the {@code Corner} controlled by this controller.
	 * @see Settlement
	 * @see Corner
	 */
	@FXML
	public void select() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(Attributes.TYPE.toString(), Attributes.SETTLEMENT.toString());
//		jsonObj.put(Attributes.POSITION.toString(), corner.getCornerLocality());
		JSONArray array = new JSONArray();
		for (int i = 0; i < 3; i++) {
			JSONObject positionHex = new JSONObject();
			positionHex.put(Attributes.X.toString(),
					corner.getAdjacentHexesOfCorner().get(i).getxPosAxialHex());
			positionHex.put(Attributes.Y.toString(),
					corner.getAdjacentHexesOfCorner().get(i).getyPosAxialHex());

			array.put(positionHex);
		}
		
		jsonObj.put(Attributes.POSITION.toString(), array);
		client.sendToServer(ServerTypes.BUILD.toString(), jsonObj);
	}

	/**
	 * Draws the {@code Corner} being controlled by this controller.
	 * @see Corner
	 */
	private void draw() {
		double xCorner = corner.getxPosCartesianCorner();
		double yCorner = corner.getyPosCartesianCorner();

		cornerView.setLayoutX(xCorner);
		cornerView.setLayoutY(yCorner);
	}


	public void doInitializations(Corner corner, ClientConnection client) {
		this.corner = corner;
		this.client = client;
		draw();
	}

}
