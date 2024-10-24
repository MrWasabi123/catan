package game.board;

import java.util.ArrayList; 
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import game.board.construction.Construction;
import game.board.construction.localities.City;
import game.board.construction.localities.Locality;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.corners.Corner;
import game.board.edges.Edge;
import game.board.harbors.Harbor;
import game.board.harbors.HarborType;
import game.board.hexes.Hex;
import game.board.hexes.HexType;
import game.board.robber.Robber;
import game.board.tokens.Token;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 * Represents the game board.
 * @author Christoph Hermann
 * @author Cornelia Sedlmeir-Hofmann
 */
public class Board {

	/**
	 * The {@code Robber}.
	 * @see Robber
	 */
	private Robber robber;

	/**
	 * The number of {@code Hex} circles on this board.
	 * @see Hex
	 */
	public static final int NUMBER_OF_CIRCLES = 3;

	/**
	 * The distance between the center and a {@code Corner} of a {@code Hex}.
	 * @see Corner
	 * @see Hex
	 */
	public static final double RADIUS = 50.0;

	/**
	 * The distance between the center and an {@code Edge} of a {@code Hex}.
	 * @see Edge
	 * @see Hex
	 */
	public static final double DISTANCE = Math.sqrt(3) / 2.0 * RADIUS;

	/**
	 * The hexagonal fields of this board.
	 * @see Hex
	 */
	private ArrayList<Hex> hexes;

	/**
	 * The {@code Edges} of the hexes on this board.
	 * @see Edge
	 * @see Hex
	 */
	private ArrayList<Edge> edges;

	/**
	 * The {@code Corners} of the hexes on this board.
	 * @see Corner
	 * @see Hex
	 */
	private ArrayList<Corner> corners;

	/**
	 * The {@code Construction} on this board.
	 * @see Construction
	 */
	private SimpleListProperty<Construction> constructionsProperty
	= new SimpleListProperty<Construction>(FXCollections.observableArrayList(
			new ArrayList<Construction>()));

	/**
	 * The {@code Settlement} on this board.
	 * @see Settlement
	 */
	private ArrayList<Settlement> settlements = new ArrayList<Settlement>();

	/**
	 * The {@code City} on this board.
	 * @see City
	 */
	private ArrayList<City> cities = new ArrayList<City>();

	/**
	 * The {@code Roads} on this board.
	 * @see Road
	 */
	private ArrayList<Road> roads = new ArrayList<Road>();

	/**
	 * The {@code Harbor} on this board.
	 * @see Harbor
	 */
	private ArrayList<Harbor> harbors;

	/**
	 * The tokens on this board.
	 * @see Token
	 */
	private ArrayList<Token> tokens = new ArrayList<>();

	/**
	 * Creates a new board. It will be initialized by an internal {@link BoardInitialization} object.
	 */
	public Board() {
		BoardInitialization boardIntitializer = new BoardInitialization();

		this.hexes = boardIntitializer.getHexes();
		this.corners = boardIntitializer.getCorners();
		this.edges = boardIntitializer.getEdges();
		this.harbors = boardIntitializer.getHarbors();

		for(Hex hex:hexes) {
			if(hex.getType().equals(HexType.DESERT)) {
				this.robber=new Robber(hex);
				break;
			}

		}

		setTokens();
	}

	/**
	 * Adds a {@code Construction} to this board.
	 * @param construction the {@link Construction} to add.
	 */
	public void addConstruction(Construction construction) {
		if(construction instanceof Locality) {
			Locality locality = (Locality) construction;
			locality.getPosition().setLocality(locality);
			if(((Locality)construction) instanceof Settlement) {
				settlements.add((Settlement)construction);
			}
			else{
				cities.add((City)construction);
			}
		} else if (construction instanceof Road) {
			Road road = (Road)construction;
			road.getPosition().setRoad(road);
			roads.add((Road)construction);
		}
		constructionsProperty.get().add(construction);
	}

