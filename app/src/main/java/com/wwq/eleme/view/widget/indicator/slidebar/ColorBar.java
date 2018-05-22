package com.wwq.eleme.view.widget.indicator.slidebar;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.View;

public class ColorBar implements ScrollBar {
    protected Gravity gravity;
    protected View view;
    protected int drawableResId;
    protected int height;
    protected int width;

    public ColorBar(Context context, @DrawableRes int drawableResId, int height) {
        this(context, drawableResId, height, Gravity.BOTTOM);
    }

    public ColorBar(Context context, @DrawableRes int drawableResId, int height, Gravity gravity) {
        view = new View(context);
        this.drawableResId = drawableResId;
        view.setBackgroundResource(drawableResId);
        this.height = height;
        this.gravity = gravity;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public void setColor(int drawableResId) {
        this.drawableResId = drawableResId;
        view.setBackgroundResource(drawableResId);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getHeight(int tabHeight) {
        if (height == 0) {
            return tabHeight;
        }
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getWidth(int tabWidth) {
        if (width == 0) {
            return tabWidth;
        }
        return width;
    }

    @Override
    public View getSlideView() {
        return view;
    }

    @Override
    public Gravity getGravity() {
        return gravity;
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

}
