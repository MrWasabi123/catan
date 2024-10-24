package game.ai.aiplan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import game.Game;
import game.ai.Ai;
import game.ai.AiPhases;
import game.ai.monopoly.Monopoly;
import game.board.construction.localities.City;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.corners.Corner;
import game.board.edges.Edge;
import game.board.harbors.HarborType;
import game.board.hexes.HexType;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import game.trade.Trade;
import utility.Utility;

/**
 * the Plan of an ai for one turn.
 * for each turn the ai takes a new aiPlan will be created
 * @author Wanja Sajko
 * @author Yize Sun
 *
 */
public class AiPlan {

	/**
	 * The ai this plan is for
	 */
	private Ai ai;
	
	/**
	 * The location where the ai wants to build a settlement or city
	 * null if the ai doesn't want to build a settlement or city, or doesn't´t have the resources to afford one
	 */
	private Corner buildingCorner = null;
	
	/**
	 * The location where the ai wants to build a road
	 * null if the ai doesn't want to build a road, or doesn't´t have the resources to afford one
	 */
	private Edge buildingEdge = null;
	
	/**
	 * Value if the ai wants to buy a developmentcard
	 * ture, if ai wants to buy one. false, otherwise
	 */
	private boolean buying;
	
	/**
	 * The {@code PlayableDevelopmentCard} the ai wants to play
	 * null, if the ai doesn't want to play  one
	 */
	private PlayableDevelopmentCard playing;
	
	/**
	 * The player of the ai
	 */
	private Player player;

	/**
	 * Number of attempts
	 */
	private int attempt;
	
	/**
	 * Game on which the ai plays
	 */
	private Game game;
	
	/**
	 * The resource the ai wants to get closer to it´s goal
	 */
	private ResourceType wantedResource;

	/**
	 * Arraylist of corners which the ai wants to build
	 */
	private ArrayList<Corner> sortCorners;

	/**
	 * The strategy to evaluate Corners and Edges
	 */
	private Evaluate eval;
	
	/**
	 * An ArrayList of PlayerbaleDevelopmentCards the ai has
	 */
	private ArrayList<PlayableDevelopmentCard> devCards = new ArrayList<PlayableDevelopmentCard>();
	
	/**
	 * An int array of resources the ai needs to build or buy something
	 */
	private int[] neededResources;
	
	/**
	 * The resource the ai wants to get when it plays a MonopolyCard
	 */
	private Monopoly monopoly;
	
	/**
	 * The resources the ai wants to get when it plays a YearOfPleantyCard
	 */
	private Resources yearOfPleanty;

	/**
	 * The resource the ai wants when it plays a KnightCard
	 */
	private ResourceType robberResource;

	/**
	 * Is making a trade with player or not in this turn
	 */
	private boolean tradeWithPlayer;
	
	/**
	 * Has maked a trade with player or not in this turn
	 */
	private boolean wasTrade =false;
	
	/**
	 * An array with two edges the ai wants to build on, after playing a RoadbuildingCard
	 */
	private Edge[] freeRoads = new Edge[2];

	/**
	 * A map with the edge and its quality
	 */
	private Map<Edge, Float> edgeQuality = new HashMap<Edge, Float>();
	
	/**
	 * Constructor
	 * @param player
	 * 			Player of the ai
	 * @param attempt
	 * 			attempts to do something of the ai
	 * @param game
	 * 			the game the ai plays on
	 * @param ai
	 * 			the ai, we want to build a plan for
	 * @param eval
	 *          the eval methods
	 * @param wasTrade
	 *          has maked a trade or not in this turn 
	 * @param tradeWithPlayer
	 *          is making a trade or not in this turn
	 */
	public  AiPlan(Player player, int attempt, Game game, Ai ai, Evaluate eval,boolean wasTrade,boolean tradeWithPlayer){ 
		this.player = player;
		this.attempt = attempt;
		this.game = game;
		this.ai = ai;
		this.eval = eval;
		this.monopoly = new Monopoly();
		this.wasTrade = wasTrade;
		this.tradeWithPlayer = tradeWithPlayer;
		
		for(DevelopmentCard dev : player.getDevelopmentCards()){
			if(dev instanceof PlayableDevelopmentCard){
				devCards.add((PlayableDevelopmentCard) dev);
			}
		}
		PlayableDevelopmentCard roadBuilding = getDevCard(PlayableDevelopmentCardType.ROAD_BUILDING);
		if(roadBuilding != null && player.hasPlayedDevelopmentCardThisTurn()){
			if(roadBuilding.canPlayAtThisTurn(game.getRoundCount()) && ai.allowdToPlayDevCard){
				if(Player.getMaxRoad() - player.getCountRoad() > 0 && !player.getAvilableCorners().isEmpty()){
					playing = roadBuilding;
					setFreeRoads();
				}
			}
		}
		if(playing == null){
			AiPhases aiPhase = getAiPhase();
			switch(aiPhase){
			case BEGINNING_PHASE:
				buildRoadOrSettlement();
				break;
			case SECOND_ROAD_PHASE:
				buildRoad();
				break;
			case EXPANDING_PHASE: 
				expansionStrat();
				break;
			case CITY_ROAD_PHASE:
				cityRoadDevBuild();
				break;
			default:
				break;
			
			}
		}
	}
	
	/**
	 * creates an arrayList of Edges from the List of Corners, that represent the shortestPath between two corners
	 * @param corners the shortestPath between two corners
	 * @return an arrayList of Edges that represent the shortestPath between two corners
	 */
	public ArrayList<Edge> makeLongestRoadEdges(List<Corner> corners){
		ArrayList<Edge> longestRoadEdges = new ArrayList<Edge>();
		for(int i=0; i<corners.size()-1; i++){
			Edge edge = game.getBoard().searchEdge(corners.get(i), corners.get(i+1));
			if(!longestRoadEdges.contains(edge)){
				longestRoadEdges.add(edge);
			}
		}
		return longestRoadEdges;
	}

