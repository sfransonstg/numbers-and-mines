package sfranson.minesweeper;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RowTest {

	private Row instance;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void closeCalculatesBottom() {
		Row next = new Row(0, 5);
		instance.withNext(next);
		instance.withCellCount(5);

		instance.withCells("*.*.*");
		next.withCells(".....");

		instance.close();
		next.close();

		assertThat(next.cellValue(0), equalTo(1));
		assertThat(next.cellValue(1), equalTo(2));
		assertThat(next.cellValue(2), equalTo(1));
		assertThat(next.cellValue(3), equalTo(2));
		assertThat(next.cellValue(4), equalTo(1));
	}

	@Test
	public void closeCalculatesLeftRight() {
		instance.withCellCount(5);

		instance.withCells("*.*.*").close();

		assertThat(instance.cellValue(0), equalTo(-1));
		assertThat(instance.cellValue(1), equalTo(2));
		assertThat(instance.cellValue(2), equalTo(-1));
		assertThat(instance.cellValue(3), equalTo(2));
		assertThat(instance.cellValue(4), equalTo(-1));
	}

	@Test
	public void closeCalculatesTop() {
		Row prev = new Row(0, 5);
		instance.withPrev(prev);
		instance.withCellCount(5);

		prev.withCells(".....");
		instance.withCells("*.*.*");

		prev.close();
		instance.close();

		assertThat(prev.cellValue(0), equalTo(1));
		assertThat(prev.cellValue(1), equalTo(2));
		assertThat(prev.cellValue(2), equalTo(1));
		assertThat(prev.cellValue(3), equalTo(2));
		assertThat(prev.cellValue(4), equalTo(1));
	}

	@Test
	public void closeReturnsInstance() {
		assertThat(instance.close(), equalTo(instance));
	}

	@Test
	public void print() {
		instance.withCellCount(4);

		instance.withCells("*.*.");

		assertThat(instance.print(), equalTo("*2*1"));
	}

	@Before
	public void setup() {
		instance = new Row(0, 1);
	}

	@Test
	public void withColCountReturnsInstance() {
		assertThat(instance.withCellCount(2), equalTo(instance));
	}

	@Test
	public void withColumnsRowsAsColumnsSetsRawValues() {
		instance.withCellCount(4);
		String columnDefinition = "****";

		instance.withColumns(columnDefinition, true);

		assertThat(instance.cellValue(0), equalTo(-1));
		assertThat(instance.cellValue(1), equalTo(0));
		assertThat(instance.cellValue(2), equalTo(0));
		assertThat(instance.cellValue(3), equalTo(0));

		instance.withColumns(columnDefinition, true);

		assertThat(instance.cellValue(0), equalTo(-1));
		assertThat(instance.cellValue(1), equalTo(-1));
		assertThat(instance.cellValue(2), equalTo(0));
		assertThat(instance.cellValue(3), equalTo(0));

		instance.withColumns(columnDefinition, true);

		assertThat(instance.cellValue(0), equalTo(-1));
		assertThat(instance.cellValue(1), equalTo(-1));
		assertThat(instance.cellValue(2), equalTo(-1));
		assertThat(instance.cellValue(3), equalTo(0));

		instance.withColumns(columnDefinition, true);

		assertThat(instance.cellValue(0), equalTo(-1));
		assertThat(instance.cellValue(1), equalTo(-1));
		assertThat(instance.cellValue(2), equalTo(-1));
		assertThat(instance.cellValue(3), equalTo(-1));
	}

	@Test
	public void withColumnsSetsRawValues() {
		instance.withCellCount(4);
		String columnDefinition = "*...";
		instance.withCells(columnDefinition);

		assertThat(instance.cellValue(0), equalTo(-1));
		assertThat(instance.cellValue(1), equalTo(0));
		assertThat(instance.cellValue(2), equalTo(0));
		assertThat(instance.cellValue(3), equalTo(0));
	}

	@Test
	public void withColumnsThrowsOnTooManyColumns() {
		thrown.expect(MineSweeperException.class);
		thrown.expectMessage(containsString("columns"));

		instance.withCellCount(3);

		instance.withCells("****");
	}

	@Test
	public void withNextReturnsInstance() {
		Row next = new Row(1, 1);
		assertThat(instance.withNext(next), equalTo(instance));
	}

	@Test
	public void withNextSetsNext() {
		Row next = new Row(0, 1);
		instance.withNext(next);
		assertThat(instance.getNext(), equalTo(next));
	}

	@Test
	public void withNextSetsPrev() {
		Row next = new Row(0, 1);
		instance.withNext(next);
		assertThat(next.getPrev(), equalTo(instance));
	}

	@Test
	public void withPrevReturnsInstance() {
		Row prev = new Row(1, 1);
		assertThat(instance.withPrev(prev), equalTo(instance));
	}

	@Test
	public void withPrevSetsNext() {
		Row prev = new Row(0, 1);
		instance.withPrev(prev);
		assertThat(prev.getNext(), equalTo(instance));
	}

	@Test
	public void withPrevSetsPrev() {
		Row prev = new Row(0, 1);
		instance.withPrev(prev);
		assertThat(instance.getPrev(), equalTo(prev));
	}
}
