package network.protocol;

import org.json.JSONArray;
import org.json.JSONObject;

import game.board.Board;
import game.board.construction.localities.City;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.hexes.Hex;
import game.resources.ResourceType;
import game.resources.Resources;

/**
 * A collection of functions for converting protocol JSONObjects 
 * to other Objects.  
 * @author Christoph Hermann
 * @author Yize Sun
 */
public class ProtocolJSONObjectConverter {

	/**
	 * Return the {@code Resources} which the protocol identifies 
	 * with the specified JSONObject.
	 * @param j_ressources 
	 *                    JSONObject resources
	 * @return resources
	 */
	public static Resources getResources(JSONObject j_ressources) {

		Resources resources = new Resources();

		int lumber = 0;
		int wool = 0;
		int brick = 0;
		int grain = 0;
		int ore = 0;

		if (j_ressources.has("Holz")) {
			lumber = (int) j_ressources.get("Holz");
		}
		if (j_ressources.has("Wolle")) {
			wool = (int) j_ressources.get("Wolle");
		}
		if (j_ressources.has("Lehm")) {
			brick = (int) j_ressources.get("Lehm");
		}
		if (j_ressources.has("Getreide")) {
			grain = (int) j_ressources.get("Getreide");
		}
		if (j_ressources.has("Erz")) {
			ore = (int) j_ressources.get("Erz");
		}
		resources.getResources().put(ResourceType.LUMBER, lumber);
		resources.getResources().put(ResourceType.WOOL, wool);
		resources.getResources().put(ResourceType.BRICK, brick);
		resources.getResources().put(ResourceType.GRAIN, grain);
		resources.getResources().put(ResourceType.ORE, ore);
		return resources;
	}

	/**
	 * Get {@code Robber} position from JSONObject.
	 * @param jaRobber 
	 *                JSONObject
	 * @param board
	 *             {@code Board} in {@code Game}
	 * @return the Hex the robber is positioned on.
	 */
	public static Hex getRobberPosition(JSONObject jaRobber, Board board) {

		int x = (int) jaRobber.get("x");
		int y = (int) jaRobber.get("y");			
		Hex hex = board.searchHex(x,y);

		return hex;
	}


	/**
	 * Get {@code Hex} position from JSONObject.
	 * @param jaHex
	 *             JSONObject
	 * @param board 
	 *             {@code Board} in {@code Game}
	 * @return the xy position of hexagon in String
	 */
	public static String getHexPosition(JSONObject jaHex, Board board) {
		String position="";		

		JSONObject joPosition = jaHex.getJSONObject(Attributes.POSITION.toString());

		int x = (int) joPosition.get("x");
		int y = (int) joPosition.get("y");
		Hex hex = board.searchHex(x,y);
		position=position+hex.getLocality();

		return position;
	}

	/**
	 * Get the {@code Construction} position from JSONObject.
	 * @param object
	 *              JSONObject
	 * @param board
	 *             {@code Board} in {@code Game}
	 * @return the xy position of hexagons nearby this construction in String
	 * @see Road
	 * @see Settlement
	 * @see City
	 */
	public static String getPosition(JSONObject object, Board board) {
		String position="";

		JSONArray array = new JSONArray();
		array = object.getJSONArray(Attributes.POSITION.toString());

		JSONObject joPosition = new JSONObject();
		for(int i=0;i<array.length();i++) {
			joPosition = (JSONObject) array.get(i);
			int x = (int) joPosition.get("x");
			int y = (int) joPosition.get("y");
			Hex hex = board.searchHex(x,y);
			position=position+hex.getLocality();
		}
		return position;
	}
	
	/**
	 * Get the first {@code Road} position from JSONObject.
	 * @param object
	 *              JSONObject
	 * @param board
	 *             {@code Board} in {@code Game}
	 * @return the xy position of hexagons nearby this road in String
	 * @see Road
	 */
	public static String getStreetOnePosition(JSONObject object, Board board) {
		String position="";

		JSONArray array = new JSONArray();

		array = object.getJSONArray(Attributes.STREAT_ONE.toString());

		JSONObject joPosition = new JSONObject();
		for(int i=0;i<array.length();i++) {
			joPosition = (JSONObject) array.get(i);
			int x = (int) joPosition.get("x");
			int y = (int) joPosition.get("y");
			Hex hex = board.searchHex(x,y);
			position=position+hex.getLocality();
		}
		return position;
	}
	
	/**
	 * Get the second {@code Road} position from JSONObject.
	 * @param object
	 *              JSONObject
	 * @param board
	 *             {@code Board} in {@code Game}
	 * @return the xy position of hexagons nearby this road in String
	 * @see Road
	 */
	public static String getStreetTwoPosition(JSONObject object, Board board) {
		String position="";

		JSONArray array = new JSONArray();
		array = object.getJSONArray(Attributes.STREAT_TWO.toString());

		JSONObject joPosition = new JSONObject();
		for(int i=0;i<array.length();i++) {
			joPosition = (JSONObject) array.get(i);
			int x = (int) joPosition.get("x");
			int y = (int) joPosition.get("y");
			Hex hex = board.searchHex(x,y);
			position=position+hex.getLocality();
		}
		return position;
	}
}
