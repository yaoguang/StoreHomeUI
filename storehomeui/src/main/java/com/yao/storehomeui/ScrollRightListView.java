package com.yao.storehomeui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/8/16.
 */
public class ScrollRightListView extends ListView {
    public ScrollRightListView(Context context) {
        super(context);
        init();
    }

    public ScrollRightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollRightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollRightListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private float mLastY = 0;
    private TopContentView myScrollParent;
    private int mTouchSlop;

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setScrollParent(TopContentView scrollView) {
        this.myScrollParent = scrollView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean a;
        float y = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                a = super.dispatchTouchEvent(ev);
                myScrollParent.mActionY = ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (myScrollParent.isVirtualClick) {
                    if (myScrollParent.mActionMoveDistance < mTouchSlop) {
                        ev.setAction(MotionEvent.ACTION_UP);
                        a = super.dispatchTouchEvent(ev);
                    } else a = super.onTouchEvent(ev);

                    myScrollParent.isVirtualClick = false;
                } else a = super.dispatchTouchEvent(ev);

                if (myScrollParent.marginTop != 0) {
                    myScrollParent.animScrollMargin();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (myScrollParent == null)
                    throw new NullPointerException("myScrollParent is null,You Forget setScrollParent");
                float deltaY = y - mLastY;
                boolean canMoveUp = canScrollVertically(-1);
                if (deltaY >= 0 && canMoveUp) {
                    a = super.dispatchTouchEvent(ev);
                } else if ((deltaY >= 0.5 && !canMoveUp) || (deltaY < -0.5 && myScrollParent.marginTop > 0)) {
                    a = false;

                    myScrollParent.mActionY = ev.getRawY();

                    myScrollParent.isChildThrowEvent = true;
                    myScrollParent.resetTouchStateMine();
                    myScrollParent.setIntercept(true);
                } else
                    a = super.dispatchTouchEvent(ev);
                break;
            default:
                a = super.dispatchTouchEvent(ev);
                break;
        }
        mLastY = y;
        return a;
    }
}
