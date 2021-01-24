package android.friedrich.sudoKu;

import android.util.Log;

import java.util.*;

public class SudoKuBoard {
    private static String TAG = "SudoKuBoard";

    /**
     * the collection of numbers used in SudoKu
     */
    public static final String DIGITS = "123456789";

    /**
     * represent board row from A to I
     */
    private static final String ROWS = "ABCDEFGHI";

    /**
     * represent board col from 1 to 9
     */
    private static final String COLS = "123456789";

    /**
     * represent the cell should be assigned by user
     */
    public static final char dot = '.';

    /**
     * the number of cells in the board
     */
    public static final int CELL_SIZE = 81;

    /**
     * the number of cells related with a specified cell
     */
    private static final int PEER_SIZE = 20;

    /**
     * the number of cells that one row,col,or subBoard contains
     */
    private static final int UNIT_SIZE = 9;

    /**
     * the number of rows,cols and subBoard. Nine rows,nine cols and nine subBoard
     */
    private static final int UNIT_NUMBER = 27;

    /**
     * numbers used at least at the initial status of the board
     */
    private static final int INIT_DIFFERENT_NUMBERS = 8;

    /**
     * the number of unit related with a special cell
     */
    private static final int RELATED_UNITS_SIZE = 3;

    /**
     * the offset of row units
     */
    private static final int ROW_UNIT_OFFSET = 0;

    /**
     * the offset of col units
     */
    private static final int COL_UNIT_OFFSET = 9;

    /**
     * the offset of sub board units
     */
    private static final int SUB_BOARD_UNIT_OFFSET = 18;

    /**
     * the generator for creating a random board
     */
    private static SudoKuBoard generator;

    /**
     * name of the cells in SudoKu board
     */
    public static String[] sCellNameArray;

    /**
     * divide the board in 3*9 units
     */
    public static int[][] sUnits;

    /**
     * peers for each cell
     */
    public static Set<Integer>[] sPeers;

    /**
     * possible numbers(1-9) could be assigned for every cell
     */
    private String[] mPossibleValues;

    private Set<Integer>[] mPeers;
    private int[][] mUnits;

    static {
        sCellNameArray = new String[CELL_SIZE];
        int count = 0;
        for (int i = 0; i < ROWS.length(); i++) {
            for (int j = 0; j < COLS.length(); j++) {
                String name = ROWS.charAt(i) + "" + COLS.charAt(j);
                sCellNameArray[count] = name;
                count++;
            }
        }
        setGridUnits();
        setGridPeers();
    }

    public SudoKuBoard() throws Exception {
        mPossibleValues = new String[CELL_SIZE];
        int count = 0;
        for (int i = 0; i < ROWS.length(); i++) {
            for (int j = 0; j < COLS.length(); j++) {
                String name = ROWS.charAt(i) + "" + COLS.charAt(j);
                mPossibleValues[count] = DIGITS;
                count++;
            }
        }
        setUnits();
        setPeers();
    }

    public static void main(String[] args) throws Exception {
        String values = "4.....8.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......";
        SudoKuBoard sudoKuBoardSolver = new SudoKuBoard();
        String gridString = Generate();
        System.out.println(gridString);
        sudoKuBoardSolver.solve(gridString);
        System.out.println(sudoKuBoardSolver);
    }

    /**
     * choose a random char in the string
     *
     * @param values the specified String
     * @return the random char in the specified String
     */
    public static char getRandomChar(String values) {
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(values.length());
        return values.charAt(index);
    }

