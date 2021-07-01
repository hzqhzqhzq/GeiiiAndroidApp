package com.sheiii.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class MyViewPagerr extends ViewPager {
    /**
     * Constructor
     *
     * @param context
     *            the context
     */
    public MyViewPagerr(Context context) {
        super(context);
    }
    /**
     * Constructor
     *
     * @param context
     *            the context
     * @param attrs
     *            the attribute set
     */
    public MyViewPagerr(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
//            if (h > height)
//                height = h * 2 / 3;
            height = h;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}