package game.ai.aiplan;

import java.util.ArrayList;

import game.Game;
import game.board.construction.localities.City;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.corners.Corner;
import game.board.harbors.Harbor;
import game.board.harbors.HarborType;
import game.board.hexes.Hex;
import game.board.hexes.HexType;
import game.cards.DevelopmentCard;
import game.player.Player;
import utility.Utility;

/**
 * Evaluate player on his round count
 * @author Wanja Sajko
 *
 */
public class Evaluate {
	
	/**
	 * The default roll limit
	 */
	private static final float DEFAULT_ROLL_LIMIT = 200;
	
	/**
	 * List of harbors
	 */
	private ArrayList<Harbor> harbors = new ArrayList<Harbor>();
	
	/**
	 * The game in which this ai player play 
	 */
	private Game game;
	
	/**
	 * Constructor for the Evaluate class
	 * @param game the game that is currently played
	 */
	public Evaluate(Game game){
		this.game = game;
	}
	
	/**
	 * Evaluates a {@code Player} based on his the round count until 
	 * he can afford a {@code Road}, {@code Settlement}, {@code City} 
	 * and {@code DevelopmentCard}.
	 * @param player the player
	 * @param corners the occupied corners of this player
	 * @return roundcount, when player can afford a construction or developmentCard
	 */
	public float evalETBAll(Player player, ArrayList<Corner> corners){
		float value = 0;
		
		for(Corner corner : corners){
			if(game.getBoard().isHarbor(corner) != null){
				this.harbors.add(game.getBoard().isHarbor(corner));
			}
		}
		
		int[] frequency = calcFrequency(player, corners);
		
		value += evalETBRoad(player, frequency);
		value += evalETBSettlement(player, frequency);
		value += evalETBCity(player, frequency);
		value += evalETBDevCard(player, frequency);
		
		return value;
	}
	
	/**
	 * Evaluates a {@code Player} based on his the round count until 
	 * he can afford a {@code Road} and {@code Settlement}
	 * @param player the player
	 * @param corners the occupied corners of this player
	 * @return roundcount, when player can afford a road or a settlement
	 */
	public float evalETBRS(Player player, ArrayList<Corner> corners){
		float value = 0;
		
		for(Corner corner : corners){
			if(game.getBoard().isHarbor(corner) != null){
				this.harbors.add(game.getBoard().isHarbor(corner));
			}
		}
		
		int[] frequency = calcFrequency(player, corners);
		
		value += evalETBRoad(player, frequency);
		value += evalETBSettlement(player, frequency);
		
		return value;
	}

	/**
	 * Counts the average turns the player needs to get the needed resources 
	 * for an {@link DevelopmentCard}
	 * @param player the player
	 * @param frequency
	 * @return roundcount, when player can afford a development card
	 */
	private float evalETBDevCard(Player player, int[] frequency) {
		int[] neededResources = DevelopmentCard.getCost().convertResources();
		for(int i = 0; i < neededResources.length; i++){
			neededResources[i] *= -1;
		}
		return evalETB(player, frequency, neededResources);
	}

	/**
	 * Counts the average turns the player needs to get the needed resources 
	 * for an {@link City}
	 * @param player
	 * @param frequency
	 * @return roundcount, when player can afford a city
	 */
	private float evalETBCity(Player player, int[] frequency) {
		int[] neededResources = City.cost.convertResources();
		for(int i = 0; i < neededResources.length; i++){
			neededResources[i] *= -1;
		}
		return evalETB(player, frequency, neededResources);
	}

	/**
	 * Counts the average turns the player needs to get the needed 
	 * resources for an {@link Settlement}
	 * @param player
	 * @param frequency
	 * @return roundcount, when player can afford a settlement
	 */
	private float evalETBSettlement(Player player, int[] frequency) {
		int[] neededResources = Settlement.getCost().convertResources();
		for(int i = 0; i < neededResources.length; i++){
			neededResources[i] *= -1;
		}
		return evalETB(player, frequency, neededResources);
	}

	/**
	 * Counts the average turns the player needs to get the needed 
	 * resources for an {@link Road}
	 * @param player
	 * @param frequency
	 * @return roundcount, when player can afford a road
	 */
	private float evalETBRoad(Player player, int[] frequency) {
		int[] neededResources = Road.cost.convertResources();
		for(int i = 0; i < neededResources.length; i++){
			neededResources[i] *= -1;
		}
		return evalETB(player, frequency, neededResources);
	}

