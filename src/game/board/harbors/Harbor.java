package game.board.harbors;

import game.board.edges.Edge;

/**
 * Represents a harbor.
 * @author Christoph Hermann
 */
public class Harbor {

	/**
	 * The {@code Edge} on which this harbor is positioned.
	 * @see Edge
	 */
	private Edge position;

	/**
	 * The type of this harbor.
	 * @see HarborType
	 */
	private HarborType type;

	/**
	 * Creates a new Harbor of the specified type.
	 * @param type the {@link HarborType}.
	 */
	public Harbor(HarborType type) {
		this.type = type;
	}

	/**
	 * Returns the {@code Edge} on which this harbor is positioned.
	 * @return the the {@link Edge}.
	 * @see Edge
	 */
	public Edge getPosition() {
		return position;
	}

	/**
	 * Sets the {@code Edge} on which this harbor is positioned.
	 * @param position the {@link Edge}.
	 */
	public void setPosition(Edge position) {
		this.position = position;
	}

	/**
	 * Returns the type of this harbor.
	 * @return the {@link HarborType}.
	 */
	public HarborType getType() {
		return type;
	}

	/**
	 * Set the type of this harbor.
	 * @param harborType the {@link HarborType}.
	 */
	public void setType(HarborType harborType) {
		this.type = harborType;
	}

}
