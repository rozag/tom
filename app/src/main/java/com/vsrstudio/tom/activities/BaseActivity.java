package com.vsrstudio.tom.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.vsrstudio.tom.R;

public abstract class BaseActivity extends ActionBarActivity {

    protected Toolbar mToolBar;
    protected Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceIdentifier());

        loadComponents();
        loadInfoView();
        initializeToolBar();
    }

    private void loadComponents() {
        mContext = getApplicationContext();
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
    }


    private void loadInfoView() {
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }
    }

    protected void initializeToolBar() {
        if (mToolBar != null) {
            mToolBar.setTitle(getTitleToolBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(getDisplayHomeAsUp());
            getSupportActionBar().setHomeButtonEnabled(getHomeButtonEnabled());
        }
    }

    protected abstract int getLayoutResourceIdentifier();

    protected abstract String getTitleToolBar();

    protected abstract boolean getDisplayHomeAsUp();

    protected abstract boolean getHomeButtonEnabled();

}
