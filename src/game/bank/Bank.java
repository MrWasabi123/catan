package game.bank;

import java.util.Collections;

import game.Game;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.cards.victorypointcards.VictoryPointCard;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a game bank managing all {@code Resources} and {@code DevelopmentCards} not owned by {@code Players}.
 * @author Christoph Hermann
 * @see Resources
 * @see DevelopmentCard
 * @see Player
 */
public class Bank {

	/**
	 * The amount of {@code Resources} of each type that this bank 
	 * has at the start of the {@code Game}.
	 * @see Resources
	 * @see Game
	 */
	private final static int N_RESOURCES = 19;

	/**
	 * The amount of knight cards that this bank has at the start 
	 * of the {@code Game}.
	 * @see PlayableDevelopmentCard
	 * @see Game
	 */
	private final static int N_KNIGHT_CARDS = 14;

	/**
	 * The amount of progress cards of each type that this bank 
	 * has at the start of the {@code Game}.
	 * @see PlayableDevelopmentCard
	 * @see Game
	 */
	private final static int N_PROGRESS_CARDS = 2;

	/**
	 * The amount of {@code VictoryPointCards} that this bank 
	 * has at the start of the {@code Game}.
	 * @see VictoryPointCard
	 * @see Game
	 */
	private final static int N_VICTORY_POINT_CARDS = 5;

	/**
	 * The {@code Resources} property in this {@code Bank}.
	 * @see Resources
	 */
	private SimpleObjectProperty<Resources> resources = new SimpleObjectProperty<>();

	/**
	 * The {@code DevelopmentCards} in this bank.
	 * @see DevelopmentCard
	 */
	private ObservableList<DevelopmentCard> developmentCards = FXCollections.observableArrayList();

	/**
	 * Creates a new Bank containing all {@code Resources} and 
	 * {@code DevelopmentCards} not owned by {@code Players}.
	 * @see Resources
	 * @see DevelopmentCard
	 * @see Player
	 */
	public Bank() {
		resources.set(new Resources(
				ResourceType.BRICK, N_RESOURCES,
				ResourceType.GRAIN, N_RESOURCES,
				ResourceType.LUMBER, N_RESOURCES,
				ResourceType.ORE, N_RESOURCES,
				ResourceType.WOOL, N_RESOURCES));

		createAllDevelopmentCards();
		shuffleDevelopmentCards();
	}

	/**
	 * Creates all {@code DevelopmentCards} this bank starts with.
	 */
	private void createAllDevelopmentCards() {
		// Add knight cards.
		for (int i=0; i<N_KNIGHT_CARDS; i++) {
			DevelopmentCard card = new PlayableDevelopmentCard(PlayableDevelopmentCardType.KNIGHT);
			developmentCards.add(card);
		}

		// Add progress cards.
		for(int i=0; i<N_PROGRESS_CARDS; i++) {
			DevelopmentCard card = new PlayableDevelopmentCard(PlayableDevelopmentCardType.MONOPOLY);
			developmentCards.add(card);

			card = new PlayableDevelopmentCard(PlayableDevelopmentCardType.ROAD_BUILDING);
			developmentCards.add(card);

			card = new PlayableDevelopmentCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY);
			developmentCards.add(card);
		}

		// Add victory point cards.
		for(int i=0; i<N_VICTORY_POINT_CARDS; i++) {
			VictoryPointCard victoryCard = new VictoryPointCard();
			developmentCards.add(victoryCard);
		}
	}

	/**
	 * Removes the specified {@code Resources} from this bank.
	 * @param resourcesToRemove the {@link Resources Resources}.
	 */
	public void removeResources(Resources resourcesToRemove) {
		if (resources.get().isGreaterThanOrEqualTo(resourcesToRemove)) {
			resources.get().subtract(resourcesToRemove);
		}
	}

	/**
	 * Adds the specified {@code Resources} to this bank.
	 * @param resourcesToAdd the {@link Resources Resources}.
	 */
	public void addResources(Resources resourcesToAdd) {
		resources.get().add(resourcesToAdd);
	}

	/**
	 * Draws the first card from the deck of {@code DevelopmentCards} within this bank.
	 * @return the top card of the deck of {@link DevelopmentCard DevelopmentCards}.
	 */
	public DevelopmentCard drawDevelopmentCard() {
		DevelopmentCard TopCard = developmentCards.get(developmentCards.size()-1);
		developmentCards.remove(TopCard);

		return TopCard;
	}

	/**
	 * Returns the amount of {@code Resources} of the specified type which are still in this bank.
	 * @param type the {@link ResourceType}.
	 * @return the amount of {@code Resources}.
	 */
	public int getResourceAmount(ResourceType type) {
		return resources.get().getResources().get(type);
	}

	/**
	 * Shuffles the deck of {@code DevelopmentCards} within this bank.
	 * @see DevelopmentCard
	 */
	private void shuffleDevelopmentCards() {
		Collections.shuffle(developmentCards);
	}

	/**
	 * Get the {@code Resources} of this {@code Bank}.
	 * @return {@code Resources}
	 */
	public Resources getResources() {
		return resources.get();
	}

	/**
	 * Returns the developmentCards.
	 * @return the developmentCards.
	 */
	public ObservableList<DevelopmentCard> getDevelopmentCards() {
		return developmentCards;
	}

	/**
	 * checks if the requested amount of resources is still in the bank
	 * @param comparedResources resources to compare
	 * @return true or false
	 */
	public boolean hasResources(Resources comparedResources) {
		return getResources().isGreaterThanOrEqualTo(comparedResources);
	}

}
