package game.board;

import java.awt.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import game.board.corners.Corner;
import game.board.edges.Edge;
import game.board.harbors.Harbor;
import game.board.harbors.HarborType;
import game.board.hexes.CornerPosition;
import game.board.hexes.EdgePosition;
import game.board.hexes.Hex;
import game.board.hexes.HexPosition;
import game.board.hexes.HexType;
import game.board.tokens.Token;

/**
 * Initializes the hexes on the game {@code Board}.
 * 
 * @author Cornelia Sedlmeir-Hofmann
 * @author Paula Wikidal
 * @author Christoph Hermann
 * @see Hex
 * @see Board
 */
public class BoardInitialization {
	/**
	 * ArrayList of hexagons
	 */
	private ArrayList<Hex> hexes = new ArrayList<>();
	/**
	 * list of HexTypes
	 */
	private ArrayList<HexType> typeList = new ArrayList<>();
	/**
	 * map associates the vector dependent on the position in the hex
	 */
	private Map<HexPosition, Point> hex_direction;
	/**
	 * type of landscape or water
	 */
	private HexType type;
	/**
	 * list of corners
	 */
	private ArrayList<Corner> corners = new ArrayList<>();
	/**
	 * list of edges
	 */
	private ArrayList<Edge> edges = new ArrayList<>();

	/**
	 * vector to reach the next hex dependent on its position to the hex
	 */
	private ArrayList<Point> vector;
	/**
	 * x position of Hex in axial coordinates, starting with 0
	 */
	private int xPosAxialHex = 0;
	/**
	 * y position of Hex in axial coordinates, starting with 0
	 */
	private int yPosAxialHex = 0;
	/**
	 * vector to add to center of hex to reach the corner dependent on its position
	 * in the hex
	 */
	private ArrayList<Point> vectorC;
	/**
	 * y position of corner in axial coordinates, starting with 0
	 */

	private int yPosAxialCorner = 0;
	/**
	 * x position of corner in axial coordinates, starting with 0
	 */
	private int xPosAxialCorner = 0;
	/**
	 * associates a vector to position of a corner in a hex
	 */
	private Map<CornerPosition, Point> corner_direction = new HashMap<>();
	/**
	 * associates a vector to position of an edge in a hex
	 */
	private Map<EdgePosition, Point> edge_direction = new HashMap<>();
	/**
	 * x position of edge in axial coordinates, starting with 0
	 */
	private int xPosAxialEdge;
	/**
	 * y position of edge in axial coordinates, starting with 0
	 */
	private int yPosAxialEdge;

	/**
	 * list of harbors on the coast tiles
	 */
	private ArrayList<Harbor> harbors = new ArrayList<>();

	/**
	 * BoardInitialization
	 * 
	 */
	public BoardInitialization() {

		typeList = createTypeList();
		vector = createVector();
		hex_direction = createMapHexDirection();
		vectorC = makeVectorC();
		corner_direction = createMapCornerDirection();
		edge_direction = createMapEdgeDirection();

		hexes = createListOfHexagones();
		makeListOfTokens();
		corners = makeListOfCorners();
		edges = makeListOfEdges();
		harbors = makeListOfHarbors();

	}

	/**
	 * make a list of harbors with their special types and distribute them randomly
	 * on the coasts
	 * 
	 * @return harbors
	 */
	public ArrayList<Harbor> makeListOfHarbors() {

		makeHarborList();
		makeListOfCoasts();
		return harbors;
	}

	/**
	 * make a list of harbors 9 special one's and 21 others
	 * 
	 * @return harbors ArrayList of harbors
	 */
	public ArrayList<Harbor> makeHarborList() {

		// Create 9 harbors in total.
		for (HarborType type : HarborType.values()) {
			if (type != HarborType.NONE) {
				harbors.add(new Harbor(type));
			}
		}
		for (int j = 0; j < 3; j++) {
			harbors.add(new Harbor(HarborType.UNIVERSAL));
		}

		// Shuffle the harbors.
		Collections.shuffle(harbors);

		// Space out the harbors so that no two harbors are next to each other.
		for (int i = 9; i >= 0; i--) {
			harbors.add(i, new Harbor(HarborType.NONE));
			harbors.add(i, new Harbor(HarborType.NONE));
		}
		harbors.add(15, new Harbor(HarborType.NONE));

		// Rotate the list of harbors so it starts with a random harbor.
		int randomRotation = new Random().nextInt(harbors.size());
		Collections.rotate(harbors, randomRotation);

		return harbors;
	}

