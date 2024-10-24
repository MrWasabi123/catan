package game.ai.monopoly;

import java.util.ArrayList;
import game.ai.playertracker.PlayerTracker;
import game.board.harbors.HarborType;
import game.player.Player;
import game.resources.ResourceType;

/**
 * Represents the ai
 * @author Wanja Sajko
 *
 */
public class Monopoly {

	/**
     * This is the resource we want to monopolize
     */
    protected ResourceType monopoly;
    
    /**
     * Decide one {@code ResourceType} the best type by use monopoly card.
     * @param playerTrackers the player trackers
     * @param player the player
     * @return true, if the type of resource is the best type.
     */
	public boolean decideMonopoly(ArrayList<PlayerTracker> playerTrackers, Player player){
		int bestResourceCount = 0;
        ResourceType bestResource = null;

        for (ResourceType reType : ResourceType.values())
        {
            int freeResourceCount = 0;
            boolean twoForOne = false;
            boolean threeForOne = false;

            if (player.hasHarbor(reType.toHarborType()))
            {
                twoForOne = true;
            }
            else if (player.hasHarbor(HarborType.UNIVERSAL))
            {
                threeForOne = true;
            }

            int resourceTotal = 0;

            for (PlayerTracker pt : playerTrackers)
            {
                    resourceTotal += pt.getResourceAmount(reType);
            }

            if (twoForOne)
            {
                freeResourceCount = resourceTotal / 2;
            }
            else if (threeForOne)
            {
                freeResourceCount = resourceTotal / 3;
            }
            else
            {
                freeResourceCount = resourceTotal / 4;
            }

            if (freeResourceCount > bestResourceCount)
            {
                bestResourceCount = freeResourceCount;
                bestResource = reType;
            }
        }

        if (bestResourceCount > 2)
        {
            monopoly = bestResource;

            return true;
        }
        else
        {
            return false;
        }
	}
	
	/**
	 * Checks the resource amount of all players of one type
	 * @param playerTrackers list of all players
	 * @param type type of the resource 
	 * @return resourceTotal the amount of resources of one type of all players
	 */
	public int checkResourceAmount(ArrayList<PlayerTracker> playerTrackers, ResourceType type){
		int resourceTotal = 0;
		for (PlayerTracker pt : playerTrackers)
        {
                resourceTotal += pt.getResourceAmount(type);
        }
		return resourceTotal;
	}

	/** 
	 * Getter for monopoly
     * @return monopoly resource Type that is claimed
     */
	public ResourceType getMonopolyChoice() {
		return monopoly;
	}
	
	/** 
	 * Setter for monopoly
	 * @param monopoly resource Type that is claimed
	 */
	public void setMonopolyChoice(ResourceType monopoly) {
		this.monopoly = monopoly;
	}
}
