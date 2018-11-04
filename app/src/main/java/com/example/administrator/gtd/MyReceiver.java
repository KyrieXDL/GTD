package com.example.administrator.gtd;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver {

    private SharedPreferences sharedPreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        sharedPreferences=context.getSharedPreferences("music_data",MODE_PRIVATE);
        /*int music=sharedPreferences.getInt("music",0);
        int playMusic=sharedPreferences.getInt("play_music",0);
        int isShake=sharedPreferences.getInt("isShake",0);*/

        int playMusic=intent.getIntExtra("play_music",0);
        int isShake=intent.getIntExtra("isShake",0);
        int music=intent.getIntExtra("music",0);

        String content=intent.getStringExtra("content1");
        int level=intent.getIntExtra("level",0);
        int num=Integer.parseInt(intent.getStringExtra("num1"));
        //Log.d("alarmContent=",content);
        /*intent.putExtra("playmusic",playMusic);
        intent.putExtra("music",music);
        intent.putExtra("isShake",isShake);*/
        intent.setClass(context, AlarmActivity.class);

        PendingIntent pi=PendingIntent.getActivity(context,num,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager=(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // create the instance of Notification
        Notification notification=new NotificationCompat.Builder(context)
                .setContentTitle("有一件事情需要处理")
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.alarm_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.alarm_icon))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        Log.d("fucking","playMusic: "+playMusic+" music: "+music+" isShake: "+isShake);

        if (playMusic==1) {

            if (music == 1) {
                notification.sound = Uri.parse("android.resource://" + context.getPackageName()
                        + "/" + R.raw.music1);
            } else if (music == 2) {
                notification.sound = Uri.parse("android.resource://" + context.getPackageName()
                        + "/" + R.raw.music2);
            } else if (music == 3) {
                notification.sound = Uri.parse("android.resource://" + context.getPackageName()
                        + "/" + R.raw.music3);
            } else if (music == 4) {
                notification.sound = Uri.parse("android.resource://" + context.getPackageName()
                        + "/" + R.raw.music4);
            }
        }else{
            notification.sound=null;
        }

        if (isShake==1){
            long[] vibrate = {100, 200, 300, 400};
            notification.vibrate = vibrate;
        }

        /*if (level==1){
            notification.ledARGB = Color.YELLOW;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
            notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
            notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
            notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，其中就包括显示
        }else if(level==2){
            notification.ledARGB = Color.BLUE;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
            notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
            notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
            notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，其中就包括显示
        }else if (level==3){
            notification.ledARGB = Color.RED;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
            notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
            notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
            notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，其中就包括显示
        }*/

        /* set the sound of the alarm. There are two way of setting the sound */
        // n.sound=Uri.parse("file:///sdcard/alarm.mp3");
        //n.sound= Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "20");
        // Post a notification to be shown in the status bar
        manager.notify(num, notification);
    }
}
