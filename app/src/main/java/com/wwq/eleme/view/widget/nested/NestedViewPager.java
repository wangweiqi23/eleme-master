package com.wwq.eleme.view.widget.nested;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.Arrays;

/**
 * Created by alexwangweiqi on 18/3/30.
 */

public class NestedViewPager extends ViewPager implements NestedScrollingChild2 {

    public static final String TAG = NestedViewPager.class.getSimpleName();

    /**
     * 业务逻辑
     */
    private final int MENU_PAGE = 0;
    public static volatile boolean sIsMenuExpand = true;

    private NestedScrollingChildHelper mChildHelper;

    private boolean mIsScrollVertical = false;//默认水平滑动
    private boolean mIsBeingDragged;

    private VelocityTracker mVelocityTracker;

    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mNestedYOffset;

    /**
     * Position of the last motion event.
     */
    private int mLastMotionX;
    private int mLastMotionY;
    private int mInitialMotionX;
    private int mInitialMotionY;

    private int mTouchSlop;

    private int mMiniTopHeight;

    public NestedViewPager(Context context) {
        super(context);
        init();
    }

    public NestedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mChildHelper = new NestedScrollingChildHelper(this);

        setNestedScrollingEnabled(true);

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setViewHeight(int miniTopHeight) {
        this.mMiniTopHeight = miniTopHeight;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /**
     * 上滑时 当appbarLayout非最小状态  优先处理appbarLayout
     * 下滑时 优先判断子view是否有滑动需求
     * @return
     */
    private boolean isInterceptNested(int dy, int x, int y) {
        if (dy < 0) {//上滑
            return getY() > mMiniTopHeight;
        } else {
            if(getCurrentItem() == MENU_PAGE){
                return sIsMenuExpand && !canScroll(this, false, dy, x, y);
            }else{
                return !canScroll(this, false, dy, x, y);
            }
        }
    }

    protected boolean canScroll(View v, boolean checkV, int dy, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        canScroll(child, true, dy, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV && ViewCompat.canScrollVertically(v, -dy);
    }


    protected boolean canScrollHorizontal(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight()
                        && y + scrollY >= child.getTop() && y + scrollY < child.getBottom()
                        && canScrollHorizontal(child, true, dx, x + scrollX - child.getLeft(),
                        y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        boolean intercept = false;

        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }

        boolean superIntercept = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = mInitialMotionX = (int)ev.getX();
                mLastMotionY = mInitialMotionY = (int)ev.getY();
                intercept = mIsBeingDragged = false;
                mIsScrollVertical =false;
                superIntercept = super.onInterceptTouchEvent(ev);
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                Log.v(TAG, "NestedViewPager onInterceptTouchEvent ACTION_DOWN " +
                        "mLastMotionX:" + mLastMotionX + " mLastMotionY:" + mLastMotionY);
                break;
            case MotionEvent.ACTION_MOVE: {
                final int x = (int)ev.getX();
                final int dx = x - mLastMotionX;
                final int xDiff = Math.abs(dx);
                final int y = (int) ev.getY();
                final int dy = y - mInitialMotionY;
                final int yDiff = Math.abs(dy);
                if (yDiff > mTouchSlop && yDiff * 0.5f > xDiff && isInterceptNested(dy, x, y)) {
//                    if (!mIsBeingDragged) {
////                        mLastMotionY = y;
////                        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
//                        mIsBeingDragged = true;
//                    }
                    mLastMotionY = y;
                    intercept = true;
                    mIsScrollVertical = true;
                    mNestedYOffset = 0;
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                } else if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff && !canScrollHorizontal
                        (this, false, dx, x, y)) {
                    intercept = true;
                    mIsScrollVertical = false;
                    superIntercept = super.onInterceptTouchEvent(ev);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                endDrag();
                intercept = false;
                superIntercept = super.onInterceptTouchEvent(ev);
                break;
        }
        Log.v(TAG, "NestedViewPager onInterceptTouchEvent intercept:"
                + intercept + " super:" + superIntercept + " mIsScrollVertical:" +
                mIsScrollVertical);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!mIsScrollVertical) {
            return super.onTouchEvent(ev);
        }

        initVelocityTrackerIfNotExists();

        final int action = ev.getActionMasked();

        MotionEvent vtev = MotionEvent.obtain(ev);

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }
        vtev.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "NestedViewPager onTouchEvent ACTION_DOWN mIsBeingDragged:"+mIsBeingDragged);
                mLastMotionX = mInitialMotionX = (int)ev.getX();
                mLastMotionY = mInitialMotionY = (int)ev.getY();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;
            case MotionEvent.ACTION_MOVE:
                final int y = (int) ev.getY();
                int deltaY = mLastMotionY - y;

                Log.i(TAG, "=1= NestedViewPager onTouchEvent ACTION_MOVE " +
                        "mLastMotionY:" + mLastMotionY + " y:" + ev.getY() + " deltaY:" + deltaY
                        + " mIsBeingDragged:" + mIsBeingDragged + " Y:" + getY()
                        + " mScrollConsumed:" + Arrays.toString(mScrollConsumed)
                        + " mScrollOffset:" + Arrays.toString(mScrollOffset)
                        + " mNestedYOffset:" + mNestedYOffset);

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset, ViewCompat.TYPE_TOUCH)) {
                    deltaY -= mScrollConsumed[1];
                    vtev.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                if (mIsBeingDragged) {
                    mLastMotionY = y - mScrollOffset[1];

                    if (dispatchNestedScroll(0, 0, 0, deltaY, mScrollOffset, ViewCompat.TYPE_TOUCH)) {
                        mLastMotionY -= mScrollOffset[1];
                        vtev.offsetLocation(0, mScrollOffset[1]);
                        mNestedYOffset += mScrollOffset[1];
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                endDrag();
                break;
        }

        vtev.recycle();
//        return mIsBeingDragged || super.onTouchEvent(ev);
        return true;
    }

    private void endDrag() {
        mIsBeingDragged = false;

        recycleVelocityTracker();
        stopNestedScroll(ViewCompat.TYPE_TOUCH);

    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int
            dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable
            int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow,
                type);
    }

}
