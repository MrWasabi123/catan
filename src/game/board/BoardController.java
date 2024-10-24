package game.board;

import java.io.IOException; 
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import game.board.construction.localities.Locality;
import game.board.construction.localities.Settlement;
import game.board.corners.Corner;
import game.board.corners.CornerController;
import game.board.edges.Edge;
import game.board.edges.EdgeController;
import game.board.harbors.HarborType;
import game.board.hexes.Hex;
import game.board.hexes.HexController;
import game.board.hexes.HexType;
import game.board.robber.Robber;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.player.Player;
import game.player.PlayerInfoController;
import game.player.PlayerState;
import interfaces.Controller;
import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;

/**
 * The controller connected to the BoardView FXML file. It manages the view elements, 
 * that are part of the board.
 * @author Paula Wikidal
 */
public class BoardController implements Controller<Board> {

	/**The Board*/
	private Board board;

	/**Connection to the Client*/
	private ClientConnection client;

	/**The Pane containing the background elements.*/
	@FXML private Pane backgroundPane;

	/**The Pane containing the hex ImageViews.*/
	@FXML private Group hexesGroup;

	/**The Pane containing the corner ImageViews.*/
	@FXML private Group cornersGroup;

	/**The Pane containing the token ImageViews.*/
	@FXML private Group tokensGroup;

	/**The Pane containing the edge ImageViews.*/
	@FXML private Group edgesGroup;

	/**The Pane containing the harbor ImageViews.*/
	@FXML private Group harborGroup;

	/**The Group containing the robber ImageViews.*/
	@FXML private Group robberGroup;

	/**The Group containing the localities ImageViews.*/
	@FXML private Group localitiesGroup;

	/**The Group containing the roads ImageViews.*/
	@FXML private Group roadsGroup;

	/**The image of the waves in the background*/
	@FXML private ImageView backgroundWater;

	/**The image of the land beneath the hexes*/
	@FXML private ImageView backgroundLand;

	/**
	 * The area where the player can select which player to steal a card from when the player 
	 * has decided to move the Robber to this {@code Hex}.
	 */
	@FXML private Pane stealResourceCardPane;

	/** The pane that contains the buttons to select a player to steal a resource card from.  */
	@FXML private Pane playerSelectionPane;
	
	/**
	 * The {@code PlayerInfoControllers} required to select the target when the {@code Robber} moves.
	 * @see PlayerInfoController
	 * @see Robber
	 */
	private List<PlayerInfoController> playerInfos = new ArrayList<>();

	/**
	 * Loads the views and corresponding controllers of the hexes, edges and corners.
	 * @see HexController
	 * @see EdgeController
	 * @see CornerController
	 */
	private void loadBoardView() {
		backgroundWater.setFitWidth(Board.DISTANCE*13);
		backgroundPane.getChildren().add(backgroundWater);

		backgroundLand.setFitWidth(Board.DISTANCE*9.23);
		backgroundPane.getChildren().add(backgroundLand);

		backgroundPane.setTranslateX(-backgroundPane.getBoundsInLocal().getWidth()/2);
		backgroundPane.setTranslateY(-backgroundPane.getBoundsInLocal().getHeight()/2);

		loadHexesAndTokens();
		loadHarbors();
		loadEdges();
		loadCorners();

		load("/game/board/robber/RobberView.fxml", robberGroup, board.getRobber());

		bindConstructions();
		bindStealResourceCardVisibility();

		startAnimation();
	}

	/**
	 * Starts the animation that places the hexes and harbors and the robber on the board.
	 */
	private void startAnimation() {
		double delay = 1.0;
		double hexFadingTime = 0.1;

		for (Node hex: hexesGroup.getChildren()) {
			FadeTransition ft = new FadeTransition(Duration.seconds(hexFadingTime), hex);
			ft.setCycleCount(1);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.setDelay(Duration.seconds(delay));
			delay+=0.1;
			ft.play();
		}

		double harborFadingTime = 1.5;
		double timeBetweenAnimations = 0.5;

		for (Node harbor: harborGroup.getChildren()) {
			FadeTransition ft2 = new FadeTransition(Duration.seconds(harborFadingTime), harbor);
			ft2.setCycleCount(1);
			ft2.setFromValue(0.0);
			ft2.setToValue(1.0);
			ft2.setDelay(Duration.seconds(delay+timeBetweenAnimations));
			ft2.play();

		}

		double robberFadingTime = 1.0;
		FadeTransition ft = new FadeTransition(Duration.seconds(robberFadingTime), robberGroup.getChildren().get(0));
		ft.setCycleCount(1);
		ft.setFromValue(0.0);
		ft.setToValue(0.5);
		ft.setDelay(Duration.seconds(delay+timeBetweenAnimations));
		ft.play();
	}

