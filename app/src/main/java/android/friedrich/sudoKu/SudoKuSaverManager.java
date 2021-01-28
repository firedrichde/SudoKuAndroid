package android.friedrich.sudoKu;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SudoKuSaverManager {
    private static SudoKuSaverManager sManager;

    private Context mContext;
    private List<SudoKuSaver> mSudoKuSaverList;

    public static SudoKuSaverManager getManager(Context context) {
        if (sManager == null) {
            sManager = new SudoKuSaverManager(context);
        }
        return sManager;
    }

    public SudoKuSaverManager(Context context) {
        mContext = context;
        mSudoKuSaverList = new ArrayList<>();
    }

    public void addSudoKuSaver(SudoKuSaver sudoKuSaver) {
        mSudoKuSaverList.add(sudoKuSaver);
    }

    /*
    for test
     */
    public SudoKuSaver get() {
        return mSudoKuSaverList.get(0);
    }
}
