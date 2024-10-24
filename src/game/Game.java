package game;

import java.util.ArrayList;
import java.util.List;

import game.bank.Bank;
import game.board.Board;
import game.cards.specialcards.SpecialCard;
import game.cards.specialcards.SpecialCardType;
import game.dice.Dice;
import game.player.Player;
import game.trade.Trade;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a game of Settlers of Catan.
 * @author Christoph Hermann
 */
public class Game {

	/**
	 * The list of {@code Players} participating in this game.
	 * @see Player
	 */
	private List<Player> players = new ArrayList<>();

	/**
	 * The game {@code Board}.
	 * @see Board
	 */
	private Board board;

	/**
	 * The game {@code Bank}.
	 * @see Bank
	 */
	private Bank bank;

	/**
	 * The {@code Dice} used in this game.
	 * Dice
	 */
	private Dice dice;

	/**
	 * The {@code Player} whose turn it currently is.
	 * @see Player
	 */
	private SimpleObjectProperty<Player> activePlayerProperty = new SimpleObjectProperty<Player>();

	/**
	 * The longest road card.
	 */
	private SpecialCard longestRoadCard;

	/**
	 * To help count the round
	 */
	private int playersDone = 1;

	/**
	 * The largest army card.
	 */
	private SpecialCard largestArmyCard;

	/**
	 * Whether this game is over or not.
	 */
	private boolean gameOver;

	/**
	 * The phases of this game
	 */
	private GamePhases phase;

	/**
	 * Contains a trade that an active player initiates.
	 */
	private ObjectProperty<Trade> tradeProperty = new SimpleObjectProperty<Trade>();

	/**
	 * Contains the amount of rounds played.
	 */
	private SimpleIntegerProperty roundCountProperty = new SimpleIntegerProperty(1);

	/**
	 * The number of players in {@code Game}.
	 * @see Player
	 */
	private SimpleIntegerProperty playersNumberInGameProperty = new SimpleIntegerProperty(0);

	/**
	 * The property wrapping the player who won the game.
	 */
	private SimpleObjectProperty<Player> winnerProperty = new SimpleObjectProperty<Player>();

	/**
	 * Creates a new game with the specified {@code Players}.
	 * @param players the {@link Player Players}.
	 */
	public Game(List<Player> players) {
		this.players = players;
		this.board = new Board();
		this.dice = new Dice();
		this.bank = new Bank();
		this.playersNumberInGameProperty.set(players.size());

		longestRoadCard = new SpecialCard(SpecialCardType.LONGEST_ROAD);
		largestArmyCard = new SpecialCard(SpecialCardType.LARGEST_ARMY);

		for(Player player : players){
			player.setGame(this);
		}

		activePlayerProperty.set(players.get(0));
	}


	/**
	 * Sets the next {@code Player} to be the active player.
	 * Additionally calls on the setRoundCount method to maybe update the roundCount.
	 * @see Player
	 */
	public void giveControlToNextPlayer() {
		int activePlayerIndex = getActivePlayerIndex();
		int nextPlayerIndex = (activePlayerIndex + 1) % players.size();

		setActivePlayer(players.get(nextPlayerIndex));
		increaseRoundCount();
	}

	/**
	 * Increases the round counter by one.
	 */
	public void increaseRoundCount() {
		int playerDoneReset = 1;
		//not a fixed value 4, but a variable value(number of players in game)
		if(playersDone==getPlayersNumberInGame()) {
			//playerDone start with value 1, after last player in round finish round(means similar before the first player in round starts), 
			//-the playerDone should be 1 and the roundCount should be roundCount + 1 , 
			roundCountProperty.set((getRoundCount()+1));
			playersDone = playerDoneReset;
		}
		//else should be used here, otherwise playersDone = 1,3,4,6,7,9... that means short of 2,5,8,11... 
		else {
			playersDone = playersDone+1;
		}
	}

