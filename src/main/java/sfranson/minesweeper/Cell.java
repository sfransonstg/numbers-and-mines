package sfranson.minesweeper;

/**
 * Single cell withing a board.
 */
public class Cell {

	private int value;

	/**
	 * Increments the value if possible.
	 * 
	 * <p>
	 * {@link #isMine() Mine cells} will not be incremented.
	 */
	public void increment() {
		if (!isMine()) {
			value++;
		}
	}

	/**
	 * Indicates the cell represents a mine.
	 */
	public boolean isMine() {
		return value == -1;
	}

	/**
	 * Gets the raw (i.e. uninterpreted) value of the cell.
	 * 
	 * <p>
	 * {@link #isMine() Mine cells} are represented as -1.
	 * </p>
	 * 
	 * @return -1 if cell is a mine, else the current number of adjacent mine cells.
	 */
	public int rawValue() {
		return value;
	}

	/**
	 * Sets the raw value of the cell based on a cell definition pattern.
	 * 
	 * <p>
	 * <ul>
	 * Patterns:
	 * <li><b>*</b> - mine</li>
	 * <li><b>.</b> - safe cell</li>
	 * </ul>
	 * 
	 * @param value
	 *            cell pattern
	 */
	public void rawValue(String value) {
		this.value = "*".equals(value) ? -1 : 0;
	}

	/**
	 * Returns the interpreted value of the cell, either "*" for a mine, or the
	 * current number of adjacent mine cells.
	 * @see #rawValue(String)
	 */
	public String toString() {
		return isMine() ? "*" : Integer.toString(value);
	}
}
