package game.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

import game.Game;
import game.ai.aiplan.AiPlan;
import game.ai.aiplan.Evaluate;
import game.ai.playertracker.PlayerTracker;
import game.board.construction.localities.City;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.corners.Corner;
import game.board.edges.Edge;
import game.board.hexes.Hex;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.player.Player;
import game.player.PlayerState;
import game.resources.ResourceType;
import game.resources.Resources;
import game.trade.Trade;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import network.Mapper;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ProtocolStringConverter;
import network.protocol.ServerTypes;
import network.server.Server;
import users.AIUser;
import users.User;
import utility.Utility;

/**
 * Represents the ai
 * @author Wanja Sajko
 * @author Yize Sun
 *
 */
public class Ai {

	/**
	 * The maximum number of times an ai will try to to choose its color.
	 */
	private static final int MAX_COLOR_CHOOSING_ATTEMPTS = 10;

	/**
	 * The number of times this ai has tried to choose its color.
	 */
	private int nColorChoosingAttempts;

	/**
	 * The player this {@code Ai} controls
	 */
	private Player player;

	/**
	 * The game this ai plays on
	 */
	private Game game;

	/**
	 * The client of this ai
	 */
	private ClientConnection client;
	
	/**
	 * Count the players ,who has accepted or declined the trade
	 */
	private Set<Player> tradeActor = new HashSet<>();

	/**
	 * The number of attempts the {@code Ai} has made to make a move 
	 * while the player is in the current state
	 */
	private int attempt = 0;

	/**
	 * The game plan of the ai
	 */
	private AiPlan aiPlan;

	/**
	 * The strategy to evaluate Corners and Edges
	 */
	private Evaluate eval;
	
	/**
	 * A list of playerTrackers for each player other the the ai
	 */
	private ArrayList<PlayerTracker> playerTrackers = new ArrayList<PlayerTracker>();
	
	/**
	 * A boolean to check if ai is making a trade with player
	 */
	private boolean tradeWithPlayer = false;
	
	/**
	 * A boolean to check if ai has traded in this turn
	 */
	private boolean wasTrade = false;
	
	/**
	 * A List of Corners which represents the roads the ai wants to build for the longest road
	 */
	private List<Corner> longestRoad = null;

	/**
	 * A flag to show if the ai is allow to play a DevelopmentCard
	 */
	public boolean allowdToPlayDevCard = true;

	/**
	 * Constructor for the ai
	 */
	public Ai() {
	}

	/**
	 * Decides, which move the AI makes. This method is called 
	 * when the state of the player changes and when the server 
	 * sends the message that the last move was invalid.
	 */
	public void makeNextMove() {
		PlayerState state;

		if(game == null) {
			state = client.getUser().getState();
		} else {
			state = player.getState();
		}

		if(state == null) { System.out.println("state null"); }
		System.out.println(state.toString());

		if(state == PlayerState.TRADE_OR_BUILD || state == PlayerState.BUILD_FREE_ROAD ||
				state == PlayerState.BUILD_FREE_SETTLEMENT){
			aiPlan = new AiPlan(player, attempt, game, this, eval, wasTrade, tradeWithPlayer);
		}
		switch(state) {
		case BUILD_FREE_SETTLEMENT:
			buildSettlement();
			break;
		case BUILD_FREE_ROAD:
			buildRoad();
			break;
		case TRADE_OR_BUILD: tradeOrBuild();
		break;
		case ROLL_DICE: rollDice();
		break;
		case MOVE_ROBBER: moveRobber();
		break;
		case DISCARD_RESOURCES: discardResources();
		break;
		case START_GAME:
			changeColor();
			startGame();
			break;
		case WAIT_FOR_GAME_START:
			break;
		case CONNECTION_LOST:
			break;
		case WAIT:
			break;
		default:
			break;
		}

		attempt++;
	}

