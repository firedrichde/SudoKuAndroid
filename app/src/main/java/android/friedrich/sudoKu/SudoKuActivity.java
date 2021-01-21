package android.friedrich.sudoKu;

import androidx.fragment.app.Fragment;

public class SudoKuActivity extends SingleActivity{
    @Override
    protected Fragment createFragment() {
        return SudoKuFragment.newInstance();
    }
}