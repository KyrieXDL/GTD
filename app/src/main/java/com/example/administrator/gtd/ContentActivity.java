package com.example.administrator.gtd;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import android.widget.AdapterView.OnItemSelectedListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContentActivity extends AppCompatActivity implements View.OnClickListener{

    private  EditText text;
    private TextView time;
    private String str_time;
    private RelativeLayout selectTime;
    private TextView currentTime;
    private CustomDatePicker customDatePicker;
    private String alarmTime;
    private int activityName;    //区别是哪个activity
    private int number;// 事件的number
    private int numFromContentActivity;
    private String now;  ///当前时间
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> strList=new ArrayList<>();

    private String nextContent="nothing";
    private Content content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);
        /*
        * 初始化控件*/
        Toolbar toolbar=(Toolbar) findViewById(R.id.contentToolBar);
        setSupportActionBar(toolbar);
        text=(EditText) findViewById(R.id.edit_text);
        time=(TextView) findViewById(R.id.time);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        //date=new Date(System.currentTimeMillis());
        str_time=sdf.format(new Date());
        time.setText(str_time);

        selectTime = (RelativeLayout) findViewById(R.id.selectTime);
        selectTime.setOnClickListener(this);
        currentTime = (TextView) findViewById(R.id.currentTime);  //提醒时间

        //通过intent获取数据
        Intent intent=getIntent();
        String data=intent.getStringExtra("content0");
        String datatime=intent.getStringExtra("time0");    //事件创立时间
        String timetemp=intent.getStringExtra("alarmtime0");
        number=intent.getIntExtra("num0",0);
        numFromContentActivity=intent.getIntExtra("numFromContentActivity",0);
        String nextContentFromAdapter=intent.getStringExtra("nextContentFromAdapter");
        activityName=intent.getIntExtra("activityName",0);  //区分是哪个activity
        text.setText(data);  //设置事件的内容

        //获取content实例 , 并初始化strList数组
        if (activityName==1){  //1表示当前是有ContentAdapter跳转的
            List<Content> tempList1=DataSupport.where("msg=?",data).find(Content.class);
            content=tempList1.get(0);
            initSpinnerList(content);
        }else{
            initStrList(strList);
        }

        spinner=(Spinner) findViewById(R.id.spinner);
        //strList=intent.getStringArrayListExtra("list");
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,strList);

        //设置下拉框的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, true);
       // spinner.setVisibility(View.VISIBLE);

        //设置下拉框的选择事件
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                nextContent=(String)spinner.getItemAtPosition(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                nextContent="nothing";
            }
        });

        /*
        * intent来自ContentAdapter时*/
        if(activityName==1){
            time.setText(datatime);  //设置事件创立时间
            spinner.setSelection(strList.indexOf(nextContentFromAdapter),true);
            if (nextContentFromAdapter!=null){
                nextContent=nextContentFromAdapter;
            }else{
                nextContent="nothing";
            }
        }
        if (activityName==1){
            currentTime.setText(timetemp);  //设置提醒时间
            alarmTime=currentTime.getText().toString(); //设置alarmTime的默认值
            customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
                @Override
                public void handle(String time) { // 回调接口，获得选中的时间
                    currentTime.setText(time);
                    alarmTime=time;

                    refreshSpinner(currentTime.getText().toString());
                }
            }, "2010-01-01 00:00", "2050-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
            customDatePicker.showSpecificTime(true); // 显示时和分
            customDatePicker.setIsLoop(true); // 允许循环滚动
        }else{
            initDatePicker();   //初始化时间选择控件
        }
    }

    private void initSpinnerList(Content content){
        strList.clear();
        strList.add("nothing");
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        for(int i=0;i<newList.size();i++){
            //未被其他事件设置为nextContent
            // 或则是当前事件的nextContent的事件可以被添加到strList，并传给ContentActivity
            if ((getPreContent(newList,newList.get(i).getMsg()).equals(""))||(getPreContent(newList,newList.get(i).getMsg()).equals(content.getMsg()))){
                String timeDifference=getTimeDifferenceHour(content.getAlarmTime(),newList.get(i).getAlarmTime());
                //并且若要设置nextcontent，则nextcontent的提醒时间必须要晚于当前的content
                if (Long.parseLong(timeDifference)>0){
                    strList.add(newList.get(i).getMsg());
                }
            }
        }
    }

    private void initStrList(ArrayList<String> strList){
        strList.clear();
        strList.add("nothing");
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        for(int i=0;i<newList.size();i++){
            //未被其他事件设置为nextContent
            // 或则是当前事件的nextContent的事件可以被添加到strList，并传给ContentActivity
            if (getPreContent(newList,newList.get(i).getMsg()).equals("")){
                strList.add(newList.get(i).getMsg());
            }
        }
    }

    private void refreshSpinnerList(ArrayList<String> list,Content content){
        list.clear();
        list.add("nothing");
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        for(int i=0;i<newList.size();i++){
            //未被其他事件设置为nextContent
            // 或则是当前事件的nextContent的事件可以被添加到strList，并传给ContentActivity
            if ((getPreContent(newList,newList.get(i).getMsg()).equals(""))||(getPreContent(newList,newList.get(i).getMsg()).equals(content.getMsg()))){
                list.add(newList.get(i).getMsg());
            }
        }
    }

    private void refreshSpinner(String time){
        /*
                    * 在每次设置完提醒时间后，都将更新spinner的adapter*/
        ArrayList<String> list=new ArrayList<>();
        list.clear();
        if (activityName==1){
            refreshSpinnerList(list,content);
        }else{
            initStrList(list);
        }
        strList.clear();
        strList.add("nothing");
        ArrayList<String> list2=removeEarlyContent(time,list);
        for (int i=0;i<list2.size();i++){
            strList.add(list2.get(i));
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();
        //获取nextcontent的实例
       if (!nextContent.equals("nothing")){
            List<Content> tempList1=DataSupport.where("msg=?",nextContent).find(Content.class);
            Content content=tempList1.get(0);
           if(Long.parseLong(getTimeDifferenceHour(time,content.getAlarmTime()))<0){
               spinner.setSelection(0,true);
           }else{
               spinner.setSelection(strList.indexOf(nextContent),true);
           }
       }
    }

    private String getPreContent(List<Content> listTemp,String str){
        //遍历数据库，返回nextContentdent与str的msg
        for(int i=0;i<listTemp.size();i++){
            if(listTemp.get(i).getNextContent()!=null){
                if (listTemp.get(i).getNextContent().equals(str)){
                    return listTemp.get(i).getMsg();
                }
            }
        }
        return "";
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
        Log.d("Check===","yes");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        now = sdf.format(new Date());
        currentTime.setText(now);
        alarmTime=now;
        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentTime.setText(time);
                alarmTime=time;
                refreshSpinner(currentTime.getText().toString());
            }
        }, "2010-01-01 00:00", "2050-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        //Log.d("initDatrePicker",alarmTime);
        customDatePicker.showSpecificTime(true); // 显示时和分
        customDatePicker.setIsLoop(true); // 允许循环滚动
    }

    //nextcontent的数组中删除提醒时间早于当前提醒时间的content
    public ArrayList<String> removeEarlyContent(String time,ArrayList<String> list1){
        ArrayList<String> listResult=new ArrayList<>();
        for (int i=1;i<list1.size();i++){
            List<Content> tempList=DataSupport.where("msg=?",list1.get(i)).find(Content.class);
            Content content=tempList.get(0);
            String timeDifference=getTimeDifferenceHour(time,content.getAlarmTime());  //事件创立时间与事件提醒时间的相差时间
            if (Long.parseLong(timeDifference)>=0){
                listResult.add(content.getMsg());
                //strList.add(content.getMsg());
                //adapter.notifyDataSetChanged();
            }
        }
        return listResult;
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

    public void setReminder(boolean b,Long time,String content,int num) {
        // get the AlarmManager instance
        AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);
        // create a PendingIntent that will perform a broadcast
        Intent intent=new Intent(ContentActivity.this,MyReceiver.class);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        intent.putExtra("content1",content); //发送广播的同时，将事件的内容传给receiver，当点击通知时显示在界面上
        intent.putExtra("num1",num+"");  //num为每个事件唯一标号
        PendingIntent pi= PendingIntent.getBroadcast(ContentActivity.this, num, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(b){
            // just use current time as the Alarm time.
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis(time);
            // schedule an alarm
            am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
        }
        else{
            // cancel current alarm
            am.cancel(pi);
        }
    }

    //最新修改
    public String getTimeDifferenceHour(String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            String string = Long.toString(diff);
            timeString=string;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;
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
                /*
                * 由MainActivity跳转到当前活动，则进行保存数据*/
                if(activityName!=1){
                    //设置新建时间的时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                    str_time=sdf.format(new Date());
                    time.setText(str_time);
                    Long currentTime=System.currentTimeMillis();

                    //String now = sdf.format(new Date());

                    //将数据保存到数据库中，并在主界面添加事件
                    Content contentTemp=new Content("",false,false);
                    contentTemp.setMsg(text.getText().toString());
                    contentTemp.setTime(now);

                    Log.d("nowAlarmTime",now);
                    Log.d("setAlarmTime",alarmTime);
                    if (!alarmTime.equals(now)){
                    contentTemp.setAlarmTime(alarmTime);}
                    else{
                        contentTemp.setAlarmTime(now);  //如果未设置提醒时间则初始化为当前事件
                    }
                    contentTemp.setNum(number);
                    contentTemp.setDone(false);
                    contentTemp.setNextContent(nextContent);
                    contentTemp.save();

                    /*
                    * 如果用户在创立事件时设置提醒时间，则发送广播*/
                    String temptime=getTimeDifferenceHour(str_time,alarmTime);  //事件创立时间与事件提醒时间的相差时间
                    if(!alarmTime.equals(now)) {
                       // setResult(RESULT_OK, intent);   //回调mainActivity中的onActivityResult方法
                        setReminder(true,Integer.parseInt(temptime)+currentTime,text.getText().toString(),numFromContentActivity);
                    }
                    Toast.makeText(ContentActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                }else if(activityName==1){  //由ContentAdapter跳转到当前活动则进行修改更新数据
                    /*最新修改 2018-3-13 11.20*/
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                    String nowtemp = sdf.format(new Date());
                    Long currentTime=System.currentTimeMillis();
                    String temptime=getTimeDifferenceHour(nowtemp,alarmTime);  //事件创立时间与事件提醒时间的相差时间

                    ContentValues values=new ContentValues();
                    values.put("msg",text.getText().toString());
                    values.put("alarmTime",alarmTime);
                    if (Long.parseLong(temptime)>=0) {
                        values.put("isDone", false);
                    }
                    values.put("time",nowtemp);
                    values.put("nextContent",nextContent);
                    time.setText(nowtemp);

                    Log.d("alarmContent=",text.getText().toString());
                    Log.d("numFromContentActivity",numFromContentActivity+"");
                    DataSupport.updateAll(Content.class,values,"num=?",numFromContentActivity+"");

                    if (Long.parseLong(temptime)>=0){
                        setReminder(true,Integer.parseInt(temptime)+currentTime,text.getText().toString(),numFromContentActivity);
                    }
                    Toast.makeText(ContentActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                }
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
