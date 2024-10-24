package lobby.endscreen;

import java.util.ArrayList;

import game.Game;
import game.player.Player;
import interfaces.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.ApplicationController;
import main.ApplicationInstance;
import network.client.ClientConnection;

/**
 * Controller that handles the Endscreen
 * This contains the announcement of who won and an option to return to the lobby
 * @author Svenja Schoett
 *
 */
public class EndscreenController implements Controller<Game>{

	/**
	 * The specific game this controller controls the trade for
	 */
	private Game game; 

	/**
	 * The {@code ApplicationController}
	 * @see ApplicationController
	 */
	private ApplicationController applicationController;

	/** The text saying who won the game*/
	@FXML private Label winnerText; 

	/**The name of the winner */
	@FXML private Label nameWinner; 

	/**The picture of the winner*/
	@FXML private ImageView pictureWinner;

	/** The victory points of the winner*/
	@FXML private Label pointsWinner; 

	/**The name of the loser 1 */
	@FXML private Label name1; 

	/**The picture of the loser 1*/
	@FXML private ImageView picture1;


	/** The victory points of loser 1*/
	@FXML private Label points1; 

	/**The name of the loser 2 */
	@FXML private Label name2; 

	/**The picture of loser 2*/
	@FXML private ImageView picture2;

	/** The victory points of loser 2*/
	@FXML private Label points2; 

	/**The name of the loser 3 */
	@FXML private Label name3; 

	/**The picture of loser 3*/
	@FXML private ImageView picture3;

	/** The victory points of loser 3*/
	@FXML private Label points3; 

	/**The button to restart the game */
	@FXML private Button restartButton; 

	/**
	 * Displays the specified text, as well as who achieved how many points.
	 * @param text the text to display.
	 */
	public void putText(String text) {
		winnerText.setText(text);
		restartButton.setText(ApplicationInstance.getInstance().getBundleProperty().get().getString("OK"));
		String points = ApplicationInstance.getInstance().getBundleProperty().get().getString("VICTORY_POINTS");

		ArrayList<Player> players = sortPlayers();
		
		Player winner = players.get(0);
		nameWinner.setText(winner.getName()+"  :");
		pictureWinner.setImage(new Image(winner.getImageLocation()));
		pointsWinner.setText(winner.getVictoryPoints()+" "+points);

		Player loser1 = players.get(1);
		name1.setText(loser1.getName()+" :");
		picture1.setImage(new Image(loser1.getImageLocation()));
		points1.setText(loser1.getVictoryPoints()+" "+points);

		if(players.size()>=3) {
			Player loser2 = players.get(2);
			name2.setText(loser2.getName()+" :");
			picture2.setImage(new Image(loser2.getImageLocation()));
			points2.setText(loser2.getVictoryPoints()+" "+points);
		}
		
		if(players.size()==4) {
			Player loser3 = players.get(3);
			name3.setText(loser3.getName()+" :");
			picture3.setImage(new Image(loser3.getImageLocation()));
			points3.setText(loser3.getVictoryPoints()+" "+points);
		}
	}

	/**
	 * Sorting algorithm for the players to display them in the order of 
	 * the victory points they have. 
	 * @return the sorted list of the players.
	 */
	private ArrayList<Player> sortPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		for(Player player: game.getPlayers()) {
			int i;
			for(i = 0; i<players.size(); i++) {
				if (player.getVictoryPoints() > players.get(i).getVictoryPoints()) {
					break;
				}
			}
			players.add(i, player);
		}
		return players;
	}

	/**
	 * If the button is clicked, the user returns to the main menu. 
	 */
	@FXML
	public void handleButton() {
		applicationController.returnToMainMenu();
	}

	@Override
	public void doInitializations(Game game, ClientConnection client) {
		this.game=game;
	}

	/**
	 * Sets the applicationController that is used to switch between scenes
	 * @param applicationController the applicationController
	 */
	public void setApplicationController(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

}
