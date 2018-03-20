package com.example.administrator.gtd;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

public class AlarmActivity extends AppCompatActivity {

    private TextView alarmText;
    private Button do_it_now;
    private Button do_it_later;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Intent intent=getIntent();
        alarmText=(TextView) findViewById(R.id.alarm_text);
        do_it_now=(Button) findViewById(R.id.do_it_now);
        do_it_later=(Button) findViewById(R.id.do_it_later);

        final String content=intent.getStringExtra("content1");
        final String num=intent.getStringExtra("num1");
       // Log.d("c",content);

        alarmText.setText(content);

        Log.d("alarmContent=",content);
        do_it_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues value=new ContentValues();
                value.put("isDone",true);
                //DataSupport.updateAll(Content.class,value,"msg=?",content);
                DataSupport.updateAll(Content.class,value,"num=?",num);
            }
        });
    }
}
