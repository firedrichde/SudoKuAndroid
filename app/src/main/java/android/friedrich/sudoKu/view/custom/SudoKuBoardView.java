package android.friedrich.sudoKu.view.custom;

import android.content.Context;
import android.friedrich.sudoKu.R;
import android.friedrich.sudoKu.utils.SudoKuConstant;
import android.friedrich.sudoKu.data.Cell;
import android.friedrich.sudoKu.utils.CellsManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Stack;

public class SudoKuBoardView extends View {
    private static final String TAG = "SudoKuBoardView";
    private static final int SIZE = SudoKuConstant.UNIT_CELL_SIZE;
    /**
     * row of current cell selected
     */
    private int rowSelected = 3;

    /**
     * column of current cell selected
     */
    private int colSelected = 2;
    private float cellSizePixel;
    /**
     * paint for SudoKu borders
     */
    private Paint thickLinePaint;

    /**
     * paint for SudoKu cell borders
     */
    private Paint thinLinePaint;

    /**
     * paint for current cell selected
     */
    private Paint selectedCellPaint;

    /**
     * paint for the peers of row and column units
     */
    private Paint relativeCellPaint;
    private Paint selectedCellTextPaint;
    private Paint commonCellTextPaint;

    // TODO: 1/28/21 distinguish cell text assignment between user and program
    /**
     * paint for cell text assigned by user
     */
    private Paint userAssignmentCellTextPaint;

    /**
     * paint for cell text assigned by program
     */
    private Paint programAssignmentCellTextPaint;
    /**
     * paint for text of cell that conflict with its peer
     */
    private Paint conflictCellTextPaint;

    private Paint lastCellAssignedTextPaint;

    private onTouchListener mListener;


    private CellsManager mCellsManager;

    private Stack<Integer> mAssignTracker;

    public SudoKuBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int color;
        thickLinePaint = new Paint();
        thickLinePaint.setStyle(Paint.Style.STROKE);
        thickLinePaint.setColor(Color.BLACK);
        thickLinePaint.setStrokeWidth(4F);

        thinLinePaint = new Paint();
        thinLinePaint.setStyle(Paint.Style.STROKE);
        thinLinePaint.setColor(Color.BLACK);
        thinLinePaint.setStrokeWidth(2F);

        selectedCellPaint = new Paint();
        selectedCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        color = ContextCompat.getColor(context, R.color.light_yellow);
        selectedCellPaint.setColor(color);

        relativeCellPaint = new Paint();
        relativeCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        color = ContextCompat.getColor(context, R.color.light_white);
        relativeCellPaint.setColor(color);

        selectedCellTextPaint = new Paint();
        selectedCellTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        color = ContextCompat.getColor(context, R.color.green);
        selectedCellTextPaint.setColor(color);

        commonCellTextPaint = new Paint();
        commonCellTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        color = ContextCompat.getColor(context, R.color.black);
        commonCellTextPaint.setColor(color);

        conflictCellTextPaint = new Paint();
        conflictCellTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        color = ContextCompat.getColor(context, R.color.red);
        conflictCellTextPaint.setColor(color);

        lastCellAssignedTextPaint = new Paint(commonCellTextPaint);
        color = ContextCompat.getColor(context, R.color.light_green);
        lastCellAssignedTextPaint.setColor(color);

        userAssignmentCellTextPaint = new Paint(commonCellTextPaint);
        color = ContextCompat.getColor(context, R.color.light_blue);
        userAssignmentCellTextPaint.setColor(color);

