package network;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import game.board.Board;
import game.board.construction.Construction;
import game.board.construction.localities.City;
import game.board.construction.localities.Settlement;
import game.board.construction.roads.Road;
import game.board.harbors.Harbor;
import game.board.harbors.HarborType;
import game.board.hexes.Hex;
import game.cards.DevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCard;
import game.cards.playabledevelopmentcards.PlayableDevelopmentCardType;
import game.cards.victorypointcards.VictoryPointCard;
import game.player.Player;
import game.resources.ResourceType;
import game.resources.Resources;
import game.trade.Trade;
import javafx.scene.paint.Color;
import network.protocol.Attributes;
import network.protocol.ProtocolStringConverter;
import users.User;

/**
 * Provides methods for converting different game objects to a {@code JSONObject}.
 * @author Wanja Sajko
 * @author Cornelia Sedlmeir-Hofmann
 * @author Christoph Hermann
 * @author Yize Sun
 * @see JSONObject
 */
public class Mapper {

	/**
	 * Converts a {@code Board} to a {@code JSONObject}.
	 * @param board 
	 *             the {@link Board} being converted
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Board board){
		JSONObject jo = new JSONObject();

		// Convert the hexes to JSONObjects.
		List<Hex> hexes = board.getHexes();
		JSONArray jaHex = new JSONArray();
		for (int i=0; i < hexes.size(); i++){
			jaHex.put(writeValueAsJson(hexes.get(i)));	
		}
		jo.put(Attributes.HEXES.toString(), jaHex);

		// Convert the constructions to JSONObjects.
		List<Construction> constructions = board.getConstructions();
		JSONArray jaCon = new JSONArray();
		for (int i=0; i < constructions.size(); i++) {
			jaCon.put(writeValueAsJson(constructions.get(i)));	
		}
		jo.put(Attributes.CONSTRUCTIONS.toString(), jaCon);

		// Convert the harbors to JSONObjects.
		ArrayList<Harbor> harbors = board.getHarbors();
		JSONArray jaHar = new JSONArray();
		for (int i=0; i < harbors.size(); i++){
			if (harbors.get(i).getType() != HarborType.NONE) {
				jaHar.put(writeValueAsJson(harbors.get(i)));
			}
		}
		jo.put(Attributes.HARBORS.toString(), jaHar);

		// Convert the robber to a JSONObject.
		//		jo.put(Attributes.ROBBER.toString(), board.getRobber().getPosition().getLocality());
		JSONObject jaRobber = new JSONObject();
		jaRobber = writeValueRobberAsJson(board.getRobber().getPosition());
		jo.put(Attributes.ROBBER.toString(), jaRobber);
		return jo;
	}

	/**
	 * Converts a Robber to a {@code JSONObject}. 
	 * @param robber 
	 *             the {@code Robber}
	 * 
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueRobberAsJson(Hex robber) {
		JSONObject position = new JSONObject();

		position.put(Attributes.X.toString(), robber.getxPosAxialHex());
		position.put(Attributes.Y.toString(), robber.getyPosAxialHex());

		return position;
	}

	/** 
	 * Converts a {@code Hex} to a {@code JSONObject}.
	 * robber position
	 * @param hex
	 *            the {@link Hex} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueRobAsJson(Hex hex) {
		JSONObject position = new JSONObject();
		JSONObject object = new JSONObject();

		position.put(Attributes.X.toString(), hex.getxPosAxialHex());
		position.put(Attributes.Y.toString(), hex.getyPosAxialHex());
		object.put(Attributes.ROBBER.toString(), position);

		return object;
	}

	/**
	 * Converts a {@code Harbor} to a {@code JSONObject}.
	 * @param harbor 
	 *              the {@link Harbor} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Harbor harbor) {
		JSONObject object = new JSONObject();
		int hexsNumberNearHarbor = 2;

		//		object.put(Attributes.POSITION.toString(), harbor.getPosition().getEdgeStringPosition());
		JSONArray array = new JSONArray();
		for (int i = 0; i < hexsNumberNearHarbor; i++) {
			JSONObject positionHex = new JSONObject();
			positionHex.put(Attributes.X.toString(),
					harbor.getPosition().getAdjacentHexesOfEdge().get(i).getxPosAxialHex());
			positionHex.put(Attributes.Y.toString(),
					harbor.getPosition().getAdjacentHexesOfEdge().get(i).getyPosAxialHex());
			array.put(positionHex);
		}
		object.put(Attributes.POSITION.toString(), array);
		object.put(Attributes.TYPE.toString(), harbor.getType().toString());

		return object;
	}

	/**
	 * Converts a {@code Construction} to a {@code JSONObject}.
	 * @param construction 
	 *                    the {@link Construction} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Construction construction) {
		JSONObject object = new JSONObject();

		if (construction instanceof Road) {
			object = writeValueAsJson((Road) construction);
		} else if (construction instanceof Settlement) {
			object = writeValueAsJson((Settlement) construction);
		} else if (construction instanceof City) {
			object = writeValueAsJson((City) construction);
		}

		return object;
	}

	/**
	 * Converts a {@code City} to a {@code JSONObject}.
	 * @param city the {@link City} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(City city) {
		JSONObject object = new JSONObject();
		int hexNumberNearCity = 3;

		object.put(Attributes.OWNER.toString(), city.getOwner().getId());
		//		object.put(Attributes.POSITION.toString(), city.getPosition().getCornerLocality());
		JSONArray array = new JSONArray();
		for (int i = 0; i < hexNumberNearCity; i++) {
			JSONObject positionHex = new JSONObject();
			positionHex.put(Attributes.X.toString(),
					city.getPosition().getAdjacentHexesOfCorner().get(i).getxPosAxialHex());
			positionHex.put(Attributes.Y.toString(),
					city.getPosition().getAdjacentHexesOfCorner().get(i).getyPosAxialHex());

			array.put(positionHex);
		}
		object.put(Attributes.POSITION.toString(), array);
		object.put(Attributes.TYPE.toString(), Attributes.CITY.toString());

		return object;
	}

	/**
	 * Converts a {@code Settlement} to a {@code JSONObject}.
	 * @param settlement the {@link Settlement} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Settlement settlement) {
		JSONObject object = new JSONObject();
		int hexNumberNearSettlement = 3;

		object.put(Attributes.OWNER.toString(), settlement.getOwner().getId());
		//		object.put(Attributes.POSITION.toString(), settlement.getPosition().getCornerLocality());
		JSONArray array = new JSONArray();
		for (int i = 0; i < hexNumberNearSettlement; i++) {
			JSONObject positionHex = new JSONObject();
			positionHex.put(Attributes.X.toString(),
					settlement.getPosition().getAdjacentHexesOfCorner().get(i).getxPosAxialHex());
			positionHex.put(Attributes.Y.toString(),
					settlement.getPosition().getAdjacentHexesOfCorner().get(i).getyPosAxialHex());

			array.put(positionHex);
		}

		object.put(Attributes.POSITION.toString(), array);
		object.put(Attributes.TYPE.toString(), Attributes.SETTLEMENT.toString());

		return object;
	}

	/**
	 * Converts a {@code Road} to a {@code JSONObject}.
	 * @param road 
	 *            the {@link Road} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Road road) {
		JSONObject object = new JSONObject();
		int hexNumberNearRoad = 2;

		object.put(Attributes.OWNER.toString(), road.getOwner().getId());
		//		object.put(Attributes.POSITION.toString(), road.getPosition().getEdgeStringPosition());
		JSONArray array = new JSONArray();
		for (int i = 0; i < hexNumberNearRoad; i++) {
			JSONObject positionHex = new JSONObject();
			positionHex.put(Attributes.X.toString(),
					road.getPosition().getAdjacentHexesOfEdge().get(i).getxPosAxialHex());
			positionHex.put(Attributes.Y.toString(),
					road.getPosition().getAdjacentHexesOfEdge().get(i).getyPosAxialHex());
			array.put(positionHex);
		}
		object.put(Attributes.POSITION.toString(), array);
		object.put(Attributes.TYPE.toString(), Attributes.ROAD.toString());

		return object;
	}

	/**
	 * If PLAY_ROAD_BUILDING card is played converts both {@code Road} 
	 * to a {@code JSONObject}.
	 * @param roadOne 
	 * 			the first {@link Road} being converted.
	 * @param roadTwo 
	 * 			the second {@link Road} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueStreetBuildingAsJson(Road roadOne, Road roadTwo) {
		JSONObject object = new JSONObject();
		int hexNumberNearRoad = 2;

		//		object.put(Attributes.OWNER.toString(), roadOne.getOwner().getId());
		//		object.put(Attributes.POSITION.toString(), road.getPosition().getEdgeStringPosition());
		JSONArray arrayOne = new JSONArray();
		for (int i = 0; i < hexNumberNearRoad; i++) {
			JSONObject positionHex = new JSONObject();
			positionHex.put(Attributes.X.toString(),
					roadOne.getPosition().getAdjacentHexesOfEdge().get(i).getxPosAxialHex());
			positionHex.put(Attributes.Y.toString(),
					roadOne.getPosition().getAdjacentHexesOfEdge().get(i).getyPosAxialHex());
			arrayOne.put(positionHex);
		}
		object.put(Attributes.STREAT_ONE.toString(), arrayOne);
		
		if(roadTwo != null){
			JSONArray arrayTwo = new JSONArray();
			for (int i = 0; i < hexNumberNearRoad; i++) {
				JSONObject positionHex = new JSONObject();
				positionHex.put(Attributes.X.toString(),
						roadTwo.getPosition().getAdjacentHexesOfEdge().get(i).getxPosAxialHex());
				positionHex.put(Attributes.Y.toString(),
						roadTwo.getPosition().getAdjacentHexesOfEdge().get(i).getyPosAxialHex());
				arrayTwo.put(positionHex);
			}
			object.put(Attributes.STREAT_TWO.toString(), arrayTwo);
		}

		return object;
	}

	/**
	 * Converts a {@code Hex} to a {@code JSONObject}.
	 * @param hex 
	 *           the {@link Hex} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Hex hex) {
		JSONObject object = new JSONObject();

		//		object.put(Attributes.POSITION.toString(), hex.getLocality());
		JSONObject position = new JSONObject();
		position.put(Attributes.X.toString(), hex.getxPosAxialHex());
		position.put(Attributes.Y.toString(), hex.getyPosAxialHex());
		object.put(Attributes.POSITION.toString(), position);
		object.put(Attributes.TYPE.toString(), hex.getType().toString());

		// Water hexes don't have tokens.
		if (hex.getToken() != null) {
			object.put(Attributes.NUMBER.toString(), hex.getToken().getNumber());
		}

		return object;
	}

	/**
	 * Converts a {@code Player} to a {@code JSONObject}.
	 * @param player 
	 *              the {@link Player} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Player player) {
		JSONObject object = new JSONObject();

		object.put(Attributes.ID.toString(), player.getId());
		object.put(Attributes.COLOR.toString(), ProtocolStringConverter.getName(player.getColor()));
		object.put(Attributes.NAME.toString(), player.getName());
		object.put(Attributes.STATE.toString(), player.getState().toString());
		object.put(Attributes.VICTORY_POINTS.toString(), player.getVictoryPoints());
		// Add resources.
		JSONObject resources = writeValueAsJson(player.getResources());
		object.put(Attributes.RESOURCES.toString(), resources);
		object.put(Attributes.ARMY.toString(), player.getPlayedKnightCards());
		//Add development cards.
		JSONObject j_development_card = writeValueAsJson(player.getDevelopmentCards());
		object.put(Attributes.DEVELOMENTCARDS.toString(), j_development_card);
		object.put(Attributes.LARGEST_ARMY.toString(), player.hasLargestArmyCard());
		object.put(Attributes.LONGEST_ROAD.toString(), player.hasLongestRoadCard());

		return object;
	}

	/**
	 * Converts a {@code Player} to a {@code JSONObject}.
	 * @param player 
	 *              the {@link Player} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJsonByWin(Player player) {
		JSONObject object = new JSONObject();

		object.put(Attributes.ID.toString(), player.getId());
		object.put(Attributes.COLOR.toString(), ProtocolStringConverter.getName(player.getColor()));
		object.put(Attributes.NAME.toString(), player.getName());
		object.put(Attributes.STATE.toString(), player.getState().toString());
		object.put(Attributes.VICTORY_POINTS.toString(), player.getVictoryPoints());
		// Add resources.
		JSONObject resources = writeValueAsJson(player.getResources());
		object.put(Attributes.RESOURCES.toString(), resources);
		object.put(Attributes.ARMY.toString(), player.getPlayedKnightCards());
		//Add development cards.
		JSONObject j_development_card = writeValueAsJson(player.getDevelopmentCards());
		object.put(Attributes.DEVELOMENTCARDS.toString(), j_development_card);
		object.put(Attributes.LARGEST_ARMY.toString(), player.hasLargestArmyCard());
		object.put(Attributes.LONGEST_ROAD.toString(), player.hasLongestRoadCard());

		return object;
	}
	
	/**
	 * Converts a {@code Player} to a {@code JSONObject} with unknown value.
	 * @param player 
	 *              the {@link Player} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeUnknownValueAsJson(Player player) {
		JSONObject object = new JSONObject();

		object.put(Attributes.ID.toString(), player.getId());
		object.put(Attributes.COLOR.toString(), ProtocolStringConverter.getName(player.getColor()));
		object.put(Attributes.NAME.toString(), player.getName());
		object.put(Attributes.STATE.toString(), player.getState().toString());
		object.put(Attributes.VICTORY_POINTS.toString(), player.getVisibleVictoryPoints());

		// Add resources.
		JSONObject resources = new JSONObject();
		resources.put(Attributes.UNKNOWN.toString(), player.getResources().getSum());
		object.put(Attributes.RESOURCES.toString(), resources);

		object.put(Attributes.ARMY.toString(), player.getPlayedKnightCards());

		//Add development cards.
		JSONObject developmentCards = new JSONObject();
		developmentCards.put(Attributes.UNKNOWN.toString(), player.getNumberOfAllDevelopmentCard());
		object.put(Attributes.DEVELOMENTCARDS.toString(), developmentCards);

		object.put(Attributes.LARGEST_ARMY.toString(),player.hasLargestArmyCard());
		object.put(Attributes.LONGEST_ROAD.toString(), player.hasLongestRoadCard());

		return object;
	}

	/**
	 * Converts a {@code User} to a {@code JSONObject}.
	 * @param user 
	 *            the {@link User} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(User user) {
		JSONObject userObject = new JSONObject();

		userObject.put(Attributes.ID.toString(), user.getId());

		if (user.getState() != null) {
			userObject.put(Attributes.STATE.toString(), user.getState().toString());
		}
		if(user.getName()!=null) {
			userObject.put(Attributes.NAME.toString(), user.getName());
		}
		if(user.getColor()!=null) {
			Color color = user.getColor();
			String colorName = ProtocolStringConverter.getName(color);
			userObject.put(Attributes.COLOR.toString(), colorName);
		}
		JSONObject object = new JSONObject();
		object.put(Attributes.PLAYER.toString(), userObject);

		return object;
	}

	/**
	 * Converts {@code Resources} to a {@code JSONObject}.
	 * @param resorces 
	 *                the {@link Resources} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Resources resorces) {
		JSONObject object = new JSONObject();

		for (ResourceType type : ResourceType.values()) {
			int amount = resorces.getResources().get(type);
			String name = ProtocolStringConverter.getName(type);

			if (amount > 0) {
				object.put(name, amount);
			}
		}

		return object;
	}

	/**
	 * Converts {@code Resources} to a {@code JSONObject} with unknown value.
	 * @param resorces 
	 *                the {@link Resources} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJsonUnknownResources(Resources resorces) {
		JSONObject object = new JSONObject();

		object.put(Attributes.UNKNOWN.toString(), resorces.getSum());

		return object;
	}

	/**
	 * Converts developmentCards to a {@code JSONObject} with unknown value.
	 * @param developmentCards 
	 *                        the developmentCards being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJsonUnknownDevelopmentCard(List<DevelopmentCard> developmentCards) {
		JSONObject object = new JSONObject();
		int cardsNumber=0;
		for(DevelopmentCard developmentCard:developmentCards) {
			if(developmentCard!=null) {
				cardsNumber++;
			}
		}
		object.put(Attributes.UNKNOWN.toString(), cardsNumber);
		return object;
	}

	/**
	 * Converts developmentCards to a {@code JSONObject}.
	 * @param developmentCards 
	 *                        the developmentCards being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(List<DevelopmentCard> developmentCards) {
		JSONObject object = new JSONObject();
		int countKNIGHTCards = 0;
		int countMONOPOLYCards = 0;
		int countROAD_BUILDINGCards = 0;
		int countYEAR_OF_PLENTYCards = 0;
		int countVICTORYCards = 0;

		for(DevelopmentCard developmentCard: developmentCards) {
			if(developmentCard instanceof PlayableDevelopmentCard) {
				PlayableDevelopmentCardType developmentCardType = ((PlayableDevelopmentCard) developmentCard).getType();
				switch(developmentCardType){
				case KNIGHT:   countKNIGHTCards++;
				break; 

				case MONOPOLY:    countMONOPOLYCards++;
				break;

				case ROAD_BUILDING:  countROAD_BUILDINGCards++;
				break;

				case YEAR_OF_PLENTY: countYEAR_OF_PLENTYCards++;
				break;
				}
			}
			else {
				if(developmentCard instanceof VictoryPointCard) {
					countVICTORYCards++;}
			}
		}
		object.put(Attributes.KNIGHT.toString(), countKNIGHTCards);
		object.put(Attributes.MONOPOLY.toString(), countMONOPOLYCards);
		object.put(Attributes.ROAD_BUILDING.toString(), countROAD_BUILDINGCards);
		object.put(Attributes.YEAR_OF_PLENTY.toString(), countYEAR_OF_PLENTYCards);
		object.put(Attributes.VICTORY_POINT.toString(), countVICTORYCards);
		return object;	
	}

	/**
	 * Converts a {@code Trade} to a {@code JSONObject}.
	 * @param trade 
	 *             the {@link Trade} being converted.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Trade trade) {
		JSONObject object = new JSONObject();

		object.put(Attributes.PLAYER.toString(), trade.getPlayer().getId());
		object.put(Attributes.TRADING_ID.toString(), trade.getTradeID());
		object.put(Attributes.SUPPLY.toString(), writeValueAsJson(trade.getTradeOffer()));
		object.put(Attributes.DEMAND.toString(), writeValueAsJson(trade.getTradeRequest()));

		return object;
	}

	/**
	 * Converts two {@code Resources} to a {@code JSONObject}.
	 * @param offer 
	 *             the {@link Resources} representing the offer.
	 * @param request 
	 *               the {@link Trade} representing the request.
	 * @return the converted {@link JSONObject}.
	 */
	public JSONObject writeValueAsJson(Resources offer, Resources request) {
		JSONObject object = new JSONObject();

		object.put(Attributes.SUPPLY.toString(), writeValueAsJson(offer));
		object.put(Attributes.DEMAND.toString(), writeValueAsJson(request));

		return object;
	}

