package android.friedrich.sudoKu;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SudoKuSaverManager {
    private static SudoKuSaverManager sManager;

    private Context mContext;
    private List<SudoKuSaver> mSudoKuSaverListForProgram;
    private List<SudoKuSaver> mSudoKuSaversListForUser;

    public static SudoKuSaverManager getManager(Context context) {
        if (sManager == null) {
            sManager = new SudoKuSaverManager(context);
        }
        return sManager;
    }

    public SudoKuSaverManager(Context context) {
        mContext = context;
        mSudoKuSaverListForProgram = new ArrayList<>();
        mSudoKuSaversListForUser = new ArrayList<>();
    }

    public void addSudoKuSaver(SudoKuSaver sudoKuSaver) {
        mSudoKuSaverListForProgram.add(sudoKuSaver);
    }

    public void addSudoKuSaverByUser(SudoKuSaver sudoKuSaver) {
        mSudoKuSaversListForUser.add(sudoKuSaver);
    }

    /*
    for test
     */
    public SudoKuSaver get() {
        return mSudoKuSaverListForProgram.get(0);
    }

    public SudoKuSaver getUserSaver() {return mSudoKuSaversListForUser.get(0);}
}
