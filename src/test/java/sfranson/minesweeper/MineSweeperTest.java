package sfranson.minesweeper;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import sfranson.minesweeper.MineSweeper;
import sfranson.minesweeper.MineSweeperBoard;
import sfranson.minesweeper.MineSweeperException;

public class MineSweeperTest {

	private MineSweeper instance;

	@Mock
	private OutputStream outputStream;

	private String[] params;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void fileSourceGetsStreamFromAbsoluteFileName() throws Exception {
		InputStream stream = Mockito.mock(InputStream.class);
		doReturn(null).doReturn(stream).when(instance).getResourceAsStream(anyString());

		assertThat(instance.fileSource("test"), equalTo(stream));
		verify(instance, times(2)).getResourceAsStream(contains("test"));
	}

	@Test
	public void fileSourceGetsStreamFromPath() throws Exception {
		doReturn(null).when(instance).getResourceAsStream(anyString());

		thrown.expect(FileNotFoundException.class);
		thrown.expectMessage(containsString("test"));

		instance.fileSource("test");
	}

	@Test
	public void fileSourceGetsStreamFromRawFileName() throws Exception {
		InputStream stream = Mockito.mock(InputStream.class);
		doReturn(stream).when(instance).getResourceAsStream(any());

		assertThat(instance.fileSource("test"), equalTo(stream));
		verify(instance).getResourceAsStream("test");
	}

	@Test
	public void getResourceAsStream() {
		assertThat(instance.getResourceAsStream("/placeholder.txt"), notNullValue());
	}

	@Test
	public void instanceCreatesNew() {
		MineSweeper.instance = null;
		MineSweeper result = MineSweeper.instance(params);
		assertThat(result, notNullValue());
		assertThat(MineSweeper.instance, not(equalTo(result)));
	}

	@Test
	public void mainCallsPrintsException() {

		PrintStream mockErr = Mockito.mock(PrintStream.class);

		PrintStream old = System.err;

		System.setErr(mockErr);

		MineSweeperException toBeThrown = Mockito.spy(new MineSweeperException("test exception"));

		doThrow(toBeThrown).when(instance).sweep();

		MineSweeper.main(params);
		System.setErr(old);

		verify(toBeThrown).printStackTrace(mockErr);
		verify(mockErr).println("test exception");

	}

	@Test
	public void mainCallsSweep() {

		doNothing().when(instance).sweep();
		
		MineSweeper.main(params);

		verify(instance).sweep();
	}

	@Test
	public void processBoardSetsDimensions() {

		MineSweeperBoard result = instance.processBoard("11 10", null);

		assertThat(result.getRowCount(), equalTo(11));
		assertThat(result.getColumnCount(), equalTo(10));

	}

	@Test
	public void processBoardSetsFirstId() {

		MineSweeperBoard result = instance.processBoard("1 1", null);

		assertThat(result.getId(), equalTo("1"));

	}

	@Test
	public void processBoardSetsIncrementedId() {
		MineSweeperBoard board = Mockito.mock(MineSweeperBoard.class);
		when(board.getId()).thenReturn("2");

		MineSweeperBoard result = instance.processBoard("1 1", board);

		assertThat(result.getId(), equalTo("3"));

	}
	@Test
	public void processBoardCallsRowsAsColumns() {
		MineSweeperBoard board = Mockito.mock(MineSweeperBoard.class);
		
		doReturn(true).when(instance).rowsAsColumns();
		
		when(board.getId()).thenReturn("2");
		
		MineSweeperBoard result = instance.processBoard("1 1", board);
		
		assertThat(result.usingRowsAsColumns(), equalTo(true));
		
	}
	
	@Test
	public void rowsAsColumns() {
		params = new String[2];
		params[1] = "columns";
		setupInstance();
		
		MineSweeperBoard board = instance.processBoard("0 0", null);
		assertThat(board.usingRowsAsColumns(), equalTo(true));
	}

	@Test
	public void processInputPrintsOnStart() {
		MineSweeperBoard board = Mockito.mock(MineSweeperBoard.class);

		instance.processInput("0 0", board);
		verify(board).print(outputStream);
	}

	@Test
	public void processInputProcessesNewBoard() {
		MineSweeperBoard board = Mockito.mock(MineSweeperBoard.class);

		doReturn(board).when(instance).processBoard(any(), any());

		assertThat(instance.processInput("1 1", board), equalTo(board));
		verify(instance).processBoard("1 1", board);
	}

	@Test
	public void processInputThrowsOnInvalidInput() {

		thrown.expect(MineSweeperException.class);
		thrown.expectMessage(containsString("Invalid input"));
		instance.processInput("invalid", null);
	}

	@Test
	public void processInputValidInput() {
		MineSweeperBoard board = Mockito.mock(MineSweeperBoard.class);

		String input = "**..";

		assertThat(instance.processInput(input, board), equalTo(board));
		verify(board).withRow(input);
	}

	@Test
	public void scanner() throws Exception {
		params[0] = "test";
		doReturn(Mockito.mock(InputStream.class)).when(instance).fileSource("test");

		assertThat(instance.scanner(params), notNullValue());
	}

	@Test
	public void scannerThrowsOnException() throws Exception {
		params[0] = "test";
		doThrow(new NullPointerException("test exception")).when(instance).fileSource("test");

		thrown.expect(MineSweeperException.class);
		thrown.expectMessage(containsString("file name"));

		instance.scanner(params);
	}

	@Before
	public void setup() {
		params = new String[1];
		MockitoAnnotations.initMocks(this);
		setupInstance();
	}

	private void setupInstance() {
		instance = Mockito.spy(new MineSweeper(params, outputStream));
		MineSweeper.instance = instance;
	}

	@Test
	public void sweepRunsUntilEndOfInput() {
		Scanner scanner = new Scanner(new StringReader("*....\n....\n"));
		doReturn(scanner).when(instance).scanner(params);

		MineSweeperBoard result = Mockito.mock(MineSweeperBoard.class);
		doReturn(result).when(instance).processInput(any(), any());

		instance.sweep();

		verify(instance, times(2)).processInput(any(), any());

	}

	@Test
	public void sweepRunsUntilEndOfInputWithTerminator() {
		Scanner scanner = new Scanner(new StringReader("*....\n....\n0 0\n"));
		doReturn(scanner).when(instance).scanner(params);

		MineSweeperBoard result = Mockito.mock(MineSweeperBoard.class);
		doReturn(result).when(instance).processInput(any(), any());

		instance.sweep();

		verify(instance, times(3)).processInput(any(), any());

	}

	@Test
	public void sweepRunsUntilNullBoard() {
		Scanner scanner = new Scanner(new StringReader("test1\ntest2\ntest3\n"));
		doReturn(scanner).when(instance).scanner(params);

		doReturn(null).when(instance).processInput(any(), any());

		instance.sweep();

		verify(instance, times(1)).processInput(any(), any());

	}

	@After
	public void tearDown() {
		MineSweeper.instance = null;
	}

}
