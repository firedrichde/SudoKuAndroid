package android.friedrich.sudoKu.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CellDao {
    @Query("DELETE FROM sudo_ku_cells")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Cell> cells);

    @Delete
    void delete(Cell cell);

    @Query("SELECT * FROM sudo_ku_cells")
    LiveData<List<Cell>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Cell cell);

    @Query("SELECt * FROM sudo_ku_cells")
    List<Cell> getCellsList();
}
