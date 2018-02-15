package sfranson;

import sfranson.minesweeper.MineSweeper;
import sfranson.missingnumber.MissingNumber;

/**
 * Application that executes sample runs of the MissingNumbers class and
 * MineSweeper application.
 */
public class NumbersAndMinesApplication {

	public static void main(String[] args) {
		MissingNumber numbers = new MissingNumber();

		System.out.println("**** Running Missing Numbers ****");
		System.out.println("Missing number: " + numbers.findMissing(new int[] { 2, 5, 1, 7, 8, 6, 3 }));

		System.out.println("**** Running Mine Sweeper ****");
		MineSweeper.main(new String[] { "/minesweeper-input.txt" });

		System.out.println("**** Running Mine Sweeper w/ rows as columns ****");
		MineSweeper.main(new String[] { "/minesweeper-input-columns.txt", "columns" });
	}
}