	/**
	 * Returns the {@code Hex} represented by the specified String.
	 * @param position the String identifier of the {@link Hex}.
	 * @return the hex.
	 */
	public Hex searchHex(String position) {
		char[] charArray = position.toCharArray();
		Arrays.sort(charArray);
		for (Hex hex : this.hexes) {
			char[] hexPosition = hex.getLocality().toCharArray();
			Arrays.sort(hexPosition);
			if (Arrays.equals(hexPosition, charArray)) {
				return hex;
			}
		}
		return null;
	}

	/**
	 * Returns the {@code Hex} represented by axial x y coordinates.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the hex.
	 */
	public Hex searchHex(int x, int y) {
		for(Hex hex:this.hexes) {
			if(x == hex.getxPosAxialHex() && y== hex.getyPosAxialHex())
				return hex;
		}
		return null;
	}

	/**
	 * Returns the {@code Corner} represented by the specified String.
	 * @param position the String identifier of the {@link Corner}.
	 * @return the corner.
	 */
	public Corner searchCorner(String position) {
		char[] charArray = position.toCharArray();
		Arrays.sort(charArray);
		for (Corner corner : this.corners) {
			char[] cornerPosition = corner.getCornerLocality().toCharArray();
			Arrays.sort(cornerPosition);
			if (Arrays.equals(cornerPosition, charArray)) {
				return corner;
			}
		}
		return null;
	}

	/**
	 * Returns the {@code Edge} represented by the specified String.
	 * @param position the String identifier of the {@link Edge}.
	 * @return the edge.
	 */
	public Edge searchEdge(String position) {
		char[] charArray = position.toCharArray();
		Arrays.sort(charArray);
		for (Edge edge : this.edges) {
			char[] edgePosition = edge.getEdgeStringPosition().toCharArray();
			Arrays.sort(edgePosition);
			if (Arrays.equals(edgePosition, charArray)) {
				return edge;
			}
		}
		return null;
	}

	/**
	 * Returns the {@code Hexes} on this board.
	 * @return the {@link Hex Hexes}.
	 */
	public ArrayList<Hex> getHexes() {
		return hexes;
	}

	/**
	 * Returns the {@code Robber} on this board.
	 * @return the {@link Robber}.
	 */
	public Robber getRobber() {
		return robber;
	}

	/**
	 * Returns the {@code Edges} on this board.
	 * @return the {@link Edge Edges}.
	 */
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	/**
	 * Returns the {@code Corners} on this board.
	 * @return the {@link Corner Corners}.
	 */
	public ArrayList<Corner> getCorners() {
		return corners;
	}

	/**
	 * Returns the {@code Constructions} on this board.
	 * @return the {@link Construction Constructions}.
	 */
	public List<Construction> getConstructions() {
		return constructionsProperty.get();
	}

	/**
	 * Returns the {@code Harbors} on this board.
	 * @return the {@link Harbor Harbors}.
	 */
	public ArrayList<Harbor> getHarbors() {
		return harbors;
	}

	/**
	 * Replace {@code Settlement} in constructionlist with {@code City} when player build a city on the settlement
	 * @param city the City that is added.
	 */
	public void addCityinBoard(City city) {
		Corner cityPosition = city.getPosition();
		for (Construction construction : getConstructions()) {
			if (construction.getOwner()==city.getOwner()) {
				if(construction instanceof Locality) {
					if(((Locality) construction).getPosition()==cityPosition) {
						//Danke Paula~
						//because before this construction here is a settlement 
						//and i want replace settlement in constructionlist with the city
						getConstructions().remove(construction);
						settlements.remove((Settlement)construction);
						getConstructions().add(city);
						cities.add(city);
						((Locality) construction).getPosition().setLocality(city);
						break;
					}
				}
			}
		}
	}

	/**
	 * Set tokens in board
	 * @see Token
	 * @see Board
	 */
	private void setTokens() {
		for(Hex hex:getHexes()) {
			if(hex.getToken()!=null) {
				this.tokens.add(hex.getToken());
			}
		}

	}

	/**
	 * Get tokens in board
	 * @return tokens
	 */
	public List<Token> getTokens(){
		return tokens;
	}

	/**
	 * Return cities of this board
	 * @return cities
	 */
	public ArrayList<City> getCities(){
		return cities;
	}

