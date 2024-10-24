package game.board.hexes;

import game.board.corners.Corner;

/**
 * Represents the different positions a {@code Corner} can have relative to a {@code Hex}.
 * @author Christoph Hermann
 * @see Corner
 * @see Hex
 */
public enum CornerPosition {

	/**
	 * Represents the top {@code Corner} of a {@code Hex}.
	 * @see Corner
	 * @see Hex
	 */
	TOP,

	/**
	 * Represents the top right {@code Corner} of a {@code Hex}.
	 * @see Corner
	 * @see Hex
	 */
	TOP_RIGHT,

	/**
	 * Represents the bottom right {@code Corner} of a {@code Hex}.
	 * @see Corner
	 * @see Hex
	 */
	BOTTOM_RIGHT,

	/**
	 * Represents the bottom {@code Corner} of a {@code Hex}.
	 * @see Corner
	 * @see Hex
	 */
	BOTTOM,

	/**
	 * Represents the bottom left {@code Corner} of a {@code Hex}.
	 * @see Corner
	 * @see Hex
	 */
	BOTTOM_LEFT,

	/**
	 * Represents the top left {@code Corner} of a {@code Hex}.
	 * @see Corner
	 * @see Hex
	 */
	TOP_LEFT;

}
