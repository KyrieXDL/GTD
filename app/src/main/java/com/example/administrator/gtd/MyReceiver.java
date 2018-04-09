package com.example.administrator.gtd;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        String content=intent.getStringExtra("content1");
        int num=Integer.parseInt(intent.getStringExtra("num1"));
        Log.d("alarmContent=",content);
        intent.setClass(context, AlarmActivity.class);
        PendingIntent pi=PendingIntent.getActivity(context,num,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager=(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // create the instance of Notification
        Notification notification=new NotificationCompat.Builder(context)
                .setContentTitle("有一件事情需要处理")
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        /* set the sound of the alarm. There are two way of setting the sound */
        // n.sound=Uri.parse("file:///sdcard/alarm.mp3");
        //n.sound= Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "20");
        // Post a notification to be shown in the status bar
        manager.notify(num, notification);
    }
}
