package game.board.construction.localities;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Animations;
import interfaces.Controller;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import network.client.ClientConnection;
import network.protocol.Attributes;
import network.protocol.ServerTypes;
import network.server.Server;

/**
 * Represents the Controller responsible for controlling a {@code Settlement}.
 * @author Svenja Schoett
 * @author Christoph Hermann
 * @author Paula Wikidal
 * @see Settlement
 */
public class SettlementController implements Controller<Settlement> {

	/**
	 * The settlement being controlled by this controller.
	 */
	private Settlement settlement;

	/**
	 * The client this controller uses to send messages to the {@code Server}
	 * @see ClientConnection
	 * @see Server
	 */
	private ClientConnection client; 

	/**
	 * The ImageView containing the image of the {@code Settlement}.
	 * @see Settlement
	 */
	@FXML private ImageView settlementView;

	/**
	 * Requests to build a {@code City} on the position of this {@code Settlement}.
	 * @see Settlement
	 * @see City
	 */
	@FXML
	public void select() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(Attributes.TYPE.toString(), Attributes.CITY.toString());

		JSONArray array = new JSONArray();
		for (int i = 0; i < 3; i++) {
			JSONObject positionHex = new JSONObject();
			positionHex.put(Attributes.X.toString(),
					settlement.getPosition().getAdjacentHexesOfCorner().get(i).getxPosAxialHex());
			positionHex.put(Attributes.Y.toString(),
					settlement.getPosition().getAdjacentHexesOfCorner().get(i).getyPosAxialHex());

			array.put(positionHex);
		}
		jsonObj.put(Attributes.POSITION.toString(), array);

		client.sendToServer(ServerTypes.BUILD.toString(), jsonObj);
	}

	/**
	 * Makes the {@code Settlement} appear in its owners color.
	 * @see Settlement
	 */
	private void setColor() {
		Blend blend = new Blend(BlendMode.SRC_ATOP);
		Color color = settlement.getOwner().getColor().deriveColor(0, 1, 1, 0.7);
		ColorInput colorInput = new ColorInput(settlementView.getX(), settlementView.getY(), settlementView.getBoundsInParent().getWidth(), 
				settlementView.getBoundsInParent().getHeight()+2, color);
		blend.setTopInput(colorInput);
		Blend multiply = new Blend(BlendMode.MULTIPLY);
		multiply.setBottomInput(blend);
		settlementView.setEffect(blend);

		DropShadow dropShadow = new DropShadow();
		dropShadow.setInput(multiply);

		settlementView.effectProperty().bind(Bindings.createObjectBinding(() -> {
			if(settlementView.isHover()) {
				return dropShadow;
			} else {
				return multiply;
			}
		}, settlementView.hoverProperty()));
	}

	/**
	 * Draws the {@code Settlement} being controlled by this controller.
	 * @see Settlement
	 */
	private void draw() {
		settlementView.xProperty().set(settlement.getPosition().getxPosCartesianCorner()
				- settlementView.getBoundsInLocal().getWidth()/2);
		settlementView.yProperty().set(settlement.getPosition().getyPosCartesianCorner()
				- settlementView.getBoundsInLocal().getHeight()*0.6);

		setColor();

		Animations.playFadeTransition(settlementView);

		if (settlement.getOwner() != client.getUser().getPlayer()) {
			settlementView.setMouseTransparent(true);
		}
	}

	/**
	 * Binds the view component of this controller to its model.
	 */
	private void addBindings() {
		settlementView.visibleProperty().bind(Bindings.createObjectBinding(()-> {
			if (settlement.getPosition().localityProperty().get() instanceof Settlement) {
				return true;
			} else {
				return false;
			}
		}, settlement.positionProperty().get().localityProperty()));
	}

	@Override
	public void doInitializations(Settlement settlement, ClientConnection client) {
		this.settlement = settlement;
		this.client = client;
		addBindings();
		draw();
	}

}
