package android.friedrich.sudoKu;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SudoKuBoardTest {
    private String testGridString;
    private SudoKuBoard subject;

    @Before
    public void setUp() throws Exception {
        testGridString = SudoKuBoard.Generate();
        subject = new SudoKuBoard();
    }

    @Test
    public void testSolve() throws Exception{
        boolean isSolved = subject.solve(testGridString);
        assertTrue(isSolved);
    }
}