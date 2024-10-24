package game.ai;

/**
 * The phases the ai can be in
 * @author Wanja Sajko
 *
 */
public enum AiPhases {
	/**
	 * The phase to choose two settlements and roads
	 */
	BEGINNING_PHASE,
	
	/**
	 * The Phase to build the second road
	 */
	SECOND_ROAD_PHASE,

	/**
	 * The phase to choose new corners and edges to build
	 */
	EXPANDING_PHASE,

	/**
	 * The phase to build cities and roads 
	 */
	CITY_ROAD_PHASE;
}
