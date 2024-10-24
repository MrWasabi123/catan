package users.usernamegenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class contains methods for generating random usernames.
 * @author Christoph Hermann
 */
public abstract class UsernameGenerator {

	/**
	 * The path to the file containing all adjectives used by this UsernameGenerator.
	 */
	private static final String PATH_TO_ADJECTIVES = "/users/usernamegenerator/adjectives.txt";

	/**
	 * The path to the file containing all nouns used by this UsernameGenerator.
	 */
	private static final String PATH_TO_NOUNS = "/users/usernamegenerator/nouns.txt";

	/**
	 * The path to the file containing all titles used by this UsernameGenerator.
	 */
	private static final String PATH_TO_TITLES = "/users/usernamegenerator/titles.txt";

	/**
	 * The maximum length of a username.
	 */
	private static final int MAX_NAME_LENGTH = 20;

	/**
	 * The list of all adjectives used by this UsernameGenerator.
	 */
	private static final List<String> ADJECTIVES = generateList(PATH_TO_ADJECTIVES);

	/**
	 * The list of all nouns used by this UsernameGenerator.
	 */
	private static final List<String> NOUNS = generateList(PATH_TO_NOUNS);

	/**
	 * The list of all titles used by this UsernameGenerator.
	 */
	private static final List<String> TITLES = generateList(PATH_TO_TITLES);

	/**
	 * The probability that a randomly generated name will contain a title.
	 */
	private static final double TITLE_PROBABILITY = 0.2;

	/**
	 * The default name this generator will use, if no other name could be created.
	 */
	private static final String DEFAULT_NAME = "Computer";

	/**
	 * The integer which will be added to the default name.
	 */
	private static int id = 1;

	/**
	 * Generates a list of Strings from a file. Each line of the file will be used as a separate String in the list.
	 * @param path the path to the file.
	 * @return the list of Strings containing the lines of the file.
	 */
	private static List<String> generateList(String path) {
		List<String> list = new ArrayList<>();

		try (InputStream stream = UsernameGenerator.class.getResourceAsStream(path);
				InputStreamReader reader = new InputStreamReader(stream);
				BufferedReader bufferdReader = new BufferedReader(reader)) {
			String line;

			while ((line = bufferdReader.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException exception) {
			// No need to do anything here. In the worst case scenario, the list will be empty, which is totally fine.
		}

		return list;
	}

	/**
	 * Returns a randomly generated username.
	 * @return the random username.
	 */
	public static String getRandomUsername() {
		String name;

		do {
			String title = getRandomWord(TITLES);
			String adjective = getRandomWord(ADJECTIVES);
			String noun = getRandomWord(NOUNS);

			name = combineWords(title, adjective, noun);

			// Ensures that all names are unique.
			ADJECTIVES.remove(adjective);
			ADJECTIVES.remove(noun);
		} while (name.length() > MAX_NAME_LENGTH);

		return name;
	}

	/**
	 * Combines the title, the adjective and the noun to a new name. The title will only have a certain probability to
	 * be added to the final name. Therefore not all names will have a title. If the noun or adjective are empty, a
	 * default name will be returned. 
	 * @param title the specified title.
	 * @param adjective the specified adjective.
	 * @param noun the specified noun.
	 * @return the concatenation of the title, the adjective and the noun.
	 */
	private static String combineWords(String title, String adjective, String noun) {
		if (adjective.isEmpty() || noun.isEmpty()) {
			return DEFAULT_NAME + " " + id++;
		} else {
			if (new Random().nextDouble() < TITLE_PROBABILITY) {
				return title + " " + adjective + noun;
			} else {
				return adjective + noun;
			}
		}
	}

	/**
	 * Returns a random word from a list of words. The returned word will be removed from the list to assure that the
	 * same word cannot be chosen again.
	 * @param words the list of words.
	 * @return a random word. If the list of words is empty or {@code null}, the empty string will be returned.
	 */
	private static String getRandomWord(List<String> words) {
		if (words == null || words.isEmpty()) {
			return "";
		} else {
			Random random = new Random();
			int randomIndex = random.nextInt(words.size());
			String word = words.get(randomIndex);

			return word;
		}
	}

}
