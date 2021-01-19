package android.friedrich.sukudo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

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
    private onTouchListener mListener;

    public SudoKuBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Resources resources = context.getResources();
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
        int color = ContextCompat.getColor(context, R.color.light_yellow);
        selectedCellPaint.setColor(color);

        relativeCellPaint = new Paint();
        relativeCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        color = ContextCompat.getColor(context, R.color.light_white);
        relativeCellPaint.setColor(color);

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
    }

    public void updateMeasurements(int width) {
        cellSizePixel = ((float) width) / SIZE;

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
        mListener.handle(row,col);
        invalidate();
    }

    public interface onTouchListener {
        void handle(int row,int col);
    }

    public void setListener(onTouchListener listener) {
        mListener = listener;
    }
}
