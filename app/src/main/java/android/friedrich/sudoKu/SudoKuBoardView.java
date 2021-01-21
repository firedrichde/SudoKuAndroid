package android.friedrich.sudoKu;

import android.content.Context;
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

public class SudoKuBoardView extends View {
    private static final String TAG = "SudoKuBoardView";
    private static int SIZE = 9;
    private int rowSelected = 3;
    private int colSelected = 2;
    private float cellSizePixel;
    private Paint thickLinePaint;
    private Paint thinLinePaint;
    private Paint selectedCellPaint;
    private Paint relativeCellPaint;
    private Paint selectedCellTextPaint;
    private Paint commonCellTextPaint;
    private onTouchListener mListener;

    private Cell[] mCells;

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
        color = ContextCompat.getColor(context, R.color.red);
        selectedCellTextPaint.setColor(color);

        commonCellTextPaint = new Paint();
        commonCellTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        color = ContextCompat.getColor(context, R.color.black);
        commonCellTextPaint.setColor(color);

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
        selectedCellTextPaint.setTextSize(cellSizePixel/1.5F);
        commonCellTextPaint.setTextSize(cellSizePixel/1.5F);

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
                if (r == rowSelected && c == colSelected) {
                    drawCellText(canvas, r, c, selectedCellTextPaint);
                } else {
                    drawCellText(canvas, r, c, commonCellTextPaint);
                }
            }
        }
    }

    private void drawCellText(Canvas canvas, int row, int col, Paint paint) {
        int index = Cell.getIndex(row, col);
        if (mCells == null || index >= mCells.length || mCells[index] == null) {
            return;
        }
        String text = mCells[index].getPossibleValue();
        if (!text.equals(Cell.UNFILLED_VALUE)) {
            Rect rect = new Rect();
            paint.getTextBounds(text,0,text.length(),rect);
            float width = paint.measureText(text);
            float height = rect.height();
            float x = (float) (col + 0.5) * cellSizePixel;
            float y = (float) (row + 0.5) * cellSizePixel;
            canvas.drawText(text, x-width/2, y+height/2, paint);
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
        rowSelected = row;
        colSelected = col;
        mListener.handle(row, col);
        invalidate();
    }

    public void bindCells(Cell[] cells) {
        mCells = cells;
    }

    public interface onTouchListener {
        void handle(int row, int col);
    }

    public void setListener(onTouchListener listener) {
        mListener = listener;
    }
}
