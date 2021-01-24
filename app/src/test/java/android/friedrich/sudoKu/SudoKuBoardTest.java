package android.friedrich.sudoKu;

import android.os.SystemClock;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SudoKuBoardTest {
    private String testGridString;
    private SudoKuBoard subject;
    private AssignmentTracker mTracker;

    @Before
    public void setUp() throws Exception {
        mTracker = new AssignmentTracker();
        subject = new SudoKuBoard();
        testGridString = "4.....8.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......";
        subject.bindAssignmentTracker(mTracker);
    }

    @Test
    public void testSolve() throws Exception{
        boolean isSolved = subject.solve(testGridString);
        int[] count  = new int[SudoKuConstant.BOARD_CELL_SIZE];
        for (AssignmentTracker.Node node:
             mTracker.getSteps()) {
            System.out.println(node);
            count[node.getCellIndex()]++;
        }
        for (int i = 0; i <count.length ; i++) {
            System.out.println("i="+i+", count="+count[i]);
        }
        System.out.println(subject);
        System.out.println("tracker size: "+mTracker.getSize());
        assertTrue(isSolved);
    }
}