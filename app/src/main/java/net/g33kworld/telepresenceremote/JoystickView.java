package net.g33kworld.telepresenceremote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    private Paint bgPaint;
    private Paint fgPaint;
    private Paint linePaint;
    private Path bgArrows;
    private float arrowRadius;
    private float dotRadius;
    private float centerX;
    private float centerY;
    private float x;
    private float y;

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Initialize all variables for drawing
        bgPaint = new Paint();
        bgPaint.setColor(Color.GRAY);
        fgPaint = new Paint();
        fgPaint.setColor(Color.BLACK);
        linePaint = new Paint();
        linePaint.setColor(Color.DKGRAY);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        bgArrows = new Path();
        arrowRadius = 0.0f;
        dotRadius = 0.0f;
        centerX = 1.0f;
        centerY = 1.0f;

        //Initialize X and Y coordinates
        x = 1.0f;
        y = 1.0f;
    }

    //Constrain the view to be square regardless of available space
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Use the smallest layout dimension for both width and height
        setMeasuredDimension(Math.min(getMeasuredWidth(), getMeasuredHeight()), Math.min(getMeasuredWidth(), getMeasuredHeight()));
    }

    //Update variables dependent on view size (also resets stored X and Y coordinates)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //Calculate size-dependent display variables
        arrowRadius = ((w + h) / 2) * 0.3f;
        dotRadius = ((w + h) / 2) * 0.1f;
        centerX = w / 2;
        centerY = h / 2;
        x = centerX;
        y = centerY;

        //Calculate diamond for joystick arrows
        bgArrows = new Path();
        bgArrows.setFillType(Path.FillType.EVEN_ODD);
        bgArrows.moveTo(dotRadius, centerY);
        bgArrows.lineTo(centerX, dotRadius);
        bgArrows.lineTo(w - dotRadius, centerY);
        bgArrows.lineTo(centerX, h - dotRadius);
        bgArrows.lineTo(dotRadius, centerY);
        bgArrows.close();

        //Calculate joystick line thickness
        linePaint.setStrokeWidth(dotRadius);
    }

    //Draw joystick background and touch point on canvas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw main background circle
        canvas.drawCircle(centerX, centerY, centerX, bgPaint);
        //Draw diamond for background arrows
        canvas.drawPath(bgArrows, fgPaint);
        //Draw circle to remove center of diamond
        canvas.drawCircle(centerX, centerY, arrowRadius, bgPaint);

        //Draw joystick line
        canvas.drawLine(centerX, centerY, x, y, linePaint);
        //Draw touch point
        canvas.drawCircle(x, y, dotRadius, fgPaint);
    }

    //Process touch events
    //Note: Invoked after onTouchListeners, so values returned from them will be one frame behind
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        //Update X and Y coordinates
        x = event.getX();
        y = event.getY();

        //Force view to be redrawn to show new touch point
        invalidate();

        //Consume touch event
        return true;
    }

    //Return raw X coordinate of last touch
    public float getRawX() {
        return x;
    }

    //Return raw Y coordinate of last touch
    public float getRawY() {
        return y;
    }

    //Return raw X and Y coordinates of last touch
    public float[] getRawAxes() {
        return new float[] {getRawX(), getRawY()};
    }

    //Return X coordinate of last touch scaled to [-1.0, 1.0]
    public float getScaledX() {
        return -((x - centerX) / centerX);
    }

    //Return Y coordinate of last touch scaled to [-1.0, 1.0]
    public float getScaledY() {
        return -((y - centerX) / centerX);
    }

    //Return X and Y coordinates of last touch scaled to [-1.0, 1.0]
    public float[] getScaledAxes() {
        return new float[] {getScaledX(), getScaledY()};
    }

    //Reset stored X and Y coordinates to scaled 0 (center of View)
    public void reset() {
        x = centerX;
        y = centerY;

        //Force view to be redrawn to reflect updated coordinates
        invalidate();
    }
}
