package game;

import game.board.construction.Construction;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Responsible for creating and playing an animation when building {@code Constructions}.
 * @author Christoph Hermann
 * @see Construction
 */
public abstract class Animations {

	/**
	 * Makes the specified node blink for a short duration.
	 * @param node the node.
	 */
	public static void playFadeTransition(Node node) {
		FadeTransition fade = new FadeTransition(Duration.millis(250), node);
		fade.setFromValue(0.3);
		fade.setToValue(1.0);
		fade.setCycleCount(11);
		fade.setAutoReverse(true);

		fade.play();
	}

}