	/**
	 * Return settlements of this board
	 * @return settlements
	 */
	public ArrayList<Settlement> getSettlements(){
		return settlements;
	}

	/**
	 * Return roads of this board
	 * @return roads
	 */
	public ArrayList<Road> getRoads(){
		return roads;
	}

	/**
	 * The property wrapping the list of constructions on the board.
	 * @return the constructions list property
	 */
	public SimpleListProperty<Construction> constructionsProperty() {
		return constructionsProperty;
	}

	/**
	 * checks if the corner has a Harbor with the HarborType type
	 * @param corner the corner which we check
	 * @param type the type which the harbor has to have
	 * @return true, if the corner has a harbor with the HarborType type. false, otherwise.
	 */
	public boolean isHarbor(Corner corner, HarborType type) {
		for(Harbor harbor : getHarbors())
			if(harbor.getType() == type){
				for(Edge edge : corner.getAdjacentEdgesOfCorner()){
					if(edge == harbor.getPosition()){
						return true;
					}
				}
			}
		return false;
	}

	/**
	 * checks if the corner has any Harbor
	 * @param corner the Corner that is checked for an harbor.
	 * @return the harbor, if there is one, else null.
	 */
	public Harbor isHarbor(Corner corner) {
		for(Harbor harbor : getHarbors())
			if(harbor.getType() != HarborType.NONE){
				for(Edge edge : corner.getAdjacentEdgesOfCorner()){
					if(edge == harbor.getPosition()){
						return harbor;
					}
				}
			}
		return null;
	}

	/**
	 * Searches the edge with the two given corners on the board.
	 * @param corner1 Corner
	 * @param corner2 Corner
	 * @return the edge, if it exists. null, otherwise
	 */
	public Edge searchEdge(Corner corner1, Corner corner2) {
		for(Edge edge : this.getEdges()){
			if((corner1 == edge.getCorner1() && corner2 == edge.getCorner2()) ||
					(corner1 == edge.getCorner2() && corner2 == edge.getCorner1())){
				return edge;
			}
		}
		return null;
	}
	
	/**
	 * gets the shortest path between startingNode and destinationNode
	 * @param destinationNode the corner we want to reach
	 * @param startingNode the corner we start our search
	 * @param length 
	 * @return an List with the Corners on the path. null, if there is no available path
	 */
	public List<Corner> shortesPath(Corner startingNode, Corner destinationNode, int length) {
	    Map<Corner, Corner> backlinks = new HashMap<Corner, Corner>();
	    Map<Corner, Integer> distanceCorner = new HashMap<Corner, Integer>();
	    Corner currentNode = startingNode;

	    distanceCorner.put(currentNode, 0);
	    Queue<Corner> queue = new LinkedList<Corner>();
	    queue.add(currentNode);

	    int i = 0;
	    while (!queue.isEmpty()) {
	    	if(i > length){
	    		break;
	    	}
	        currentNode = queue.remove();
	        if (currentNode.equals(destinationNode)) {
	            break;
	        } else {
	            for (Corner nextNode : currentNode.getAdjacentCornersOfCorner()) {
	            	if(nextNode.isOnLand()){
	            		Edge edge = this.searchEdge(currentNode, nextNode);

		            	if (!backlinks.containsKey(nextNode) && !nextNode.isCornerOccupied() && !edge.isOccupied()) {
		            		if(!distanceCorner.containsKey((nextNode))){
		            			distanceCorner.put(nextNode, distanceCorner.get(currentNode) + 1);
		            		}
		            		if(distanceCorner.get(nextNode) < length){
			            		backlinks.put(nextNode, currentNode);
			            	    queue.add(nextNode);
		            		}
		            	}
	            	}
	            }
	        }
	        i++;
	    }

	    if (!currentNode.equals(destinationNode)) {
	        return null;
	    }

	    List<Corner> directions = new LinkedList<Corner>();
	    for (Corner node = destinationNode; node != null; node = backlinks.get(node)) {
	        directions.add(node);
	    }

	    //Collections.reverse(directions);
	    return directions;
	}

}
