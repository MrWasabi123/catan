package game.ai.playertracker;

import java.util.ArrayList;

import game.Game;
import game.board.construction.localities.City;
import game.board.construction.localities.Locality;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.harbors.HarborType;
import game.board.hexes.Hex;
import game.cards.DevelopmentCard;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import utility.Utility;

/**
 * stores the Resources of a Player and counts the as the game goes on.
 * if it is not clear how much Resources the player loses/gains, then this class will create as many Resource object as there are possibilities
 * @author Wanja Sajko
 *
 */
public class PlayerTracker {

	/**
	 * the {@code Player} the {@code Ai} is tracking
	 */
	private Player player;
	
	/**
	 * the arrayList of Resources with all possible variations the player could have
	 */
	private ArrayList<Resources> resources = new ArrayList<Resources>();
	
	/**
	 * the arrayList of Resources with all possible variations with a specific amount of resources
	 */
	private ArrayList<Resources> subtractResources = new ArrayList<Resources>();
	
	/**
	 * the arrayList of Resources with all possible variations of one resource
	 */
	private ArrayList<Resources> subtractResourcesOne = new ArrayList<Resources>();

	/**
	 * the array with the needed Resources.
	 * array[0] = "Lumber";
	 * array[1] = "Wool";
	 * array[2] = "Grain";
	 * array[3] = "Brick";
	 * array[4] = "Ore";
	 */
	private int[] neededResources;
	
	/**
	 * the resourceType that will be lost/gained
	 */
	private ResourceType monopoly;

	/**
	 * a flag so the player Tracker knows the player loses resources and the message from the server was because of a monopoly card
	 */
	private boolean loseResources;

	/**
	 * a flag so the player Tracker knows the player gains resources and the message from the server was because of a monopoly card
	 */
	private boolean winResources;

	/**
	 * the amount something has cost
	 */
	private int cost;

	/**
	 * the Resources something has cost
	 */
	private Resources costResources;
	
	/**
	 * the game all players are playing on
	 */
	private Game game;
	
	/**
	 * a copy of the arrayList resources
	 */
	private ArrayList<Resources> copyList;
	/**
	 * Constructor which sets the players resources to 0 for each {@code ResourceType}
	 * @param player the specific player
	 * @param game the game on which all players are playing
	 */
	public PlayerTracker(Player player, Game game){
		this.player = player;
		this.game = game;
		setAllNewResources(1);
		this.subtractResourcesOne = new ArrayList<Resources>(this.subtractResources);
		this.subtractResources = new ArrayList<Resources>();
		this.resources.add(new Resources(ResourceType.LUMBER, 0,
				ResourceType.WOOL, 0,
				ResourceType.GRAIN, 0,
				ResourceType.BRICK, 0,
				ResourceType.ORE, 0));
	}
	
	/**
	 * adds resources to the player.
	 * if the player has a {@code Settlement} at a {@code Hex} with the number diceValue, add one to the corresponding resource.
	 * if the player has a {@code City} at a {@code Hex} with the number diceValue, add two to the corresponding resource. 
	 * @param diceValue the value of the dice
	 */
	public void updateResources(int diceValue){
		if(this.resources.size() > 1000){
			resetResources();
		}
		copyList = new ArrayList<Resources>(resources);
		if(!resources.isEmpty()){
			if(diceValue != 7){
				for(Locality loc : player.getLocalities()){
					for(Hex hex : loc.getPosition().getAdjacentHexesOfCorner()){
						if(loc instanceof Settlement && hex.getToken().getNumber() == diceValue && game.getBoard().getRobber().getPosition() != hex){
							updateResources(new Resources(hex.getType().toResourceType(), 1));
						}else if(loc instanceof City && hex.getToken().getNumber() == diceValue && game.getBoard().getRobber().getPosition() != hex){
							updateResources(new Resources(hex.getType().toResourceType(), 2));
						}
					}
				}
			}else{
				for(Resources re : copyList){
					if(re.getSum() > 7){
						setAllNewResources(re.getSum()/2);
						resources.remove(re);
						for(Resources subResources : subtractResources){
							Resources newResources = re.subtractResources(subResources);
							if(Utility.min(newResources.convertResources()) >= 0 && !alreadyInArrayList(resources, newResources)){
								resources.add(newResources);
							}
						}
					}
				}
			}
		}else{
			resetResources();
			//increaseResources(player.getResourceQuantity());
		}
	}

