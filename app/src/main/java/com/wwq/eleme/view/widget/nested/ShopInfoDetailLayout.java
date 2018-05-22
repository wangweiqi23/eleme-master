package com.wwq.eleme.view.widget.nested;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.wwq.eleme.R;
import com.wwq.eleme.util.DensityUtil;

/**
 * Created by alexwangweiqi on 18/3/30.
 */
public class ShopInfoDetailLayout extends RelativeLayout {

    public static final String TAG = ShopInfoDetailLayout.class.getSimpleName();

    private final boolean isDebug = true;

    View mLayoutName;
    AppCompatTextView mTvName;

    View mLayoutPostcardTip;
    View mLayoutPostcardContent;
    AppCompatTextView mTvPostcardContent;

    View mLayoutDiscountTip;
    View mLayoutDiscountContent;
    View mTvDiscountContentDetail;

    LinearLayout mLayoutDiscountTableCount;
    AppCompatTextView mTvDiscountTableCount;
    AppCompatImageView mIvDiscountTableCount;
    TableLayout mLayoutDiscountTable;

    /**
     * 底部收起按钮
     */
    private ImageView mHeadsUpArrow;

    private int mDpToPx20;
    private int mDpToPx30;

    private int mPostcardTipHeight;
    private int mPostcardContentHeight;

    private int mDiscountTipHeight;
    private int mDiscountContentHeight;

    private int mDiscountTableHeight;

    private int mCurrentOffset;
    private int mMaxOffset;
    private int mTotalPostcardContentHeight;
    private int mTotalDiscountTipHeight;
    private int mTotalDiscountContentHeight;

    private int mNormalHeight;

    public ShopInfoDetailLayout(Context context) {
        this(context, null);
    }

    public ShopInfoDetailLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShopInfoDetailLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        if (isDebug) {
            Log.d(TAG, "ShopInfoDetail init");
        }

        mDpToPx20 = DensityUtil.dip2px(context, 20);
        mDpToPx30 = DensityUtil.dip2px(context, 30);

        LayoutInflater.from(getContext()).inflate(R.layout.partial_shop_header_view, this);

        mLayoutName = findViewById(R.id.layout_name);
        mTvName = findViewById(R.id.tv_name);

        mLayoutPostcardTip = findViewById(R.id.layout_shop_postcard_tip);
        mLayoutPostcardContent = findViewById(R.id.layout_shop_postcard_content);
        mTvPostcardContent = findViewById(R.id.tv_shop_postcard_content);

        mLayoutDiscountTip = findViewById(R.id.layout_shop_discount_tip);
        mLayoutDiscountContent = findViewById(R.id.layout_shop_discount_content);
        mTvDiscountContentDetail = findViewById(R.id.tv_shop_discount_detail);

        mLayoutDiscountTableCount = findViewById(R.id.layout_table_shop_discount_count);
        mTvDiscountTableCount = findViewById(R.id.tv_table_shop_discount_count);
        mIvDiscountTableCount = findViewById(R.id.iv_table_shop_discount_count);
        mLayoutDiscountTable = findViewById(R.id.table_shop_discount);

