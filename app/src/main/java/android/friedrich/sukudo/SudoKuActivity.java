package android.friedrich.sukudo;

import androidx.fragment.app.Fragment;

public class SudoKuActivity extends SingleActivity{
    @Override
    protected Fragment createFragment() {
        return SudoKuFragment.newInstance();
    }
}