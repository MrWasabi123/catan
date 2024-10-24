package game.board.hexes;
/**
 * Represents the different positions an {@code Hex} can have relative to another hex.
 * @author Cornelia Sedlmeir-Hofmann
 * @see Hex
 */
public enum HexPosition {

	/**
	 * Represents the bottom right {@code Hex} of a {@code Hex}.
	 * @see Hex
	 */
	BOTTOM_RIGHT,

	/**
	 * Represents the right {@code Hex} of a {@code Hex}.
	 * @see Hex
	 */
	RIGHT,

	/**
	 * Represents the top right {@code Hex} of a {@code Hex}.
	 * @see Hex
	 */
	TOP_RIGHT,

	/**
	 * Represents the top left {@code Hex} of a {@code Hex}.
	 * @see Hex
	 */
	TOP_LEFT,

	/**
	 * Represents the left {@code Hex} of a {@code Hex}.
	 * @see Hex
	 */
	LEFT,

	/**
	 * Represents the bottom left {@code Hex} of a {@code Hex}.
	 * @see Hex
	 */
	BOTTOM_LEFT;

}
