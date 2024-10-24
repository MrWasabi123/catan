package game.board.hexes;


import java.util.HashMap;
import java.util.Map;

import game.board.Board;
import interfaces.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import network.client.ClientConnection;

/**
 * Represents the ViewModel responsible for controlling hexes. It also controls 
 * moving the robber and playing  the {@code DevelopmentCard} Knight.
 * @author Cornelia Sedlmeir-Hofmann
 * @author Christoph Hermann
 * @author Paula Wikidal
 * @see Hex
 */
public class HexController implements Controller<Hex> {

	/**
	 * The hex being controlled by this ViewModel.
	 */
	private Hex hex;

	/**
	 * The visual representation of the hex being controlled by this ViewModel.
	 */
	@FXML
	private ImageView hexView;

	/**
	 * The image of the token.
	 */
	@FXML
	private ImageView tokenView;

	/**
	 * The Pane containing the token
	 */
	@FXML
	private Pane token;

	/**
	 * The Label that displays the number on the token
	 */
	@FXML
	private Label tokenLabel;

	/**
	 * The markers on the {@code Hex} which indicate how likely it is to roll the number on the hex.
	 * @see Hex
	 */
	@FXML
	private HBox probabilityMarkers;

	/**
	 * The radius of probability markers.
	 */
	private static final double PROBABILITY_MARKER_RADIUS = 4;

	/**
	 * A map containing information about which type of hex uses which image.
	 * @see HexType
	 */
	private  Map<HexType, String> imageLocations = initializeImageLocations();

	/**
	 * Sets the position and size of the Hex.
	 */
	public void draw() {
		HexType hexType = hex.getType();
		String imageLocation = imageLocations.get(hexType);

		hexView.setImage(new Image(imageLocation));

		hexView.setFitWidth(Board.DISTANCE*2);

		double xPosition = hex.getxPosCartesianHex() - hexView.getBoundsInLocal().getWidth()/2;
		double yPosition = hex.getyPosCartesianHex() - hexView.getBoundsInLocal().getHeight()/2;

		hexView.setX(xPosition);
		hexView.setY(yPosition);
		if (hex.getToken().getNumber()!=0) {
			token.setVisible(true);
			token.setLayoutX(hex.getxPosCartesianHex() - token.getBoundsInLocal().getWidth()/2);
			token.setLayoutY(hex.getyPosCartesianHex() - token.getBoundsInLocal().getWidth()/2);
			tokenLabel.setText(String.valueOf(hex.getToken().getNumber()));
			addProbabilityMarkers(hex);
		}
	}

	/**
	 * Adds the probability markers to the specified {@code Hex}.
	 * @param hex the {@link Hex}.
	 */
	private void addProbabilityMarkers(Hex hex) {
		// Add probability markers.
		int nProbabilityMarkers = getNProbabilityMarkers(hex);
		Color probabilityMarkerColor = getProbabilityMarkersColor(hex);

		for (int i=0; i<nProbabilityMarkers; i++) {
			probabilityMarkers.getChildren().add(new Circle(PROBABILITY_MARKER_RADIUS, probabilityMarkerColor.desaturate()));
		}

		// relocate probability markers.
		double hexBaseXPosition = hex.getxPosCartesianHex();
		double hexBaseYPosition = hex.getyPosCartesianHex();
		double tokenRadius = token.getBoundsInLocal().getWidth()/2;
		double probabilityMarkersXOffset = hexBaseXPosition - (PROBABILITY_MARKER_RADIUS * nProbabilityMarkers);
		double probabilityMarkersYOffset = hexBaseYPosition + PROBABILITY_MARKER_RADIUS + tokenRadius;

		probabilityMarkers.relocate(probabilityMarkersXOffset, probabilityMarkersYOffset);
	}

	/**
	 * Returns the color of the probability markers for the specified {@code Hex}.
	 * @param hex the {@link Hex}.
	 * @return the color of the probability markers.
	 */
	private Color getProbabilityMarkersColor(Hex hex) {
		switch (hex.getToken().getNumber()) {
		case 6:
		case 8:
			return Color.RED;
		case 5:
		case 9:
			return Color.ORANGERED;
		case 4:
		case 10:
			return Color.ORANGE;
		case 3:
		case 11:
			return Color.YELLOW;
		case 2:
		case 12:
			return Color.WHITE;
		default:
			return null;
		}
	}

	/**
	 * Returns the number of probability markers for the specified {@code Hex}.
	 * @param hex the {@link Hex}.
	 * @return the number of probability markers.
	 */
	private int getNProbabilityMarkers(Hex hex) {
		switch (hex.getToken().getNumber()) {
		case 6:
		case 8:
			return 5;
		case 5:
		case 9:
			return 4;
		case 4:
		case 10:
			return 3;
		case 3:
		case 11:
			return 2;
		case 2:
		case 12:
			return 1;
		default:
			return 0;
		}
	}

	/**
	 * Initializes a map containing information about which type of hex uses which image.
	 * @return the map.
	 * @see Hex
	 * @see HexType
	 */
	private Map<HexType, String> initializeImageLocations() {
		Map<HexType, String> imageLocations = new HashMap<>();

		imageLocations.put(HexType.DESERT, "/game/board/hexes/images/desert.png");
		imageLocations.put(HexType.FIELDS, "/game/board/hexes/images/fields.png");
		imageLocations.put(HexType.FOREST, "/game/board/hexes/images/forest.png");
		imageLocations.put(HexType.HILLS, "/game/board/hexes/images/hills.png");
		imageLocations.put(HexType.MOUNTAIN, "/game/board/hexes/images/mountain.png");
		imageLocations.put(HexType.PASTURE, "/game/board/hexes/images/pasture.png");
		imageLocations.put(HexType.WATER, "/game/board/hexes/images/water.png");

		return imageLocations;
	}

	/**
	 * Returns the {@code Hex} being controlled by this ViewModel.
	 * @return the {@link Hex}
	 */
	public Hex getHex() {
		return hex;
	}

	@Override
	public void doInitializations(Hex hex, ClientConnection client) {
		this.hex = hex;
		draw();
	}

}
