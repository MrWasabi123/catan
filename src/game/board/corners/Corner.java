package game.board.corners;

import java.util.ArrayList;

import game.board.Board;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import game.board.construction.localities.Locality;
import game.board.edges.Edge;
import game.board.hexes.CornerPosition;
import game.board.hexes.Hex;
import game.board.hexes.HexType;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents the corner of a {@code Hex}
 * 
 * @author Cornelia Sedlmeir-Hofmann
 * @author Christoph Hermann
 * @author Yize Sun
 * @see game.board.hexes.Hex
 */
public class Corner {

	/**
	 * The {@code Locality} build on this corner.
	 * 
	 * @see Locality
	 */
	private SimpleObjectProperty<Locality> localityProperty = new SimpleObjectProperty<Locality>();

	/**
	 * The x position of this corner in axial coordinates.
	 */
	private int xPosAxialCorner = 0;

	/**
	 * The y position of this corner in axial coordinates.
	 */
	private int yPosAxialCorner = 0;

	/**
	 * The x position of this corner in cartesian coordinates.
	 */
	private double xPosCartesianCorner = 0.0;

	/**
	 * The y position of this corner in cartesian coordinates.
	 */
	private double yPosCartesianCorner = 0.0;

	/**
	 * The position this corner has relative to the {@code Hex} it belongs to.
	 * @see Hex
	 */
	private CornerPosition cornerPosition;

	/**
	 * Whether a {@code Locality} is built on this corner or not.
	 * @see Locality
	 */
	private boolean isCornerOccupied;

	/**
	 * String representation of adjacent hexes
	 */
	private String cornerLocality;

	/**
	 * The list of adjacent {@code Edges} of this corner.
	 * @see Edge
	 */
	private ArrayList<Edge> adjacentEdgesOfCorner = new ArrayList<>();

	/**
	 * The list of adjacent {@code Hexes} of this corner.
	 * @see Hex
	 */
	private ArrayList<Hex> adjacentHexesOfCorner = new ArrayList<>();

	/**
	 * The list of adjacent corners of this corner.
	 */
	private ArrayList<Corner> adjacentCornersOfCorner = new ArrayList<>();

	/**
	 * Creates a new corner with the specified positions.
	 * @param xPosCartesianHex x position of hex in cartesian coordinates
	 * @param yPosCartesianHex y position of hex in cartesian coordinates
	 * @param xPosAxialCorner x position of hex in axial coordinates
	 * @param yPosAxialCorner y position of hex in axial coordinates
	 */
	public Corner(double xPosCartesianHex, double yPosCartesianHex, int xPosAxialCorner, int yPosAxialCorner) {
		this.xPosAxialCorner = xPosAxialCorner;
		this.yPosAxialCorner = yPosAxialCorner;

		xPosCartesianCorner = xPosCartesianHex + xPosAxialCorner * Board.DISTANCE + yPosAxialCorner * Board.DISTANCE;

		yPosCartesianCorner = yPosCartesianHex + xPosAxialCorner * Board.RADIUS / 2.0
				- yPosAxialCorner * Board.RADIUS / 2.0;
	}

