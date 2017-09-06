package com.yao.storehomeui;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017/7/27.
 */

public class BackScrollView extends ScrollView {
    public BackScrollView(Context context) {
        super(context);
        init();
    }

    public BackScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        density = getResources().getDisplayMetrics().density;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public int dpToPx(int dpValue) {
        return (int) (dpValue * density + 0.5F);
    }

    private float density = -1F;
    private int screenHeight;
    private int mTouchSlop;

    private TopContentView topView;
    private ContentView contentView;
    private int mActionDown;
    private View headChildView;

    private int scrollTop;

    private int backAnimStartY = 0;

    public void bindViews(ContentView contentView, TopContentView topView) {
        this.topView = topView;
        this.contentView = contentView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActionDown = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getRawY();
                if (Math.abs(moveY - mActionDown) > mTouchSlop && topView.isDefaultState()) {
                    return true;
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        scrollTop = t;
    }

    public void changeAlpha(float alpha) {
        if (headChildView == null)
            return;
        if (headChildView.getAlpha() != alpha)
            headChildView.setAlpha(alpha);
    }

    public void bindChildView(@IdRes int id) {
        headChildView = findViewById(id);
    }

    public void backToTop(float value) {
        if (backAnimStartY == 0) {
            backAnimStartY = scrollTop;
        }
        smoothScrollTo(0, (int) (backAnimStartY * value));
        System.out.println((int) (backAnimStartY * value));
        if (value == 0)
            backAnimStartY = 0;
    }
}
