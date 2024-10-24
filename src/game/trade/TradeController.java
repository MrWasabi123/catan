package game.trade;

import java.io.IOException;

import org.json.JSONObject;

import game.Game;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import game.trade.tradeparticipant.TradeParticipantController;
import interfaces.Controller;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.ApplicationInstance;
import network.Mapper;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;
import network.server.Server;

/**
 * Represents the controller responsible for controlling trade.
 * @author Svenja Schoett
 * @author Christoph Hermann
 */
public class TradeController implements Controller<Game> {

	/**
	 * The specific game this controller controls the trade for
	 */
	private Game game; 

	/**
	 * The Label responsible for showing how many bricks the active player suggests to trade
	 */
	@FXML
	private Label BrickLabel;

	/**
	 * The Label responsible for showing how much lumber the active player suggests to trade
	 */
	@FXML
	private Label LumberLabel;

	/**
	 * The Label responsible for showing how much ore the active player suggests to trade
	 */
	@FXML
	private Label OreLabel;

	/**
	 * The Label responsible for showing how much grain the active player suggests to trade
	 */
	@FXML
	private Label GrainLabel;

	/**
	 * The Label responsible for showing how much wool the active player suggests to trade
	 */
	@FXML
	private Label WoolLabel;

	/**
	 * The Label responsible for showing how many bricks the active player suggests to trade
	 */
	@FXML
	private Label BrickLabelOffer;

	/**
	 * The Label responsible for showing how much lumber the active player suggests to trade
	 */
	@FXML
	private Label LumberLabelOffer;

	/**
	 * The Label responsible for showing how much ore the active player suggests to trade
	 */
	@FXML
	private Label OreLabelOffer;

	/**
	 * The Label responsible for showing how much grain the active player suggests to trade
	 */
	@FXML
	private Label GrainLabelOffer;

	/**
	 * The Label responsible for showing how much wool the active player suggests to trade
	 */
	@FXML
	private Label WoolLabelOffer;

	/**
	 * The VBox responsible for telling the player which players would accept the trade
	 */
	@FXML
	private VBox tradeParticipantsBox;

	/**
	 * The Box containing all elements necessary to finish a {@code Trade} once it started.
	 * @see Trade
	 */
	@FXML
	private BorderPane tradeStarterBox;

	/**
	 * The Pane responsible for defining the details of a {@code Trade}.
	 * @see Trade
	 */
	@FXML
	private GridPane defineTradePane;

	/**
	 * The ImageView that is responsible for showing that Player 1 would accept the trade
	 */
	@FXML
	private ImageView Player1;

	/**
	 * The ImageView that is responsible for showing that Player 2 would accept the trade
	 */
	@FXML
	private ImageView Player2;

	/**
	 * The ImageView that is responsible for showing that Player 3 would accept the trade
	 */
	@FXML
	private ImageView Player3;

	/**
	 * The ImageView that is responsible for showing that Player 4 would accept the trade
	 */
	@FXML
	private ImageView Player4;

	/**
	 * Amount of requested bricks
	 */
	private IntegerProperty brickRequest = new SimpleIntegerProperty();

	/**
	 * Amount of requested lumber
	 */
	private  IntegerProperty lumberRequest= new SimpleIntegerProperty();

	/**
	 * Amount of requested ore
	 */
	private  IntegerProperty oreRequest= new SimpleIntegerProperty();

	/**
	 * Amount of requested grain
	 */
	private IntegerProperty grainRequest= new SimpleIntegerProperty();

	/**
	 * Amount of requested wool
	 */
	private IntegerProperty woolRequest= new SimpleIntegerProperty();

	/**
	 * Amount of offered bricks
	 */
	private IntegerProperty brickOffer= new SimpleIntegerProperty();

	/**
	 * Amount of offered lumber
	 */
	private IntegerProperty lumberOffer= new SimpleIntegerProperty();

	/**
	 * Amount of offered ore
	 */
	private IntegerProperty oreOffer= new SimpleIntegerProperty();

