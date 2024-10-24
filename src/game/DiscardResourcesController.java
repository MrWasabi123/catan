package game;

import java.util.ArrayList;

import org.json.JSONObject;

import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import interfaces.Controller;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import main.ApplicationInstance;
import network.Mapper;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;

/**
 * The controller for the resources that the player has to discard due to the robber being moved.
 * @author Paula Wikidal
 *
 */
public class DiscardResourcesController implements Controller<Player>{
	
	/**
	 * The connection to the client.
	 */
	private ClientConnection client;
	
	/**
	 * The player associated with this client.
	 */
	private Player player;
	
	/**
	 * The list of spinners where the amount of each resource can be selected.
	 */
	@FXML private ArrayList<Spinner<Integer>> spinnerList;
	
	/**
	 * The label showing the number of cards the player has to discard.
	 */
	@FXML private Label amountOfResourcesToDiscardLabel;
	
	/**
	 * The label showing the sentence of drop resources cards part 1
	 */
	@FXML private Label dropResourcesCardsLabelPart_1;
	
	/**
	 * The label showing the sentence of drop resources cards part 2
	 */
	@FXML private Label dropResourcesCardsLabelPart_2;
	
	/**
	 * The button to send the selected resources to the server.
	 */
	@FXML private Button okButton;
	
	/**Binds the text on the buttons to the language selected in the settings*/
	@FXML
	private void initialize() {
		okButton.textProperty().bind(ApplicationInstance.getInstance().createStringBinding("OK"));
	}
	
	/**
	 * Sends a request to discard the resources selected with the spinners.
	 */
	@FXML
	private void discardResources() {
		Resources resources = new Resources();
		for (Spinner<Integer> spinner : spinnerList) {
			ResourceType type = ResourceType.valueOf(spinner.getId());
			int amount = (int) spinner.getValue();
			resources.getResources().put(type, amount);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Attributes.SUBMIT.toString(), (new Mapper()).writeValueAsJson(resources));
		client.sendToServer(ServerTypes.LOSE_RESOURCE.toString(), jsonObject);
		
		for (Spinner<Integer> spinner : spinnerList) {
			spinner.getValueFactory().setValue(0);
		}
		okButton.setDisable(true);
	}
	
	/**
	 * Binds the spinners, so that the player can't select more cards of a {code ResourceType}
	 * than available.
	 */
	private void addBindings() {
		amountOfResourcesToDiscardLabel.textProperty().bind(
				player.resourceQuantityProperty().divide(2).asString());
		dropResourcesCardsLabelPart_1.textProperty().bind(
				ApplicationInstance.getInstance().createStringBinding("DROP_CARDS_1"));
		dropResourcesCardsLabelPart_2.textProperty().bind(
				ApplicationInstance.getInstance().createStringBinding("DROP_CARDS_2"));
		
		for (Spinner<Integer> spinner : spinnerList) {
			 final SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory;
             valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0);
             Resources resources = client.getUser().getPlayer().getResources();
             valueFactory.maxProperty().bind(Bindings.createIntegerBinding(()-> {
            	 return resources.getResources().get(ResourceType.valueOf(spinner.getId()));
             },resources.getResources()));
             spinner.setValueFactory(valueFactory);
		}
		
		for (Spinner<Integer> spinner1 : spinnerList) {
			spinner1.valueProperty().addListener((observable) -> {
				int totalCards = 0;
				for (Spinner<Integer> spinner : spinnerList) {
					totalCards += spinner.getValue();
				}
				okButton.setDisable(totalCards != player.getResourceQuantity()/2);
			});
		}
	}

	@Override
	public void doInitializations(Player player, ClientConnection client) {
		this.player = player;
		this.client = client;
		addBindings();
	}
}
