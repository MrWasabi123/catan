package main;

import java.util.Locale;
import java.util.ResourceBundle;

import game.player.Player;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import users.HumanUser;
import users.User;

/**
 * Represents an instance of this Settlers of Catan application. Because this class follows the Singleton-Pattern, only
 * one instance of this class can be created per application.
 * @author Christoph Hermann
 */
public final class ApplicationInstance {

	/**
	 * The {@code User} associated with this instance of the application.
	 * @see User
	 */
	private final User user = new HumanUser();

	/**
	 * The one instance of this class. (See: Singleton Design Pattern.)
	 */
	private static final ApplicationInstance INSTANCE = new ApplicationInstance();

	/**
	 * The currently used locale.
	 */
	private Locale locale = new Locale("en","US");

	/**
	 * The property of the resource bundle.
	 */
	private ObjectProperty<ResourceBundle> bundleProperty = new SimpleObjectProperty<ResourceBundle>(
			ResourceBundle.getBundle("internationalization.MessagesBundle", locale));

	/**
	 * Creates a new instance of this application.
	 */
	private ApplicationInstance() {
		// This constructor is private to prevent any outside instantiation of this class.
	}
	
	/**
	 * Creates a StringBinding to bind the textProperty of a Label to the 
	 * bundleProperty, that is the language, of this application instance.
	 * @param key the key value of the bundle the property is bound to. 
	 * @return  the String binding.
	 */
	public StringBinding createStringBinding(String key) {
        return Bindings.createStringBinding(() -> {
        	return bundleProperty.get().getString(key);
        }, bundleProperty);
    }

	/**
	 * Returns the one instance of this class. (See: Singleton Design Pattern.)
	 * @return the instance.
	 */
	public static ApplicationInstance getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns the {@code User} associated with this instance of the application.
	 * @return the {@link User}.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Returns the {@code Player} controlled by the {@code User} associated with this instance of the application.
	 * @return the {@link Player}.
	 * @see User
	 */
	public Player getPlayer() {
		return user.getPlayer();
	}

	/**
	 * Returns the locale.
	 * @return the locale.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Changes the language of the game by creating a new bundle with the given locale. 
	 * @param newLocale the Locale of the new language
	 */
	public void changeLocale(Locale newLocale) {
		bundleProperty.set(ResourceBundle.getBundle("internationalization.MessagesBundle", newLocale));
	}
	
	/**
	 * Getter for the Property of the ResourceBundle containing the languages.
	 * @return the bundle property
	 */
	public ObjectProperty<ResourceBundle> getBundleProperty() {
		return bundleProperty;
	}

}
