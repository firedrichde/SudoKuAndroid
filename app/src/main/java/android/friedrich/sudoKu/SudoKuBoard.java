package android.friedrich.sudoKu;

import java.util.*;

public class SudoKuBoard {
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
     * the number of cells in board
     */
    public static final int CELL_SIZE = 81;
    private static final int PEER_SIZE = 20;
    private static final int UNIT_SIZE = 9;
    private static final int UNIT_NUMBER = 27;
    private static final int INIT_DIFFERENT_NUMBERS = 8;
    private static final int RELATED_UNITS_SIZE = 3;

    private static SudoKuBoard generator;

    public static String[] sSquareNames;
    public static int[][] sUnits;
    public static Set<Integer>[] sPeers;

    private String[] mPossibleValues;
    private String[] mPossibleValuesBackup;
    private Set<Integer>[] mPeers;
    private int[][] mUnits;

    static {
        sSquareNames = new String[CELL_SIZE];
        int count = 0;
        for (int i = 0; i < ROWS.length(); i++) {
            for (int j = 0; j < COLS.length(); j++) {
                String name = ROWS.charAt(i) + "" + COLS.charAt(j);
                sSquareNames[count] = name;
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
//        String values = "4.....8.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......";
        String values = "4.....8.5.3..........7......2.....6.....8.4......1.......6.3.7.5..2.....1.4......";
        SudoKuBoard sudoKuBoardGenerator = new SudoKuBoard();
        SudoKuBoard sudoKuBoardSolver = new SudoKuBoard();
//        boolean solved = grid.solve(values);
////        grid.generateForAllCell();
//        if (solved) {
//        System.out.println(grid);
//        }else {
//            System.out.println("not solved");
//            System.out.println(grid);
//        }
        String gridString = Generate();
        System.out.println(gridString);
        sudoKuBoardSolver.solve(gridString);
        System.out.println(sudoKuBoardSolver);
    }

    public static char getRandomChar(String values) {
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(values.length());
        return values.charAt(index);
    }

    public static boolean check(SudoKuBoard sudukuBoard) {
        for (int i = 0; i < CELL_SIZE; i++) {
            if (sudukuBoard.mPossibleValues[i].length() > 1) {
                return false;
            }
        }
        for (int i = 0; i < CELL_SIZE; i++) {
            Set<Integer> peers = sudukuBoard.getPeers(i);
            for (Integer x :
                    peers) {
                if (x == i) {
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
        int limit = 18;
        gridString = generator.random_puzzle(limit, limit + 10);
        SudoKuBoard sudukuBoard = new SudoKuBoard();
        while (!sudukuBoard.solve(gridString)) {
            gridString = generator.random_puzzle(limit, limit + 10);
            sudukuBoard = new SudoKuBoard();
        }
        return gridString;
    }

    public boolean solve(String values) {
        boolean hasParsed = parseGrid(values);
        if (!hasParsed) {
            System.err.println("grid is illegal");
            return false;
        } else {
            String[] possibleValues = copy(mPossibleValues);
            String[] ret = search(possibleValues);
            mPossibleValues = ret;
            return check(this);
        }
    }

    public void generateForAllCell() {
//        Candidate candidate = new Candidate();
//        for (int i = 0; i < ROWS.length(); i++) {
//            for (int j = 0; j < COLS.length(); j++) {
//                String name = ROWS.charAt(i)+""+COLS.charAt(j);
//                mSquares.put(name,candidate.toString());
//                mSquareNames.add(name);
//            }
//        }
//        System.out.println(candidate);
    }

    public boolean parseGrid(String grid) {
        assert grid.length() == 81;
        String[] possible = copy(mPossibleValues);
        for (int i = 0; i < grid.length(); i++) {
            char ch = grid.charAt(i);
//            if (ch==dot){
//                continue;
//            }
            if (validValue(ch, DIGITS) && !assignValue(possible, i, ch)) {
                return false;
            }
        }
        mPossibleValues = possible;
        return true;
    }

    private boolean validValue(char ch, String s) {
        return s.indexOf(ch) != -1;
    }

    private boolean assignValue(String[] possibleValues, int index, char ch) {
//        System.out.println("assign "+index+" num="+ch);
        String otherValues = possibleValues[index].replace(String.valueOf(ch), "");
//        mPossibleValues[index] = String.valueOf(ch);
        for (int i = 0; i < otherValues.length(); i++) {
            if (!eliminate(possibleValues, index, otherValues.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean eliminate(String[] possibleValues, int index, char ch) {
        if (possibleValues[index].indexOf(ch) == -1) {
            return true;
        } else {
            String values = possibleValues[index].replace(String.valueOf(ch), "");
            possibleValues[index] = values;
            if (values.length() == 0) {
                // remove all possible value
                return false;
            } else if (values.length() == 1) {
                /*int[] peers = getPeers(index);
                for (int i = 0; i < peers.length; i++) {
                    if (!eliminate(peers[i], values.charAt(0))) {
                        return false;
                    }
                }*/
//                System.out.println(display(possibleValues));
                Set<Integer> peers = getPeers(index);
                for (Integer x :
                        peers) {
                    if (!eliminate(possibleValues, x, values.charAt(0))) {
                        return false;
                    }
                }
            }
            int[] unitsIndex = getUnitsIndex(index);
            for (int i = 0; i < unitsIndex.length; i++) {
                int[] unit = mUnits[unitsIndex[i]];
                int count = 0;
                int numberEnsureIndex = 0;
                for (int j = 0; j < unit.length; j++) {
                    if (possibleValues[unit[j]].indexOf(ch) != -1) {
                        count++;
                        numberEnsureIndex = unit[j];
                    }
                }
                if (count == 0) {
                    // no place for number ch in the unit
                    return false;
                } else if (count == 1) {
                    if (!assignValue(possibleValues, numberEnsureIndex, ch)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private Set<Integer> getPeers(int index) {
        return mPeers[index];
    }

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
//    public Set<Integer>

    /**
     * set the peers for each square of the Gird
     */
    public static void setGridPeers() {
        sPeers = (HashSet<Integer>[]) new HashSet[CELL_SIZE];
        for (int squareIndex = 0; squareIndex < CELL_SIZE; squareIndex++) {
            sPeers[squareIndex] = new HashSet<>();
            int[] relativeUnitIndexArray = getRelativeUnitsByIndex(squareIndex);
            for (int unitIndex = 0; unitIndex < relativeUnitIndexArray.length; unitIndex++) {
                for (int k = 0; k < sUnits[relativeUnitIndexArray[unitIndex]].length; k++) {
                    // void to make square self as its peer
                    if (sUnits[relativeUnitIndexArray[unitIndex]][k] == squareIndex) {
                        continue;
                    }
                    sPeers[squareIndex].add(sUnits[relativeUnitIndexArray[unitIndex]][k]);
                }
            }
            // ensure the size of peers is PEER_SIZE
            assert (sPeers[squareIndex].size() == PEER_SIZE);
        }
    }

    public static Set<Integer> getGridPeers(int index) {
        if (index < 0 || index >= CELL_SIZE) {
            throw new IllegalArgumentException(String.format("index(%s) is not in range[0,%d)", index, CELL_SIZE));
        } else {
            return sPeers[index];
        }
    }

    private void backup() {
        mPossibleValuesBackup = new String[CELL_SIZE];
        for (int i = 0; i < CELL_SIZE; i++) {
            mPossibleValuesBackup[i] = mPossibleValues[i];
        }
    }

    private void recover() {
        for (int i = 0; i < CELL_SIZE; i++) {
            mPossibleValues[i] = mPossibleValuesBackup[i];
        }
    }

    private String[] copy(String[] s) {
        String[] ret = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            ret[i] = s[i];
        }
        return ret;
    }

    public String[] search(String[] possibleValues) {
//        System.out.println(display(possibleValues));
        int count = 0;
        int minLength = DIGITS.length();
        int minPossibleValueIndex = 0;
        for (int i = 0; i < possibleValues.length; i++) {
            int length = possibleValues[i].length();
            if (length == 1) {
                count++;
            } else {
                if (length < minLength) {
                    minLength = length;
                    minPossibleValueIndex = i;
                }
            }
        }
        if (count == CELL_SIZE) {
            return possibleValues;
        } else {
            for (int i = 0; i < possibleValues[minPossibleValueIndex].length(); i++) {
                String[] copy = copy(possibleValues);
//                System.out.println("index="+minPossibleValueIndex+", num="+copy[minPossibleValueIndex].charAt(i));
                if (assignValue(copy, minPossibleValueIndex, copy[minPossibleValueIndex].charAt(i))) {
                    String[] values = search(copy);
                    if (values != null) {
                        return values;
                    }
                } else {
                    System.out.println("assign failed");
                }
            }
        }
        return null;
    }

    /*@Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 1;
        for (String s :
                mSquares.keySet()) {
            stringBuilder.append(s);
            stringBuilder.append(" ");
            if (index % 9 == 0) {
                stringBuilder.append("\n");
            }
            index++;
        }
        return stringBuilder.toString();
    }*/

    @Deprecated
    private int getSubGridIndex(int index) {
        return getUnitsIndex(index)[2];
    }

    /**
     * return the array of relative unit's index at the specified position of Gird squares.
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
        // all subgrid units
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

    public static void setGridUnits() {
        sUnits = new int[UNIT_NUMBER][UNIT_SIZE];
        // all row units
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sUnits[i][j] = i * UNIT_SIZE + j;
            }
        }
        // all col units
        for (int i = 9; i < 18; i++) {
            for (int j = 0; j < 9; j++) {
                sUnits[i][j] = j * UNIT_SIZE + i - 9;
            }
        }
        // all subgrid units
        for (int i = 18; i < UNIT_NUMBER; i++) {
            int count = 0;
            int j = (i - 18) / 3;
            int p = (i - 18) % 3;
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

    public String[] newPossibleValues() {
        String[] possibleValues = new String[CELL_SIZE];
        for (int i = 0; i < CELL_SIZE; i++) {
            possibleValues[i] = DIGITS;
        }
        return possibleValues;
    }

    public String random_puzzle(int min, int max) {
        StringBuilder builder = null;
        String[] possibleValues = newPossibleValues();
        int count = 0;
        while (!reachTarget(possibleValues, min, max)) {
            count++;
            possibleValues = newPossibleValues();
            List<Integer> order = new ArrayList<>();
            for (int i = 0; i < CELL_SIZE; i++) {
                order.add(i);
            }
            Collections.shuffle(order, new Random(System.currentTimeMillis()));
            for (int i = 0; i < CELL_SIZE; i++) {
                if (!assignValue(possibleValues, /*index=*/order.get(i), getRandomChar(possibleValues[order.get(i)]))) {
                    break;
                } else {
                    if (reachTarget(possibleValues, min, max)) {
                        builder = new StringBuilder();
                        for (int j = 0; j < possibleValues.length; j++) {
                            if (possibleValues[j].length() > 1) {
                                builder.append(".");
                            } else {
                                builder.append(possibleValues[j]);
                            }
                        }
                        break;
                    }
                }
            }
        }
        return builder.toString();
    }

    private boolean reachTarget(String[] possibleValues, int minLimit, int maxLimit) {
        Set<String> differentNumberSet = new HashSet<>();
        int count = 0;
        for (int i = 0; i < possibleValues.length; i++) {
            if (possibleValues[i].length() == 1) {
                count++;
                differentNumberSet.add(possibleValues[i]);
            }
        }
        return count >= minLimit && count <= maxLimit && differentNumberSet.size() >= INIT_DIFFERENT_NUMBERS;
    }
}
