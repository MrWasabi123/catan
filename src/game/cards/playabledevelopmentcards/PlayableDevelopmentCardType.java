package game.cards.playabledevelopmentcards;

/**
 * Represents the different types of {@code PlayableDevelopmentCards}.
 * @author Christoph Hermann
 * @see PlayableDevelopmentCard
 */
public enum PlayableDevelopmentCardType {

	/**
	 * Represents a type of development card which functions as a knight card.
	 */
	KNIGHT("Ritter"),

	/**
	 * Represents a type of development card which functions as a monopoly card.
	 */
	MONOPOLY("Monopol"),

	/**
	 * Represents a type of development card which functions as a road building card.
	 */
	ROAD_BUILDING("Strassenbau"),

	/**
	 * Represents a type of development card which functions as a year of plenty card.
	 */
	YEAR_OF_PLENTY("Erfindung");
	
	/**
	 * The German string representation of the developmentCardType
	 */
	private String string;
	
	/**
	 * Creates a new PlayableDevelopmentCardType
	 * @param string The German string representation of the developmentCardType.
	 */
	private PlayableDevelopmentCardType(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

}
