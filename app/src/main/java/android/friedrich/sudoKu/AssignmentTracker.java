package android.friedrich.sudoKu;

import android.os.SystemClock;

import androidx.annotation.NonNull;

import java.util.Stack;

public class AssignmentTracker{
    private static int STEP = 1;
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

        public Node(int cellIndex, int step, byte number) {
            this.cellIndex = cellIndex;
            this.step = step;
            this.number = number;
        }

        public int getCellIndex() {
            return cellIndex;
        }

        public byte getNumber() {
            return number;
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

    private Stack<Node> tracker;

    private boolean isOn;

    public AssignmentTracker() {
        tracker = new Stack<>();
        isOn = false;
    }

    public void addRecord(int row, int col, byte number) {
        int cellIndex = row * SudoKuConstant.UNIT_CELL_SIZE + col;
        addRecord(cellIndex, number);
    }

    public void addRecord(int cellIndex, byte number) {
//        int step = tracker.size();
        int step = STEP++;
        Node record = new Node(cellIndex, step, number);
        tracker.push(record);
    }

    public void removeRecord() {
        if (!tracker.isEmpty()) {
            tracker.pop();
        } else {
            throw new IllegalArgumentException("can't remove record");
        }
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

    public void rollback(int trackerId) {
        System.out.println("rollback from "+getSize()+" to "+trackerId + "at step "+STEP);
        while (getSize()> trackerId) {
            removeRecord();
        }
    }
}
