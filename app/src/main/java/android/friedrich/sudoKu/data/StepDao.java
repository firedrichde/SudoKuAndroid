package android.friedrich.sudoKu.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
interface StepDao {

    @Insert
    void insert(Step step);

    @Update()
    void update(Step step);

    @Query("SELECT * FROM steps WHERE puzzle_id == :puzzleId order by step")
    LiveData<List<Step>> getAllStepsOfOnePuzzle(int puzzleId);


}
