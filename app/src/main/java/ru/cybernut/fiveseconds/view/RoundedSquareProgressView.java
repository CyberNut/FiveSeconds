package ru.cybernut.fiveseconds.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import ru.cybernut.fiveseconds.utils.CalculationUtil;

public class RoundedSquareProgressView extends View {

    private double progress;
    private Paint progressBarPaint;

    private float widthInDp = 10;
    private float strokewidth = 0;
    private Canvas canvas;
    private boolean roundedCorners = true;

    public RoundedSquareProgressView(Context context) {
        super(context);
        initializePaints(context);
    }

    public RoundedSquareProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePaints(context);
    }

    public RoundedSquareProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializePaints(context);
    }

    private void initializePaints(Context context) {
        progressBarPaint = new Paint();
        progressBarPaint.setColor(context.getResources().getColor(
                android.R.color.holo_green_dark));
        progressBarPaint.setStrokeWidth(CalculationUtil.convertDpToPx(
                widthInDp, getContext()));
        progressBarPaint.setAntiAlias(true);
        progressBarPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(canvas);
        strokewidth = CalculationUtil.convertDpToPx(widthInDp, getContext());
        int cW = canvas.getWidth();
        int cH = canvas.getHeight();
        float scope = (2 * cW) + (2 * cH) - (4 * strokewidth);
        float hSw = strokewidth / 2;

        Path path = new Path();
        DrawStop drawEnd = getDrawEnd((scope / 100) * Float.valueOf(String.valueOf(progress)), canvas);

        if (drawEnd.place == Place.TOP) {
            if (drawEnd.location > (cW / 2) && progress < 100.0) {
                path.moveTo(cW / 2, hSw);
                path.lineTo(drawEnd.location, hSw);
            } else {
                path.moveTo(cW / 2, hSw);
                path.lineTo(cW - hSw, hSw);
                path.lineTo(cW - hSw, cH - hSw);
                path.lineTo(hSw, cH - hSw);
                path.lineTo(hSw, hSw);
                path.lineTo(strokewidth, hSw);
                path.lineTo(drawEnd.location, hSw);
            }
            canvas.drawPath(path, progressBarPaint);
        }

        if (drawEnd.place == Place.RIGHT) {
            path.moveTo(cW / 2, hSw);
            path.lineTo(cW - hSw, hSw);
            path.lineTo(cW - hSw, 0
                    + drawEnd.location);
            canvas.drawPath(path, progressBarPaint);
        }

        if (drawEnd.place == Place.BOTTOM) {
            path.moveTo(cW / 2, hSw);
            path.lineTo(cW - hSw, hSw);
            path.lineTo(cW - hSw, cH - hSw);
            path.lineTo(cW - strokewidth, cH - hSw);
            path.lineTo(drawEnd.location, cH - hSw);
            canvas.drawPath(path, progressBarPaint);
        }

        if (drawEnd.place == Place.LEFT) {
            path.moveTo(cW / 2, hSw);
            path.lineTo(cW - hSw, hSw);
            path.lineTo(cW - hSw, cH - hSw);
            path.lineTo(hSw, cH - hSw);
            path.lineTo(hSw, cH - strokewidth);
            path.lineTo(hSw, drawEnd.location);
            canvas.drawPath(path, progressBarPaint);
        }
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        this.invalidate();
    }

    public DrawStop getDrawEnd(float percent, Canvas canvas) {
        DrawStop drawStop = new DrawStop();
        strokewidth = CalculationUtil.convertDpToPx(widthInDp, getContext());
        float halfOfTheImage = canvas.getWidth() / 2;

        // top right
        if (percent > halfOfTheImage) {
            float second = percent - (halfOfTheImage);

            // right
            if (second > (canvas.getHeight() - strokewidth)) {
                float third = second - (canvas.getHeight() - strokewidth);

                // bottom
                if (third > (canvas.getWidth() - strokewidth)) {
                    float forth = third - (canvas.getWidth() - strokewidth);

                    // left
                    if (forth > (canvas.getHeight() - strokewidth)) {
                        float fifth = forth - (canvas.getHeight() - strokewidth);

                        // top left
                        if (fifth == halfOfTheImage) {
                            drawStop.place = Place.TOP;
                            drawStop.location = halfOfTheImage;
                        } else {
                            drawStop.place = Place.TOP;
                            drawStop.location = strokewidth + fifth;
                        }
                    } else {
                        drawStop.place = Place.LEFT;
                        drawStop.location = canvas.getHeight() - strokewidth - forth;
                    }

                } else {
                    drawStop.place = Place.BOTTOM;
                    drawStop.location = canvas.getWidth() - strokewidth - third;
                }
            } else {
                drawStop.place = Place.RIGHT;
                drawStop.location = strokewidth + second;
            }

        } else {
            drawStop.place = Place.TOP;
            drawStop.location = halfOfTheImage + percent;
        }

        return drawStop;
    }

    private class DrawStop {

        private Place place;
        private float location;

        public DrawStop() {

        }
    }

    public enum Place {
        TOP, RIGHT, BOTTOM, LEFT
    }

}