	/**
	 * Determines if all {@code Players} who can decline the {@code Trade} have done so.
	 * @return true, if the number of {@link Player Players} who declined the {@link Trade} reaches the maximum number
	 * of player who could decline the trade . false, otherwise.
	 */
	public boolean haveAllPlayersDeclinedTheTrade() {
		if(tradeProperty.get()!=null) {
			if(tradeProperty.get().getDeclinedList()!=null) {
				return tradeProperty.get().getDeclinedList().size() >= players.size() - 1;
			} else {return false;}
		} else {return false;}
	}

	/**
	 * Returns the list index of the active player within the players list.
	 * @return the index of the active {@link Player}. 
	 */
	private int getActivePlayerIndex() {
		for (int i=0; i<players.size(); i++) {
			if (players.get(i) == activePlayerProperty.get()) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Returns the {@code Player} whose turn it is, if the active player ends their turn.
	 * @return the next active {@link Player}.
	 */
	public Player getNextPlayer() {
		int nextPlayerIndex = (getActivePlayerIndex() + 1) % players.size();

		return players.get(nextPlayerIndex);
	}

	/**
	 * Returns the {@code Player} whose turn it was before the current turn.
	 * @return the previously active {@link Player}.
	 */
	public Player getPrevousPlayer() {
		int previousPlayerIndex;

		// The %-operator returns the remainder, not the modulus. For a negative number the remainder is also negative.
		// That's why we need an if-statement instead of the %-operator to prevent a negative index.
		if (getActivePlayerIndex() == 0) {
			previousPlayerIndex = players.size() - 1;
		} else {
			previousPlayerIndex = getActivePlayerIndex() - 1;
		}

		return players.get(previousPlayerIndex);
	}

	/**
	 * Returns the {@code Player} who has the most victory points apart from the loosing player.
	 * @param looser the {@link Player} who lost the game.
	 * @return the player with the most points.
	 */
	public Player getPlayerWithMostPointsOtherThan(Player looser) {
		int mostVictoryPoints = Integer.MIN_VALUE;
		Player playerWithMostPoints = null;

		for (Player player : players) {
			if (player.getVictoryPoints() > mostVictoryPoints && player != looser) {
				playerWithMostPoints = player;
			}
		}

		return playerWithMostPoints;
	}

	//following two methods will be used in ServerController
	/**
	 * Return the id of player whose id it is previous id of  the current player, and set this player as activePlayer in game
	 * @return previousPlayerId
	 */
	public int getPreviousPlayerId() {
		setActivePlayer(getPrevousPlayer());
		return activePlayerProperty.get().getId();
	}

	/**
	 * Return the id of player whose id it is next id of current player, and set this player as activePlayer in game
	 * @return nextPlayerId
	 */
	public int getNextPlayerId() {
		setActivePlayer(getNextPlayer());
		return activePlayerProperty.get().getId();
	}

	/**
	 * Sets the {@code Player} with the specified id to be the active player.
	 * @param id the id of the {@link Player}.
	 */
	public void giveControlToPlayer(int id) {
		for (int i=0; i<players.size(); i++) {
			if (players.get(i).getId() == id) {
				setActivePlayer(players.get(i));
			}
		}
	}

	/**
	 * Returns the {@code Board} used by this game.
	 * @return the {@link Board}.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Sets the {@code Board} used by this game.
	 * @param board the {@link Board}.
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Returns the {@code Dice} used by this game.
	 * @return the {@link Dice}.
	 */
	public Dice getDice() {
		return dice;
	}

	/**
	 * Returns the {@code Players} in this game.
	 * @return the {@link Player Players}.
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Returns the {@code Player} whose turn it currently is.
	 * @return the active {@link Player}.
	 */
	public Player getActivePlayer() {
		return activePlayerProperty.get();
	}

	/**
	 * Sets the specified {@code Player} to be the active player.
	 * @param activePlayer the new active {@link Player}.
	 */
	public void setActivePlayer(Player activePlayer) {
		this.activePlayerProperty.set(activePlayer);
	}

	/**
	 * Iterates over the number of Players to look for the one with the stated ID.
	 * @param ID the ID of the {@link Player}.
	 * @return Player with the stated ID
	 */
	public Player searchPlayers(int ID){
		for(Player player : players){
			if(player.getId() == ID)
				return player;
		}
		return null;
	}

	/**
	 * Getter method for the amount of rounds played.
	 * @return roundCount
	 */
	public int getRoundCount() {
		return roundCountProperty.get();
	}

	/**
	 * Returns the {@code Bank} in this game.
	 * @return the {@link Bank}.
	 */
	public Bank getBank() {
		return bank;
	}

	/**
	 * Getter for the current {@code Trade}.
	 * @return the {@link Trade}
	 */
	public Trade getTrade() {
		return tradeProperty.get();
	}

	/**
	 * Setter for the current {@code Trade}
	 * @param trade the {@link Trade} to set.
	 */
	public void setTrade(Trade trade) {
		this.tradeProperty.set(trade);
	}

	/**
	 * The property wrapping the round count.
	 * @return the round count.
	 */
	public SimpleIntegerProperty roundCountProperty() {
		return roundCountProperty;
	}

	/**
	 * Set dice in this game when a player roll dice
	 * @param dice the player has rolled
	 */
	public void setDice(Dice dice) {
		this.dice = dice;
	}

	/**
	 * The property wrapping the player whose turn it currently is.
	 * @return the active player.
	 */
	public SimpleObjectProperty<Player> activePlayerProperty() {
		return activePlayerProperty;
	}

	/**
	 * Returns the {@code Player} with the specified id.
	 * @param id the id.
	 * @return player the {@link Player}.
	 */
	public Player getPlayerWithId(int id) {
		for (Player player:getPlayers()) {
			if(player.getId()==id) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Get the phase of this game
	 * @return the phase
	 */
	public GamePhases getPhase() {
		return phase;
	}

	/**
	 * Set the phase of this game
	 * @param phase the phase to set
	 */
	public void setPhase(GamePhases phase) {
		this.phase = phase;
	}

	/**
	 * Getter for the tradeProperty that is needed to bind trade in GameController
	 * @return tradeProperty
	 */
	public ObjectProperty<Trade> tradeProperty() {
		return tradeProperty;
	}

	/**
	 * Get the longest road card in game
	 * @return the longestRoadCard
	 */
	public SpecialCard getLongestRoadCard() {
		return longestRoadCard;
	}

	/**
	 * Set the longest road card in game
	 * @param longestRoadCard the longestRoadCard to set
	 */
	public void setLongestRoadCard(SpecialCard longestRoadCard) {
		this.longestRoadCard = longestRoadCard;
	}

	/**
	 * Get the largest army card in game
	 * @return the largestArmyCard
	 */
	public SpecialCard getLargestArmyCard() {
		return largestArmyCard;
	}

	/**
	 * Get the largest army card in game
	 * @param largestArmyCard the largestArmyCard to set
	 */
	public void setLargestArmyCard(SpecialCard largestArmyCard) {
		this.largestArmyCard = largestArmyCard;
	}

	/**
	 * Sets the winner of the game
	 * @param player the winner
	 */
	public void setWinner(Player player) {
		this.winnerProperty.set(player);
	}

	/**
	 * Getter for the property wrapping the player who won the game.
	 * @return the winner property
	 */
	public SimpleObjectProperty<Player> winnerProperty() {
		return winnerProperty;
	}

	/**
	 * Get the number of players in {@code Game}.
	 * @return the number of players in game
	 * @see Player
	 */
	public int getPlayersNumberInGame() {
		return playersNumberInGameProperty.get();
	}

	/**
	 * Set the number of players in {@code Game}.
	 * @param playersNumberInGame the number of players to set
	 * @see Player
	 */
	public void setPlayersNumberInGame(int playersNumberInGame) {
		this.playersNumberInGameProperty.set(playersNumberInGame);
	}

	/**
	 * Returns whether this game is over or not.
	 * @return true, if this game is over. false, otherwise.
	 */
	public boolean isOver() {
		return gameOver;
	}

	/**
	 * Sets whether this game is over or not.
	 * @param gameOver whether this game is over or not.
	 */
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

}
