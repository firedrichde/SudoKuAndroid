package android.friedrich.sudoKu;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SudoKuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SudoKuFragment extends Fragment {
    private static final String TAG = "SudoKuFragment";
    private static final String KEY_BOARD_STRING = "boardString";

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
        /*if (savedInstanceState != null) {
            *//*
            load the previous SudoKu if the activity is still in progress
             *//*
            mBoardString = savedInstanceState.getString(KEY_BOARD_STRING, "");
        }*/
        mButtonNumberList = new ArrayList<>();
        mSaverManager = SudoKuSaverManager.getManager(getActivity());
        mCells = new Cell[SudoKuBoard.CELL_SIZE];
        mCellsManager = new CellsManager(mCells);
        mCellsManager.setCellAssignmentListener(new CellsManager.AssignmentListener() {
            @Override
            public void onAssign(int row, int col, byte number) {
                if (mUserAssignmentSaver==null) {
                    mUserAssignmentSaver = mSaverManager.getUserSaver();
                }
                if (mUserAssignmentSaver != null) {
                    mUserAssignmentSaver.addAssignment((byte)row,(byte)col, number);
                }
            }
        });
        mActiveNumber = ASSIGNMENT_UN_FOCUS;
        for (int i = 0; i < mCells.length; i++) {
            mCells[i] = new Cell(i);
        }
        if (mBoardString == null || mBoardString.equals("")) {
            mSudoKuGenerateTask = new SudoKuGenerateTask();
            mSudoKuGenerateTask.execute();
        } else {
//            bindCells(mBoardString);
        }
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
                if (!mCellsManager.isGenerateByProgram(row, col) && mActiveNumber!=ASSIGNMENT_UN_FOCUS) {
                    mCellsManager.assignValue(row, col, mActiveNumber);
                    Log.i(TAG, "handle: assign cell (" + row+", "+col+")"
                            + ", number=" + mActiveNumber);
                    mCanUndo = true;
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
                sudoKuSaver= SudoKuBoard.GeneratePuzzle();
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
            mSaverManager.addSudoKuSaver(sudoKuSaver);
            mSudoKuSaver = sudoKuSaver;
            mUserAssignmentSaver = new SudoKuSaver(mSudoKuSaver);
            mSaverManager.addSudoKuSaverByUser(mUserAssignmentSaver);
            bindCells(sudoKuSaver.getPuzzleString());
        }
    }

    private void bindCells(String gridString) {
        mBoardString = gridString;
        for (int i = 0; i < gridString.length(); i++) {
            char value = gridString.charAt(i);
            if (value != SudoKuBoard.dot) {
                mCells[i].setNumber(Byte.parseByte(String.valueOf(value)));
                mCells[i].setGenerateByProgram(true);
            }
        }
        if (mSudoKuSaver==null){
            mSudoKuSaver = SudoKuSaverManager.getManager(getActivity()).get();
        }
        AssignmentPreference.setPreferenceAssignmentStep(getActivity(),0);
        mBoardView.invalidate();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the grid initial values when fragment is paused
        outState.putSerializable(KEY_BOARD_STRING, mBoardString);
    }

    /**
     * show next assignment by program
     * @param view the specified button
     */
    public void showNextAssignmentByProgram(View view) {
        if (mSudoKuSaver == null) {
            mSudoKuSaver = SudoKuSaverManager.getManager(getActivity()).get();
        }
        int stepIndex = showAssignment(false, mSudoKuSaver);
       if (stepIndex+mSudoKuSaver.getAssignmentOffset() < SudoKuConstant.BOARD_CELL_SIZE){
           AssignmentPreference.setPreferenceAssignmentStep(getActivity(),stepIndex+1);
       }
    }

    /**
     * show previous assignment by program
     * @param view the specified button
     */
    public void showPreviousAssignmentByProgram(View view) {
        if (mSudoKuSaver == null) {
            mSudoKuSaver = SudoKuSaverManager.getManager(getActivity()).get();
        }
        int stepIndex = showAssignment(true, mSudoKuSaver);
        if (stepIndex >0){
            AssignmentPreference.setPreferenceAssignmentStep(getActivity(),stepIndex-1);
        }
    }

    /**
     * show next assignment by user
     * @param view the specified button
     */
    public void showNextAssignmentByUser(View view) {
        if (mUserAssignmentSaver == null) {
            mUserAssignmentSaver = SudoKuSaverManager.getManager(getActivity()).getUserSaver();
        }
        int stepIndex = showAssignment(false,mUserAssignmentSaver);
        if (stepIndex != -1/*the specified assignment exists*/)  {
           AssignmentPreference.setPreferenceAssignmentStep(getActivity(),stepIndex+1);
        }
    }

    public void showPreviousAssignmentByUser(View view) {
        if (mUserAssignmentSaver == null) {
            mUserAssignmentSaver = SudoKuSaverManager.getManager(getActivity()).getUserSaver();
        }
        int stepIndex = showAssignment(true,mUserAssignmentSaver);
        if (stepIndex != -1) {
            AssignmentPreference.setPreferenceAssignmentStep(getActivity(), stepIndex-1);
        }
    }

    private int showAssignment(boolean previous, SudoKuSaver sudoKuSaver){
        mActiveNumber = ASSIGNMENT_UN_FOCUS;
        int stepIndex = AssignmentPreference.getPreferenceAssignmentStep(getActivity());
        Log.i(TAG, "showAssignment: step="+stepIndex);
        if (stepIndex == -1) {
            Log.e(TAG, "showNextAssignment: should set up the step of assignment");
        }else {
            SudoKuSaver.Assignment assignment = sudoKuSaver.getAssignment(stepIndex);
            if (assignment == null) {
                return -1;
            }
            if (previous) {
                mCellsManager.assignValue(assignment.getRow(),assignment.getCol(),SudoKuConstant.NUMBER_UNCERTAIN);
            }else {
                mCellsManager.assignValue(assignment.getRow(),assignment.getCol(),assignment.getNumber());
            }
            mBoardView.invalidate();
        }
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
}