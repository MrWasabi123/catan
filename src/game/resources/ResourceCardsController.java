package game.resources;

import game.player.Player;
import interfaces.Controller;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import network.client.ClientConnection;
import sounds.AudioPlayer;

/**
 * The display of the resource cards.
 * @author Paula Wikidal
 */
public class ResourceCardsController implements Controller<Resources> {

	/**
	 * The {@code Resources} of the {@code Player}.
	 * @see Resources
	 * @see Player
	 */
	Resources resources;

	/**
	 * The width of a card.
	 */
	private final int CARD_WIDTH = 40;

	/**
	 * The distance between two resource cards of the same type that partly overlap.
	 */
	private int TRANSLATION_OVERLAPPING_CARDS = CARD_WIDTH/4;

	/**
	 * The maximum translation all cards of one type have in total.
	 */
	private int MAX_WIDTH = TRANSLATION_OVERLAPPING_CARDS*7;

	/**
	 * The file path to the brick card image.
	 */
	private final String BRICK_IMG_PATH = "game/resources/pictures/brickcard.png";

	/**
	 * The file path to the grain card image.
	 */
	private final String GRAIN_IMG_PATH = "game/resources/pictures/graincard.png";

	/**
	 * The file path to the ore card image.
	 */
	private final String ORE_IMG_PATH = "game/resources/pictures/orecard.png";

	/**
	 * The file path to the lumber card image.
	 */
	private final String LUMBER_IMG_PATH = "game/resources/pictures/lumbercard.png";

	/**
	 * The file path to the wool card image.
	 */
	private final String WOOL_IMG_PATH = "game/resources/pictures/woolcard.png";

	/**
	 * The AnchorPane, that contains the ImageViews of the brick resource cards.
	 */
	@FXML
	private AnchorPane brickCardsPane;

	/**
	 * The AnchorPane, that contains the ImageViews of the ore resource cards.
	 */
	@FXML
	private AnchorPane oreCardsPane;

	/**
	 * The AnchorPane, that contains the ImageViews of the grain resource cards.
	 */
	@FXML
	private AnchorPane grainCardsPane;

	/**
	 * The AnchorPane, that contains the ImageViews of the lumber resource cards.
	 */
	@FXML
	private AnchorPane lumberCardsPane;

	/**
	 * The AnchorPane, that contains the ImageViews of the wool resource cards.
	 */
	@FXML
	private AnchorPane woolCardsPane;

	/**
	 * The audio player.
	 */
	private AudioPlayer audioPlayer = new AudioPlayer();

	/**
	 * Updates the displayed resource cards of the player by binding them
	 * to the resources of the player.
	 */
	private void addBindings() {
		SimpleMapProperty<ResourceType, Integer> map = resources.getResources();
		map.addListener(new InvalidationListener()  {
			@Override
			public void invalidated(Observable observable) {
				updateCards(ResourceType.BRICK, BRICK_IMG_PATH, brickCardsPane);
				updateCards(ResourceType.ORE, ORE_IMG_PATH, oreCardsPane);
				updateCards(ResourceType.GRAIN, GRAIN_IMG_PATH, grainCardsPane);
				updateCards(ResourceType.LUMBER, LUMBER_IMG_PATH, lumberCardsPane);
				updateCards(ResourceType.WOOL, WOOL_IMG_PATH, woolCardsPane);
			}
		});
	}

	/**
	 * Adds or removes images from the specified AnchorPane depending on the quantity
	 * of the resource in the resources map.
	 * @param type The RessourceType of the Resource that is being updated.
	 * @param imagePath The file path to the image of the card.
	 * @param pane The pane the card is added to or removed from.
	 */
	private void updateCards(ResourceType type, String imagePath, AnchorPane pane) {
		audioPlayer.playCardSound();
		SimpleMapProperty<ResourceType, Integer> map = resources.getResources();
		if (map.get(type) < pane.getChildren().size()) {
			pane.getChildren().remove(map.get(type), pane.getChildren().size());
		} else if (map.get(type) > pane.getChildren().size()) {
			while(pane.getChildren().size() < map.get(type)) {
				ImageView cardView = addCard(imagePath, pane.getChildren());
				startAnimation(cardView);
			}
		}
		updateLayout(pane);
	}

	/**
	 * Adds an new card to the display of resource cards
	 * @param imagePath the path to the image of the card
	 * @param list the list of children the card is added to.
	 * @return the created ImageView
	 */
	private ImageView addCard(String imagePath, ObservableList<Node> list) {
		Image image = new Image(imagePath);
		ImageView cardView = new ImageView(image);
		cardView.setPreserveRatio(true);
		cardView.setFitWidth(CARD_WIDTH);
		AnchorPane.setBottomAnchor(cardView, 0.0);
		list.add(cardView);
		return cardView;
	}
	
	/**
	 * Starts the animation that adds a card.
	 * @param card the card to animate.
	 */
	private void startAnimation(ImageView card) {
		double scaleFactor = 1.4;
		ScaleTransition scale = new ScaleTransition(Duration.seconds(1.5), card);
		card.setScaleX(scaleFactor);
		card.setScaleY(scaleFactor);
		scale.setInterpolator(Interpolator.EASE_BOTH);
		scale.setToX(1);
		scale.setToY(1);
		scale.play();
	}

	/**
	 * Adjusts the distance between overlapping cards.
	 * @param pane the pane in which the layout is adjusted.
	 */
	private void updateLayout(Pane pane) {
		for (int i = pane.getChildren().size()-1; i>=0; i--) {
			Node node = pane.getChildren().get(i);
			if(pane.getChildren().size()>7) {
			AnchorPane.setLeftAnchor(node, (double)i*(MAX_WIDTH/pane.getChildren().size()));
			} else {
				AnchorPane.setLeftAnchor(node, (double) (i*TRANSLATION_OVERLAPPING_CARDS));
			}
		}
	}
	
	@Override
	public void doInitializations(Resources resources, ClientConnection client) {
		this.resources = resources;
		addBindings();
	}

}