	/**
	 * Counts the average turns the player needs to get the needed resources 
	 * @param player
	 * @param frequency
	 * @param neededResources
	 * @return round count after the player has the needed resources
	 */
	private float evalETB(Player player, int[] frequency, int[] neededResources) {
		int rolls = 0;
		
		while(!Utility.allPositive(neededResources) && rolls < getDefaultRollLimit()){
			rolls++;
			
			for(int i=0; i < neededResources.length; i++){
				if(frequency[i] != -1 && rolls % frequency[i] == 0){
					neededResources[i]++;
				}
			}
		
			int mostNeeded = Utility.minIndex(neededResources);
			
			if(mostNeeded != -1){
				for(int i =0; i< neededResources.length; i++){
					if(checkHarbor(i, harbors) && neededResources[i] >= 2){
						neededResources[i] -= 2;
						neededResources[mostNeeded] += 1;
						mostNeeded = Utility.minIndex(neededResources);
					}
				}
			}
			
			if(mostNeeded != -1){
				for(int i =0; i< neededResources.length; i++){
					if(checkHarbor(5, harbors) && neededResources[i] >= 3){
						neededResources[i] -= 3;
						neededResources[mostNeeded] += 1;
						mostNeeded = Utility.minIndex(neededResources);
					}
				}
			}
			
			if(mostNeeded != -1){
				for(int i =0; i< neededResources.length; i++){
					if(neededResources[i] >= 4){
						neededResources[i] -= 4;
						neededResources[mostNeeded] += 1;
						mostNeeded = Utility.minIndex(neededResources);
					}
				}
			}
		}
		
		return rolls;
	}

	/**
	 * Checks if {@code harbors} has an Harbor with the type specified by i.
	 * i = 0: checks for an lumberHarbor
	 * i = 1: checks for an woolHarbor
	 * i = 2: checks for an grainHarbor
	 * i = 3: checks for an brickHarbor
	 * i = 4: checks for an oreHarbor
	 * i = 5: checks for an universalHarbor
	 * @param i
	 * @param harbors the harbors of the evaluated Player
	 * @return true, if the harbor is in harbors. false, otherwise
	 */
	private boolean checkHarbor(int i, ArrayList<Harbor> harbors) {
		if(!harbors.isEmpty()){
			switch (i){
			case 0: for(Harbor harbor : harbors){
				if(harbor.getType() == HarborType.LUMBER){
					return true;
				}
			}
			return false;
			case 1: for(Harbor harbor : harbors){
				if(harbor.getType() == HarborType.WOOL){
					return true;
				}
			}
			return false;
			case 2: for(Harbor harbor : harbors){
				if(harbor.getType() == HarborType.GRAIN){
					return true;
				}
			}
			return false;
			case 3: for(Harbor harbor : harbors){
				if(harbor.getType() == HarborType.BRICK){
					return true;
				}
			}
			return false;
			case 4: for(Harbor harbor : harbors){
				if(harbor.getType() == HarborType.ORE){
					return true;
				}
			}
			return false;
			case 5: for(Harbor harbor : harbors){
				if(harbor.getType() == HarborType.UNIVERSAL){
					return true;
				}
			}
			return false;
			default: return false;
			}
		}
		return false;
	}

	/**
	 * Calculates the frequency with which the player gets his resources
	 * @param player
	 * @param corners
	 * @return the frequency which the player gets his resources
	 */
	public int[] calcFrequency(Player player, ArrayList<Corner> corners) {
		float[]resourceProbs = new float[5];
		
		for(Corner corner : corners){
			for(Hex h : corner.getAdjacentHexesOfCorner()){
				if(h != null && (h.getType() != HexType.DESERT) && h.isLand()){
					if(player.isSettlement(corner)){
						resourceProbs[h.getResourceOrder()] += h.getHexQuality();
					}
					else if(player.isCity(corner)){
							resourceProbs[h.getResourceOrder()] += 2*h.getHexQuality();	
					} else {
						resourceProbs[h.getResourceOrder()] += h.getHexQuality();	
					}
				}
			}
		}
		
		int[] frequency = new int[5];
		for(int i =0; i < resourceProbs.length; i++){
			if(resourceProbs[i] == 0){
				frequency[i] = -1;				
			} else{
				frequency[i] = Math.round(1.0f/resourceProbs[i]);
			}
		}
		//System.out.println("1: " + frequency[0] + "\n2: " + frequency[1] + "\n3: " + frequency[2] +"\n4: " + frequency[3]+ "\n5: " + frequency[4]);
		return frequency;
	}

	/**
	 * Getter for default roll limit
	 * @return the default limit of roll
	 */
	public static float getDefaultRollLimit() {
		return DEFAULT_ROLL_LIMIT;
	}

}