	/**
	 * sets the two roads the ai wants to build with his Road_BuildingCard
	 */
	private void setFreeRoads() {
		AiPhases aiPhase = getAiPhase();
		if(aiPhase == AiPhases.EXPANDING_PHASE){
			freeRoads[0] = searchRoadsExpanding(player.getAvilableEdges());
			ArrayList<Edge> newEdges = new ArrayList<Edge>(player.getAvilableEdges());
			for(Edge edge : freeRoads[0].getAdjacentEdgesOfEdge()){
				if(!edge.isOccupied() && !newEdges.contains(freeRoads[0])){
					newEdges.add(edge);
				}
			}
			newEdges.remove(freeRoads[0]);
			if(Player.getMaxRoad() - player.getCountRoad()>1){
				freeRoads[1] = searchRoadsExpanding(newEdges);
			}
		}else if(aiPhase == AiPhases.CITY_ROAD_PHASE){
			if(ai.getLongestRoad() == null){
				searchLongestRoad();
			}else{
				Corner corner1 = ai.getLongestRoad().get(0);
				Corner corner2 = ai.getLongestRoad().get(ai.getLongestRoad().size()-1);
				List<Corner> longestRoad = game.getBoard().shortesPath(corner1, corner2, Player.getMaxRoad() - player.getCountRoad());
				if(longestRoad == null || !longestRoad.equals(ai.getLongestRoad())){
					searchLongestRoad();
				}
			}
			List<Corner> longestRoad = ai.getLongestRoad();
			if(longestRoad == null){
				freeRoads[0] = findNextEdgeForLongestRoad(player.getAvilableEdges());
				if(Player.getMaxRoad() - player.getCountRoad()>1){
					ArrayList<Edge> newEdges = new ArrayList<Edge>(player.getAvilableEdges());
					if(!newEdges.contains(freeRoads[0])){
						newEdges.add(freeRoads[0]);
					}
					freeRoads[1] = findNextEdgeForLongestRoad(newEdges);
				}
			}else{
				if(longestRoad.size() > 2){
					freeRoads[0] = game.getBoard().searchEdge(longestRoad.get(0), longestRoad.get(1));
					if(Player.getMaxRoad() - player.getCountRoad()>1){
						freeRoads[1] = game.getBoard().searchEdge(longestRoad.get(1), longestRoad.get(2));
					}
				}
			}
		}
	}
	
	/**
	 * searches the best road the ai can build to reach a good corner.
	 * it will set an entry for each {@link Edge} in {@cod edges} with an float value, that represents the edges quality, in {@code edgeQuality}
	 * @param edges the edges, that get assessed
	 * @return the best edge the ai can build
	 */
	private Edge searchRoadsExpanding(ArrayList<Edge> edges){
		edgeQuality = new HashMap<Edge, Float>();
		for(Edge edge : edges){
			if(areCornersUnoccupied(edge.getAdjacentCornersOfEdge())){
				//search all edges with two unoccupied corners, assess them and put them into the map edgeQuality
				if(!edge.getCorner1().isAdjacentCornerOccupied()){
					if(!edgeQuality.containsKey(edge)){
						edgeQuality.put(edge, assessCornerExpanding(edge.getCorner1()));
					}
				}else if(!edge.getCorner2().isAdjacentCornerOccupied()){
					if(!edgeQuality.containsKey(edge)){
						edgeQuality.put(edge, assessCornerExpanding(edge.getCorner2()));
					}
				}
			}else{
				//assesses all other edges and puts them in the map edgeQuality
				Map<Edge, Corner> cornersDisTwo = CornersDistanceTwo();
				if(cornersDisTwo.get(edge) != null){
					if(!edgeQuality.containsKey(edge)){
						edgeQuality.put(edge, assessCornerExpanding(cornersDisTwo.get(edge)));
					}
				}
			}
		}
		List<Edge> quality = new ArrayList<Edge>(edgeQuality.keySet());
		
		Collections.sort(quality, new Comparator<Edge>() {

			@Override
			public int compare(Edge o1, Edge o2) {
				return (int) (edgeQuality.get(o1) - edgeQuality.get(o2));
			}
	    });
		
		int attemptIndex = Math.max(0, Math.min(attempt, quality.size()-1));
		if(quality.isEmpty()){
			return null;
		}
		return quality.get(attemptIndex);
	}
	
	/**
	 * searches the shortestPath between all outerCorners and checks if it increases the longest road.
	 * the shortestPath between two corners that increases the longest road the most
	 * and is additionally pretty small, will be stored in {@code Ai.longestRoad}
	 */
	private void searchLongestRoad(){
		ArrayList<Corner> outerCorners = searchOuterCorners();
		ArrayList<Corner> otherCorners = new ArrayList<Corner>(outerCorners);
		List<Corner> longestRoad = null;
		for(Corner corner1 : outerCorners){
			otherCorners.remove(corner1);
			if(!otherCorners.isEmpty()){
				for(Corner corner2 : otherCorners){
					List<Corner> shortesPath = game.getBoard().shortesPath(corner1, corner2, Player.getMaxRoad() - player.getCountRoad());
					if(shortesPath != null){
						ArrayList<Edge> edgesSP = new ArrayList<Edge>(player.getOccupiedEdges());
						for(Edge edge : makeLongestRoadEdges(shortesPath)){
							if(!edgesSP.contains(edge)){
								edgesSP.add(edge);
							}
						}
						ArrayList<Edge> edgesLR = new ArrayList<Edge>(player.getOccupiedEdges());
						if(longestRoad != null){
							for(Edge edge : makeLongestRoadEdges(longestRoad)){
								if(!edgesLR.contains(edge)){
									edgesLR.add(edge);
								}
							}
						}
						if(longestRoad == null || (countLongestRoad(edgesSP)/(float)edgesSP.size()) > (countLongestRoad(edgesLR)/(float)edgesLR.size())){
							longestRoad = shortesPath;
						}
					}
				}
			}
		}
		ai.setLongestRoad(longestRoad);
	}
	
	/**
	 * counts the longest Road
	 * @param edges the edges where we search the longest Road
	 * @return longest Road
	 */
	private float countLongestRoad(ArrayList<Edge> edges) {
		ArrayList<Road> visitedRoads = new ArrayList<>();
		ArrayList<Edge> blackRoads = new ArrayList<>();
		
		ArrayList<Road> roads = new ArrayList<>();
		for(Edge edge : edges){
			roads.add(new Road(player, edge));
		}
		visitedRoads.addAll(roads);

		float longestRoad = 1.0f;
		for (int i = 0; i < visitedRoads.size(); i++) {

			blackRoads.removeAll(blackRoads);

			longestRoad = (float)Math.max(longestRoad, player.visitRoad(blackRoads, visitedRoads.get(i), null));

		}
		return longestRoad;
	}