        programAssignmentCellTextPaint = new Paint(commonCellTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizePixels, sizePixels);

    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        updateMeasurements(getMeasuredWidth());
        fillCells(canvas);
        drawLines(canvas);
        drawCellsText(canvas);
    }

    public void updateMeasurements(int width) {
        cellSizePixel = ((float) width) / SIZE;
        selectedCellTextPaint.setTextSize(cellSizePixel / 1.5F);
        commonCellTextPaint.setTextSize(cellSizePixel / 1.5F);
        conflictCellTextPaint.setTextSize(cellSizePixel / 1.5F);
        lastCellAssignedTextPaint.setTextSize(cellSizePixel / 1.5F);
        userAssignmentCellTextPaint.setTextSize(cellSizePixel / 1.5F);
        programAssignmentCellTextPaint.setTextSize(cellSizePixel / 1.5F);
    }

    private void drawLines(Canvas canvas) {
        canvas.drawRect(0F, 0F, getMeasuredWidth(), getMeasuredHeight(), thickLinePaint);
        for (int i = 0; i <= SIZE; i++) {
            if (i % 3 == 0) {
                canvas.drawLine(0F, i * cellSizePixel, getMeasuredWidth(), i * cellSizePixel, thickLinePaint);
                canvas.drawRect(i * cellSizePixel, 0F, i * cellSizePixel, getMeasuredHeight(), thickLinePaint);
            } else {
                canvas.drawLine(0F, i * cellSizePixel, getMeasuredWidth(), i * cellSizePixel, thinLinePaint);
                canvas.drawRect(i * cellSizePixel, 0F, i * cellSizePixel, getMeasuredHeight(), thinLinePaint);
            }
        }
    }

    private void fillCells(Canvas canvas) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (r == rowSelected && c == colSelected) {
                    fillCell(canvas, r, c, selectedCellPaint);
                } else if (r == rowSelected || c == colSelected) {
                    fillCell(canvas, r, c, relativeCellPaint);
                }
            }
        }
    }

    private void fillCell(Canvas canvas, int row, int col, Paint paint) {
        canvas.drawRect(cellSizePixel * col, cellSizePixel * row, cellSizePixel * (col + 1), cellSizePixel * (row + 1), paint);
    }

    private void drawCellsText(Canvas canvas) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                drawCellText(canvas, r, c);
            }
        }
    }

    private void drawCellText(Canvas canvas, int row, int col) {
        if (mCellsManager == null) {
            return;
        }
        Cell cell = mCellsManager.getCell(row, col);
        if (cell.isAssigned()) {
            /*
           draw cell number
             */
            Paint paint = null;
            if (cell.isGenerateByProgram()) {
                paint = programAssignmentCellTextPaint;
            } else if (cell.getConflictCount() > 0) {
                paint = conflictCellTextPaint;
            } else if (row == rowSelected && col == colSelected) {
                paint = selectedCellTextPaint;
            } else {
                paint = userAssignmentCellTextPaint;
            }
            byte cellNumber = cell.getNumber();
            Rect rect = new Rect();
            String cellText = String.valueOf(cellNumber);
            paint.getTextBounds(cellText, 0, cellText.length(), rect);
            float width = paint.measureText(cellText);
            float height = rect.height();
            float x = (float) (col + 0.5) * cellSizePixel;
            float y = (float) (row + 0.5) * cellSizePixel;
            canvas.drawText(cellText, x - width / 2, y + height / 2, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    private void handleActionDown(float x, float y) {
        int col = (int) (x / cellSizePixel);
        int row = (int) (y / cellSizePixel);
        Log.i(TAG, "handleActionDown: (" + row + ", " + col + ")");
//        rowSelected = row;
//        colSelected = col;
        mListener.handle(row, col);
        invalidate();
    }

    public void bindCellsManager(CellsManager manager) {
        mCellsManager = manager;
    }

    public void updateBoardFocus(Integer row, Integer col) {
        rowSelected = row;
        colSelected = col;
        invalidate();
    }

    public void updateBoardUI(List<Cell> cells) {
        mCellsManager.bind(cells);
        invalidate();
    }

    public interface onTouchListener {
        void handle(int row, int col);
    }

    public void setListener(onTouchListener listener) {
        mListener = listener;
    }
}
