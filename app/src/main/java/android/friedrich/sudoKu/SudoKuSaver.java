package android.friedrich.sudoKu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SudoKuSaver {

    public static final String PREFIX_FILE = "SudoKuFile";

    public static final String SUFFIX_USER = "user";
    /**
     * id for sudoKu saver
     */
    private UUID mUUID;

    /**
     * each character in puzzle represent a digit in SudoKu board, character '.' means
     * the assignment is designed for user
     */
    private String mPuzzleString;

    /**
     * empty cell number in SudoKu board
     */
    private int mEmptyNumber;

    private final String mStorageFileName;

    /**
     * record all the assignments for SudoKu puzzle
     */
    private List<Assignment> mAssignmentList;
    private AssignmentTracker mTracker;

    /**
     * assignment offset based on assignments by initial sudoKu puzzle
     */
    private int mAssignmentOffset;


    public SudoKuSaver() {
        mUUID = UUID.randomUUID();
        mStorageFileName = PREFIX_FILE + mUUID.toString();
        mAssignmentOffset = 0;
        mAssignmentList= new ArrayList<>();
        mTracker = new AssignmentTracker();
    }

    public SudoKuSaver(SudoKuSaver other) {
        mUUID = other.mUUID;
        mStorageFileName = other.mStorageFileName+ SUFFIX_USER;
        mAssignmentOffset = other.mAssignmentOffset;
        mPuzzleString = other.mPuzzleString;
        mAssignmentList= new ArrayList<>();
        for (int i = 0; i <mAssignmentOffset ; i++) {
            mAssignmentList.add(other.mAssignmentList.get(i));
        }
        mTracker = new AssignmentTracker();
        mEmptyNumber = -1;
    }

    public void setAssignmentTracker(AssignmentTracker assignmentTracker) {
        this.mTracker = assignmentTracker;
        if (mAssignmentList == null) {
            mAssignmentList = new ArrayList<>();
        }
        dumpTracker();
    }

    public int getAssignmentOffset() {
        return mAssignmentOffset;
    }

    public void setAssignmentOffset(int assignmentOffset) {
        mAssignmentOffset = assignmentOffset;
    }

    public void addAssignment(byte row, byte col, byte number) {
        Assignment lastAssignment = getLastAssignment();
        if (lastAssignment.col == col && lastAssignment.row == row) {
            /*
            undo assignment or redo assignment
             */
            mTracker.removeRecord();
        }
        mAssignmentList.add(new Assignment(row, col, number));
        mTracker.addRecord(row,col,number);
    }

    public int getAssignmentSize() {
        return mAssignmentList.size();
    }


    /**
     * sudoKu assignment class
     */
    public final class Assignment {
        private byte row;
        private byte col;
        private byte number;

        public Assignment(byte row, byte col, byte number) {
            this.row = row;
            this.col = col;
            this.number = number;
        }

        public byte getRow() {
            return row;
        }

        public byte getCol() {
            return col;
        }

        public byte getNumber() {
            return number;
        }
    }

    public UUID getUUID() {
        return mUUID;
    }

    public void setUUID(UUID UUID) {
        mUUID = UUID;
    }

    public String getPuzzleString() {
        return mPuzzleString;
    }

    public void setPuzzleString(String puzzleString) {
        mPuzzleString = puzzleString;
        int fillCellsCount = 0;
        for (int i = 0; i < puzzleString.length(); i++) {
            if (puzzleString.charAt(i) != SudoKuBoard.dot) {
                fillCellsCount++;
            }
        }
        mAssignmentOffset = fillCellsCount;
        mEmptyNumber = SudoKuConstant.BOARD_CELL_SIZE - fillCellsCount;
    }

    public int getEmptyNumber() {
        return mEmptyNumber;
    }

    public void setEmptyNumber(int emptyNumber) {
        mEmptyNumber = emptyNumber;
    }

    public String getStorageFileName() {
        return mStorageFileName;
    }

    //    public void setStorageFileName(String storageFileName) {
//        mStorageFileName = storageFileName;
//    }

    /**
     * dump tracker content to assignment list
     */
    private void dumpTracker() {
        for (AssignmentTracker.Node trackerNode :
                mTracker.getSteps()) {
            mAssignmentList.add(new Assignment(trackerNode.getRow(), trackerNode.getCol(), trackerNode.getNumber()));
        }
    }

    /**
     * return the cell assignment at the specified step
     * @param index the specified step base on mAssignmentOffset
     * @return null if the assignment does not exist, else the specified assignment
     */
    public Assignment getAssignment(int index) {
        int position = index + mAssignmentOffset;
        if (position >= mAssignmentList.size()) {
//            position = SudoKuConstant.BOARD_CELL_SIZE - 1;
            return null;
        }
        if (position < 0) {
//            position = 0;
            return null;
        }
        return mAssignmentList.get(position);
    }

    public Assignment getLastAssignment() {
        if (mAssignmentList.isEmpty()) {
            return null;
        } else {
            return mAssignmentList.get(mAssignmentList.size()-1);
        }
    }

    public Assignment getSecondLastAssignment() {
        if (mAssignmentList.size() <2) {
            return null;
        } else {
            return mAssignmentList.get(mAssignmentList.size()-2);
        }
    }


    /**
     * dump assignments and tracker to internal storage
     */
    public void dump() {

    }
}
