package cn.com.larunda.safebox.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by sddt on 18-4-9.
 */

public class RightBar extends LinearLayout {
    private int downX;
    private int downY;
    private int scrollOffset;
    private Scroller mScroller;
    private View rightBar;
    private View rightContent;
    private float downX2;
    private float offset;
    private boolean isShow = false;

    public RightBar(Context context) {
        this(context, null);
    }

    public RightBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        rightBar = getChildAt(0);
        rightContent = getChildAt(1);
        rightBar.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        rightContent.layout(getMeasuredWidth(), 0, getMeasuredWidth() + rightContent.getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                downX2 = event.getX();
                /*//当内容布局未显示时不消费点击事件
                if (!isShow) {
                    if (downY2 < rightContent.getMeasuredHeight()) {
                        return false;
                    }
                }*/
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) event.getX();
                int dx = endX - downX;
                int toScroll = getScrollX() - dx;
                if (toScroll < 0) {
                    toScroll = 0;
                } else if (toScroll > rightContent.getMeasuredWidth()) {
                    toScroll = rightContent.getMeasuredWidth();
                }
                scrollTo(toScroll, 0);
                downX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                scrollOffset = getScrollX();
                offset = event.getX() - downX2;
                if (offset > 0) {
                    closeNavigation();
                } else if (offset < 0) {
                    if (scrollOffset > rightContent.getMeasuredWidth() / 4) {
                        showNavigation();
                    } else {
                        closeNavigation();
                    }

                }/* else {
                    if (isShow) {
                        closeNavigation();
                    } else {
                        showNavigation();
                    }
                }*/
                break;
        }

        return true;
    }

    private void showNavigation() {
        int dx = rightContent.getMeasuredWidth() - scrollOffset;
        mScroller.startScroll(getScrollX(), getScrollY(), dx, 0, 100);
        invalidate();
        isShow = true;
    }

    private void closeNavigation() {
        int dx = 0 - scrollOffset;
        mScroller.startScroll(getScrollX(), getScrollY(), dx, 0, 100);
        invalidate();
        isShow = false;
    }

    @Override
    public void computeScroll() {
        //判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

}
