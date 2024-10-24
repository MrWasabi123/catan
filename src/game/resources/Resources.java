package game.resources;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Represents a group of resources and offers methods for modifying them.
 * @author Christoph Hermann
 */
public class Resources {

	/**
	 * A {@link Map Map} representing the different resources and their amount.
	 */
	private SimpleMapProperty<ResourceType, Integer> resources = 
			new SimpleMapProperty<ResourceType, Integer>(
					FXCollections.observableMap(new HashMap<ResourceType, Integer>()));

	/**
	 * Creates new resources.
	 */
	public Resources() {
		for (ResourceType type: ResourceType.values()) {
			if(!resources.containsKey(type)) {
				resources.put(type, 0);
			}
		}
	}

	/**
	 * Creates new resources containing a certain amount of resources of the specified type.
	 * @param type the {@link ResourceType}.
	 * @param amount the amount of resources of the given type.
	 */
	public Resources(ResourceType type, int amount) {
		this();
		resources.put(type, amount);
	}

	/**
	 * Creates new resources containing a certain amount of resources of the specified types.
	 * @param type1 the first {@link ResourceType}.
	 * @param amount1 the amount of resources of the first type.
	 * @param type2 the second {@link ResourceType}.
	 * @param amount2 the amount of resources of the second type.
	 */
	public Resources(ResourceType type1, int amount1, ResourceType type2, int amount2) {
		this();
		resources.put(type1, amount1);
		resources.put(type2, amount2);
	}

	/**
	 * Creates new resources containing a certain amount of resources of the specified types.
	 * @param type1 the first {@link ResourceType}.
	 * @param amount1 the amount of resources of the first type.
	 * @param type2 the second {@link ResourceType}.
	 * @param amount2 the amount of resources of the second type.
	 * @param type3 the third {@link ResourceType}.
	 * @param amount3 the amount of resources of the third type.
	 */
	public Resources(
			ResourceType type1, int amount1,
			ResourceType type2, int amount2,
			ResourceType type3, int amount3) {
		this();
		resources.put(type1, amount1);
		resources.put(type2, amount2);
		resources.put(type3, amount3);
	}

	/**
	 * Creates new resources containing a certain amount of resources of the specified types.
	 * @param type1 the first {@link ResourceType}.
	 * @param amount1 the amount of resources of the first type.
	 * @param type2 the second {@link ResourceType}.
	 * @param amount2 the amount of resources of the second type.
	 * @param type3 the third {@link ResourceType}.
	 * @param amount3 the amount of resources of the third type.
	 * @param type4 the fourth {@link ResourceType}.
	 * @param amount4 the amount of resources of the fourth type.
	 */
	public Resources(
			ResourceType type1, int amount1, 
			ResourceType type2, int amount2,
			ResourceType type3, int amount3,
			ResourceType type4, int amount4) {
		this();
		resources.put(type1, amount1);
		resources.put(type2, amount2);
		resources.put(type3, amount3);
		resources.put(type4, amount4);
	}

	/**
	 * Creates new resources containing a certain amount of resources of the specified types.
	 * @param type1 the first {@link ResourceType}.
	 * @param amount1 the amount of resources of the first type.
	 * @param type2 the second {@link ResourceType}.
	 * @param amount2 the amount of resources of the second type.
	 * @param type3 the third {@link ResourceType}.
	 * @param amount3 the amount of resources of the third type.
	 * @param type4 the fourth {@link ResourceType}.
	 * @param amount4 the amount of resources of the fourth type.
	 * @param type5 the fourth {@link ResourceType}.
	 * @param amount5 the amount of resources of the fourth type.
	 */
	public Resources(
			ResourceType type1, int amount1, 
			ResourceType type2, int amount2,
			ResourceType type3, int amount3,
			ResourceType type4, int amount4,
			ResourceType type5, int amount5) {
		this();
		resources.put(type1, amount1);
		resources.put(type2, amount2);
		resources.put(type3, amount3);
		resources.put(type4, amount4);
		resources.put(type5, amount5);
	}

	/**
	 * Adds additional resources to the existing ones.  
	 * @param additionalResources the resources to be added.
	 */
	public void add(Resources additionalResources) {
		for (ResourceType type: ResourceType.values()) {
			int oldAmount =  additionalResources.getResources().get(type);
			int newAmount = resources.get(type)+oldAmount;
			resources.put(type, newAmount);
		}
	}

	/**
	 * Create a new {@code Resources} Object and adds the additional ones.
	 * @param addResources the resources to be added.
	 * @return re the new resources
	 */
	public Resources addResources(Resources addResources) {
		Resources newResources = new Resources();

		for (ResourceType type: ResourceType.values()) {
			int oldAmount = addResources.getResources().get(type);
			int newAmount = resources.get(type) + oldAmount;
			newResources.addOneTypeResource(newAmount, type);
		}

		return newResources;
	}

	/**
	 * Subtracts additional resources from the existing ones. If the existing resources doesn't already contain a
	 * {@link ResourceType ResourceType} contained by the additional resources, it will be added (with a negative
	 * value).
	 * @param additionalResources the resources to be subtracted.
	 */
	public void subtract(Resources additionalResources) {
		for (ResourceType type: ResourceType.values()) {
			int oldAmount =  resources.get(type);
			int newAmount =  oldAmount - additionalResources.getResources().get(type);
			resources.put(type, newAmount);
		}
	}

