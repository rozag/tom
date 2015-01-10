package com.vsrstudio.tom;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

public class Tom extends Service {

    public State mCurrentState = State.BREAK_FINISHED;
    private Pomodorable pomodoroListener = null;
    private TimerBinder mBinder = new TimerBinder();
    private PomodoroCountDownTimer mTimer = null;

    private final long WORK_TIME = 25 * 60 * 1000; // 25 minutes
    private final long BREAK_TIME = 5 * 60 * 1000; // 5 minutes
    private final long LONG_BREAK_TIME = 15 * 60 * 1000; // 15 minutes

//    Dev constants
//    private final long WORK_TIME = 10 * 1000;
//    private final long BREAK_TIME = 10 * 1000;
//    private final long LONG_BREAK_TIME = 20 * 1000;

    public boolean isCounting = false;
    private int pomodorosCount = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void buttonClicked(final Activity activity) {
        switch (mCurrentState) {
            case WORK:
            case BREAK:
            case LONG_BREAK:
                final DialogFragment dialog = new StopTomDialog();
                dialog.show(activity.getFragmentManager(), "StopTimerDialog");
                break;
            case WORK_FINISHED:
                if (pomodorosCount == 4) {
                    pomodorosCount = 0;
                    startLongBreak();
                } else {
                    startBreak();
                }
                break;
            case BREAK_FINISHED:
                startWork();
                break;
        }
    }

    public class TimerBinder extends Binder {
        public Tom getService() {
            return Tom.this;
        }
    }

    public void setPomodoroListener(final Pomodorable listener) {
        pomodoroListener = listener;

        switch (mCurrentState) {
            case WORK:
                pomodoroWorkStarted();
                break;
            case BREAK:
            case LONG_BREAK:
                pomodoroBreakStarted();
                break;
            case WORK_FINISHED:
                pomodoroWorkFinished();
                break;
            case BREAK_FINISHED:
                pomodoroBreakFinished();
                break;
        }
    }

    public void removePomodoroListener() {
        pomodoroListener = null;
    }

    // called after every phase
    private void phaseFinished() {
        switch (mCurrentState) {
            case WORK:
                pomodorosCount++;
                mCurrentState = State.WORK_FINISHED;
                pomodoroWorkFinished();
                break;
            case BREAK:
            case LONG_BREAK:
                mCurrentState = State.BREAK_FINISHED;
                pomodoroBreakFinished();
                break;
        }
    }

    private void startWork() {
        if (mTimer != null) mTimer.cancel();
        mTimer = new PomodoroCountDownTimer(WORK_TIME);
        mCurrentState = State.WORK;
        mTimer.start();
        isCounting = true;
        pomodoroWorkStarted();
    }

    private void startBreak() {
        if (mTimer != null) mTimer.cancel();
        mTimer = new PomodoroCountDownTimer(BREAK_TIME);
        mCurrentState = State.BREAK;
        mTimer.start();
        isCounting = true;
        pomodoroBreakStarted();
    }

    private void startLongBreak() {
        if (mTimer != null) mTimer.cancel();
        mTimer = new PomodoroCountDownTimer(LONG_BREAK_TIME);
        mCurrentState = State.LONG_BREAK;
        mTimer.start();
        isCounting = true;
        pomodoroBreakStarted();
    }

    private class PomodoroCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture The number of millis in the future from the call
         *                       to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                       is called.
         */
        public PomodoroCountDownTimer(long millisInFuture) {
            super(millisInFuture, 1000); // 1000 ms is 1 second
        }

        @Override
        public void onTick(final long millisUntilFinished) {
            if (pomodoroListener != null) {
                final long SECOND = 1000;
                final long minutes = millisUntilFinished / (60 * SECOND); // (60 * 1000) is one minute
                final long tmp = millisUntilFinished % (60 * SECOND);
                final long seconds = (tmp <= 6 * SECOND) ? roundSeconds(tmp) / 10 : roundSeconds(tmp);
                final String timeText = rightFormat(minutes) + ":" + rightFormat(seconds);

                float progress = 0f;
                switch (mCurrentState) {
                    case WORK:
                        progress = (float) millisUntilFinished / (float) WORK_TIME;
                        break;
                    case BREAK:
                        progress = (float) millisUntilFinished / (float) BREAK_TIME;
                        break;
                    case LONG_BREAK:
                        progress = (float) millisUntilFinished / (float) LONG_BREAK_TIME;
                        break;
                }

                pomodoroListener.setTime(timeText, progress);
            }
        }

        // Take only 2 left digits (e.g. 12345 -> 12)
        private long roundSeconds(long seconds) {
            final long SECONDS_IN_MINUTE = 60;
            if (seconds < SECONDS_IN_MINUTE) return seconds;
            else return roundSeconds(seconds / 10);
        }

        private String rightFormat(long number) {
            if (number < 10) return "0" + number;
            else return "" + number;
        }

        @Override
        public void onFinish() {
            isCounting = false;
            if (pomodoroListener == null) {
                switch (mCurrentState) {
                    case WORK:
                        Notificator.with(getApplicationContext()).sendNotification(
                                getString(R.string.app_name),
                                getString(R.string.lets_take_brake),
                                R.drawable.ic_notification,
                                R.drawable.ic_notification_large);
                        break;
                    case BREAK:
                    case LONG_BREAK:
                        Notificator.with(getApplicationContext()).sendNotification(
                                getString(R.string.app_name),
                                getString(R.string.lets_work),
                                R.drawable.ic_notification,
                                R.drawable.ic_notification_large);
                        break;
                }
            } else {
                Notificator.with(getApplicationContext()).playSoundAndVibrate();
            }
            phaseFinished();
        }

    }

    // Reset timer
    public void onStopTomClick() {
        mCurrentState = State.BREAK_FINISHED;
        mTimer.cancel();
        isCounting = false;
        pomodorosCount = 0;
        pomodoroBreakFinished();
    }

    public void pomodoroWorkStarted() {
        if (pomodoroListener != null) pomodoroListener.workStarted();
    }

    public void pomodoroWorkFinished() {
        if (pomodoroListener != null) pomodoroListener.workFinished();
    }

    public void pomodoroBreakStarted() {
        if (pomodoroListener != null) pomodoroListener.breakStarted();
    }

    public void pomodoroBreakFinished() {
        if (pomodoroListener != null) pomodoroListener.breakFinished();
    }

    public static interface Pomodorable {
        public void workStarted();

        public void workFinished();

        public void breakStarted();

        public void breakFinished();

        public void setTime(final String time, final float progress);
    }

    // Possible timer states
    public static enum State {
        WORK,
        BREAK,
        LONG_BREAK,
        WORK_FINISHED,
        BREAK_FINISHED
    }

}