        mHeadsUpArrow = findViewById(R.id.ivHeadsupArrow);
    }

    public void setMaxOffset(int maxOffset) {
        this.mMaxOffset = maxOffset;
    }

    public int getCurrentOffset() {
        return mCurrentOffset;
    }

    /**
     * 初始化 view
     */
    public int initViewsHeight() {
        int initHeight;

        initViewHeight();

        //标题栏高度
        if (mLayoutName.getHeight() > mTvName.getHeight()) {
            initHeight = getHeight() - (mLayoutName.getHeight() - mTvName.getHeight());
            RelativeLayout.LayoutParams nameParams = (RelativeLayout.LayoutParams)
                    mLayoutName.getLayoutParams();
            nameParams.height = mTvName.getHeight();
            mLayoutName.setLayoutParams(nameParams);
        } else {
            initHeight = getHeight();
        }

        //公告内容高度
        initHeight -= (mLayoutPostcardContent.getHeight() - mTvPostcardContent.getLineHeight());
        if (isDebug) {
            Log.d(TAG, "ShopInfoDetail lineHight:" + mTvPostcardContent.getLineHeight()
                    + " lineCount:" + mTvPostcardContent.getLineCount() + " w:" + mTvPostcardContent
                    .getWidth() + " h:" + mTvPostcardContent.getHeight());
        }
        mTvPostcardContent.setMaxLines(1);

        mLayoutPostcardContent.setPadding(mDpToPx30, 0, mDpToPx30, 0);
        RelativeLayout.LayoutParams postcardContentParams = (RelativeLayout.LayoutParams)
                mLayoutPostcardContent.getLayoutParams();
        postcardContentParams.height = mTvPostcardContent.getLineHeight();
        mLayoutPostcardContent.setLayoutParams(postcardContentParams);

        //优惠条列高度
        initHeight -= (mLayoutDiscountTable.getHeight() - mLayoutDiscountTable.getChildAt(0)
                .getHeight());

        RelativeLayout.LayoutParams discountTableParams = (RelativeLayout.LayoutParams)
                mLayoutDiscountTable.getLayoutParams();
        discountTableParams.height = mLayoutDiscountTable.getChildAt(0).getHeight();
        mLayoutDiscountTable.setLayoutParams(discountTableParams);

        mNormalHeight = initHeight;

        return initHeight;
    }

    private void initViewHeight() {
        mPostcardTipHeight = mLayoutPostcardTip.getHeight();
        mPostcardContentHeight = mTvPostcardContent.getHeight();

        mDiscountTipHeight = mLayoutDiscountTip.getHeight();
        mDiscountContentHeight = mLayoutDiscountContent.getHeight();

        mDiscountTableHeight = mLayoutDiscountTable.getHeight();

        mTotalPostcardContentHeight = mPostcardTipHeight + (mPostcardContentHeight -
                mTvPostcardContent.getLineHeight());
        mTotalDiscountTipHeight = mTotalPostcardContentHeight + mDiscountTipHeight;
        mTotalDiscountContentHeight = mTotalDiscountTipHeight + mDiscountContentHeight;
    }

    public void onSpringChange(int state, int newOffset) {
        if (isDebug) {
            Log.d(TAG, "ShopInfoDetail onSpringChange state:" + state
                    + " newOffset:" + newOffset + " mCurrentOffset:" + mCurrentOffset);
        }
        if (newOffset < 0) {
            newOffset = 0;
        }
        if (newOffset == mCurrentOffset) return;
        mCurrentOffset = newOffset;

        boolean isUsed = false;
        onSetPostcardTipProcess(isUsed, newOffset);
        onSetPostcardContentAndDiscountTip(isUsed, newOffset);
        onSetDiscountContentProcess(isUsed, newOffset);
        onSetDiscountTableProcess(isUsed, newOffset);
        onSetHeadsUpArrow(isUsed, newOffset);

        this.getLayoutParams().height = mNormalHeight + newOffset;

    }

    private boolean onSetPostcardTipProcess(boolean isUsed, int newOffset) {
        if (isUsed) return isUsed;
        int height;
        float alpha;
        float translationY;
        if (newOffset <= 0) {
            alpha = 0;
            translationY = 0;
            height = mTvName.getHeight();
        } else if (newOffset <= mPostcardTipHeight) {
            height = mTvName.getHeight() + newOffset;
            translationY = ((float) newOffset / mPostcardTipHeight) * mTvName.getHeight();
            alpha = (float) newOffset / mPostcardTipHeight;
            isUsed = true;
        } else {
            alpha = 1;
            translationY = mTvName.getHeight();
            height = mTvName.getHeight() + mPostcardTipHeight;
        }
        if (isDebug) {
            Log.d(TAG, "ShopInfoDetail onSetPostcardProcess newOffset:" + newOffset + " height:" +
                    height);
        }
        mLayoutName.getLayoutParams().height = height;
        mLayoutPostcardTip.setAlpha(alpha);
        mLayoutPostcardTip.setTranslationY(translationY);
        return isUsed;
    }

    private boolean onSetPostcardContentAndDiscountTip(boolean isUsed, int newOffset) {
        if (isUsed) return isUsed;
        int maxLines;
        int height;
        int padding;

        float alpha;
        float translationY;

        if (newOffset <= mPostcardTipHeight) {
            maxLines = 1;
            padding = mDpToPx30;
            height = mTvPostcardContent.getLineHeight();

            alpha = 0;
            translationY = 0;
        } else if (newOffset <= mTotalPostcardContentHeight) {//postcardContent
            int dNewOffset = newOffset - mPostcardTipHeight;
            height = mTvPostcardContent.getLineHeight() + dNewOffset;
            maxLines = Integer.MAX_VALUE;
            padding = mDpToPx30 - dNewOffset;
            if (padding < mDpToPx20) {
                padding = mDpToPx20;
            }

            alpha = 0;
            translationY = 0;
            isUsed = true;
        } else if (newOffset <= mTotalPostcardContentHeight + mDiscountTipHeight) {//discountTip
            int dy = newOffset - mTotalPostcardContentHeight;
            padding = mDpToPx20;
            height = mPostcardContentHeight + dy;
            maxLines = Integer.MAX_VALUE;

            translationY = ((float) dy / mDiscountTipHeight) * mPostcardContentHeight;
            alpha = (float) dy / mDiscountTipHeight;
            isUsed = true;
        } else {
            padding = mDpToPx20;
            height = mPostcardContentHeight + mDiscountTipHeight;
            maxLines = Integer.MAX_VALUE;

            alpha = 1;
            translationY = mTvPostcardContent.getHeight();
        }
        if (isDebug) {
            Log.d(TAG, "ShopInfoDetail onSetPostcardContent newOffset:" + newOffset + " height:" +
                    height + " maxLines:" + maxLines + "alpha:" + alpha + " translationY:" +
                    translationY);
        }
        mTvPostcardContent.setMaxLines(maxLines);
        mLayoutPostcardContent.getLayoutParams().height = height;
        mLayoutPostcardContent.setPadding(padding, 0, padding, 0);

        mLayoutDiscountTip.setAlpha(alpha);
        mLayoutDiscountTip.setTranslationY(translationY);

        return isUsed;
    }

    private boolean onSetDiscountContentProcess(boolean isUsed, int newOffset) {
        if (isUsed) return isUsed;
        float scale;
        int visibility;
        if (newOffset <= mTotalDiscountTipHeight) {
            scale = 1;
            visibility = View.GONE;
        } else if (newOffset <= mTotalDiscountTipHeight + mDiscountContentHeight) {
            int dy = newOffset - mTotalDiscountTipHeight;
            scale = (float) dy / (mDiscountContentHeight * 2) + 1;
            if (scale > 1.2) {
                scale = 1.2f;
                visibility = View.VISIBLE;
            } else {
                visibility = View.GONE;
            }
            isUsed = true;
        } else {
            scale = 1.2f;
            visibility = View.VISIBLE;
        }
        if (isDebug) {
            Log.d(TAG, "ShopInfoDetail onSetDiscountContentProcess newOffset:" + newOffset + " " +
                    "scale:" + scale);
        }
        mLayoutDiscountContent.setScaleX(scale);
        mLayoutDiscountContent.setScaleY(scale);
        mTvDiscountContentDetail.setVisibility(visibility);
        return isUsed;
    }

    private boolean onSetDiscountTableProcess(boolean isUsed, int newOffset) {
        if (isUsed) return isUsed;
        int height;
        if (newOffset <= mTotalDiscountContentHeight) {
            height = mLayoutDiscountTable.getChildAt(0).getHeight();
        } else if (newOffset <= mTotalDiscountContentHeight + (mDiscountTableHeight -
                mLayoutDiscountTable.getChildAt(0).getHeight())) {
            int dNewOffset = newOffset - mTotalDiscountContentHeight;
            height = mLayoutDiscountTable.getChildAt(0).getHeight() + dNewOffset;
            isUsed = true;
        } else {
            height = mDiscountTableHeight;
        }
        if (isDebug) {
            Log.d(TAG, "ShopInfoDetail onSetDiscountTableProcess newOffset:" + newOffset + " " +
                    "height:" + height + " mMaxHeight:" + mMaxOffset);
        }
        mLayoutDiscountTable.getLayoutParams().height = height;
        return isUsed;
    }

    private boolean onSetHeadsUpArrow(boolean isUsed, int newOffset) {
        if (isUsed) return isUsed;
        int visibility;
        if (newOffset == mMaxOffset) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        Log.d(TAG, "ShopInfoDetail onSetHeadsUpArrow newOffset:" + newOffset + " visibility:" +
                visibility);
        if (mHeadsUpArrow.getVisibility() != visibility) {
            mHeadsUpArrow.setVisibility(visibility);
        }

        return isUsed;
    }

}
