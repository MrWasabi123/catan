package game.board.construction.localities;

import game.board.construction.Construction;
import game.board.corners.Corner;
import game.player.Player;
import game.resources.Resources;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a locality.
 * @author Yize Sun
 * @author Christoph Hermann
 */
public abstract class Locality extends Construction {

	/**
	 * The {@code Corner} this locality is positioned on.
	 * @see Corner
	 */
	SimpleObjectProperty<Corner> positionProperty = new SimpleObjectProperty<Corner>();

	/**
	 * The amount of victory points a {@code Player} gets for owning this locality.
	 * @see Player
	 */
	final int victoryPoints;

	/**
	 * The multiplier used to determine the amount of {@code Resources} this locality produces.
	 * @see Resources
	 */
	final int resourceMultiplier;

	/**
	 * Creates a new locality belonging to the specified {@code Player} on the given {@code Corner}.
	 * @param player the {@link Player} owning this locality.
	 * @param corner the corner to build the Locality on.
	 * @param victoryPoints the victory points a player gets for owning this locality.
	 * @param resourceMultiplier the multiplier used to determine the amount of {@code Resources} this locality produces.
	 */
	public Locality(Player player, Corner corner, int victoryPoints, int resourceMultiplier) {
		super(player);
		this.victoryPoints = victoryPoints;
		this.resourceMultiplier = resourceMultiplier;
		this.positionProperty.set(corner);
	}

	/**
	 * Returns the {@code Corner} on which this locality is position.
	 * @return the position.
	 * @see Corner
	 */
	public Corner getPosition() {
		return positionProperty.get();
	}

	/**
	 * Returns the amount of victory points a {@code Player} gets for owning this locality. 
	 * @return the victoryPoints.
	 * @see Player
	 */
	public int getVictoryPoints() {
		return victoryPoints;
	}

	/**
	 * Returns the resourceMutiplier.
	 * @return the resourceMutiplier.
	 */
	public int getResourceMultiplier() {
		return resourceMultiplier;
	}

	/**
	 * Getter for the Position Property of the {@code Locality} on the {@code corner}
	 * @return the position property
	 */
	public SimpleObjectProperty<Corner> positionProperty(){
		return positionProperty;
	}

	/**
	 * Sets the {@code Corner} this locality is positioned on.
	 * @param position the {@link Corner} to set.
	 */
	public void setPosition(Corner position) {
		positionProperty.set(position);
	}

}
