package android.support.design.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.wwq.eleme.R;
import com.wwq.eleme.util.DensityUtil;
import com.wwq.eleme.util.ImageUtil;
import com.wwq.eleme.view.widget.indicator.FixedIndicatorView;
import com.wwq.eleme.view.widget.nested.ShopInfoDetailLayout;

import java.util.Arrays;

/**
 * 分为三个状态 滑动 正常 拉伸
 * Created by alexwangweiqi on 18/3/23.
 * nestedViewPager 导致的快速下拉到底 页面到底了但是时机处理没到底的异常 偶现
 */

public class AppBarLayoutScrollBehavior extends AppBarLayout.Behavior {

    public static final String TAG = AppBarLayoutScrollBehavior.class.getSimpleName();

    private OnAppBarListener mListener;

    private boolean mIsBeingDragged = false;
    private boolean mInited = false;

    private CoordinatorLayout mParent;
    private AppBarLayout mAppBarLayout;

    /**
     * title相关
     */
    private View mTitleSearchBtn;
    private View mTitlePindanBtn;
    private View mTitleSearchTextView;
    private ConstraintLayout mTitleLayout;
    private ConstraintLayout mTitleBarLayout;
    private ImageView mTitleBlurFrontLayout;
    private View mTitleBottomView;

    /**
     * 店铺顶部
     */
    private View mShopHeaderView;

    /**
     * 店铺图片
     */
    private View mImageViewShop;
    private View mImageViewShopLike;

    /**
     * 店铺相关信息 折叠展示
     */
    private ShopInfoDetailLayout mShopInfoLayout;

    /**
     * 底部 viewpager导航栏
     */
    private FixedIndicatorView mIndicatorView;

    /**
     * 底部收起按钮
     */
    private ImageView mHeadsUpArrow;

    private int mAppbarLayoutMinOffset;
    private int mAppbarLayoutMaxOffset;
    private int mIndicatorHeight;
    private int mImageViewScaleHeight;
    private int mNormalViewHeight;
    private int mTitleBarHeight;
    private int mShopInfoHeight;

    private int mDpToPx10;
    private int mDpToPx20;
    private int mDpToPx40;
    private int mDpToPx50;
    private int mDpToPx60;
    private int mDpToPx106;

    /**
     * 下拉展示店铺信息
     */
    private int mOffsetSpring;
    private ValueAnimator mSpringRecoverAnimator;

    public AppBarLayoutScrollBehavior(Context context, OnAppBarListener listener) {
        init(context, listener);
    }

    private void init(Context context, OnAppBarListener listener) {
        mListener = listener;

        mDpToPx10 = DensityUtil.dip2px(context, 10);
        mDpToPx20 = DensityUtil.dip2px(context, 20);
        mDpToPx40 = DensityUtil.dip2px(context, 40);
        mDpToPx50 = DensityUtil.dip2px(context, 50);
        mDpToPx60 = DensityUtil.dip2px(context, 60);
        mDpToPx106 = DensityUtil.dip2px(context, 106);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        boolean handled = super.onLayoutChild(parent, abl, layoutDirection);
        if (mInited) {
            return handled;
        }

        initViews(parent, abl);
        initViewsHeight(parent);

        return handled;
    }

