package com.hxgz.chuantv.widget.ImageCardView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Leanback CardView 控件.
 * Created by hailongqiu on 2016/8/26.
 */
public class OpenCardView extends FrameLayout implements Target {

    private final Rect mShadowBounds = new Rect();
    private Drawable mDrawableShadow;

    public OpenCardView(Context context) {
        this(context, null);
    }

    public OpenCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OpenCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setShadowDrawable(Drawable drawable) {
        this.mDrawableShadow = drawable;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawableShadow != null) {
            Rect padding = new Rect();
            mDrawableShadow.getPadding(padding);
            mDrawableShadow.setBounds(-padding.left, -padding.top, padding.right + canvas.getWidth(), padding.bottom + canvas.getHeight());
            mDrawableShadow.draw(canvas);
        }
        super.onDraw(canvas);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        //Set your error drawable
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        //Set your placeholder
    }
}
