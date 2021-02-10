package android.friedrich.sudoKu.utils;

import android.friedrich.sudoKu.data.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CellsManager {
    private Cell[] mCells;

    private AssignmentListener mCellAssignmentListener;

    public CellsManager(Cell[] cells) {
        mCells = cells;
    }

    public void setCellAssignmentListener(AssignmentListener mCellAssignmentListener) {
        this.mCellAssignmentListener = mCellAssignmentListener;
    }

    /**
     * assign the given number to the specified cell. if the assignment cause or reduce conflict,
     * update conflict count among peer cells
     *
     * @param row    the row position of cell
     * @param col    the column position of cell
     * @param number the give number
     */
    public void assignValue(int row, int col, byte number) {
        int cellIndex = getIndex(row, col);
        int[] relativeUnitsIndex = SudoKuBoard.getRelativeUnitsByIndex(cellIndex);

        Cell cell = mCells[cellIndex];
        if (!cell.isAssigned() && number == SudoKuConstant.NUMBER_UNCERTAIN) {
            /*
            delete number in a empty cell
             */
            return;
        } else {
            mCellAssignmentListener.onAssign(row, col, number);
            Set<Integer> peersIndexSet = SudoKuBoard.getGridPeers(cellIndex);
            if (number == SudoKuConstant.NUMBER_UNCERTAIN) {
                /*
                delete number in a filled cell
                 */
                byte previous_assign_number = cell.getNumber();
                for (Integer peerIndex :
                        peersIndexSet) {
                    Cell peerCell = mCells[peerIndex];
                    if (peerCell.isAssigned() && peerCell.getNumber() == previous_assign_number) {
                        /*
                         decrease the conflict
                         */
                        peerCell.decreaseConflictCount();
                    }
                }
                cell.clearConflictCount();
                cell.setNumber(SudoKuConstant.NUMBER_UNCERTAIN);
            } else {
                /*
                assign number
                 */
                if (cell.isAssigned()) {
                    if (cell.getNumber() == number) {
                       /*
                       repeat assignment
                        */
                        return;
                    } else {
                        byte previous_assign_number = cell.getNumber();
                        cell.clearConflictCount();
                        cell.setNumber(number);
                        for (Integer peerIndex :
                                peersIndexSet) {
                            Cell peerCell = mCells[peerIndex];
                            if (peerCell.isAssigned()) {
                                if (peerCell.getNumber() == previous_assign_number) {
                                    peerCell.decreaseConflictCount();
                                } else if (peerCell.getNumber() == number) {
                                    peerCell.increaseConflictCount();
                                    cell.increaseConflictCount();
                                }
                            }
                        }
                    }
                } else {

                   /*
                   assign number to a empty cell
                    */
                    cell.setNumber(number);
                    for (Integer peerIndex :
                            peersIndexSet) {
                        Cell peerCell = mCells[peerIndex];
                        if (peerCell.isAssigned()) {
                            if (peerCell.getNumber() == number) {
                                peerCell.increaseConflictCount();
                                cell.increaseConflictCount();
                            }
                        }
                    }
                }
            }
        }
    }


    public void assignNode(int index, String note) {
        String oldNote = mCells[index].getPossibleValue();
        if (!oldNote.contains(note)) {
            mCells[index].setPossibleValue(oldNote.concat(note));
        }
    }

    public String getAssignValueOrNote(int index) {
        return mCells[index].getPossibleValue();
    }

    public Cell[] getCells() {
        return mCells;
    }

    /**
     * return the position of the specified cell in the SudoKu board
     *
     * @param row the row position
     * @param col the column position
     * @return the position of the specified cell
     */
    public int getIndex(int row, int col) {
        return row * SudoKuConstant.UNIT_CELL_SIZE + col;
    }

    /**
     * return the cell in the specified position in the board
     *
     * @param row the row position
     * @param col the column position
     * @return null if the position is outside the board, else the desired cell
     */
    public Cell getCell(int row, int col) {
        int index = getIndex(row, col);
        if (index >= SudoKuConstant.BOARD_CELL_SIZE) {
            return null;
        } else {
            return mCells[index];
        }
    }

    /**
     * judge if the specified cell is assigned by program
     *
     * @param row the row position
     * @param col the column position
     * @return true if the cell is assigned by program, else false
     */
    public boolean isGenerateByProgram(int row, int col) {
        return isGenerateByProgram(/*cellIndex*/getIndex(row, col));
    }

    private boolean isGenerateByProgram(int cellIndex) {
        return mCells[cellIndex].isGenerateByProgram();
    }

    /**
     * generate a string that represent current status of SudoKu
     *
     * @return a string represent sudoKu puzzle
     */
    public String generatePuzzleString() {
        StringBuilder puzzleBuilder = new StringBuilder();
        for (int i = 0; i < mCells.length; i++) {
            if (mCells[i].isAssigned()) {
                puzzleBuilder.append(mCells[i].getNumber());
            } else {
                puzzleBuilder.append(SudoKuBoard.dot);
            }
        }
        return puzzleBuilder.toString();
    }

    public static List<Cell> parseCellsFromPuzzleString(String puzzle) {
       List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < puzzle.length(); i++) {
            if (puzzle.charAt(i) != SudoKuBoard.dot) {
                Cell cell = new Cell(i);
                cell.setNumber(Byte.parseByte(String.valueOf(puzzle.charAt(i))));
                cells.add(cell);
            }
        }
        return cells;
    }

    public void parsePuzzleString(String puzzle) {
        for (int i = 0; i < puzzle.length(); i++) {
            if (puzzle.charAt(i) != SudoKuBoard.dot) {
                mCells[i].setNumber(Byte.parseByte(puzzle.substring(i, i + 1)));
            } else {
                mCells[i].setNumber(SudoKuConstant.NUMBER_UNCERTAIN);
            }
        }
    }

    public boolean isCompleteSolve() {
        return SudoKuBoard.check(generatePuzzleString());
    }

    public void bind(List<Cell> cells) {
        mCells = new Cell[SudoKuConstant.BOARD_CELL_SIZE];
        cells.stream().forEach(cell -> {
            int index = cell.getIndex();
            mCells[index] = cell;
        });
    }

    /**
     * interface definition for a callback to be invoked when a cell assignment occurs
     */
    public interface AssignmentListener {
        /**
         * call when a cell assignment occurs
         *
         * @param row    cell row
         * @param col    cell column
         * @param number cell number
         */
        void onAssign(int row, int col, byte number);
    }
}
