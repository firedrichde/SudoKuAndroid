package android.friedrich.sudoKu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SudoKuSaver {

    public static final String FILE_PREFIX = "SudoKuFile";
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

    private int mAssignmentOffset;


    public SudoKuSaver() {
        mUUID = UUID.randomUUID();
        mStorageFileName = FILE_PREFIX + mUUID.toString();
        mAssignmentOffset = 0;
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


    /**
     * sudoKu assignment class
     */
    public  class Assignment {
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
            if (puzzleString.charAt(i)!=SudoKuBoard.dot) {
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

    public Assignment getAssignment(int index) {
        int position = index + mAssignmentOffset;
        if (position>= SudoKuConstant.BOARD_CELL_SIZE) {
            position = SudoKuConstant.BOARD_CELL_SIZE-1;
        }
        if (position < 0){
            position = 0;
        }
        return mAssignmentList.get(position);
    }
}