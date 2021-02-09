package android.friedrich.sudoKu.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PuzzleRepository {
    private PuzzleDao mPuzzleDao;
    private LiveData<List<Puzzle>> mAllPuzzles;

    public PuzzleRepository(Application application) {
        PuzzleRoomDatabase database = PuzzleRoomDatabase.getDatabase(application);
        mPuzzleDao = database.puzzleDao();
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
}
