package game.dice;

import java.util.Random;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Represents the two dice used in Settlers of Catan.
 * @author Christoph Hermann
 */
public class Dice {

	/**
	 * The number on the first die.
	 */
	private IntegerProperty dieOneNumber = new SimpleIntegerProperty(1);

	/**
	 * The number on the second die.
	 */
	private IntegerProperty dieTwoNumber = new SimpleIntegerProperty(1);

	/**
	 * Rolls the dice and adds the rolled numbers together.
	 * @return the sum of the numbers on the two dice.
	 */
	public int roll() {
		Random random = new Random();

		dieOneNumber.set(random.nextInt(6) + 1);
		dieTwoNumber.set(random.nextInt(6) + 1);

		int sum = dieOneNumber.get() + dieTwoNumber.get();

		return sum;
	}

	/**
	 * Sets the dice to the specified values.
	 * @param dieOne the value of the first die.
	 * @param dieTwo the value of the second die.
	 */
	public void setDice(int dieOne, int dieTwo){
		dieOneNumber.set(dieOne);
		dieTwoNumber.set(dieTwo);
	}

	/**
	 * Returns the number on the first die.
	 * @return the first number.
	 */
	public final int getDieOneNumber() {
		return dieOneNumber.get();
	}

	/**
	 * Returns the number on the second die.
	 * @return the second number.
	 */
	public final int getDieTwoNumber() {
		return dieTwoNumber.get();
	}

	/**
	 * Returns the number property of the first die.
	 * @return the first die property.
	 */
	public IntegerProperty dieOneNumberProperty() {
		return dieOneNumber;
	}

	/**
	 * Returns the number property of the second die.
	 * @return the second die property.
	 */
	public IntegerProperty dieTwoNumberProperty() {
		return dieTwoNumber;
	}

}