	/**
	 * Read the incoming messages and transform the JSONObjects 
	 * to "local" coordinates.
	 * @param jaRobber robber as JSON Object
	 * @param board board
	 * @return position the hex where the robber sits
	 */
	public String readValueRobberFromJSON(JSONObject jaRobber, Board board) {
		String position="";

		int x = (int) jaRobber.get("x");
		int y = (int) jaRobber.get("y");			
		Hex hex = board.searchHex(x,y);
		position=position+hex.getLocality();

		return position;}

	/**
	 * Read the incoming messages and transform the JSONObjects 
	 * to "local" coordinates.
	 * @param jaHex hex as JSON Object
	 * @param board board
	 * @return position of hex 
	 */
	public String readValueHexFromJSON(JSONObject jaHex, Board board) {
		String position="";		

		JSONObject joPosition = jaHex.getJSONObject(Attributes.POSITION.toString());

		int x = (int) joPosition.get("x");
		int y = (int) joPosition.get("y");
		Hex hex = board.searchHex(x,y);
		position=position+hex.getLocality();

		return position;}
	/**
	 * Read the incoming messages and transform the JSONObjects 
	 * to "local" coordinates.
	 * @param object maybe road or settlement
	 * @param board board
	 * @return position of object 
	 */
	public String readValueFromJSON(JSONObject object, Board board) {
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
	 * Read the incoming messages and transform the JSONObjects 
	 * to "local" coordinates.
	 * @param object object as JSON Object
	 * @param board board
	 * @return position of first street to build 
	 */
	public String readValueStreetOneFromJSON(JSONObject object, Board board) {
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
	 * Read the incoming messages and transform the JSONObjects 
	 * to "local" coordinates.
	 * @param object object as JSON Object
	 * @param board board
	 * @return position of second street to build 
	 */
	public String readValueStreetTwoFromJSON(JSONObject object, Board board) {
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
	
    /*
	/**	
	 * Converts {@code Dice} to a {@code JSONObject}.
	 * @param dice the {@link Dice} being converted.
	 * @return the converted {@link JSONObject}.
	 *
	public JSONObject writeValueAsJson(Dice dice) {
		JSONObject object = new JSONObject();

		JSONArray diceValues = new JSONArray();
		diceValues.put(dice.getDieOneNumber());
		diceValues.put(dice.getDieTwoNumber());

		object.put(Attributes.PLAYER.toString(), client.getUser().getPlayer().getId());
		object.put(Attributes.DICE_VALUE.toString(), diceValues);

		return object;
	}
	 */
}
