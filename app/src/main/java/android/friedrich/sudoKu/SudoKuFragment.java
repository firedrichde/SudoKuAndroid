package android.friedrich.sudoKu;

import android.icu.lang.UScript;
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
        mCells = new Cell[SudoKuBoard.CELL_SIZE];
        mCellsManager = new CellsManager(mCells);
        mActiveNumber = SudoKuConstant.NUMBER_UNCERTAIN;
        for (int i = 0; i < mCells.length; i++) {
            mCells[i] = new Cell(i);
        }
        if (mBoardString == null || mBoardString.equals("")) {
            mSudoKuGenerateTask = new SudoKuGenerateTask();
            mSudoKuGenerateTask.execute();
        } else {
//            bindCells(mBoardString);
        }
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
                if (!mCellsManager.isGenerateByProgram(row, col)) {
                    mCellsManager.assignValue(row, col, mActiveNumber);
                    Log.i(TAG, "handle: assign cell (" + row+", "+col+")"
                            + ", number=" + mActiveNumber);
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
        mButtonShowNextStep = view.findViewById(R.id.button_show_next);
        mButtonShowPreviousStep = view.findViewById(R.id.button_show_previous);
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

        mButtonShowNextStep.setOnClickListener(this::showNextAssignment);
        mButtonShowPreviousStep.setOnClickListener(this::showPreviousAssignment);
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
            SudoKuSaverManager.getManager(getActivity()).addSudoKuSaver(sudoKuSaver);
            mSudoKuSaver = sudoKuSaver;
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

    public void showNextAssignment(View view) {
       int stepIndex = showAssignment(false);
       if (stepIndex+mSudoKuSaver.getAssignmentOffset() < SudoKuConstant.BOARD_CELL_SIZE){
           AssignmentPreference.setPreferenceAssignmentStep(getActivity(),stepIndex+1);
       }
    }

    public void showPreviousAssignment(View view) {
        int stepIndex = showAssignment(true);
        if (stepIndex >0){
            AssignmentPreference.setPreferenceAssignmentStep(getActivity(),stepIndex-1);
        }
    }

    public int showAssignment(boolean previous){
        int stepIndex = AssignmentPreference.getPreferenceAssignmentStep(getActivity());
        Log.i(TAG, "showAssignment: step="+stepIndex);
        if (stepIndex == -1) {
            Log.e(TAG, "showNextAssignment: should set up the step of assignment");
        }else {
            if (mSudoKuSaver == null) {
                mSudoKuSaver = SudoKuSaverManager.getManager(getActivity()).get();
            }
            SudoKuSaver.Assignment assignment = mSudoKuSaver.getAssignment(stepIndex);
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
}