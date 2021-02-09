package android.friedrich.sudoKu.viewmodels;

import android.app.Application;
import android.friedrich.sudoKu.data.Puzzle;
import android.friedrich.sudoKu.data.PuzzleRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PuzzleViewModel extends AndroidViewModel {
    private PuzzleRepository mPuzzleRepository;
    private final LiveData<List<Puzzle>> mAllPuzzles;
    private LiveData<Puzzle> mPuzzle;

    public PuzzleViewModel(@NonNull  Application application) {
        super(application);
        mPuzzleRepository = new PuzzleRepository(application);
        mAllPuzzles = mPuzzleRepository.getAllPuzzles();
        mPuzzle = mPuzzleRepository.getLastPuzzle();
    }

    public LiveData<List<Puzzle>> getAllPuzzles() {
        return mAllPuzzles;
    }

    public void insert(Puzzle puzzle) {
        mPuzzleRepository.insert(puzzle);
    }

    public LiveData<Puzzle> getLastPuzzle() {
//        return mPuzzle;
        return mPuzzleRepository.getLastPuzzle();
    }

    public void update(Puzzle puzzle){
        mPuzzleRepository.update(puzzle);
    }
}