	/**
	 * defines the vectors to add to the hexagons center position to reach the six
	 * neighbors centers
	 * 
	 * @return vector
	 */

	public ArrayList<Point> createVector() {
		ArrayList<Point> vector = new ArrayList<Point>(6);

		vector.add(new Point(+1, +1));
		vector.add(new Point(+1, 0));
		vector.add(new Point(0, -1));
		vector.add(new Point(-1, -1));
		vector.add(new Point(-1, 0));
		vector.add(new Point(0, +1));

		return vector;
	}

	/**
	 * Mapping : which vector to add to center of hexagon to next hexagon
	 * 
	 * @return hex_direction map of positions in the {@code Hex}
	 */
	public Map<HexPosition, Point> createMapHexDirection() {
		Map<HexPosition, Point> hex_direction = new HashMap<HexPosition, Point>();

		hex_direction.put(HexPosition.TOP_RIGHT, vector.get(0));
		hex_direction.put(HexPosition.RIGHT, vector.get(1));
		hex_direction.put(HexPosition.BOTTOM_RIGHT, vector.get(2));
		hex_direction.put(HexPosition.BOTTOM_LEFT, vector.get(3));
		hex_direction.put(HexPosition.LEFT, vector.get(4));
		hex_direction.put(HexPosition.TOP_LEFT, vector.get(5));

		return hex_direction;

	}

	/**
	 * the vector to add to the hexagons center position to reach the six neighbors
	 * centers
	 * 
	 * @return vectorC
	 */

	public ArrayList<Point> makeVectorC() {
		ArrayList<Point> vectorC = new ArrayList<Point>(6);

		vectorC.add(new Point(-1, +1));
		vectorC.add(new Point(0, +1));
		vectorC.add(new Point(+1, 0));
		vectorC.add(new Point(+1, -1));
		vectorC.add(new Point(0, -1));
		vectorC.add(new Point(-1, 0));

		return vectorC;
	}

	/**
	 * associates vector to position of corner in hex
	 * 
	 * @return corner_direction
	 */
	public Map<CornerPosition, Point> createMapCornerDirection() {
		corner_direction.put(CornerPosition.TOP, vectorC.get(0));
		corner_direction.put(CornerPosition.TOP_RIGHT, vectorC.get(1));
		corner_direction.put(CornerPosition.BOTTOM_RIGHT, vectorC.get(2));
		corner_direction.put(CornerPosition.BOTTOM, vectorC.get(3));
		corner_direction.put(CornerPosition.BOTTOM_LEFT, vectorC.get(4));
		corner_direction.put(CornerPosition.TOP_LEFT, vectorC.get(5));

		return corner_direction;
	}

	/**
	 * Mapping : which vector to add to center of hexagon to edge
	 * 
	 * @return edge_direction
	 */
	public Map<EdgePosition, Point> createMapEdgeDirection() {
		Map<EdgePosition, Point> edge_direction = new HashMap<EdgePosition, Point>();

		edge_direction.put(EdgePosition.TOP_RIGHT, vector.get(0));
		edge_direction.put(EdgePosition.RIGHT, vector.get(1));
		edge_direction.put(EdgePosition.BOTTOM_RIGHT, vector.get(2));
		edge_direction.put(EdgePosition.BOTTOM_LEFT, vector.get(3));
		edge_direction.put(EdgePosition.LEFT, vector.get(4));
		edge_direction.put(EdgePosition.TOP_LEFT, vector.get(5));

		return edge_direction;

	}

