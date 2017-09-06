package com.yao.storehomeui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.GridView;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/8/16.
 */
public class MenuGridView extends GridView {
    public MenuGridView(Context context) {
        super(context);
        init();
    }

    public MenuGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MenuGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private float mLastY = 0;
    private TopContentView myScrollParent;
    private GoodsRecyclerView goodsRecycler;
    private int mTouchSlop;

    private ObjectAnimator scrollAnim;

    private int measureHeight;
    private int menuBtnHeight;
    private float minY;

    RelativeLayout.LayoutParams lp;
    private float scrollAnimValue;
    protected boolean animScroll = false;
    private onScrollAnimListener listener;

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        initAnim();
    }

    private void initAnim() {
        scrollAnim = ObjectAnimator.ofFloat(this, "Y", 0, 0);
        scrollAnim.setDuration(300);
        scrollAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scrollAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                int offset = (int) (value - scrollAnimValue);
                goodsRecycler.animScrollBy(-offset);
                scrollAnimValue = value;
            }
        });
        scrollAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animScroll = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animScroll = false;
                if (listener != null)
                    listener.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animScroll = false;
                if (listener != null)
                    listener.onAnimationEnd();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

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

                if (y - mLastY > 0 && !isMenuShow())
                    animVisible();
                else if (y - mLastY < 0 && isMenuShow())
                    animGone();
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
                    if (getY() < 0) {
                        setY(getY() + deltaY);
                        a = true;
                    } else {
                        myScrollParent.mActionY = ev.getRawY();

                        myScrollParent.isChildThrowEvent = true;
                        myScrollParent.resetTouchStateMine();
                        myScrollParent.setIntercept(true);
                    }
                } else if (deltaY < -0.5 && myScrollParent.marginTop <= 0) {
                    goodsRecycler.animScrollBy(-(int) deltaY);
                    a = true;
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

    public void bindViews(TopContentView myScrollParent, GoodsRecyclerView goodsRecycler) {
        this.myScrollParent = myScrollParent;
        this.goodsRecycler = goodsRecycler;
    }

    public void animVisible() {
        if (goodsRecycler.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
            goodsRecycler.showMenuAfterScroll = true;
            return;
        }
        if (goodsRecycler.isShowByRecycler())
            return;
        scrollAnimValue = getY();
        scrollAnim.setFloatValues(scrollAnimValue, measureHeight + minY);
        scrollAnim.start();
    }

    public void animGone() {
        if (!isMenuShow() && goodsRecycler.isMenuShow()) {
            goodsRecycler.animScrollTo(measureHeight);
            return;
        }
        scrollAnimValue = getY();
        scrollAnim.setFloatValues(scrollAnimValue, minY);
        scrollAnim.start();
    }

    public int getMeasureHeight() {
        return measureHeight;
    }

    public int getInVisibleHeight() {
        return (int) (getY() - menuBtnHeight);
    }

    public void setMeasureHeight(int measureHeight, int menuBtnHeight) {
        this.measureHeight = measureHeight;
        this.menuBtnHeight = menuBtnHeight;
        if (lp == null)
            lp = (RelativeLayout.LayoutParams) getLayoutParams();
        lp.height = measureHeight;
        lp.topMargin = -measureHeight;
        requestLayout();
        minY = menuBtnHeight - measureHeight;
    }

    public void setGoneTranslation(float translation) {
        float y = getY();
        y -= Math.abs(translation);
        y = Math.max(minY, y);
        setY(y);
    }

    public boolean isMenuShow() {
        return getY() > minY + 0.5;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public void setListener(onScrollAnimListener listener) {
        this.listener = listener;
    }

    public interface onScrollAnimListener {
        void onAnimationEnd();
    }
}