	/**
	 * create an ArrayList with all possible variations to split amount resources on 4 resourceTypes
	 * the given type is ignored.
	 * @param amount the amount that gets split up over the 4 resourceTypes
	 * @param type the ignored resourceType
	 */
	private void setAllNewResources(int amount, ResourceType type) {
		int index = type.convertToInt();
		int[] subResources = new int[5];
		for(int i=0; i<subResources.length; i++){
			for(int j=0; j<amount; j++){
				for(int l=0; l <subResources.length; l++){
					if(l!=i && l!=index){
						setNull(subResources);
						if(j!=0){
							subResources[i] = amount-j;
							subResources[l] = j;
							if(!alreadyInArrayList(subtractResources, Resources.convertIntToResources(subResources))){
								subtractResources.add(Resources.convertIntToResources(subResources));
							}
						}else{
							subResources[i] = amount;
							if(!alreadyInArrayList(subtractResources, Resources.convertIntToResources(subResources))){
								subtractResources.add(Resources.convertIntToResources(subResources));
							}
							break;
						}
					}
				}
				
			}
		}
	}

	/**
	 * uses the subtract method in Resources to decrease the resources of the tracked {@code Player}
	 * @see Resources
	 * @param subResources the Resources that are subtracted 
	 */
	public void decreaseResources(Resources subResources){
		if(player.getResourceQuantity() == 0){
			resetResources();
		}else{
			copyList = new ArrayList<Resources>(this.resources);
			for(Resources pResources : copyList){
				pResources.subtract(subResources);
				if(Utility.min(pResources.convertResources()) < 0){
					resources.remove(pResources);
				}
			}
		}
	}
	
	/**
	 * uses the subtractResources method in Resources to decrease the resources of the tracked {@code Player}
	 * if the exact resourceType is unknown, the playerTracker creates new Resource objects with all possibilities
	 * @param amount the amount of resources the player loses
	 */
	public void decreaseResources(int amount){
		if(amount != 1){
			setAllNewResources(amount);
			copyList = new ArrayList<Resources>(this.resources);
			for(Resources pResources : copyList){
				if(!Utility.allNull(pResources.convertResources())){
					resources.remove(pResources);
					for(Resources subResources : subtractResources){
						Resources res = pResources.subtractResources(subResources);
						if(Utility.min(res.convertResources()) >= 0 && !alreadyInArrayList(resources, res)){
							resources.add(res);
						}
					}
				}
			}
		}else{
			copyList = new ArrayList<Resources>(this.resources);
			for(Resources pResources : copyList){
				if(!Utility.allNull(pResources.convertResources())){
					resources.remove(pResources);
					for(Resources subResources : subtractResourcesOne){
						Resources res = pResources.subtractResources(subResources);
						if(Utility.min(res.convertResources()) >= 0 && !alreadyInArrayList(resources, res)){
							resources.add(res);
						}
					}
				}
			}
		}
	}
	
