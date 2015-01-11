package com.vsrstudio.tom;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.vsrstudio.tom.activities.MainActivity;

import java.io.IOException;

public class Notificator {

    public static Notification with(final Context context) {
        return new Notification(context);
    }

    public static class Notification {

        private final Context mContext;
        private final int notificationId = 8; // some number from my head

        private Notification(final Context context) {
            mContext = context;
        }

        public void sendNotification(final String title, final String text, final int smallIconId, final int largeIconId) {
            final Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long[] pattern = {500, 500};

            final NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext)
                            .setSmallIcon(smallIconId)
                            .setLargeIcon(getLargeIconBitmap(largeIconId))
                            .setAutoCancel(true)
                            .setSound(sound)
                            .setOngoing(false)
                            .setVibrate(pattern)
                            .setLights(Color.RED, 500, 2000)
                            .setContentTitle(title)
                            .setContentText(text);

            sendNotification(mBuilder);
        }

        public void removeNotification() {
            final NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationId);
        }

        public void updateTimeNotification(final String title, final String text, final int smallIconId, final int largeIconId) {
            final NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext)
                            .setSmallIcon(smallIconId)
                            .setLargeIcon(getLargeIconBitmap(largeIconId))
                            .setOngoing(true)
                            .setContentTitle(title)
                            .setContentText(text);

            sendNotification(mBuilder);
        }

        private void sendNotification(final NotificationCompat.Builder mBuilder) {
            final Intent resultIntent = new Intent(mContext, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            final NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationId, mBuilder.build());
        }

        public void playSoundAndVibrate() {
            final Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            try {
                mediaPlayer.setDataSource(mContext, sound);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        final Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);

                        mp.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Bitmap getLargeIconBitmap(final int largeIconId) {
            BitmapDrawable icon = (BitmapDrawable) mContext.getResources().getDrawable(largeIconId);
            return icon.getBitmap();
        }

    }

}
