package game.player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import game.Game;
import game.TradingRatios;
import game.bank.Bank;
import game.board.Board;
import game.board.construction.localities.City;
import game.board.construction.localities.Locality;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.corners.Corner;
import game.board.edges.Edge;
import game.board.harbors.Harbor;
import game.board.harbors.HarborType;
import game.board.hexes.Hex;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.cards.specialcards.SpecialCard;
import game.cards.specialcards.SpecialCardType;
import game.cards.victorypointcards.VictoryPointCard;
import game.dice.Dice;
import game.resources.ResourceType;
import game.resources.Resources;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import users.User;

/**
 * Represents one of the players of Settlers of Catan.
 * 
 * @author Christoph Hermann
 */
public class Player {

	/**
	 * The id of this player. It is, among other things, used to determine the turn
	 * order.
	 */
	private final int id;

	/**
	 * The name of this player.
	 */
	private String name;

	/**
	 * The {@code User} controlling this player.
	 * @see User
	 */
	private User user;

	/**
	 * The location of this player's profile picture.
	 */
	private String imageLocation;

	/**
	 * The color of this player.
	 */
	private Color color;

	/**
	 * The {@code Resources} this player has.
	 * 
	 * @see Resources
	 */
	private Resources resources = new Resources();
	
	/**
	 * The {@code Resources} the player last received. They are
	 * show next to the player info.
	 * @see Resources
	 */
	private Resources lastReceivedResources = new Resources();
	
	/**
	 * The {@code Resources} the player last lost. They are
	 * show next to the player info.
	 * @see Resources
	 */
	private Resources lastLostResources = new Resources();
	
	/** The dice that show the numbers  the player last rolled*/
	private Dice lastRolledDice = new Dice();

	/**
	 * The max number of {@code Road},that one player can own
	 */
	private static final int MAX_ROADS = 15;

	/**
	 * The max number of {@code Settlement},that one player can own
	 */
	private static final int MAX_SETTLEMENTS = 5;

	/**
	 * The max number of {@code City},that one player can own
	 */
	private static final int MAX_CITIES = 4;

	/**
	 * number of {@code Road},that one player own
	 */
	private int countRoad = 0;

	/**
	 * number of {@code settlement},that one player own
	 */
	private int countSettlement = 0;

	/**
	 * number of {@code City},that one player own
	 */
	private int countCity = 0;

	/**
	 * The amount of all night card that this {@code Player}
	 * 
	 * @see PlayableDevelopmentCardType
	 * @see PlayableDevelopmentCard
	 * @see Player
	 */
	private int countKnightCards = 0;

	/**
	 * The amount of all monopoly cards that this {@code Player}
	 * 
	 * @see PlayableDevelopmentCardType
	 * @see PlayableDevelopmentCard
	 * @see Player
	 */
	private int countMonopolyCards = 0;

	/**
	 * The amount of all road_building cards that this {@code Player}
	 * 
	 * @see PlayableDevelopmentCardType
	 * @see PlayableDevelopmentCard
	 * @see Player
	 */
	private int countRoadBuildingCards = 0;

	/**
	 * The amount of all year_of_plenty cards that this {@code Player}
	 * 
	 * @see PlayableDevelopmentCardType
	 * @see PlayableDevelopmentCard
	 * @see Player
	 */
	private int countYearOfPlentyCards = 0;

	/**
	 * The amount of all {@code VictoryPointCard} that this {@code Player}
	 * 
	 * @see VictoryPointCard
	 * @see Player
	 */
	private int countVictoryPointCards = 0;

	/**
	 * The amount of all developmentCard
	 * 
	 * @see DevelopmentCard
	 */
	private int numberOfAllDevelopmentCard = 0;

	/**
	 * If player has a harbor or not
	 */
	private boolean hasHarbor = false;

	/**
	 * If player want finish round twice or not
	 */
	private boolean wantFinishRoundTwice = false;

	/**
	 * If player has used development card this round or not
	 */
	private boolean hasPlayedDevelopmentCardThisTurn = false;
	
	/**
	 * List of {@code Edge}, the positions of roads, which is builded by {@code DevelopmentCard}.
	 */
	private List<Edge> cardRoadPositions = new ArrayList<>();

	/**
	 * The property of the set of {@code SpecialCards} this player owns.
	 * 
	 * @see SpecialCard
	 */
	private SimpleListProperty<SpecialCard> specialCardsProperty 
	= new SimpleListProperty<SpecialCard>(
			FXCollections.observableArrayList(new ArrayList<SpecialCard>()));

	/**
	 * The list of {@code Harbor} this {@code Player} owns
	 * 
	 * @see HarborType
	 */
	private List<Harbor> harbors = new ArrayList<>();

	/**
	 * The list of normal {@code Harbor} this {@code Player} owns
	 * 
	 * @see HarborType
	 */
	private List<Harbor> normalHarbors = new ArrayList<>();

	/**
	 * The list of special {@code Harbor} this {@code player} owns
	 * 
	 * @see HarborType
	 */
	private List<Harbor> specialHarbors = new ArrayList<>();

	/**
	 * The property of the list of edges that are selected to build a road on in the process of
	 * playing a Road Building DevelopmentCard.
	 */
	private SimpleListProperty<Edge> lastTwoClickedOnEdgesProperty = new SimpleListProperty<Edge>(
			FXCollections.observableArrayList(new ArrayList<Edge>()));