	/**
	 * checks if the given resource is already in the arrayList.
	 * This Method does NOT check if there are two of the same Element in the arrayList,
	 * but rather if two Resources have the same amount of each ResourceType
	 * @param resources2 the arrayList of Resources
	 * @param res the Resources Object
	 * @return true, if two Resources Objects have the same value of each ResourcType. false, otherwise.
	 */
	private boolean alreadyInArrayList(ArrayList<Resources> resources2, Resources res) {
		for(int i=0; i<resources2.size(); i++){
			if(sameResources(resources2.get(i), res)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks if the two Resource objects have the same value for each ResouceType
	 * @param r1 resource
	 * @param r2 resource
	 * @return true, if both Resource objects have the same value for each ResouceType. false, otherwise.
	 */
	private boolean sameResources(Resources r1, Resources r2){
		for(ResourceType type : ResourceType.values()){
			if(r1.getResources().get(type) != r2.getResources().get(type)){
				return false;
			}
		}
		return true;
	}

	/**
	 * uses the addResources method in Resources to increase the resources of the tracked {@code Player}
	 * if the exact resourceType is unknown, the playerTracker creates new Resource objects with all possibilities
	 * @param amount the amount of resources the player gains
	 */
	public void increaseResources(int amount) {
		if(amount != 1){
			setAllNewResources(amount);
			copyList = new ArrayList<Resources>(this.resources);
			for(Resources pResources : copyList){
				resources.remove(pResources);
				for(Resources subResources : subtractResources){
					Resources res = pResources.addResources(subResources);
					resources.add(res);
				}
			}
		}else{
			copyList = new ArrayList<Resources>(this.resources);
			for(Resources pResources : copyList){
				resources.remove(pResources);
				for(Resources subResources : subtractResourcesOne){
					Resources res = pResources.addResources(subResources);
					resources.add(res);
				}
			}
		}
	}

	/**
	 * create an ArrayList with all possible variations to split amount resources on the 5 resourceTypes
	 * @param amount amount of resources
	 */
	public void setAllNewResources(int amount){
		int[] subResources = new int[5];
		for(int i=0; i<subResources.length; i++){
			for(int j=0; j<amount; j++){
				for(int l=0; l <subResources.length; l++){
					if(l!=i){
						setNull(subResources);
						if(j!=0){
							subResources[i] = amount-j;
							subResources[l] = j;
							if(!alreadyInArrayList(subtractResources, Resources.convertIntToResources(subResources))){
								subtractResources.add(Resources.convertIntToResources(subResources));
							}
						}else{
							subResources[i] = amount;
							if(!alreadyInArrayList(subtractResources, Resources.convertIntToResources(subResources))){
								subtractResources.add(Resources.convertIntToResources(subResources));
							}
							break;
						}
					}
				}
				
			}
		}
	}
	

	/**
	 * uses the add method in Resources to increase the resources of the tracked player
	 * @see Resources
	 * @param addResources the added resources
	 */
	public void updateResources(Resources addResources) {
		for(int i=0; i<resources.size(); i++){
			if(!alreadyInArrayList(this.resources, addResources)){
				resources.get(i).add(addResources);
			}
		}
	}
	
	/**
	 * reset all Resources of the tracked player
	 */
	public void resetResources() {
		this.resources = new ArrayList<Resources>();
		this.resources.add(new Resources(ResourceType.LUMBER, 0,
				ResourceType.WOOL, 0,
				ResourceType.GRAIN, 0,
				ResourceType.BRICK, 0,
				ResourceType.ORE, 0));
	}
	
	/**
	 * checks if there is a possibility, that the ai can afford a Settlement.
	 * @return probability, the probability that the player can afford a Settlement
	 */
	public float canAffordSettlementProbability() {
		float probability = 0f;
		Resources settlementResources = Settlement.getCost();
		
		neededResources = settlementResources.convertResources();
		for(Resources pResources : resources){
		int[] playerResources = pResources.convertResources();
			
			int[] re = new int[5];
			for(int i = 0; i < playerResources.length; i++){
				re[i] = playerResources[i] - neededResources[i];
			}
			if(Utility.allPositive(re)){
				probability += 1.0f;
			}
			
			if(tryExchange(playerResources)){
				probability += 1.0f;
			}
		}
		return probability;
	}
	

	/**
	 * checks if there is a possibility, that the ai can afford a City.
	 * @return probability, the probability that the player can afford a City
	 */
	public float canAffordCityProbability(){
	
		float probability = 0f;
		Resources cityResources = City.cost;
		
		neededResources = cityResources.convertResources();
		for(Resources pResources : resources){
			int[] playerResources = pResources.convertResources();
				
				int[] re = new int[5];
				for(int i = 0; i < playerResources.length; i++){
					re[i] = playerResources[i] - neededResources[i];
				}
				if(Utility.allPositive(re)){
					probability += 1.0f;
				}
				
				if(tryExchange(playerResources)){
					probability += 1.0f;
				}
			}
			return probability;
	
	}


	/**
	 * checks if there is a possibility, that the ai can afford a DevelopmentCard.
	 * @return probability, the probability that the player can afford a DevelopmentCard
	 */
	public float canAffordDevCardProbability() {
		float probability = 0f;
		Resources devResources = DevelopmentCard.getCost();
		
		neededResources = devResources.convertResources();
		for(Resources pResources : resources){
			int[] playerResources = pResources.convertResources();
				
				int[] re = new int[5];
				for(int i = 0; i < playerResources.length; i++){
					re[i] = playerResources[i] - neededResources[i];
				}
				if(Utility.allPositive(re)){
					probability += 1.0f;
				}
				
				if(tryExchange(playerResources)){
					probability += 1.0f;
				}
			}
			return probability;
	}

	/**
	 * checks if there is a possibility, that the ai can afford a road.
	 * @return probability, the probability that the player can afford a road
	 */
	public float canAffordRoadProbability(){
	
		float probability = 0f;
		Resources roadResources = Road.cost;
	
		neededResources = roadResources.convertResources();
		for(Resources pResources : resources){
		int[] playerResources = pResources.convertResources();
			
			int[] re = new int[5];
			for(int i = 0; i < playerResources.length; i++){
				re[i] = playerResources[i] - neededResources[i];
			}
			if(Utility.allPositive(re)){
				probability += 1.0f;
			}
			
			if(tryExchange(playerResources)){
				probability += 1.0f;
			}
		}
		return probability;
	}
	
	/**
	 * Tries to exchange Resources with the bank, so it can afford the neededResources
	 * @param playerResources the current Resources the player has
	 * @return true, if it can trade. false, otherwise
	 */
	public boolean tryExchange(int[] playerResources){
		int[] offerResources = new int[5];
		int[] wantedResources = new int[5];
		for(int i = 0; i < playerResources.length; i++){
			playerResources[i] -= neededResources[i];
			if(playerResources[i] >= 0){
				neededResources[i] = 0;
			}
			else{
				neededResources[i] = Math.abs(playerResources[i]);
			}
		}

		int mostNeeded = Utility.maxIndex(neededResources);
		
		if(mostNeeded != -1){
			for(int i =0; i< playerResources.length; i++){
				if(checkHarbor(i, player) && playerResources[i] >= 2){
					playerResources[i] -= 2;
					playerResources[mostNeeded] += 1;
					neededResources[mostNeeded] -= 1;
					if(Utility.allNull(neededResources)){
						offerResources[i] = 2;
						wantedResources[mostNeeded] = 1;
						return true;
					}
				}
			}
		}
		
		if(mostNeeded != -1){
			for(int i =0; i< playerResources.length; i++){
				if(checkHarbor(5, player) && playerResources[i] >= 3){
					playerResources[i] -= 3;
					playerResources[mostNeeded] += 1;
					neededResources[mostNeeded] -= 1;
					if(Utility.allNull(neededResources)){
						offerResources[i] = 3;
						wantedResources[mostNeeded] = 1;
						return true;
					}
				}
			}
		}
		
		if(mostNeeded != -1){
			for(int i =0; i< playerResources.length; i++){
				if(playerResources[i] >= 4){
					playerResources[i] -= 4;
					playerResources[mostNeeded] += 1;
					neededResources[mostNeeded] -= 1;
					if(Utility.allNull(neededResources)){
						offerResources[i] = 4;
						wantedResources[mostNeeded] = 1;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * checks if the player has an Harbor.
	 * i = 0: checks for an lumberHarbor
	 * i = 1: checks for an woolHarbor
	 * i = 2: checks for an grainHarbor
	 * i = 3: checks for an brickHarbor
	 * i = 4: checks for an oreHarbor
	 * i = 5: checks for an universalHarbor
	 * @param i Integer
	 * @param player the Player
	 * @return true, if the player has the specific harbor. false, otherwise
	 */
	private boolean checkHarbor(int i, Player player) {
		switch (i){
		case 0: return player.hasHarbor(HarborType.LUMBER);
		case 1: return player.hasHarbor(HarborType.WOOL);
		case 2: return player.hasHarbor(HarborType.GRAIN);
		case 3: return player.hasHarbor(HarborType.BRICK);
		case 4: return player.hasHarbor(HarborType.ORE);
		case 5: return player.hasNormalHarbor();
		}
		return false;
	}

	
	/**
	 * set all array values to 0
	 * @param array array of Integers
	 */
	public void setNull(int[] array){
		for(int i=0; i<array.length; i++){
			array[i] = 0;
		}
	}

	/**
	 * return the tracked Player
	 * @return tracked Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * returns all possible Resources the player could have
	 * @return arrayList of all possible Resources
	 */
	public ArrayList<Resources> getResources() {
		return resources;
	}

	/**
	 * gets the probability, that the player has the Resource with the ResourceType reType
	 * @param reType ResourceType
	 * @return float between 0 and 1
	 */
	public float getResourceAmount(ResourceType reType) {
		float propability = 0f;
		ArrayList<Integer> resourceType = new ArrayList<Integer>();
		for(Resources re : resources){
			resourceType.add(re.getResources().get(reType));
		}
		for(Integer integer : resourceType){
			if(integer.intValue() > 0){
				propability += 1.0f;
			}
		}
		return propability/(float)resourceType.size();
	}
	
	/**
	 * sets the flags for the monopolyCard, so the next ServerMessage can be handled
	 * @param resourceType ResourceType
	 */
	public void decreaseResourcesMonopoly(ResourceType resourceType) {
		monopoly = resourceType;
		loseResources = true;
		winResources = false;
	}

	/**
	 * sets the flags for the monopolyCard, so the next ServerMessage can be handled
	 * @param resourceType ResourceType
	 */
	public void increaseResourcesMonopoly(ResourceType resourceType) {
		monopoly = resourceType;
		loseResources = false;
		winResources = true;
	}

	/**
	 * reduces the amount of the resource with the resourcetype stored in monopoly to 0.
	 * if one of the possible Resource objects dosn't have the same amount as given, it will be removed
	 * sets the flag loseResources to false, so other Server messages will be correct interpreted
	 * @param amount Integer
	 */
	public void decreaseMonopoly(int amount) {
		if(loseResources){
			copyList = new ArrayList<Resources>(this.resources);
			for(Resources resource : copyList){
				if(resource.getResources().get(monopoly) != amount){
					resources.remove(resource);
				}else{
					resource.subtractOneTypeResource(amount, monopoly);
				}
			}
		}
		loseResources = false;
	}
	
	/**
	 * 
	 * increases the amount of the resource with the resourcetype stored in monopoly by amount.
	 * sets the flag winResources to false, so other Server messages will be correct interpreted
	 * @param amount Integer
	 */
	public void increaseMonopoly(int amount) {
		if(winResources){
			copyList = new ArrayList<Resources>(this.resources);
			for(Resources resource : copyList){
				resource.addOneTypeResource(amount, monopoly);
			}
		}
		winResources = false;
	}
	
	/**
	 * string representation of an ArrayList of Resources
	 * @param re the arrayList that should get turned into a String
	 * @return the string representation of an ArrayList of Resources
	 */
	private String getString(ArrayList<Resources> re){
		String message = "Player " + player.getId() + ":\n";
		//for-each loop caused ConcurrentModificationException
		for(int i=0; i<re.size(); i++){
			Resources res = re.get(i);
			message += "\n";
			for(ResourceType reType : ResourceType.values()){
				message += reType.toString() + ": " + res.getResources().get(reType) + "\n";
			}	
		}
		return message;
	}

	/**
	 * sets the amount the player has spend
	 * @param b the cost
	 */
	public void setCost(int b) {
		this.cost = b;
	}
	
	/**
	 * sets the Resources the player has spend
	 * @param costResources the resources
	 */
	public void setCostResources(Resources costResources){
		this.costResources = costResources;
	}

	/**
	 * updates the Resources objects of the player after a sea-Trade.
	 * this Method will be called, when the playerTracker doesn't know which Resources the player has traded
	 */
	public void updateResourcesTrade() {
		if(this.cost > 1){
			copyList = new ArrayList<Resources>(resources);
			for(Resources re : copyList){
				if(Utility.max(re.convertResources()) < cost){
					this.resources.remove(re);
				}else{
					this.resources.remove(re);
					for(ResourceType type : ResourceType.values()){
						if(re.getResources().get(type) >= cost){
							re.subtract(new Resources(type, cost));
							setAllNewResources(1, type);
							for(Resources newResources : subtractResources){
								Resources addedResources = re.addResources(newResources);
								this.resources.add(addedResources);
							}
						}
					}
				}
			}
			this.cost = 0;
		}
	}

	/**
	 * updates the Resources objects of the player after a sea-Trade.
	 * this Method will be called, when the playerTracker does know which Resources the player has traded
	 * @param resourcesToAdd the resources the player gets of his trade
	 */
	public void updateResourcesTrade(Resources resourcesToAdd) {
		if(this.costResources != null){
			copyList = new ArrayList<Resources>(resources);
			for(Resources re : copyList){
				re.subtract(costResources);
				if(Utility.min(re.convertResources()) >= 0){
					re.add(resourcesToAdd);
					if(!alreadyInArrayList(resources, re)){
						resources.add(re);
					}
				}else{
					this.resources.remove(re);
				}
			}
			this.costResources = null;
		}
	}
}
