package game.cards;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import game.board.edges.Edge;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.cards.victorypointcards.VictoryPointCard;
import game.player.Player;
import game.player.PlayerState;
import game.resources.ResourceType;
import game.resources.Resources;
import interfaces.Controller;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import main.ApplicationInstance;
import javafx.scene.Node;
import network.Mapper;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;

/**
 * Displays the developments cards and their infos and updates them when the
 * cards of the player change.
 * 
 * @author Paula Wikidal
 *
 */
public class DevelopmentCardsController implements Controller<SimpleListProperty<DevelopmentCard>> {

	/** The player associated with this client */
	private Player player;

	/** The Pane that contains the infos of each card */
	@FXML
	private Pane infoPane;

	/** The Pane that contains the menus of each card */
	@FXML
	private Pane menuPane;

	/**
	 * The Pane containing the card the player has selected while playing a Year of
	 * Plenty card.
	 */
	@FXML
	private Pane selectedResourceCardsPane;

	/** The Pane that contains the Year of Plenty cards */
	@FXML
	private AnchorPane yearOfPlentyPane;

	/** Label that contains the info text to the Year of Plenty card */
	@FXML
	private Label yearOfPlentyInfo;

	/**
	 * The Pane that contains the menu where the player can select two resource
	 * cards from the bank
	 */
	@FXML
	private Pane yearOfPlentyMenu;
	
	/** The Label containing the text on how to play a year of plenty card*/
	@FXML 
	private Label yearOfPlentyMenuLabel;

	/** The Pane that contains the Road Building cards */
	@FXML
	private AnchorPane roadBuildingPane;

	/** Label that contains the info text to the Road Building card */
	@FXML
	private Label roadBuildingInfo;

	/** The Pane that tells the player how to play this development card */
	@FXML
	private Pane roadBuildingMenu;
	
	/** The Label containing the text on how to play a road building card*/
	@FXML
	private Label roadBuildingMenuLabel;

	/** The Pane that contains the Monopoly cards */
	@FXML
	private AnchorPane monopolyPane;

	/** Label that contains the info text to the Monopoly card */
	@FXML
	private Label monopolyInfo;

	/**
	 * The Pane that contains the menu where the player can select a resource type
	 */
	@FXML
	private Pane monopolyMenu;
	
	/** The Label containing the text on how to play a monopoly card*/
	@FXML
	private Label monopolyMenuLabel;

	/** The Pane that contains the Knight cards */
	@FXML
	private AnchorPane knightsPane;

	/** Label that contains the info text to the Knight card */
	@FXML
	private Label knightInfo;

	/** The Pane that tells the player how to play this development card */
	@FXML
	private Pane knightMenu;
	
	/** The Label containing the text on how to play a knight card*/
	@FXML
	private Label knightMenuLabel;

	/** The Pane that contains the Victory Point cards */
	@FXML
	private AnchorPane victoryPointsPane;

	/**
	 * The property of the list that contains the development cards.
	 */
	private SimpleListProperty<DevelopmentCard> developmentCardsProperty;

	/**
	 * The client connection to communicate with the server.
	 */
	private ClientConnection client;

	/**
	 * The width of a card.
	 */
	private final int CARD_WIDTH = 40;

	/**
	 * The distance between two resource cards of the same type that partly overlap.
	 */
	private final int TRANSLATION_OVERLAPPING_CARDS = 10;

	/**
	 * The maximum translation all cards of one type have in total.
	 */
	private final int MAX_WIDTH = TRANSLATION_OVERLAPPING_CARDS * 2;

	/** The file path to the Year of Plenty card */
	private final String YEAR_OF_PLENTY_IMG_PATH = "game/cards/images/yearOfPlenty.png";

	/** The file path to the Road Building card */
	private final String ROAD_BUILDING_IMG_PATH = "game/cards/images/roadBuilding.png";

	/** The file path to the Monopoly card */
	private final String MONOPOLY_IMG_PATH = "game/cards/images/monopoly.png";

	/** The file path to the Knight card */
	private final String KNIGHT_IMG_PATH = "game/cards/images/knight.png";

	/** The file path to the Victory Point card */
	private final String VICTORY_POINT_IMG_PATH = "game/cards/images/victoryPoint.png";

