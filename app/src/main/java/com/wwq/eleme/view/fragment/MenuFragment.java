package com.wwq.eleme.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wwq.eleme.R;
import com.wwq.eleme.mode.Goods;
import com.wwq.eleme.view.adapter.GoodsAdapter;
import com.wwq.eleme.view.adapter.LeftAdapter;
import com.wwq.eleme.view.adapter.NiceGoodsAdapter;
import com.wwq.eleme.view.listener.MenuAppBarStateChangeListener;
import com.wwq.eleme.view.widget.nested.NestedViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexwangweiqi on 18/3/28.
 */

public class MenuFragment extends Fragment {

    List<Goods> mModelList;
    List<Goods.SubModel> mSubModelList;

    CoordinatorLayout mMenuCoordinatorLayout;
    LinearLayout mGoodsMenuLayout;

    RecyclerView mNiceGoodsRecyclerView;

    RecyclerView mLeftRecyclerView;
    RecyclerView mGoodsRecyclerView;

    AppBarLayout mAppBarLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        findView(rootView);
        initView();
        initAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initAdapter() {

    }

    private void initView() {
        /**
         * State.EXPANDED 展开状态
         * State.COLLAPSED 折叠状态
         * 其他中间状态
         */
        mAppBarLayout.addOnOffsetChangedListener(new MenuAppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                NestedViewPager.sIsMenuExpand = (state == State.EXPANDED);
            }
        });
        mLeftRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLeftRecyclerView.setAdapter(new LeftAdapter(this.getContext(), mModelList));

        mGoodsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGoodsRecyclerView.setAdapter(new GoodsAdapter(this.getContext(), mSubModelList));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mNiceGoodsRecyclerView.setLayoutManager(layoutManager);
        mNiceGoodsRecyclerView.setAdapter(new NiceGoodsAdapter(this.getContext(), mSubModelList
                .subList(0, 7)));
        mNiceGoodsRecyclerView.setNestedScrollingEnabled(false);

    }

    private void findView(View rootView) {
        mAppBarLayout = rootView.findViewById(R.id.appbar_layout);
        mMenuCoordinatorLayout = rootView.findViewById(R.id.menu_layout);
        mGoodsMenuLayout = rootView.findViewById(R.id.goods_menu_layout);
        mNiceGoodsRecyclerView = rootView.findViewById(R.id.nice_goods_list);
        mLeftRecyclerView = rootView.findViewById(R.id.left_list);
        mGoodsRecyclerView = rootView.findViewById(R.id.goods_list);

        ((TextView)rootView.findViewById(R.id.tv_goods_list_tip)).setText(Html.fromHtml(getResources().getString(R.string.goods_list_tip)));
    }

    private void initData() {

        mModelList = Goods.initData();

        mSubModelList = new ArrayList<>();

        for (Goods m : mModelList) {
            for (Goods.SubModel sm : m.getSubModelList()) {
                sm.setcId(m.getcId());
                sm.setcName(m.getcName());
                mSubModelList.add(sm);
            }
        }
    }
}
