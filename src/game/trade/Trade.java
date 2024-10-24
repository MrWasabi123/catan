package game.trade;

import game.player.Player;
import game.resources.Resources;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * class that contains variables for trade offers, requests, the player and the trade-id 
 * so that the client controller can produce an instance of this class for each individual trade
 * which helps with the communication between the different players
 * @author Svenja Schoett
 *
 */
public class Trade {

	/**
	 * variable that contains the resources the player is willing to offer
	 */
	private Resources tradeOffer = new Resources();

	/**
	 * variable that contains the resources the player requests for that trade
	 */
	private Resources tradeRequest = new Resources();

	/**
	 * contains the player who started the trade
	 */
	private Player player;

	/**
	 * contains the specific id for this trade which makes them completely distinguishable from one another
	 */
	private int tradeID;

	/**
	 * list of players who accept the trade
	 */
	private ObservableSet<Player> acceptedList = FXCollections.observableSet() ;

	/**
	 * list of players who decline the trade
	 */
	private ObservableSet<Player> declinedList = FXCollections.observableSet() ;

	/**
	 * Constructor for the trade class
	 * @param tradeOffer The offered {@code Resources}
	 * @param tradeRequest The requested {@code Resources}
	 * @param player The player that started the trade.
	 * @param tradeID the trade id.
	 * @param acceptedList The list of player who accepted the trade.
	 */
	public void trade(Resources tradeOffer, Resources tradeRequest, Player player, int tradeID, ObservableSet<Player> acceptedList) {
		this.tradeOffer=tradeOffer; 
		this.tradeRequest=tradeRequest;
		this.player=player;
		this.tradeID=tradeID;
		this.acceptedList=acceptedList;
	}

	/**
	 * Trade constructor without parameters, so that it can be created at the start of
	 * the game when no trade exists.
	 */
	public Trade() {}

	/**
	 * Constructor for the trade class
	 * @param tradeOffer The offered {@code Resources}
	 * @param tradeRequest The requested {@code Resources}
	 * @param player The player that started the trade.
	 * @param tradeID the trade id.
	 */
	public Trade(int tradeID, Resources tradeOffer, Resources tradeRequest,Player player) {
		this.tradeOffer=tradeOffer; 
		this.tradeRequest=tradeRequest;
		this.player=player;
		this.tradeID=tradeID;
	}

	/*
	 * Only Getters and Setters from here on 
	 */

	/**
	 * Getter Method for what the player offers to give up in a trade
	 * @return tradeOffer
	 */
	public Resources getTradeOffer() {
		return tradeOffer;
	}

	/**
	 * Setter for what the player is willing to give away
	 * @param tradeOffer The offered {code Resources}
	 */
	public void setTradeOffer(Resources tradeOffer) {
		this.tradeOffer = tradeOffer;
	}

	/**
	 * Getter for what the player wants to have after the trade
	 * @return tradeRequest The requested {code Resources}
	 */
	public Resources getTradeRequest() {
		return tradeRequest;
	}

	/**
	 * Setter for what the player wants to get
	 * @param tradeRequest The requested {code Resources}
	 */
	public void setTradeRequest(Resources tradeRequest) {
		this.tradeRequest = tradeRequest;
	}

	/**
	 * Getter for the trading player
	 * @return player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Setter for the trading player
	 * @param player the player who started this trade.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Getter for the tradeID
	 * @return tradeID
	 */
	public int getTradeID() {
		return tradeID;
	}

	/**
	 * Setter for the tradeID
	 * @param tradeID the id of this trade
	 */
	public void setTradeID(int tradeID) {
		this.tradeID = tradeID;
	}

	/**
	 * Getter for the list of players who accept the trade
	 * @return acceptedList
	 */
	public ObservableSet<Player> getAcceptedList() {
		return acceptedList;
	}

	/**
	 * Setter for the list of players who accept the trade
	 * @param acceptedList The list of players who accepted this trade.
	 */
	public void setAcceptedList(ObservableSet<Player> acceptedList) {
		this.acceptedList=acceptedList;
	}

	/**
	 * Getter for the list of players who decline the trade
	 * @return acceptedList
	 */
	public ObservableSet<Player> getDeclinedList(){
		return declinedList;
	}

	/**
	 * Setter for the list of players who declined the trade
	 * @param declinedList The list of players.
	 */
	public void setDeclinedList(ObservableSet<Player> declinedList) {
		this.declinedList = declinedList;
	}

	/**
	 * Adds a {@code Player} to the list of players who accepted this trade.
	 * @param player the {@link Player} who accepted this trade.
	 */
	public void addAcceptingPlayer(Player player) {
		acceptedList.add(player);
	}

	/**
	 * Removes a {@code Player} from the list of players who accepted this trade.
	 * @param player the {@link Player} who no longer accepts this trade.
	 */
	public void removeAcceptingPlayer(Player player) {
		acceptedList.add(player);
	}

	/**
	 * Adds a {@code Player} to the list of payers who declined this trade.
	 * @param player the {@link Player} who declined this trade.
	 */
	public void addDecliningPlayer(Player player) {
		declinedList.add(player);
	}

}
