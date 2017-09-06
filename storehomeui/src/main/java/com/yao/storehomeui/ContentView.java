package com.yao.storehomeui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;


/**
 * Created by Administrator on 2017/7/27.
 */

public class ContentView extends RelativeLayout {
    public ContentView(Context context) {
        super(context);
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private TopContentView topView;

    protected int topPadding;

    public void bindViews(TopContentView topView, BackScrollView scrollView) {
        this.topView = topView;

        if (this.topPadding == 0)
            this.topPadding = getPaddingTop();
        topView.bindViews(this, scrollView);
        scrollView.bindViews(this, topView);
    }

    public void bindViews(TopContentView topView, BackScrollView scrollView, int topPadding) {
        this.topPadding = topPadding;
        bindViews(topView, scrollView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (topView.animScroll)
            return false;
        return super.dispatchTouchEvent(ev);
    }
}