	/**
	 * Consider whether accept this trade and make decision.
	 * @param trade
	 *             the {@code Trade} which one player offered
	 */
	public void acceptTrade(Trade trade){
		
		JSONObject j_accept_trade = new JSONObject();
		j_accept_trade.put(Attributes.TRADING_ID.toString(), trade.getTradeID());
		
		boolean hasEnoughResources = this.player.getResources().isGreaterThanOrEqualTo(trade.getTradeRequest());
		
		//has enough resources
		if(hasEnoughResources){
			int winRatio = willIWin(trade);
			int loseRatio = willILose(trade);
			
			if(winRatio > loseRatio) {   //accept trade
			j_accept_trade.put(Attributes.ACCEPT.toString(), true);
			client.sendToServer(ServerTypes.ACCEPT_TRADE.toString(), j_accept_trade);
		} else if(winRatio < loseRatio ){                  //reject trade
			j_accept_trade.put(Attributes.ACCEPT.toString(), false);
			client.sendToServer(ServerTypes.ACCEPT_TRADE.toString(), j_accept_trade);
		} else {
			Random r = new Random();
			int acceptRatio = r.nextInt(2);
			
			if(acceptRatio == 0 && hasEnoughResources) {   //50% accept trade
				j_accept_trade.put(Attributes.ACCEPT.toString(), true);
				client.sendToServer(ServerTypes.ACCEPT_TRADE.toString(), j_accept_trade);
			} else {                                       //50% reject trade
				j_accept_trade.put(Attributes.ACCEPT.toString(), false);
				client.sendToServer(ServerTypes.ACCEPT_TRADE.toString(), j_accept_trade);
			}	
		}
	} else {// has not enough resources
		j_accept_trade.put(Attributes.ACCEPT.toString(), false);
		client.sendToServer(ServerTypes.ACCEPT_TRADE.toString(), j_accept_trade);
	}
	}

	/**
	 * Consider weather execute trade with this fellow {@code Player}
	 * and make decision.
	 * @param fellowPlayer 
	 *             the player, who has accept this trade
	 * @param trade 
	 *             the offered trade
	 * @param playersNumber 
	 *             the number of players in game
	 * @see Trade
	 * @see Game
	 */
	public void executeTrade(Player fellowPlayer, Trade trade, int playersNumber){
		JSONObject j_execute_trade = new JSONObject();
		j_execute_trade.put(Attributes.TRADING_ID.toString(), trade.getTradeID());
		int mediumGoal = 6;
		int advancedGoal = 8;
		boolean finishTrade = false;
		
		if(fellowPlayer.getVictoryPoints()< mediumGoal) {
			j_execute_trade.put(Attributes.FELLOW_PLAYER.toString(), fellowPlayer.getId());
			client.sendToServer(ServerTypes.FINISH_TRADE.toString(), j_execute_trade);
			resetTradeActor();
			finishTrade = true;
		} else if (fellowPlayer.getVictoryPoints()< advancedGoal) {
			Random r = new Random();
			int executeRatio = r.nextInt(2);
			
			if(executeRatio == 0) {   //50% execute trade
				j_execute_trade.put(Attributes.FELLOW_PLAYER.toString(), fellowPlayer.getId());
				client.sendToServer(ServerTypes.FINISH_TRADE.toString(), j_execute_trade);
				resetTradeActor();
				finishTrade = true;
			}
		}
		
		if (!finishTrade && tradeActor.size()==playersNumber-1) {
			cancelTrade(trade);
		}
	}
	
	/**
	 * Cancel the {@code Trade}.
	 * @param trade
	 *            the offered trade or the accepted trade
	 */
	public void cancelTrade(Trade trade) {
		//Danke Wanja
		JSONObject j_cancel_trade = new JSONObject();
		j_cancel_trade.put(Attributes.TRADING_ID.toString(), trade.getTradeID());
		client.sendToServer(ServerTypes.CANCEL_TRADE.toString(), j_cancel_trade);	
	}
	
	/**
	 * Reset the set of players, who has accepted or declined 
	 * the trade.
	 */
	public void resetTradeActor() {
		tradeActor.clear();
	}
	
	/**
	 * One more player has accepted or declined the trade.
	 * @param player 
	 *             the player who accepted or declined the trade
	 */
	public void addTradeActor(Player player) {
		tradeActor.add(player);
	}
	
