package com.example.administrator.gtd;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    private TextView alarmText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Intent intent=getIntent();
        alarmText=(TextView) findViewById(R.id.alarm_text);
        String content=intent.getStringExtra("content1");
       // Log.d("c",content);

        alarmText.setText(content);
    }
}
