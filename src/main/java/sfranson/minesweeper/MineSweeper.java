package sfranson.minesweeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Scanner;

public class MineSweeper {

	private static final String BOARD_START_REGEX = "\\d\\s*\\d";
	private static final String INPUT_END_REGEX = "0\\s*0";
	// package private for testing
	static MineSweeper instance;

	private static final String VALID_INPUT_REGEX = "[\\*\\.]*";

	public static MineSweeper instance(String[] args) {
		if (instance == null) {
			return new MineSweeper(args, System.out);
		}
		return instance;
	}

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

	MineSweeperBoard processBoard(String input, MineSweeperBoard currentBoard) {
		String[] dimensions = input.split("\\s");

		String oldId = currentBoard != null ? currentBoard.getId() : "0";
		int id = Integer.parseInt(oldId) + 1;

		Integer rows = new Integer(dimensions[0]);
		Integer columns = new Integer(dimensions[1]);

		boolean rowsAsColumns = rowsAsColumns();

		int rowCount = rowsAsColumns ? columns : rows;
		int colCount = rowsAsColumns ? rows : columns;

		MineSweeperBoard board = new MineSweeperBoard(Integer.toString(id), rowCount, colCount);
		if (rowsAsColumns) {
			board.rowsAsColumns();
		}
		return board;
	}

	MineSweeperBoard processInput(String input, MineSweeperBoard currentBoard) {

		MineSweeperBoard boardUsed = null;

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

		MineSweeperBoard currentBoard = null;

		do {
			String input = scanner.nextLine();
			currentBoard = processInput(input, currentBoard);
		} while (scanner.hasNextLine() && currentBoard != null);
	}
}
