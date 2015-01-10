package com.vsrstudio.tom.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;
import com.vsrstudio.tom.R;
import com.vsrstudio.tom.StopTomDialog;
import com.vsrstudio.tom.Tom;

public class MainActivity extends BaseActivity implements Tom.Pomodorable,
        View.OnClickListener,
        StopTomDialog.StopTomDialogListener {

    private HoloCircularProgressBar mProgressBar;
    private ImageView mProcessImage;
    private TextView mTimeText;
    private TextView mProcessText;
    private View mMainSquare;

    private Tom mTimer;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectToService();
        initializeComponents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectFromService();
    }

    private void connectToService() {
        final Intent timerIntent = new Intent(this, Tom.class);
        startService(timerIntent);
        bindService(timerIntent, connection, BIND_ABOVE_CLIENT);
    }

    private void disconnectFromService() {
        if (mTimer != null) mTimer.removePomodoroListener();
        if (!bound) return;
        final Intent timerIntent = new Intent(this, Tom.class);
        if (!mTimer.isCounting) stopService(timerIntent);
        unbindService(connection);
        bound = false;
    }

    private void initializeComponents() {
        mProgressBar.setProgressColor(getResources().getColor(R.color.white));
        mProgressBar.setProgressBackgroundColor(getResources().getColor(R.color.transparent));
        mProgressBar.setProgress(1f);

        mMainSquare.setOnClickListener(this);
    }

    private void findComponents() {
        mProgressBar = (HoloCircularProgressBar) findViewById(R.id.progress_bar);
        mProcessImage = (ImageView) findViewById(R.id.image);
        mTimeText = (TextView) findViewById(R.id.time);
        mProcessText = (TextView) findViewById(R.id.status);
        mMainSquare = findViewById(R.id.main_square);
    }

    @Override
    public void onClick(View v) {
        mTimer.buttonClicked(this);
    }

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mTimer = ((Tom.TimerBinder) binder).getService();
            mTimer.setPomodoroListener(MainActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void workStarted() {
        mProcessImage.setVisibility(View.VISIBLE);
        mProcessImage.setImageResource(R.drawable.ic_work);

        mTimeText.setVisibility(View.VISIBLE);

        mProcessText.setVisibility(View.VISIBLE);
        mProcessText.setText(R.string.working);
    }

    @Override
    public void workFinished() {
        mProgressBar.setProgress(1f);

        mProcessImage.setVisibility(View.GONE);

        mTimeText.setVisibility(View.GONE);

        mProcessText.setVisibility(View.VISIBLE);
        mProcessText.setText(R.string.start_break);
    }

    @Override
    public void breakStarted() {
        mProcessImage.setVisibility(View.VISIBLE);
        mProcessImage.setImageResource(R.drawable.ic_break);

        mTimeText.setVisibility(View.VISIBLE);

        mProcessText.setVisibility(View.VISIBLE);
        mProcessText.setText(R.string.relaxing);
    }

    @Override
    public void breakFinished() {
        mProgressBar.setProgress(1f);

        mProcessImage.setVisibility(View.GONE);

        mTimeText.setVisibility(View.GONE);

        mProcessText.setVisibility(View.VISIBLE);
        mProcessText.setText(R.string.start_working);
    }

    @Override
    public void setTime(final String timeText, final float progress) {
        mTimeText.setText(timeText);
        mProgressBar.setProgress(progress);
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.click_back_again, Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onStopTomClick() {
        mTimer.onStopTomClick();
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
