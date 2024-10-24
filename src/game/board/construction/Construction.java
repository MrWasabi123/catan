package game.board.construction;

import game.player.Player;

/**
 * Represents any type of construction that a {@code Player} can build.
 * @author Christoph Hermann
 * @see Player
 */
public abstract class Construction {

	/**
	 * The {@code Player} owning this construction.
	 * @see Player
	 */
	final Player owner;

	/**
	 * Creates a new construction owned by the specified {@code Player}.
	 * @param player the {@link Player} owning this construction.
	 */
	public Construction(Player player) {
		owner = player;
	}

	/**
	 * Returns the {@code Player} owning this construction.
	 * @return the owner.
	 * @see Player
	 */
	public Player getOwner() {
		return owner;
	}

}
