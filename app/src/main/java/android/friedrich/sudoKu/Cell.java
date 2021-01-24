package android.friedrich.sudoKu;

public class Cell {
    private static final int UNIT_SIZE = 9;
    private final int row;
    private final int col;
    private final int index;
    /**
     * assign the number to the cell
     */
    private byte number;

    /**
     * note the possible numbers to the cell
     */
    private byte[] noteNumbers;
    private String possibleValue;

    /**
     * count cell conflict with its peers.if the variable equals 0, means no conflict
     */
    private int conflictCount;

    /**
     * if cell possible value is assigned by program,serverMode is true, else false
     */
    private boolean generateByProgram;

    @Deprecated
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.conflictCount = 0;
        index = row * UNIT_SIZE + col;
    }

    public Cell(int index) {
        this.index = index;
        this.row = index / UNIT_SIZE;
        this.col = index % UNIT_SIZE;
        this.conflictCount = 0;
        number = SudoKuConstant.NUMBER_UNCERTAIN;
        noteNumbers = new byte[SudoKuConstant.UNIT_CELL_SIZE];
    }


    public void setPossibleValue(String possibleValue) {
        this.possibleValue = possibleValue;
    }

    public String getPossibleValue() {
        return possibleValue;
    }

    public byte getNumber() {
        return number;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    public byte[] getNoteNumbers() {
        return noteNumbers;
    }

    public void setNoteNumbers(byte[] noteNumbers) {
        this.noteNumbers = noteNumbers;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getIndex() {
        return index;
    }

    public boolean isGenerateByProgram() {
        return generateByProgram;
    }

    public void setGenerateByProgram(boolean generateByProgram) {
        this.generateByProgram = generateByProgram;
    }

    public int getConflictCount() {
        return conflictCount;
    }

    public void setConflictCount(int conflictCount) {
        this.conflictCount = conflictCount;
    }

    public void increaseConflictCount() {
        conflictCount++;
    }

    public void decreaseConflictCount() {
        if (conflictCount > 0) {
            conflictCount--;
        }
    }

    public void clearConflictCount() {
        conflictCount = 0;
    }

    /**
     * judge if the cell is assigned with a number(1-9)
     *
     * @return true if the cell is assigned with a number, else false
     */
    public boolean isAssigned() {
        return number != SudoKuConstant.NUMBER_UNCERTAIN;
    }
}