	/**
	 * Amount of offered grain
	 */
	private IntegerProperty grainOffer= new SimpleIntegerProperty();

	/**
	 * Amount of offered wool
	 */
	private IntegerProperty woolOffer= new SimpleIntegerProperty();

	/**
	 * Label that shows the ratio the player can trade brick with.
	 */
	@FXML private Label brickRatio;

	/**
	 * Label that shows the ratio the player can trade lumber with.
	 */
	@FXML private Label lumberRatio;

	/**
	 * Label that shows the ratio the player can trade ore with.
	 */
	@FXML private Label oreRatio;

	/**
	 * Label that shows the ratio the player can trade grain with.
	 */
	@FXML private Label grainRatio;

	/**
	 * Label that shows the ratio the player can trade wool with.
	 */
	@FXML private Label woolRatio;

	/**
	 * The button used to cancel the {@code Trade}.
	 * @see Trade
	 */
	@FXML
	private Button cancelButton;
	
	/**The button with which to trade with the bank*/
	@FXML private Button bankTradeButton;
	
	/**The button with which to start trading with other players*/
	@FXML private Button playersTradeButton;
	
	/**The header of the request resources box*/
	@FXML private Label requestLabel;
	
	/**The header of the offer resources box*/
	@FXML private Label offerLabel;
	
