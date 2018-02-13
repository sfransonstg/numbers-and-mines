package sfranson.minesweeper;

import java.io.OutputStream;
import java.io.PrintWriter;

public class MineSweeperBoard {

	private int[][] board;

	/**
	 * Indicates the board has been closed for further modification.
	 * 
	 * @see #close()
	 */
	private boolean closed;

	private int columnCount;
	private int currentCol = 0;
	private int currentRow = 0;
	private String id;
	private int rowCount;

	private boolean rowsAsColumns = false;

	/**
	 * Convenience constructor, used for testing.
	 */
	MineSweeperBoard() {
	};

	public MineSweeperBoard(String id, int rows, int columns) {
		as(id, rows, columns);
	}

	/**
	 * Convenience method, mainly for testing...
	 * 
	 * @param id
	 * @param columns
	 * @param rows
	 */
	MineSweeperBoard as(String id, int rows, int columns) {
		this.id = id;
		this.columnCount = columns;
		this.rowCount = rows;
		board = new int[rows][columns];
		return this;
	}

	private void calculateCurrentRow() {
		int length = columnCount;
		for (currentCol = 0; currentCol < length; currentCol++) {
			if (board[currentRow][currentCol] < 0) { // MINE!!!
				incrementAdjacent();
			}
		}
	}

	private void calculateRows() {
		currentRow = 0;
		for (currentRow = 0; currentRow < rowCount; currentRow++) {
			calculateCurrentRow();
		}
	}

	private boolean canIncrement(int row, int col) {
		boolean validRow = row >= 0 && row < rowCount;
		boolean validCol = col >= 0 && col < columnCount;
		return validRow && validCol && board[row][col] >= 0;
	}

	/**
	 * Terminal operation. Once closed, no additional rows will be allowed.
	 * 
	 * @return board instance useful for method chaining.
	 * @see #print(OutputStream)
	 */
	public MineSweeperBoard close() {
		if (!closed) {
			calculateRows();
			closed = true;
		}
		return this;
	}

	/**
	 * Convenience method mainly used for testing.
	 * 
	 * @param row
	 *            zero-based index of the row
	 * @param col
	 *            zero-based index of the column
	 * @return raw cell value at row and column. Cells with a mine will return "-1".
	 */
	String get(int row, int col) {
		int actualRow = rowsAsColumns ? col : row;
		int actualCol = rowsAsColumns ? row : col;
		return Integer.toString(board[actualRow][actualCol]);
	}

	/**
	 * Gets the specified column count.
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * Gets the board id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the specified row count.
	 */
	public int getRowCount() {
		return rowCount;
	}

	private void increment(int row, int col) {
		if (canIncrement(row, col)) {
			board[row][col]++;
		}
	}

	private void incrementAdjacent() {
		incrementLeft();
		incrementRight();
		incrementTop();
		incrementTopLeft();
		incrementTopRight();
		incrementBottom();
		incrementBottomLeft();
		incrementBottomRight();
	}

	private void incrementBottom() {
		increment(rowBelowIdx(), currentCol);
	}

	private void incrementBottomLeft() {
		increment(rowBelowIdx(), leftCellIdx());
	}

	private void incrementBottomRight() {
		increment(rowBelowIdx(), rightCellIdx());
	}

	private void incrementLeft() {
		increment(currentRow, leftCellIdx());
	}

	private void incrementRight() {
		increment(currentRow, rightCellIdx());
	}

	private void incrementTop() {
		increment(rowAboveIdx(), currentCol);
	}

	private void incrementTopLeft() {
		increment(rowAboveIdx(), leftCellIdx());
	}

	private void incrementTopRight() {
		increment(rowAboveIdx(), rightCellIdx());
	}

	private int leftCellIdx() {
		return currentCol - 1;
	}

	/**
	 * Terminal operation that will print the calculated hints to the OutputStream.
	 * 
	 * @param out
	 *            stream to write hints.
	 */
	public void print(OutputStream out) {
		// ensure board is closed.
		close();

		PrintWriter print = new PrintWriter(out);

		print.println("Mine Field #" + id + ":");

		int rowRange = rowsAsColumns ? columnCount : rowCount;
		int colRange = rowsAsColumns ? rowCount : columnCount;

		for (int row = 0; row < rowRange; row++) {

			for (int col = 0; col < colRange; col++) {
				int cell = board[row][col];
				print.print(cell == -1 ? "*" : cell);
			}
			print.println();
		}
		print.println();
		print.flush();
	}

	private void processRow(String rowPattern) {
		if (rowPattern != null && !"".equals(rowPattern)) {
			String[] columns = rowPattern.split("");

			int colRange = rowsAsColumns ? rowCount : columnCount;

			if (columns.length != colRange) {
				throw new MineSweeperException("Invalid input.  Number of columns doesn't match.");
			}
			for (int col = 0; col < columns.length; col++) {
				String cell = columns[col];
				if (rowsAsColumns) {
					board[col][currentRow] = "*".equals(cell) ? -1 : 0;
				} else {
					board[currentRow][col] = "*".equals(cell) ? -1 : 0;
				}
			}
		}
	}

	private int rightCellIdx() {
		return currentCol + 1;
	}

	private int rowAboveIdx() {
		return currentRow - 1;
	}

	private int rowBelowIdx() {
		return currentRow + 1;
	}

	public MineSweeperBoard rowsAsColumns() {
		rowsAsColumns = true;
		board = new int[columnCount][rowCount];
		return this;
	}
	
	public boolean usingRowsAsColumns() {
		return rowsAsColumns;
	}

	/**
	 * Adds a row to the board. Rows that exceed the number of rows for the board
	 * will be ignored.
	 * 
	 * @param rowPattern
	 * @return board instance useful for method chaining.
	 */
	public MineSweeperBoard withRow(String rowPattern) {
		if (closed) {
			throw new MineSweeperException("Board has been closed.  No additional rows may be added.");
		}
		if (currentRow < rowCount) {
			processRow(rowPattern);
			currentRow++;
		} // else ignore row
		return this;
	}

	/**
	 * Convenience method to allow more than one row to be entered in.
	 * 
	 * @param rows
	 * @return board instance useful for method chaining.
	 */
	public MineSweeperBoard withRows(String... rows) {
		for (String row : rows) {
			withRow(row);
		}
		return this;
	}
}
