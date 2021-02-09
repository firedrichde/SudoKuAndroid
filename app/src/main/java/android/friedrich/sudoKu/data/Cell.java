package android.friedrich.sudoKu.data;

import android.friedrich.sudoKu.utils.SudoKuConstant;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "sudo_ku_cells")
public class Cell {
    private static final int UNIT_SIZE = 9;
    @ColumnInfo(name = "row")
    private int row;
    @ColumnInfo(name = "col")
    private int col;
    @PrimaryKey
    @ColumnInfo(name = "index")
    private int index;
    /**
     * assign the number to the cell
     */
    @ColumnInfo(name = "number")
    private byte number;

    /**
     * note the possible numbers to the cell
     */
    @Ignore
    private byte[] noteNumbers;
    @Ignore
    private String possibleValue;

    /**
     * count cell conflict with its peers.if the variable equals 0, means no conflict
     */
    @ColumnInfo(name = "conflict_count", defaultValue = "0")
    private int conflictCount;

    /**
     * if cell possible value is assigned by program,serverMode is true, else false
     */
    @ColumnInfo(name = "generate_by_program",defaultValue = "false")
    private boolean generateByProgram;

//    @Deprecated
//    public Cell(int row, int col) {
//        this.row = row;
//        this.col = col;
//        this.conflictCount = 0;
//        index = row * UNIT_SIZE + col;
//    }

    public Cell(int index) {
        this.index = index;
        this.row = index / UNIT_SIZE;
        this.col = index % UNIT_SIZE;
        this.conflictCount = 0;
        number = SudoKuConstant.NUMBER_UNCERTAIN;
        noteNumbers = new byte[SudoKuConstant.UNIT_CELL_SIZE];
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setIndex(int index) {
        this.index = index;
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
