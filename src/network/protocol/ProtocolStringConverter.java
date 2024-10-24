package network.protocol;

import org.json.JSONObject;

import game.resources.ResourceType;
import game.resources.Resources;
import javafx.scene.paint.Color;

/**
 * A collection of functions for converting protocol Strings to other 
 * Objects and vice versa.  
 * @author Christoph Hermann
 */
public class ProtocolStringConverter {

	/**
	 * Returns the name of the specified color.
	 * @param color the color.
	 * @return the name of the color.
	 */
	public static String getName(Color color) {
		if (color == Color.RED) return Attributes.RED.toString();
		if (color == Color.ORANGE) return Attributes.ORANGE.toString();
		if (color == Color.WHITE) return Attributes.WHITE.toString();
		if (color == Color.BLUE) return Attributes.BLUE.toString();
		return null;
	}

	/**
	 * Returns the color which the protocol identifies with the 
	 * specified name.
	 * @param colorName the name of the color.
	 * @return the color.
	 */
	public static Color getColor(String colorName) {
		if (colorName.equals(Attributes.RED.toString())) return Color.RED;
		if (colorName.equals(Attributes.ORANGE.toString())) return Color.ORANGE;
		if (colorName.equals(Attributes.WHITE.toString())) return Color.WHITE;
		if (colorName.equals(Attributes.BLUE.toString())) return Color.BLUE;
		return null;
	}

	/**
	 * Return the name of the specified {@code ResourceType}.
	 * @param resourceType the type of the resource
	 * @return the resourceType
	 */
	public static String getName(ResourceType resourceType) {
		if(resourceType==ResourceType.BRICK) return Attributes.BRICK.toString();
		if(resourceType==ResourceType.GRAIN) return Attributes.GRAIN.toString();
		if(resourceType==ResourceType.LUMBER)return Attributes.LUMBER.toString();
		if(resourceType==ResourceType.ORE) return Attributes.ORE.toString();
		if(resourceType==ResourceType.WOOL)return Attributes.WOOL.toString();
		return null;
	}

	/**
	 * Return the {@code ResourceType} which the protocol identifies 
	 * with the specified type name.
	 * @param resourceType the String representation of the {@link ResourceType}.
	 * @return the resourceType
	 */
	public static ResourceType getResourceType(String resourceType) {
		if(resourceType.contentEquals(Attributes.BRICK.toString())) return ResourceType.BRICK;
		if(resourceType.contentEquals(Attributes.GRAIN.toString())) return ResourceType.GRAIN;
		if(resourceType.contentEquals(Attributes.LUMBER.toString())) return ResourceType.LUMBER;
		if(resourceType.contentEquals(Attributes.ORE.toString())) return ResourceType.ORE;
		if(resourceType.contentEquals(Attributes.WOOL.toString()))return ResourceType.WOOL;
		return null;
	}

	/**
	 * Return the {@code Resources} which the protocol identifies 
	 * with the specified JSONObject.
	 * @param j_resources JSONObject resources
	 * @return resources
	 */
	public static Resources getResources(JSONObject j_resources) {
		int brick,grain,lumber,ore,wool;

		brick = j_resources.optInt(Attributes.BRICK.toString());
		grain = j_resources.optInt(Attributes.GRAIN.toString());
		lumber = j_resources.optInt(Attributes.LUMBER.toString());
		ore = j_resources.optInt(Attributes.ORE.toString());
		wool =  j_resources.optInt(Attributes.WOOL.toString());

		return new Resources(ResourceType.BRICK,brick,ResourceType.GRAIN,grain,ResourceType.LUMBER,lumber,ResourceType.ORE,ore,ResourceType.WOOL,wool);
	}

}
