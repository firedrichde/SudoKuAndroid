package android.friedrich.sudoKu.viewmodels;

import android.app.Application;
import android.friedrich.sudoKu.data.PuzzleRepository;
import android.friedrich.sudoKu.utils.SudoKuGame;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class PuzzleViewModel extends AndroidViewModel {
    private PuzzleRepository mPuzzleRepository;
    public SudoKuGame mSudoKuGame;

    public PuzzleViewModel(@NonNull Application application) {
        super(application);
        mPuzzleRepository = new PuzzleRepository(application);
        mSudoKuGame = new SudoKuGame(application, mPuzzleRepository);
    }
}


