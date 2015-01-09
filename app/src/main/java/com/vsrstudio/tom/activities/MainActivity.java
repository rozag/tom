package com.vsrstudio.tom.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;
import com.vsrstudio.tom.R;
import com.vsrstudio.tom.Timer;

public class MainActivity extends BaseActivity implements Timer.Pomodorable, View.OnClickListener {

    private HoloCircularProgressBar mProgressBar;
    private ImageView mProcessImage;
    private TextView mTimeText;
    private TextView mProcessText;
    private View mMainSquare;

    private Timer mTimer;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {              // TODO Написать сам таймер - при старте активити подключаемся к сервису, начинаем брать инфу.
        super.onCreate(savedInstanceState);
        findComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectToService();
        initializeComponents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectFromService();
    }

    private void connectToService() {
        Intent timerIntent = new Intent(this, Timer.class);
        startService(timerIntent);
        bindService(timerIntent, connection, BIND_ABOVE_CLIENT);
    }

    private void disconnectFromService() {
        mTimer.removeUISlave();
        if (!bound) return;
        unbindService(connection);
        bound = false;
    }

    private void initializeComponents() {
        mProgressBar.setProgressColor(getResources().getColor(R.color.white));
        mProgressBar.setProgressBackgroundColor(getResources().getColor(R.color.transparent));

        mMainSquare.setOnClickListener(this);
    }

    private void findComponents() {
        mProgressBar = (HoloCircularProgressBar) findViewById(R.id.progress_bar);
        mProcessImage = (ImageView) findViewById(R.id.image);
        mTimeText = (TextView) findViewById(R.id.time);
        mProcessText = (TextView) findViewById(R.id.status);
        mMainSquare = findViewById(R.id.main_square);
    }

    private boolean workIcon = true; // TODO remove

    @Override
    public void onClick(View v) {
        if (workIcon) {
            mProcessImage.setImageResource(R.drawable.ic_break);
            workIcon = false;
        } else {
            mProcessImage.setImageResource(R.drawable.ic_work);
            workIcon = true;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mTimer = ((Timer.TimerBinder) binder).getService();
            mTimer.setUISlave(MainActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void workStarted() {
        // TODO
    }

    @Override
    public void workStopped() {
        // TODO
    }

    @Override
    public void breakStarted() {
        // TODO
    }

    @Override
    public void breakStopped() {
        // TODO
    }

    @Override
    public void setTime(String time) {

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
