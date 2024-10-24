package game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import game.bank.Bank;
import game.player.Player;
import game.resources.ResourceType;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

/**
 * Represents the ratios at which a {@code Player} can trade resources with the {@code Bank}.
 * @author Christoph Hermann
 * @see Player
 * @see Bank
 */
public class TradingRatios {

	/**
	 * A {@link Map Map} representing the different types of resources and the ratio at which they can be traded.
	 */
	private SimpleMapProperty<ResourceType, Integer> ratios = 
			new SimpleMapProperty<ResourceType, Integer>(
					FXCollections.observableMap(new HashMap<ResourceType, Integer>()));

	/**
	 * Creates a new TradingRatios allowing a {@code Player} to trade the specified {@code ResourceType} at the
	 * specified ratio.
	 * @param type the {@link ResourceType}.
	 * @param ratio the ratio.
	 * @see Player
	 */
	public TradingRatios(ResourceType type, int ratio) {
		ratios.put(type, ratio);
	}

	/**
	 * Creates a new TradingRatios allowing a {@code Player} to trade the specified {@code ResourceType ResourceTypes}
	 * at the specified ratios.
	 * @param type the first {@link ResourceType}.
	 * @param ratio the first ratio.
	 * @param type2 the first {@link ResourceType}.
	 * @param ratio2 the first ratio.
	 * @param type3 the first {@link ResourceType}.
	 * @param ratio3 the first ratio.
	 * @param type4 the first {@link ResourceType}.
	 * @param ratio4 the first ratio.
	 * @param type5 the first {@link ResourceType}.
	 * @param ratio5 the first ratio.
	 */
	public TradingRatios(
			ResourceType type, int ratio,
			ResourceType type2, int ratio2,
			ResourceType type3, int ratio3,
			ResourceType type4, int ratio4,
			ResourceType type5, int ratio5) {
		ratios.put(type, ratio);
		ratios.put(type2, ratio2);
		ratios.put(type3, ratio3);
		ratios.put(type4, ratio4);
		ratios.put(type5, ratio5);
	}

	/**
	 * Combines this TradingRatios with a second one. If the second ratio is better than the original, it will override
	 * it. This is done for each individual {@link ResourceType ResourceType}.
	 * @param newRatios the second trading ratios.
	 */
	public void combine(TradingRatios newRatios) {
		for (Entry<ResourceType, Integer> entry: newRatios.getRatios().entrySet()) {
			ResourceType type = entry.getKey();
			int newRatio = entry.getValue();
			int oldRatio = ratios.get(type);

			if (newRatio < oldRatio) {
				ratios.put(type, newRatio);
			}
		}
	}

	/**
	 * Checks if the specified {@code ResourceType} can be traded with the specified ratio or not.
	 * @param type the {@link ResourceType}.
	 * @param ratioToTest the ratio.
	 * @return true, if the ResourceType can be traded with the specified ratio. false, otherwise.
	 */
	public boolean isCorrectRatio(ResourceType type, int ratioToTest) {
		if (ratios.containsKey(type)) {
			int thisRatio = ratios.getValue().get(type);
			return ratioToTest % thisRatio == 0;
		} else {
			return false;
		}
	}

	/**
	 * Returns a {@code Map} representing the different types of resources and the ratio at which they can be traded.
	 * @return the ratios.
	 */
	public SimpleMapProperty<ResourceType, Integer> getRatios() {
		return ratios;
	}

}
