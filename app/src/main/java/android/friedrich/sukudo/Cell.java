package android.friedrich.sukudo;

public class Cell {
    private static final int UNIT_SIZE = 9;
    private int row;
    private int col;
    private int index;
    private String possibleValue;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        index = row * UNIT_SIZE + col;
    }

    public Cell(int index) {
        this.index = index;
        this.row = index / UNIT_SIZE;
        this.col = index % UNIT_SIZE;
    }

    public static int getIndex(int row,int col){
        return row*UNIT_SIZE+col;
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
}
