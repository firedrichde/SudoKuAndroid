package android.friedrich.sudoKu;

import android.content.Context;
import android.friedrich.sudoKu.data.PuzzleDao;
import android.friedrich.sudoKu.data.Puzzle;
import android.friedrich.sudoKu.data.PuzzleRepository;
import android.friedrich.sudoKu.viewmodels.PuzzleViewModel;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SudoKuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SudoKuFragment extends Fragment {
    private static final String TAG = "SudoKuFragment";
//    private static final String KEY_BOARD_STRING = "boardString";

    /**
     * assignment is not on focus
     */
    private static final byte ASSIGNMENT_UN_FOCUS = 10;

    /**
     * view for SudoKu board
     */
    private SudoKuBoardView mBoardView;

    /**
     * fields from mButton1 to mButton9 is button with number 1 to 9
     */
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButtonDelete;

    /**
     * button is designed for undo assignment operation
     */
    private Button mButtonUndo;

    /**
     * button is designed for redo assignment operation
     */
    private Button mButtonRedo;
    /**
     * button is designed for show next assignment
     */
    private Button mButtonShowNextStep;

    /**
     * button is designed for show previous assignment
     */
    private Button mButtonShowPreviousStep;
    private List<Button> mButtonNumberList;
    /**
     * current number for assignment
     */
    private byte mActiveNumber;
    private Cell[] mCells;

    private CellsManager mCellsManager;
    /**
     * asyncTask for sudoKu generate
     */
    private SudoKuGenerateTask mSudoKuGenerateTask;
    private String mBoardString;

    private SudoKuSaver mSudoKuSaver;

    private SudoKuSaver mUserAssignmentSaver;

    private SudoKuSaverManager mSaverManager;
    private boolean mCanUndo;

    private boolean mShowPrevious;

    /**
     * sudoKu database repository
     */
    private PuzzleViewModel mPuzzleViewModel;

    /**
     * current puzzle on the sudoKu board
     */
    private Puzzle mPuzzle;

    public SudoKuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SudokuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SudoKuFragment newInstance() {
        SudoKuFragment fragment = new SudoKuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");

        mButtonNumberList = new ArrayList<>();
        mSaverManager = SudoKuSaverManager.getManager(getActivity().getApplicationContext());
        mCells = new Cell[SudoKuBoard.CELL_SIZE];
        mCellsManager = new CellsManager(mCells);
        mCellsManager.setCellAssignmentListener(new CellsManager.AssignmentListener() {
            @Override
            public void onAssign(int row, int col, byte number) {
                if (mUserAssignmentSaver == null) {
                    mUserAssignmentSaver = mSaverManager.getSaverForUser();
                }
                if (mUserAssignmentSaver != null) {
                    mUserAssignmentSaver.addAssignment((byte) row, (byte) col, number);
                }
            }
        });
        mActiveNumber = ASSIGNMENT_UN_FOCUS;
        for (int i = 0; i < mCells.length; i++) {
            mCells[i] = new Cell(i);
        }
        mPuzzleViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(PuzzleViewModel.class);
        mPuzzleViewModel.getLastPuzzle().observe(this, puzzle -> {
            if (puzzle==null) {
                /*
                table puzzles data is empty
                 */
                mPuzzle = new Puzzle();
                mSudoKuGenerateTask = new SudoKuGenerateTask();
                mSudoKuGenerateTask.execute();
//                return;
            }else {
                if (mPuzzle == null) {
                    mPuzzle = new Puzzle();
                }
                mPuzzle.setPuzzleString(puzzle.getPuzzleString());
                mPuzzle.setId(puzzle.getId());
                mPuzzle.setFilename(puzzle.getFilename());
                mPuzzle.setSolved(puzzle.isSolved());
            }
//            mCellsManager.parsePuzzleString(puzzle.getPuzzleString());
            bindPuzzle(mPuzzle.getPuzzleString());
        });
//        if (mBoardString == null || mBoardString.equals("")) {
//            /*
//            generate new puzzle
//             */
//        } else {
//            bindPuzzle(mBoardString);
//        }
        mCanUndo = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.item_grid, container, false);
        mBoardView = view.findViewById(R.id.sudoKu_board_view);
        mBoardView.setListener(new SudoKuBoardView.onTouchListener() {
            @Override
            public void handle(int row, int col) {
                 /*
                 modify cell value if the value is assigned by user
                  */
                if (!mCellsManager.isGenerateByProgram(row, col) && mActiveNumber != ASSIGNMENT_UN_FOCUS) {
                    mCellsManager.assignValue(row, col, mActiveNumber);
                    Log.i(TAG, "handle: assign cell (" + row + ", " + col + ")"
                            + ", number=" + mActiveNumber);
                    mCanUndo = true;
                    boolean solved = mCellsManager.isCompleteSolve();
                    mPuzzle.setSolved(solved);
                }
            }
        });
        mBoardView.bindCellsManager(mCellsManager);
        mButton1 = view.findViewById(R.id.button_number_1);
        mButton2 = view.findViewById(R.id.button_number_2);
        mButton3 = view.findViewById(R.id.button_number_3);
        mButton4 = view.findViewById(R.id.button_number_4);
        mButton5 = view.findViewById(R.id.button_number_5);
        mButton6 = view.findViewById(R.id.button_number_6);
        mButton7 = view.findViewById(R.id.button_number_7);
        mButton8 = view.findViewById(R.id.button_number_8);
        mButton9 = view.findViewById(R.id.button_number_9);
        mButtonDelete = view.findViewById(R.id.button_delete_number);
        mButtonShowNextStep = view.findViewById(R.id.button_show_program_next);
        mButtonShowPreviousStep = view.findViewById(R.id.button_show_program_previous);
        mButtonUndo = view.findViewById(R.id.button_undo);
        mButtonRedo = view.findViewById(R.id.button_redo);
        mButtonNumberList.add(mButton1);
        mButtonNumberList.add(mButton2);
        mButtonNumberList.add(mButton3);
        mButtonNumberList.add(mButton4);
        mButtonNumberList.add(mButton5);
        mButtonNumberList.add(mButton6);
        mButtonNumberList.add(mButton7);
        mButtonNumberList.add(mButton8);
        mButtonNumberList.add(mButton9);
        for (Button button :
                mButtonNumberList) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // set the value for assignment
                    mActiveNumber = Byte.parseByte(button.getText().toString());
                    Log.i(TAG, "onClick: active number " + mActiveNumber);
                }
            });
        }
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 set the value to Cell.UNFILLED_VALUE in order to remove previous assignment
                 */
                mActiveNumber = SudoKuConstant.NUMBER_UNCERTAIN;
                Log.i(TAG, "onClick: reset active number");
            }
        });

        mButtonShowNextStep.setOnClickListener(this::showNextAssignmentByProgram);
        int assignIndex = AssignmentPreference.getPreferenceAssignmentStep(getActivity());
        if (assignIndex < 0) {
            /**
             * not previous assignment
             */
            mButtonShowPreviousStep.setEnabled(false);
        } else {
            mButtonShowPreviousStep.setEnabled(true);
        }
        mButtonShowPreviousStep.setOnClickListener(this::showPreviousAssignmentByProgram);
        mButtonUndo.setOnClickListener(this::undoAssignment);
        mButtonRedo.setOnClickListener(this::redoAssignment);
        return view;
    }

    private class SudoKuGenerateTask extends AsyncTask<Void, Void, SudoKuSaver> {
        @Override
        protected SudoKuSaver doInBackground(Void... voids) {
            SudoKuSaver sudoKuSaver = null;
            String boardString = "";
            try {
                sudoKuSaver = SudoKuBoard.GeneratePuzzle();
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: sudoKu generate failed", e);
            }
            return sudoKuSaver;
        }

        @Override
        protected void onPostExecute(SudoKuSaver sudoKuSaver) {
            super.onPostExecute(sudoKuSaver);
            /*
             bind the grid initial values to cells
             */
//            SudoKuSaverManager saverManager = SudoKuSaverManager.getManager(getActivity());
            mPuzzle = new Puzzle();
            mPuzzle.mFilename = UUID.randomUUID().toString();
            mPuzzle.mSolved = false;
            mPuzzle.mPuzzleString = sudoKuSaver.getPuzzleString();
//            bindPuzzle(sudoKuSaver);
//            bindPuzzle(mPuzzle.getPuzzleString());
        }
    }

    private void bindPuzzle(String mBoardString) {
        mCellsManager.parsePuzzleString(mBoardString);
        mBoardView.invalidate();
    }

    private void bindPuzzle(SudoKuSaver sudoKuSaver) {
        mSaverManager.setSudoKuSaverForProgram(sudoKuSaver);
        mSudoKuSaver = sudoKuSaver;
        mUserAssignmentSaver = new SudoKuSaver(mSudoKuSaver);
        mSaverManager.setSudoKuSaverForUser(mUserAssignmentSaver);
        mBoardString = sudoKuSaver.getPuzzleString();
        mCellsManager.parsePuzzleString(mBoardString);
        if (mSudoKuSaver == null) {
            mSudoKuSaver = SudoKuSaverManager.getManager(getActivity()).getSaverForProgram();
        }
        AssignmentPreference.setPreferenceAssignmentStep(getActivity(), -1);
        Puzzle puzzle = new Puzzle();
        puzzle.setFilename(UUID.randomUUID().toString());
        puzzle.setSolved(false);
        puzzle.setPuzzleString(mBoardString);
        mPuzzleViewModel.insert(puzzle);
        mBoardView.invalidate();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the grid initial values when fragment is paused
//        outState.putSerializable(KEY_BOARD_STRING, mBoardString);
    }

    /**
     * show next assignment by program
     *
     * @param view the specified button
     */
    public void showNextAssignmentByProgram(View view) {
        if (mSudoKuSaver == null) {
            mSudoKuSaver = SudoKuSaverManager.getManager(getActivity()).getSaverForProgram();
        }
        int stepIndex = showAssignment(false, mSudoKuSaver);
        if (stepIndex == -1) {
            return;
        }
        if (stepIndex + mSudoKuSaver.getAssignmentOffset() < SudoKuConstant.BOARD_CELL_SIZE) {
            AssignmentPreference.setPreferenceAssignmentStep(getActivity(), stepIndex);
        }
    }

    /**
     * show previous assignment by program
     *
     * @param view the specified button
     */
    public void showPreviousAssignmentByProgram(View view) {
        // TODO: 1/29/21 if click the P_PRE button at the beginning, the app will crash
        if (mSudoKuSaver == null) {
            mSudoKuSaver = SudoKuSaverManager.getManager(getActivity()).getSaverForProgram();
        }
        int stepIndex = showAssignment(true, mSudoKuSaver);
        if (stepIndex == -1) {
            return;
        }
        if (stepIndex >= 0) {
            AssignmentPreference.setPreferenceAssignmentStep(getActivity(), stepIndex);
        }
    }

    /**
     * show next assignment by user
     *
     * @param view the specified button
     */
    public void showNextAssignmentByUser(View view) {
        if (mUserAssignmentSaver == null) {
            mUserAssignmentSaver = SudoKuSaverManager.getManager(getActivity()).getSaverForUser();
        }
        int stepIndex = showAssignment(false, mUserAssignmentSaver);
        if (stepIndex != -1/*the specified assignment exists*/) {
            AssignmentPreference.setPreferenceAssignmentStep(getActivity(), stepIndex + 1);
        }
    }

    public void showPreviousAssignmentByUser(View view) {
        if (mUserAssignmentSaver == null) {
            mUserAssignmentSaver = SudoKuSaverManager.getManager(getActivity()).getSaverForUser();
        }
        int stepIndex = showAssignment(true, mUserAssignmentSaver);
        if (stepIndex != -1) {
            AssignmentPreference.setPreferenceAssignmentStep(getActivity(), stepIndex - 1);
        }
    }

    private int showAssignment(boolean previous, SudoKuSaver sudoKuSaver) {
        mActiveNumber = ASSIGNMENT_UN_FOCUS;
        int stepIndex = AssignmentPreference.getPreferenceAssignmentStep(getActivity());
        Log.i(TAG, "showAssignment: step=" + stepIndex);
        if (previous) {
            if (mShowPrevious) {
                stepIndex--;
            }
//            stepIndex--;
            SudoKuSaver.Assignment assignment = sudoKuSaver.getAssignment(stepIndex);
            if (assignment == null) {
                return -1;
            }
            mCellsManager.assignValue(assignment.getRow(), assignment.getCol(), SudoKuConstant.NUMBER_UNCERTAIN);
            mShowPrevious = true;
        } else {
            if (!mShowPrevious) {
                stepIndex++;
            }
            SudoKuSaver.Assignment assignment = sudoKuSaver.getAssignment(stepIndex);
            if (assignment == null) {
                return -1;
            }
            mCellsManager.assignValue(assignment.getRow(), assignment.getCol(), assignment.getNumber());
            mShowPrevious = false;
        }
        mBoardView.invalidate();
        return stepIndex;
    }


    public void undoAssignment(View view) {
        SudoKuSaver.Assignment lastAssignment = mUserAssignmentSaver.getLastAssignment();
        if (lastAssignment == null || !mCanUndo) {
            return;
        } else {
            mCellsManager.assignValue(lastAssignment.getRow(),
                    lastAssignment.getCol(),
                    SudoKuConstant.NUMBER_UNCERTAIN);
            mCanUndo = !mCanUndo;
            mBoardView.invalidate();
        }
    }

    public void redoAssignment(View view) {
        SudoKuSaver.Assignment secondLastAssignment = mUserAssignmentSaver.getSecondLastAssignment();
        if (secondLastAssignment == null || mCanUndo) {
            return;
        } else {
            mCellsManager.assignValue(secondLastAssignment.getRow(),
                    secondLastAssignment.getCol(),
                    secondLastAssignment.getNumber());
            mCanUndo = !mCanUndo;
            mBoardView.invalidate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: dump puzzle");
        mPuzzle.setPuzzleString(mCellsManager.generatePuzzleString());
        mPuzzleViewModel.update(mPuzzle);
//        dumpPuzzleString();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        mPuzzleDao = SudoKuDatabaseHelper.getDatabase(getActivity()).puzzleDao();
//        mPuzzle = mPuzzleDao.getLatest();
//        if (mPuzzle != null) {
//            // retrieve puzzle from database
//            mBoardString = mPuzzle.mPuzzleString;
//            mSaverManager = SudoKuSaverManager.getManager(getActivity().getApplicationContext());
//            mSaverManager.load(mPuzzle);
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        boolean exist = mPuzzleDao.getPuzzle(mPuzzle.mId) == 1;
//        mSaverManager.dump(mPuzzle);
//        if (exist) {
//            mPuzzleDao.update(mPuzzle);
//        } else {
//            mPuzzleDao.insert(mPuzzle);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}