package com.larunda.recycler;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sddt on 18-3-28.
 */

public class PullToRefreshRecyclerView extends HeaderAndFooterRecyclerView {

    //    当前状态
    private int mState = STATE_DEFAULT;
    //    初始
    public final static int STATE_DEFAULT = 0;
    //    正在下拉
    public final static int STATE_PULLING = 1;
    //    松手刷新
    public final static int STATE_RELEASE_TO_REFRESH = 2;
    //    刷新中
    public final static int STATE_REFRESHING = 3;

    //    下拉阻尼系数
    private float mPullRatio = 0.5f;

    //    辅助头部
    private View topView;

    //    刷新头部
    private View mRefreshView;
    //    刷新头部的高度
    private int mRefreshViewHeight = 0;

    //    触摸事件辅助，当RecyclerView滑动到顶部时，记录触摸事件的y轴坐标
    private float mFirstY = 0;
    //    当前是否正在下拉
    private boolean mPulling = false;

    //    是否可以下拉刷新
    private boolean mRefreshEnable = true;

    //    回弹动画
    private ValueAnimator valueAnimator;

    //    刷新监听
    private OnRefreshListener mOnRefreshListener;

    //    刷新头部构造器
    private RefreshHeaderCreator mRefreshHeaderCreator;

    public PullToRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public PullToRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (topView == null) {
            topView = new View(context);
//        该view的高度不能为0，否则将无法判断是否已滑动到顶部
            topView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
//        设置默认LayoutManager
            setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//        初始化默认的刷新头部
            mRefreshHeaderCreator = new DefaultRefreshHeaderCreator();
            mRefreshView = mRefreshHeaderCreator.getRefreshView(context, this);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (mRefreshView != null) {
            addHeaderView(topView);
            addHeaderView(mRefreshView);
        }
        if (mRefreshView != null && mRefreshViewHeight == 0) {
            mRefreshView.measure(0, 0);
            mRefreshViewHeight = mRefreshView.getLayoutParams().height;
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
            marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin - mRefreshViewHeight - 1, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
            setLayoutParams(marginLayoutParams);
        }
    }


    /**
     * 在measure的时候，隐藏刷新头部
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
//    若是不可以下拉
        if (!mRefreshEnable) return super.onTouchEvent(e);
//    若刷新头部为空，不处理
        if (mRefreshView == null)
            return super.onTouchEvent(e);
//    若回弹动画正在进行，不处理
        if (valueAnimator != null && valueAnimator.isRunning())
            return super.onTouchEvent(e);

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!mPulling) {
                    if (isTop()) {
//                    当listview滑动到最顶部时，记录当前y坐标
                        mFirstY = e.getRawY();
                    }
//                若listview没有滑动到最顶部，不处理
                    else
                        break;
                }
                float distance = (int) ((e.getRawY() - mFirstY) * mPullRatio);
//            若向上滑动(此时刷新头部已隐藏)，不处理
                if (distance < 0) break;
                mPulling = true;
//            若刷新中，距离需加上头部的高度
                if (mState == STATE_REFRESHING) {
                    distance += mRefreshViewHeight;
                }
//            下拉
                setState(distance);
                return true;
            case MotionEvent.ACTION_UP:
//            回弹
                replyPull();
                break;
        }
        return super.onTouchEvent(e);
    }

    private boolean isTop() {
        return !ViewCompat.canScrollVertically(this, -1);
    }

    private void setState(float distance) {
//    刷新中，状态不变
        if (mState == STATE_REFRESHING) {
        } else if (distance == 0) {
            mState = STATE_DEFAULT;
        }
//    松手刷新
        else if (distance >= mRefreshViewHeight) {
            int lastState = mState;
            mState = STATE_RELEASE_TO_REFRESH;
            if (mRefreshHeaderCreator != null)
                if (!mRefreshHeaderCreator.onReleaseToRefresh(distance, lastState))
                    return;
        }
//    正在拖动
        else if (distance < mRefreshViewHeight) {
            int lastState = mState;
            mState = STATE_PULLING;
            if (mRefreshHeaderCreator != null)
                if (!mRefreshHeaderCreator.onStartPull(distance, lastState))
                    return;
        }
//    开始下拉
        startPull(distance);
    }

    private void startPull(float distance) {
//        辅助头部的高度不能为0，否则将无法判断是否已滑动到顶部
        if (distance < 1)
            distance = 1;
        if (topView != null) {
            LayoutParams layoutParams = (LayoutParams) topView.getLayoutParams();
            layoutParams.height = (int) distance;
            topView.setLayoutParams(layoutParams);
        }
    }

    //松手回弹
    private void replyPull() {
        mPulling = false;
//    回弹位置
        float destinationY = 0;
//    判断当前状态
//    若是刷新中，回弹
        if (mState == STATE_REFRESHING) {
            destinationY = mRefreshViewHeight;
        }
//    若是松手刷新，刷新，回弹
        else if (mState == STATE_RELEASE_TO_REFRESH) {
//        改变状态
            mState = STATE_REFRESHING;
//        刷新
            if (mRefreshHeaderCreator != null)
                mRefreshHeaderCreator.onStartRefreshing();
            if (mOnRefreshListener != null)
                mOnRefreshListener.onStartRefreshing();
//        若在onStartRefreshing中调用了completeRefresh方法，将不会滚回初始位置，因此这里需加个判断
            if (mState != STATE_REFRESHING) return;
            destinationY = mRefreshViewHeight;
        } else if (mState == STATE_DEFAULT || mState == STATE_PULLING) {
            mState = STATE_DEFAULT;
        }

        LayoutParams layoutParams = (RecyclerView.LayoutParams) topView.getLayoutParams();
        float distance = layoutParams.height;
        if (distance <= 0) return;

        valueAnimator = ObjectAnimator.ofFloat(distance, destinationY).setDuration((long) (distance * 0.5));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float nowDistance = (float) animation.getAnimatedValue();
                startPull(nowDistance);
            }
        });
        valueAnimator.start();
    }

    public void completeRefresh() {
        if (mRefreshHeaderCreator != null)
            mRefreshHeaderCreator.onStopRefresh();
        mState = STATE_DEFAULT;
        replyPull();
        mAdapter.notifyDataSetChanged();
    }

    public void setRefreshViewCreator(RefreshHeaderCreator refreshHeaderCreator) {
        this.mRefreshHeaderCreator = refreshHeaderCreator;
        mRefreshView = refreshHeaderCreator.getRefreshView(getContext(), this);
//    若有适配器，添加到头部
        if (mAdapter != null) {
            addHeaderView(topView);
            addHeaderView(mRefreshView);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void setmOnRefreshListener(OnRefreshListener mOnRefreshListener) {
        this.mOnRefreshListener = mOnRefreshListener;
    }

    /**
     * 获得刷新View和顶部填充view的个数，用于绘制分割线
     */
    public int getRefreshViewCount() {
        if (mRefreshView != null)
            return 2;
        return 0;
    }

    /**
     * 设置是否可以下拉
     */
    public void setRefreshEnable(boolean refreshEnable) {
        this.mRefreshEnable = refreshEnable;
    }

    /**
     * 设置下拉阻尼系数
     */
    public void setPullRatio(float pullRatio) {
        this.mPullRatio = pullRatio;
    }

    /**
     * 获得真正的adapter
     */
    @Override
    public Adapter getRealAdapter() {
        return mRealAdapter;
    }
}
