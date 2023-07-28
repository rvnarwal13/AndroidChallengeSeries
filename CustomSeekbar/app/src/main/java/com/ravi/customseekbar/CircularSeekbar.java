package com.ravi.customseekbar;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CircularSeekbar extends View {

    private static final int START_ANGLE = 120;
    private static final int SWEEP_ANGLE = 300;
    private int maxProgress = 100;
    private int currentProgress = 0;
    private Paint progressPaint;
    private Paint dottedLinePaint;
    private Paint progressDottedLinePaint;
    private RectF circleBounds;
    private float radius;
    private Path dottedLinePath;
    private Path progressDottedLinePath;
    private boolean isDragging = false;
    private float lastTouchAngle = 0;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public CircularSeekbar(Context context) {
        super(context);
        init(null);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public CircularSeekbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public CircularSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        progressPaint = new Paint();
        progressPaint.setColor(Color.WHITE);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(100);

        dottedLinePaint = new Paint();
        dottedLinePaint.setColor(Color.RED);
        dottedLinePaint.setStyle(Paint.Style.STROKE);
        dottedLinePaint.setStrokeWidth(10);

        progressDottedLinePaint = new Paint();
        progressDottedLinePaint.setColor(Color.GREEN);
        progressDottedLinePaint.setStyle(Paint.Style.STROKE);
        progressDottedLinePaint.setStrokeWidth(10);

        circleBounds = new RectF();
        dottedLinePath = new Path();
        progressDottedLinePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        float diameter = Math.min(w, h) - getPaddingLeft() - getPaddingRight() - progressPaint.getStrokeWidth();
        radius = diameter / 2;
        float centerX = w / 2f;
        float centerY = h / 2f;
        circleBounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        updateDottedLinePath();
    }

    private void updateDottedLinePath() {
        float dottedLineStartX = circleBounds.centerX() + radius;
        float dottedLineStartY = circleBounds.centerY();
        float dottedLineEndX = circleBounds.centerX() + radius;
        float dottedLineEndY = circleBounds.centerY();

        dottedLinePath.reset();
        dottedLinePath.moveTo(dottedLineStartX, dottedLineStartY);
        dottedLinePath.lineTo(dottedLineEndX, dottedLineEndY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float sweepAngle = (float) currentProgress / maxProgress * SWEEP_ANGLE;
//        canvas.drawArc(circleBounds, START_ANGLE, SWEEP_ANGLE, false, progressPaint);

        float centerX = circleBounds.centerX();
        float centerY = circleBounds.centerY();
        float outerRadius = radius + 30;
        float innerRadius = radius - 30;

        for (int i = 0; i <= maxProgress; i += 1) {

            float angle = START_ANGLE + i * SWEEP_ANGLE / maxProgress;
            float startX = (float) (centerX + outerRadius * Math.cos(Math.toRadians(angle)));
            float startY = (float) (centerY + outerRadius * Math.sin(Math.toRadians(angle)));
            float endX = (float) (centerX + innerRadius * Math.cos(Math.toRadians(angle)));
            float endY = (float) (centerY + innerRadius * Math.sin(Math.toRadians(angle)));
            if ((angle <= sweepAngle + START_ANGLE) && sweepAngle != 0) {
                canvas.drawLine(startX, startY, endX, endY, progressDottedLinePaint);
            } else {
                canvas.drawLine(startX, startY, endX, endY, dottedLinePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (isInsideSeekBar(x, y)) {
            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;
            double angle = Math.toDegrees(Math.atan2(y - centerY, x - centerX));

            double startAngle = calculateStartAngle(angle);

            float touchProgress = Math.min((float) (startAngle / SWEEP_ANGLE * maxProgress), maxProgress);

            if(startAngle > SWEEP_ANGLE + 5) {
                touchProgress = 0;
            }

            float angleDiff = (float) (startAngle - lastTouchAngle);
            if (angleDiff < 0) {
                angleDiff += 360;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    isDragging = true;
                    setCurrentProgress((int) touchProgress);
                    lastTouchAngle = (float) angle;
                }
                break;
                case MotionEvent.ACTION_MOVE:
                    if (isDragging) {
                        if (angleDiff > 0) {
                            setCurrentProgress((int) (currentProgress + touchProgress));
                        } else {
                            setCurrentProgress((int) (currentProgress - touchProgress));
                        }
                        lastTouchAngle = (float) angle;
                        updateProgress(x, y);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isDragging = false;
                    break;
            }
        }
        return true;
    }

    private boolean isInsideSeekBar(float x, float y) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float distance = (float) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        float outerRadius = radius + dottedLinePaint.getStrokeWidth() / 2 + 50;
        float innerRadius = radius - dottedLinePaint.getStrokeWidth() / 2 - 50;
        return ((distance <= outerRadius) && (distance >= innerRadius));
    }

    private void updateProgress(float x, float y) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        double angle = Math.toDegrees(Math.atan2(y - centerY, x - centerX));

        double startAngle = calculateStartAngle(angle);

        currentProgress = Math.min((int) (startAngle / SWEEP_ANGLE * maxProgress), maxProgress);

        if (startAngle > SWEEP_ANGLE + 5) {
            currentProgress = 0;
        }
        Log.i("Angle", "" + startAngle);
        Log.i("Progress", "" + currentProgress);
        invalidate();
    }

    private double calculateStartAngle(double angle) {
        angle += 360;
        angle %= 360;
        angle -= START_ANGLE;
        angle += 360;
        angle %= 360;
        return angle;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = Math.max(0, Math.min(currentProgress, maxProgress));
        invalidate();
    }
}