	/**
	 * create list of ({@code HexType} all available landscapes, shuffle, add desert
	 * and water
	 * 
	 * @return typeList
	 */
	public ArrayList<HexType> createTypeList() {

		// land types 4 times available
		for (int i = 0; i <= 3; i++) {
			type = HexType.FOREST;
			typeList.add(type);
			type = HexType.FIELDS;
			typeList.add(type);
			type = HexType.PASTURE;
			typeList.add(type);
		}

		// land types 3 times available
		for (int i = 0; i <= 2; i++) {
			type = HexType.HILLS;
			typeList.add(type);
			type = HexType.MOUNTAIN;
			typeList.add(type);
		}
		// Collections.shuffle(typeList); the desert will always be in the middle
		type = HexType.DESERT;
		typeList.add(0, type);
		Collections.shuffle(typeList);// Desert will be somewhere

		for (int i = 0; i <= 17; i++) {
			type = HexType.WATER;
			typeList.add(type);

		}
		return typeList;
	}

	/**
	 * creating new hexagones counterclockwise axial coordinates first hexagon
	 * position 0,0 and assigning a type
	 * 
	 * @see #hex_direction
	 * @see #giveHexID()
	 * @see #changeHexCoordinateSystem()
	 * @see #makeHexNeighbor()
	 * @see #makeLocality()
	 * @return hexes list of hexes
	 */
	public ArrayList<Hex> createListOfHexagones() {

		int t = 0;

		Hex hex = new Hex(typeList.get(t), 0, 0);

		hexes.add(hex);

		// first step to an outer ring: TOP_LEFT, no hex made
		// going in a spiral around the inner ring

		for (int i = 1; i <= Board.NUMBER_OF_CIRCLES; i++) {

			xPosAxialHex += hex_direction.get(HexPosition.TOP_LEFT).x;
			yPosAxialHex += hex_direction.get(HexPosition.TOP_LEFT).y;

			for (int j = 0; j < i; j++) {
				t = t + 1;
				xPosAxialHex += hex_direction.get(HexPosition.BOTTOM_LEFT).x;
				yPosAxialHex += hex_direction.get(HexPosition.BOTTOM_LEFT).y;
				hex = new Hex(typeList.get(t), xPosAxialHex, yPosAxialHex);
				hex.setType(typeList.get(t));
				hexes.add(hex);

			}
			for (int j = 0; j < i; j++) {
				t = t + 1;
				xPosAxialHex += hex_direction.get(HexPosition.BOTTOM_RIGHT).x;
				yPosAxialHex += hex_direction.get(HexPosition.BOTTOM_RIGHT).y;
				hex = new Hex(typeList.get(t), xPosAxialHex, yPosAxialHex);

				hexes.add(hex);

			}
			for (int j = 0; j < i; j++) {
				t = t + 1;
				xPosAxialHex += hex_direction.get(HexPosition.RIGHT).x;
				yPosAxialHex += hex_direction.get(HexPosition.RIGHT).y;
				hex = new Hex(typeList.get(t), xPosAxialHex, yPosAxialHex);

				hexes.add(hex);

			}
			for (int j = 0; j < i; j++) {
				t = t + 1;
				xPosAxialHex += hex_direction.get(HexPosition.TOP_RIGHT).x;
				yPosAxialHex += hex_direction.get(HexPosition.TOP_RIGHT).y;
				hex = new Hex(typeList.get(t), xPosAxialHex, yPosAxialHex);

				hexes.add(hex);

			}
			for (int j = 0; j < i; j++) {
				t = t + 1;
				xPosAxialHex += hex_direction.get(HexPosition.TOP_LEFT).x;
				yPosAxialHex += hex_direction.get(HexPosition.TOP_LEFT).y;
				hex = new Hex(typeList.get(t), xPosAxialHex, yPosAxialHex);

				hexes.add(hex);

			}
			for (int j = 0; j < i; j++) {
				t = t + 1;
				xPosAxialHex += hex_direction.get(HexPosition.LEFT).x;
				yPosAxialHex += hex_direction.get(HexPosition.LEFT).y;
				hex = new Hex(typeList.get(t), xPosAxialHex, yPosAxialHex);

				hexes.add(hex);

			}
		}
		giveHexID();
		changeHexCoordinateSystem();
		makeHexNeighbor();
		makeLocality();

		return hexes;
	}