	/**
	 * The ratios at which this player can trade the different resources with the
	 * bank.
	 * 
	 * @see TradingRatios
	 * @see Resources
	 * @see Bank
	 */
	private ObjectProperty<TradingRatios> tradingRatios = new SimpleObjectProperty<>();

	/**
	 * The list of {@code DevelopmentCards} this player has.
	 * 
	 * @see DevelopmentCard
	 */
	private SimpleListProperty<DevelopmentCard> developmentCardsProperty = new SimpleListProperty<DevelopmentCard>(
			FXCollections.observableArrayList(new ArrayList<DevelopmentCard>()));

	/**
	 * The list of all {@code Localities} this player owns.
	 * 
	 * @see Locality
	 * @see Board
	 */
	private SimpleListProperty<Locality> localitiesProperty = new SimpleListProperty<Locality>(
			FXCollections.observableArrayList(new ArrayList<Locality>()));

	/**
	 * The list of all {@code Roads} this player owns.
	 * 
	 * @see Road
	 * @see Board
	 */
	private SimpleListProperty<Road> roadsProperty = new SimpleListProperty<Road>(
			FXCollections.observableArrayList(new ArrayList<Road>()));

	/**
	 * The amount of victory points this player has.
	 */
	private IntegerProperty victoryPointsProperty = new SimpleIntegerProperty();

	/**
	 * The amount of victory points this player has which are visible to all players.
	 */
	private IntegerProperty visibleVictoryPointsProperty = new SimpleIntegerProperty();

	/**
	 * The length of the longest road this player owns.
	 */
	private IntegerProperty longestRoadLengthProperty = new SimpleIntegerProperty();

	/**
	 * Specifies whether the player has played a development card this turn or not.
	 */
	private BooleanProperty developmentCardPlayedThisTurn = new SimpleBooleanProperty(false);

	/**
	 * The total number of Knight cards this player has played.
	 * 
	 * @see PlayableDevelopmentCardType
	 */
	private IntegerProperty playedKnightCards = new SimpleIntegerProperty();

	/**
	 * The current state of this player.
	 */
	private SimpleObjectProperty<PlayerState> stateProperty = new SimpleObjectProperty<PlayerState>();

	/**
	 * The amount of Resources this player has.
	 * 
	 * @see Resources
	 */
	private IntegerProperty resourceQuantityProperty = new SimpleIntegerProperty();
	
	/**
	 * The property of the development card the player is playing. Null if the
	 * player isn't currently playing any development card.
	 */
	private SimpleObjectProperty<PlayableDevelopmentCardType> currentySelectedDevelopmentCardProperty = new SimpleObjectProperty<PlayableDevelopmentCardType>();

	/**
	 * an ArrayList of corners on which the player can build
	 */
	private ArrayList<Corner> avilableCorners = new ArrayList<Corner>();

	/**
	 * an ArrayList of corners which the player has already occupied
	 */
	private ArrayList<Corner> occupiedCorners = new ArrayList<Corner>();

	/**
	 * an ArrayList of edges on which the player can build
	 */
	private ArrayList<Edge> avilableEdges = new ArrayList<Edge>();

	/**
	 * an ArrayList of edges which the player has already occupied
	 */
	private ArrayList<Edge> occupiedEdges = new ArrayList<Edge>();

	/**
	 * The current game
	 */
	private Game game;

	/**
	 * Creates a new player with the specified values.
	 * 
	 * @param id
	 *            the id of this player.
	 * @param name
	 *            the name of this player.
	 * @param imageLocation
	 *            the location of the image of this player.
	 * @param color
	 *            the color of this player.
	 * @param state
	 *            the {@link PlayerState} of this player.
	 */
	public Player(int id, String name, String imageLocation, Color color, PlayerState state) {
		this.id = id;
		this.name = name;
		this.imageLocation = imageLocation;
		this.color = color;
		this.stateProperty.set(state);

		tradingRatios.set(new TradingRatios(ResourceType.BRICK, 4, ResourceType.GRAIN, 4,
				ResourceType.LUMBER, 4, ResourceType.ORE, 4, ResourceType.WOOL, 4));
	}

	/**
	 * Recalculates the amount of victory points this player has.
	 */
	public void updateVictoryPoints() {
		updateVisibleVictoryPoints();

		int newVictoryPoints = visibleVictoryPointsProperty.get();

		for (DevelopmentCard card : developmentCardsProperty.get()) {
			if (card instanceof VictoryPointCard) {
				newVictoryPoints += ((VictoryPointCard) card).getVictoryPoints();
			}
		}

		victoryPointsProperty.set(newVictoryPoints);
	}

	/**
	 * Recalculates the amount of victory points this player has.
	 */
	private void updateVisibleVictoryPoints() {
		int newVictoryPoints = 0;

		for (SpecialCard card : getSpecialCards()) {
			newVictoryPoints += card.getVictoryPoints();
		}

		for (Locality locality : localitiesProperty.get()) {
			newVictoryPoints += locality.getVictoryPoints();
		}

		visibleVictoryPointsProperty.set(newVictoryPoints);
	}

