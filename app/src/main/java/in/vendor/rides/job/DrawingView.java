package in.vendor.rides.job;

/**
 * Created by PrabhagaranR on 13-04-19
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private static float STROKE_WIDTH = 5f;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private Paint drawPaint, canvasPaint;
    /** Need to track this so the dirty region can accommodate the stroke. **/
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    public static  List<Path> paths = new ArrayList<Path>();
    public static List<Paint> paints = new ArrayList<Paint>();
    /**
     * Optimizes painting by invalidating the smallest possible area.
     */
    public float lastTouchX;
    public float lastTouchY;
    public final RectF dirtyRect = new RectF();
    public int[] colors;
    public static Path path = new Path();
    public int nextColor;
    private int paintColor = 0xff000000;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            //colors = new int[] { Color.BLUE, Color.CYAN, Color.GREEN,Color.MAGENTA, Color.YELLOW, Color.RED, Color.WHITE };
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(paintColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
            paints.add(paint);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        try {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Erases the signature.
     */
    public void clear() {
        try {
            for (Path path : paths) {
                path.reset();
            }
            // Repaints the entire view.
            invalidate();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            canvasPaint = new Paint(Paint.DITHER_FLAG);
            if (isInEditMode()) {
                canvas.drawARGB(255, 255, 0, 0);
                canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            }
            super.onDraw(canvas);
            int i = 0;
            for (Path path : paths) {
                canvas.drawPath(path, paints.get(i++));
                canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
                if (i == paints.size()) {
                    i = 0;
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {

            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path = new Path();
                    paths.add(path);
                    path.moveTo(eventX, eventY);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    path.lineTo(eventX, eventY);
                case MotionEvent.ACTION_UP:
                    // After replaying history, connect the line to the touch point.
                    break;

                default:
                    return false;
            }
            // Include half the stroke width to avoid clipping.
            invalidate();
            lastTouchX = eventX;
            lastTouchY = eventY;

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    //start new drawing
    public void startNew() {
        try {
            if(path != null)
            {
                clear();
                paths = new ArrayList<Path>();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
