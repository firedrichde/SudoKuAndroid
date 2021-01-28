package android.friedrich.sudoKu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AssignmentPreference {
    public static final String PRE_ASSIGNMENT_STEP = "android.friedrich.sudoKu.assignmentStep";
//    public static final String P

    public static void setPreferenceAssignmentStep(Context context, int step){
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(PRE_ASSIGNMENT_STEP, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(PRE_ASSIGNMENT_STEP, step).apply();
    }

    public static int getPreferenceAssignmentStep(Context context) {
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(PRE_ASSIGNMENT_STEP, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PRE_ASSIGNMENT_STEP,-1);
    }
}
