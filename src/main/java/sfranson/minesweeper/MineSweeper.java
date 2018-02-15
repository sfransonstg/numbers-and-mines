package sfranson.minesweeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Program that will read a set of board definitions from a file and print out
 * the calculated hints.
 */
public class MineSweeper {

	/**
	 * Regex used for detecting the start of a board definition.
	 */
	private static final String BOARD_START_REGEX = "\\d\\s*\\d";

	/**
	 * Reges used for detecting the end of input.
	 */
	private static final String INPUT_END_REGEX = "0\\s*0";

	// package private for testing
	static MineSweeper instance;

	/**
	 * Regex used for detecting a valid cell definition line.
	 */
	private static final String VALID_INPUT_REGEX = "[\\*\\.]*";

	/**
	 * Gets an instance of the program.
	 * 
	 * <p>
	 * Package private to aid in testing.
	 * </p>
	 * 
	 * @param args
	 */
	static MineSweeper instance(String[] args) {
		if (instance == null) {
			return new MineSweeper(args, System.out);
		}
		return instance;
	}

	/**
	 * <ul>
	 * Valid Arguments:
	 * <li>0 - name/path of file containing board definitions.</li>
	 * <li>1 (optional) - "columns" if the board defitions should be interpreted as
	 * column-oriented, rather than row-oriented.</li>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			instance(args).sweep();
		} catch (MineSweeperException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	private OutputStream out;

	private String[] params;

	/**
	 * Creates an instance with a given set of parameters and sets the output
	 * stream.
	 */
	public MineSweeper(String[] params, OutputStream out) {
		this.params = params;
		this.out = out;
	}

	InputStream fileSource(String fileName) throws FileNotFoundException {
		// Try absolute path first
		InputStream source = getResourceAsStream(fileName);
		if (source == null) {
			source = getResourceAsStream("/" + fileName);
		}
		if (source == null) {
			String name = Paths.get(fileName).toAbsolutePath().toString();
			source = new FileInputStream(new File(name));
		}
		return source;
	}

	InputStream getResourceAsStream(String fileName) {
		return getClass().getResourceAsStream(fileName);
	}

	Board processBoard(String input, Board currentBoard) {
		String[] dimensions = input.split("\\s");

		String oldId = currentBoard != null ? currentBoard.getId() : "0";
		int id = Integer.parseInt(oldId) + 1;

		Integer rows = new Integer(dimensions[0]);
		Integer columns = new Integer(dimensions[1]);

		boolean rowsAsColumns = rowsAsColumns();

		int rowCount = rowsAsColumns ? columns : rows;
		int colCount = rowsAsColumns ? rows : columns;

		Board board = new Board(Integer.toString(id), rowCount, colCount);
		if (rowsAsColumns) {
			board.rowsAsColumns();
		}
		return board;
	}

	Board processInput(String input, Board currentBoard) {

		Board boardUsed = null;

		if (input.matches(BOARD_START_REGEX)) {
			if (currentBoard != null) {
				currentBoard.print(out);
			}
			if (!input.matches(INPUT_END_REGEX)) {
				boardUsed = processBoard(input, currentBoard);
			} else {
				boardUsed = null;
			}
			// end
		} else {
			if (input.matches(VALID_INPUT_REGEX)) {
				boardUsed = currentBoard;
				boardUsed.withRow(input);
			} else {
				throw new MineSweeperException(
						"Invalid input.  Cell definition can only inlude the * and . characters.");
			}
		}
		return boardUsed;
	}

	boolean rowsAsColumns() {
		return params.length > 1 && params[1].equals("columns");
	}

	Scanner scanner(String[] params) throws MineSweeperException {
		Scanner scanner = null;
		try {
			InputStream source = fileSource(params[0]);
			scanner = new Scanner(source);
		} catch (Exception e) {
			throw new MineSweeperException("Invalid file name.  First argument must be a path to an input file.");
		}
		return scanner;
	}

	void sweep() {
		Scanner scanner = scanner(params);

		Board currentBoard = null;

		do {
			String input = scanner.nextLine();
			currentBoard = processInput(input, currentBoard);
		} while (scanner.hasNextLine() && currentBoard != null);
	}
}
