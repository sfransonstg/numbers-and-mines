package sfranson.minesweeper;

/**
 * Represents a row of a board.
 */
public class Row {

	/**
	 * Raw representation of the row contents.
	 */
	private Cell[] cells;

	/**
	 * Flag indicating the row cells have been evaluated and no more cells can be
	 * added.
	 */
	private boolean closed;

	/**
	 * Specified number of columns.
	 */
	private int columnCount;

	/**
	 * Internal counter of columns.
	 */
	private int currentCell = 0;

	/**
	 * Next (or bottom) row.
	 */
	private Row next;

	/**
	 * Previous (or top) row.
	 */
	private Row prev;
	/**
	 * Row id.
	 */
	private int row;

	/**
	 * Creates an identified row with the number of cells.
	 * 
	 * @param row
	 * @param cellCount
	 */
	public Row(int row, int cellCount) {
		this.row = row;
		withCellCount(cellCount);
	}

	private void calculate() {
		for (int col = 0; col < cells.length; col++) {
			currentCell = col;
			if (cells[currentCell].isMine()) {
				incrementLeft();
				incrementRight();
				incrementTop();
				incrementTopLeft();
				incrementTopRight();
				incrementBottom();
				incrementBottomLeft();
				incrementBottomRight();
			}
		}
	}

	private boolean canIncrement(int col) {
		return col >= 0 && col < columnCount;
	}

	/**
	 * Gets a cell's {@link Cell#rawValue() raw value}.
	 * 
	 * @param cellIdx
	 *            position of the cell within the row.
	 */
	public int cellValue(int cellIdx) {
		return cells[cellIdx].rawValue();
	}

	/**
	 * Terminal operation that will prohibit further addition of cells to the row.
	 * 
	 * @see #withCells(String)
	 */
	public Row close() {
		if (!closed) {
			calculate();
			closed = true;
		}
		return this;
	}

	/**
	 * Gets the configured row that is next (or below).
	 * 
	 * @see #withNext(Row)
	 */
	public Row getNext() {
		return next;
	}

	/**
	 * Gets the configured row that is previous (or above).
	 * 
	 * @see #withPrev(Row)
	 */
	public Row getPrev() {
		return prev;
	}

	private void increment(int colIdx) {
		if (canIncrement(colIdx)) {
			cells[colIdx].increment();
		}
	}

	private void incrementBottom() {
		if (next != null) {
			next.increment(currentCell);
		}
	}

	private void incrementBottomLeft() {
		if (next != null) {
			next.increment(leftCellIdx());
		}
	}

	private void incrementBottomRight() {
		if (next != null) {
			next.increment(rightCellIdx());
		}
	}

	private void incrementLeft() {
		increment(leftCellIdx());
	}

	private void incrementRight() {
		increment(rightCellIdx());
	}

	private void incrementTop() {
		if (prev != null) {
			prev.increment(currentCell);
		}
	}

	private void incrementTopLeft() {
		if (prev != null) {
			prev.increment(leftCellIdx());
		}
	}

	private void incrementTopRight() {
		if (prev != null) {
			prev.increment(rightCellIdx());
		}
	}

	private int leftCellIdx() {
		return currentCell - 1;
	}

	/**
	 * Prints the interpreted values of the row.
	 *
	 * @see Row#print()
	 */
	public String print() {
		// ensure closed
		close();

		StringBuilder b = new StringBuilder();
		for (Cell cell : cells) {
			b.append(cell.toString());
		}
		return b.toString();
	}

	private int rightCellIdx() {
		return currentCell + 1;
	}

	/**
	 * Modifier that will set the column count for the row.
	 * 
	 * <p>
	 * Warning: the state of the row will be reset, meaning any previously set cell
	 * data will be lost.
	 * </p>
	 * 
	 * @param count
	 */
	public Row withCellCount(int count) {

		if (columnCount != count) {
			columnCount = count;
			cells = new Cell[count];
			for (int col = 0; col < columnCount; col++) {
				cells[col] = new Cell();
			}
		}
		return this;
	}

	/**
	 * Adds a row, defining its cell content based on a cell definition pattern.
	 * 
	 * @param cellDefinition
	 *            definition of a row's cell content.
	 * 
	 * @see #withColumns(String, boolean)
	 */
	public Row withCells(String cellDefinition) {
		return withColumns(cellDefinition, false);
	}

	/**
	 * Adds a row, defining its cell content based on a cell definition pattern.
	 * 
	 * <p>
	 * If the <code>rowAsColumns</code> paramter is true, the pattern will be
	 * interpreted as column-oriented, meaning each character will be the next
	 * column for the row. The row's value will be derived from the specified row
	 * id.
	 * </p>
	 * 
	 * @param cellDefinition
	 *            pattern defining the cell content.
	 * @param rowsAsColumns
	 * @return
	 */
	public Row withColumns(String cellDefinition, boolean rowsAsColumns) {
		if (cellDefinition != null && !"".equals(cellDefinition)) {
			String[] cellDefs = cellDefinition.split("");

			if (rowsAsColumns) {
				cells[currentCell++].rawValue(cellDefs[row]);
			} else {
				if (cellDefs.length != columnCount) {
					throw new MineSweeperException("Invalid input.  Number of columns doesn't match.");
				}
				for (currentCell = 0; currentCell < cellDefs.length; currentCell++) {
					String cell = cellDefs[currentCell];
					cells[currentCell].rawValue(cell);
				}
			}
		}
		return this;
	}

	/**
	 * Establishes the relationship between the row and its "next" (or below) row.
	 * 
	 * @param next
	 *            row that follows the row.
	 */
	public Row withNext(Row next) {
		this.next = next;
		if (next.prev != this) {
			next.withPrev(this);
		}
		return this;
	}

	/**
	 * Establishes the relationship between the row and its "previous" (or above)
	 * row.
	 * 
	 * @param prev
	 *            row that precedes the row.
	 */
	public Row withPrev(Row prev) {
		this.prev = prev;
		if (prev.next != this) {
			prev.withNext(this);
		}
		return this;
	}

}