	/**
	 * One player has withdraw the accept.
	 * @param player 
	 *            the player who withdraw the accept
	 */
	public void minusTradeActor(Player player) {
		tradeActor.remove(player);
	}
	
	/**
	 * Calculate additional win ratio after this {@code Trade}.
	 * @param trade
	 *             the {@code Trade} which one player offered
	 * @return the additional win ratio
	 */
	public int willIWin(Trade trade){
		int winRatio = 0;
		int maxWinRatio =10;

		Resources supply = trade.getTradeOffer();
		Resources demand = trade.getTradeRequest();
		Resources resources = new Resources();
		for(ResourceType type: this.player.getResources().getResources().keySet()) {
			resources.getResources().put(type, this.player.getResources().getResources().get(type));
		}
		//resources of ai after trade
		resources.add(supply);
		resources.subtract(demand);
		
		//after trade can build road settlement... but before trade resources not enough do these, than increase winRatio
		if(resources.isGreaterThanOrEqualTo(Road.getCost()) && !this.player.getResources().isGreaterThanOrEqualTo(Road.getCost())) 			             winRatio++;
		if(resources.isGreaterThanOrEqualTo(Settlement.getCost()) && !this.player.getResources().isGreaterThanOrEqualTo(Settlement.getCost())) 	         winRatio++;
		if(resources.isGreaterThanOrEqualTo(City.getCost()) && !this.player.getResources().isGreaterThanOrEqualTo(City.getCost()))                       winRatio++;
		if(resources.isGreaterThanOrEqualTo(DevelopmentCard.getCost()) && !this.player.getResources().isGreaterThanOrEqualTo(DevelopmentCard.getCost())) winRatio++;
		if(this.player.getVictoryPoints()>8) winRatio = maxWinRatio;
		
		return winRatio;
	}
	
	/**
	 * Calculate additional lose ratio after this {@code Trade}.
	 * @param trade
	 *             the {@code Trade} which one player offered 
	 * @return the additional lose level
	 */
	public int willILose(Trade trade) {
		int loseRatio = 0;
		int maxLoseRatio = 10;
		
		int lowWinLevel = 4;
		int middelWinLevel = 5;
		int middelPlusWinLevel = 6;
		int greatWinLevel = 7;
		
		
		int playerId = trade.getPlayer().getId();
		Player tradePlayer = game.searchPlayers(playerId);
		if(tradePlayer.getVictoryPoints()>lowWinLevel) loseRatio++;
		if(tradePlayer.getVictoryPoints()>middelWinLevel) loseRatio++;
		if(tradePlayer.getVictoryPoints()>middelPlusWinLevel) loseRatio++;
		if(tradePlayer.getVictoryPoints()>greatWinLevel) loseRatio = maxLoseRatio;
		
		return loseRatio;
	}
	
	/**
	 * Changes the color of the {@code User} running this Ai and 
	 * notifies the {@code Server}.
	 * @see User
	 * @see AIUser
	 * @see Server
	 */
	private void changeColor() {
		AIUser user = (AIUser) client.getUser();

		// Change Color.
		user.chooseDifferentColor();

		// Create message.
		JSONObject object = new JSONObject();
		object.put(Attributes.COLOR.toString(), ProtocolStringConverter.getName(user.getColor()));
		object.put(Attributes.NAME.toString(), user.getName());

		// Send message.
		client.sendToServer(ServerTypes.PLAYER.toString(), object);

		// Disconnect after too many tries.
		if (nColorChoosingAttempts > MAX_COLOR_CHOOSING_ATTEMPTS) {
			client.stopRunning();
		}

		// Increase the attempt number.
		nColorChoosingAttempts++;
	}

	/**
	 * Tells the {@code Server} that the {@code User} running this 
	 * Ai is ready to start the game.
	 * @see Server
	 * @see User
	 * @see AIUser
	 */
	private void startGame() {
		client.sendToServer(ServerTypes.GAME_START.toString(), new JSONObject());
	}

	/**
	 * Rolls the {@code Dice}.
	 */
	private void rollDice() {
		client.sendToServer(ServerTypes.ROLL_DICE.toString(), new JSONObject());
	}

