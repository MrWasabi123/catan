package game.board.harbors;

import java.util.HashMap;
import java.util.Map;

import game.board.Board;
import game.board.edges.Edge;
import interfaces.Controller;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import network.client.ClientConnection;

/**
 * Represents the controller of the edge
 * @author svenja
 * @author Christoph Hermann
 * @author Paula Wikidal
 * @see Edge
 */
public class HarborController implements Controller<Harbor> {

	/**
	 * The {@code Harbor} being controlled by this controller.
	 * @see Harbor
	 */
	private Harbor harbor;

	/**
	 * The ImageView containing the image of the harbor.
	 */
	@FXML
	private ImageView harborView;

	/**
	 * A map containing information about which type of {@code Harbor} uses which image.
	 * @see Harbor
	 * @see HarborType
	 */
	private Map<HarborType, String> imageLocations = initializeImageLocations();

	/**
	 * Draws the {@code Harbor} being controlled by this controller.
	 * @see Harbor
	 */
	private void draw() {
			// Set the image.
			harborView.setImage(new Image (imageLocations.get(harbor.getType())));
			harborView.setFitHeight(Board.RADIUS*0.9);
			
			// Set the position
			harborView.setX(harbor.getPosition().getxPosCartesianEdge());
			harborView.setY(harbor.getPosition().getyPosCartesianEdge() - harborView.getBoundsInLocal().getHeight()/2);

			// Rotate the image.
			Rotate rotation = new Rotate();
			rotation.setPivotX(harborView.getX());
			rotation.setPivotY(harborView.getY()+(harborView.getFitHeight()/2));
			int index = harbor.getPosition().getEdgePosition().ordinal()+5;
			rotation.setAngle(index*60);	
			harborView.getTransforms().add(rotation);
	}

	/**
	 * Initializes a map containing information about which type of harbor uses which image.
	 * @return the map.
	 * @see Harbor
	 * @see HarborType
	 */
	private Map<HarborType, String> initializeImageLocations() {
		Map<HarborType, String> imageLocations = new HashMap<>();

		imageLocations.put(HarborType.UNIVERSAL, "game/board/harbors/images/Universal.png");
		imageLocations.put(HarborType.BRICK, "game/board/harbors/images/Brick.png");
		imageLocations.put(HarborType.GRAIN, "game/board/harbors/images/Grain.png");
		imageLocations.put(HarborType.LUMBER, "game/board/harbors/images/Lumber.png");
		imageLocations.put(HarborType.ORE, "game/board/harbors/images/Ore.png");
		imageLocations.put(HarborType.WOOL, "game/board/harbors/images/Wool.png");

		return imageLocations;
	}

	@Override
	public void doInitializations(Harbor harbor, ClientConnection client) {
		this.harbor = harbor;
		draw();
	}

}
