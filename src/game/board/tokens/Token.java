package game.board.tokens;

import game.board.hexes.Hex;

/**
 * Represents a token on top of a {@code Hex}.
 * @author Cornelia Sedlmeir-Hofmann
 * @see Hex
 */
public class Token {

	/**
	 * The value of this token.
	 */
	private int number;

	/**
	 * The x-position of this token.
	 */
	private double xPosCartesianToken;

	/**
	 * The y-position of this token.
	 */
	private double yPosCartesianToken;

	/**
	 * The {@code Hex} this token is on top of.
	 * @see Hex
	 */
	private Hex hex;

	/**
	 * Creates a new token on top of the specified {@code Hex} and with the specified value and locality.
	 * @param number the value.
	 * @param hex the {@link Hex}.
	 */
	public Token(int number, Hex hex) {
		this.number = number;
		this.hex = hex;
		this.xPosCartesianToken = hex.getxPosCartesianHex();
		this.yPosCartesianToken = hex.getyPosCartesianHex();
	}

	/**
	 * Returns the x-position of this token.
	 * @return the x-position.
	 */
	public double getxPosCartesianToken() {
		return xPosCartesianToken;
	}

	/**
	 * Returns the y-position of this token.
	 * @return the y-position.
	 */
	public double getyPosCartesianToken() {
		return yPosCartesianToken;
	}

	/**
	 * Returns the value of this token.
	 * @return the value.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns the {@code Hex} this token is on top of.
	 * @return the {@link Hex}.
	 */
	public Hex getHex() {
		return hex;
	}

}
