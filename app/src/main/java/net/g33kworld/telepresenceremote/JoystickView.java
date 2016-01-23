package net.g33kworld.telepresenceremote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    private Paint joyPaint;
    private float radius;
    private float centerX;
    private float centerY;
    private float x;
    private float y;

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Set view background image from XML "android:src" attribute (background is blank if attribute is missing)
        setBackgroundResource(attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", 0));

        //Initialize variables for drawing
        joyPaint = new Paint(Color.BLACK);
        radius = 0.0f;
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
        radius = ((w + h) / 2) * 0.1f;
        centerX = w / 2;
        centerY = h / 2;
        x = centerX;
        y = centerY;
    }

    //Draw touch point on canvas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, radius, joyPaint);
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
