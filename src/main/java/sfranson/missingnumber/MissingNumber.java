package sfranson.missingnumber;

import java.util.Arrays;

/**
 * Class to find the missing number in an array of numbers.
 */
public class MissingNumber {

	/**
	 * Finds a missing number from a set of unordered numbers.
	 * 
	 * @param input
	 *            - array of input numbers.
	 * @return - the missing number or -1 if there are no missing numbers;
	 */
	public int findMissing(int[] input) {

		int max = Arrays.stream(input).max().getAsInt();

		boolean[] found = new boolean[max];

		for (int i : input) {
			found[i - 1] = true;
		}

		int missing = -1;
		for (int i = 0; i < found.length; i++) {
			if (!found[i]) {
				missing = i + 1;
				break;
			}
		}

		return missing;
	}

}
