package game.board.edges;

import java.util.ArrayList;

import game.board.Board;
import game.board.construction.roads.Road;
import game.board.corners.Corner;
import game.board.harbors.Harbor;
import game.board.harbors.HarborType;
import game.board.hexes.EdgePosition;
import game.board.hexes.Hex;
import game.player.Player;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents the edge of a {@code Hex}.
 * 
 * @author Christoph Hermann
 * @author Cornelia Sedlmeir-Hofmann
 * @see Hex
 */
public class Edge {
/**
 * road as SompleObjectProperty
 */
	private SimpleObjectProperty<Road> roadProperty = new SimpleObjectProperty<Road>();
	/**
	 * new harbor of type NONE
	 */
	private Harbor harbor = new Harbor(HarborType.NONE);
	/**
	 *  Position of an edge relative to a hex
	 */
	private EdgePosition edgePosition;
	/**
	 * String representation of edge position
	 */
	private String edgeStringPosition;
	/**
	 * list of adjacent hexes of an edge
	 */
	private ArrayList<Hex> adjacentHexesOfEdge = new ArrayList<>();
	/**
	 * list of adjacent corners of an edge
	 */
	private ArrayList<Corner> adjacentCornersOfEdge = new ArrayList<>();
	/**
	 * list of adjacent edges of an edge
	 */
	private ArrayList<Edge> adjacentEdgesOfEdge = new ArrayList<>();
	/**
	 * x-Position of an edge in axial coordinates
	 */
	private int xPosAxialEdge;
	/**
	 * y-Position of an edge in axial coordinates
	 */
	private int yPosAxialEdge;
	/**
	 * x-Position of an edge in cartesian coordinates
	 */
	private double xPosCartesianEdge;
	/**
	 * y-Position of an edge in cartesian coordinates
	 */
	private double yPosCartesianEdge;
	
	
	/**Constructor
	 * @param edgePosition Position of an edge 
	 */
	public Edge(EdgePosition edgePosition) {
		this.edgePosition = edgePosition;
	}

	/**
	 * Creates a new edge with the specified positions.
	 * 
	 * @param xPosCartesianHex x-Cartesian Coordinate of the position 
	 * @param yPosCartesianHex y-Cartesian Coordinate of the position 
	 * @param xPosAxialEdge x-Axial Coordinate of the position 
	 * @param yPosAxialEdge y-Axial Coordinate of the position 
	 * @param edgePosition position of the edge  
	 */
	public Edge(double xPosCartesianHex, double yPosCartesianHex, int xPosAxialEdge, int yPosAxialEdge, EdgePosition edgePosition) {
		this.xPosAxialEdge = xPosAxialEdge;
		this.yPosAxialEdge = yPosAxialEdge;
		this.edgePosition = edgePosition;

		xPosCartesianEdge = xPosCartesianHex + xPosAxialEdge * Board.DISTANCE - yPosAxialEdge * Board.DISTANCE / 2.0;

		yPosCartesianEdge = yPosCartesianHex - yPosAxialEdge * 3 / 4.0 * Board.RADIUS;

	}
	
	/**
	 * Checks if the player has a road on the adjacent edges of this edge.
	 * @param player The Player whose roads are checked.
	 * @return true if there is a road in the adjacent edges, false otherwise.
	 */
	public boolean hasRoadInAdjacentEdges(Player player) {
		boolean adjRoad = false;
		for(Edge adjEdge: getAdjacentEdgesOfEdge()) {
			boolean ownRoadHere = false;
			if(adjEdge.getRoad()!=null) {
				ownRoadHere = adjEdge.getRoad().getOwner()==player;
			}
			adjRoad = adjRoad || ownRoadHere;
		}
		return adjRoad;
	};

//	/**
//	 * Map xy-position of adjacent hexes
//	 */
//	public Map<positionHex, Integer> makeMapAdjHexes() {
//		Map<positionHex, Integer> position = new HashMap<positionHex, Integer>();
//		position.put(positionHex.X1, adjacentHexesOfEdge.get(0).getxPosAxialHex());
//		position.put(positionHex.Y1, adjacentHexesOfEdge.get(0).getyPosAxialHex());
//		position.put(positionHex.X2, adjacentHexesOfEdge.get(1).getxPosAxialHex());
//		position.put(positionHex.Y2, adjacentHexesOfEdge.get(1).getyPosAxialHex());
//		return position;
//	}

	/**
	 * Getter for the x position of edge in axial coordinates
	 * 
	 * @return xPosAxialEdge
	 */
	public int getxPosAxialEdge() {
		return xPosAxialEdge;
	}

	/**
	 * Getter for the y position of edge in axial coordinates
	 * 
	 * @return yPosAxialEdge
	 */
	public int getyPosAxialEdge() {
		return yPosAxialEdge;
	}

