package android.friedrich.sudoKu.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "puzzles")
public class Puzzle {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int mId;

    @ColumnInfo(name = "puzzle_string")
    public String mPuzzleString;

    /**
     * name of  user assignments file
     */
    @ColumnInfo(name = "filename")
    public String mFilename;

//    @ColumnInfo(name = "create_time")
//    public Date mCreateTime;
//
//    @ColumnInfo(name = "finish_time")
//    public Date mFinishTime;

    @ColumnInfo(name = "solved")
    public boolean mSolved;

    public void setId(int id) {
        mId = id;
    }

    public void setPuzzleString(String puzzleString) {
        mPuzzleString = puzzleString;
    }

    public void setFilename(String filename) {
        mFilename = filename;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public int getId() {
        return mId;
    }

    public String getPuzzleString() {
        return mPuzzleString;
    }

    public String getFilename() {
        return mFilename;
    }

    public boolean isSolved() {
        return mSolved;
    }
}
