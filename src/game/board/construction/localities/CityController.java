package game.board.construction.localities;

import game.Animations;
import interfaces.Controller;
import javafx.fxml.FXML;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import network.client.ClientConnection;

/**
 * Represents the Controller responsible for controlling cities.
 * @author Christoph Hermann
 * @author Paula Wikidal
 * @see City
 */
public class CityController implements Controller<City> {

	/**
	 * The {@code City} being controlled by this controller.
	 * @see City
	 */
	private City city;

	/**
	 * The GUI-element representing the {@code City} controlled by this controller.
	 * @see City
	 */
	@FXML
	private ImageView cityView;

	/**
	 * Draws the {@code City} being controlled by this controller.
	 * @see City
	 */
	private void draw() {
		cityView.xProperty().set(city.positionProperty().get().getxPosCartesianCorner()
				- cityView.getBoundsInLocal().getWidth()/2);
		cityView.yProperty().set(city.positionProperty().get().getyPosCartesianCorner()
				- cityView.getBoundsInLocal().getHeight()*0.6);

		colorizeCity();
		Animations.playFadeTransition(cityView);
	}

	/**
	 * Makes the {@code City} appear in its owners color.
	 * @see City
	 */
	private void colorizeCity() {
		Blend blend = new Blend(BlendMode.SRC_ATOP);
		Color color = city.getOwner().getColor().deriveColor(0, 1, 1, 0.7);
		ColorInput colorInput = new ColorInput(cityView.getX(), cityView.getY(), cityView.getBoundsInParent().getWidth(), 
				cityView.getBoundsInParent().getHeight(), color);
		blend.setTopInput(colorInput);
		Blend multiply = new Blend(BlendMode.MULTIPLY);
		multiply.setBottomInput(blend);
		cityView.setEffect(multiply);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setInput(multiply);
		
	}

	@Override
	public void doInitializations(City city, ClientConnection client) {
		this.city = city;
		draw();
	}

}
