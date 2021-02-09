package android.friedrich.sudoKu.utils;

import android.app.Application;
import android.friedrich.sudoKu.data.Cell;
import android.friedrich.sudoKu.data.Puzzle;
import android.friedrich.sudoKu.data.PuzzleRepository;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SudoKuGame {
    private static ExecutorService sExecutorService = Executors.newFixedThreadPool(4);
    private CellsManager mCellsManager;
    private SudoKuBoard mSudoKuBoard;
    private Application mApplication;
    private PuzzleRepository mPuzzleRepository;

    private int mSelectedRow;
    private int mSelectedCol;

    public MutableLiveData<Pair<Integer, Integer>> mSelectedCellLiveData;
    //    public MutableLiveData<List<Cell>> mCellsLiveData;
    public LiveData<List<Cell>> mCellsLiveData;

    public SudoKuGame(Application application, PuzzleRepository puzzleRepository) {
        mApplication = application;
        mPuzzleRepository = puzzleRepository;
        mSelectedCol = -1;
        mSelectedRow = -1;
        mSelectedCellLiveData = new MutableLiveData<>();
        mSelectedCellLiveData.postValue(new Pair(mSelectedRow, mSelectedCol));
        mCellsLiveData = mPuzzleRepository.getAllCells();
    }

    public void savePuzzleString(String puzzleString) {
        Puzzle puzzle = new Puzzle();
        puzzle.setPuzzleString(puzzleString);
        puzzle.setFilename(UUID.randomUUID().toString());
        puzzle.setSolved(false);
        mPuzzleRepository.insert(puzzle);
    }

    public void saveCells(List<Cell> cells) {
        mPuzzleRepository.insertAllCells(cells);
    }

    public void handleAssignment(byte number) {
        int index = mSelectedRow * SudoKuConstant.BOARD_ROW_SIZE + mSelectedCol;
        if (index < 0 || index >= SudoKuConstant.BOARD_CELL_SIZE) {
            return;
        }
        sExecutorService.execute(new Runnable() {
            @Override
            public void run() {
//                synchronized (CellsManager.class) {
                List<Cell> cells = mPuzzleRepository.getCellsList();
                Optional<Cell> object = cells.stream()
                        .filter(cell -> cell.getIndex() == index)
                        .findFirst();

                Cell targetCell;
                if (!object.isPresent()) {
                    targetCell = new Cell(index);
                } else {
                    targetCell = object.get();
                    targetCell.setConflictCount(0);
                }
                targetCell.setNumber(number);
                Set<Integer> peerIndex = SudoKuBoard.getGridPeers(index);
                cells.stream()
                        .filter(cell ->
                                peerIndex.contains(cell.getIndex()) && cell.getNumber() == number)
                        .forEach(cell -> {
                            cell.increaseConflictCount();
                            targetCell.increaseConflictCount();
                        });
                cells.add(targetCell);
                mPuzzleRepository.insertAllCells(cells);
            }
//            }
        });
//
//        Cell cell = new Cell(index);
//        cell.setNumber((byte)number);
//        mPuzzleRepository.insertCell(cell);
    }

    public void updateBoardFocus(int row, int col) {
        mSelectedRow = row;
        mSelectedCol = col;
        mSelectedCellLiveData.postValue(new Pair<>(mSelectedRow, mSelectedCol));
    }

//    public void updateCellSManager()
}
