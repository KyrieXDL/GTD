package com.example.administrator.gtd;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
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
        final int num=Integer.parseInt(intent.getStringExtra("num1"));
       // Log.d("c",content);

        alarmText.setText(content);
        do_it_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues value=new ContentValues();
                value.put("isdone",true);
                //DataSupport.updateAll(Content.class,value,"msg=?",content);
                //发送请求更新服务端数据
                String updateurl="http://120.79.7.33/update3.php?contentid="+num;
                new MyUpdateTask().execute(updateurl);
                //DataSupport.update(Content.class,value,num);
                //更新本地数据
                DataSupport.updateAll(Content.class,value,"contentid=?",num+"");
            }
        });
    }

    class MyUpdateTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                int res=object.getInt("res");
                if (res==0){
                    //Toast.makeText(MainActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