	/**
	 * Makes the card panes show their info when the mouse hovers over them.
	 */
	@FXML
	private void initialize() {
		bindLanguage();
		reactToHover(knightsPane, knightInfo, knightMenu);
		reactToHover(yearOfPlentyPane, yearOfPlentyInfo, yearOfPlentyMenu);
		reactToHover(monopolyPane, monopolyInfo, monopolyMenu);
		reactToHover(roadBuildingPane, roadBuildingInfo, roadBuildingMenu);
	}
	
	/**
	 * Binds the text on the labels to the language selected in the settings.
	 */
	private void bindLanguage() {
		knightInfo.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("KNIGHT_INFO"));
		yearOfPlentyInfo.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("YEAR_OF_PLENTY_INFO"));
		monopolyInfo.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("MONOPOLY_INFO"));
		roadBuildingInfo.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ROADBUILDING_INFO"));
		
		knightMenuLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("KNIGHT_MENU"));
		yearOfPlentyMenuLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("YEAR_OF_PLENTY_MENU"));
		monopolyMenuLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("MONOPOLY_MENU"));
		roadBuildingMenuLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("ROADBUILDING_MENU"));
	}

	/**
	 * Adds a new cards to the selected cards in the menu of the Year of Plenty
	 * development card.
	 * 
	 * @param event
	 *            the mouse click that caused this method to be called.
	 */
	@FXML
	private void resourceSelected(MouseEvent event) {
		String type = ((Node) event.getSource()).getId();
		ImageView card = new ImageView();
		card.getStyleClass().add(type);
		card.setFitWidth(20);
		card.setPreserveRatio(true);
		card.setId(type);
		selectedResourceCardsPane.getChildren().add(0, card);
		if (selectedResourceCardsPane.getChildren().size() == 3) {
			selectedResourceCardsPane.getChildren().remove(2);
		}
	}

	/**
	 * Sends the message to the server that the player wants to play a Year of
	 * Plenty card.
	 */
	@FXML
	private void playYearOfPlenty() {
		if (selectedResourceCardsPane.getChildren().size() == 2) {
			ImageView firstCard = (ImageView) selectedResourceCardsPane.getChildren().get(0);
			ResourceType type1 = ResourceType.valueOf(firstCard.getId().toUpperCase());
			ImageView secondCard = (ImageView) selectedResourceCardsPane.getChildren().get(1);
			ResourceType type2 = ResourceType.valueOf(secondCard.getId().toUpperCase());

			Resources resources = new Resources(type1, 1);
			Resources resources2 = new Resources(type2, 1);
			resources.add(resources2);

			JSONObject jsonObj = new JSONObject();
			jsonObj.put(Attributes.RESOURCES.toString(), (new Mapper()).writeValueAsJson(resources));
			client.sendToServer(ServerTypes.PLAY_YEAR_OF_PLENTY.toString(), jsonObj);
			selectedResourceCardsPane.getChildren().clear();
			player.currentySelectedDevelopmentCardProperty().setValue(null);
		}
	}

	/**
	 * Sends the message to the server that the player wants to play a Monopoly
	 * card.
	 * 
	 * @param event
	 *            The mouse click that caused this method to be called.
	 */
	@FXML
	private void playMonopoly(MouseEvent event) {
		Node card = (Node) event.getSource();
		ResourceType type = ResourceType.valueOf(card.getId().toUpperCase());
		JSONObject jsonObject = new JSONObject();
		switch (type) {
		case BRICK:
			jsonObject.put(Attributes.RESOURCE.toString(), Attributes.BRICK.toString());
			break;
		case GRAIN:
			jsonObject.put(Attributes.RESOURCE.toString(), Attributes.GRAIN.toString());
			break;
		case LUMBER:
			jsonObject.put(Attributes.RESOURCE.toString(), Attributes.LUMBER.toString());
			break;
		case ORE:
			jsonObject.put(Attributes.RESOURCE.toString(), Attributes.ORE.toString());
			break;
		case WOOL:
			jsonObject.put(Attributes.RESOURCE.toString(), Attributes.WOOL.toString());
			break;
		}
		client.sendToServer(ServerTypes.PLAY_MONOPOLY.toString(), jsonObject);
		player.currentySelectedDevelopmentCardProperty().setValue(null);
	}

	/**
	 * Sends to the server that the player wants to play roadBuilding card.
	 */
	@FXML
	private void sendRoadBuilding() {
		List<Edge> edgesSelectedForRoadBuilding = client.getUser().getPlayer().getLastTwoClickedOnEdges();
		if (edgesSelectedForRoadBuilding.size() >= 1) {
			JSONObject jsonObj = new JSONObject();
			JSONArray array = new JSONArray();
			Edge edge = edgesSelectedForRoadBuilding.get(0);
			for (int i = 0; i < 2; i++) {
				JSONObject positionHex = new JSONObject();
				positionHex.put(Attributes.X.toString(), edge.getAdjacentHexesOfEdge().get(i).getxPosAxialHex());
				positionHex.put(Attributes.Y.toString(), edge.getAdjacentHexesOfEdge().get(i).getyPosAxialHex());
				array.put(positionHex);
			}
			jsonObj.put(Attributes.STREAT_ONE.toString(), array);

			
			if (edgesSelectedForRoadBuilding.size() >= 2) {
				JSONArray array2 = new JSONArray();
				Edge edge2 = edgesSelectedForRoadBuilding.get(1);
				for (int i = 0; i < 2; i++) {
					JSONObject positionHex = new JSONObject();
					positionHex.put(Attributes.X.toString(), edge2.getAdjacentHexesOfEdge().get(i).getxPosAxialHex());
					positionHex.put(Attributes.Y.toString(), edge2.getAdjacentHexesOfEdge().get(i).getyPosAxialHex());
					array2.put(positionHex);
				}
				jsonObj.put(Attributes.STREAT_TWO.toString(), array2);
			}	
			
			client.sendToServer(ServerTypes.PLAY_ROAD_BUILDING.toString(), jsonObj);
			edgesSelectedForRoadBuilding.clear();
			player.currentySelectedDevelopmentCardProperty().setValue(null);
		}
	}

	/**
	 * Adds an new card to the display of resource cards
	 * 
	 * @param imagePath
	 *            the path to the image of the card
	 * @param pane
	 *            the Pane the image is added to
	 */
	private void addCard(String imagePath, Pane pane) {
		Image image = new Image(imagePath);
		ImageView cardView = new ImageView(image);
		cardView.setPreserveRatio(true);
		cardView.setFitWidth(CARD_WIDTH);
		AnchorPane.setBottomAnchor(cardView, 0.0);
		AnchorPane.setRightAnchor(cardView, (double) (pane.getChildren().size() * (TRANSLATION_OVERLAPPING_CARDS)));
		cardView.setPickOnBounds(true);
		if (pane != victoryPointsPane) {
			cardView.getStyleClass().add("clickable");
		}
		pane.getChildren().add(cardView);
		
		if (pane.getChildren().size()==1) {
			pane.setMouseTransparent(true);
		}
		player.stateProperty().addListener((observable) -> {
			if (player.getState() == PlayerState.MOVE_ROBBER || player.getState() == PlayerState.DISCARD_RESOURCES) {
				pane.setMouseTransparent(true);
			} else {
				pane.setMouseTransparent(false);
			}
		});
	}

	/**
	 * Shows the info to the development card when the mouse hovers over the card.
	 * 
	 * @param cardsPane
	 *            The pane which registers the entering of the mouse.
	 * @param infoLabel
	 *            The label that is shown.
	 * @param playCardMenu
	 *            the menu that appears when the card is clicked.
	 */
	private void reactToHover(Pane cardsPane, Label infoLabel, Pane playCardMenu) {
		cardsPane.hoverProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!playCardMenu.isVisible()) {
					infoLabel.setVisible(newValue);
					infoPane.setVisible(newValue);
				} else {
					infoLabel.setVisible(false);
					infoPane.setVisible(false);
				}

			}
		});
	}

	/**
	 * Updates the displayed {@code PlayableDevelopmentCards} of the player by
	 * binding them to the list of {@code DevelopmentCards} of the player.
	 * 
	 * @see PlayableDevelopmentCard
	 * @see DevelopmentCard
	 */
	private void addBindings() {
		developmentCardsProperty.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateCards(PlayableDevelopmentCardType.KNIGHT, KNIGHT_IMG_PATH, knightsPane);
				updateCards(PlayableDevelopmentCardType.MONOPOLY, MONOPOLY_IMG_PATH, monopolyPane);
				updateCards(PlayableDevelopmentCardType.ROAD_BUILDING, ROAD_BUILDING_IMG_PATH, roadBuildingPane);
				updateCards(PlayableDevelopmentCardType.YEAR_OF_PLENTY, YEAR_OF_PLENTY_IMG_PATH, yearOfPlentyPane);

				updateVictoryPointCards();
			}
		});

		player.currentySelectedDevelopmentCardProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				PlayableDevelopmentCardType type = player.currentySelectedDevelopmentCardProperty().get();
				menuPane.getChildren().forEach(node -> node.setVisible(false));
				infoPane.getChildren().forEach(node -> node.setVisible(false));
				infoPane.setVisible(false);
				if (type != null) {
					menuPane.setVisible(true);
					switch (type) {
					case KNIGHT:
						knightMenu.setVisible(true);
						break;
					case MONOPOLY:
						monopolyMenu.setVisible(true);
						break;
					case ROAD_BUILDING:
						roadBuildingMenu.setVisible(true);
						break;
					case YEAR_OF_PLENTY:
						yearOfPlentyMenu.setVisible(true);
						break;
					}
				} else {
					menuPane.setVisible(false);
				}
			}
		});
		
		player.stateProperty().addListener((observable) -> {
			player.currentySelectedDevelopmentCardProperty().set(null);
		});
	}

	/**
	 * Sets the currentySelectedDevelopmentCardProperty of the player to the
	 * selected card, so that clicks on edges and hexes are now registered as part
	 * of the process of playing a development card
	 * 
	 * @param event
	 *            the mouse click that caused this method to be called
	 */
	@FXML
	private void cardSelected(MouseEvent event) {
		String sourceId = ((Node) event.getSource()).getId();
		PlayableDevelopmentCardType type = PlayableDevelopmentCardType.valueOf(sourceId);
		if (player.currentySelectedDevelopmentCardProperty().get() != type) {
			player.currentySelectedDevelopmentCardProperty().set(type);
		} else {
			player.currentySelectedDevelopmentCardProperty().set(null);
		}

	}

	/**
	 * Updates the displayed {@code VictoryPointCards} of the player
	 * 
	 * @see VictoryPointCard
	 */
	private void updateVictoryPointCards() {
		int count = 0;
		for (DevelopmentCard card : developmentCardsProperty.get()) {
			if (card instanceof VictoryPointCard) {
				count++;
			}
		}

		if (count < victoryPointsPane.getChildren().size()) {
			victoryPointsPane.getChildren().remove(count, victoryPointsPane.getChildren().size());
		} else if (count > victoryPointsPane.getChildren().size()) {
			while (victoryPointsPane.getChildren().size() < count) {
				addCard(VICTORY_POINT_IMG_PATH, victoryPointsPane);
			}
		}
	}

	/**
	 * Adds or removes images from the specified AnchorPane depending on the count
	 * of cards of the specified type the player has.
	 * 
	 * @param type
	 *            The PlayableDevelopmentCardType of the cards that are being
	 *            updated.
	 * @param imagePath
	 *            The file path to the image of the card.
	 * @param pane
	 *            The pane the card is added to or removed from.
	 */
	private void updateCards(PlayableDevelopmentCardType type, String imagePath, AnchorPane pane) {
		int count = 0;
		for (DevelopmentCard card : developmentCardsProperty.get()) {
			if (card instanceof PlayableDevelopmentCard) {
				if (((PlayableDevelopmentCard) card).getType() == type) {
					count++;
				}
			}
		}

		if (count < pane.getChildren().size()) {
			pane.getChildren().remove(count, pane.getChildren().size());
		} else if (count > pane.getChildren().size()) {
			while (pane.getChildren().size() < count) {
				addCard(imagePath, pane);
			}
		}
		updateLayout(pane);
	}

	/**
	 * Adjusts the distance between overlapping cards.
	 * 
	 * @param pane
	 *            the pane in which the layout is adjusted.
	 */
	private void updateLayout(Pane pane) {
		for (Node node : pane.getChildren()) {
			if (pane.getChildren().size() > 2) {
				AnchorPane.setRightAnchor(node,
						(double) pane.getChildren().indexOf(node) * (MAX_WIDTH / pane.getChildren().size()));
			} else {
				AnchorPane.setRightAnchor(node,
						(double) pane.getChildren().indexOf(node) * TRANSLATION_OVERLAPPING_CARDS);
			}
		}
	}

	@Override
	public void doInitializations(SimpleListProperty<DevelopmentCard> cards, ClientConnection client) {
		this.developmentCardsProperty = cards;
		this.client = client;
		this.player = client.getUser().getPlayer();
		addBindings();
	}

}
