package game.board.robber;

import game.board.hexes.Hex;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents the robber.
 * @author svenja
 * @author Christoph Hermann
 */
public class Robber {

	/**
	 * The {@code Hex} this robber is positioned on as a property
	 */
	private SimpleObjectProperty<Hex> positionProperty = new SimpleObjectProperty<Hex>();

	/**
	 * The previous position of the Robber. Needed for reverting the position 
	 * in case the server doesn't confirm the request to move the Robber.
	 */
	private Hex lastValidPosition;

	/**
	 * Constructor for the Robber
	 * @param hex The Hex the Robber is placed on
	 */
	public Robber(Hex hex) {
		this.positionProperty.set(hex);
		this.lastValidPosition = hex;
	}

	/**
	 * Moves this robber to the specified {@code Hex}.
	 * @param hex the new position.
	 * @see Hex
	 */
	public void move(Hex hex) {
		this.lastValidPosition = hex;
		this.positionProperty.set(hex);
	}

	/**
	 * Moves the robber back to its previous position.
	 */
	public void resetPosition() {
		this.positionProperty.set(lastValidPosition);
	}

	/**
	 * Returns the {@code Hex} this robber is currently positioned on.
	 * @return the position.
	 * @see Hex
	 */
	public Hex getPosition() {
		return positionProperty.get();
	}

	/**
	 * Returns the position of the {@code Hex} this robber is currently positioned on as a property.
	 * @return the position
	 */
	public SimpleObjectProperty<Hex> getPositionProperty() {
		return positionProperty;
	}

}
