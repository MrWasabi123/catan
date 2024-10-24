package game.board.hexes;

import game.board.edges.Edge;

/**
 * Represents the different positions an {@code Edge} can have relative to a {@code Hex}.
 * @author Christoph Hermann
 * @see Edge
 * @see Hex
 */
public enum EdgePosition {

	
	/**
	 * Represents the top right {@code Edge} of a {@code Hex}.
	 * @see Edge
	 * @see Hex
	 */
	TOP_RIGHT,

	/**
	 * Represents the right {@code Edge} of a {@code Hex}.
	 * @see Edge
	 * @see Hex
	 */
	RIGHT,

	/**
	 * Represents the bottom right {@code Edge} of a {@code Hex}.
	 * @see Edge
	 * @see Hex
	 */
	BOTTOM_RIGHT,

	/**
	 * Represents the bottom left {@code Edge} of a {@code Hex}.
	 * @see Edge
	 * @see Hex
	 */
	BOTTOM_LEFT,
	

	/**
	 * Represents the left {@code Edge} of a {@code Hex}.
	 * @see Edge
	 * @see Hex
	 */
	LEFT,

	/**
	 * Represents the top left {@code Edge} of a {@code Hex}.
	 * @see Edge
	 * @see Hex
	 */
	TOP_LEFT;

}
