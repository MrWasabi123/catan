package game.player;

import java.util.HashMap;

import game.resources.ResourceType;
import game.resources.Resources;
import interfaces.Controller;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleMapProperty;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import network.client.ClientConnection;

/**
 * The controller for the resource cards the player just lost or received.
 */
public class LastReceivedAndLostResourcesController implements Controller<Player>{

	/** The width of a card*/
	private final int CARD_HEIGHT = 35;

	/** The spacing between cards of different types*/
	private final int SPACING = 8;

	/**
	 * The distance between two resource cards of the same type that partly overlap.
	 */
	private double TRANSLATION_OVERLAPPING_CARDS = 7.0;

	/** The map that maps the ResourceType to the styleClass*/
	private HashMap<ResourceType, String> styleMap = createStyleClassMap();

	/** The player who's cards are displayed*/
	private Player player;

	/**The pane containing the received cards*/
	@FXML private Pane receivedCardsPane;

	/**The pane containing the lost cards*/
	@FXML private Pane lostCardsPane;

	/**
	 * Updates the received cards.
	 */
	private void bindReceivedResources() {
		player.getLastReceivedResources().getResources().addListener((InvalidationListener) o -> {
			HBox resourcesBox = createResources(player.getLastReceivedResources());
			receivedCardsPane.getChildren().add(resourcesBox);
			animateReceivedCards(resourcesBox);
		});
	}

	/**
	 * Updates the lost cards.
	 */
	private void bindLostResources() {
		player.getLastLostResources().getResources().addListener((InvalidationListener) o -> {
			HBox resourcesBox = createResources(player.getLastLostResources());
			lostCardsPane.getChildren().add(resourcesBox);
			animateLostCards(resourcesBox);
		});
	}

	/**
	 * Creates a HBox containing the images representing the given resources.
	 * @param resources the resources that are displayed.
	 * @return the HBox containing all images.
	 */
	private HBox createResources(Resources resources) {
		HBox hbox = new HBox();
		hbox.setSpacing(SPACING);
		SimpleMapProperty<ResourceType, Integer> map = resources.getResources();
		for(ResourceType type : map.keySet()) {
			if(map.get(type) != 0) {
				AnchorPane  pane = new AnchorPane();
				while(pane.getChildren().size() < map.get(type)) {
					addCard(type, pane);
				}
				hbox.getChildren().add(pane);
			}
		}
		return hbox;
	}

	/**
	 * Adds an ImageView of a card to an AnchorPane.
	 * @param type the ResourceType of the card that is added
	 * @param anchorPane the pane the card is added to
	 */
	private void addCard(ResourceType type, AnchorPane anchorPane) {
		ImageView cardView = new ImageView();
		cardView.setPreserveRatio(true);
		cardView.setFitHeight(CARD_HEIGHT);
		cardView.getStyleClass().add(styleMap.get(type));
		AnchorPane.setBottomAnchor(cardView, 0.0);
		AnchorPane.setLeftAnchor(cardView, TRANSLATION_OVERLAPPING_CARDS*anchorPane.getChildren().size());
		anchorPane.getChildren().add(0, cardView);
	}

	/** 
	 * The animation of received cards
	 * @param hbox the Box of cards that is animated
	 */
	private void animateReceivedCards(HBox hbox) {
		TranslateTransition translate =
				new TranslateTransition(Duration.seconds(1.5),hbox);
		translate.setFromX(50);
		translate.setToX(0);
		translate.setCycleCount(1);
		translate.play();

		FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), hbox);
		fadeIn.setCycleCount(1);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.play();

		FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), hbox);
		fadeOut.setCycleCount(1);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setDelay(Duration.seconds(3));
		fadeOut.play();
		fadeOut.setOnFinished((event) -> {
			((Pane)hbox.getParent()).getChildren().remove(hbox);
		});
	}

	/** 
	 * The animation of lost cards
	 * @param hbox the Box of cards that is animated
	 */
	private void animateLostCards(HBox hbox) {
		FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.1), hbox);
		fadeIn.setCycleCount(1);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.play();

		TranslateTransition translate =
				new TranslateTransition(Duration.seconds(1.5), hbox);
		translate.setFromX(0);
		translate.setToX(50);
		translate.setCycleCount(1);
		translate.setDelay(Duration.seconds(0.5));
		translate.setInterpolator(Interpolator.EASE_IN);
		translate.play();

		FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), hbox);
		fadeOut.setCycleCount(1);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setDelay(Duration.seconds(0.5));
		fadeOut.setInterpolator(Interpolator.EASE_IN);
		fadeOut.play();

		fadeOut.setOnFinished((event) -> {
			((Pane)hbox.getParent()).getChildren().remove(hbox);
		});

	};

	/**
	 * Maps ResourceTypes to StyleClasses.
	 * @return the map
	 */
	private HashMap<ResourceType, String> createStyleClassMap() {
		HashMap<ResourceType, String> map = new HashMap<ResourceType, String>();
		map.put(ResourceType.BRICK, "brick");
		map.put(ResourceType.GRAIN, "grain");
		map.put(ResourceType.LUMBER, "lumber");
		map.put(ResourceType.ORE, "ore");
		map.put(ResourceType.WOOL, "wool");
		return map;
	}

	@Override
	public void doInitializations(Player player, ClientConnection client) {
		this.player = player;
		bindReceivedResources();
		bindLostResources();
	};

}
