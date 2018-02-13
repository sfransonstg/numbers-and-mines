package sfranson.minesweeper;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import sfranson.minesweeper.MineSweeperBoard;
import sfranson.minesweeper.MineSweeperException;

public class MineSweeperBoardTest {

	private MineSweeperBoard instance;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void board3x3() {
		instance.as("3x3", 3, 3).withRows("...", ".*.", "...").close();

		assertThat(instance.get(0, 0), equalTo("1"));
		assertThat(instance.get(0, 1), equalTo("1"));
		assertThat(instance.get(0, 2), equalTo("1"));
		assertThat(instance.get(1, 0), equalTo("1"));
		assertThat(instance.get(1, 1), equalTo("-1"));
		assertThat(instance.get(1, 2), equalTo("1"));
		assertThat(instance.get(2, 0), equalTo("1"));
		assertThat(instance.get(2, 1), equalTo("1"));
		assertThat(instance.get(2, 2), equalTo("1"));
	}

	@Test
	public void board3x5() {
		instance.as("3x5", 3, 5).withRows("....*", ".*...", "...*.").print(System.out);

		assertThat(instance.get(0, 0), equalTo("1"));
		assertThat(instance.get(0, 1), equalTo("1"));
		assertThat(instance.get(0, 2), equalTo("1"));
		assertThat(instance.get(0, 3), equalTo("1"));
		assertThat(instance.get(0, 4), equalTo("-1"));
		assertThat(instance.get(1, 0), equalTo("1"));
		assertThat(instance.get(1, 1), equalTo("-1"));
		assertThat(instance.get(1, 2), equalTo("2"));
		assertThat(instance.get(1, 3), equalTo("2"));
		assertThat(instance.get(1, 4), equalTo("2"));
		assertThat(instance.get(2, 0), equalTo("1"));
		assertThat(instance.get(2, 1), equalTo("1"));
		assertThat(instance.get(2, 2), equalTo("2"));
		assertThat(instance.get(2, 3), equalTo("-1"));
		assertThat(instance.get(2, 4), equalTo("1"));
	}

	@Test
	public void board5x3() {
		instance.as("5x3", 5, 3).withRows("..*", ".*.", ".*.", "*..", "..*").close();

		assertThat(instance.get(0, 0), equalTo("1"));
		assertThat(instance.get(0, 1), equalTo("2"));
		assertThat(instance.get(0, 2), equalTo("-1"));
		assertThat(instance.get(1, 0), equalTo("2"));
		assertThat(instance.get(1, 1), equalTo("-1"));
		assertThat(instance.get(1, 2), equalTo("3"));
		assertThat(instance.get(2, 0), equalTo("3"));
		assertThat(instance.get(2, 1), equalTo("-1"));
		assertThat(instance.get(2, 2), equalTo("2"));
		assertThat(instance.get(3, 0), equalTo("-1"));
		assertThat(instance.get(3, 1), equalTo("3"));
		assertThat(instance.get(3, 2), equalTo("2"));
		assertThat(instance.get(4, 0), equalTo("1"));
		assertThat(instance.get(4, 1), equalTo("2"));
		assertThat(instance.get(4, 2), equalTo("-1"));
	}

	@Test
	public void getId() {
		instance.as("getId", 3, 3);
		assertThat(instance.getId(), equalTo("getId"));
	}

	@Test
	public void mineSweeperWithIdRowsAndColumns() {
		instance = new MineSweeperBoard("constructor", 1, 5);

		assertThat(instance.getId(), equalTo("constructor"));
		assertThat(instance.getRowCount(), equalTo(1));
		assertThat(instance.getColumnCount(), equalTo(5));
	}

	@Test
	public void print1() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		instance.as("1", 4, 4).withRow("*...").withRow("....").withRow(".*..").withRow("....").withRow("..*")
				.print(out);

		String expected = "Mine Field #1:\n" + "*100\n" + "2210\n" + "1*10\n" + "1110\n\n";

		assertThat(out.toString(), equalTo(expected));
	}

	@Test
	public void print2() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		instance.as("2", 3, 5).withRow("**...").withRow(".....").withRow(".*...").withRow("....").withRow("..*")
				.print(out);

		String expected = "Mine Field #2:\n" + "**100\n" + "33200\n" + "1*100\n\n";

		assertThat(out.toString(), equalTo(expected));
	}

	@Test
	public void print5x3() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		instance.as("5x3", 5, 3).withRow("..*").withRow(".*.").withRow(".*.").withRow("*..").withRow("..*").print(out);

		String expected = "Mine Field #5x3:\n" + "12*\n" + "2*3\n" + "3*2\n" + "*32\n" + "12*\n\n";

		assertThat(out.toString(), equalTo(expected));
	}

	@Test
	public void returnsInstanceWhenClosed() {
		assertThat(instance.close(), equalTo(instance));
	}

	@Test
	public void rowsAsColumns() {
		instance.rowsAsColumns();
		instance.as("rowsAsColumns", 5, 3).withRow("**...").withRow(".....").withRow(".*...").close();

		assertThat(instance.get(2, 0), equalTo("1"));
		assertThat(instance.get(2, 4), equalTo("0"));

	}

	@Test
	public void rowsAsColumnsReturnsInstance() {
		assertThat(instance.rowsAsColumns(), equalTo(instance));
	}

	@Before
	public void setup() {
		instance = new MineSweeperBoard();
	}

	@Test
	public void throwsOnNotEnoughColumns() {
		thrown.expect(MineSweeperException.class);
		thrown.expectMessage(containsString("columns"));

		instance.as("throwsOnInvalidPattern", 3, 3);

		instance.withRow("**");

	}

	@Test
	public void throwsOnTooManyColumns() {
		thrown.expect(MineSweeperException.class);
		thrown.expectMessage(containsString("columns"));

		instance.as("throwsOnInvalidPattern", 3, 3);

		instance.withRow("****");
	}

	@Test
	public void throwsWhenClosed() {
		instance.close();
		thrown.expect(MineSweeperException.class);
		thrown.expectMessage(containsString("closed"));

		instance.withRow("****");
	}
}
