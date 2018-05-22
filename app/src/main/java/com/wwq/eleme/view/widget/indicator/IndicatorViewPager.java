/*
Copyright 2014 shizhefei（LuckyJayce）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.wwq.eleme.view.widget.indicator;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;

import com.wwq.eleme.view.widget.indicator.slidebar.ScrollBar;
import com.wwq.eleme.view.widget.indicator.viewpager.RecyclingPagerAdapter;
import com.wwq.eleme.view.widget.indicator.viewpager.SViewPager;

public class IndicatorViewPager {
    private Indicator indicatorView;
    private ViewPager viewPager;
    private IndicatorPagerAdapter adapter;
    private OnIndicatorPageChangeListener onIndicatorPageChangeListener;

    private OnIndicatorClickListener mOnIndicatorClickListener;

    public IndicatorViewPager(Indicator indicator, ViewPager viewPager) {
        super();
        this.indicatorView = indicator;
        this.viewPager = viewPager;
        viewPager.setOnPageChangeListener(onPageChangeListener);
        indicatorView.setOnItemSelectListener(onItemSelectedListener);
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(IndicatorPagerAdapter adapter) {
        this.adapter = adapter;
        viewPager.setAdapter(adapter.getPagerAdapter());
        indicatorView.setAdapter(adapter.getIndicatorAdapter());
    }

    /**
     * 切换至指定的页面
     *
     * @param item 页面的索引
     * @param anim 是否动画效果
     */
    public void setCurrentItem(int item, boolean anim) {
        viewPager.setCurrentItem(item, anim);
        indicatorView.setCurrentItem(item, anim);
    }

    /**
     * 设置页面切换监听
     *
     * @return
     */
    public void setOnIndicatorPageChangeListener(OnIndicatorPageChangeListener onIndicatorPageChangeListener) {
        this.onIndicatorPageChangeListener = onIndicatorPageChangeListener;
    }

    public void setOnIndicatorClickListener(OnIndicatorClickListener onIndicatorClickListener) {
        mOnIndicatorClickListener = onIndicatorClickListener;
    }

    /**
     * 设置indicatorView上滑动变化的转换监听，tab在切换过程中会调用此监听。
     *
     * @param onTransitionListener
     */
    public void setIndicatorOnTransitionListener(Indicator.OnTransitionListener onTransitionListener) {
        indicatorView.setOnTransitionListener(onTransitionListener);
    }

    /**
     * 设置indicatorView的滑动块样式
     *
     * @param scrollBar
     */
    public void setIndicatorScrollBar(ScrollBar scrollBar) {
        indicatorView.setScrollBar(scrollBar);
    }

    /**
     * 设置缓存界面的个数，左右两边缓存界面的个数，不会被重新创建。<br>
     * 默认是1，表示左右两边 相连的1个界面和当前界面都会被缓存住，比如切换到左边的一个界面，那个界面是不会重新创建的。
     *
     * @param limit
     */
    public void setPageOffscreenLimit(int limit) {
        viewPager.setOffscreenPageLimit(limit);
    }

    /**
     * 设置page间的图片的宽度
     *
     * @param marginPixels
     */
    public void setPageMargin(int marginPixels) {
        viewPager.setPageMargin(marginPixels);
    }

    /**
     * 设置page间的图片
     *
     * @param d
     */
    public void setPageMarginDrawable(Drawable d) {
        viewPager.setPageMarginDrawable(d);
    }

    /**
     * 设置page间的图片
     *
     * @param resId
     */
    public void setPageMarginDrawable(int resId) {
        viewPager.setPageMarginDrawable(resId);
    }

    /**
     * 获取上一次选中的索引
     *
     * @return
     */
    public int getPreSelectItem() {
        return indicatorView.getPreSelectItem();
    }

    /**
     * 获取当前选中的索引
     *
     * @return
     */
    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    public IndicatorPagerAdapter getAdapter() {
        return this.adapter;
    }

    public OnIndicatorPageChangeListener getOnIndicatorPageChangeListener() {
        return onIndicatorPageChangeListener;
    }

    public Indicator getIndicatorView() {
        return indicatorView;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 通知适配器数据变化，重新加载页面
     */
    public void notifyDataSetChanged() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private Indicator.OnItemSelectedListener onItemSelectedListener = new Indicator.OnItemSelectedListener() {

        @Override
        public void onItemSelected(View selectItemView, int select, int preSelect) {
            if (mOnIndicatorClickListener != null) {
                mOnIndicatorClickListener.onIndicatorClick(preSelect, select);
            }
            if (viewPager instanceof SViewPager) {
                viewPager.setCurrentItem(select, ((SViewPager) viewPager).isCanScroll());
            } else {
                viewPager.setCurrentItem(select, true);
            }

            // if (onIndicatorPageChangeListener != null) {
            // onIndicatorPageChangeListener.onIndicatorPageChange(preSelect,
            // select);
            // }
        }
    };

    public interface OnIndicatorPageChangeListener {
        /**
         * 注意 preItem 可能为 -1。表示之前没有选中过,每次adapter.notifyDataSetChanged也会将preItem
         * 设置为-1；
         *
         * @param preItem
         * @param currentItem
         */
        void onIndicatorPageChange(int preItem, int currentItem);
    }

    public interface OnIndicatorClickListener {
        void onIndicatorClick(int preItem, int currentItem);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            indicatorView.setCurrentItem(position, true);
            if (onIndicatorPageChangeListener != null) {
                onIndicatorPageChangeListener.onIndicatorPageChange(indicatorView.getPreSelectItem(), position);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public interface IndicatorPagerAdapter {

        PagerAdapter getPagerAdapter();

        Indicator.IndicatorAdapter getIndicatorAdapter();

        void notifyDataSetChanged();

    }

    /**
     * viewpage 的每个页面是view的形式
     *
     * @author Administrator
     */
    public static abstract class IndicatorViewPagerAdapter implements IndicatorPagerAdapter {

        private Indicator.IndicatorAdapter indicatorAdapter = new Indicator.IndicatorAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent, int
                    currentSelectedIndex) {
                return IndicatorViewPagerAdapter.this.getViewForTab(position, convertView,
                        parent, currentSelectedIndex);
            }

            @Override
            public int getCount() {
                return IndicatorViewPagerAdapter.this.getCount();
            }
        };

        private RecyclingPagerAdapter pagerAdapter = new RecyclingPagerAdapter() {

            @Override
            public int getCount() {
                return IndicatorViewPagerAdapter.this.getCount();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup container) {
                return IndicatorViewPagerAdapter.this.getViewForPage(position, convertView, container);
            }

            @Override
            public float getPageWidth(int position) {
                return IndicatorViewPagerAdapter.this.getPageRatio(position);
            }

            @Override
            public int getItemPosition(Object object) {
                return IndicatorViewPagerAdapter.this.getItemPosition(object);
            }
        };

        public int getItemPosition(Object object) {
            return RecyclingPagerAdapter.POSITION_UNCHANGED;
        }

        public abstract int getCount();

        public abstract View getViewForTab(int position, View convertView, ViewGroup container, int
                currentSelectedIndex);

        public abstract View getViewForPage(int position, View convertView, ViewGroup container);

        public float getPageRatio(int position) {
            return 1f;
        }

        @Override
        public void notifyDataSetChanged() {
            indicatorAdapter.notifyDataSetChanged();
            pagerAdapter.notifyDataSetChanged();
        }

        @Override
        public PagerAdapter getPagerAdapter() {
            return pagerAdapter;
        }

        @Override
        public Indicator.IndicatorAdapter getIndicatorAdapter() {
            return indicatorAdapter;
        }

    }

    /**
     * viewpage 的每个页面是Fragment的形式
     *
     * @author Administrator
     */
    public static abstract class IndicatorFragmentPagerAdapter implements IndicatorPagerAdapter {
        private FragmentListPageAdapter pagerAdapter;

        public IndicatorFragmentPagerAdapter(FragmentManager fragmentManager) {
            super();
            pagerAdapter = new FragmentListPageAdapter(fragmentManager) {

                @Override
                public int getCount() {
                    return IndicatorFragmentPagerAdapter.this.getCount();
                }

                @Override
                public Fragment getItem(int position) {
                    return IndicatorFragmentPagerAdapter.this.getFragmentForPage(position);
                }

                @Override
                public float getPageWidth(int position) {
                    return IndicatorFragmentPagerAdapter.this.getPageRatio(position);
                }

                @Override
                public int getItemPosition(Object object) {
                    return IndicatorFragmentPagerAdapter.this.getItemPosition(object);
                }
            };
        }

        private Indicator.IndicatorAdapter indicatorAdapter = new Indicator.IndicatorAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent, int
                    currentSelectedIndex) {
                return IndicatorFragmentPagerAdapter.this.getViewForTab(position, convertView,
                        parent, currentSelectedIndex);
            }

            @Override
            public int getCount() {
                return IndicatorFragmentPagerAdapter.this.getCount();
            }

        };

        public int getItemPosition(Object object) {
            return FragmentListPageAdapter.POSITION_UNCHANGED;
        }

        /**
         * 获取position位置上的Fragment，Fragment没有被创建时返回null
         *
         * @param position
         * @return
         */
        public Fragment getExitFragment(int position) {
            return pagerAdapter.getExitFragment(position);
        }

        /**
         * 获取当前显示的Fragment
         *
         * @return
         */
        public Fragment getCurrentFragment() {
            return pagerAdapter.getCurrentFragment();
        }

        public abstract int getCount();

        public abstract View getViewForTab(int position, View convertView, ViewGroup container, int
                currentSelectedIndex);

        public abstract Fragment getFragmentForPage(int position);

        public float getPageRatio(int position) {
            return 1f;
        }

        @Override
        public void notifyDataSetChanged() {
            indicatorAdapter.notifyDataSetChanged();
            pagerAdapter.notifyDataSetChanged();
        }

        @Override
        public PagerAdapter getPagerAdapter() {
            return pagerAdapter;
        }

        @Override
        public Indicator.IndicatorAdapter getIndicatorAdapter() {
            return indicatorAdapter;
        }

    }
}
