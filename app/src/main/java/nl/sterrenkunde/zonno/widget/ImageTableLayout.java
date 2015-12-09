package nl.sterrenkunde.zonno.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nl.sterrenkunde.zonno.R;

/**
 * Created by mint on 30-11-15.
 */
public class ImageTableLayout extends LinearLayout {
    private static final String TAG = ImageTableLayout.class.getName();

    private NinePatchDrawable[][] _drawableMatrix;
    private int[][] _colorGrid;

    public ImageTableLayout(Context context) {
        super(context);
        _drawableMatrix = new NinePatchDrawable[3][8];
        _colorGrid = new int[3][8];
    }

    public ImageTableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        _drawableMatrix = new NinePatchDrawable[3][8];
        _colorGrid = new int[3][8];
    }

    public void setColor(int x, int y, int color) {
        _colorGrid[y][x] = color;
    }

    public void setImage(int x, int y, Drawable drawable) {
        if (drawable != null) {
            _drawableMatrix[y][x] = (NinePatchDrawable)drawable;
        }
    }

    public void update() {
        LinearLayout[] layers = new LinearLayout[]{
                (LinearLayout) findViewById(R.id.highLayer),
                (LinearLayout) findViewById(R.id.lowLayer),
                (LinearLayout) findViewById(R.id.horizonLayer)
        };

        for (int y = 0; y < layers.length; y++) {
            LinearLayout layer = layers[y];
            for (int x = 0; x < layer.getChildCount(); x++) {
                View child = layer.getChildAt(x);
                if (child instanceof FrameLayout) {
                    child.setBackgroundColor(_colorGrid[y][x]);
                    ImageView imageView = (ImageView) child.findViewById(R.id.cloudImage);
                    imageView.setImageDrawable(_drawableMatrix[y][x]);
                }
            }
        }

        super.invalidate();
    }

}
