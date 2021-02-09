package android.friedrich.sudoKu.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PuzzleDao {
    @Insert
    void insert(Puzzle puzzle);

    @Delete
    void delete(Puzzle puzzle);

    @Query("DELETE FROM puzzles")
    void deleteAll();

    @Query("SELECT * FROM puzzles")
    LiveData<List<Puzzle>> getAll();

    @Query("SELECT * FROM puzzles WHERE id=(SELECT max(id) FROM puzzles)")
    LiveData<Puzzle> getLatest();

    @Update
    void update(Puzzle puzzle);

    @Query("SELECT count(*) FROM puzzles WHERE id=:mId")
    int getPuzzle(int mId);
}
