package com.yao.storehomeui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Administrator on 2017/7/27.
 */

public class GoodsRecyclerView extends RecyclerView {
    public GoodsRecyclerView(Context context) {
        super(context);
        init();
    }

    public GoodsRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GoodsRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private float density = -1F;
    private int screenHeight;
    private int scrollY, scrollToY;

    private ValueAnimator scrollAnimator;
    private boolean animScroll = false;
    private int DEFAULT_ANIM_MOVE_TIME = 300;

    private int scrollDistance;

    private float mLastY = 0;
    private TopContentView myScrollParent;
    private int mTouchSlop;

    private int headHeight;

    private int velocityY;
    private int scrollHeadState;

    private MenuGridView menuView;

    protected boolean showMenuAfterScroll = false;

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        density = getResources().getDisplayMetrics().density;
        initAnimScroll();

        initListener();
    }

    public int dpToPx(int dpValue) {
        return (int) (dpValue * density + 0.5F);
    }

    public void bindViews(TopContentView myScrollParent, MenuGridView menuView) {
        this.myScrollParent = myScrollParent;
        this.menuView = menuView;
    }

    private void initListener() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                getScrollYDistance();
                setScrollDistance(getScrollYDistance() - dy);

                if (menuView.isMenuShow()) {
                    if (menuView.getInVisibleHeight() <= getScrollYDistance())
                        menuView.setGoneTranslation(-headHeight);
                    else
                        menuView.setGoneTranslation(dy);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_SETTLING)
                    scrollHeadState = 2;
                if (newState == SCROLL_STATE_IDLE) {
                    if (scrollHeadState == 2) {
//                        if (velocityY > 0 && Math.abs(scrollDistance) < headHeight) {
//                            animScrollTo(headHeight);
//                        } else if (velocityY < 0 && Math.abs(scrollDistance) < headHeight)
//                            animScrollTo(0);
                    }

                    scrollHeadState = 0;
                    velocityY = 0;

                    if (showMenuAfterScroll) {
                        menuView.animVisible();
                        showMenuAfterScroll = false;
                    } else if (menuView.isMenuShow()) {
                        if (menuView.getInVisibleHeight() <= getScrollYDistance())
                            menuView.setGoneTranslation(-headHeight);
                        else
                            menuView.animGone();
                    }
                }
            }
        });
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

    public void animScrollBy(int offset) {
        scrollBy(0, offset);
    }

    public void animScrollTo(int scrollToY) {
        animScroll = true;

        scrollY = -getScrollYDistance();
        this.scrollToY = scrollToY;

        scrollAnimator.setIntValues(scrollY, scrollToY);
        long duration = (long) (Math.abs(scrollToY - scrollY) / (headHeight + 0.1) * 200 + 100);
        scrollAnimator.setDuration(duration);
        scrollAnimator.start();
    }


    private void initAnimScroll() {
        scrollAnimator = ValueAnimator.ofInt(0, 0);
        scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int nowScrollY = (int) animation.getAnimatedValue();
                if (nowScrollY == scrollToY) {
                    scrollBy(0, scrollToY + getScrollYDistance());
                } else
                    scrollBy(0, nowScrollY - scrollY);
                scrollY = nowScrollY;
            }
        });
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

    public boolean isMenuShow() {
        return getScrollYDistance() > -headHeight;
    }

    public boolean isShowByRecycler() {
        if ((Math.abs(getScrollYDistance()) - 1) <= headHeight) {
            animScrollTo(0);
            return true;
        }
        return false;
    }

    private void animOver() {
        animScroll = false;
    }

    public void setHeadHeight(int headHeight) {
        this.headHeight = headHeight;
    }

    public int getScrollYDistance() {
        if (scrollDistance >= 0)
            return resetDistance();
        return scrollDistance;
    }

    public void changeAnimState() {
        if (animScroll || menuView.animScroll)
            return;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        GoodsRecyclerView.this.velocityY = velocityY;
        return super.fling(velocityX, velocityY);
    }

    public void setScrollDistance(int scrollDistance) {
        this.scrollDistance = scrollDistance;
    }

    public int resetDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        int spanCount = 1;
        if (getLayoutManager() instanceof GridLayoutManager)
            spanCount = ((GridLayoutManager) getLayoutManager()).getSpanCount();
        int position = layoutManager.findFirstVisibleItemPosition();
        if (position < 0) {
            scrollDistance = 0;
            return scrollDistance;
        }
        if (position == 0) {
            View firstVisibleChildView = layoutManager.findViewByPosition(position);
            setScrollDistance(firstVisibleChildView.getTop());
        } else {
            View firstVisibleChildView = layoutManager.findViewByPosition(position);
            int itemHeight = firstVisibleChildView.getHeight();
            int centerItemSize = (position - 1) / spanCount;
            setScrollDistance(firstVisibleChildView.getTop() - headHeight - centerItemSize * itemHeight);
        }
        return scrollDistance;
    }
}
