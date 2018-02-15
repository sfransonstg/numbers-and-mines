package sfranson.missingnumber;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class MissingNumberTest {

	private MissingNumber instance;

	@Test
	public void findMissing() {

		int[] intArray = new int[] { 2, 5, 3, 6, 1 };

		int expected = 4;

		int result = instance.findMissing(intArray);

		assertThat(result, equalTo(expected));

	}

	@Test
	public void findMissingBig() {
		int[] intArray = new int[] { 15, 9, 3, 13, 1, 2, 7, 10, 8, 5, 12, 4, 6, 14 };

		int expected = 11;

		int result = instance.findMissing(intArray);

		assertThat(result, equalTo(expected));

	}

	@Test
	public void findMissingEmpty() {
		int[] intArray = new int[] { };

		int expected = -1;

		int result = instance.findMissing(intArray);

		assertThat(result, equalTo(expected));
	}
	
	@Test
	public void findMissingSmall() {
		int[] intArray = new int[] { 1, 3 };
		
		int expected = 2;
		
		int result = instance.findMissing(intArray);
		
		assertThat(result, equalTo(expected));
	}

	@Before
	public void setup() {
		instance = new MissingNumber();
	}

}