	/**
	 * Create a new resource object and subtract additional resources from the existing ones. If the existing resources doesn't already contain a
	 * {@link ResourceType ResourceType} contained by the additional resources, it will be added (with a negative
	 * value).
	 * @param additionalResources the resources to be subtracted.
	 * @return re new resources list
	 */
	public Resources subtractResources(Resources additionalResources) {
		Resources newResources = new Resources();

		for (ResourceType type: ResourceType.values()) {
			int oldAmount =  resources.get(type);
			int newAmount =  oldAmount - additionalResources.getResources().get(type);
			newResources.addOneTypeResource(newAmount, type);
		}

		return newResources;
	}

	/**
	 * Add resource of one {@code ResourceType} to existing resources
	 * @param resource the resource to be added
	 * @param resourceType the Type of resource which to be added
	 */
	public void addOneTypeResource(int resource, ResourceType resourceType) {
		for(ResourceType type: ResourceType.values()) {
			if(type == resourceType) {
				int oldAmount = resources.get(resourceType);
				int newAmount = oldAmount + resource;
				resources.put(resourceType, newAmount);
				break;
			}
		}
	}

	/**
	 * Subtract resource of one {@code ResourceType} from existing resources
	 * @param resource the resource to be subtracted
	 * @param resourceType the Type of resource which to be subtracted
	 */
	public void subtractOneTypeResource(int resource, ResourceType resourceType) {
		for(ResourceType type: ResourceType.values()) {
			if(type == resourceType) {
				int oldAmount = resources.get(resourceType);
				int newAmount = oldAmount - resource;
				resources.put(resourceType, newAmount);
				break;
			}
		}
	}

	/**
	 * Multiplies the amount of resources by a given value.
	 * @param multiplier the value that the resources are multiplied by.
	 */
	public void multiply(int multiplier) {
		for (ResourceType type: ResourceType.values()) {
			resources.put(type, resources.get(type) * multiplier);
		}
	}

	/**
	 * Sets the amount of resources of each type of this resources object to the amount specified by another resources
	 * object.
	 * @param newResources the other resources object.
	 */
	public void set(Resources newResources) {
		ObservableMap<ResourceType, Integer> map = FXCollections.observableMap(new HashMap<ResourceType, Integer>());
		for (ResourceType type: ResourceType.values()) {
			map.put(type, newResources.getResources().get(type));
		}
		resources.set(map);
	}

	/**
	 * Get {@code ResourceType} of supply and demand when server receives sea trade require
	 * This method is used in serverController for seaTrad 
	 * @return type ResourceType
	 */
	public ResourceType getResourceTypeFromTrad() {
		for(ResourceType type:ResourceType.values()) {
			int value = resources.get(type);
			if(value!=0) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Compares these resources to other resources and calculates if these resources are greater than or equal to the
	 * other ones. 
	 * @param comparedResources the resources to compare against.
	 * @return true, if these resources are greater than or equal to the compared resources. false, otherwise.
	 */
	public boolean isGreaterThanOrEqualTo(Resources comparedResources) {
		for (ResourceType type: ResourceType.values()) {
			if (resources.get(type) < comparedResources.getResources().get(type)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a {@code Map} representing the resources and their amount.
	 * @return the resources and their amount.
	 */
	public SimpleMapProperty<ResourceType, Integer> getResources() {
		return resources;
	}

	/**
	 * Calculates the sum of these resources.
	 * @return the sum of resources.
	 */
	public int getSum() {
		int sum = 0;

		for (Map.Entry<ResourceType, Integer> entry : resources.get().entrySet()) {
			sum += entry.getValue();
		}

		return sum;
	}

	/**
	 * Converts these resources into an integer Array.
	 * @return the integer Array containing the amounts of resources of each type.
	 */
	public int[] convertResources() {
		int[] intResources = new int[5];

		intResources[0] = this.getResources().get(ResourceType.LUMBER);
		intResources[1] = this.getResources().get(ResourceType.WOOL);
		intResources[2] = this.getResources().get(ResourceType.GRAIN);
		intResources[3] = this.getResources().get(ResourceType.BRICK);
		intResources[4] = this.getResources().get(ResourceType.ORE);

		return intResources;
	}

	/**
	 * Converts an array with length 5 into an Resource Object
	 * @param resources list of resources
	 * @return re list of resources
	 */
	public static Resources convertIntToResources(int[] resources) {
		Resources re = new Resources(ResourceType.LUMBER, resources[0],
				ResourceType.WOOL, resources[1],
				ResourceType.GRAIN, resources[2],
				ResourceType.BRICK, resources[3],
				ResourceType.ORE, resources[4]);
		return re;

	}

	/**
	 * converts an int to a resourceType
	 * 0 = "Lumber",
	 * 1 = "Wool",
	 * 2 = "Grain",
	 * 3 = "Brick",
	 * 4 = "Ore".
	 * @param intResource type of resource as integer
	 * @return ResourceType
	 */
	public static ResourceType convertIntToResourceType(int intResource) {
		switch(intResource){
		case 0: return ResourceType.LUMBER;
		case 1: return ResourceType.WOOL;
		case 2: return ResourceType.GRAIN;
		case 3: return ResourceType.BRICK;
		case 4: return ResourceType.ORE;
		default: return null;
		}
	}

}
