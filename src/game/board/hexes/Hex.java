package game.board.hexes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import game.board.corners.Corner;
import game.board.edges.Edge;
import game.board.tokens.Token;
import game.resources.Resources;

/**
 * Represents a hexagonal board tile.
 * 
 * @author Cornelia Sedlmeir-Hofmann
 * @author Christoph Hermann
 */
public class Hex {

	/**
	 * Collection of all surrounding hexagones
	 */
	private Map<HexPosition, Hex> hexNeighbor = new HashMap<>();
	/**
	 * Corresponding position of adjacent hex
	 * 
	 */
	private Map<HexPosition, HexPosition> hexMapHex = new HashMap<>();

	/**
	 * List of all corners that make up this hex
	 */
	private ArrayList<Corner> adjCornersOfHex = new ArrayList<>();
	/**
	 * List of all edges that make up this hex
	 */
	private ArrayList<Edge> adjEdgesOfHex = new ArrayList<>();
	/**
	 * List of adjacent hexagones
	 */
	private ArrayList<Hex> adjHexesOfHex = new ArrayList<>();

	/**
	 * Relative coordinate x in an axial view
	 */
	private int xPosAxialHex;

	/**
	 * Relative coordinate x coordinates
	 */
	private double xPosCartesianHex;

	/**
	 * Relative coordinate y in an axial view
	 */
	private int yPosAxialHex;
	
	/**
	 * Relative coordinate y cartesian coordinates
	 */
	private double yPosCartesianHex;
	
	/**
	 * Locality of hex in axial coordinates
	 */
	int[] xyLocality = new int[2];
	
	/**
	 * Quality of the Hex
	 */
	private float hexQuality;
	
	/**
	 * Chip with number on the landscapes.
	 */
	private Token token;
	
	/**
	 * String that defines the location on board of the hex.
	 */
	private String locality;
	
	/**
	 * The type of this hex.
	 * 
	 * @see  HexType
	 */
	private HexType type;

	/**
	 * Get the x value in cartesian coordinates.
	 * 
	 * @return xPosCartesianHex
	 */
	public double getxPosCartesianHex() {
		return xPosCartesianHex;
	}

	/**
	 * Set the x value in cartesian coordinates.
	 * @param xPositionHex the x value in cartesian coordinates xPositionHex 
	 */
	public void setxPosCartesianHex(double xPositionHex) {
		this.xPosCartesianHex = xPositionHex;
	}

	/**
	 * Get the y value in cartesian coordinates.
	 * 
	 * @return yPositionHex value in cartesian coordinates yPosCartesianHex
	 */
	public double getyPosCartesianHex() {
		return yPosCartesianHex;
	}

	/**
	 * Set the y value in cartesian coordinates.
	 * @param yPosCartesianHex value in cartesian coordinates yPosCartesianHex 
	 */
	public void setyPosCartesianHex(double yPosCartesianHex) {
		this.yPosCartesianHex = yPosCartesianHex;
	}

	/**
	 * Getter for {@code locality} of {@code hex}.
	 * 
	 * @return String
	 */
	public String getLocality() {
		return locality;
	}

	/**
	 * Setter for {@code xyLocality} of hex in axial coordinates.
	 * @param 	xPosAxialHex x-Position of hex axial coordinates
	 * @param yPosAxialHex y-Position of hex axial coordinates
	 */
	public void setXyLocality(int xPosAxialHex, int yPosAxialHex) {
		xyLocality[0] = xPosAxialHex;
		xyLocality[1] = yPosAxialHex;
	}

	/**
	 * Getter for locality of hex in axial coordinates.
	 * 
	 * @return locality of hex in axial coordinates xy
	 */
	public int[] getXyLocality() {
		return xyLocality;
	}	

	/**
	 * Setter for {@code locality} of {@code hex}
	 * 
	 * @param locality of a hex
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}

	/**
	 * Getter for type {@link HexType}.
	 * 
	 * @return the Type of Hex type
	 */
	public HexType getType() {
		return type;
	}

	/**
	 * Setter for type {@link HexType}.
	 * 
	 * @param type the Type of Hex 
	 */
	public void setType(HexType type) {
		this.type = type;
	}

	/**
	 * Getter for List of adjacent Corners of the Hex.
	 * 
	 * @return the adjacent Corners adjCornersOfHex
	 */
	public ArrayList<Corner> getAdjCornersOfHex() {
		return adjCornersOfHex;
	}

