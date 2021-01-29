package android.friedrich.sudoKu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AssignmentPreference {
    /**
     * step of assignment by program
     */
    public static final String PRE_ASSIGNMENT_STEP = "android.friedrich.sudoKu.assignmentStep";

    /**
     * preference for puzzle string
     */
    public static final String PRE_PUZZLE_STRING = "android.friedrich.sudoKu.puzzleString";

    public static void setPreferenceAssignmentStep(Context context, int step) {
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(PRE_ASSIGNMENT_STEP, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(PRE_ASSIGNMENT_STEP, step).apply();
    }

    public static int getPreferenceAssignmentStep(Context context) {
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(PRE_ASSIGNMENT_STEP, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PRE_ASSIGNMENT_STEP, -1);
    }

    public static void setPrePuzzleString(Context context, String puzzle) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRE_PUZZLE_STRING,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PRE_PUZZLE_STRING, puzzle).apply();
    }

    public static String getPrePuzzleString(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRE_PUZZLE_STRING,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(PRE_PUZZLE_STRING,null);
    }
}
