package android.friedrich.sudoKu.utils;

import android.content.Context;
import android.friedrich.sudoKu.data.Puzzle;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SudoKuSaverManager {
    private static final String TAG = "SudoKuSaverManager";
    private static final String USER_FILE_SUFFIX = "_user";
    private static final String PROGRAM_FILE_SUFFIX = "_program";

    private static SudoKuSaverManager sManager;

    private Context mContext;
    private SudoKuSaver mSudoKuSaverForProgram;
    private SudoKuSaver mSudoKuSaverForUser;

    public static SudoKuSaverManager getManager(Context context) {
        if (sManager == null) {
            sManager = new SudoKuSaverManager(context);
        }
        return sManager;
    }

    public SudoKuSaverManager(Context context) {
        mContext = context;
    }

    public void setSudoKuSaverForProgram(SudoKuSaver sudoKuSaver) {
        mSudoKuSaverForProgram = sudoKuSaver;
    }

    public void setSudoKuSaverForUser(SudoKuSaver sudoKuSaver) {
        mSudoKuSaverForUser = sudoKuSaver;
    }

    /*
    for test
     */
    public SudoKuSaver getSaverForProgram() {
        return mSudoKuSaverForProgram;
    }

    public SudoKuSaver getSaverForUser() {
        return mSudoKuSaverForUser;
    }

    public void dump(Puzzle puzzle){
        String fileBaseName = puzzle.mFilename;
        programDump(fileBaseName+PROGRAM_FILE_SUFFIX);
        userDump(fileBaseName+USER_FILE_SUFFIX);
    }

    private void dump(String filename, SudoKuSaver sudoKuSaver) {
        try {
            FileOutputStream fileOutputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            int assignmentCount = sudoKuSaver.getAssignmentSize();
            fileOutputStream.write(assignmentCount);
            for (int i = 0; i <assignmentCount; i++) {
                SudoKuSaver.Assignment assignment = sudoKuSaver.getAssignment(i);
                byte[] bytes = new byte[3];
                bytes[0] = assignment.getRow();
                bytes[1] = assignment.getCol();
                bytes[2] = assignment.getNumber();
                fileOutputStream.write(bytes,0, bytes.length);
            }
        } catch (IOException e) {
            Log.e(TAG, "dump ", e);
        }
    }

    public void  load(Puzzle puzzle) {
        String filename = puzzle.mFilename;
        String puzzleString = puzzle.mPuzzleString;
       mSudoKuSaverForUser = load(filename+USER_FILE_SUFFIX);
       mSudoKuSaverForUser.setPuzzleString(puzzleString);
       mSudoKuSaverForUser.setUUID(UUID.fromString(filename));
       mSudoKuSaverForProgram = load(filename+PROGRAM_FILE_SUFFIX);
       mSudoKuSaverForProgram.setPuzzleString(puzzleString);
       mSudoKuSaverForProgram.setUUID(UUID.fromString(filename));
    }

    private SudoKuSaver load(String filename) {
        SudoKuSaver sudoKuSaver= new SudoKuSaver();
//        mSudoKuSaverForUser.setPuzzleString(puzzle.mPuzzleString);
        try{
            FileInputStream fileInputStream = mContext.openFileInput(filename);
            int assignmentCount = fileInputStream.read();
            if (assignmentCount > 0){
                for (int i = 0; i < assignmentCount; i++) {
                    byte[] bytes = new byte[3];
                    fileInputStream.read(bytes,0,3);
                    sudoKuSaver.addAssignment(bytes[0],bytes[1],bytes[2]);
                }
            }
        }catch (IOException e){
            Log.e(TAG, "loadForUser: ",e );
        }
        return sudoKuSaver;
    }

    public void userDump(String filename) {
        dump(filename, mSudoKuSaverForUser);
    }

    public void programDump(String filename) {
        dump(filename, mSudoKuSaverForProgram);
    }
}