	/**
	 * Setter for List of adjacent Corners of the Hex.
	 * 
	 * @param adjCornersOfHex the adjacent Corners
	 */
	public void setAdjCornersOfHex(ArrayList<Corner> adjCornersOfHex) {
		this.adjCornersOfHex = adjCornersOfHex;
	}

	/**
	 * Getter for List of adjacent Edges of the Hex.
	 * 
	 * @return the adjacent Edges adjEdgesOfHex
	 */
	public ArrayList<Edge> getAdjEdgesOfHex() {
		return adjEdgesOfHex;
	}

	/**
	 * Setter for List of adjacent Edges of the Hex.
	 * 
	 * @param adjEdgesOfHex all edges of hex
	 */
	public void setAdjEdgesOfHex(ArrayList<Edge> adjEdgesOfHex) {
		this.adjEdgesOfHex = adjEdgesOfHex;
	}

	/**
	 * Getter for List of adjacent Hex of the Hex.
	 * 
	 * @return the adjacent Hex adjHexesOfHex
	 */
	public ArrayList<Hex> getAdjHexesOfHex() {
		return adjHexesOfHex;
	}

	/**
	 * Setter for List of adjacent Hex of the Hex.
	 * 
	 * @param adjHexesOfHex the adjacent Hax 
	 */
	public void setAdjHexesOfHex(ArrayList<Hex> adjHexesOfHex) {
		this.adjHexesOfHex = adjHexesOfHex;
	}

	/**
	 * Getter for x Position of hex in axial coordinates.
	 * 
	 * @return the x Position of the axial Hex xPosAxialHex
	 */
	public int getxPosAxialHex() {
		return xPosAxialHex;
	}

	/**
	 * Setter for x Position of hex in axial coordinates.
	 * @param xPosHex the x-Coordinate of the position of the axial hex 
	 */
	public void setxPosAxialHex(int xPosHex) {
		this.xPosAxialHex = xPosHex;
	}

	/**
	 * Getter for y Position of hex in axial coordinates.
	 * 
	 * @return the y-Coordinate of the Position of the axial hex yPosAxialHex
	 */
	public int getyPosAxialHex() {
		return yPosAxialHex;
	}

	/**
	 * Setter for y Position of hex in axial coordinates.
	 * @param yPosHex the y-Coordinate of the position of this hex
	 */
	public void setyPosHex(int yPosHex) {
		this.yPosAxialHex = yPosHex;
	}

	/**
	 * Returns the {@code Resources} this hex produces.
	 * 
	 * @return the {@link Resources Resources}.
	 */
	public Resources getResources() {
		return type.getResources();
	}
/** Getter for {@link Token}
 * @return token 
 * */
	public Token getToken() {
		return token;
	}
/**Setter for {@link Token}
 * @param token the token on a hex
 * */
	public void setToken(Token token) {
		this.token = token;
	}
/** Getter for hexNeighbor
 * 
 * @return hexNeighbor map position of adjacent hex of a hex
 */
	public Map<HexPosition, Hex> getHexNeighbor() {
		return hexNeighbor;
	}

	/**
	 * ID of hex.
	 */
	private int hexID;

	/**
	 * 
	 */
	private int occupiedCornerCount;
/**Getter for hexID
 * @return hexId
 */
	public int getHexID() {
		return hexID;
	}
	/**Setter for hexID
	 * @param hexID ID of a hex
	 */
	public void setHexID(int hexID) {
		this.hexID = hexID;
	}

	/**
	 * Constructor of hex.
	 * 
	 * @param type of Hex type
	 * @param x x-Coordinate
	 * @param y y-Coordinate
	 */
	public Hex(HexType type, int x, int y) {
		this.xPosAxialHex = x;
		this.yPosAxialHex = y;
		this.type = type;
	}
	
	/**
	 * Gets the player count. 
	 * @return the number of occupied corners
	 */
	public int getOccupiedCornerCount(){
		for (Corner corner : this.getAdjCornersOfHex()){
			if(corner.isCornerOccupied()){
				occupiedCornerCount++;
			}
		}
		return occupiedCornerCount;
	}
	