	/**
	 * Discards {@code Resources}.
	 */
	private void discardResources() {
		Resources resources = player.getResources();
		Resources discarded = new Resources();
		int cardsToDiscard = player.getResourceQuantity()/2;

		for (Map.Entry<ResourceType, Integer> entry : resources.getResources().entrySet()) {
			for(int i = 0; i<=entry.getValue(); i++) {
				if(discarded.getSum() < cardsToDiscard) {
					discarded.getResources().put(entry.getKey(), i);
				}
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Attributes.SUBMIT.toString(), (new Mapper().writeValueAsJson(discarded)));
		client.sendToServer(ServerTypes.LOSE_RESOURCE.toString(), jsonObject);
	}

	/**
	 * Moves the {@code Robber}.
	 */
	private void moveRobber() {
		List<Player> players = searchBestPlayer();
		Player bestPlayer = players.get(Math.min(attempt, players.size()-1));
		ArrayList<Hex> hexes = searchBestHex(bestPlayer.getOccupiedCorners());

		Hex hex = hexes.get(new Random().nextInt(hexes.size()));
		for (int i=attempt; i<hexes.size(); i++) {
			if (!doesAiOccupyCorners(hexes.get(i)) && game.getBoard().getRobber().getPosition() != hexes.get(i)) {
				hex = hexes.get(i);
			}
		}

		JSONObject jsonObject = new JSONObject();
		JSONObject position = new JSONObject();

		position.put(Attributes.X.toString(), hex.getxPosAxialHex());
		position.put(Attributes.Y.toString(), hex.getyPosAxialHex());

		jsonObject.put(Attributes.POSITION.toString(), position);
		jsonObject.put(Attributes.TARGET.toString(), bestPlayer.getId());
		client.sendToServer(ServerTypes.MOVE_ROBBER.toString(), jsonObject);
	}

	/**
	 * Tells the ai what to do after it threw the dice.
	 */
	private void tradeOrBuild() {
		if(!aiPlan.getTradeWihtPlayer()){
			if(aiPlan.getBuildingCorner() != null && player.getOccupiedCorners().contains(aiPlan.getBuildingCorner())){
				buildCity();
			} else if(aiPlan.getBuildingCorner() != null) {
				buildSettlement();
			} else if(aiPlan.getBuildingEdge() != null){
				buildRoad();
			}else if(aiPlan.getBuying()){
				buyDevCard();
			}else if(aiPlan.getPlaying() != null){
				playDevCard(aiPlan.getPlaying());
			} 
			
			if(didAiBuild()){
				endTurn();
			}else if(aiDidNothing()){
				aiPlan.checkPlayerResources();
				endTurn();
			}
		}
	}

	/**
	 * Checks if the ai build anything this cycle of makeNextMove().
	 * @return true, id the ai did. false, otherwise.
	 */
	private boolean didAiBuild() {
		if(aiPlan.getBuildingCorner() != null ||
				aiPlan.getBuildingEdge() != null){
			return true;
		}
		return false;
			
	}
	
	/**
	 * Checks if the ai did anything this cycle of makeNextMove().
	 * @return true, id the ai did nothing. false, otherwise.
	 */
	private boolean aiDidNothing() {
		if(aiPlan.getBuildingCorner() == null &&
				aiPlan.getBuildingEdge() == null &&
				!aiPlan.getBuying() &&
				aiPlan.getPlaying() == null &&
				!aiPlan.getTradeWihtPlayer()){
			return true;
		}
		return false;
			
	}

	/**
	 * The ai creates a JSONObject with the Developmentcard it wants to player and sends it to the server
	 * @param devCard
	 * 			they type of the Developmentcard the ai wants to play
	 */
	private void playDevCard(PlayableDevelopmentCard devCard) {
		PlayableDevelopmentCardType type = devCard.getType();
		JSONObject card = new JSONObject();
		
		switch(type){
		case KNIGHT: {
			Player p = searchPlayer(aiPlan.getRobberResource());
			if(p == null){
				p = searchBestPlayer().get(0);
			}
			ArrayList<Hex> hexes = searchBestHex(p.getOccupiedCorners());

			Hex hex = hexes.get(new Random().nextInt(hexes.size()));
			for (int i=attempt; i<hexes.size(); i++) {
				if (!doesAiOccupyCorners(hexes.get(i)) && game.getBoard().getRobber().getPosition() != hexes.get(i)) {
					hex = hexes.get(i);
				}
			}
			
			JSONObject position = new JSONObject();

			position.put(Attributes.X.toString(), hex.getxPosAxialHex());
			position.put(Attributes.Y.toString(), hex.getyPosAxialHex());

			card.put(Attributes.POSITION.toString(), position);
			card.put(Attributes.TARGET.toString(), p.getId());
			client.sendToServer(ServerTypes.PLAY_KNIGHT.toString(), card);
		}
			break;
		case MONOPOLY: {
			String resource = aiPlan.getMonopoly().getMonopolyChoice().toString();
			card.put(Attributes.RESOURCE.toString(), resource);
			client.sendToServer(ServerTypes.PLAY_MONOPOLY.toString(), card);
		}
			break;
		case ROAD_BUILDING:
			Edge[] roads = aiPlan.getFreeRoads();
			if(roads[1] != null){
				card = new Mapper().writeValueStreetBuildingAsJson(new Road(client.getUser().getPlayer(), roads[0]), new Road(client.getUser().getPlayer(), roads[1]));
				if(longestRoad != null){
					longestRoad.remove(0);
					longestRoad.remove(1);
				}
			}else{
				card = new Mapper().writeValueStreetBuildingAsJson(new Road(client.getUser().getPlayer(), roads[0]), null);
				if(longestRoad != null){
					longestRoad.remove(0);
				}
			}
			client.sendToServer(ServerTypes.PLAY_ROAD_BUILDING.toString(), card);

			break;
		case YEAR_OF_PLENTY: {
			JSONObject resources = new Mapper().writeValueAsJson(aiPlan.getYearOfPleanty());
			card.put(Attributes.RESOURCES.toString(), resources);
			client.sendToServer(ServerTypes.PLAY_YEAR_OF_PLENTY.toString(), card);
		}
			break;
		default:
			break;
		}
		
		
	}
	
	/**
	 * Searches a player which has a Probability of over 50% to have a specific Resource
	 * @param type
	 * 			the resourceType the player has to have
	 * @return a player who probably has the wanted resourceType
	 */
	private Player searchPlayer(ResourceType type) {
		for(PlayerTracker pt : playerTrackers){
			if(pt.getResourceAmount(type) > 0.5){
				return pt.getPlayer();
			}
		}
		return null;
	}

	/**
	 * Sends an buy request for a {@code DevelopmentCard} to the server
	 */
	private void buyDevCard() {
		JSONObject devCard = new JSONObject();
		client.sendToServer(ServerTypes.BUY_DEVLOPMENTCARD.toString(), devCard);
	}

	/**
	 * Sends an tradeoffer to the Server.
	 * @param offerResources
	 *                      the offered resources
	 * @param wantedResources
	 *                      the wanted resources
	 */                     
	public void trade(int[] offerResources, int[] wantedResources){
		int maxIndexWantedResources = Utility.maxIndex(wantedResources);
		if(Utility.maxIndex(offerResources) == maxIndexWantedResources){
			wantedResources[maxIndexWantedResources] = 0;
			wantedResources[((maxIndexWantedResources+1) % 5)] = 1;
		}
		Resources offerRe = Resources.convertIntToResources(offerResources);
		Resources wantedRe = Resources.convertIntToResources(wantedResources);
		
		JSONObject trade = new Mapper().writeValueAsJson(offerRe, wantedRe);
		client.sendToServer(ServerTypes.SEA_TRADING.toString(), trade);
	}

	/**
	 * Send a offer trade message to server.
	 * @param offerResources
	 *                      the offered resources
	 * @param wantedResources
	 *                      the required resources
	 */
	public void tradeWithPlayer(int[] offerResources, int[] wantedResources) {
		Resources offer = Resources.convertIntToResources(offerResources);
		Resources request = Resources.convertIntToResources(wantedResources);
		//ai is now trading with someone.
		//setTradeWithPlayer(false) will be called when the Trade is executed or when it is canceld
		setTradeWithPlayer(true);
		
		JSONObject j_trade = new Mapper().writeValueAsJson(offer, request);
		client.sendToServer(ServerTypes.OFFER_TRADE.toString(), j_trade);
	}
	
	/**
	 * Send a build City message to server.
	 */
	private void buildCity() {
		Corner corner = aiPlan.getBuildingCorner();

		JSONObject city = new Mapper().writeValueAsJson(new City(client.getUser().getPlayer(), corner));
		client.sendToServer(ServerTypes.BUILD.toString(), city);
	}

	/**
	 * Tells the {@code Server} that the {@code User} wants to build 
	 * a {@code settlement}.
	 */
	private void buildSettlement() {
		Corner corner = aiPlan.getBuildingCorner();

		JSONObject settlement = new Mapper().writeValueAsJson(new Settlement(client.getUser().getPlayer(), corner));
		client.sendToServer(ServerTypes.BUILD.toString(), settlement);
	}

	/**
	 * Tells the {@code Server} that the {@code User} wants to build 
	 * a {@code road}.
	 */
	private void buildRoad() {
		Edge edge = aiPlan.getBuildingEdge();
		
		if(longestRoad != null){
			for(Edge e : aiPlan.makeLongestRoadEdges(longestRoad)){
				if(e.equals(edge) && edge.getAdjacentCornersOfEdge().contains(longestRoad.get(0))){
					longestRoad.remove(0);
				}else if(e.equals(edge) && edge.getAdjacentCornersOfEdge().contains(longestRoad.get(longestRoad.size()-1))){
					longestRoad.remove(longestRoad.size()-1);
				}else if(e.equals(edge)){
					longestRoad = null;
				}
			}
		}

		JSONObject road = new Mapper().writeValueAsJson(new Road(client.getUser().getPlayer(), edge));
		client.sendToServer(ServerTypes.BUILD.toString(), road);
	}

	/**
	 * Searches the player which is most likely to win.
	 * @return the best Player
	 */
	private List<Player> searchBestPlayer(){
		List<Player> players = game.getPlayers();

		players.remove(player);
		Player temp;
		for (int i = 1; i < players.size(); i++){
			temp = players.get(i);
			int j = i;
			while (j > 0 && assessPlayer(players.get(j - 1)) < assessPlayer(temp)) {
				players.set(j, players.get(j-1));
				j--;
			}
			players.set(j, temp);
		}
		players.add(player);
		return players;
	}

	/**
	 * Assess the players.
	 * @param player the player
	 * @return an overall float value based on the ability to build/buy a developmentcard/Road, City, Settlement
	 */
	private float assessPlayer(Player player){
		return eval.evalETBAll(player, player.getOccupiedCorners());
	}

	/**
	 * Sorts the {@code Hex} in an ArrayList, based on the players 
	 * which occupy an adjacent corner.
	 * @param corners the corners that are searched
	 * @return sorted ArrayList
	 */
	private ArrayList<Hex> searchBestHex(ArrayList<Corner> corners){
		ArrayList<Hex> hexes = new ArrayList<Hex>();
		for (Corner corner : corners){
			for (Hex hex : corner.getAdjacentHexesOfCorner()){
				char hexChar = hex.getLocality().charAt(0);
				if (!hexes.contains(hex) && Character.isUpperCase(hexChar)){
					hexes.add(hex);
				}
			}
		}

		Hex temp;
		for (int i = 1; i < hexes.size(); i++){
			temp = hexes.get(i);
			int j = i;
			while (j > 0 && hexes.get(j - 1).getOccupiedCornerCount() < temp.getOccupiedCornerCount()) {
				hexes.set(j, hexes.get(j-1));
				j--;
			}
			hexes.set(j, temp);
		}
		return hexes;
	}

	/**
	 * Checks if the ai occupies and adjacent corner of an given array.
	 * @param hex the hex to check the corners of
	 * @return true, if th ai occupies one of the corners of the hex. false, otherwise.
	 */
	private boolean doesAiOccupyCorners(Hex hex) {
		for (Corner corner : player.getOccupiedCorners()){
			if (hex.getAdjCornersOfHex().contains(corner)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the {@code Game} used by this Ai.
	 * @param game the {@link Game} to set.
	 */
	public void setGame(Game game) {
		this.game = game;

		player = client.getUser().getPlayer();
		this.eval = new Evaluate(game);
		
		for(Player p : game.getPlayers()){
			if(p != this.player){
				playerTrackers.add(new PlayerTracker(p, game));
			}
		}

		player.stateProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				attempt = 0;
				if (!game.isOver()) {
					makeNextMove();
				}
			}
		});
	}

	/**
	 * Sets the {@code ClientConnection} used by this Ai.
	 * @param client the {@link ClientConnection} to set.
	 */
	public void setClient(ClientConnection client) {
		this.client = client;
	}

	/**
	 * Searches the {@code PlayerTracker} which tracks the player. 
	 * with playerID.
	 * @param playerID the ID of the player
	 * @return PlayerTracker
	 */
	public PlayerTracker getPlayerTracker(int playerID) {
		for(PlayerTracker pT : playerTrackers){
			if(pT.getPlayer().getId() == playerID){
				return pT;
			}
		}
		return null;
		
	}

	/**
	 * Gets all PlayerTrackers.
	 * @return arrayList of PlayerTrackers
	 */
	public ArrayList<PlayerTracker> getPlayerTrackers() {
		return playerTrackers;
	}

	/**
	 * Getter for tradeActor.
	 * @return the set of tradeActor
	 */
	public Set<Player> getTradeActor() {
		return this.tradeActor;
	}
	
	/**
	 * Sets the attempt.
	 * @param attempt number of the attempt
	 */
	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}