	/**
	 * each hex gets a number in the order of its creation
	 */
	private void giveHexID() {
		for (int i = 0; i < hexes.size(); i++) {
			hexes.get(i).setHexID(i);
		}

	}

	/**
	 * calculate cartesian coordinates of hexes from axial
	 * 
	 */
	public void changeHexCoordinateSystem() {
		for (int i = 0; i < hexes.size(); i++) {
			double xPosCartesianHex = 2 * hexes.get(i).getxPosAxialHex() * Board.DISTANCE
					- hexes.get(i).getyPosAxialHex() * Board.DISTANCE;
			hexes.get(i).setxPosCartesianHex(xPosCartesianHex);
			double yPosCartesianHex = -hexes.get(i).getyPosAxialHex() * Board.RADIUS * 3 / 2.0;
			hexes.get(i).setyPosCartesianHex(yPosCartesianHex);
		}
	}

	/**
	 * makes 6 corners for each hexagon axial coordinates different from those of
	 * hexes!! Remove doubles.
	 * 
	 * @see #makeAdjListCornersOfHex()
	 * @see #makeAdjListHexOfCorner()
	 * @see #makeAdjListCornersOfCorner()
	 * 
	 * @return corners
	 */
	public ArrayList<Corner> makeListOfCorners() {

		for (int i = 0; i < hexes.size(); i++) {
			if (hexes.get(i).getType() != HexType.WATER) {
				for (CornerPosition cornerPosition : CornerPosition.values()) {
					xPosAxialCorner = corner_direction.get(cornerPosition).x;
					yPosAxialCorner = corner_direction.get(cornerPosition).y;
					Corner corner = new Corner(hexes.get(i).getxPosCartesianHex(), hexes.get(i).getyPosCartesianHex(),
							xPosAxialCorner, yPosAxialCorner);

					corners.add(corner);
				}
			}

		}

		makeAdjListHexOfCorner();
		makeAdjListCornersOfHex();
		makeAdjListCornersOfCorner();
		return corners;
	}

	/**
	 * make 6 edges for each hexagon
	 * 
	 * @see #makeAdjListHexesOfEdge()
	 * @see #makeAdjListCornersOfEdge()
	 * @see #makeAdjListEdgesOfCorner()
	 * @see #makeAdjListEdgesOfHex()
	 * @see #makeAdjListEdgesOfEdge()
	 * 
	 * @return edges
	 */
	public ArrayList<Edge> makeListOfEdges() {
		for (int i = 0; i < hexes.size(); i++) {

			if (hexes.get(i).getType() != HexType.WATER) {
				for (EdgePosition edgePosition : EdgePosition.values()) {
					// xPosAxialEdge = hex_direction.get(hexPosition).x;
					// yPosAxialEdge = hex_direction.get(hexPosition).y;
					xPosAxialEdge = edge_direction.get(edgePosition).x;
					yPosAxialEdge = edge_direction.get(edgePosition).y;
					Edge edge = new Edge(hexes.get(i).getxPosCartesianHex(), hexes.get(i).getyPosCartesianHex(),
							xPosAxialEdge, yPosAxialEdge, edgePosition);
					// System.out.println("i "+i+" x "+edge.getxPosAxialEdge()+"
					// "+edge.getxPosCartesianEdge()+" y "+edge.getyPosAxialEdge()+"
					// "+edge.getyPosCartesianEdge());
					edges.add(edge);

				}
			}
		}

		makeAdjListHexesOfEdge();
		makeAdjListCornersOfEdge();
		makeAdjListEdgesOfCorner();
		makeAdjListEdgesOfHex();
		makeAdjListEdgesOfEdge();
		return edges;
	}