	/**
	 * The client controller used to send messages to {@code Server}
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client;

	/**
	 * Binds the text of the buttons and labels to the selected locale.
	 */
	@FXML
	private void initialize() {
		cancelButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("CANCEL"));
		bankTradeButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("BANK"));
		playersTradeButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("OTHER_PLAYERS"));
		requestLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("REQUEST"));
		offerLabel.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("OFFER"));
	}

	//Request methods
	/**
	 * Adds more brick resource cards to the trade request
	 */
	@FXML
	private void moreBricks() {
		brickRequest.set(brickRequest.get()+1);
		this.BrickLabel.setText(""+brickRequest.get());
	}

	/**
	 * Takes brick resource cards from the trade request 
	 */
	@FXML
	private void lessBricks() {
		if(brickRequest.get()!=0) {
			brickRequest.set(brickRequest.get()-1);
			this.BrickLabel.setText(""+brickRequest.get());
		}
	}

	/**
	 * Adds more lumber resource cards to the trade request
	 */
	@FXML
	private void moreLumber() {
		lumberRequest.set(lumberRequest.get()+1);
		this.LumberLabel.setText(""+lumberRequest.get());
	}

	/**
	 * Takes lumber resource cards from the trade request 
	 */
	@FXML
	private void lessLumber() {
		if(lumberRequest.get()!=0) {
			lumberRequest.set(lumberRequest.get()-1);
			this.LumberLabel.setText(""+lumberRequest.get());
		}
	}

	/**
	 * Adds more ore resource cards to the trade request
	 */
	@FXML
	private void moreOre() {
		oreRequest.set(oreRequest.get()+1);
		this.OreLabel.setText(""+oreRequest.get());
	}

	/**
	 * Takes ore resource cards from the trade request 
	 */
	@FXML
	private void lessOre() {
		if(oreRequest.get()!=0) {
			oreRequest.set(oreRequest.get()-1);
			this.OreLabel.setText(""+oreRequest.get());
		}
	}

	/**
	 * Adds more grain resource cards to the trade request
	 */
	@FXML
	private void moreGrain() {
		grainRequest.set(grainRequest.get()+1);
		this.GrainLabel.setText(""+grainRequest.get());
	}

	/**
	 * Takes grain resource cards from the trade request 
	 */
	@FXML
	private void lessGrain() {
		if(grainRequest.get()!=0) {
			grainRequest.set(grainRequest.get()-1);
			this.GrainLabel.setText(""+grainRequest.get());
		}
	}

	/**
	 * Adds more wool resource cards to the trade request
	 */
	@FXML
	private void moreWool() {
		woolRequest.set(woolRequest.get()+1);
		this.WoolLabel.setText(""+woolRequest.get());
	}

	/**
	 * Takes wool resource cards from the trade request 
	 */
	@FXML
	private void lessWool() {
		if(woolRequest.get()!=0) {
			woolRequest.set(woolRequest.get()-1);
			this.WoolLabel.setText(""+woolRequest.get());
		}
	}

	//Offer Methods

	/**
	 * Adds more brick resource cards to the trade offer
	 */
	@FXML
	private void moreBricksOffer() {
		SimpleMapProperty<ResourceType, Integer> map = game.getActivePlayer().getResources().getResources();
		if(brickOffer.get() < map.get(ResourceType.BRICK)) {
		brickOffer.set(brickOffer.get()+1);
		this.BrickLabelOffer.setText(""+brickOffer.get());
		}
	}

	/**
	 * Takes brick resource cards from the trade offer
	 */
	@FXML
	private void lessBricksOffer() {
		if(brickOffer.get()!=0) {
			brickOffer.set(brickOffer.get()-1);
			this.BrickLabelOffer.setText(""+brickOffer.get());
		}
	}

	/**
	 * Adds more lumber resource cards to the trade offer
	 */
	@FXML
	private void moreLumberOffer() {
		SimpleMapProperty<ResourceType, Integer> map = game.getActivePlayer().getResources().getResources();
		if(lumberOffer.get() < map.get(ResourceType.LUMBER)) {
		lumberOffer.set(lumberOffer.get()+1);
		this.LumberLabelOffer.setText(""+lumberOffer.get());
		}
	}

	/**
	 * Takes lumber resource cards from the trade offer
	 */
	@FXML
	private void lessLumberOffer() {
		if(lumberOffer.get()!=0) {
			lumberOffer.set(lumberOffer.get()-1);
			this.LumberLabelOffer.setText(""+lumberOffer.get());
		}
	}
	/**
	 * Adds more ore resource cards to the trade offer
	 */
	@FXML
	private void moreOreOffer() {
		SimpleMapProperty<ResourceType, Integer> map = game.getActivePlayer().getResources().getResources();
		if(oreOffer.get() < map.get(ResourceType.ORE)) {
		oreOffer.set(oreOffer.get()+1);
		this.OreLabelOffer.setText(""+oreOffer.get());
		}
	}

	/**
	 * Takes ore resource cards from the trade offer 
	 */
	@FXML
	private void lessOreOffer() {
		if(oreOffer.get()!=0) {
			oreOffer.set(oreOffer.get()-1);
			this.OreLabelOffer.setText(""+oreOffer.get());
		}
	}

	/**
	 * Adds more grain resource cards to the trade offer
	 */
	@FXML
	private void moreGrainOffer() {
		SimpleMapProperty<ResourceType, Integer> map = game.getActivePlayer().getResources().getResources();
		if(grainOffer.get() < map.get(ResourceType.GRAIN)) {
		grainOffer.set(grainOffer.get()+1);
		this.GrainLabelOffer.setText(""+grainOffer.get());
		}
	}

	/**
	 * Takes grain resource cards from the trade offer
	 */
	@FXML
	private void lessGrainOffer() {
		if(grainOffer.get()!=0) {
			grainOffer.set(grainOffer.get()-1);
			this.GrainLabelOffer.setText(""+grainOffer.get());
		}
	}

	/**
	 * Adds more wool resource cards to the trade offer
	 */
	@FXML
	private void moreWoolOffer() {
		SimpleMapProperty<ResourceType, Integer> map = game.getActivePlayer().getResources().getResources();
		if(woolOffer.get() < map.get(ResourceType.WOOL)) {
		woolOffer.set(woolOffer.get()+1);
		this.WoolLabelOffer.setText(""+woolOffer.get());
	}
	}
	/**
	 * Takes wool resource cards from the trade offer
	 */
	@FXML
	private void lessWoolOffer() {
		if(woolOffer.get()!=0) {
			woolOffer.set(woolOffer.get()-1);
			this.WoolLabelOffer.setText(""+woolOffer.get());
		}}

	/*Methods that show trading ratios*/
	/**
	 * Method that fills the Resources Ratios with the correct trading ratios according to the players harbor situation
	 */
	private void fillRatio() {
		brickRatio.textProperty().bind(Bindings.createStringBinding(() -> {
			return client.getUser().getPlayer().getTradingRatios().getRatios().get(ResourceType.BRICK) + ":1";
		}, client.getUser().getPlayer().getTradingRatios().getRatios()));

		lumberRatio.textProperty().bind(Bindings.createStringBinding(() -> {
			return client.getUser().getPlayer().getTradingRatios().getRatios().get(ResourceType.LUMBER) + ":1";
		}, client.getUser().getPlayer().getTradingRatios().getRatios()));

		grainRatio.textProperty().bind(Bindings.createStringBinding(() -> {
			return client.getUser().getPlayer().getTradingRatios().getRatios().get(ResourceType.GRAIN) + ":1";
		}, client.getUser().getPlayer().getTradingRatios().getRatios()));

		woolRatio.textProperty().bind(Bindings.createStringBinding(() -> {
			return client.getUser().getPlayer().getTradingRatios().getRatios().get(ResourceType.WOOL) + ":1";
		}, client.getUser().getPlayer().getTradingRatios().getRatios()));

		oreRatio.textProperty().bind(Bindings.createStringBinding(() -> {
			return client.getUser().getPlayer().getTradingRatios().getRatios().get(ResourceType.ORE) + ":1";
		}, client.getUser().getPlayer().getTradingRatios().getRatios()));
	}

	/**
	 * Method that handles the trade with the bank. It collects the offered and requested resources and sends them to the client. 
	 */
	@FXML
	private void tradeWithBank() {
		
		Resources resources = new Resources(ResourceType.BRICK, brickRequest.get(), ResourceType.LUMBER, lumberRequest.get(), 
				ResourceType.ORE, oreRequest.get(), ResourceType.GRAIN, grainRequest.get(), ResourceType.WOOL, woolRequest.get());
		Resources offer = new Resources(ResourceType.BRICK, brickOffer.get(), ResourceType.LUMBER, lumberOffer.get(), 
				ResourceType.ORE, oreOffer.get(), ResourceType.GRAIN, grainOffer.get(), ResourceType.WOOL, woolOffer.get());
		JSONObject json = new Mapper().writeValueAsJson(offer, resources);
		client.sendToServer(ServerTypes.SEA_TRADING.toString(), json);

		resetTradeWindow();
	}
	

	/**
	 * Method that handles the trade with other players. It collects the offered and requested resources and sends them to the client. 
	 */
	@FXML
	private void tradeWithOtherPlayers() {
		
		Resources resources = new Resources(ResourceType.BRICK, brickRequest.get(), ResourceType.LUMBER, lumberRequest.get(), 
				ResourceType.ORE, oreRequest.get(), ResourceType.GRAIN, grainRequest.get(), ResourceType.WOOL, woolRequest.get());
		Resources offer = new Resources(ResourceType.BRICK, brickOffer.get(), ResourceType.LUMBER, lumberOffer.get(), 
				ResourceType.ORE, oreOffer.get(), ResourceType.GRAIN, grainOffer.get(), ResourceType.WOOL, woolOffer.get());
		JSONObject json = new Mapper().writeValueAsJson(offer, resources);
		client.sendToServer(ServerTypes.OFFER_TRADE.toString(), json);
	}
	

	/**
	 * Method that shows the player which other players would accept the trade offer
	 * or whether none would by showing the respective Players Image 
	 */
	private void showTradePossibilities() {
		// Show the tradeStarterBox, if a trade exists and it was started by this player.
		tradeStarterBox.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			if (game.getTrade() == null) {
				return false;
			} else {
				return game.getTrade().getPlayer() == client.getUser().getPlayer();
			}
		}, game.tradeProperty()));

		// Disable the defineTradePane, if the tradeStarterBox is visible.
		defineTradePane.disableProperty().bind(tradeStarterBox.visibleProperty());

		// Show info of all players except the one who started the trade.
		for (Player player : game.getPlayers()) {
			if (player != client.getUser().getPlayer()) {
				try {
					// Load view and controller.
					FXMLLoader loader = new FXMLLoader();
					HBox tradeParticipantView = loader.load(getClass().
							getResourceAsStream("/game/trade/tradeparticipant/TradeParticipantView.fxml"));
					TradeParticipantController tradeParticipantController = loader.getController();

					// Configure view and controller.
					tradeParticipantsBox.getChildren().add(tradeParticipantView);
					tradeParticipantController.fillParticipantBox(player, game.tradeProperty(), client, this);
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * Resets the trade window so that new {@code Trade} doesn't still contain trade offer and request from the previous
	 * trade.
	 * @see Trade
	 */
	public void resetTradeWindow() {
		brickRequest.set(0);
		this.BrickLabel.setText(""+brickRequest.get());
		lumberRequest.set(0);;
		this.LumberLabel.setText(""+lumberRequest.get());
		oreRequest.set(0);;
		this.OreLabel.setText(""+oreRequest.get());
		grainRequest.set(0);;
		this.GrainLabel.setText(""+grainRequest.get());
		woolRequest.set(0);;
		this.WoolLabel.setText(""+woolRequest.get());

		brickOffer.set(0);
		this.BrickLabelOffer.setText(""+brickOffer.get());
		lumberOffer.set(0);
		this.LumberLabelOffer.setText(""+lumberOffer.get());
		oreOffer.set(0);
		this.OreLabelOffer.setText(""+oreOffer.get());
		grainOffer.set(0);
		this.GrainLabelOffer.setText(""+grainOffer.get());
		woolOffer.set(0);
		this.WoolLabelOffer.setText(""+woolOffer.get());
	}

	/**
	 * Initializes the trade controller and disables the bankTrade and Trade with other players if the trade is empty
	 */
	@Override
	public void doInitializations(Game game, ClientConnection client) {
		this.game=game;
		this.client = client; 
		
		brickOffer.set(0);
		brickRequest.set(0);
		lumberOffer.set(0);
		lumberRequest.set(0);
		oreOffer.set(0);
		oreRequest.set(0);
		grainOffer.set(0);
		grainRequest.set(0);
		woolOffer.set(0);
		woolRequest.set(0);
		
		fillRatio();
		showTradePossibilities();
		
		bankTradeButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			return (brickRequest.get() +lumberRequest.get() +oreRequest.get()+ grainRequest.get()+ woolRequest.get() == 0 | brickOffer.get() + lumberOffer.get()+ oreOffer.get()+ grainOffer.get()+ woolOffer.get() == 0);
			}, brickRequest, lumberRequest, oreRequest, grainRequest, woolRequest, brickOffer, lumberOffer, oreOffer, grainOffer, woolOffer));
		
		playersTradeButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			return (brickRequest.get() +lumberRequest.get() +oreRequest.get()+ grainRequest.get()+ woolRequest.get() == 0 | brickOffer.get() + lumberOffer.get()+ oreOffer.get()+ grainOffer.get()+ woolOffer.get() == 0);
			}, brickRequest, lumberRequest, oreRequest, grainRequest, woolRequest, brickOffer, lumberOffer, oreOffer, grainOffer, woolOffer));
		
	}

	/**
	 * Tells the {@code Server} that the {@code Player} wants to cancel the {@code Trade}.
	 * @see Server
	 * @see Player
	 * @see Trade
	 */
	@FXML
	public void cancelTrade() {
		resetTradeWindow();

		JSONObject message = new JSONObject();
		message.put(Attributes.TRADING_ID.toString(), game.getTrade().getTradeID());
		client.sendToServer(ServerTypes.CANCEL_TRADE.toString(), message);
	}

}