    private void initViews(CoordinatorLayout parent, AppBarLayout abl) {
        if (mParent == null) {
            mParent = parent;
        }

        if (mAppBarLayout == null) {
            mAppBarLayout = abl;
        }

        if (mTitleLayout == null) {
            mTitleLayout = parent.findViewById(R.id.title_layout);
            mTitleBarLayout = parent.findViewById(R.id.title_bar_layout);
            mTitleSearchBtn = parent.findViewById(R.id.iv_title_search);
            mTitlePindanBtn = parent.findViewById(R.id.iv_title_pindan);
            mTitleSearchTextView = parent.findViewById(R.id.tv_title_search);
            mTitleBlurFrontLayout = parent.findViewById(R.id.view_shop_info_header_front);
            mTitleBottomView = parent.findViewById(R.id.view_shop_info_header_bottom);

            Resources res = mTitleLayout.getContext().getResources();
            Bitmap bmp = ImageUtil.doBlur(BitmapFactory.decodeResource(res, R.drawable
                    .shop_logo_title), 20, false);
            mTitleBlurFrontLayout.setImageDrawable(new BitmapDrawable(bmp));
            mTitleBlurFrontLayout.setAlpha(0);

            mTitleSearchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onTitleBarItemClick(mTitleSearchBtn.getId());
                    }
                }
            });
        }

        if (mShopHeaderView == null) {
            mShopHeaderView = parent.findViewById(R.id.view_shop_info_header);
        }

        if (mShopInfoLayout == null) {
            mShopInfoLayout = parent.findViewById(R.id.shop_info_header_view);
            int initHeight = mShopInfoLayout.initViewsHeight();
            Log.d(TAG, "ShopInfoDetail height:" + mShopInfoLayout.getHeight() + " childH:"
                    + mAppBarLayout.getHeight() + " parentH:" + parent.getHeight() + " " +
                    "initHeight:" + initHeight);
            setAppBarLayoutParams(mAppBarLayout, mAppBarLayout.getHeight() - mShopInfoLayout
                    .getHeight() + initHeight);
        }

        if (mImageViewShop == null) {
            mImageViewShop = parent.findViewById(R.id.iv_shop);
        }

        if (mImageViewShopLike == null) {
            mImageViewShopLike = parent.findViewById(R.id.iv_like);
        }

        if (mIndicatorView == null) {
            mIndicatorView = parent.findViewById(R.id.sliding_tabs);
        }

        if (mHeadsUpArrow == null) {
            mHeadsUpArrow = parent.findViewById(R.id.ivHeadsupArrow);
            mHeadsUpArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkShouldSpringRecover(mParent, mAppBarLayout);
                }
            });
        }
    }

    private void initViewsHeight(CoordinatorLayout parent) {
        if (mNormalViewHeight == 0) {
            mNormalViewHeight = mAppBarLayout.getBottom() - mIndicatorHeight;
        }

        if (mTitleBarHeight == 0) {
            mTitleBarHeight = mTitleBarLayout.getHeight();
        }

        if (mIndicatorHeight == 0) {
            mIndicatorHeight = mIndicatorView.getHeight();
        }

        if (mNormalViewHeight > 0 && mAppBarLayout.getBottom() < mNormalViewHeight) {
            mInited = true;
            if (mListener != null) {
                mListener.initEnd(mTitleBarHeight + mIndicatorHeight);
            }
            mNormalViewHeight = mAppBarLayout.getBottom() - mIndicatorHeight;
            mShopInfoHeight = mShopInfoLayout.getHeight();
            mImageViewScaleHeight = mAppBarLayout.getBottom() - mIndicatorHeight - mDpToPx20;
            mAppbarLayoutMinOffset = mDpToPx106 - mNormalViewHeight - mIndicatorHeight;
            mAppbarLayoutMaxOffset = parent.getHeight() - mAppBarLayout.getBottom() +
                    mIndicatorHeight;
            mShopInfoLayout.setMaxOffset(mAppbarLayoutMaxOffset);
            Log.d(TAG, "end ShopInfoDetail height:" + mShopInfoLayout.getHeight() + " childH:"
                    + mAppBarLayout.getHeight() + " parentH:" + parent.getHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child,
                                         MotionEvent ev) {
        //滑动到最大状态后 屏蔽滑动事件
        mIsBeingDragged = super.onInterceptTouchEvent(parent, child, ev) && mOffsetSpring !=
                mAppbarLayoutMaxOffset;
        return false;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        boolean touch = super.onTouchEvent(parent, child, ev) && mOffsetSpring !=
                mAppbarLayoutMaxOffset;
        if (child != null) {
//            float fixedBottom = getFixedBottom(child);
//            if (fixedBottom <= mNormalViewHeight) {//滑动状态
//                onHandleScroll(child, fixedBottom);
//            }
        }
        return false;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View
            directTargetChild, View target, int nestedScrollAxes, int type) {
        //屏蔽fling事件
        final boolean started = super.onStartNestedScroll(parent, child, directTargetChild,
                target, nestedScrollAxes, type) && (type == ViewCompat.TYPE_TOUCH);
        Log.d(TAG, "onStartNestedScroll started:" + started + " type:" + type);
        if (started && mSpringRecoverAnimator != null && mSpringRecoverAnimator.isRunning()) {
            mSpringRecoverAnimator.cancel();
        }
        return started;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                  View target, int dx, int dy, int[] consumed, int type) {
        Log.e(TAG, "onNestedPreScroll dy:" + dy + " childBottom " + child.getBottom() + " type:"
                + type + " consumed:" + Arrays.toString(consumed) + " mOffsetSpring:"
                + mOffsetSpring + " isBottom:" + (getFixedBottom(child) <= mNormalViewHeight)
                + " infoOffset:" + mShopInfoLayout.getCurrentOffset());
        if (dy > 0) {//上滑
            if (mOffsetSpring - dy >= mAppbarLayoutMinOffset) {
                consumed[1] = dy;
                mOffsetSpring = mOffsetSpring - dy;
            } else {
                consumed[1] = dy;//mOffsetSpring - mAppbarLayoutMinOffset;
                mOffsetSpring = mAppbarLayoutMinOffset;
            }
        } else {
            if (mOffsetSpring - dy <= mAppbarLayoutMaxOffset) {
                consumed[1] = dy;
                mOffsetSpring = mOffsetSpring - dy;
            } else {
                consumed[1] = mOffsetSpring - mAppbarLayoutMaxOffset;
                mOffsetSpring = mAppbarLayoutMaxOffset;
            }
        }

        onHandleScroll(child, mNormalViewHeight + mOffsetSpring);
        setTopAndBottomOffset((mOffsetSpring <= 0) ? mOffsetSpring : 0);
        checkShouldSpring(coordinatorLayout, child, mOffsetSpring);
    }

    private void checkShouldSpring(CoordinatorLayout coordinatorLayout, AppBarLayout child, int
            newOffset) {
        if (newOffset < 0) {
            newOffset = 0;
        }
        if (newOffset != mShopInfoLayout.getCurrentOffset()) {
            updateSpringOffsetByscroll(coordinatorLayout, child, newOffset);
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                               View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl,
                                   View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type);
        checkShouldSpringMinOrMax(coordinatorLayout, abl);
    }

    @Override
    public boolean onNestedFling(final CoordinatorLayout coordinatorLayout,
                                 final AppBarLayout child, View target, float velocityX, float
                                         velocityY, boolean consumed) {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY,
                consumed);
    }

    @Override
    void onFlingFinished(CoordinatorLayout parent, AppBarLayout layout) {
        super.onFlingFinished(parent, layout);
//        animateRecoverBySpring(parent, layout);
    }

    private boolean onHandleScroll(AppBarLayout child, int fixedBottom) {
        Log.d(TAG, "handleScroll fixedBottom:" + fixedBottom + " mImageViewScaleHeight:"
                + mImageViewScaleHeight + " mNormalViewHeight:" + mNormalViewHeight);
        setTitleBarBgProcess(fixedBottom);

        float scale = setShopImageScaleProcess(fixedBottom);

        setSreachBtnAlphaProcess(fixedBottom);

        setTitleBarSearchProcess(fixedBottom);

        onTitleLayoutTranslationY(fixedBottom, scale, scale);
        return true;
    }

    /**
     * 处理 TitleBar搜索框显示 Alpha mImageViewScaleHeight - mDpToPx40 到 mImageViewScaleHeight -
     * mDpToPx50 间距10dp
     *
     * @param fixedBottom
     */
    private void setTitleBarSearchProcess(float fixedBottom) {
        float alpha = 1;
        if (fixedBottom > mImageViewScaleHeight - mDpToPx40) {
            alpha = 0;
        } else if (fixedBottom <= mImageViewScaleHeight - mDpToPx40 && fixedBottom >
                mImageViewScaleHeight - mDpToPx50) {
            alpha = (mImageViewScaleHeight - mDpToPx40 - fixedBottom) / mDpToPx10;
        }
        if (mTitleSearchTextView != null) {
            mTitleSearchTextView.setAlpha(getValidValue(alpha));
        }
    }

    /**
     * 处理 TitleBar搜索按钮隐藏 Alpha效果 mImageViewScaleHeight - mDpToPx20 到 mImageViewScaleHeight -
     * mDpToPx40 间距20dp
     *
     * @param fixedBottom
     */
    private void setSreachBtnAlphaProcess(float fixedBottom) {
        float alpha = 0;
        if (fixedBottom > mImageViewScaleHeight - mDpToPx20) {
            alpha = 1;
        } else if (fixedBottom <= mImageViewScaleHeight - mDpToPx20 && fixedBottom >
                mImageViewScaleHeight - mDpToPx40) {
            alpha = (mDpToPx40 + fixedBottom - mImageViewScaleHeight) / mDpToPx20;
        }
        if (mTitleSearchBtn != null) {
            mTitleSearchBtn.setAlpha(getValidValue(alpha));
        }
        if (mTitlePindanBtn != null) {
            mTitlePindanBtn.setAlpha(getValidValue(alpha));
        }
    }

    /**
     * 处理 店铺照片缩放 alpha 倍数为1
     * 缩放 mImageViewScaleHeight 到 mImageViewScaleHeight - mDpToPx40  间距40dp
     * alpha mImageViewScaleHeight - mDpToPx20 到 mImageViewScaleHeight - mDpToPx40  间距20dp
     *
     * @param fixedBottom
     * @return
     */
    private float setShopImageScaleProcess(float fixedBottom) {
        float scale = 0;
        float alpha = 0;
        if (fixedBottom > mImageViewScaleHeight) {
            scale = 1;
        } else if (fixedBottom <= mImageViewScaleHeight && fixedBottom > mImageViewScaleHeight -
                mDpToPx40) {
            scale = (mDpToPx40 + fixedBottom - mImageViewScaleHeight) / mDpToPx40;
        }
        if (fixedBottom > mImageViewScaleHeight - mDpToPx20) {
            alpha = 1;
        } else if (fixedBottom <= mImageViewScaleHeight - mDpToPx20 && fixedBottom >
                mImageViewScaleHeight - mDpToPx40) {
            alpha = (mDpToPx40 + fixedBottom - mImageViewScaleHeight) / mDpToPx20;
        }
        scale = getValidValue(scale);
        alpha = getValidValue(alpha);
        // 缩放目标View
        if (mImageViewShop != null) {
            mImageViewShop.setScaleX(scale);
            mImageViewShop.setScaleY(scale);
            mImageViewShop.setAlpha(alpha);
        }
        if (mImageViewShopLike != null) {
            mImageViewShopLike.setScaleX(scale);
            mImageViewShopLike.setScaleY(scale);
            mImageViewShopLike.setAlpha(alpha);
        }
        return scale;
    }

    /**
     * 处理 TitleBar背景高斯 mNormalViewHeight 到 mImageViewScaleHeight 间距20dp
     *
     * @param fixedBottom
     */
    private void setTitleBarBgProcess(float fixedBottom) {
        float alpha;
        if (fixedBottom > mNormalViewHeight) {
            alpha = 0;
        } else if (fixedBottom <= mNormalViewHeight && fixedBottom > mImageViewScaleHeight) {
            alpha = (mNormalViewHeight - fixedBottom) / mDpToPx20;
        } else {
            alpha = 1;
        }
        if (mTitleBlurFrontLayout != null) {
            mTitleBlurFrontLayout.setAlpha((int) (getValidValue(alpha) * 255));
        }
    }

    /**
     * 改变标题布局高度 平移店铺头像
     * 标题栏 修改高度 mNormalViewHeight 到 mImageViewScaleHeight - mDpToPx40 间距 60dp
     * 店铺头像 修改高度 mNormalViewHeight  到 mImageViewScaleHeight - mDpToPx20 间距 40dp
     *
     * @param fixedBottom
     * @param shopScaleY     缩放对平移动画补偿
     * @param shopLikeScaleY
     */
    private void onTitleLayoutTranslationY(int fixedBottom, float shopScaleY, float
            shopLikeScaleY) {
        int headerHeight;
        int titleBottom;

        if (fixedBottom > mImageViewScaleHeight) {
            shopScaleY = 0;
        } else if (fixedBottom <= mImageViewScaleHeight && fixedBottom > mImageViewScaleHeight -
                mDpToPx20) {
            shopScaleY = mImageViewShop.getHeight() * (1 - shopScaleY) / 2;
        } else if (fixedBottom <= mImageViewScaleHeight - mDpToPx20 && fixedBottom >
                mImageViewScaleHeight - mDpToPx40) {
            shopScaleY = mImageViewShop.getHeight() / 4;
        } else {
            shopScaleY = mImageViewShop.getHeight() / 4;
        }
        shopLikeScaleY = 0;

        if (fixedBottom > mNormalViewHeight) {
            headerHeight = mDpToPx20;
            titleBottom = mNormalViewHeight - mShopInfoHeight + mDpToPx20;
        } else if (fixedBottom <= mNormalViewHeight && fixedBottom > mNormalViewHeight -
                mDpToPx40) {
            headerHeight = mDpToPx20;
            titleBottom = fixedBottom - mShopInfoHeight + mDpToPx20;
        } else if (fixedBottom <= mNormalViewHeight - mDpToPx40 && fixedBottom >
                mNormalViewHeight - mDpToPx60) {
            headerHeight = fixedBottom - mNormalViewHeight + mDpToPx60;
            titleBottom = fixedBottom - mShopInfoHeight + mDpToPx20;
        } else {
            headerHeight = 0;
            titleBottom = mTitleBarHeight;
        }

        if (mTitleBottomView != null) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)
                    mTitleBottomView.getLayoutParams();
            layoutParams.height = headerHeight;
            mTitleBottomView.setAlpha(headerHeight);
        }
        if (mTitleBlurFrontLayout != null) {
            mTitleBlurFrontLayout.setBottom(titleBottom);
        }
        if (mTitleLayout != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
                    mTitleLayout.getLayoutParams();
            layoutParams.height = titleBottom;
            mTitleLayout.setLayoutParams(layoutParams);
        }

        // 缩放目标View
        if (mImageViewShop != null) {
            mImageViewShop.setTranslationY(shopScaleY + mDpToPx20 - headerHeight);
        }
        if (mImageViewShopLike != null) {
            mImageViewShopLike.setTranslationY(mDpToPx20 - headerHeight);
        }
    }

    /**
     * 处理 店铺公告、优惠等信息展开
     *
     * @param coordinatorLayout
     * @param appBarLayout
     * @param offset
     */
    private void updateSpringOffsetByscroll(CoordinatorLayout coordinatorLayout, AppBarLayout
            appBarLayout, int offset) {
        if (mSpringRecoverAnimator != null && mSpringRecoverAnimator.isRunning())
            mSpringRecoverAnimator.cancel();
        updateSpringHeaderHeight(coordinatorLayout, appBarLayout, offset);
    }

    private boolean updateSpringHeaderHeight(CoordinatorLayout coordinatorLayout, AppBarLayout
            appBarLayout, int offset) {
        if (appBarLayout.getHeight() < mNormalViewHeight + mIndicatorHeight || offset < 0)
            return false;
        setShopInfoSpringChange(offset);
        setAppBarLayoutParams(appBarLayout, mNormalViewHeight + mIndicatorHeight + offset);

        coordinatorLayout.dispatchDependentViewsChanged(appBarLayout);
        return true;
    }

    private void setShopInfoSpringChange(int offset) {
        if (mShopInfoLayout == null) return;
        mShopInfoLayout.onSpringChange(0, offset);
    }

    private void setAppBarLayoutParams(AppBarLayout appBarLayout, int height) {
        if (appBarLayout == null) return;
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
                mAppBarLayout.getLayoutParams();
        layoutParams.height = height;
        appBarLayout.setLayoutParams(layoutParams);
    }

    private float getValidValue(float value) {
        if (value < 0) {
            value = 0;
        } else if (value > 1) {
            value = 1;
        }
        return value;
    }

    private float getFixedBottom(AppBarLayout child) {
        return child.getBottom() - mIndicatorHeight;
    }

    public interface OnAppBarListener {
        void onTitleBarItemClick(@IdRes int id);

        void initEnd(int miniTopHeight);
    }

    private void checkShouldSpringMinOrMax(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {
        if (mOffsetSpring > 0 && mOffsetSpring < mAppbarLayoutMaxOffset) {
            int endValue = 0;
            if (mOffsetSpring > mAppbarLayoutMaxOffset / 3) {
                endValue = mAppbarLayoutMaxOffset;
            }
            animateRecoverBySpring(coordinatorLayout, abl, endValue);
        }
    }

    private void checkShouldSpringRecover(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {
        if (mOffsetSpring == mAppbarLayoutMaxOffset) {
            animateRecoverBySpring(coordinatorLayout, abl, 0);
        }
    }

    private void animateRecoverBySpring(final CoordinatorLayout coordinatorLayout, final
    AppBarLayout abl, int endValue) {
        if (mSpringRecoverAnimator == null) {
            mSpringRecoverAnimator = new ValueAnimator();
            mSpringRecoverAnimator.setDuration(200);
            mSpringRecoverAnimator.setInterpolator(new DecelerateInterpolator());
            mSpringRecoverAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    if (updateSpringHeaderHeight(coordinatorLayout, abl, value)) {
                        mOffsetSpring = (value < 0) ? 0 : value;
                    }
                }
            });
        } else {
            if (mSpringRecoverAnimator.isRunning()) {
                mSpringRecoverAnimator.cancel();
            }
        }
        mSpringRecoverAnimator.setIntValues(mOffsetSpring, endValue);
        mSpringRecoverAnimator.start();
    }

}
