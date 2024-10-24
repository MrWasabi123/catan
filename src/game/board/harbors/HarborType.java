package game.board.harbors;

import game.resources.*;
import game.TradingRatios;

/**
 * Represents the different types of harbors.
 * @author Yize Sun
 * @author Christoph Hermann
 */
public enum HarborType {

	/**
	 * Represents the absence of a harbor.
	 */
	NONE(new TradingRatios(ResourceType.WOOL, 4, ResourceType.BRICK, 4, ResourceType.GRAIN, 4, ResourceType.LUMBER, 4,
			ResourceType.ORE,4), ""),

	/**
	 * Represents a type of harbor which functions as a 3:1 harbor.
	 */
	UNIVERSAL(new TradingRatios(ResourceType.WOOL, 3, ResourceType.BRICK, 3, ResourceType.GRAIN, 3, ResourceType.LUMBER,
			3, ResourceType.ORE, 3), "Hafen"),

	/**
	 * Represents a type of harbor which functions as a wool harbor.
	 */
	WOOL(new TradingRatios(ResourceType.WOOL, 2), "Wolle Hafen"),

	/**
	 * Represents a type of harbor which functions as a lumber harbor.
	 */
	LUMBER(new TradingRatios(ResourceType.LUMBER, 2), "Holz Hafen"),

	/**
	 * Represents a type of harbor which functions as a ore harbor.
	 */
	ORE(new TradingRatios(ResourceType.ORE, 2), "Erz Hafen"),

	/**
	 * Represents a type of harbor which functions as a brick harbor.
	 */
	BRICK(new TradingRatios(ResourceType.BRICK, 2), "Lehm Hafen"),

	/**
	 * Represents a type of harbor which functions as a grain harbor.
	 */
	GRAIN(new TradingRatios(ResourceType.GRAIN, 2), "Getreide Hafen");

	/**
	 * The ratios at which certain resources can be traded.
	 */
	private TradingRatios tradingRatios;

	/**
	 * The string representation of this type.
	 */
	private final String string;

	/**
	 * Creates a new HarborType.
	 * @param tradingRatio the {@link TradingRatios} of this type of harbor.
	 * @param string the string representation of this type.
	 */
	private HarborType(TradingRatios tradingRatio, String string) {
		this.tradingRatios = tradingRatio;
		this.string = string;
	}

	/**
	 * Getter for getting TradingRatios of this HarborType.
	 * @return tradingRatios the TradingRatios of this HarborType.
	 */
	public TradingRatios getTradingRatio() {
		return tradingRatios;
	}

	@Override
	public String toString() {
		return string;
	}

}
