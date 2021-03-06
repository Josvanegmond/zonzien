package nl.sterrenkunde.zonno.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import nl.sterrenkunde.zonno.R;

/**
 * Created by mint on 28-11-15.
 */
public class TextView extends android.widget.TextView {

    private Typeface _customTypeface;

    public TextView(Context context) {
        super(context);
        _init();
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init(attrs);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        _init(attrs);
    }

    private void _init() {
        _init(null);
    }

    private void _init(AttributeSet attrs) {
        if (_customTypeface == null) {
            if (attrs != null) {
                String customTypefacePath = "SinkinSans-200XLight.ttf";
                TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.nl_sterrenkunde_zonno_widget_TextView);
                if (typedArray != null) {
                    String findCustomTypeFace = typedArray.getString(R.styleable.nl_sterrenkunde_zonno_widget_TextView_typeface);
                    if (findCustomTypeFace != null) {
                        customTypefacePath = findCustomTypeFace;
                    }
                    typedArray.recycle();
                }

                AssetManager assets = getContext().getAssets();
                _customTypeface = Typeface.createFromAsset(assets, customTypefacePath);
                setTypeface(_customTypeface);
                setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
            }
        } else {
            setTypeface(_customTypeface);
        }
    }
}