	/**
	 * Getter for list of edges
	 * 
	 * @return edges
	 */
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	/**
	 * Method to find all adjacent hexes
	 */
	public void makeHexNeighbor() {
		for (int i = 0; i < hexes.size(); i++) {
			for (HexPosition hp : HexPosition.values()) {
				for (int j = 0; j < hexes.size(); j++) {
					if (hexes.get(i).getxPosAxialHex() + hex_direction.get(hp).x == hexes.get(j).getxPosAxialHex()
							&& hexes.get(i).getyPosAxialHex() + hex_direction.get(hp).y == hexes.get(j)
									.getyPosAxialHex()) {

						hexes.get(i).createHexNeighbor(hp, hexes.get(j));
					}
				}
			}
		}

	}

	/**
	 * make list of adjacent hexes for each corner all those hexes with distance of
	 * center to corner equal or smaller than Board.RADIUS
	 * 
	 * filter cornerList remove all those with same localities determine string
	 * representation of corner position
	 */
	public void makeAdjListHexOfCorner() {

		for (int i = 0; i < corners.size(); i++) {
			for (int j = 0; j < hexes.size(); j++) {
				if ((corners.get(i).getxPosCartesianCorner() - Board.RADIUS <= hexes.get(j).getxPosCartesianHex()
						&& corners.get(i).getxPosCartesianCorner() + Board.RADIUS >= hexes.get(j).getxPosCartesianHex())
						&& (corners.get(i).getyPosCartesianCorner() - Board.RADIUS <= hexes.get(j).getyPosCartesianHex()
								&& corners.get(i).getyPosCartesianCorner() + Board.RADIUS >= hexes.get(j)
										.getyPosCartesianHex())) {

					corners.get(i).getAdjacentHexesOfCorner().add(hexes.get(j));

				}
			}

		}

		for (int i = 0; i < corners.size(); i++) {
			ArrayList<String> liste1 = new ArrayList<String>();
			for (int k = 0; k < 3; k++) {
				liste1.add(corners.get(i).getAdjacentHexesOfCorner().get(k).getLocality());
			}
			Collections.sort(liste1);

			for (int j = 0; j < corners.size(); j++) {

				if (i != j) {
					ArrayList<String> liste2 = new ArrayList<String>();
					for (int k = 0; k < 3; k++) {
						liste2.add(corners.get(j).getAdjacentHexesOfCorner().get(k).getLocality());
					}
					Collections.sort(liste2);

					if (liste1.equals(liste2)) {
						corners.remove(corners.get(j));
					}
				}

			}
		}
		/**
		 * String representation of corner position
		 */
		for (int i = 0; i < corners.size(); i++) {
			String s = "";
			for (int k = 0; k < 3; k++) {
				s = s + (corners.get(i).getAdjacentHexesOfCorner().get(k).getLocality());

			}
			corners.get(i).setCornerLocality(s);
		}

	}

	/**
	 * make list of all {@code Hex} connecting to an edge filter edges list remove
	 * all those with same localities determine the string representation of edge
	 * position
	 */
	public void makeAdjListHexesOfEdge() {
		for (int i = 0; i < edges.size(); i++) {
			for (int j = 0; j < hexes.size(); j++) {
				if ((edges.get(i).getxPosCartesianEdge() - Board.RADIUS <= hexes.get(j).getxPosCartesianHex()
						&& edges.get(i).getxPosCartesianEdge() + Board.RADIUS >= hexes.get(j).getxPosCartesianHex())
						&& (edges.get(i).getyPosCartesianEdge() - Board.RADIUS <= hexes.get(j).getyPosCartesianHex()
								&& edges.get(i).getyPosCartesianEdge() + Board.RADIUS >= hexes.get(j)
										.getyPosCartesianHex())) {

					edges.get(i).getAdjacentHexesOfEdge().add(hexes.get(j));

				}

			}
			// edges.get(i).makeMapAdjHexes();
		}

		for (int i = 0; i < edges.size(); i++) {
			ArrayList<String> liste1 = new ArrayList<String>();
			for (int k = 0; k < 2; k++) {
				liste1.add(edges.get(i).getAdjacentHexesOfEdge().get(k).getLocality());
			}
			Collections.sort(liste1);

			for (int j = 0; j < edges.size(); j++) {

				if (i != j) {
					ArrayList<String> liste2 = new ArrayList<String>();
					for (int k = 0; k < 2; k++) {
						liste2.add(edges.get(j).getAdjacentHexesOfEdge().get(k).getLocality());
					}
					Collections.sort(liste2);
					//

					if (liste1.equals(liste2)) {

						edges.remove(edges.get(j));
					}

				}

			}

		}
		/**
		 * String representation of Edges position
		 */
		for (int i = 0; i < edges.size(); i++) {
			String s = "";
			for (int k = 0; k < 2; k++) {
				s = s + (edges.get(i).getAdjacentHexesOfEdge().get(k).getLocality());

			}
			edges.get(i).setEdgeStringPosition(s);
		}

	}

