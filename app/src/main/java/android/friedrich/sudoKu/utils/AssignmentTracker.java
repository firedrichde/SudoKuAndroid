package android.friedrich.sudoKu.utils;

import android.friedrich.sudoKu.utils.SudoKuConstant;

import androidx.annotation.NonNull;

import java.util.Stack;

public class AssignmentTracker{

    /**
     * record assignment
     */
    private Stack<Node> tracker;

    /**
     * if the tracker works
     */
    private boolean isOn;

    /**
     * tracker step of the assignment
     */
    private int step;

    /**
     * clear the tracker
     */
    public void clear() {
        tracker.clear();
        step = 0;
    }

    public class Node {
        /**
         * index of cell that assigned at current step
         */
        private int cellIndex;

        /**
         * assignment step
         */
        private int step;

        /**
         * the number of assignment
         */
        private byte number;

        private byte row;

        private byte col;

        public Node(int cellIndex, int step, byte number) {
            this.cellIndex = cellIndex;
            this.step = step;
            this.number = number;
            this.row= (byte) (cellIndex / SudoKuConstant.UNIT_CELL_SIZE);
            this.col= (byte) (cellIndex % SudoKuConstant.UNIT_CELL_SIZE);
        }

        public int getCellIndex() {
            return cellIndex;
        }

        public byte getNumber() {
            return number;
        }

        public byte getRow() {
            return row;
        }

        public byte getCol() {
            return col;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("(%d, %d) %d %d %d",
                    cellIndex / SudoKuConstant.UNIT_CELL_SIZE,
                    cellIndex % SudoKuConstant.UNIT_CELL_SIZE,
                    cellIndex,
                    number,
                    step
            );
        }
    }

    public AssignmentTracker() {
        tracker = new Stack<>();
        isOn = false;
        step = 0;
    }

    public void addRecord(int row, int col, byte number) {
        int cellIndex = row * SudoKuConstant.UNIT_CELL_SIZE + col;
        addRecord(cellIndex, number);
    }

    public void addRecord(int cellIndex, byte number) {
//        int step = tracker.size();
        Node record = new Node(cellIndex, step++, number);
        tracker.push(record);
    }

    public void removeRecord() {
        if (!tracker.isEmpty()) {
            tracker.pop();
        } else {
            throw new IllegalArgumentException("can't remove record");
        }
    }

    public byte getLastRow() {
        return tracker.peek().row;
    }

    public byte getLastCol() {
        return tracker.peek().col;
    }

    public byte getLastNumber() {
        return tracker.peek().number;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public Iterable<Node> getSteps() {
        return tracker;
    }

    public int getSize() {
        return tracker.size();
    }

    public int getCurrentStep() {
        return tracker.peek().step;
    }

    /**
     * discard assignments after the specified tracker point
     * @param trackerId the specified tracker history point
     */
    public void rollback(int trackerId) {
        System.out.println("rollback from "+getSize()+" to "+trackerId + "at step "+step);
        while (getSize()> trackerId) {
            removeRecord();
        }
    }
}
