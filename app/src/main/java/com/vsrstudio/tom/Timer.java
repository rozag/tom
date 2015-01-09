package com.vsrstudio.tom;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class Timer extends Service {

    public State mCurrentState = State.FINISHED_BREAK;
    private Pomodorable uiSlave = null;
    private TimerBinder mBinder = new TimerBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class TimerBinder extends Binder {
        public Timer getService() {
            return Timer.this;
        }
    }

    public void setUISlave(final Pomodorable slave) {
        uiSlave = slave;
    }

    public void removeUISlave() {
        uiSlave = null;
    }

    public static interface Pomodorable {
        public void workStarted();

        public void workStopped();

        public void breakStarted();

        public void breakStopped();

        public void setTime(final String time);
    }

    // Возможные состояния
    public static enum State {
        WORK,
        BREAK,
        LONG_BREAK,
        FINISHED_WORK,
        FINISHED_BREAK
    }

}
