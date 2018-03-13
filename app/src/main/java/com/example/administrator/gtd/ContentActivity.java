package com.example.administrator.gtd;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ContentActivity extends AppCompatActivity implements View.OnClickListener{

    private  EditText text;
    private TextView time;
    private Date date;
    private String str_time;

    private RelativeLayout selectTime;
    private TextView currentTime;
    private CustomDatePicker customDatePicker;

    private String alarmTime;
    private int activityName;

    private int number;// 事件的number
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        Toolbar toolbar=(Toolbar) findViewById(R.id.contentToolBar);
        setSupportActionBar(toolbar);
        text=(EditText) findViewById(R.id.edit_text);
        time=(TextView) findViewById(R.id.time);
        date=new Date(System.currentTimeMillis());
        str_time=date.toLocaleString();
        time.setText(str_time);

        selectTime = (RelativeLayout) findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
        currentTime = (TextView) findViewById(R.id.currentTime);  //提醒时间

        Intent intent=getIntent();
        String data=intent.getStringExtra("content0");
        String datatime=intent.getStringExtra("time0");    //事件创立时间
        String timetemp=intent.getStringExtra("alarmtime0");
        number=intent.getIntExtra("num0",0);

        Log.d("ContentActivity===","onCreate");

        activityName=intent.getIntExtra("activityName",0);
        text.setText(data);
        if(activityName==1){
            time.setText(datatime);
            //currentTime.setText(timetemp);
        }
        if (activityName==1){
            currentTime.setText(timetemp);
        }else{
            initDatePicker();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectTime:
                // 日期格式为yyyy-MM-dd HH:mm
                customDatePicker.show(currentTime.getText().toString());
                break;
        }
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        currentTime.setText(now);
        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentTime.setText(time);
                alarmTime=time;
            }
        }, "2010-01-01 00:00", "2050-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.showSpecificTime(true); // 显示时和分
        customDatePicker.setIsLoop(true); // 允许循环滚动
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.backup:
               /* Intent intent=new Intent(ContentActivity.this,MainActivity.class);
                startActivity(intent);*/
               /*返回到主界面
               * */
               finish();
                break;
            case R.id.save:
                //设置新建时间的时间
                date=new Date(System.currentTimeMillis());
                str_time=date.toLocaleString();
                time.setText(str_time);
                Long currentTime=System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String now = sdf.format(new Date());

                //将数据保存到数据库中，并在主界面添加事件
                Content contentTemp=new Content("",false,false);
                contentTemp.setMsg(text.getText().toString());
                contentTemp.setTime(now);
                contentTemp.setAlarmTime(alarmTime);
                contentTemp.setNum(number);
                contentTemp.save();

                //将事件标题显示在主界面上
                //String content=text.getText().toString();
                Intent intent=new Intent();
                intent.putExtra("content",contentTemp.getMsg());
                intent.putExtra("name",contentTemp.getName());
                intent.putExtra("time",now);
                intent.putExtra("alarmTime",alarmTime);
                intent.putExtra("currentTime",currentTime);

                setResult(RESULT_OK,intent);   //回调mainActivity中的onActivityResult方法
                Toast.makeText(ContentActivity.this,"save success",Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                /*
                * 删除当前事件*/
                break;
            default:

        }
        return true;
    }
}
