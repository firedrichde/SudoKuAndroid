package android.friedrich.sudoKu.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "steps")
public class Step {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "row")
    public byte row;
    @ColumnInfo(name = "col")
    public byte col;
    @ColumnInfo(name = "cell_index")
    public byte cellIndex;

    @ColumnInfo(name = "number")
    public byte number;

    @ColumnInfo(name = "conflict_count")
    public int conflictCount;

    @ColumnInfo(name = "step")
    public int step;

    @ColumnInfo(name = "puzzle_id")
    public String puzzleId;


    public byte getRow() {
        return row;
    }

    public void setRow(byte row) {
        this.row = row;
    }

    public byte getCol() {
        return col;
    }

    public void setCol(byte col) {
        this.col = col;
    }

    public byte getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(byte cellIndex) {
        this.cellIndex = cellIndex;
    }

    public byte getNumber() {
        return number;
    }

    public void setNumber(byte number) {
        this.number = number;
    }

    public int getConflictCount() {
        return conflictCount;
    }

    public void setConflictCount(int conflictCount) {
        this.conflictCount = conflictCount;
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