	/**
	 * Getter of String representation of adjacent hexes
	 * 
	 * @return edgeLocality
	 */
	public String getEdgeStringPosition() {
		return edgeStringPosition;
	}

	/**
	 * Setter of String representation of adjacent hexes
	 * 
	 * @param edgeStringPosition the new String representation of this edge.
	 */
	public void setEdgeStringPosition(String edgeStringPosition) {
		this.edgeStringPosition = edgeStringPosition;
	}

	/**
	 * Get the position of this {@code Edge}
	 * 
	 * @return EdgePosition {@code EdgePosition}
	 */
	public EdgePosition getEdgePosition() {
		return edgePosition;
	}

	/**
	 * Getter hexagons of one Edge
	 * 
	 * @return adjacentHexesOfEdge he
	 */

	public ArrayList<Hex> getAdjacentHexesOfEdge() {
		return adjacentHexesOfEdge;
	}

	/**
	 * Setter for setting hexagons of one Edge
	 * 
	 * @param adjacentHexesOfEdge
	 *            hexagons of this edge
	 */
	public void setAdjacentHexesOfEdge(ArrayList<Hex> adjacentHexesOfEdge) {
		this.adjacentHexesOfEdge = adjacentHexesOfEdge;

	}

	/**
	 * Getter for adjacent Corners of one Edge
	 * 
	 * @return adjacentCornersOfEdge corners of this edge
	 */
	public ArrayList<Corner> getAdjacentCornersOfEdge() {
		return adjacentCornersOfEdge;
	}

	/**
	 * Setter for setting adjacentCorners of one {@code Edge}
	 * 
	 * @param adjacentCornersOfEdge
	 *            corners of this edge
	 */
	public void setAdjacentCornersOfEdge(ArrayList<Corner> adjacentCornersOfEdge) {
		this.adjacentCornersOfEdge = adjacentCornersOfEdge;
	}

	/**
	 * Getter for adjacent Edges of {@code Edge}
	 * 
	 * @return adjacentEdgesOfEdge edges of this edge
	 */

	public ArrayList<Edge> getAdjacentEdgesOfEdge() {
		return adjacentEdgesOfEdge;
	}

	/**
	 * Setter for adjacent Edges of {@code Edge}
	 * 
	 * @param adjacentEdgesOfEdge
	 *            edges of this edge
	 */
	public void setAdjacentEdgesOfEdge(ArrayList<Edge> adjacentEdgesOfEdge) {
		this.adjacentEdgesOfEdge = adjacentEdgesOfEdge;
	}

	/**
	 * Get the first {@code Corner} of this {@code Edge}
	 * 
	 * @return corner the corner of one side of this Edge
	 */
	public Corner getCorner1() {
		return adjacentCornersOfEdge.get(0);
	}

	/**
	 * getter for x position of Edge center in cartesian coordinates
	 * 
	 * @return xPosCartesianEdge
	 */
	public double getxPosCartesianEdge() {
		return xPosCartesianEdge;
	}

	/**
	 * getter for y position of Edge center in cartesian coordinates
	 * 
	 * @return yPosCartesianEdge
	 */
	public double getyPosCartesianEdge() {
		return yPosCartesianEdge;
	}

	/**
	 * Get the second {@code Corner} of this {@code Edge}
	 * 
	 * @return corner the corner of the other side of this Edge
	 */
	public Corner getCorner2() {
		return adjacentCornersOfEdge.get(1);
	}

	/**
	 * Getter for the {@code Road}
	 * 
	 * @return The {@code Road} has been builded on this edge
	 */
	public Road getRoad() {
		return roadProperty.get();
	}

	/**
	 * Set one road on this {@code Edge}
	 * 
	 * @param road the new {@link Road} on this edge.
	 */
	public void setRoad(Road road) {
		roadProperty.set(road);
	}

	/**
	 * Getter for the road property
	 * 
	 * @return the road property wrapping the road built on this edge
	 */
	public SimpleObjectProperty<Road> roadProperty() {
		return roadProperty;
	}

	/**
	 * Get {@code Harbor} of this {@code Edge}
	 * 
	 * @return harbor
	 */
	public Harbor getHarbor() {
		return harbor;
	}

	/**
	 * Set one Harbor of this {@code Edge}
	 * 
	 * @param harbor the new {@link Harbor} on this edge.
	 */
	public void setHarbor(Harbor harbor) {
		this.harbor = harbor;
	}

	/**
	 * Check if one road at this {@code Edge}
	 * 
	 * @return true if one road on this edge
	 */
	public boolean isOccupied() {
		return getRoad() != null;
	}

}