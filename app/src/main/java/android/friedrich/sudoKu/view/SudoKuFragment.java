package android.friedrich.sudoKu.view;

import android.content.Context;
import android.friedrich.sudoKu.databinding.ItemGridBinding;
import android.friedrich.sudoKu.utils.AssignmentPreference;
import android.friedrich.sudoKu.data.Cell;
import android.friedrich.sudoKu.utils.CellsManager;
import android.friedrich.sudoKu.R;
import android.friedrich.sudoKu.utils.SudoKuBoard;
import android.friedrich.sudoKu.view.custom.SudoKuBoardView;
import android.friedrich.sudoKu.utils.SudoKuConstant;
import android.friedrich.sudoKu.utils.SudoKuSaver;
import android.friedrich.sudoKu.utils.SudoKuSaverManager;
import android.friedrich.sudoKu.data.Puzzle;
import android.friedrich.sudoKu.viewmodels.PuzzleViewModel;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Collections;
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

    private ItemGridBinding mItemGridBinding;
    /**
     * assignment is not on focus
     */
    @Deprecated
    private static final byte ASSIGNMENT_UN_FOCUS = 10;

    /**
     * view for SudoKu board
     */
    private SudoKuBoardView mBoardView;


    private ImageButton mButtonRemoveNumber;

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
    @Deprecated
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
    @Deprecated
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
        return new SudoKuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");

        mButtonNumberList = new ArrayList<>();
        mSaverManager = SudoKuSaverManager.getManager(getActivity().getApplicationContext());
//        mCells = new Cell[SudoKuBoard.CELL_SIZE];
        mCellsManager = new CellsManager();
        mCellsManager.setCellAssignmentListener((row, col, number) -> {
            if (mUserAssignmentSaver == null) {
                mUserAssignmentSaver = mSaverManager.getSaverForUser();
            }
            if (mUserAssignmentSaver != null) {
                mUserAssignmentSaver.addAssignment((byte) row, (byte) col, number);
            }
        });
//        mActiveNumber = ASSIGNMENT_UN_FOCUS;
//        for (int i = 0; i < mCells.length; i++) {
//            mCells[i] = new Cell(i);
//        }
        mPuzzleViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(PuzzleViewModel.class);
        mPuzzleViewModel.mSudoKuGame.mSelectedCellLiveData.observe(this,
                selectedPair -> updateBoardFocus(selectedPair));
        mPuzzleViewModel.mSudoKuGame.mCellsLiveData.observe(this,
                cells -> {
                    if (cells == null || cells.size() == 0) {
                        SudoKuGenerateTask generateTask = new SudoKuGenerateTask();
                        generateTask.execute();
                    } else {
                        updateBoardUI(cells);
                    }
                });
        mCanUndo = false;
    }

    private void updateBoardUI(List<Cell> cells) {
        mBoardView.updateBoardUI(cells);
    }

    private void updateBoardFocus(Pair<Integer, Integer> selectedPair) {
        mBoardView.updateBoardFocus(selectedPair.first, selectedPair.second);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mItemGridBinding = ItemGridBinding.inflate(inflater,container, false);
        View view = mItemGridBinding.getRoot();
        mBoardView = mItemGridBinding.sudoKuBoardView;
        mBoardView.setListener((row, col) -> mPuzzleViewModel.mSudoKuGame.updateBoardFocus(row,col));
        mBoardView.bindCellsManager(mCellsManager);

        Collections.addAll(mButtonNumberList,
                mItemGridBinding.buttonNumber1,
                mItemGridBinding.buttonNumber2,
                mItemGridBinding.buttonNumber3,
                mItemGridBinding.buttonNumber4,
                mItemGridBinding.buttonNumber5,
                mItemGridBinding.buttonNumber6,
                mItemGridBinding.buttonNumber7,
                mItemGridBinding.buttonNumber8,
                mItemGridBinding.buttonNumber9
                );
        mButtonUndo = mItemGridBinding.buttonUndo;
        mButtonRedo = mItemGridBinding.buttonRedo;
        mButtonRemoveNumber = mItemGridBinding.buttonRemoveNumber;
        mButtonShowNextStep = mItemGridBinding.buttonShowProgramNext;
        mButtonShowPreviousStep = mItemGridBinding.buttonShowProgramPrevious;

        mButtonNumberList.stream().forEach(button -> {
            String number = button.getText().toString();
            button.setOnClickListener((buttonView) -> mPuzzleViewModel.mSudoKuGame.handleAssignment((byte)Integer.parseInt(number)));
        });
        mButtonRemoveNumber.setOnClickListener(v -> {
            mPuzzleViewModel.mSudoKuGame.handleRemoveNumber();
            Log.i(TAG, "onClick: reset active number");
        });

        mButtonShowNextStep.setOnClickListener(this::showNextAssignmentByProgram);
        int assignIndex = AssignmentPreference.getPreferenceAssignmentStep(requireActivity());
        if (assignIndex < 0) {
            /*
              not previous assignment
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
//            String boardString = "";
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
            bindPuzzle(sudoKuSaver.getPuzzleString());
        }
    }

    private void bindPuzzle(String mBoardString) {
        List<Cell> cells = CellsManager.parseCellsFromPuzzleString(mBoardString);
        cells.stream().forEach(cell -> cell.setGenerateByProgram(true));
        mPuzzleViewModel.mSudoKuGame.savePuzzleString(mBoardString);
        mPuzzleViewModel.mSudoKuGame.saveCells(cells);
//        mBoardView.updateBoardUI(cells);
    }

    private void bindPuzzle(SudoKuSaver sudoKuSaver) {
        mSaverManager.setSudoKuSaverForProgram(sudoKuSaver);
        mSudoKuSaver = sudoKuSaver;
        mUserAssignmentSaver = new SudoKuSaver(mSudoKuSaver);
        mSaverManager.setSudoKuSaverForUser(mUserAssignmentSaver);
        mBoardString = sudoKuSaver.getPuzzleString();
        mCellsManager.parsePuzzleString(mBoardString);
        if (mSudoKuSaver == null) {
            mSudoKuSaver = SudoKuSaverManager.getManager(requireActivity()).getSaverForProgram();
        }
        AssignmentPreference.setPreferenceAssignmentStep(requireActivity(), -1);
        Puzzle puzzle = new Puzzle();
        puzzle.setFilename(UUID.randomUUID().toString());
        puzzle.setSolved(false);
        puzzle.setPuzzleString(mBoardString);
//        mPuzzleViewModel.insert(puzzle);
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
            mSudoKuSaver = SudoKuSaverManager.getManager(requireActivity()).getSaverForProgram();
        }
        int stepIndex = showAssignment(true, mSudoKuSaver);
        if (stepIndex == -1) {
            return;
        }
        if (stepIndex >= 0) {
            AssignmentPreference.setPreferenceAssignmentStep(requireActivity(), stepIndex);
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
//        mActiveNumber = ASSIGNMENT_UN_FOCUS;
        int stepIndex = AssignmentPreference.getPreferenceAssignmentStep(requireActivity());
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
        } else {
            mCellsManager.assignValue(secondLastAssignment.getRow(),
                    secondLastAssignment.getCol(),
                    secondLastAssignment.getNumber());
            mCanUndo = !mCanUndo;
            mBoardView.invalidate();
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.i(TAG, "onPause: dump puzzle");
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
}