    /**
     * judge whether the specified sudoKu is solved
     *
     * @param sudoKuBoard the specified sudoKu board
     * @return true if the sudoKu is solved, else return false
     */
    public static boolean check(SudoKuBoard sudoKuBoard) {
        for (int cellIndex = 0; cellIndex < CELL_SIZE; cellIndex++) {
            /*
              every cell is assigned with a number
             */
            if (sudoKuBoard.mPossibleValues[cellIndex].length() > 1) {
                return false;
            }
        }
        for (int cellIndex = 0; cellIndex < CELL_SIZE; cellIndex++) {
            Set<Integer> peers = getGridPeers(cellIndex);
            for (Integer x :
                    peers) {
                /*
                  the specified cell is conflict with it's peers
                 */
                if (sudoKuBoard.mPossibleValues[x].equals(sudoKuBoard.mPossibleValues[cellIndex])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String Generate() throws Exception {
        if (generator == null) {
            generator = new SudoKuBoard();
        }
        String gridString = "";
        // TODO: 1/21/21 constant 10 should be assigned by a constant variable
        int limit = 18;
        /*
            number of cells which generated by program is in range(limit,limit+10)
         */
        gridString = generator.randomSudoKuPuzzle(limit, limit + 10);
        SudoKuBoard sudukuBoard = new SudoKuBoard();
        while (!sudukuBoard.solve(gridString)) {
            gridString = generator.randomSudoKuPuzzle(limit, limit + 10);
            sudukuBoard = new SudoKuBoard();
        }
        return gridString;
    }

    /**
     * solve the sudoKu from the given string
     *
     * @param boardString initial status of the sudoKu is represented by a string
     * @return true if program can solve the sudoKu,else return false
     */
    public boolean solve(String boardString) {
        boolean hasParsed = parseGrid(boardString);
        if (!hasParsed) {
            Log.e(TAG, "solve: the board string is illegal " + boardString);
            return false;
        } else {
            String[] possibleValues = copy(mPossibleValues);
            String[] resultArray = search(possibleValues);
            if (resultArray == null) {
                return false;
            }
            mPossibleValues = resultArray;
            return check(this);
        }
    }

    /**
     * parse the specified string to assign number for every cell.
     *
     * @param boardString initial status of the sudoKu is represented by a string
     * @return true if the numbers can be filled on the board without logical conflict, else false
     */
    public boolean parseGrid(String boardString) {
        assert boardString.length() == 81;
        for (int cellIndex = 0; cellIndex < boardString.length(); cellIndex++) {
            char ch = boardString.charAt(cellIndex);
            /*
            if the char is not digits(for example '.'(dot)), means the cell number is implicit now.
            if one cell assigned with the specified number conflict with another cell, then break
            the parse loop.
             */
            if (validateNumber(ch) && !assignValue(mPossibleValues, cellIndex, ch)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateNumber(char number) {
        return DIGITS.indexOf(number) != -1;
    }

    /**
     * assign certain number to the specified cell.
     * assign the given number means eliminate other numbers in the possible values of the
     * specified cell. for example: the cell's possible values is "3456",if is to be assigned with
     * number '4', eliminate other numbers "356" in the loop.
     *
     * @param possibleValues possible numbers of all cells
     * @param cellIndex      the position of the specified cell
     * @param number         the given number for assignment
     * @return true if the specified cell can be assigned with the given number, else false
     */
    private boolean assignValue(String[] possibleValues, int cellIndex, char number) {
        String otherValues = possibleValues[cellIndex].replace(String.valueOf(number), "");
        for (int i = 0; i < otherValues.length(); i++) {
            if (!eliminate(possibleValues, cellIndex, /* number need to remove*/otherValues.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * eliminate the given number for the specified cell
     *
     * @param possibleValues possible numbers of all cells
     * @param cellIndex      the position of the specified cell
     * @param number         the specified number
     * @return true if the specified cell can be eliminate with the given number, else false
     */
    private boolean eliminate(String[] possibleValues, int cellIndex, char number) {
        /*
        if number has been removed from the specified cell, no eliminate operation.
        the conditions is false, means two peers from different units have been assigned with
        the number, not assignment conflict
         */
        // TODO: 1/22/21 if the condition is false, means conflict assignment?
        if (possibleValues[cellIndex].indexOf(number) != -1) {
            String values = possibleValues[cellIndex].replace(String.valueOf(number), "");
            /*
            remove number from the specified cell
             */
            possibleValues[cellIndex] = values;
            if (values.length() == 0) {
                /*
                 remove all possible value
                 */
                return false;
            } else if (values.length() == 1) {
                Set<Integer> peers = getGridPeers(cellIndex);
                for (Integer x :
                        peers) {
                    if (!eliminate(possibleValues, x, values.charAt(0))) {
                        /*
                        failed to eliminate number in the cell's peers
                         */
                        return false;
                    }
                }
            }
            int[] relativeUnitsByIndex = getRelativeUnitsByIndex(cellIndex);
            /*
            scan relative units to see if some cells can be assigned explicit or conflict may
            maybe occur.
             */
            for (int i = 0; i < relativeUnitsByIndex.length; i++) {
                int[] unit = sUnits[relativeUnitsByIndex[i]];
                int count = 0;
                int numberEnsureIndex = 0;
                for (int j = 0; j < unit.length; j++) {
                    if (possibleValues[unit[j]].indexOf(number) != -1) {
                        count++;
                        numberEnsureIndex = unit[j];
                    }
                }
                if (count == 0) {
                    /*
                     no place for number in the unit
                     */
                    return false;
                } else if (count == 1) {
                    /*
                     only one place for number in the unit
                     */
                    if (!assignValue(possibleValues, numberEnsureIndex, number)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Deprecated
    private Set<Integer> getPeers(int index) {
        return mPeers[index];
    }

    @Deprecated
    private void setPeers() throws Exception {
        mPeers = (HashSet<Integer>[]) new HashSet[CELL_SIZE];
        for (int i = 0; i < CELL_SIZE; i++) {
            mPeers[i] = new HashSet<>();
            int[] unitsIndex = getUnitsIndex(i);
            for (int j = 0; j < unitsIndex.length; j++) {
                for (int k = 0; k < mUnits[unitsIndex[j]].length; k++) {
                    if (mUnits[unitsIndex[j]][k] == i) {
                        continue;
                    }
                    mPeers[i].add(mUnits[unitsIndex[j]][k]);
                }
            }
            if (mPeers[i].size() != PEER_SIZE) {
                throw new Exception();
            }
        }
    }

    /**
     * set the peers for each cell of the Gird
     */
    public static void setGridPeers() {
        sPeers = (HashSet<Integer>[]) new HashSet[CELL_SIZE];
        for (int cellIndex = 0; cellIndex < CELL_SIZE; cellIndex++) {
            sPeers[cellIndex] = new HashSet<>();
            int[] relativeUnitIndexArray = getRelativeUnitsByIndex(cellIndex);
            for (int unitIndex = 0; unitIndex < relativeUnitIndexArray.length; unitIndex++) {
                for (int k = 0; k < sUnits[relativeUnitIndexArray[unitIndex]].length; k++) {
                    /*
                     void to make square self as its peer
                     */
                    if (sUnits[relativeUnitIndexArray[unitIndex]][k] == cellIndex) {
                        continue;
                    }
                    sPeers[cellIndex].add(sUnits[relativeUnitIndexArray[unitIndex]][k]);
                }
            }
            /*
             ensure the size of peers is PEER_SIZE
             */
            assert (sPeers[cellIndex].size() == PEER_SIZE);
        }
    }

    /**
     * get the peers of the specified cell
     *
     * @param index the position of the specified cell
     * @return peers of the specified cell
     */
    public static Set<Integer> getGridPeers(int index) {
        if (index < 0 || index >= CELL_SIZE) {
            throw new IllegalArgumentException(String.format("index(%s) is not in range[0,%d)", index, CELL_SIZE));
        } else {
            return sPeers[index];
        }
    }

    /**
     * copy the specified string array
     *
     * @param src the source string array
     * @return string array that is copied from src
     */
    private String[] copy(String[] src) {
        String[] dst = new String[src.length];
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i];
        }
        return dst;
    }

    /**
     * use depth first search to fill the sudoKu.
     *
     * @param possibleValues possible numbers of all cells
     * @return null if all assignments cause conflict,else return certain values of all cells.
     */
    public String[] search(String[] possibleValues) {
        int filledCellCount = 0;
        int minLengthOfPossibleValue = DIGITS.length();
        int indexOfFirstAssignCell = 0;
        for (int cellIndex = 0; cellIndex < possibleValues.length; cellIndex++) {
            int length = possibleValues[cellIndex].length();
            if (length == 1) {
                filledCellCount++;
            } else {
                if (length < minLengthOfPossibleValue) {
                    minLengthOfPossibleValue = length;
                    indexOfFirstAssignCell = cellIndex;
                }
            }
        }
        if (filledCellCount == CELL_SIZE) {
            /*
            all cells in the board are filled
             */
            return possibleValues;
        } else {
            for (int i = 0; i < possibleValues[indexOfFirstAssignCell].length(); i++) {
                String[] copy = copy(possibleValues);
                if (assignValue(copy, indexOfFirstAssignCell, copy[indexOfFirstAssignCell].charAt(i))) {
                    String[] values = search(copy);
                    if (values != null) {
                        return values;
                    }
                } else {
                    Log.i(TAG, String.format("search: assign %c to index %d failed",
                            indexOfFirstAssignCell, copy[indexOfFirstAssignCell].charAt(i)));
                }
            }
        }
        return null;
    }


    /**
     * return the array of relative unit's index at the specified position of SudoKu squares.
     *
     * @param index index of the specified Grid cell
     * @return the array of Grid unit's index that contains the specified square
     */
    public static int[] getRelativeUnitsByIndex(int index) {
        int[] unitIndexArray = new int[RELATED_UNITS_SIZE];
        int row = index / UNIT_SIZE;
        int col = index % UNIT_SIZE;
        int subGridIndex = (row / 3) * 3 + col / 3;
        // row unit related to the specified square
        unitIndexArray[0] = row;
        // col unit related to the specified square
        unitIndexArray[1] = col + 9;
        // TODO: 1/21/21 if we should retrieve row and col position from the square name
//        String squareName = sSquareNames[index];
        // subGird unit related to the specified square
        unitIndexArray[2] = subGridIndex + 18;
        return unitIndexArray;
    }

    /**
     * return the unit at the specified position of SudoKu
     *
     * @param index index of the unit
     * @return the array of cell index that make up the unit
     */
    public static int[] getUnitByIndex(int index) {
        return sUnits[index];
    }

    @Deprecated
    private int[] getUnitsIndex(int index) {
        int[] unitsIndex = new int[3];
        int row = index / UNIT_SIZE;
        int col = index % UNIT_SIZE;
        unitsIndex[0] = row;
        unitsIndex[1] = col + 9;
        int subIndex = (row / 3) * 3 + col / 3;
        unitsIndex[2] = subIndex + 18;
        return unitsIndex;
    }

    @Deprecated
    private void setUnits() {
        mUnits = new int[UNIT_NUMBER][UNIT_SIZE];
        // all row units
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                mUnits[i][j] = i * UNIT_SIZE + j;
            }
        }
        // all col units
        for (int i = 9; i < 18; i++) {
            for (int j = 0; j < 9; j++) {
                mUnits[i][j] = j * UNIT_SIZE + i - 9;
            }
        }
        // all subBoard units
        for (int i = 18; i < UNIT_NUMBER; i++) {
            int count = 0;
            int j = (i - 18) / 3;
            int p = (i - 18) % 3;
            int base = j * 27 + 3 * p;
            for (int k = 0; k < 3; k++) {
                for (int l = 0; l < 3; l++) {
                    int index = base + 9 * k + l;
                    mUnits[i][count++] = index;
                }
            }
        }
    }

    /**
     * divide the SudoKu board into 27 units
     * nine row units, nine col units, nine subBoard units
     */
    public static void setGridUnits() {
        sUnits = new int[UNIT_NUMBER][UNIT_SIZE];
        // all row units
        for (int i = ROW_UNIT_OFFSET; i < COL_UNIT_OFFSET; i++) {
            for (int j = 0; j < UNIT_SIZE; j++) {
                sUnits[i][j] = (i - ROW_UNIT_OFFSET) * UNIT_SIZE + j;
            }
        }
        // all column units
        for (int i = COL_UNIT_OFFSET; i < SUB_BOARD_UNIT_OFFSET; i++) {
            for (int j = 0; j < UNIT_SIZE; j++) {
                sUnits[i][j] = j * UNIT_SIZE + i - COL_UNIT_OFFSET;
            }
        }
        // all subBoard units
        for (int i = SUB_BOARD_UNIT_OFFSET; i < UNIT_NUMBER; i++) {
            int count = 0;
            int j = (i - SUB_BOARD_UNIT_OFFSET) / 3;
            int p = (i - SUB_BOARD_UNIT_OFFSET) % 3;
            int base = j * 27 + 3 * p;
            for (int k = 0; k < 3; k++) {
                for (int l = 0; l < 3; l++) {
                    int index = base + 9 * k + l;
                    sUnits[i][count++] = index;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int width = 0;
        for (int i = 0; i < CELL_SIZE; i++) {
            if (mPossibleValues[i].length() > width) {
                width = mPossibleValues[i].length();
            }
        }
        width += 1;
        StringBuilder lineBuild = new StringBuilder();
        lineBuild.append("\n");
        for (int i = 0; i < 9 * width; i++) {
            lineBuild.append("-");
        }
        lineBuild.append("\n");
        String line = lineBuild.toString();
        for (int i = 0; i < CELL_SIZE; i++) {
            builder.append(String.format("%" + width + "s", mPossibleValues[i]));
            if ((i + 1) % 27 == 0) {
                builder.append(line);
            } else if ((i + 1) % UNIT_SIZE == 0) {
                builder.append("\n");
            } else if ((i + 1) % 3 == 0) {
                builder.append("|");
            }
        }
        return builder.toString();
    }

    @Deprecated
    public String display(String[] possibleValues) {
        StringBuilder builder = new StringBuilder();
        int width = 0;
        for (int i = 0; i < CELL_SIZE; i++) {
            if (possibleValues[i].length() > width) {
                width = possibleValues[i].length();
            }
        }
        width += 1;
        StringBuilder lineBuild = new StringBuilder();
        lineBuild.append("\n");
        for (int i = 0; i < 9 * width; i++) {
            lineBuild.append("-");
        }
        lineBuild.append("\n");
        String line = lineBuild.toString();
        for (int i = 0; i < CELL_SIZE; i++) {
            builder.append(String.format("%" + width + "s", possibleValues[i]));
            if ((i + 1) % 27 == 0) {
                builder.append(line);
            } else if ((i + 1) % UNIT_SIZE == 0) {
                builder.append("\n");
            } else if ((i + 1) % 3 == 0) {
                builder.append("|");
            }
        }
        return builder.toString();
    }

    /**
     * return possible numbers of all cells assigned with DIGITS numbers
     *
     * @return possible numbers of all cells assigned with DIGITS numbers
     */
    public String[] newPossibleValues() {
        String[] possibleValues = new String[CELL_SIZE];
        for (int i = 0; i < CELL_SIZE; i++) {
            possibleValues[i] = DIGITS;
        }
        return possibleValues;
    }

    /**
     * generate a random sudoKu. number of filled cells in the puzzle is between
     * {@param filledCellMinNumber} and {@param filledCellMaxNumber}.
     *
     * @param filledCellMinNumber minimum filled cells
     * @param filledCellMaxNumber maximum filled cells
     * @return a string represent a sudoKu, character '.'(dot) means unfilled cell, character '1' to
     * '9' means the specified cell is assigned with correspond digit.
     */
    public String randomSudoKuPuzzle(int filledCellMinNumber, int filledCellMaxNumber) {
        StringBuilder boardStringBuilder = null;
        String[] possibleValues = null;
        while (!reachSudoKuGenerateTarget(possibleValues,
                filledCellMinNumber, filledCellMaxNumber)) {
            possibleValues = newPossibleValues();
            List<Integer> cellIndexOrder = new ArrayList<>();
            for (int cellIndex = 0; cellIndex < CELL_SIZE; cellIndex++) {
                cellIndexOrder.add(cellIndex);
            }
            /*
            generate a random order of cellIndex
             */
            Collections.shuffle(cellIndexOrder, new Random(System.currentTimeMillis()));
            for (int orderIndex = 0; orderIndex < cellIndexOrder.size(); orderIndex++) {
                if (!assignValue(possibleValues, /*cellIndex=*/cellIndexOrder.get(orderIndex),
                        /*number=*/getRandomChar(possibleValues[cellIndexOrder.get(orderIndex)]))) {
                    break;
                } else {
                    if (reachSudoKuGenerateTarget(possibleValues,
                            filledCellMinNumber, filledCellMaxNumber)) {
                        boardStringBuilder = new StringBuilder();
                        for (int j = 0; j < possibleValues.length; j++) {
                            if (possibleValues[j].length() > 1) {
                                boardStringBuilder.append(".");
                            } else {
                                boardStringBuilder.append(possibleValues[j]);
                            }
                        }
                        /*
                        generate a desired sudoKu
                         */
                        break;
                    }
                }
            }
        }
        assert boardStringBuilder != null;
        return boardStringBuilder.toString();
    }

    /**
     * judge whether the given sudoKu meet the generate target
     *
     * @param possibleValues       possible numbers of all cells assigned with DIGITS numbers
     * @param filledCellsMinNumber minimum filled cells
     * @param filledCellsMaxNumber maximum filled cells
     * @return true if the used numbers meet the target {@value INIT_DIFFERENT_NUMBERS} and number
     * of filled cells is in the range.
     */
    private boolean reachSudoKuGenerateTarget(
            String[] possibleValues, int filledCellsMinNumber, int filledCellsMaxNumber) {
        Set<String> numbersUsedSet = new HashSet<>();
        if (possibleValues == null) {
            return false;
        }
        int filledCellCount = 0;
        for (int cellIndex = 0; cellIndex < possibleValues.length; cellIndex++) {
            if (possibleValues[cellIndex].length() == 1) {
                filledCellCount++;
                numbersUsedSet.add(possibleValues[cellIndex]);
            }
        }
        return filledCellCount >= filledCellsMinNumber && filledCellCount <= filledCellsMaxNumber
                && numbersUsedSet.size() >= INIT_DIFFERENT_NUMBERS;
    }
}