	/**
	 * make list of adjacent corners for each edge maximum distance corner from
	 * edge's center Board.distance
	 */

	public void makeAdjListCornersOfEdge() {
		for (int i = 0; i < edges.size(); i++) {
			for (int j = 0; j < corners.size(); j++) {
				if ((edges.get(i).getxPosCartesianEdge() - Board.DISTANCE <= corners.get(j).getxPosCartesianCorner()
						&& edges.get(i).getxPosCartesianEdge() + Board.DISTANCE >= corners.get(j)
								.getxPosCartesianCorner())
						&& (edges.get(i).getyPosCartesianEdge() - Board.DISTANCE <= corners.get(j)
								.getyPosCartesianCorner()
								&& edges.get(i).getyPosCartesianEdge() + Board.DISTANCE >= corners.get(j)
										.getyPosCartesianCorner())) {
					edges.get(i).getAdjacentCornersOfEdge().add(corners.get(j));

				}

			}

		}
	}

	/**
	 * make list of adjacent corners for each hex distance {@code board.RADIUS}
	 */
	public void makeAdjListCornersOfHex() {
		for (int i = 0; i < hexes.size(); i++) {
			if (hexes.get(i).getType() != HexType.WATER) {
				for (int j = 0; j < corners.size(); j++) {
					if (((hexes.get(i).getxPosCartesianHex() - Board.RADIUS <= corners.get(j).getxPosCartesianCorner())
							&& (hexes.get(i).getxPosCartesianHex() + Board.RADIUS >= corners.get(j)
									.getxPosCartesianCorner()))
							&& ((hexes.get(i).getyPosCartesianHex() - Board.RADIUS <= corners.get(j)
									.getyPosCartesianCorner())
									&& (hexes.get(i).getyPosCartesianHex() + Board.RADIUS >= corners.get(j)
											.getyPosCartesianCorner()))) {
						hexes.get(i).getAdjCornersOfHex().add(corners.get(j));
					}
				}
			}
		}
	}

	/**
	 * make list of adjacent corners for each corner distance corner from corner
	 * {@code #Board.RADIUS}
	 */
	public void makeAdjListCornersOfCorner() {
		for (int i = 0; i < corners.size(); i++) {
			for (int j = 0; j < corners.size(); j++) {
				if ((corners.get(i).getxPosCartesianCorner() - Board.RADIUS <= corners.get(j).getxPosCartesianCorner()
						&& corners.get(i).getxPosCartesianCorner() + Board.RADIUS >= corners.get(j)
								.getxPosCartesianCorner())
						&& (corners.get(i).getyPosCartesianCorner() - Board.RADIUS <= corners.get(j)
								.getyPosCartesianCorner()
								&& corners.get(i).getyPosCartesianCorner() + Board.RADIUS >= corners.get(j)
										.getyPosCartesianCorner())
						&& (i != j)) {
					corners.get(i).getAdjacentCornersOfCorner().add(corners.get(j));

				}

			}

		}
	}

