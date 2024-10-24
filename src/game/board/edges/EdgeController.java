package game.board.edges;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import game.board.Board;
import game.board.construction.roads.Road;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.player.Player;
import interfaces.Controller;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;
import network.server.Server;

/**
 * Represents the controller of the edge
 * @author svenja
 * @author Christoph Hermann
 * @author Paula Wikidal
 * @see Edge
 */
public class EdgeController implements Controller<Edge> {

	/**
	 * The {@link Edge} being controlled by this controller.
	 * @see Edge
	 */
	private Edge edge;

	/**
	 * The client this controller uses to send messages to the {@code Server}
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client;

	/**
	 * The Rectangle representing the edge.
	 */
	@FXML
	private Rectangle edgeView;

	/**
	 * Requests to build a {@code Road} to the {@code Edge} controlled by this Controller.
	 * @see Road
	 * @see Edge
	 */
	@FXML
	public void select() {
		if(client.getUser().getPlayer().currentySelectedDevelopmentCardProperty().get()!=null) {
			if (client.getUser().getPlayer().currentySelectedDevelopmentCardProperty().get()
					.equals(PlayableDevelopmentCardType.ROAD_BUILDING)) {
				List<Edge> lastTwoClickedOnEdges = client.getUser().getPlayer().getLastTwoClickedOnEdges();
				if(lastTwoClickedOnEdges.size()==2) {
					if(lastTwoClickedOnEdges.get(0).hasRoadInAdjacentEdges(client.getUser().getPlayer())) {
						lastTwoClickedOnEdges.remove(1);
					} else {
						lastTwoClickedOnEdges.remove(0);	
					}
				}
				lastTwoClickedOnEdges.add(0,edge);
			}
		} else {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(Attributes.TYPE.toString(), Attributes.ROAD.toString());
			JSONArray array = new JSONArray();
			for (int i = 0; i < 2; i++) {
				JSONObject positionHex = new JSONObject();
				positionHex.put(Attributes.X.toString(),
						edge.getAdjacentHexesOfEdge().get(i).getxPosAxialHex());
				positionHex.put(Attributes.Y.toString(),
						edge.getAdjacentHexesOfEdge().get(i).getyPosAxialHex());

				array.put(positionHex);
			}
			
			jsonObj.put(Attributes.POSITION.toString(), array);
			client.sendToServer(ServerTypes.BUILD.toString(), jsonObj);
		}
	}


	/**
	 * Draws the {@code Edge} being controlled by this controller.
	 * @see Edge
	 */
	private void draw() {
		edgeView.setHeight(Board.RADIUS*0.8);
		edgeView.setX(edge.getxPosCartesianEdge() - edgeView.getBoundsInLocal().getWidth()/2);
		edgeView.setY(edge.getyPosCartesianEdge() - edgeView.getBoundsInLocal().getHeight()/2);
		int indexOfPosition = edge.getEdgePosition().ordinal()+5;
		edgeView.setRotate(indexOfPosition*60);

		DropShadow highlightEffect = new DropShadow();
		highlightEffect.setColor(Color.BLUE);
		Player player = client.getUser().getPlayer();
		edgeView.effectProperty().bind(Bindings.createObjectBinding(() -> {
			if(player.currentySelectedDevelopmentCardProperty().get()!=null &&
					player.currentySelectedDevelopmentCardProperty().get()
					.equals(PlayableDevelopmentCardType.ROAD_BUILDING)
					&& player.getLastTwoClickedOnEdges().contains(edge)) {
				return highlightEffect;
			} else {
				return null;
			}
		}, player.lastTwoClickedOnEdgesProperty()));
	}

	@Override
	public void doInitializations(Edge edge, ClientConnection client) {
		this.edge = edge;
		this.client = client;
		draw();
	}

}