	/**
	 * Removes a {@code SpecialCard} from this player.
	 * 
	 * @param specialCard
	 *            the {@link SpecialCard SpecialCard}.
	 */
	public void removeSpecialCard(SpecialCard specialCard) {
		getSpecialCards().remove(specialCard);
	}

	/**
	 * Adds a {@code SpecialCard} to this player.
	 * 
	 * @param specialCard
	 *            the {@link SpecialCard SpecialCard}.
	 */
	public void addSpecialCard(SpecialCard specialCard) {
		getSpecialCards().add(specialCard);
	}

	/**
	 * Adds a {@code DevelopmentCard} to this player
	 * 
	 * @param developmentCard
	 *            the developmentCard
	 */
	public void addDevelopmentCard(DevelopmentCard developmentCard) {
		developmentCardsProperty.get().add(developmentCard);
	}

	/**
	 * Sets the {@code resourceQuantity} according to the {@code resources}
	 */
	public void updateResourceQuantity() {
		int value = 0;
		for (ResourceType rt : ResourceType.values()) {
			value += resources.getResources().get(rt);
		}
		resourceQuantityProperty.set(value);
	}

	/**
	 * Adds a {@code Locality} to this player.
	 * 
	 * @param locality
	 *            the {@link Locality} to add.
	 */
	public void addLocality(Locality locality) {
		countSettlement++;
		this.localitiesProperty.add(locality);

		this.occupiedCorners.add(locality.getPosition());

		try{
			this.avilableCorners.remove(locality.getPosition());
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

		for(Edge edge : locality.getPosition().getAdjacentEdgesOfCorner()){
			if(!edge.isOccupied() && !this.avilableEdges.contains(edge)){
				this.avilableEdges.add(edge);
			}
		}
		
		if(locality instanceof Settlement){
			Harbor cornerHarbor = game.getBoard().isHarbor(locality.getPosition());
			if(cornerHarbor != null){
				harbors.add(cornerHarbor);
				if(cornerHarbor.getType() == HarborType.UNIVERSAL){
					normalHarbors.add(cornerHarbor);
				}else{
					specialHarbors.add(cornerHarbor);
				}
			}
		}
		
	}

	/**
	 * Adds a {@code Road} to this player.
	 * 
	 * @param road
	 *            the {@link Road} to add.
	 */
	public void addRoad(Road road) {
		countRoad++;
		this.roadsProperty.add(road);

		this.occupiedEdges.add(road.getPosition());
		try{

			Corner adCorner = null;
			for(Corner corner : road.getPosition().getAdjacentCornersOfEdge()){
				if(!corner.isCornerOccupied()){
					adCorner = corner;
				}
			}
			for(Edge edge : adCorner.getAdjacentEdgesOfCorner()){
				if(!edge.isOccupied() && edge != road.getPosition()){
					this.avilableEdges.add(edge);
				}
			}
			this.avilableEdges.remove(road.getPosition());
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

		for (Corner corner : road.getPosition().getAdjacentCornersOfEdge()){
			if(cornersUnOccupied(corner.getAdjacentCornersOfCorner()) && !corner.isCornerOccupied()){
				this.avilableCorners.add(corner);
			}
		}

		updateLongestRoadLengthProperty();
	}

	/**
	 * checks if all given corners are unoccupied
	 * @param corners the list of the corners that are being checked
	 * @return true if at least one of the corners is occupied, false etherwise
	 */
	public boolean cornersUnOccupied(ArrayList<Corner> corners){
		for(Corner corner : corners){
			if(corner.isCornerOccupied()){
				return false;
			}
		}
		return true;
	}

	/**
	 * updates the ({@code longestRoad}) of this player
	 */
	public void updateLongestRoadLengthProperty() {

		// längste Handelsstrasse erstellen Tiefensuche rekursiv aus roadsProperty
		// longestRoadLengthProperty
		// ausgehend von irgendeiner Strasse
		// localitiesProperty

		ArrayList<Road> visitedRoads = new ArrayList<>();
		ArrayList<Edge> blackRoads = new ArrayList<>();
		visitedRoads.addAll(roadsProperty);

		int longestRoad = 1;
		for (int i = 0; i < visitedRoads.size(); i++) {

			blackRoads.removeAll(blackRoads);

			longestRoad = Math.max(longestRoad, visitRoad(blackRoads, visitedRoads.get(i), null));

		}

		longestRoadLengthProperty.set(longestRoad);
	}

	/**
	 * Recursively called function to calculate the longest road
	 * 
	 * @param blackRoads
	 *            roads just visited, you need this to avoid circles
	 * @param vRoad
	 *            momentarily regarded road
	 * @param corner1
	 *            you need this to avoid going backwards
	 * @return counter2
	 */

	public int visitRoad(ArrayList<Edge> blackRoads, Road vRoad, Corner corner1) {
		int counter2 = 0;
		int counter = 0;
		blackRoads.add(vRoad.getPosition());// damit diese Kante nicht nochmals betrachtet wird
		// betrachte die beiden Ecken der Kante
		for (int j = 0; j < 2; j++) {
			Corner corner = vRoad.getPosition().getAdjacentCornersOfEdge().get(j);

			if (!corner.equals(corner1)) {
				if (corner.getLocality() == null
						|| (corner.getLocality() != null && corner.getLocality().getOwner() == this)) {

					for (int k = 0; k < corner.getAdjacentEdgesOfCorner().size(); k++) {
						if (!(corner.getAdjacentEdgesOfCorner().get(k).equals(vRoad.getPosition()))
								&& corner.getAdjacentEdgesOfCorner().get(k).getRoad() != null) {
							Edge r = corner.getAdjacentEdgesOfCorner().get(k);

							// if
							// (roadsProperty.contains(corner.getAdjacentEdgesOfCorner().get(k).getRoad()))
							// {
							List<Road> filteredList = roadsProperty.stream().filter(s -> s.getPosition().equals(r))
									.collect(Collectors.toList());
							if (!filteredList.isEmpty()) {

								if (!(blackRoads.contains(corner.getAdjacentEdgesOfCorner().get(k)))) {

									counter2 = visitRoad(blackRoads, corner.getAdjacentEdgesOfCorner().get(k).getRoad(),
											corner);

									counter = Math.max(counter, counter2);
								}
							}
						}
					}
				}
			}
		}
		return 1 + counter;
	}

	/**
	 * Add a {@code Harbor} to this {@code Player} and distinguish the type to
	 * normalList.
	 * 
	 * @param harbor
	 *            the {@link Harbor} to add.
	 * @see Player
	 */
	public void addHarbor(Harbor harbor) {
		boolean isTrue = true;
		HarborType harborType = harbor.getType();
		if (harborType == HarborType.NONE) {
		} else if (harbor.getType() != HarborType.UNIVERSAL) {
			this.harbors.add(harbor);
			this.specialHarbors.add(harbor);
			tradingRatios.get().combine(harbor.getType().getTradingRatio());
			setHasHarbor(isTrue);
		} else {
			this.harbors.add(harbor);
			this.normalHarbors.add(harbor);
			setHasHarbor(isTrue);
		}
	}

	/**
	 * Calculate {@code DevelopmentCard} number of every developmentCardType and the
	 * number of every Type of development cards will be updated
	 * 
	 * @see PlayableDevelopmentCardType
	 * @see VictoryPointCard
	 * @return number of all developmentCards
	 */
	public int calculateNumberOfDevelopmentCards() {
		int knightCardsNumber = 0;
		int monopolyCardsNumber = 0;
		int roadCardsNumber = 0;
		int plentyCardsNumber = 0;
		int victoryCardsNumber = 0;

		for (DevelopmentCard developmentCard : getDevelopmentCards()) {
			if (developmentCard instanceof PlayableDevelopmentCard) {
				PlayableDevelopmentCardType developmentCardType = ((PlayableDevelopmentCard) developmentCard).getType();
				switch (developmentCardType) {
				case KNIGHT:
					knightCardsNumber++;
					break;

				case MONOPOLY:
					monopolyCardsNumber++;
					break;

				case ROAD_BUILDING:
					roadCardsNumber++;
					break;

				case YEAR_OF_PLENTY:
					plentyCardsNumber++;
					break;
				}
			} else if (developmentCard instanceof VictoryPointCard) {
				victoryCardsNumber++;
			}
		}

		setCountKNIGHTCards(knightCardsNumber);
		setCountMONOPOLYCards(monopolyCardsNumber);
		setCountROAD_BUILDINGCards(roadCardsNumber);
		setCountYEAR_OF_PLENTYCards(plentyCardsNumber);
		setCountVICTORYCards(victoryCardsNumber);
		return numberOfAllDevelopmentCard = knightCardsNumber + monopolyCardsNumber + roadCardsNumber
				+ plentyCardsNumber + victoryCardsNumber;
	}

	/**
	 * Adds a {@code City} to this players' list of cities. Doing so removes the
	 * {@link Settlement} at the position of the new {@link City}.
	 * 
	 * @param city
	 *            the city to add.
	 */
	public void addCity(City city) {
		Corner cityPosition = city.getPosition();
		for (Locality locality : getLocalities()) {
			if (locality.getPosition() == cityPosition) {
				countSettlement--;
				countCity++;
				getLocalities().remove(locality);
				getLocalities().add(city);
				break;
			}
		}
	}

	/**
	 * checks if the {@code Player} has an {@code Settlement} on the position {@code Corner}
	 * @param corner the corner that is checked for a {@code Settlement}
	 * @return true, if the player has a settlement on the position corner
	 */
	public boolean isSettlement(Corner corner){
		for(Locality loc : getLocalities()){
			if((loc.getPosition() == corner) && (loc instanceof Settlement)){
				return true;
			}
		}
		return false;
	}

	/**
	 * checks if the {@code Player} has an {@code City} on the position {@code Corner}
	 * @param corner the corner that is checked for an {@code City}
	 * @return true, if the player has a city on the position corner
	 */
	public boolean isCity(Corner corner){
		for(Locality loc : getLocalities()){
			if((loc.getPosition() == corner) && (loc instanceof City)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks if a availableCorner is next to the given corner.
	 * If so then the availableCorner gets removed form the ArrayList of avialableCorners
	 * @param  corner a corner
	 */
	public void updateAvialableCorners(Corner corner) {
		ArrayList<Corner> copyList = new ArrayList<Corner>(avilableCorners);
		for(Corner avCorner : copyList){
			if(avCorner.getAdjacentCornersOfCorner().contains(corner) || avCorner == corner){
				avilableCorners.remove(avCorner);
			}
		}
	}
	
	/**
	 * checks if a availableEdge is the same as the given corner.
	 * If so then the availableEdge gets removed form the ArrayList of avialableEdges
	 * @param  edge an edge
	 */
	public void updateAvialableEdges(Edge edge) {
		ArrayList<Edge> copyList = new ArrayList<Edge>(avilableEdges);
		for(Edge avEdge : copyList){
			if(avEdge == edge){
				avilableEdges.remove(avEdge);
			}
		}
	}


	/*
	 * *****************************************************************************
	 * *********************** Note: Below this point are only getters and setters.
	 * Please make sure to add all other method above.
	 */

	/**
	 * Returns all localities owned by this player.
	 * 
	 * @return the {@link Locality localities}.
	 */
	public List<Locality> getLocalities() {
		return localitiesProperty.get();
	}

	/**
	 * Returns all roads owned by this player.
	 * 
	 * @return the {@link Road roads}.
	 */
	public List<Road> getRoads() {
		return roadsProperty.get();
	}

	/**
	 * Sets this player's roads.
	 * 
	 * @param roads
	 *            the {@link Road roads} to set.
	 */
	public void setRoads(List<Road> roads) {
		this.roadsProperty.get().setAll(roads);
	}

	/**
	 * Returns the id of this player.
	 * 
	 * @return the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the name of this player.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the location of the profile image of this player.
	 * 
	 * @return the imageLocation.
	 */
	public String getImageLocation() {
		return imageLocation;
	}

	/**
	 * Returns the color of this player.
	 * 
	 * @return the color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the resources this player owns.
	 * 
	 * @return the resources.
	 */
	public Resources getResources() {
		return resources;
	}

	/**
	 * Returns the developmentCards this player has.
	 * 
	 * @return the developmentCards.
	 */
	public List<DevelopmentCard> getDevelopmentCards() {
		return developmentCardsProperty.get();
	}

	/**
	 * Returns the length of the longest {@code Road} of this player.
	 * 
	 * @return the length of the longest {@link Road}.
	 */
	public IntegerProperty getLongestRoadLengthProperty() {
		return longestRoadLengthProperty;
	}

	/**
	 * Returns the property of the developmentCards this player has.
	 * 
	 * @return the developmentCards.
	 */
	public SimpleListProperty<DevelopmentCard> developmentCardsProperty() {
		return developmentCardsProperty;
	}

	/**
	 * Returns the specialCards this player has.
	 * 
	 * @return the specialCards.
	 */
	public List<SpecialCard> getSpecialCards() {
		return specialCardsProperty.get();
	}

	/**
	 * Returns the tradingRatios of this player.
	 * 
	 * @return the tradingRatios.
	 */
	public TradingRatios getTradingRatios() {
		return tradingRatios.get();
	}

	/**
	 * Returns the tradingRatios property of this player.
	 * 
	 * @return the tradingRatios property.
	 */
	public ObjectProperty<TradingRatios> tradingRatios() {
		return tradingRatios;
	}

	/**
	 * Returns the amount of victory points this player has.
	 * 
	 * @return the amount of victory points.
	 */
	public int getVictoryPoints() {
		return victoryPointsProperty.get();
	}
	
	/**
	 * Returns the amount of visible victory points this player has.
	 * 
	 * @return the amount of visible victory points.
	 */
	public int getVisibleVictoryPoints() {
		return visibleVictoryPointsProperty.get();
	}

	/**
	 * Returns the length of this player's longest road.
	 * 
	 * @return the longest road length.
	 */
	public int getLongestRoadLength() {
		return longestRoadLengthProperty.get();
	}

	/**
	 * Returns the number of knight cards this player has played.
	 * 
	 * @return the number of played knight cards.
	 */
	public int getPlayedKnightCards() {
		return playedKnightCards.get();
	}

	/**
	 * Returns the victoryPoints property.
	 * 
	 * @return the victoryPoints.
	 */
	public IntegerProperty victoryPointsProperty() {
		return victoryPointsProperty;
	}

	/**
	 * Returns the visibleVictoryPoints property.
	 * 
	 * @return the visible victory points.
	 */
	public IntegerProperty visibleVictoryPointsProperty() {
		return visibleVictoryPointsProperty;
	}

	/**
	 * Returns the longestRoadLength property.
	 * 
	 * @return the longestRoadLength.
	 */
	public IntegerProperty longestRoadLengthProperty() {
		return longestRoadLengthProperty;
	}

	/**
	 * Returns the playedKnightCards property.
	 * 
	 * @return the playedKnightCards.
	 */
	public IntegerProperty playedKnightCardsProperty() {
		return playedKnightCards;
	}

	/**
	 * Returns whether this player has played a development card this turn or not.
	 * 
	 * @return true, if this player has played a development card this turn. false,
	 *         otherwise.
	 */
	public boolean getDevelopmentCardPlayedThisTurn() {
		return developmentCardPlayedThisTurn.get();
	}

	/**
	 * Returns the developmentCardPlayedThisTurn property.
	 * 
	 * @return the developmentCardPlayedThisTurn.
	 */
	public BooleanProperty developmentCardPlayedThisTurnProperty() {
		return developmentCardPlayedThisTurn;
	}

	/**
	 * Returns the state of this player.
	 * 
	 * @return the {@link PlayerState}.
	 */
	public PlayerState getState() {
		return stateProperty.get();
	}

	/**
	 * Sets the state of this player.
	 * 
	 * @param state
	 *            the {@link PlayerState}.
	 */
	public void setState(PlayerState state) {
		this.stateProperty.set(state);
	}

	/**
	 * Returns the amount of Resources this player has.
	 * 
	 * @return the amount of {@link Resources}.
	 */
	public int getResourceQuantity() {
		return resourceQuantityProperty.get();
	}

	/**
	 * Sets the amount of Resources this player has.
	 * 
	 * @param resourceQuantity
	 *            the amount of {@link Resources}.
	 */
	public void setResourceQuantity(int resourceQuantity) {
		this.resourceQuantityProperty.set(resourceQuantity);
	}

	/**
	 * Sets the name of this player.
	 * 
	 * @param name
	 *            the name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the color of this player.
	 * 
	 * @param color
	 *            the color to set.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * The Getter for the roadsProperty
	 * 
	 * @return The property wrapping the list of {@code Localities}.
	 */
	public SimpleListProperty<Locality> localitiesProperty() {
		return localitiesProperty;
	}

	/**
	 * The Getter for the roadsProperty
	 * 
	 * @return The property wrapping the list of {@code Roads}.
	 */
	public SimpleListProperty<Road> roadsProperty() {
		return roadsProperty;
	}

	/**
	 * Returns the property representing the number of {@code Resources} this player
	 * has.
	 * 
	 * @return the property representing the number of {@link Resources}.
	 */
	public IntegerProperty resourceQuantityProperty() {
		return resourceQuantityProperty;
	}

	/**
	 * Sets the number of knight cards which this player has played.
	 * 
	 * @param playedKnightCards
	 *            the number of knight cards to set.
	 * @see PlayableDevelopmentCard
	 * @see PlayableDevelopmentCardType
	 */
	public void setPlayedKnightCards(int playedKnightCards) {
		this.playedKnightCards.set(playedKnightCards);
	}

	/**
	 * Player has played one more the knight card during this game
	 */
	public void playedOneMoreKnightCard() {
		setPlayedKnightCards(playedKnightCardsProperty().get() + 1);
	}

	/**
	 * Sets the number of victory points this player has.
	 * 
	 * @param victoryPoints
	 *            the number of victory points to set.
	 */
	public void setVictoryPoints(int victoryPoints) {
		this.victoryPointsProperty.set(victoryPoints);
	}

	/**
	 * Returns the property of the players current state .
	 * 
	 * @return the PlayerState
	 */
	public SimpleObjectProperty<PlayerState> stateProperty() {
		return stateProperty;
	}

	/**
	 * Returns the number of {@code Cities} this player owns.
	 * 
	 * @return the number of {@link City Cities}.
	 */
	public int getCountCity() {
		return countCity;
	}

	/**
	 * Returns the number of {@code Settlements} this player owns.
	 * 
	 * @return the number of {@link Settlement Settlements}.
	 */
	public int getCountSettlement() {
		return countSettlement;
	}

	/**
	 * Returns the number of {@code Roads} this player owns.
	 * 
	 * @return the number of {@link Road Roads}.
	 */
	public int getCountRoad() {
		return countRoad;
	}

	/**
	 * Returns the maximum number of {@code Roads} a player can own.
	 * 
	 * @return the maximum number of {@link Road Roads}.
	 */
	public static int getMaxRoad() {
		return MAX_ROADS;
	}

	/**
	 * Returns the maximum number of {@code Settlements} a player can own.
	 * 
	 * @return the maximum number of {@link Settlement Settlements}.
	 */
	public static int getMaxSettlement() {
		return MAX_SETTLEMENTS;
	}

	/**
	 * Returns the maximum number of {@code Cities} a player can own.
	 * 
	 * @return the maximum number of {@link City Cities}.
	 */
	public static int getMaxCity() {
		return MAX_CITIES;
	}

	/**
	 * Return the number of all {@code DevelopmentCards} this player owns.
	 * 
	 * @return the number of all {@link DevelopmentCard DevelopmentCards}.
	 */
	public int getNumberOfAllDevelopmentCard() {
		return numberOfAllDevelopmentCard;
	}

	/**
	 * @return the countKNIGHTCards
	 */
	public int getCountKNIGHTCards() {
		return countKnightCards;
	}

	/**
	 * The countKNIGHTCards to set
	 * @param countKNIGHTCards the number of cards to set
	 */
	public void setCountKNIGHTCards(int countKNIGHTCards) {
		this.countKnightCards = countKNIGHTCards;
	}

	/**
	 * @return the countMONOPOLYCards
	 */
	public int getCountMONOPOLYCards() {
		return countMonopolyCards;
	}

	/**
	 * @param countMONOPOLYCards
	 *            the countMONOPOLYCards to set
	 */
	public void setCountMONOPOLYCards(int countMONOPOLYCards) {
		this.countMonopolyCards = countMONOPOLYCards;
	}

	/**
	 * @return the countROAD_BUILDINGCards
	 */
	public int getCountROAD_BUILDINGCards() {
		return countRoadBuildingCards;
	}

	/**
	 * @param countROAD_BUILDINGCards
	 *            the countROAD_BUILDINGCards to set
	 */
	public void setCountROAD_BUILDINGCards(int countROAD_BUILDINGCards) {
		this.countRoadBuildingCards = countROAD_BUILDINGCards;
	}

	/**
	 * @return the countYEAR_OF_PLENTYCards
	 */
	public int getCountYEAR_OF_PLENTYCards() {
		return countYearOfPlentyCards;
	}

	/**
	 * @param countYEAR_OF_PLENTYCards
	 *            the countYEAR_OF_PLENTYCards to set
	 */
	public void setCountYEAR_OF_PLENTYCards(int countYEAR_OF_PLENTYCards) {
		this.countYearOfPlentyCards = countYEAR_OF_PLENTYCards;
	}

	/**
	 * @return the countVICTORYCards
	 */
	public int getCountVICTORYCards() {
		return countVictoryPointCards;
	}

	/**
	 * @param countVICTORYCards
	 *            the countVICTORYCards to set
	 */
	public void setCountVICTORYCards(int countVICTORYCards) {
		this.countVictoryPointCards = countVICTORYCards;
	}

	/**
	 * Set new {@code TradingRatios} of this player.
	 * 
	 * @param tradingRatios
	 *            the new {@link TradingRatios}.
	 */
	public void setTradingRatio(TradingRatios tradingRatios) {
		this.tradingRatios.set(tradingRatios);
	}

	/**
	 * Set true if {@code Player} has builded a settlement on harbor
	 * 
	 * @param hasHarbor
	 *            whether this player has a harbor or not.
	 */
	public void setHasHarbor(boolean hasHarbor) {
		this.hasHarbor = hasHarbor;
	}

	/**
	 * Get hasHarbor
	 * 
	 * @return true, if this {@code Player} has a {@code Harbor}
	 */
	public boolean hasHarbor() {
		return hasHarbor;
	}

	/**
	 * @return the normalHarbors of this {@code Player}
	 */
	public List<Harbor> getnormalHarbors() {
		return normalHarbors;
	}

	/**
	 * @return the harbors of this {@code Player}
	 */
	public List<Harbor> getspecialHarbors() {
		return specialHarbors;
	}

	/**
	 * @return the wantFinishRound
	 */
	public boolean isWantFinishRoundTwice() {
		return wantFinishRoundTwice;
	}

	/**
	 * @return the hasUsedDevelopmentCardThisTurn
	 */
	public boolean hasPlayedDevelopmentCardThisTurn() {
		return hasPlayedDevelopmentCardThisTurn;
	}

	/**
	 * Set the wantFinishRoundTwice True
	 */
	public void askedFinischRoundTwice() {
		this.wantFinishRoundTwice = true;
	}

	/**
	 * Set the wantFinishRoundTwice false
	 */
	public void setBackhasAsedFinischRoundTwice() {
		this.wantFinishRoundTwice = false;
	}

	/**
	 * Set hasUsedDevelopmentCardThisTurn false
	 */
	public void setBackHasPlayedDevelopmentCardThisTurn() {
		this.hasPlayedDevelopmentCardThisTurn = false;
	}

	/**
	 * Set hasUsedDevelopmentCardThisTurn true
	 */
	public void setHasPlayedDevelopmentCardThisTurn() {
		this.hasPlayedDevelopmentCardThisTurn = true;
	}

	/**
	 * Returns the list of the edges the player has selected while playing a
	 * RoadBuilding card.
	 * 
	 * @return The list of the edges.
	 */
	public List<Edge> getLastTwoClickedOnEdges() {
		return lastTwoClickedOnEdgesProperty.get();
	}

	/**
	 * Returns the property of the {@code PlayableDevelopmentCard} the player is
	 * playing. The wrapped value is null if the player in't currently playing any
	 * {@code PlayableDevelopmentCard}
	 * 
	 * @return the PlayableDevelopmentCard list as a property
	 */
	public SimpleObjectProperty<PlayableDevelopmentCardType> currentySelectedDevelopmentCardProperty() {
		return currentySelectedDevelopmentCardProperty;
	}

	/**
	 * Add one development card
	 */
	public void addDevelopmentCardNumber() {
		this.numberOfAllDevelopmentCard++;
	}

	/**
	 * Minus one development card
	 */
	public void minusDevelopmentCardNumber() {
		this.numberOfAllDevelopmentCard--;
	}

	/**
	 * Minus one knight card
	 */
	public void minusCountKnightCards() {
		this.countKnightCards--;
	}

	/**
	 * Add one monopoly card
	 */
	public void addCountMonopolyCards() {
		this.countMonopolyCards++;
	}

	/**
	 * Minus one monopoly card
	 */
	public void minusCountMonopolyCards() {
		this.countMonopolyCards--;
	}

	/**
	 * Add one build road card
	 */
	public void addCountRoadBuildingCards() {
		this.countRoadBuildingCards++;
	}

	/**
	 * Minus one build road card
	 */
	public void minusCountRoadBuildingCards() {
		this.countRoadBuildingCards--;
	}

	/**
	 * Add one plenty card
	 */
	public void addCountYearOfPlentyCards() {
		this.countYearOfPlentyCards++;
	}

	/**
	 * Minus one plenty card
	 */
	public void minusCountYearOfPlentyCards() {
		this.countYearOfPlentyCards--;
	}

	/**
	 * Add one victory card
	 */
	public void addCountVictoryPointCards() {
		this.countVictoryPointCards++;
	}

	/**
	 * Minus one victory card
	 */
	public void minusCountVictoryPointCards() {
		this.countVictoryPointCards--;
	}

	/**
	 * Get boolean value, if this player has the largest army
	 * 
	 * @return haslargestArmy return true if this player has the largest army
	 */
	public boolean hasLargestArmyCard() {
		for (SpecialCard specialCard : getSpecialCards()) {
			if (specialCard.getType().equals(SpecialCardType.LARGEST_ARMY)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get boolean value, if this player has the longest road
	 * @return true, if this player has the longest road
	 */
	public boolean hasLongestRoadCard() {
		for (SpecialCard specialCard : getSpecialCards()) {
			if (specialCard.getType().equals(SpecialCardType.LONGEST_ROAD)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the resources of this player.
	 * 
	 * @param resources
	 *            the new resources of this player.
	 */
	public void setResources(Resources resources) {
		this.resources = resources;
	}
	
	/**
	 * Gets the ArrayList of {@code avilableCorners}
	 * @return avilableCorners
	 */
	public ArrayList<Corner> getAvilableCorners() {
		return avilableCorners;
	}

	/**
	 * Gets the ArrayList of {@code occupiedCorners}
	 * @return occupiedCorners
	 */
	public ArrayList<Corner> getOccupiedCorners() {
		return occupiedCorners;
	}

	/**
	 * Gets the ArrayList of {@code avilableEdges}
	 * @return avilableEdges
	 */
	public ArrayList<Edge> getAvilableEdges() {
		return avilableEdges;
	}

	/**
	 * Gets the ArrayList of {@code occupiedEdges}
	 * @return occupiedEdges
	 */
	public ArrayList<Edge> getOccupiedEdges() {
		return occupiedEdges;
	}

	/**
	 * checks if the player has an {@code Harbor} with the {@code HarborType} type
	 * @param type the {code harborType} that is checked.
	 * @return true is the player has a harbor of that type, false otherwise.
	 */
	public boolean hasHarbor(HarborType type) {
		for(Harbor harbor : getspecialHarbors()){
			if(harbor.getType() == type){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks if the player has an normal harbor
	 * @return true is the player has an universal harbor, false otherwise.
	 */
	public boolean hasNormalHarbor(){
		if(!getnormalHarbors().isEmpty()){
			return true;
		}
		return false;
	}


	/**
	 * Getter for the property of the last two clicked on edges during 
	 * playing of a road building card.
	 * @return the edges list as a property.
	 */
	public SimpleListProperty<Edge> lastTwoClickedOnEdgesProperty() {
		return lastTwoClickedOnEdgesProperty;
	}

	/**
	 * Getter for the property of the players special cards.
	 * @return the set of special cards as a property.
	 */
	public SimpleListProperty<SpecialCard> specialCardsProperty() {
		return specialCardsProperty;
	}

	/**
	 * Counts the localities which are build next to a hex with the corresponding hextype to the ResourceType
	 * @param reType the {code ResourceType} for which the number of localities is checked.
	 * @return the number of localities of this player around a hex of that type.
	 */
	public int getResourceAmount(ResourceType reType) {
		int resourceCount = 0;
		for(Locality loc : getLocalities()){
			for(Hex hex : loc.getPosition().getAdjacentHexesOfCorner()){
				if(hex.getType().toResourceType() == reType){
					resourceCount++;
				}
			}
		}
		return resourceCount;
	}

	/**
	 * searches an Locality without roads
	 * @return position of the locality
	 */
	public Corner getLocalityWithOutRoad() {
		for(Locality loc : getLocalities()){
			if(loc.getPosition().hasNoRoads()){
				return loc.getPosition();
			}
		}
		return null;
	}

	/**
	 * Returns the {@code User} controlling this player.
	 * @return the {@link User}.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the the {@code User} controlling this player.
	 * @param user the {@link User} to set.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Getter for the {@code Resources} the player last received. They are
	 * show next to the player info.
	 * @return The received {@code Resources}.
	 * @see Resources
	 */
	public Resources getLastReceivedResources() {
		return lastReceivedResources;
	}
	
	/**
	 * Sets the game.
	 * @param game the current game.
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Getter for the {@code Resources} the player last lost. They are
	 * show next to the player info.
	 * @return The lost {@code Resources}.
	 * @see Resources
	 */
	public Resources getLastLostResources() {
		return lastLostResources;
	}

	/**
	 * Getter for the positions by using build road cards.
	 * @return list of {@code Edge}, the list of road position
	 * @see PlayableDevelopmentCard
	 * @see Edge
	 * @see Road
	 */
	public List<Edge> getCardRoadPositions() {
		return cardRoadPositions;
	}

	/** 
	 * Getter for the {@code Dice} that show the numbers  the player last rolled
	 * @return the {@code Dice}
	 */
	public Dice getLastRolledDice() {
		return lastRolledDice;
	}
}
