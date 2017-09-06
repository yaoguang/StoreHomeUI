package com.yao.storehomeui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.yao.storehomeui.utils.ClassUtil;

/**
 * Created by Administrator on 2017/7/27.
 */

public class TopContentView extends RelativeLayout {
    public TopContentView(Context context) {
        super(context);
        init();
    }

    public TopContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private float density = -1F;
    private int screenHeight;
    public int bottomHeight = 45;
    public int bottomTopY;
    private int topGoneHeight, topVisibleHeight, bottomDownGoneHeight, bottomUpVisibleHeight, maxAnimDistance;
    private float topGonePercent = 0.75f, topVisiblePercent = 0.25f, bottomDownGonePercent = 0.15f, bottomUpVisiblePercent = 0.85f;
    protected int topMarginDefault;
    protected int marginTop;
    protected int toMargin;
    private ObjectAnimator scrollAnimator;
    private int marginState = 0;
    private int topPadding;

    protected float mActionDown;
    protected float mActionY;
    protected float mActionMoveDistance;

    private ContentView contentView;
    private BackScrollView backScrollView;
    private LayoutParams lp;

    private boolean scrollBack = false;

    protected boolean animScroll = false;
    private int DEFAULT_ANIM_MOVE_TIME = 300;
    private int DEFAULT_MIN_ANIM_MOVE_TIME = 100;
    private float lastInterceptY = -1;
    private int mTouchSlop;
    private float[] mActionDownXY = new float[2];
    protected boolean isVirtualClick = false;
    protected boolean moveState = false;
    private int mineMoveDistance;
    protected boolean isChildThrowEvent = false;

    private VisibleStateChangeListener mStateChange;
    private ScrollChangeListener mScrollChange;

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mineMoveDistance = (int) (mTouchSlop * 0.5f);
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        density = getResources().getDisplayMetrics().density;
        bottomHeight = dpToPx(bottomHeight);
        initAnimScroll();
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    public int dpToPx(int dpValue) {
        return (int) (dpValue * density + 0.5F);
    }

    public void refresh(int marginTop) {
        topPadding = contentView.topPadding + 1;
        if (this.marginTop == topMarginDefault) {
            this.marginTop = marginTop;
            setY(marginTop + topPadding);
        }
        topMarginDefault = marginTop;
        {
            bottomTopY = getHeight() - bottomHeight;
            topGoneHeight = (int) (topMarginDefault * topGonePercent);
            topVisibleHeight = (int) (topMarginDefault * topVisiblePercent);
            bottomDownGoneHeight = (int) (topMarginDefault + (screenHeight - topMarginDefault - bottomHeight) * bottomDownGonePercent);
            bottomUpVisibleHeight = (int) (topMarginDefault + (screenHeight - topMarginDefault - bottomHeight) * bottomUpVisiblePercent);
            maxAnimDistance = topMarginDefault - bottomTopY;
        }
    }

    public void refreshScreenHeight(int screenHeight) {
        if (this.screenHeight != screenHeight) {
            this.screenHeight = screenHeight;
            if (marginTop == bottomTopY || toMargin == bottomTopY) {
                if (animScroll)
                    toMargin = getHeight() - bottomHeight;
                else
                    setMarginTop(getHeight() - bottomHeight);
            }
            bottomTopY = getHeight() - bottomHeight;
            bottomDownGoneHeight = (int) (topMarginDefault + (screenHeight - topMarginDefault - bottomHeight) * bottomDownGonePercent);
            bottomUpVisibleHeight = (int) (topMarginDefault + (screenHeight - topMarginDefault - bottomHeight) * bottomUpVisiblePercent);
            maxAnimDistance = topMarginDefault - bottomTopY;
        }
    }

    public void bindViews(ContentView contentView, BackScrollView scrollView) {
        this.contentView = contentView;
        this.backScrollView = scrollView;
    }

