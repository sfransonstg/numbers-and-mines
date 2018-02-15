package sfranson.minesweeper;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Represents a mine sweeper board.
 */
public class Board {

	/**
	 * Indicates the board has been closed for further modification.
	 * 
	 * @see #close()
	 */
	private boolean closed;

	/**
	 * Specified number of columns
	 */
	private int columnCount;

	/**
	 * Internal counter used for calculations.
	 */
	private int currentRow = 0;

	/**
	 * Identifier of the board.
	 */
	private String id;

	/**
	 * Specified number of rows in the board.
	 */
	private int rowCount;

	/**
	 * Rows of the board.
	 * 
	 * @see Row
	 */
	private Row[] rows;

	/**
	 * Flag indicating that input column definitions should be interpreted as
	 * column-oriented instead of row oriented.
	 */
	private boolean rowsAsColumns = false;

	/**
	 * Convenience constructor, used for testing.
	 */
	Board() {
	};

	/**
	 * Constructs a new board with the given dimensions.
	 * 
	 * <p>
	 * Handles scenarios where the number of rows should actually be interpreted as
	 * the number of columns.
	 * </p>
	 * 
	 * <p>
	 * In this case the usage of the class is as follows:
	 * 
	 * <code>
	 * Board board = new Board("newId", cols, rows);
	 * board.rowsAsColumns();
	 * board.withColumns(...).close();
	 * </code>
	 * </p>
	 * 
	 * @param id
	 *            identifier for the board.
	 * @param rows
	 *            number of rows in the board.
	 * @param columns
	 *            number of columns in the board.
	 */
	public Board(String id, int rows, int columns) {
		as(id, rows, columns);
	}

	/**
	 * Convenience method, mainly for testing...
	 * 
	 * @param id
	 * @param columns
	 * @param rows
	 */
	Board as(String id, int rows, int columns) {
		this.id = id;
		this.columnCount = columns;
		this.rowCount = rows;
		initRows();
		return this;
	}

	private void calculateRows() {
		int rowRange = rowsAsColumns ? columnCount : rowCount;
		for (int row = 0; row < rowRange; row++) {
			rows[row].close();
		}
	}

	/**
	 * Terminal operation. Once closed, no additional rows will be allowed.
	 * 
	 * @return board instance useful for method chaining.
	 * @see #print(OutputStream)
	 */
	public Board close() {
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
		return Integer.toString(rows[actualRow].cellValue(actualCol));
	}

	/**
	 * Gets the specified column count.
	 */
	public int getColumnCount() {
		return rowsAsColumns ? rowCount : columnCount;
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

	private void initRows() {
		rows = new Row[rowCount];
		for (int row = 0; row < rowCount; row++) {
			Row newRow = new Row(row, columnCount);
			if (row > 0) {
				newRow.withPrev(rows[row - 1]);
			}
			rows[row] = newRow;
		}
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

		int rowRange = rowCount;
		for (int row = 0; row < rowRange; row++) {
			print.println(rows[row].print());
		}
		print.println();
		print.flush();
	}

	private void processRow(String rowPattern) {
		if (rowsAsColumns) {
			for (Row row : rows) {
				row.withColumns(rowPattern, true);
			}
		} else {
			rows[currentRow].withColumns(rowPattern, rowsAsColumns);
		}
	}

	/**
	 * Modifier that will cause the specified row and column counts to be swapped,
	 * indicating that {@link #withRow(String) row definitions} will be interpreted
	 * as column-oriented, instead of row-oriented.
	 * 
	 * @return board instance useful for chaining.
	 */
	public Board rowsAsColumns() {
		rowsAsColumns = true;
		return this;
	}

	/**
	 * Indicates if the rows-as-columns modifier has been set for the board.
	 * @return
	 */
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
	public Board withRow(String rowPattern) {
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
	 * @param rows row patterns that will define the board.
	 * @return board instance useful for method chaining.
	 */
	public Board withRows(String... rows) {
		for (String row : rows) {
			withRow(row);
		}
		return this;
	}
}
