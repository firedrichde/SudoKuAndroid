package android.friedrich.sudoKu;

public class CellsManager {
    private Cell[] mCells;

    public CellsManager(Cell[] cells) {
        mCells = cells;
    }

    public void assignValue(int index, String value) {
        mCells[index].setPossibleValue(value);
//        int[] peers  = SudoKuBoard.

    }

    public void assignNode(int index, String note) {
        String oldNote = mCells[index].getPossibleValue();
        if (!oldNote.contains(note)) {
            mCells[index].setPossibleValue(oldNote.concat(note));
        }
    }

    public Cell[] getCells() {
        return mCells;
    }
}
