package android.friedrich.sukudo;

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
    private static final String KEY_GRID_STRING = "gridString";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private SudoKuBoardView mBoardView;
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
    private List<Button> mButtonNumberList;
    private String mActiveNumber;
    private Cell[] mCells;
    private SudoKuGenerateTask mSudoKuGenerateTask;
    private String mGridString;

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
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mGridString = savedInstanceState.getString(KEY_GRID_STRING, "");
        }
        mButtonNumberList = new ArrayList<>();
        mCells = new Cell[Grid.SIZE];
        for (int i = 0; i < mCells.length; i++) {
            mCells[i] = new Cell(i);
        }
        if (mGridString == null || mGridString.equals("")) {
            mSudoKuGenerateTask = new SudoKuGenerateTask();
            mSudoKuGenerateTask.execute();
        }else{
            bindCells(mGridString);
        }
//        if (getArguments() != null) {
//        }
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
                int index = Cell.getIndex(row, col);
                if (mActiveNumber != null && !mActiveNumber.equals("") && !mCells[index].isServerMode()) {
                    mCells[index].setPossibleValue(mActiveNumber);
                    Log.i(TAG, "handle: index=" + index + ", number=" + mActiveNumber);
                }
            }
        });
        mBoardView.bindCells(mCells);
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
                    mActiveNumber = button.getText().toString();
                    Log.i(TAG, "onClick: active number " + mActiveNumber);
                }
            });
        }
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActiveNumber = Cell.UNFILLED_VALUE;
                Log.i(TAG, "onClick: reset active number");
            }
        });
        return view;
    }

    private class SudoKuGenerateTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String gridString = "";
            try {
                gridString = Grid.Generate();

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: sudoKu generate failed", e);
            }
            return gridString;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            bindCells(s);
        }
    }

    private void bindCells(String gridString) {
        mGridString = gridString;
        for (int i = 0; i < gridString.length(); i++) {
            char value = gridString.charAt(i);
            if (value != Grid.dot) {
                mCells[i].setPossibleValue(String.valueOf(value));
                mCells[i].setServerMode(true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_GRID_STRING, mGridString);
    }
}