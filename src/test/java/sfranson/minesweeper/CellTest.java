package sfranson.minesweeper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class CellTest {

	private Cell instance;

	@Test
	public void increment() {
		instance.rawValue(".");
		instance.increment();
		assertThat(instance.rawValue(), equalTo(1));
	}

	@Test
	public void incrementMineDoesntIncrement() {
		instance.rawValue("*");
		instance.increment();
		assertThat(instance.rawValue(), equalTo(-1));
	}

	@Test
	public void isMine() {
		assertThat(instance.isMine(), equalTo(false));

		instance.rawValue("*");
		assertThat(instance.isMine(), equalTo(true));
		instance.rawValue(".");
		assertThat(instance.isMine(), equalTo(false));
	}

	@Before
	public void setup() {
		instance = new Cell();
	}

	@Test
	public void toStringIncrementedValue() {
		instance.rawValue(".");
		instance.increment();
		instance.increment();
		assertThat(instance.toString(), equalTo("2"));
	}

	@Test
	public void toStringMine() {
		instance.rawValue("*");
		assertThat(instance.toString(), equalTo("*"));
	}

	@Test
	public void toStringValue() {
		instance.rawValue(".");
		assertThat(instance.toString(), equalTo("0"));
	}

}