	/**
	 * Check if ai is making trade with player.
	 * @return true if ai is making trade with player
	 */
	public boolean isTradeWithPlayer() {
		return tradeWithPlayer;
	}

	/**
	 * Set ai is or not making trade with player.
	 * @param tradeWithPlayer the new value of tradeWithPlayer
	 */
	public void setTradeWithPlayer(boolean tradeWithPlayer) {
		this.tradeWithPlayer = tradeWithPlayer;
	}
	
	 /**
     * Getter for ai plan.
     * @return the ai plan of this ai
     */
    public AiPlan getAiPlan() {
    	return this.aiPlan;
    }
    
    /**
     * Get was trade state.
	 * @return true, if ai has maked trade in this turn
	 */
	public boolean isWasTrade() {
		return wasTrade;
	}

	/**
	 * Set the was trade state.
	 * @param wasTrade the wasTrade to set
	 */
	public void setWasTrade(boolean wasTrade) {
		this.wasTrade = wasTrade;
	}

	/**
	 * ends the turn of the ai
	 */
	public void endTurn() {
		setAllowdToPlayDevCard(true);
		setWasTrade(false);
		client.sendToServer(ServerTypes.END_TURN.toString(), new JSONObject());
	}

	/**
	 * gets the {@code longestRoad}
	 * @return longest Road
	 */
	public List<Corner> getLongestRoad() {
		return longestRoad;
	}

	/**
	 * sets the {@code longestRoad}
	 * @param longestRoad
	 */
	public void setLongestRoad(List<Corner> longestRoad) {
		this.longestRoad = longestRoad;
	}
	
	/**
	 * gets if the ai is allowed to play a {@link DevelopmentCard}
	 * @return ture, if the ai is allowed. false, otherwise.
	 */
	public boolean isAllowdToPlayDevCard() {
		return allowdToPlayDevCard;
	}

	/**
	 * Serts if the ai is allowed to play a {@link DevelopmentCard}
	 * @param allowdToPlayDevCard
	 */
	public void setAllowdToPlayDevCard(boolean allowdToPlayDevCard) {
		this.allowdToPlayDevCard = allowdToPlayDevCard;
	}
}