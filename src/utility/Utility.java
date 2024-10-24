package utility;
/**
 * class for some  methods to fill array with 0 or null
 */
public class Utility {

	/**
	 * checks if an Array of Integers has value &gt; 0
	 * @param array array of Integers
	 * @return true, if all values &lt;= 0. false, otherwise
	 */
	public static boolean allNull(int[] array){
		for(int i = 0; i < array.length; i++){
			if(array[i] > 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * checks if an array of ints has an value &lt; 0
	 * @param array list of Integers
	 * @return true, if all values are &gt;= 0. false, otherwise
	 */
	public static boolean allPositive(int[] array) {
		for(int i=0; i<array.length; i++){
			if(array[i] < 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * gets the Index of the biggest value in the given array
	 * @param array array of Integers
	 * @return index of the biggest value
	 */
	public static int maxIndex(int[] array) {
		int value = array[0];
		int j = 0;
		for(int i =1; i< array.length; i++){
			if(value < array[i]){
				value = array[i];
				j = i;
			}
		}
		return j;
	}
	
	/**
	 * gets the Index of the smallest value in the given array
	 * @param array array of Integers
	 * @return index of the smallest value
	 */
	public static int minIndex(int[] array) {
		int value = array[0];
		int j = 0;
		for(int i =1; i< array.length; i++){
			if(value >= array[i]){
				value = array[i];
				j = i;
			}
		}
		return j;
	}
	

	/**
	 * adds all values of an int array
	 * @param array array of Integers
	 * @return value sum of all values in the array
	 */
	public static int arraySum(int[] array){
		int value = 0;
		for(int i=0; i< array.length; i++){
			value += array[i];
		}
		return value;
	}

	/**
	 * checks if the value occurs more then once in the given array
	 * @param value value to compare
	 * @param array array of Integers
	 * @return true or false
	 */
	public static boolean hasEqual(int value, int[] array) {
		boolean flag = false;
		for(int i=0; i < array.length; i++){
			if(!flag && value == array[i]){
				flag = true;
			} else if(flag && value == array[i]){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * gets the minimum value of the array
	 * @param array array of Integers
	 * @return minimum value
	 */
	public static int min(int[] array){
		int value = array[0];
		for(int i =1; i< array.length; i++){
			if(value > array[i]){
				value = array[i];
			}
		}
		return value;
	}

	/**
	 * gets the maximum value of the array
	 * @param array array of Integers
	 * @return maximum value
	 */
	public static int max(int[] array) {
		int value = array[0];
		for(int i =1; i< array.length; i++){
			if(value < array[i]){
				value = array[i];
			}
		}
		return value;
	}
}
