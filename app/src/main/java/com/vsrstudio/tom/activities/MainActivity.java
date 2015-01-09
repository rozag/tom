package com.vsrstudio.tom.activities;

import android.os.Bundle;

import com.vsrstudio.tom.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Some actions
    }

    @Override
    protected int getLayoutResourceIdentifier() {
        return R.layout.activity_main;
    }

    @Override
    protected String getTitleToolBar() {
        return String.valueOf(getTitle());
    }

    @Override
    protected boolean getDisplayHomeAsUp() {
        return false;
    }

    @Override
    protected boolean getHomeButtonEnabled() {
        return false;
    }

}
