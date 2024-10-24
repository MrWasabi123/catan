package game.board.robber;

import game.board.Board;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.player.Player;
import game.player.PlayerState;
import interfaces.Controller;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import network.client.ClientConnection;

/**
 * Represents the Controller responsible for controlling the {@code Robber}.
 * @author Svenja Schoett
 * @author Christoph Hermann
 * @see Robber
 */
public class RobberController implements Controller<Robber> {

	/**
	 * The {@code Robber} being controlled by this controller.
	 * @see Robber
	 */
	private Robber robber;

	/**
	 * The ImageView that contains the visualization of the {@code Robber}.
	 * @see Robber
	 */
	@FXML
	private ImageView robberView;

	/**
	 * Connection to the Client
	 */
	private ClientConnection client;

	/**
	 * The initial width of the imageView of the robber.
	 */
	private double width;

	/**
	 * The initial height of the imageView of the robber.
	 */
	private double height;

	/**
	 * Draws the {@code Robber} at its initial position.
	 * @see Robber
	 */
	private void draw() {
		width = robberView.getBoundsInLocal().getWidth();
		height = robberView.getBoundsInLocal().getHeight();

		robberView.setX(robber.getPosition().getxPosCartesianHex()
				- robberView.getBoundsInLocal().getWidth()/2);
		robberView.setY(robber.getPosition().getyPosCartesianHex()
				- robberView.getBoundsInLocal().getHeight()/2);


		DropShadow highlightEffect = new DropShadow();
		highlightEffect.setColor(Color.BLUE);
		Player player = client.getUser().getPlayer();

		robberView.effectProperty().bind(Bindings.createObjectBinding(() -> {
			if (client.getUser().getPlayer().stateProperty().get().equals(PlayerState.MOVE_ROBBER)
					|| (player.currentySelectedDevelopmentCardProperty().get()!=null 
					&& player.currentySelectedDevelopmentCardProperty().get().equals(PlayableDevelopmentCardType.KNIGHT))) {
				robberView.setMouseTransparent(false);
				return highlightEffect;
			} else {
				robberView.setMouseTransparent(true);
				return null;
			}
		}, player.stateProperty(), player.currentySelectedDevelopmentCardProperty()));

		startAnimation();
	}

	/**
	 * Starts the animation that rotates the tornado-robber.
	 */
	private void startAnimation() {
		RotateTransition rotation = new RotateTransition(Duration.seconds(0.5), robberView);
		rotation.setCycleCount(Timeline.INDEFINITE);
		rotation.setInterpolator(Interpolator.LINEAR);
		rotation.setByAngle(0);
		rotation.setToAngle(360);
		rotation.setDuration(new Duration(1500));
		rotation.play();
	}

	/**
	 * Updates the {@code Board} visualization showing where the {@code Robber} is.
	 * @see Board
	 * @see Robber
	 */
	private void bindRobberMovement(){

		robber.getPositionProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				robberView.xProperty().set(robber.getPosition().getxPosCartesianHex() - width/2);

				robberView.yProperty().set(robber.getPosition().getyPosCartesianHex() - height/2);	
			}
		});
	}

	/**
	 * Registers drag events of the robberView.
	 */
	public void setRobberDragAndDrop() {
		robberView.setOnDragDetected((event) -> {
			Dragboard db = robberView.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putString("robber");
			content.putImage(robberView.getImage());
			db.setContent(content);
			robberView.setVisible(false);
		});
		robberView.setOnDragDone(event -> {
			robberView.setVisible(true);
		});
	}

	@Override
	public void doInitializations(Robber robber, ClientConnection client) {
		this.robber = robber;
		this.client = client;
		draw();
		bindRobberMovement();
		setRobberDragAndDrop();
	}

}
