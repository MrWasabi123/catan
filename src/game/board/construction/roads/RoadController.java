package game.board.construction.roads;

import game.Animations;
import game.board.Board;
import game.board.construction.localities.City;
import interfaces.Controller;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import network.client.ClientConnection;

/**
 * Represents the Controller responsible for controlling a {@code Road}.
 * @author Paula Wikidal
 * @author Christoph Hermann
 * @see Road
 */
public class RoadController implements Controller<Road> {

	/**
	 * The {@code Road} being controlled by this controller.
	 * @see City
	 */
	private Road road;

	/**
	 * The visualization of the road.
	 */
	@FXML
	private Rectangle roadView;

	/**
	 * Draws the road by setting its color, position and rotation.
	 */
	private void draw() {
		roadView.setHeight(Board.RADIUS*0.9);

		roadView.xProperty().set(road.getPosition().getxPosCartesianEdge()
				- roadView.getBoundsInLocal().getWidth()/2);
		roadView.yProperty().set(road.getPosition().getyPosCartesianEdge()
				- roadView.getBoundsInLocal().getHeight()/2);

		double deltaX = road.getPosition().getAdjacentCornersOfEdge().get(0).getxPosCartesianCorner() 
				- road.getPosition().getAdjacentCornersOfEdge().get(1).getxPosCartesianCorner();
		double deltaY = road.getPosition().getAdjacentCornersOfEdge().get(0).getyPosCartesianCorner() 
				- road.getPosition().getAdjacentCornersOfEdge().get(1).getyPosCartesianCorner();
		roadView.setRotate(Math.toDegrees(Math.atan(deltaX/-deltaY)));

		roadView.setFill(road.getOwner().getColor());
		
		Animations.playFadeTransition(roadView);
	}

	@Override
	public void doInitializations(Road road, ClientConnection client) {
		this.road = road;
		draw();
	}

}
