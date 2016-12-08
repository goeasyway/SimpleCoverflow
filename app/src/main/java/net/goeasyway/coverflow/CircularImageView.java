package net.goeasyway.coverflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by lan on 16/12/2.
 */
public class CircularImageView extends ImageView {

    private Paint paint;
    private Paint paintBorder;
    private int width;
    private int height;
    private int canvasSize;
    private float shadowRadius;
    private int shadowColor = 0xFF3f51b5;

    private float borderWidth;

    private Drawable drawable;
    private Bitmap imageBitmap;

    public CircularImageView(Context context) {
        this(context, null);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        float density = context.getResources().getDisplayMetrics().density;
        borderWidth = 4 * density;
        shadowRadius = 8 * density;

        paint = new Paint();
        paint.setAntiAlias(true);

        paintBorder = new Paint();
        paint.setAntiAlias(true);
        paintBorder.setColor(0xFF3f51b5);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        }
        paintBorder.setShadowLayer(shadowRadius, 0, 0, shadowColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        canvasSize = w;
        if (w > h) {
            canvasSize = h;
        }
        if (imageBitmap != null) {
            updateShader();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        loadBitmap();
        if (imageBitmap == null) {
            return;
        }

        //canvas.translate(width / 2, height / 2);
        int radius = (int) ((canvasSize - borderWidth * 2) / 2);
        canvas.drawCircle(radius + borderWidth, radius + borderWidth, radius + borderWidth - (shadowRadius + shadowRadius / 2), paintBorder);
        canvas.drawCircle(radius + borderWidth, radius + borderWidth, radius - (shadowRadius + shadowRadius / 2), paint);
    }

    @Override
    public ScaleType getScaleType() {
        return ScaleType.CENTER_CROP;
    }

    private void loadBitmap() {
        if (drawable == getDrawable()) {
            return;
        }
        drawable = getDrawable();
        imageBitmap = drawableToBitmap(drawable);
        updateShader();
    }

    private void updateShader() {
        if (imageBitmap == null) {
            return;
        }
        imageBitmap = cropBitmap(imageBitmap);
        BitmapShader shader = new BitmapShader(imageBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Matrix matrix = new Matrix();
        matrix.setScale((float) canvasSize / (float) imageBitmap.getWidth(), (float) canvasSize / (float) imageBitmap.getHeight());
        shader.setLocalMatrix(matrix);

        paint.setShader(shader);
    }

    private Bitmap cropBitmap(Bitmap imageBitmap) {
        Bitmap bitmap = null;
        if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(imageBitmap, (imageBitmap.getWidth() / 2) - (imageBitmap.getHeight() / 2),
                    0, imageBitmap.getHeight(), imageBitmap.getHeight());
        } else {
            bitmap = Bitmap.createBitmap(imageBitmap, 0, (imageBitmap.getHeight() / 2) - (imageBitmap.getWidth() / 2),
                    imageBitmap.getWidth(), imageBitmap.getWidth());
        }
        return bitmap;
    }

    private Bitmap drawableToBitmap(Drawable d) {
        if (d == null) {
            return null;
        } else if (d instanceof BitmapDrawable) {
            return ((BitmapDrawable)d).getBitmap();
        }

        int intrinsicWidth = d.getIntrinsicWidth();
        int intrinsicHeight = d.getIntrinsicHeight();

        if (!(intrinsicHeight > 0 && intrinsicWidth > 0)) {
            return null;
        }

        try {
            Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        /*int imageSize = (width < height) ? width : height;
        setMeasuredDimension(imageSize, imageSize);*/
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasSize;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasSize;
        }

        return (result + 2);
    }
    //endregion
}