	/**
	 * checks if all given corners are unoccupied
	 * @param corners
	 * @return true, if all given corners are unoccupied. false, otherwise.
	 */
	private boolean areCornersUnoccupied(ArrayList<Corner> corners){
		for(Corner corner : corners){
			if(corner.isCornerOccupied()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * searches all corners of the player that have at least one free edge
	 * @return an ArrayList of corners, with at leat one free edge
	 */
	private ArrayList<Corner> searchOuterCorners(){
		ArrayList<Corner> corners = new ArrayList<Corner>();
		for(Edge edge : player.getAvilableEdges()){
			for(Corner corner : edge.getAdjacentCornersOfEdge()){
				if(player.getOccupiedCorners().contains(corner) && !corners.contains(corner)){
					corners.add(corner);
				}else{
					if(nextToPlayerEdge(corner) && !corners.contains(corner)){
						corners.add(corner);
					}
				}
			}
		}
		return corners;
	}

	/**
	 * checks if the corner is next to an edge, which the player occupies
	 * @param corner the checked corner
	 * @return true, if the corner is next to an edge, which the player occupies. false, otherwise
	 */
	private boolean nextToPlayerEdge(Corner corner) {
		for(Edge edge : corner.getAdjacentEdgesOfCorner()){
			if(player.getOccupiedEdges().contains(edge)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the buildingEdge.
	 */
	private void buildRoad() {
		buildingEdge = searchBestEdgeTwo();
	}

	/**
	 * Set the buildingCorner or buildingEdge.
	 */
	private void buildRoadOrSettlement() {
		if(player.getCountSettlement() == 0 || (player.getCountSettlement() == 1 && player.getCountRoad() == 1)){
			buildingCorner = searchBestCornerBuilding(game.getBoard().getCorners(), false);
		} else {
			buildingEdge = searchBestEdgeOne();
		}
		
	}
	
	/**
	 * Tells the ai what it should do in the beginning phase.
	 */
	private void expansionStrat() {
		//check if the ai has an open space to build a settlement
		if(!player.getAvilableCorners().isEmpty() && !tradeWithPlayer){
			//check if the ai can afford a settlement
			if(!canAffordSettlement()){
				//if not, try to use a developmentcard
				if(!tradeWithPlayer){
					setDevCards();
				}
			} else {
				//if true, search the best corner
				buildingCorner = searchBestCornerBuilding(player.getAvilableCorners(), false);
			}
		//check if the ai can afford a road and has no space for a settlement
		} else {
			if (canAffordRoad() && player.getCountRoad() < Player.getMaxRoad() && !player.getAvilableEdges().isEmpty() && !tradeWithPlayer){
				//if the ai can afford a road, search the best edge
				buildingEdge = searchRoadsExpanding(player.getAvilableEdges());
			} else if (player.getCountRoad() < Player.getMaxRoad() && !tradeWithPlayer){
				//if the ai cant afford a raod, try to use a developmentcard
				setDevCards();
			}
		}
		
		if(!tradeWithPlayer){
			//if the ai couldnt afford a road or settlement and couldnt use a developmentcard to change that,
			//check if the ai can afford a city without trading any resources
			if(playing == null && buildingCorner == null && buildingEdge == null){
				if(canAffordCityWithoutTrade() && player.getCountCity() < Player.getMaxCity()){
						buildCity();
					//if the ai can not afford a city, check if the ai can buy a develeopmentcard instead
					}else if(canAffordDevCardWithoutTrade()){
						buying = true;
					}
			}
			
			//if the ai couldnt afford a settlement and couldnt use a developmentcard to change that, but has an empty space,
			//set the needed Resource for a settlement.
			if(!buying && playing == null && buildingCorner == null && buildingEdge == null && !player.getAvilableCorners().isEmpty()){
				setWantedResource(0);
			//if the ai couldnt afford a road and couldnt use a developmentcard to change that, and has no empty space for a settlement,
			//set the needed Resource for a road.
			} else if(!buying && playing == null && buildingCorner == null && buildingEdge == null && player.getAvilableCorners().isEmpty()){
				setWantedResource(1);
			}
		}
	}
	
	/**
	 * Build a city or a road.
	 */
	private void cityRoadDevBuild() {
		if(ai.getLongestRoad() == null){
			searchLongestRoad();
		}else{
			Corner corner1 = ai.getLongestRoad().get(0);
			Corner corner2 = ai.getLongestRoad().get(ai.getLongestRoad().size()-1);
			List<Corner> longestRoad = game.getBoard().shortesPath(corner1, corner2, Player.getMaxRoad() - player.getCountRoad());
			if(longestRoad == null || !longestRoad.equals(ai.getLongestRoad())){
				searchLongestRoad();
			}
		}
		if(canAffordCity() && player.getCountCity() < Player.getMaxCity() && !tradeWithPlayer){
			buildCity();
		}else {
			if (canAffordRoad() && player.getCountRoad() < Player.getMaxRoad() && !tradeWithPlayer && !player.getAvilableEdges().isEmpty()){
				//if the ai can afford a road, search the best edge
				//int attemptIndex = Math.max(0, Math.min(attempt, sortCorners.size()-1));
				if(ai.getLongestRoad() == null || ai.getLongestRoad().size() <= 1){
					buildingEdge = findNextEdgeForLongestRoad(player.getAvilableEdges());
				}else{
					buildingEdge = makeLongestRoadEdges(ai.getLongestRoad()).get(0);
				}
			}else{
				if (player.getCountRoad() < Player.getMaxRoad() && !tradeWithPlayer){
				//if the ai cant afford a raod, try to use a developmentcard
				setDevCards();
				}else {
					if(canAffordDevCard() && !tradeWithPlayer){
						buying = true;
					}
				}
			}
		}
			
		if(!tradeWithPlayer){
			if(!buying && buildingCorner == null && buildingEdge == null){
				if(player.getCountCity() < Player.getMaxCity()){
					setWantedResource(2);
				}else if(player.getCountRoad() < Player.getMaxRoad()){
					setWantedResource(1);
				}	
			}
		}
	}

	/**
	 * searches the next edge the ai wants to build to increase it's longestRoad
	 * @param edges and arrayList of edges from which the ai can choose
	 * @return the edge that increases the longestRoad
	 */
	private Edge findNextEdgeForLongestRoad(ArrayList<Edge> edges) {
		ArrayList<Edge> copyList = new ArrayList<Edge>(edges);
		ArrayList<Edge> newEdges = new ArrayList<Edge>(player.getOccupiedEdges());
		float longestRoad = countLongestRoad(newEdges);
		for(Edge edge : copyList){
			if(!newEdges.contains(edge)){
				newEdges.add(edge);
			}
			if(countLongestRoad(newEdges) > longestRoad){
				return edge;
			}
		}
		return null;
	}

	/**
	 * Sets the buildingCorner.
	 */
	private void buildCity(){
		if(player.getCountCity() < player.getOccupiedCorners().size()){
			buildingCorner = searchBestCornerBuilding(player.getOccupiedCorners(), true);
			if(player.isCity(buildingCorner)){
				attempt++;
				buildCity();
			}
		}
	}
	
	/**
	 * Searches the best corner in the given ArrayList, based on the values stored in {@code hexQuality}.
	 * @param corners ArrayList of corners
	 * @param buildCity an boolean to determine if the ai wants to build a city or a settlement
	 * @return the best possible corner
	 */
	private Corner searchBestCornerBuilding(ArrayList<Corner> corners, boolean buildCity) {
		sortCorners = sortCornersBuilding(corners, buildCity);

		int attemptIndex = Math.max(0, Math.min(attempt, sortCorners.size()-1));
		
		if (sortCorners.isEmpty())
			return null;

		for(int i = 0; i < corners.size(); i++){
			int attemptPlusIIndex = Math.max(0, Math.min(attempt+i, sortCorners.size()-1));

			if(sortCorners.get(attemptIndex).isNextToHex(HexType.DESERT) &&
					!sortCorners.get(attemptPlusIIndex).isNextToHex(HexType.DESERT) && 
					assessCornerBuilding(sortCorners.get(attemptIndex)) == assessCornerBuilding(sortCorners.get(attemptPlusIIndex))){
				return sortCorners.get(attemptPlusIIndex);
			}
		}
		return sortCorners.get(attemptIndex);
	}

	/**
	 * Sorts the ArrayList of corners based on their {@code cornerQuality}.
	 * @param corners ArrayList of corners
	 * @param buildCity an boolean to determine if the ai wants to build a city or a settlement
	 * @return sorted ArrayList
	 */
	private ArrayList<Corner> sortCornersBuilding(ArrayList<Corner> corners, boolean buildCity){
		ArrayList<Corner> reducedCorners = new ArrayList<Corner>();
		if(!buildCity){
			for(Corner corner : corners){
				if(!corner.isCornerOccupied() && !corner.isAdjacentCornerOccupied()){
					reducedCorners.add(corner);
				}
			}
		} else {
			reducedCorners = corners;
		}
		Corner temp;
		for (int i = 1; i < reducedCorners.size(); i++) {
			temp = reducedCorners.get(i);
			int j = i;
			while (j > 0 && assessCornerBuilding(reducedCorners.get(j - 1)) > assessCornerBuilding(temp)) {
				reducedCorners.set(j, reducedCorners.get(j-1));
				j--;
			}
			reducedCorners.set(j, temp);
		}
		return reducedCorners;
	}
	
	/**
	 * Sorts the ArrayList of corners based on assessCornerExpanding.
	 * @param corners ArrayList of corners
	 * @return sorted ArrayList
	 */
	private ArrayList<Corner> sortCornersExpanding(ArrayList<Corner> corners){
		Corner temp;
		for (int i = 1; i < corners.size(); i++) {
			temp = corners.get(i);
			int j = i;
			while (j > 0 && assessCornerExpanding(corners.get(j - 1)) > assessCornerExpanding(temp)) {
				corners.set(j, corners.get(j-1));
				j--;
			}
			corners.set(j, temp);
		}
		return corners;
	}

	/**
	 * Assesses the Corners, if the ai wants to build an settlement/city.
	 * @param corner the corner that gets assessed
	 * @return the quality of the corner
	 */
	private float assessCornerBuilding(Corner corner){
		ArrayList<Corner> cs = new ArrayList<Corner>();

		for(Corner c : player.getOccupiedCorners()){
			cs.add(c);
		}
		cs.add(corner);
		float x = 0;
		if(getAiPhase() == AiPhases.BEGINNING_PHASE){
			x = eval.evalETBRS(player, cs);
		}else{
			x = eval.evalETBAll(player, cs);
		}
		return x;
	}
	
	/**
	 * Assesses the Corners, if the ai wants to build an road.
	 * @param corner the corner that gets assessed
	 * @return the quality of the corner
	 */
	private float assessCornerExpanding(Corner corner){
		float x = 0f;
		ArrayList<Corner> cs = new ArrayList<Corner>();
		if(!corner.isCornerOccupied()){
			for(Corner c : player.getOccupiedCorners()){
				cs.add(c);
			}
			cs.add(corner);
			if(corner.isAdjacentCornerOccupied()){
				return Evaluate.getDefaultRollLimit();
			}
			if(getAiPhase() == AiPhases.BEGINNING_PHASE){
				x = eval.evalETBRS(player, cs);
			}else{
				x = eval.evalETBAll(player, cs);
			}
			Player p = isInPlayersAvilableCorners(corner);
			if(p!= null){
				float y = ai.getPlayerTracker(p.getId()).canAffordSettlementProbability();
				return ((Evaluate.getDefaultRollLimit()-x)*y)+x;
			}
			return x;
		} else{
			return Evaluate.getDefaultRollLimit();
		}
	}
	
	/**
	 * Checks if the corner is already a corner on which other players can build on.
	 * @param corner the corner, that gets checked
	 * @return the player, which has the corner in his avilableCorners
	 */
	private Player isInPlayersAvilableCorners(Corner corner) {
		for(Player p : game.getPlayers()){
			if(p != player){
				if(p.getAvilableCorners().contains(corner)){
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * Searches the best first Road.
	 * @return the edge
	 */
	private Edge searchBestEdgeOne() {
		return assessNewEdges(player.getAvilableEdges());
	}
	
	/**
	 * Searches the best second road.
	 * @return the best possible edges
	 */
	private Edge searchBestEdgeTwo() {
		Corner corner = player.getLocalityWithOutRoad();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for(Edge edge : corner.getAdjacentEdgesOfCorner()){
			edges.add(edge);
		}
		return assessNewEdges(edges);
	}
	
	/**
	 * Assesses the edges in the ArrayList edges, based on the corners with distance 2 from an occupied corner.
	 * @param edges ArrayList of edges
	 * @return best edge
	 */
	private Edge assessNewEdges(ArrayList<Edge> edges) {
		ArrayList<Corner> cornerDisOne = new ArrayList<Corner>();
		//get all Corners with Distance one to a occupied Corner, that are not occupied
		for(Edge edge : edges){
			for(Corner corner : edge.getAdjacentCornersOfEdge()){
				if(!corner.isCornerOccupied() && corner.hasNoRoads()){
					cornerDisOne.add(corner);
				}
			}
		}
		
		//get all corners with Distance two to a occupiedCorner, which are not occupied and reachable
		ArrayList<Corner> cornerDisTwo = new ArrayList<Corner>();
		for(Corner c : cornerDisOne){
			for(Corner adCorner : c.getAdjacentCornersOfCorner()){
				if(!adCorner.isCornerOccupied()){
					cornerDisTwo.add(adCorner);
				}
			}
		}
		
		//search best corner with distance 2, based on the attempt
		List<Corner> corners = sortCornersExpanding(cornerDisTwo);
		
		if (corners.isEmpty())
			return edges.get(new Random().nextInt(edges.size()));
		
		Corner bestCorner = corners.get(Math.min(attempt, corners.size()-1));
		
		for(Corner corner : cornerDisOne){
			if(corner.getAdjacentCornersOfCorner().contains(bestCorner)){
				return searchEdge(corner, edges);
			}
		}
		
		return edges.get(new Random().nextInt(edges.size()));
	}
	
	/**
	 * Gets all Corners with Distance two to any other occupiedCorner.
	 * Additionally it also returns the edge from which the ai has to build to reach the corner
	 * @return a map with the edge the ai has to use to reach the corner and the corner the ai wants to reach 
	 */
	private Map<Edge, Corner> CornersDistanceTwo(){
		ArrayList<Corner> startingCorners = new ArrayList<Corner>();
		for(Corner corner : player.getOccupiedCorners()){
			if(corner.hasCornerEmptyEdge()){
				startingCorners.add(corner);
			}
		}
		
		ArrayList<Corner> cornersDisOne = new ArrayList<Corner>();
		for(Corner corner : startingCorners){
			for(Corner adCorner : corner.getAdjacentCornersOfCorner()){
				if(!player.getOccupiedCorners().contains(adCorner) && !cornersDisOne.contains(adCorner) &&
						!adCorner.isCornerOccupied()){
					if(game.getBoard().searchEdge(corner, adCorner).isOccupied() && player.getOccupiedEdges().contains(game.getBoard().searchEdge(corner, adCorner))){
						cornersDisOne.add(adCorner);
					}else if(!game.getBoard().searchEdge(corner, adCorner).isOccupied()){
						cornersDisOne.add(adCorner);
					}
				}
			}
		}
		
		ArrayList<Corner> cornersDisTwo = new ArrayList<Corner>();
		for(Corner corner : cornersDisOne){
			for(Corner adCorner : corner.getAdjacentCornersOfCorner()){
				if(!player.getOccupiedCorners().contains(adCorner) && !cornersDisOne.contains(adCorner) && !cornersDisTwo.contains(adCorner)&&
						!adCorner.isCornerOccupied()){
					if(game.getBoard().searchEdge(corner, adCorner).isOccupied() && player.getOccupiedEdges().contains(game.getBoard().searchEdge(corner, adCorner))){
						cornersDisTwo.add(adCorner);
					}else if(!game.getBoard().searchEdge(corner, adCorner).isOccupied()){
						cornersDisTwo.add(adCorner);
					}
				}
			}
		}
		
		Map<Edge, Corner> cornersDisTwoMap = new HashMap<Edge, Corner>();
		for(Corner corner : startingCorners){
			for(Corner corner2 : cornersDisTwo){
				List<Corner> cs = game.getBoard().shortesPath(corner, corner2, Player.getMaxRoad() - player.getCountRoad());
				if(cs != null && cs.size()-1 == 2 && !cornersDisTwoMap.containsValue(corner2)){
					Edge edge = game.getBoard().searchEdge(cs.get(cs.size()-1), cs.get(cs.size()-2));
					cornersDisTwoMap.put(edge, corner2);
				}
			}
		}
		return cornersDisTwoMap;
	}

	/**
	 * Searches the Edge with the given corner as one of the adjacent corners.
	 * @param corner the adjacent corner of the returned edge
	 * @param edges arrayList of edges
	 * @return edge with corner as adjacent corner
	 */
	private Edge searchEdge(Corner corner, ArrayList<Edge> edges) {
		for(Edge edge : edges){
			if(edge.getAdjacentCornersOfEdge().contains(corner)){
				return edge;
			}
		}
		return null;
	}

	
	/**
	 * Checks if the ai can afford a Settlement. If not the ai tries to trade with the bank.
	 * @return ture, if the ai can afford a settlement. false, otherwise.
	 */
	private boolean canAffordSettlement() {
		// A shorter way of doing this would be : player.getResources().isGreaterThanOrEqualTo(Settlement.getCost());
		// Also: its better to return a boolean instead of an integer if the method answers a yes or no question.

		Resources settlementResources = Settlement.getCost();
		
		neededResources = settlementResources.convertResources();
		int[] playerResources = new int[5];
		
		playerResources[0] = player.getResources().getResources().get(ResourceType.LUMBER);
		playerResources[1] = player.getResources().getResources().get(ResourceType.WOOL);
		playerResources[2] = player.getResources().getResources().get(ResourceType.GRAIN);
		playerResources[3] = player.getResources().getResources().get(ResourceType.BRICK);
		playerResources[4] = player.getResources().getResources().get(ResourceType.ORE);
		
		int[] re = new int[5];
		for(int i = 0; i < playerResources.length; i++){
			re[i] = playerResources[i] - neededResources[i];
		}
		if(Utility.allPositive(re)){
			return true;
		}
		
		if (tryExchange(playerResources)) {
		    return true;
		} else {
			if (!wasTrade) {
			tryExchangeWithPlayer(playerResources);
			}
			return false;
		}
	}
	
	/**
	 * Check if ai can afford a city without trade.
	 * @return true, if ai can afford the city without trade
	 * @see Trade
	 */
	private boolean canAffordCityWithoutTrade(){
		Resources cityResources = City.cost;
		
		int[] neededResources = cityResources.convertResources();
		int[] playerResources = new int[5];
		
		playerResources[0] = player.getResources().getResources().get(ResourceType.LUMBER);
		playerResources[1] = player.getResources().getResources().get(ResourceType.WOOL);
		playerResources[2] = player.getResources().getResources().get(ResourceType.GRAIN);
		playerResources[3] = player.getResources().getResources().get(ResourceType.BRICK);
		playerResources[4] = player.getResources().getResources().get(ResourceType.ORE);
		
		int[] re = new int[5];
		for(int i = 0; i < playerResources.length; i++){
			re[i] = playerResources[i] - neededResources[i];
		}
		if(Utility.allPositive(re)){
			return true;
		}
		return false;
	}

	/**
	 * Checks if the ai can afford a City.
	 * @return ture, if the ai can afford a city. false, otherwise.
	 */
	private boolean canAffordCity(){
	
		Resources cityResources = City.cost;
		
		neededResources = cityResources.convertResources();
		int[] playerResources = new int[5];
		
		playerResources[0] = player.getResources().getResources().get(ResourceType.LUMBER);
		playerResources[1] = player.getResources().getResources().get(ResourceType.WOOL);
		playerResources[2] = player.getResources().getResources().get(ResourceType.GRAIN);
		playerResources[3] = player.getResources().getResources().get(ResourceType.BRICK);
		playerResources[4] = player.getResources().getResources().get(ResourceType.ORE);
		
		int[] re = new int[5];
		for(int i = 0; i < playerResources.length; i++){
			re[i] = playerResources[i] - neededResources[i];
		}
		if(Utility.allPositive(re)){
			return true;
		}
		
		if (tryExchange(playerResources)) {
		    return true;
		} else {
			if (!wasTrade) {
			tryExchangeWithPlayer(playerResources);
			}
			return false;
		}
	
	}

	/**
	 * Checks if the ai can afford a {@code DevelopmentCard} without trading.
	 * @return true, if affordable. false, otherwise
	 */
	private boolean canAffordDevCardWithoutTrade() {
		Resources devCardResources = DevelopmentCard.getCost();
		
		int[] neededResources = devCardResources.convertResources();
		int[] playerResources = new int[5];
		
		playerResources[0] = player.getResources().getResources().get(ResourceType.LUMBER);
		playerResources[1] = player.getResources().getResources().get(ResourceType.WOOL);
		playerResources[2] = player.getResources().getResources().get(ResourceType.GRAIN);
		playerResources[3] = player.getResources().getResources().get(ResourceType.BRICK);
		playerResources[4] = player.getResources().getResources().get(ResourceType.ORE);
		
		int[] re = new int[5];
		for(int i = 0; i < playerResources.length; i++){
			re[i] = playerResources[i] - neededResources[i];
		}
		if(Utility.allPositive(re)){
			return true;
		}
		return false;
	}

	/**
	 * Checks if the ai can afford a DevelopmentCard.
	 *@return ture, if the ai can afford a DevelopmentCard. false, otherwise.
	 */
	private boolean canAffordDevCard() {
		Resources devResources = DevelopmentCard.getCost();
		
		neededResources = devResources.convertResources();
		int[] playerResources = new int[5];
		
		playerResources[0] = player.getResources().getResources().get(ResourceType.LUMBER);
		playerResources[1] = player.getResources().getResources().get(ResourceType.WOOL);
		playerResources[2] = player.getResources().getResources().get(ResourceType.GRAIN);
		playerResources[3] = player.getResources().getResources().get(ResourceType.BRICK);
		playerResources[4] = player.getResources().getResources().get(ResourceType.ORE);
		
		int[] re = new int[5];
		for(int i = 0; i < playerResources.length; i++){
			re[i] = playerResources[i] - neededResources[i];
		}
		if(Utility.allPositive(re)){
			return true;
		}
		return tryExchange(playerResources);
	}

	/**
	 * Checks if the ai can afford a road.
	 * @return true, if the ai can afford a road. false, otherwise.
	 */
	private boolean canAffordRoad(){
	
		Resources roadResources = Road.cost;
	
		neededResources = roadResources.convertResources();
		int[] playerResources = new int[5];
		
		playerResources[0] = player.getResources().getResources().get(ResourceType.LUMBER);
		playerResources[1] = player.getResources().getResources().get(ResourceType.WOOL);
		playerResources[2] = player.getResources().getResources().get(ResourceType.GRAIN);
		playerResources[3] = player.getResources().getResources().get(ResourceType.BRICK);
		playerResources[4] = player.getResources().getResources().get(ResourceType.ORE);
		
		int[] re = new int[5];
		for(int i = 0; i < playerResources.length; i++){
			re[i] = playerResources[i] - neededResources[i];
		}
		if(Utility.allPositive(re)){
			return true;
		}

		return tryExchange(playerResources);
	}
	
	/**
	 * Tries to exchange Resources with player.
	 * @param playerResources
	 *                     the resources of ai
	 */
	public void tryExchangeWithPlayer(int[] playerResources) {
		if(!ai.isTradeWithPlayer()){
			int[] offerResources = new int[5];
			int[] wantedResources = new int[5];
			Random r = new Random();
			int randomLevel = r.nextInt(3);
			
			int lowWinLevel = 6;
			int highWinLevel = 7;
			
			int oneResource = 1;
			int maxResourceType = 0;
			int mostWantedResourceType = 0;
			int secondGreaterResourcePosition = 0;
			
			for(int i = 0; i < playerResources.length; i++) {
				playerResources[i] -= neededResources[i];
				if(playerResources[i] > 0) {
					if(playerResources[i]>playerResources[maxResourceType]) {
						secondGreaterResourcePosition = maxResourceType;
						maxResourceType = i;
					}
				} else if (playerResources[i]<0){
					if (playerResources[i] < playerResources[mostWantedResourceType]) {
						mostWantedResourceType = i;
					}
				}
			}
	        
			
			if (playerResources[mostWantedResourceType] < 0 ) {
				wantedResources[mostWantedResourceType] +=oneResource;
			}
			if(maxResourceType==0 && playerResources[maxResourceType]>0) {
				playerResources[maxResourceType] -=oneResource;
				offerResources[maxResourceType] += oneResource;
	
				if (player.getVictoryPoints()<lowWinLevel) {
					// 2/3 probability
					if (randomLevel !=0 && playerResources[maxResourceType]>0) {
						offerResources[maxResourceType] += oneResource;
						playerResources[maxResourceType] -= oneResource;
					}
				} else if (player.getVictoryPoints()>highWinLevel){
					//2/3 probability
					if(randomLevel !=0 && playerResources[maxResourceType]>0) {
						playerResources[maxResourceType] -= oneResource;
						offerResources[maxResourceType] += oneResource;
					}
				}
				this.tradeWithPlayer = true;
				if (!Utility.allNull(offerResources) && !Utility.allNull(wantedResources)) {
					ai.tradeWithPlayer(offerResources, wantedResources);
				}
				} else if (maxResourceType!=0) {
					playerResources[maxResourceType] -=oneResource;
					offerResources[maxResourceType] += oneResource;
					
					if (randomLevel !=0 && player.getVictoryPoints()<lowWinLevel) {
						if (playerResources[secondGreaterResourcePosition]>0) {
							offerResources[secondGreaterResourcePosition] += oneResource;
							playerResources[secondGreaterResourcePosition] -= oneResource;
						}
					} else if (player.getVictoryPoints()>highWinLevel){
						if(playerResources[secondGreaterResourcePosition]>0) {
							playerResources[secondGreaterResourcePosition] -= oneResource;
							offerResources[secondGreaterResourcePosition] += oneResource;
						}
					}
					this.tradeWithPlayer = true;
					if (!Utility.allNull(offerResources) && !Utility.allNull(wantedResources)) {
						ai.tradeWithPlayer(offerResources, wantedResources);
					}	
			}
		}
	}
	
	/**
	 * Tries to exchange Resources with the bank, so it can afford the neededResources.
	 * @param playerResources the current Resources the Player has
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
						if(game.getBank().hasResources(Resources.convertIntToResources(wantedResources))){
							ai.trade(offerResources, wantedResources);
							return true;
						}
					}
					else{
						//return tryExchange(playerResources);
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
						if(game.getBank().hasResources(Resources.convertIntToResources(wantedResources))){
							ai.trade(offerResources, wantedResources);
							return true;
						}
					}
					else{
						//return tryExchange(playerResources);
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
						if(game.getBank().hasResources(Resources.convertIntToResources(wantedResources))){
							ai.trade(offerResources, wantedResources);
							return true;
						}
					}
					else{
						//return tryExchange(playerResources);
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if the player has an Harbor.
	 * i = 0: checks for an lumberHarbor
	 * i = 1: checks for an woolHarbor
	 * i = 2: checks for an grainHarbor
	 * i = 3: checks for an brickHarbor
	 * i = 4: checks for an oreHarbor
	 * i = 5: checks for an universalHarbor
	 * @param i Integer
	 * @param player Player
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
	 * Checks if the player has more then 7 resources. If thats the case it tries to exchange them.
	 */
	public void checkPlayerResources(){
		int[] playerResources = new int[5];
		ResourceType type = getWantedResource();
		
		playerResources[0] = player.getResources().getResources().get(ResourceType.LUMBER);
		playerResources[1] = player.getResources().getResources().get(ResourceType.WOOL);
		playerResources[2] = player.getResources().getResources().get(ResourceType.GRAIN);
		playerResources[3] = player.getResources().getResources().get(ResourceType.BRICK);
		playerResources[4] = player.getResources().getResources().get(ResourceType.ORE);

		neededResources = new int[5];
		
		if(type != null){
			switch(type){
			case BRICK: neededResources[3] = 1;
				break;
			case GRAIN: neededResources[2] = 1;
				break;
			case LUMBER: neededResources[0] = 1;
				break;
			case ORE: neededResources[4] = 1;
				break;
			case WOOL: neededResources[1] = 1;
				break;
			default:
				break;
			}
		}else{
			int index = Utility.minIndex(playerResources);
			neededResources[index] = 1;
		}

		tryExchange(playerResources);
	}

	/**
	 * Checks if an array which represents the resources of a player, has only one value.
	 * @param array array of Integers
	 * @return the resourceType, if the array only has one resourceType. null, otherwise.
	 */
	private ResourceType onlyOneResourceType(int[] array) {
		if(Utility.arraySum(array) == 1){
			return Resources.convertIntToResourceType(Utility.maxIndex(array));
		}else{
			if(array[Utility.maxIndex(array)] == Utility.arraySum(array)){
				return Resources.convertIntToResourceType(Utility.maxIndex(array));
			}
			return null;
		}
	}

	/**
	 * Searches for a {@code PlayableDevelopmentCard} with a given {@code PlayableDevelopmentCardType}.
	 * and returns it, if the player has such a card. returns null, otherwise.
	 * @param type PlayableDevelopmentCardType
	 * @return the PlayableDevelopmentCard, if the player has such a card. null, otherwise.
	 */
	private PlayableDevelopmentCard getDevCard(PlayableDevelopmentCardType type) {
		for(PlayableDevelopmentCard dev : devCards){
			if(dev.getType() == type){
				return dev;
			}
		}
		return null;
	}

	/**
	 * Checks if the ai has a {@code PlayableDevelopmentCard} and if it would allow it to build the planed object.
	 */
	private void setDevCards() {
		if(!player.hasPlayedDevelopmentCardThisTurn() && ai.isAllowdToPlayDevCard()){
			switch(Utility.arraySum(neededResources)){
			case 1: {
				if(getDevCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY) != null){
					setYearOfPleanty();
					if(getDevCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY).canPlayAtThisTurn(game.getRoundCount())){
						playing = getDevCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY);
					}
				}else if(getDevCard(PlayableDevelopmentCardType.MONOPOLY) != null){
					ResourceType type = Resources.convertIntToResourceType(Utility.maxIndex(neededResources));
					if(monopoly.checkResourceAmount(ai.getPlayerTrackers(), type) != 0){
						monopoly.setMonopolyChoice(type);
						if(getDevCard(PlayableDevelopmentCardType.MONOPOLY).canPlayAtThisTurn(game.getRoundCount())){
							playing = getDevCard(PlayableDevelopmentCardType.MONOPOLY);
						}
					}
				}else if(getDevCard(PlayableDevelopmentCardType.KNIGHT) != null){
					robberResource = Resources.convertIntToResourceType(Utility.maxIndex(neededResources));
					if(getDevCard(PlayableDevelopmentCardType.KNIGHT).canPlayAtThisTurn(game.getRoundCount())){
						playing = getDevCard(PlayableDevelopmentCardType.KNIGHT);
					}
				}
			}
			break;
			case 2: {
				if(getDevCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY) != null){
					setYearOfPleanty();
					if(getDevCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY).canPlayAtThisTurn(game.getRoundCount())){
						playing = getDevCard(PlayableDevelopmentCardType.YEAR_OF_PLENTY);
					}
				}else if(getDevCard(PlayableDevelopmentCardType.MONOPOLY) != null){
					if(onlyOneResourceType(neededResources) != null){
						if(monopoly.checkResourceAmount(ai.getPlayerTrackers(), onlyOneResourceType(neededResources)) >= 2){
							monopoly.setMonopolyChoice(onlyOneResourceType(neededResources));
							if(getDevCard(PlayableDevelopmentCardType.MONOPOLY).canPlayAtThisTurn(game.getRoundCount())){
								playing = getDevCard(PlayableDevelopmentCardType.MONOPOLY);
							}
						}
					}
				}
			}
			break;
			default: {
				if(getDevCard(PlayableDevelopmentCardType.MONOPOLY) != null){
					if(onlyOneResourceType(neededResources) != null){
						if(monopoly.checkResourceAmount(ai.getPlayerTrackers(), onlyOneResourceType(neededResources)) >= Utility.arraySum(neededResources)){
							monopoly.setMonopolyChoice(onlyOneResourceType(neededResources));
							if(getDevCard(PlayableDevelopmentCardType.MONOPOLY).canPlayAtThisTurn(game.getRoundCount())){
								playing = getDevCard(PlayableDevelopmentCardType.MONOPOLY);
							}
						}
					}
				}
			}
			}
		}
	}

	/**
	 * Sets the resourceType the ai needs the most for its next object.
	 * int = 0: Settlement,
	 * int = 1: Road,
	 * int = 2: City,
	 * int = 3: DevelopmentCard.
	 * @param buildingPlan Integer
	 */
	private void setWantedResource(int buildingPlan) {
		int[] playerResources = new int[5];
		
		playerResources[0] = player.getResources().getResources().get(ResourceType.LUMBER);
		playerResources[1] = player.getResources().getResources().get(ResourceType.WOOL);
		playerResources[2] = player.getResources().getResources().get(ResourceType.GRAIN);
		playerResources[3] = player.getResources().getResources().get(ResourceType.BRICK);
		playerResources[4] = player.getResources().getResources().get(ResourceType.ORE);
		
		int[] neededResources = null;
		
		switch(buildingPlan){
		case 0: neededResources = Settlement.getCost().convertResources();
			break;
		case 1: neededResources = Road.getCost().convertResources();
			break;
		case 2: neededResources = City.getCost().convertResources();
			break;
		case 3: neededResources = PlayableDevelopmentCard.getCost().convertResources();
			break;
		default: neededResources = new int[5];
		}
		for(int i = 0; i < playerResources.length; i++){
			neededResources[i] -= playerResources[i] - neededResources[i];
		}
		
		int maxValue = neededResources[Utility.maxIndex(neededResources)];
		if(Utility.hasEqual(maxValue, neededResources)){
			int[] frequenzy = eval.calcFrequency(player, player.getOccupiedCorners());
			for(int i = 0; i < neededResources.length; i++){
				if(neededResources[i] == maxValue){
					neededResources[i] = frequenzy[i];
				} else {
					neededResources[i] = 0;
				}
			}
			if(neededResources[Utility.minIndex(neededResources)] == -1){
				wantedResource = Resources.convertIntToResourceType(Utility.minIndex(neededResources));
			}else{
				wantedResource = Resources.convertIntToResourceType(Utility.maxIndex(neededResources));
			}
		}else{
			wantedResource = Resources.convertIntToResourceType(Utility.maxIndex(neededResources));
		}
	}

	/**
	 * Set the plenty card.
	 */
	private void setYearOfPleanty() {
		this.yearOfPleanty = Resources.convertIntToResources(neededResources);
	}

	/**
	 * Get the ai phase.
	 * @return the ai phase
	 */
	public AiPhases getAiPhase(){
		if(player.getCountSettlement() < 2){
			return AiPhases.BEGINNING_PHASE;
		}
		if(player.getCountSettlement() >= 2 && player.getCountRoad() == 1){
			return AiPhases.SECOND_ROAD_PHASE;
		}
		if(player.getCountSettlement() == Player.getMaxSettlement()){
			return AiPhases.CITY_ROAD_PHASE;
		}
		if(player.getCountSettlement() >= 2 && player.getCountSettlement() < Player.getMaxSettlement()){
			return AiPhases.EXPANDING_PHASE;
		}
		return null;
	}

	/**
	 * Get the building corner.
	 * @return the corner
	 */
	public Corner getBuildingCorner() {
		return buildingCorner;
	}

	/**
	 * Set the building corner.
	 * @param buildingCorner Corner
	 */
	public void setBuildingCorner(Corner buildingCorner) {
		this.buildingCorner = buildingCorner;
	}

	/**
	 * Get the buying state.
	 * @return true, if ai is buying
	 */
	public boolean isBuying() {
		return buying;
	}

	/**
	 * Set the buying state.
	 * @param buying boolean
	 */
	public void setBuying(boolean buying) {
		this.buying = buying;
	}

	/**
	 * Get the playing card.
	 * @return the playing card
	 */
	public PlayableDevelopmentCard getPlaying() {
		return playing;
	}

	/**
	 * Set the playing card.
	 * @param playing PlayableDevelopmentCard
	 */
	public void setPlaying(PlayableDevelopmentCard playing) {
		this.playing = playing;
	}

	/**
	 * Get the building edge.
	 * @return the edge
	 */
	public Edge getBuildingEdge() {
		return buildingEdge;
	}

	/**
	 * Set the building edge.
	 * @param buildingEdge Edge
	 */
	public void setBuildingEdge(Edge buildingEdge) {
		this.buildingEdge = buildingEdge;
	}
	
	/**
	 * Get the wanted resources.
	 * @return the resources
	 */
	public ResourceType getWantedResource() {
		return wantedResource;
	}

	/**
	 * Get the buying state.
	 * @return ture if the player is buying
	 */
	public boolean getBuying() {
		return buying;
	}

	/**
	 * Get the monopoly.
	 * @return the monopoly
	 */
	public Monopoly getMonopoly() {
		return monopoly;
	}

	/**
	 * Get the plenty resources.
	 * @return the resources
	 */
	public Resources getYearOfPleanty() {
		return yearOfPleanty;
	}

	/**
	 * Get the robber resources.
	 * @return the robber resources
	 */
	public ResourceType getRobberResource() {
		return robberResource;
	}

	/**
	 * Get the trade state.
	 * @return true, if ai is trading
	 */
	public boolean getTradeWihtPlayer() {
		return this.tradeWithPlayer;
	}
	
	/**
	 * Set the tradeWithPlayer.
	 * @param tradeWithPlayer
	 *                      set true if the ai is making a trade with player
	 */
	public void setTradeWithPlayer(boolean tradeWithPlayer) {
		this.tradeWithPlayer = tradeWithPlayer;
	}
	
	/**
	 * get the two roads the ai wants to build with its road_BuildingCard
	 * @return an array with two roads
	 */
	public Edge[] getFreeRoads() {
		return freeRoads;
	}

	/**
	 * Set the two roads the ai wants to build with its road_BuildingCard
	 * @param freeRoads an array with two roads
	 */
	public void setFreeRoads(Edge[] freeRoads) {
		this.freeRoads = freeRoads;
	}
}