	/**
	 * make list of adjacent edges for each corner, distance edge'center from corner
	 * {@code #Board.distance}
	 */
	public void makeAdjListEdgesOfCorner() {
		for (int i = 0; i < corners.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				if ((corners.get(i).getxPosCartesianCorner() - Board.DISTANCE <= edges.get(j).getxPosCartesianEdge())
						&& (corners.get(i).getxPosCartesianCorner() + Board.DISTANCE >= edges.get(j)
								.getxPosCartesianEdge())
						&& (corners.get(i).getyPosCartesianCorner() - Board.DISTANCE <= edges.get(j)
								.getyPosCartesianEdge())
						&& (corners.get(i).getyPosCartesianCorner() + Board.DISTANCE >= edges.get(j)
								.getyPosCartesianEdge())) {
					corners.get(i).getAdjacentEdgesOfCorner().add(edges.get(j));
				}
			}
		}
	}

	/**
	 * make list of adjacent edges for each hex, distance edge'center to hex center
	 * {@code #Board.RADIUS}
	 */
	public void makeAdjListEdgesOfHex() {
		for (int i = 0; i < hexes.size(); i++) {
			if (hexes.get(i).getType() != HexType.WATER) {
				for (int j = 0; j < edges.size(); j++) {
					if (((hexes.get(i).getxPosCartesianHex() - Board.RADIUS <= edges.get(j).getxPosCartesianEdge())
							&& (hexes.get(i).getxPosCartesianHex() + Board.RADIUS >= edges.get(j)
									.getxPosCartesianEdge()))
							&& ((hexes.get(i).getyPosCartesianHex() - Board.RADIUS <= edges.get(j)
									.getyPosCartesianEdge())
									&& (hexes.get(i).getyPosCartesianHex() + Board.RADIUS >= edges.get(j)
											.getyPosCartesianEdge()))) {
						hexes.get(i).getAdjEdgesOfHex().add(edges.get(j));
					}
				}
			}
		}
	}

	/**
	 * make list of adjacent edges for each edge, distance edge'center to hex center
	 * {@code #Board.RADIUS}
	 */
	public void makeAdjListEdgesOfEdge() {
		for (int i = 0; i < edges.size(); i++) {
			for (int j = 0; j < edges.size(); j++) {
				if ((edges.get(i).getxPosCartesianEdge() - Board.RADIUS <= edges.get(j).getxPosCartesianEdge()
						&& edges.get(i).getxPosCartesianEdge() + Board.RADIUS >= edges.get(j).getxPosCartesianEdge())
						&& (edges.get(i).getyPosCartesianEdge() - Board.RADIUS <= edges.get(j).getyPosCartesianEdge()
								&& edges.get(i).getyPosCartesianEdge() + Board.RADIUS >= edges.get(j)
										.getyPosCartesianEdge())) {
					edges.get(i).getAdjacentEdgesOfEdge().add(edges.get(j));

				}

			}

		}
	}

	/**
	 * assigning characters to the landscapes according the customers wishes String
	 * representation of {@code Hex}
	 */
	public void makeLocality() {
		ArrayList<String> location3 = new ArrayList<String>();
		ArrayList<String> location2 = new ArrayList<String>();

		for (int i = 0; i < 19; i++) {
			char c = (char) (i + 65);
			String s = "" + c;
			location3.add(s);
			Comparator<String> comparator = Collections.reverseOrder();
			Collections.sort(location3, comparator);
		}

		for (int j = 0; j < 10; j += 2) {
			char c = (char) (j + 101);
			String s = "" + c;
			location2.add(s);
		}
		for (int j = 0; j < 4; j++) {
			char c = (char) (j + 111);
			String s = "" + c;
			location2.add(s);
		}
		for (int j = 10; j > 0; j -= 2) {
			char c = (char) (j + 100);
			String s = "" + c;
			location2.add(s);
		}
		for (int j = 4; j > 0; j--) {
			char c = (char) (j + 96);
			String s = "" + c;
			location2.add(s);
		}
		ArrayList<String> location = new ArrayList<String>(location3);
		location.addAll(location2);

		for (int i = 0; i < hexes.size(); i++) {
			hexes.get(i).setLocality(location.get(i));

		}

	}

	/**
	 * list of all edges bordering on WATER the only places for harbors distributing
	 * the harbors randomly
	 * 
	 * @return coasts the list of coasts.
	 */
	private List<Edge> makeListOfCoasts() {
		List<Edge> coasts = findAllCoasts();
		assignHarborsToCoasts(coasts);

		return coasts;
	}

	/**
	 * Assigns the {@code Harbors} to the coasts.
	 * 
	 * @param coasts
	 *            the coasts which the {@link Harbor Harbors} get assigned to.
	 */
	private void assignHarborsToCoasts(List<Edge> coasts) {
		Edge coast = coasts.get(0);
		List<Edge> visitedCoasts = new ArrayList<>();

		for (int i = 0; i < harbors.size(); i++) {
			// Assign the harbor to the coast and vice versa.
			coast.setHarbor(harbors.get(i));
			harbors.get(i).setPosition(coast);

			// Mark the coast as visited.
			visitedCoasts.add(coast);

			// Find the unvisited, adjacent coast.
			for (Edge neighbor : coast.getAdjacentEdgesOfEdge()) {
				if (!visitedCoasts.contains(neighbor)) {
					coast = neighbor;
					continue;
				}
			}
		}
	}

	/**
	 * Finds all coasts on the {@code Board}. Coasts are all {@link Edge Edges}
	 * which boarder on exactly one water {@link Hex}.
	 * 
	 * @return the list of all coasts.
	 * @see Board
	 * @see HexType
	 */
	private List<Edge> findAllCoasts() {
		List<Edge> coasts = new ArrayList<>();

		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).getAdjacentHexesOfEdge().get(0).getType() == HexType.WATER
					^ edges.get(i).getAdjacentHexesOfEdge().get(1).getType() == HexType.WATER) {
				coasts.add(edges.get(i));
			}

		}

		return coasts;
	}

	/**
	 * make list of tokens corresponding number and {@code locality} assigning token
	 * to {@code Hex} so if the dice shows the number, associated hexes are
	 * addressed
	 * 
	 * @return tokens
	 */

	public ArrayList<Token> makeListOfTokens() {

		ArrayList<Token> tokens = new ArrayList<>();
		// ArrayList<Integer> number = new ArrayList<>();
		int index = getDesert();
		/** List of numbers shown on the tokens */
		Integer[] numbers = new Integer[] { 5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11 };
		List<Integer> number = new ArrayList<Integer>(Arrays.asList(numbers));
		number.add(index, 0);

		/**
		 * assign numbers to characters
		 */
		for (int i = 0; i < number.size(); i++) {
			// with desert not in the middle the streaming method doesn't work properly
			// String locality = "" + (char) (i + 65);
			// List<Hex> charHex = hexes.stream().filter(s ->
			// s.getLocality().equals(locality))
			// .collect(Collectors.toList());
			Hex charHex = hexes.get(i);
			Token token = new Token((int) number.get(i), charHex);
			// Token token = new Token(numbers[i], charHex.get(0));
			tokens.add(token);
			charHex.setToken(token);

		}

		return tokens;
	}

	/**
	 * search hex with type DESERT
	 * 
	 * @return hex
	 */
	public int getDesert() {
		int index = 0;
		for (Hex hex : hexes) {
			if (hex.getType().equals(HexType.DESERT)) {
				index = hexes.indexOf(hex);
			}
		}
		return index;
	}

	/**
	 * Getter for hexes
	 * 
	 * @return hexes arrayList of hexes
	 */
	public ArrayList<Hex> getHexes() {
		return hexes;
	}

	/**
	 * Getter for corners
	 * 
	 * @return corners arrayList of corners
	 */
	public ArrayList<Corner> getCorners() {
		return corners;
	}

	/**
	 * Getter for harbors
	 * 
	 * @return harbors arrayList of harbors
	 */
	public ArrayList<Harbor> getHarbors() {
		return harbors;
	}

}
