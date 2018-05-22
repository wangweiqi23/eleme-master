package com.wwq.eleme.view.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayoutScrollBehavior;
import android.support.design.widget.AppBarLayoutSpringBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.wwq.eleme.R;

/**
 * Created by alexwangweiqi on 18/3/23.
 */

public class PageActivity extends AppCompatActivity implements AppBarLayoutScrollBehavior
        .OnAppBarListener{

    AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_spring);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppBarLayout = findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setBehavior(new AppBarLayoutSpringBehavior(this));
        mAppBarLayout.setLayoutParams(params);

    }

    @Override
    public void onTitleBarItemClick(int id) {

    }

    @Override
    public void initEnd(int miniTopHeight) {
        findViewById(R.id.tvLoading).setVisibility(View.GONE);
    }

}
