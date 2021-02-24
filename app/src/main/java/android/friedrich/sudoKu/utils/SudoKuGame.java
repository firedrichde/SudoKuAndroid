package android.friedrich.sudoKu.utils;

import android.app.Application;
import android.friedrich.sudoKu.data.Cell;
import android.friedrich.sudoKu.data.Puzzle;
import android.friedrich.sudoKu.data.PuzzleRepository;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
        final int index = mSelectedRow * SudoKuConstant.BOARD_ROW_SIZE + mSelectedCol;
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
                Set<Integer> indexOfPeers = SudoKuBoard.getGridPeers(index);
                Cell targetCell;
                if (!object.isPresent()) {
                    targetCell = new Cell(index);
                } else {
                    targetCell = object.get();
                    targetCell.setConflictCount(0);
                    byte previousNumber = targetCell.getNumber();
                    /*
                    repeated assignment
                     */
                    if (previousNumber == number) {
                        return;
                    }
                    List<Cell> conflictCells = getConflictCells(cells, indexOfPeers, previousNumber);
                    /*
                    decrease conflict count
                     */
                    if (conflictCells.size() > 0) {
                        removeConflict(conflictCells);
                        mPuzzleRepository.updateCells(conflictCells);
                    }

                }
                targetCell.setNumber(number);
                List<Cell> conflictCells = getConflictCells(cells, indexOfPeers, number);
                if (conflictCells.size() > 0) {
                    addConflict(conflictCells, targetCell);
                    mPuzzleRepository.updateCells(conflictCells);
                }
                mPuzzleRepository.insertCell(targetCell);
            }
        });
    }

    public void handleRemoveNumber() {
        final int index = mSelectedRow * SudoKuConstant.BOARD_ROW_SIZE + mSelectedCol;
        if (index < 0 || index >= SudoKuConstant.BOARD_CELL_SIZE) {
            return;
        }
        sExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Cell> cells = mPuzzleRepository.getCellsList();
                Optional<Cell> object = cells.stream()
                        .filter(cell -> cell.getIndex() == index)
                        .findFirst();
                Set<Integer> indexOfPeers = SudoKuBoard.getGridPeers(index);
                Cell targetCell;
                if (!object.isPresent()) {
                    return;
                } else {
                    targetCell = object.get();
                    if (targetCell.isGenerateByProgram()){
                        return;
                    }
//                    targetCell.setConflictCount(0);
                    byte previousNumber = targetCell.getNumber();
                    /*
                    decrease conflict count
                     */
                    List<Cell> conflictCells = getConflictCells(cells, indexOfPeers, previousNumber);
//                    cells.remove(targetCell);
                    mPuzzleRepository.deleteCell(targetCell);
                    if (conflictCells.size() > 0) {
                        removeConflict(conflictCells);
                        mPuzzleRepository.updateCells(conflictCells);
                    }
                }
            }
        });
    }

    public void updateBoardFocus(int row, int col) {
        mSelectedRow = row;
        mSelectedCol = col;
        mSelectedCellLiveData.postValue(new Pair<>(mSelectedRow, mSelectedCol));
    }

    private List<Cell> getConflictCells(List<Cell> cells, Set<Integer> indexOfPeers, byte number) {
        List<Cell> conflictCells;
        conflictCells = cells.stream()
                .filter(cell -> indexOfPeers.contains(cell.getIndex()))
                .filter(cell -> cell.getNumber() == number)
                .collect(Collectors.toList());
        return conflictCells;
    }

    private void removeConflict(List<Cell> cells) {
        cells.stream()
                .forEach(cell -> cell.decreaseConflictCount());
    }

    private void addConflict
            (List<Cell> cells, Cell targetCell
            ) {
        cells.stream()
                .forEach(cell -> {
                    cell.increaseConflictCount();
                    targetCell.increaseConflictCount();
                });
    }

//    public void updateCellSManager()
}