	/**
	 * Setter for setting adjacentHexes of one Hex.
	 * @param hp Position of this hex relativ to a hex
	 * @param hex this hex
	 * @return the adjacent hexes of this hex
	 */
	public Map<HexPosition, Hex> createHexNeighbor(HexPosition hp, Hex hex) {

		hexNeighbor.put(hp, hex);
		adjHexesOfHex.add(hex);
		return hexNeighbor;
	}
/**map of HexPosition and CornerPosition
 * @return hexCornerMap
 */
	public Map<HexPosition, CornerPosition> hexPositionCornerPosition() {
		Map<HexPosition, CornerPosition> hexCornerMap = new HashMap<>();
		hexCornerMap.put(HexPosition.BOTTOM_LEFT, CornerPosition.BOTTOM_LEFT);
		hexCornerMap.put(HexPosition.BOTTOM_LEFT, CornerPosition.BOTTOM);
		hexCornerMap.put(HexPosition.BOTTOM_RIGHT, CornerPosition.BOTTOM);
		hexCornerMap.put(HexPosition.BOTTOM_RIGHT, CornerPosition.BOTTOM_RIGHT);
		hexCornerMap.put(HexPosition.RIGHT, CornerPosition.BOTTOM_RIGHT);
		hexCornerMap.put(HexPosition.RIGHT, CornerPosition.TOP_RIGHT);
		hexCornerMap.put(HexPosition.TOP_RIGHT, CornerPosition.TOP_RIGHT);
		hexCornerMap.put(HexPosition.TOP_RIGHT, CornerPosition.TOP);
		hexCornerMap.put(HexPosition.TOP_LEFT, CornerPosition.TOP);
		hexCornerMap.put(HexPosition.TOP_LEFT, CornerPosition.TOP_LEFT);
		hexCornerMap.put(HexPosition.LEFT, CornerPosition.TOP_LEFT);
		hexCornerMap.put(HexPosition.LEFT, CornerPosition.BOTTOM_LEFT);
		return hexCornerMap;
	}

	/**
	 * map which position an adjacent hex can have relativ to a hex
	 * 
	 * @return hexMapHex
	 */
	public Map<HexPosition, HexPosition> createMapHexPosition() {

		hexMapHex.put(HexPosition.BOTTOM_LEFT, HexPosition.TOP_RIGHT);
		hexMapHex.put(HexPosition.LEFT, HexPosition.RIGHT);
		hexMapHex.put(HexPosition.TOP_LEFT, HexPosition.BOTTOM_RIGHT);
		hexMapHex.put(HexPosition.TOP_RIGHT, HexPosition.BOTTOM_LEFT);
		hexMapHex.put(HexPosition.RIGHT, HexPosition.LEFT);
		hexMapHex.put(HexPosition.BOTTOM_RIGHT, HexPosition.TOP_LEFT);
		return hexMapHex;
	}

	/**
	 * Getter Method for {@code hexQuality}.
	 * @return hexQuality
	 */
	public float getHexQuality() {
		return hexQuality;
	}

	/**
	 * Sets the {@code hexQuality}.
	 */
	public void setHexQuality() {
		switch (getToken().getNumber()){
		case 0: this.hexQuality = 0f;
			break;
		case 1: this.hexQuality = 0f;
			break;
		case 2: this.hexQuality = (1.0f/36.0f);
			break;
		case 3: this.hexQuality = 2.0f/36.0f;
			break;
		case 4: this.hexQuality = 3.0f/36.0f;
			break;
		case 5: this.hexQuality = 4f/36f;
			break;
		case 6: this.hexQuality = 5f/36f;
			break;
		case 7: this.hexQuality = 6f/36f;
			break;
		case 8: this.hexQuality = 5f/36f;
			break;
		case 9: this.hexQuality = 4f/36f;
			break;
		case 10: this.hexQuality = 3f/36f;
			break;
		case 11: this.hexQuality = 2f/36f;
		break;
		case 12: this.hexQuality = 1f/36f;
		break;
		default: this.hexQuality = 0f;	
		}
	}
	/**
	 * Checks if the {@code Hex} is Land or not.
	 * @return true, if type is not water. false, otherwise
	 */
	public boolean isLand() {
		if(type != HexType.WATER){
			return true;
		}
		return false;
	}
	/**
	 * assigning an Integer to resource type
	 * @return order
	 */
	public int getResourceOrder(){
		int order = -1;
		switch (type){
		case DESERT:
			break;
		case FIELDS: order = 2;
			break;
		case FOREST: order = 0;
			break;
		case HILLS: order = 1;
			break;
		case MOUNTAIN: order = 4;
			break;
		case PASTURE: order = 3;
			break;
		case WATER:
			break;
		default:
			break;
		}
		return order;
	}

}