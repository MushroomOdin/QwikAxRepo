package cmps121.qwikax.AdaptersAndStuff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by andrew on 11/2/2017.
 */

public class DrawingView extends View {
    private float mX, mY;
    private Bitmap _bitmap;
    private Canvas _canvas;
    private Path _path;

    private Paint _bitmapPaint;
    private Paint _circlePaint;
    private Path _circlePath;
    private Paint _paint;

    private static final String CHECK = "MyActivity";
    private static final float TOUCH_TOLERANCE = 4;

    public DrawingView(Context c, AttributeSet set) {
        super(c, set);
        Initialize();
    }

    // Initializes the drawing view and what we draw to it.
    public void Initialize(){
        _path = new Path();
        _bitmapPaint = new Paint(Paint.DITHER_FLAG);
        _circlePaint = new Paint();
        _circlePath = new Path();
        _circlePaint.setAntiAlias(true);
        _circlePaint.setColor(Color.GREEN);
        _circlePaint.setStyle(Paint.Style.STROKE);
        _circlePaint.setStrokeJoin(Paint.Join.MITER);
        _circlePaint.setStrokeWidth(4f);

        _paint = new Paint();
        _paint.setAntiAlias(true);
        _paint.setDither(true);
        _paint.setColor(Color.BLUE);
        _paint.setStyle(Paint.Style.STROKE);
        _paint.setStrokeJoin(Paint.Join.ROUND);
        _paint.setStrokeCap(Paint.Cap.ROUND);
        _paint.setStrokeWidth(10);
    }

    public void ClearCanvas(){
        _bitmap.eraseColor(Color.TRANSPARENT);
        _canvas = new Canvas(_bitmap);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        _bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        _canvas = new Canvas(_bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);
        int numColumns = 20;
        int numRows = 20;
        if (numColumns == 0 || numRows == 0) {
            return;
        }

        // TODO: Remove this, Used for coding purposes.
        int width = getWidth();
        int height = getHeight();
        int cellWidth = width / numColumns;
        int cellHeight = height / numRows;

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, _paint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, _paint);
        }

        canvas.drawBitmap(_bitmap, 0, 0, _bitmapPaint);
        canvas.drawPath(_path, _paint);
        canvas.drawPath(_circlePath, _circlePaint);
    }

    public void touch_start(float x, float y) {
        _path.reset();
        _path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    public void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            _path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            _circlePath.reset();
            _circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    public void touch_up() {
        _path.lineTo(mX, mY);
        _circlePath.reset();
        _canvas.drawPath(_path, _paint);
        Log.i(CHECK, "check: " + _canvas.getWidth());
        _path.reset();
    }


    // GETTERS AND SETTERS

    public Paint get_paint(){return _paint;}
    public void set_paint(Paint paint){_paint = paint;}

    // GETTERS AND SETTERS
}
