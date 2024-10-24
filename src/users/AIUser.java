package users;

import java.util.ArrayList;
import java.util.List;

import game.ai.Ai;
import javafx.scene.paint.Color;
import network.client.ClientConnection;
import users.usernamegenerator.UsernameGenerator;

/**
 * Represents a user with an artificial intelligence.
 * @author Christoph Hermann
 * @see User
 */
public class AIUser extends User {

	/**
	 * The string witch will be added to the name of all AIUsers.
	 */
	private static final String NAME_AFFIX = " (AI)";

	/**
	 * The list of available colors.
	 */
	private List<Color> availableColors = new ArrayList<>();

	/**
	 * The {@code Ai} component used by this AIUser.
	 * @see Ai
	 */
	private Ai ai;

	/**
	 * Creates a new AIUser who can chose its color from among the specified colors.
	 * @param availableColors the list of colors this AIUser can chose from.
	 */
	public AIUser(List<Color> availableColors) {
		super(); 

		this.availableColors = availableColors;
		this.ai = new Ai();

		setName();
	}

	/**
	 * Sets the name of this AIUser.
	 */
	private void setName() {
		String username = UsernameGenerator.getRandomUsername() + NAME_AFFIX;
		name.set(username);
	}

	/**
	 * Chooses a different color than the one currently selected.
	 */
	public void chooseDifferentColor() {
		if (color.get() == null) {
			color.set(availableColors.get(0));
		} else {
			for (int i=0; i < availableColors.size(); i++) {
				if (color.get() == availableColors.get(i)) {
					// Select the next color in the list. Loop around to the first element if the end of the list is reached. 
					int nextColorId = (i+1) % availableColors.size();
					Color nextColor = availableColors.get(nextColorId);

					// Set the new color.
					color.set(nextColor);

					break;
				}
			}
		}
	}

	/**
	 * Returns the {@code Ai} component used by this AIUser.
	 * @return the {@link Ai} component.
	 */
	public Ai getAi() {
		return ai;
	}

	@Override
	public void setClient(ClientConnection client) {
		super.setClient(client);

		ai.setClient(client);
	}

}