	/**
	 * Check if corner is occupied by one locality
	 * @return true if this corner is occupied by a locality
	 */
	public boolean isCornerOccupied() {
		if(getLocality()!=null) {
			this.isCornerOccupied=true;
			return this.isCornerOccupied;}
		else {
			this.isCornerOccupied=false;
			return this.isCornerOccupied;
		}
	}
	
	
	/**
	 * checks if the corner no has occupied edges
	 * @return boolean value
	 */
	public boolean hasNoRoads() {
		for(Edge edge : this.getAdjacentEdgesOfCorner()){
			if(edge.isOccupied()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * checks if the corner has only one occupied edge
	 * @param e {{Edge}} that is being occupied
	 * @return true or false
	 */
	public boolean hasOneRoads(Edge e) {
		if(e.isOccupied()){
			for(Edge edge : this.getAdjacentEdgesOfCorner()){
				if(edge.isOccupied() && edge != e){
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * checks if one or more of the adjacent corners are occupied
	 * @return true or false
	 */
	public boolean isAdjacentCornerOccupied() {
		for(Corner corner : this.getAdjacentCornersOfCorner()){
			if(corner.isCornerOccupied){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks if the Corner has an adjacent empty edge
	 * @return true, if one of the adjacent edges of the corner is unoccupied. false, otherwise.
	 */
	public boolean hasCornerEmptyEdge() {
		for(Edge edge : this.getAdjacentEdgesOfCorner()){
			if(!edge.isOccupied()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks it the corner is next to a Hex with the given HexType
	 * @param hextype the type of the given hex 
	 * @return true, if an adjacent hex has the hextype
	 */
	public boolean isNextToHex(HexType hextype){
		for(Hex h : this.getAdjacentHexesOfCorner()){
			if(h.getType() == hextype){
				return true;
			}
		}
		return false;
	}

	/**
	 * Getter for x position of corner in axial coordinates
	 * @return the axial x coordinate of the position of the corner xPosAxialCorner
	 */
	public int getxPosAxialCorner() {
		return xPosAxialCorner;
	}

	/**
	 * Setter for x position of corner in axial coordinates
	 * @param xPosAxialCorner position of corner in axial coordinates 
	 */
	public void setxPosAxialCorner(int xPosAxialCorner) {
		this.xPosAxialCorner = xPosAxialCorner;
	}

	/**
	 * Getter for y position of corner in axial coordinates
	 * @return y position of corner in axial coordinates yPosAxialCorner
	 */
	public int getyPosAxialCorner() {
		return yPosAxialCorner;
	}

	/**
	 * Setter for y position of corner in axial coordinates
	 * @param yPosAxialCorner position of corner in axial coordinates 
	 */
	public void setyPosAxialCorner(int yPosAxialCorner) {
		this.yPosAxialCorner = yPosAxialCorner;
	}

	/**
	 * Getter for x position of corner in cartesian coordinates
	 * @return x position of corner in cartesian coordinates xPosCartesianCorner
	 */
	public double getxPosCartesianCorner() {
		return xPosCartesianCorner;
	}

	/**
	 * Setter for x position of corner in axial coordinates
	 * @param xPosCartesianCorner position of corner in cartesian coordinates
	 */
	public void setxPosCartesianCorner(double xPosCartesianCorner) {
		this.xPosCartesianCorner = xPosCartesianCorner;
	}

	/**
	 * Getter for y position of corner in axial coordinates
	 * @return y position of corner in axial coordinates yPosAxialCorner
	 */
	public double getyPosCartesianCorner() {
		return yPosCartesianCorner;
	}

	/**
	 * Setter for y position of corner in cartesian coordinates
	 * @param yPosCartesianCorner position of corner in cartesian coordinates
	 */
	public void setyPosCartesianCorner(double yPosCartesianCorner) {
		this.yPosCartesianCorner = yPosCartesianCorner;
	}

	/**
	 * Getter for String representation of corner position
	 * @return Locality of the corner cornerLocality
	 */

	public String getCornerLocality() {
		return cornerLocality;
	}

	/**
	 * Setter for String representation of corner position
	 * @param cornerLocality Locality of the corner 
	 */
	public void setCornerLocality(String cornerLocality) {
		this.cornerLocality = cornerLocality;
	}

	/**
	 * Getter for the adjacent {@code Edges} of the corner
	 * @return the adjacent {@code Edges} of the corner adjacentEdgesOfCorner
	 */
	public ArrayList<Edge> getAdjacentEdgesOfCorner() {
		return adjacentEdgesOfCorner;
	}

	/**
	 * Setter for the adjacent {@code Edges} of the corner
	 * @param adjacentEdgesOfCorner the adjacent  Edges of the corner
	 */
	public void setAdjacentEdgesOfCorner(ArrayList<Edge> adjacentEdgesOfCorner) {
		this.adjacentEdgesOfCorner = adjacentEdgesOfCorner;
	}

	/**
	 * Getter for the adjacent {@code hex} of the corner
	 * @return the adjacent {@code hex} of the corner adjacentHexesOfCorner
	 */
	public ArrayList<Hex> getAdjacentHexesOfCorner() {
		return adjacentHexesOfCorner;
	}

	/**
	 * Getter for the adjacent {@code Hex} of the corner
	 * @param adjacentHexesOfCorner the adjacent {@code Hex} of the corner 
	 */
	public void setAdjacentHexesOfCorner(ArrayList<Hex> adjacentHexesOfCorner) {
		this.adjacentHexesOfCorner = adjacentHexesOfCorner;
	}

	/**
	 * Getter for the corners position
	 * @return the {@code CornerPosition} 
	 */
	public CornerPosition getCornerPosition() {
		return cornerPosition;
	}

	/**
	 * Setter for the corners position
	 * @param cornerPosition the position of corner in {@code Hex} 
	 */
	public void setCornerPosition(CornerPosition cornerPosition) {
		this.cornerPosition = cornerPosition;
	}

	/**
	 * Returns the {@code Locality} build on this corner.
	 * 
	 * @return the {@link Locality}.
	 */
	public Locality getLocality() {
		return localityProperty.get();
	}
	/**
	 * Locality as SimpleObjectProperty
	 * 
	 * @return the {@link Locality}.
	 */
	public SimpleObjectProperty<Locality> localityProperty() {
		return localityProperty;
	}

	/**
	 * Sets the {@code Locality} build on this corner.
	 * 
	 * @param locality the {@link Locality}.
	 */
	public void setLocality(Locality locality) {
		this.localityProperty.set(locality);
	}

	/**
	 * Getter for the adjacent corners of the corner
	 * @return the list of adjacent corners
	 */
	public ArrayList<Corner> getAdjacentCornersOfCorner() {
		return adjacentCornersOfCorner;
	}

	/**
	 * checks if this Corner is next to at least one hex with a token
	 * @return true, if this corner is on Land. false, otherwise
	 */
	public boolean isOnLand() {
		for(Hex hex : this.getAdjacentHexesOfCorner()){
			if(hex.isLand()){
				return true;
			}
		}
		return false;
	}

}