	/**
	 * Loads the images of cities and settlements of the locality on a corner changes.
	 */
	private void bindConstructions() {
		for (Corner corner: board.getCorners()) {
			corner.localityProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					if(corner.getLocality() instanceof Settlement) {
						load("/game/board/construction/localities/SettlementView.fxml", localitiesGroup, corner.getLocality());
					} else {
						load("/game/board/construction/localities/CityView.fxml", localitiesGroup, corner.getLocality());
					}
				}	
			});
		}
		for (Edge edge: board.getEdges()) {
			edge.roadProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					load("/game/board/construction/roads/RoadView.fxml", roadsGroup, edge.getRoad());
				}	
			});
		}
	}

	/**
	 * Loads the views and corresponding controllers of all hexes.
	 * @see HexController
	 */
	private void loadHexesAndTokens() {
		for (Hex hex: board.getHexes()) {
			if(hex.getType()!=HexType.WATER) {
				FXMLLoader loader = new FXMLLoader();
				try {
					Node view = loader.load(getClass().getResourceAsStream("/game/board/hexes/HexView.fxml"));
					Controller<Hex> controller = loader.<Controller<Hex>>getController();
					controller.doInitializations(hex, client);
					hexesGroup.getChildren().add(view);
					setOnRobberDropped(view, hex);

				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Makes the pane to select the player during the process of moving the robber
	 * invisible when the player isn't currently playing a development card anymore and
	 * resets the robber to its previous position.
	 */
	private void bindStealResourceCardVisibility() {
		Player player = client.getUser().getPlayer();
		player.currentySelectedDevelopmentCardProperty().addListener((observable) -> {
			if(player.currentySelectedDevelopmentCardProperty().get()==null ) {
				playerSelectionPane.getChildren().clear();
				stealResourceCardPane.setVisible(false);
				board.getRobber().resetPosition();	
			}
		});
	}

	/**
	 * Tests if there are any settlements on the corners of the selected {@code Hex} and if so,
	 * displays the menu where the player can select which player to steal a resource card from. 
	 * Otherwise it sends a move {@code Robber} request to the server.
	 * @param view the view element responsible for drawing the {@link Robber}.
	 * @param hex  the selected {@link Hex}.
	 */
	private void setOnRobberDropped(Node view, Hex hex) {
		view.setOnDragOver((event)-> {
			Dragboard db = event.getDragboard();
			if (db.hasString()) {
				event.acceptTransferModes( TransferMode.COPY_OR_MOVE );
			}
			event.consume();
		});

		view.setOnDragDropped((event) -> {
			Player player = client.getUser().getPlayer();
			boolean playingKnight = false;
			if(player.currentySelectedDevelopmentCardProperty().get()!=null) {
				playingKnight = player.currentySelectedDevelopmentCardProperty().get()
						.equals(PlayableDevelopmentCardType.KNIGHT);
			}
			if(player.getState().equals(PlayerState.MOVE_ROBBER) || playingKnight) {
				playerSelectionPane.getChildren().clear();
				if(event.getDragboard().hasString()) {
					if (event.getDragboard().getString().equals("robber")) {
						ArrayList<Player> ownersOfLocalities = getOtherOwnersOfAdjacentLocalities(hex);
						if(ownersOfLocalities.size() == 0) {
							sendRobberWithoutTarget(hex);
						} else {
							// Make the correct PlayerInfos clickable.
							for (PlayerInfoController playerInfo : playerInfos) {
								if (getOtherOwnersOfAdjacentLocalities(hex).contains(playerInfo.getPlayer())) {
									playerInfo.setHex(hex);
									playerInfo.makeClickable(true);
								} else {
									playerInfo.makeClickable(false);
								}
							}
							board.getRobber().getPositionProperty().set(hex);
						}
					}
				};
			}
		});
	}

	/**
	 * Sends a request to move the {@code Robber} without a specified target.
	 * @param hex The hex the {@code Robber} is moved to.
	 * @see Robber
	 */
	private void sendRobberWithoutTarget(Hex hex) {
		JSONObject jsonObject = new JSONObject();
		JSONObject position = new JSONObject();
		position.put(Attributes.X.toString(), hex.getxPosAxialHex());
		position.put(Attributes.Y.toString(), hex.getyPosAxialHex());
		jsonObject.put(Attributes.POSITION.toString(), position);
		
		if(client.getUser().getPlayer().currentySelectedDevelopmentCardProperty().get()!=null) {
			if(client.getUser().getPlayer().currentySelectedDevelopmentCardProperty().get().equals(PlayableDevelopmentCardType.KNIGHT)) {
				client.sendToServer(ServerTypes.PLAY_KNIGHT.toString(), jsonObject);
				client.getUser().getPlayer().currentySelectedDevelopmentCardProperty().setValue(null);
			} 
		} else {
			client.sendToServer(ServerTypes.MOVE_ROBBER.toString(), jsonObject);
		}
		stealResourceCardPane.setVisible(false);
		board.getRobber().resetPosition();
		
		// Make all PlayerInfos un-clickable.
		for (PlayerInfoController playerInfo : playerInfos) {
			playerInfo.makeClickable(false);
		}
	}

	/**
	 * Returns a list of players that own a locality on an adjacent corner of 
	 * this hex, not including the player associated with this client.
	 * @param hex The hex around which the players that are returned have built localities.
	 * @return The list of players
	 */
	private ArrayList<Player> getOtherOwnersOfAdjacentLocalities(Hex hex) {
		ArrayList<Player> players = new ArrayList<Player>();
		for (Corner corner : hex.getAdjCornersOfHex()) {
			Locality locality = corner.getLocality();
			if(locality!= null && locality.getOwner()!=client.getUser().getPlayer()
					&& !(players.contains(corner.getLocality().getOwner()))) {
				players.add(corner.getLocality().getOwner());
			}
		}
		return players;
	}

	/**
	 * Loads the views and corresponding controllers of all hexes.
	 * @see HexController
	 */
	private void loadEdges() {
		for (Edge edge: board.getEdges()) {
			FXMLLoader loader = new FXMLLoader();
			try {
				Node view = loader.load(getClass().getResourceAsStream("/game/board/edges/EdgeView.fxml"));
				Controller<Edge> controller = loader.<Controller<Edge>>getController();
				controller.doInitializations(edge, client);
				edgesGroup.getChildren().add(view);
				bindEdgeVisibility(view, edge);

			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * Binds the visibility of the edges so that they are only shown when
	 * its possible to build a road on them.
	 * @param view the node that's visually representing the edge
	 * @param edge the edge
	 */
	private void bindEdgeVisibility(Node view, Edge edge) {
		Player player = client.getUser().getPlayer();
		view.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			boolean edgeIsFree = edge.getRoad()==null;
			boolean adjRoad = false;
			boolean adjEdgeSelectedforRoadBuilding = false;
			boolean roadBuilding = player.currentySelectedDevelopmentCardProperty().get() != null &&
					player.currentySelectedDevelopmentCardProperty().get()
					.equals(PlayableDevelopmentCardType.ROAD_BUILDING);
			int localityCount = player.getLocalities().size();
			//first two rounds
			if(player.getState().equals(PlayerState.BUILD_FREE_ROAD)
					&& (localityCount==1 || localityCount==2))  {
				return edge.getAdjacentCornersOfEdge().contains(player.getLocalities()
						.get(localityCount-1).getPosition());
				
			} else if (roadBuilding) {
				List<Edge> clickedEdges = player.getLastTwoClickedOnEdges();
				if(clickedEdges.size()==1) {
					adjEdgeSelectedforRoadBuilding = edge.getAdjacentEdgesOfEdge()
							.contains(clickedEdges.get(0));
				} else if (clickedEdges.size()>=2) {
					adjEdgeSelectedforRoadBuilding = clickedEdges.get(0).equals(edge)
							|| edge.getAdjacentEdgesOfEdge().contains(clickedEdges.get(1))
							&& clickedEdges.get(1).hasRoadInAdjacentEdges(player)
							|| edge.getAdjacentEdgesOfEdge().contains(clickedEdges.get(0))
							&& clickedEdges.get(0).hasRoadInAdjacentEdges(player);
				}
				adjEdgeSelectedforRoadBuilding = edge.hasRoadInAdjacentEdges(player) || adjEdgeSelectedforRoadBuilding;
			} else if (player.getState().equals(PlayerState.TRADE_OR_BUILD)) {
				// normal case
				adjRoad = edge.hasRoadInAdjacentEdges(player);
			}
			return (adjRoad || adjEdgeSelectedforRoadBuilding) && edgeIsFree;
		}, board.constructionsProperty(), player.stateProperty(), player.lastTwoClickedOnEdgesProperty(), 
				player.currentySelectedDevelopmentCardProperty()));
	}

	/**
	 * Loads the views and corresponding controllers of all harbors.
	 * @see HexController
	 */
	private void loadHarbors() {
		for (Edge edge : board.getEdges()) {
			if (edge.getHarbor().getType()!=HarborType.NONE) {
				load("/game/board/harbors/HarborView.fxml", harborGroup, edge.getHarbor());
			}
		}
	}

	/**
	 * Loads the view of the corners
	 * @see BoardController
	 * @see Corner
	 */
	private void loadCorners() {
		for (Corner corner: board.getCorners()) {
			FXMLLoader loader = new FXMLLoader();
			try {
				Node view = loader.load(getClass().getResourceAsStream("/game/board/corners/CornerView.fxml"));
				Controller<Corner> controller = loader.<Controller<Corner>>getController();
				controller.doInitializations(corner, client);
				cornersGroup.getChildren().add(view);
				bindCornerVisibility(view, corner);

			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * Binds the visibility of the corners so that they are only shown when
	 * its possible to build a settlement on them.
	 * @param view the node that's visually representing the corner
	 * @param corner the corner
	 */
	private void bindCornerVisibility(Node view, Corner corner) {
		Player player = client.getUser().getPlayer();
		view.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			if(player.getState().equals(PlayerState.BUILD_FREE_SETTLEMENT) 
					|| player.getState().equals(PlayerState.TRADE_OR_BUILD)) {
				boolean cornerFree = corner.getLocality()==null;
				boolean enoughDistance = true;
				for (Corner adjCorner: corner.getAdjacentCornersOfCorner()) {
					enoughDistance = enoughDistance && adjCorner.getLocality()==null;
				}
				boolean adjRoad = false;
				for(Edge edge: corner.getAdjacentEdgesOfCorner()) {
					boolean ownRoadHere = false;
					if(edge.getRoad()!=null) {
						ownRoadHere = edge.getRoad().getOwner()==player;
					}
					adjRoad = adjRoad || ownRoadHere;
				}
				boolean firstRounds = player.getLocalities().size()<=1;
				return cornerFree && (enoughDistance && (adjRoad || firstRounds));
			} else {
				return false;
			}
		}, board.constructionsProperty(), player.stateProperty()));
	}

	/**
	 * Loads a FXML file, adds it to the Parent element and calls the doInitializations method,
	 * that passes an object to the controller of the view to do the initializations.
	 * @param filePath The path to the FXML file
	 * @param parent The parent the loaded file is added to
	 * @param modelObject The object, that is passed to the controller
	 */
	private void load(String filePath, Parent parent, Object modelObject) {

		Controller<Object> controller = null;

		FXMLLoader loader = new FXMLLoader();
		try {
			Node view = loader.load(getClass().getResourceAsStream(filePath));
			controller = loader.<Controller<Object>>getController();
			controller.doInitializations(modelObject, client);

			if(parent instanceof Group) {
				((Group)parent).getChildren().add(view);
			} else if (parent instanceof Pane) {
				((Pane)parent).getChildren().add(view);
			}

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void doInitializations(Board board, ClientConnection client) {
		this.board = board;
		this.client = client;
		loadBoardView();
	}

	/**
	 * Returns the group containing the images of the edges.
	 * @return the edgesGroup
	 */
	public Group getEdgesGroup() {
		return edgesGroup;
	}

	/**
	 * Returns the group containing the images of the corners.
	 * @return the cornersGroup 
	 */
	public Group getCornersGroup() {
		return cornersGroup;
	}

	/**
	 * Getter for the pane containing the background of the board. Used for scaling.
	 * @return the pane containing the background.
	 */
	public Pane getBackgroundPane() {
		return backgroundPane;
	}

	/**
	 * Sets the playerInfos.
	 * @param playerInfos the playerInfos to set.
	 */
	public void setPlayerInfos(List<PlayerInfoController> playerInfos) {
		this.playerInfos = playerInfos;
	}

}
