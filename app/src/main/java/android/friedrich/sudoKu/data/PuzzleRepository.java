package android.friedrich.sudoKu.data;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class PuzzleRepository {
    private PuzzleDao mPuzzleDao;
    private CellDao mCellDao;
    private LiveData<List<Puzzle>> mAllPuzzles;

    public PuzzleRepository(Application application) {
        PuzzleRoomDatabase database = PuzzleRoomDatabase.getDatabase(application);
        mPuzzleDao = database.puzzleDao();
        mCellDao = database.cellDao();
        mAllPuzzles = mPuzzleDao.getAll();
    }

    public LiveData<List<Puzzle>> getAllPuzzles() {
        return mAllPuzzles;
    }

    public void insert(Puzzle puzzle) {
        PuzzleRoomDatabase.sDatabaseExecutor.execute(() -> {
            mPuzzleDao.insert(puzzle);
        });
    }

    public LiveData<Puzzle> getLastPuzzle() {
        return mPuzzleDao.getLatest();
    }

    public void update(Puzzle puzzle) {
        PuzzleRoomDatabase.sDatabaseExecutor.execute(() -> {
            mPuzzleDao.update(puzzle);
        });
    }

    public LiveData<List<Cell>> getAllCells() {
        return mCellDao.getAll();
    }
    public void insertAllCells(List<Cell> cells) {
        PuzzleRoomDatabase.sDatabaseExecutor.execute(()->{
            mCellDao.insertAll(cells);
        });
    }

    public void insertCell(Cell cell) {
        PuzzleRoomDatabase.sDatabaseExecutor.execute(()->{
            mCellDao.insert(cell);
        });
    }

    public List<Cell> getCellsList(){
        return mCellDao.getCellsList();
    }
}
