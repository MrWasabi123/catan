package game.board.construction.roads;

import game.board.construction.Construction;
import game.board.edges.Edge;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a Road.
 * @author Yize Sun
 * @author Christoph Hermann
 */
public class Road extends Construction {

	/**
	 * The position of this road.
	 */
	private Edge position;

	/**
	 * The String representation of the Position
	 */
	private String stringPosition;

	/**
	 * The position of the road as a property
	 */
	private SimpleObjectProperty<Edge> positionProperty = new SimpleObjectProperty<Edge>();

	/**
	 * The cost of building a road.
	 */
	public static final Resources cost = new Resources(ResourceType.BRICK, 1, ResourceType.LUMBER, 1);

	/**
	 * Creates a new road belonging to the specified {@code Player} on the specified edge.
	 * @param player the {@link Player} owning this road.
	 * @param edge the {@link Edge} this Road is positioned at.
	 */
	public Road(Player player, Edge edge) {
		super(player);
		this.position = edge;
	}

	/**
	 * Getter for getting the cost of building a road.
	 * @return cost the cost.
	 */
	public static Resources getCost() {
		return cost;
	}

	/**
	 * Returns the {@code Edge} this Road is positioned on.
	 * @return the position.
	 * @see Edge
	 */
	public Edge getPosition() {
		return position;
	}

	/**
	 * Returns the position of the {@code Edge} this road is positioned on as a property.
	 * @return the position.
	 * @see Edge
	 */
	public SimpleObjectProperty<Edge> getPositionProperty(){
		return positionProperty;
	}

	/**
	 * Returns the {@code String} representing the position of this Road.
	 * @param position edge the road is built on
	 * @return the stringPosition of the edge.
	 */
	public String getStringPosition(Edge position) {
		stringPosition=position.getEdgeStringPosition();
		return stringPosition;
	}

}