    public void bindChildViews(ScrollRightListView listView, GoodsRecyclerView recyclerView, MenuGridView menuView) {
        listView.setScrollParent(this);
        recyclerView.bindViews(this, menuView);
        menuView.bindViews(this, recyclerView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionMoveDistance = 0;
                intercept = false;
                mActionDownXY = new float[]{ev.getX(), ev.getY()};
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        boolean a = super.dispatchTouchEvent(ev);
        return a;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionDown = mActionY = y;
                moveState = false;
                mActionMoveDistance = 0;
                mActionDownXY = new float[]{event.getX(), event.getY()};
                return true;
            case MotionEvent.ACTION_MOVE: {
                float moveX = event.getX() - mActionDownXY[0];
                float moveY = event.getY() - mActionDownXY[1];
                float distance = (float) Math.sqrt(moveX * moveX + moveY * moveY);

                mActionMoveDistance = Math.max(mActionMoveDistance, distance);
                if (mActionMoveDistance < mineMoveDistance)
                    moveState = false;
                else {
                    moveState = true;

                    if (isChildThrowEvent) {
                        isChildThrowEvent = false;
                        if (mActionY < y) {
                            mActionY = Math.max(y - mineMoveDistance, mActionY);
                        } else {
                            mActionY = Math.min(y + mineMoveDistance, mActionY);
                        }
                    }

                    marginTop = (int) (marginTop + (y - mActionY));

                    marginTop = Math.min(marginTop, bottomTopY);
                    marginTop = Math.max(0, marginTop);

                    if (marginTop <= 0) {
                        isVirtualClick = true;
                        event.setAction(MotionEvent.ACTION_DOWN);
                        intercept = false;
                        super.dispatchTouchEvent(event);
                    }

                    setMarginTop(marginTop);
                    mActionY = y;
                }
            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (event.getAction() == MotionEvent.ACTION_UP && mActionMoveDistance < mTouchSlop && !isVirtualClick && !moveState && marginState != 2) {
                    isVirtualClick = true;
                    requestDisallowInterceptTouchEvent(true);
                    MotionEvent eventDown = MotionEvent.obtain(event.getDownTime(), event.getDownTime(), MotionEvent.ACTION_DOWN, mActionDownXY[0], mActionDownXY[1], event.getMetaState());
                    MotionEvent eventUp = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_UP, event.getX(), event.getY(), event.getMetaState());
                    super.dispatchTouchEvent(eventDown);
                    super.dispatchTouchEvent(eventUp);
                }

                isVirtualClick = false;
                animScrollMargin();
            }
            break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean a = super.onInterceptTouchEvent(ev);
        float y = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isVirtualClick)
                    return false;
                mActionDown = y;
                if (marginTop > topMarginDefault + 1)
                    return true;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        if (intercept)
            a = true;
        lastInterceptY = y;
        return a;
    }

    private boolean intercept = false;

    public void setIntercept(boolean intercept) {
        this.intercept = intercept;
    }

    public void setMarginTop(int marginTop) {
        if (marginTop < topMarginDefault) {
            float alpha = (topMarginDefault - marginTop) / (topMarginDefault * 1.0f);
            alpha = Math.max(0, 1 - alpha);
            backScrollView.changeAlpha(alpha);
        } else {
            backScrollView.changeAlpha(1);
        }

        if (marginState == 2 && scrollBack) {
            float animValue = Math.abs((marginTop - toMargin) / (this.marginTop + 0.5f - toMargin));
            backScrollView.backToTop(animValue);
        }
        setY(marginTop + topPadding);
        if (mScrollChange != null)
            mScrollChange.onScrollChange(marginTop + topPadding, toMargin + topPadding);
    }

    public int getMarginTop() {
        return marginTop;
    }

    private void initAnimScroll() {
        scrollAnimator = ObjectAnimator.ofInt(TopContentView.this, "marginTop", 0, 0);
        scrollAnimator.setDuration(DEFAULT_ANIM_MOVE_TIME);
        scrollAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scrollAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animOver();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animOver();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void animOver() {
        marginTop = toMargin;
        animScroll = false;

        if (Math.abs(marginTop - bottomTopY) < 10) {
            if (marginState != 2 && mStateChange != null)
                mStateChange.onVisibleStateChange(2);
            marginState = 2;
        } else if (Math.abs(marginTop - topMarginDefault) < 10) {
            if (marginState != 0 && mStateChange != null)
                mStateChange.onVisibleStateChange(0);
            marginState = 0;
        } else if (Math.abs(marginTop) < 10) {
            if (marginState != 1 && mStateChange != null)
                mStateChange.onVisibleStateChange(1);
            marginState = 1;
        }

        setMarginTop(marginTop);
    }

    public void animToState(int state) {
        if (marginState != state && mStateChange != null)
            mStateChange.onVisibleStateChange(state);
        marginState = state;
        switch (state) {
            case 0:
                toMargin = topMarginDefault;
                break;
            case 1:
                toMargin = 0;
                break;
            case 2:
                toMargin = bottomTopY;
                break;
        }
        animScroll();
    }

    public int getMarginState() {
        return marginState;
    }

    public boolean isBottomState() {
        return marginState == 2;
    }

    public boolean isDefaultState() {
        return marginState == 0;
    }

    public void animScrollMargin() {
        if (marginState == 1) {
            if (marginTop < topVisibleHeight)
                toMargin = 0;
            else toMargin = topMarginDefault;
        } else if (marginState == 0) {
            if (marginTop < topGoneHeight)
                toMargin = 0;
            else if (marginTop >= bottomDownGoneHeight)
                toMargin = bottomTopY;
            else
                toMargin = topMarginDefault;
        } else if (marginState == 2)
            if (marginTop > bottomUpVisibleHeight)
                toMargin = bottomTopY;
            else
                toMargin = topMarginDefault;
        else
            toMargin = bottomTopY;

        animScroll();
    }

    private void animScroll() {
        animScroll = true;
        scrollBack = backScrollView.canScrollVertically(-1);

        scrollAnimator.setIntValues(marginTop, toMargin);
        scrollAnimator.setDuration((long) (DEFAULT_MIN_ANIM_MOVE_TIME + Math.abs((marginTop - toMargin + 0.1) / maxAnimDistance * (DEFAULT_ANIM_MOVE_TIME - DEFAULT_MIN_ANIM_MOVE_TIME))));
        scrollAnimator.start();
    }

    protected void resetTouchStateMine() {
        int mGroupFlags = ClassUtil.getPrivateParameter(this, "mGroupFlags");
        mGroupFlags &= ~0x80000;
        ClassUtil.setPrivateParameter(this, "mGroupFlags", mGroupFlags);
        ClassUtil.setPrivateParameter(this, "mFirstTouchTarget", null);
    }

    public void setOnVisibleStateChangeListener(VisibleStateChangeListener stateChange) {
        this.mStateChange = stateChange;
    }

    public void setOnScrollChange(ScrollChangeListener mScrollChange) {
        this.mScrollChange = mScrollChange;
    }

    public interface VisibleStateChangeListener {
        void onVisibleStateChange(int state);
    }

    public interface ScrollChangeListener {
        void onScrollChange(int marginTop, int toMargin);
    }
}
