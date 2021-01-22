package android.friedrich.sudoKu;

public class Cell {
    public static String UNFILLED_VALUE = "0";
    private static final int UNIT_SIZE = 9;
    private int row;
    private int col;
    private int index;
    private String possibleValue;

    /**
     * if cell possible value is assigned by program,serverMode is true, else false
     */
    private boolean generateByProgram;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        index = row * UNIT_SIZE + col;
        possibleValue = UNFILLED_VALUE;
    }

    public Cell(int index) {
        this.index = index;
        this.row = index / UNIT_SIZE;
        this.col = index % UNIT_SIZE;
        possibleValue = UNFILLED_VALUE;
    }

    public static int getIndex(int row, int col) {
        return row * UNIT_SIZE + col;
    }

    public void setPossibleValue(String possibleValue) {
        this.possibleValue = possibleValue;
    }

    public String getPossibleValue() {
        return possibleValue;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getIndex() {
        return index;
    }

    public boolean isGenerateByProgram() {
        return generateByProgram;
    }

    public void setGenerateByProgram(boolean generateByProgram) {
        this.generateByProgram = generateByProgram;
    }
}
