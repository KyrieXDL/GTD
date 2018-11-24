package com.example.administrator.gtd;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContentActivity extends AppCompatActivity implements View.OnClickListener, ThemeManager.OnThemeChangeListener{

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

    /*
    * 如果当前是修改事件而不是新建事件
    * data，timetemp，nextContentFromAdapter用于存放数据（未修改前）*/
    private String data;  //事件的内容
    private String timetemp;  //事件的提醒时间
    private String nextContentFromAdapter;  //事件的nextContent

    private String nextContent="nothing";
    private Content content;
    private CardView card_text;
    private CardView card_set;
    private int level=1; //content的重要等级
    private int isSave=0;

    private UnfoldButton unfoldButton;
    private ActionBar supportActionBar;
    private LinearLayout linearLayout;
    private LinearLayout selectNextContent;
    private LinearLayout selectNextContent_hint;
    private LinearLayout selectTime_hint;
    private LinearLayout dialog_save;

    private int userid;
    private int mode=0;  //当前主题模式，0为为日间模式，1为夜间模式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.content);
        ThemeManager.registerThemeChangeListener(this);



        /*
        * 初始化控件*/
        iniView();

        //通过intent获取数据
        Intent intent=getIntent();
        data=intent.getStringExtra("content0");
        String datatime=intent.getStringExtra("time0");    //事件创立时间
        timetemp=intent.getStringExtra("alarmtime0");
        number=intent.getIntExtra("num0",0);
        numFromContentActivity=intent.getIntExtra("numFromContentActivity",0);
        nextContentFromAdapter=intent.getStringExtra("nextContentFromAdapter");
        activityName=intent.getIntExtra("activityName",0);  //区分是哪个activity
        text.setText(data);  //设置事件的内容
        mode=intent.getIntExtra("mode",0);
        userid=intent.getIntExtra("userid",0);

        //Toast.makeText(this, ""+numFromContentActivity, Toast.LENGTH_SHORT).show();

        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
        }

        //获取content实例 , 并初始化strList数组
        if (activityName==1){  //1表示当前是有ContentAdapter跳转的
            List<Content> tempList1=DataSupport.where("msg=?",data).find(Content.class);
            content=tempList1.get(0);
            initSpinnerList(content);
            level=content.getLevel();   //当前为查看事件时，level复制为当前事件已设置的level

        }else{
            initStrList(strList);
        }

        spinner=(Spinner) findViewById(R.id.spinner);
        //strList=intent.getStringArrayListExtra("list");
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,strList);

        //设置下拉框的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
        }else{
            spinner.setSelection(0, true);
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

        //第一个是菜单图标  第二个是菜单背景颜色  第三个是点击回调
        unfoldButton.addElement(R.drawable.level3,ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.colorAccent), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里写菜单的点击事件
                card_text.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.colorAccent)));
                card_set.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.colorAccent)));
                level=3;
            }
        });
        unfoldButton.addElement(R.drawable.level2,ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color5), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里写菜单的点击事件
                card_text.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color5)));
                card_set.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color5)));
                level=2;
            }
        });
        unfoldButton.addElement(R.drawable.level1,ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.yellow), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里写菜单的点击事件
                card_text.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.yellow)));
                card_set.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.yellow)));
                level=1;
            }
        });
        unfoldButton.setAngle(90);//这个是展开的总角度  建议取90的倍数
        unfoldButton.setmScale(1);//设置弹出缩放的比例  1为不缩放 范围是0—1
        unfoldButton.setLength(250);//设置弹出的距离

        //初始化主题
        initTheme();
    }

    public void iniView(){
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
        card_text=(CardView) findViewById(R.id.card_text);
        card_set=(CardView) findViewById(R.id.card_set);
        linearLayout=(LinearLayout) findViewById(R.id.content_background);
        selectNextContent=(LinearLayout) findViewById(R.id.view4);
        unfoldButton = (UnfoldButton) findViewById(R.id.unfoldButton);
        selectNextContent_hint=(LinearLayout) findViewById(R.id.view3);
        selectTime_hint=(LinearLayout) findViewById(R.id.view2);
        supportActionBar = getSupportActionBar();

        View view_save = View.inflate(this, R.layout.dialog_save, null);
        dialog_save=(LinearLayout) view_save.findViewById(R.id.dialog_save);
    }

    @Override
    public void onThemeChanged() {
        initTheme();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeManager.unregisterThemeChangeListener(this);
    }

    public void initTheme(){
        // 设置标题栏颜色
        if(supportActionBar != null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.colorPrimary))));
        }
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.colorPrimary)));
        }

        unfoldButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.colorAccent))));

        //只有当不是新建状态时才根据level设置content的背景色，否则都初始化为淡黄色
        if (activityName==1) {
            //根据content的level值设置背景色
            if (content.getLevel() == 3) {
                card_text.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.colorAccent)));
                card_set.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.colorAccent)));
            } else if (content.getLevel() == 2) {
                card_text.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color5)));
                card_set.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color5)));

            } else if (content.getLevel() == 1) {
                card_text.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.yellow)));
                card_set.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.yellow)));
            }
        }else{
            card_text.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.yellow)));
            card_set.setCardBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.yellow)));
        }


        linearLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.backgroundColor)));
        selectTime.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color2)));
        spinner.setPopupBackgroundResource(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.background));
        selectNextContent.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color2)));
        selectNextContent_hint.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color1)));
        selectTime_hint.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.color1)));

        GradientDrawable p = (GradientDrawable) dialog_save.getBackground();
        p.setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ContentActivity.this, R.color.backgroundColor)));

    }


    private void initSpinnerList(Content content){
        strList.clear();
        strList.add("nothing");
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        for(int i=0;i<newList.size();i++){
            //未被其他事件设置为nextContent
            // 或则是当前事件的nextContent的事件可以被添加到strList，并传给ContentActivity
            if ((ContentHelper.getPreContent(newList,newList.get(i).getMsg()).equals(""))||(ContentHelper.getPreContent(newList,newList.get(i).getMsg()).equals(content.getMsg()))){
                String timeDifference=ContentHelper.getTimeBetweenContents(content.getAlarmTime(),newList.get(i).getAlarmTime());
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
            if (ContentHelper.getPreContent(newList,newList.get(i).getMsg()).equals("")){
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
            if ((ContentHelper.getPreContent(newList,newList.get(i).getMsg()).equals(""))||(ContentHelper.getPreContent(newList,newList.get(i).getMsg()).equals(content.getMsg()))){
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
        ArrayList<String> list2=ContentHelper.removeEarlyContent(time,list);
        for (int i=0;i<list2.size();i++){
            strList.add(list2.get(i));
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();
        //获取nextcontent的实例
       if (!nextContent.equals("nothing")){
            List<Content> tempList1=DataSupport.where("msg=?",nextContent).find(Content.class);
            Content content=tempList1.get(0);
           if(Long.parseLong(ContentHelper.getTimeBetweenContents(time,content.getAlarmTime()))<0){
               spinner.setSelection(0,true);
           }else{
               spinner.setSelection(strList.indexOf(nextContent),true);
           }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }


    public void setReminder(boolean b,Long time,String content,int num,int level) {
        // get the AlarmManager instance

        SharedPreferences sharedPreferences=getSharedPreferences("music_data",MODE_PRIVATE);
        int music=sharedPreferences.getInt("music",0);
        int playMusic=sharedPreferences.getInt("play_music",0);
        int isShake=sharedPreferences.getInt("isShake",0);
        AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);
        // create a PendingIntent that will perform a broadcast
        Intent intent=new Intent(ContentActivity.this,MyReceiver.class);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        intent.putExtra("content1",content); //发送广播的同时，将事件的内容传给receiver，当点击通知时显示在界面上
        intent.putExtra("num1",num+"");  //num为每个事件唯一标号
        intent.putExtra("level",level);
        intent.putExtra("play_music",playMusic);
        intent.putExtra("music",music);
        intent.putExtra("isShake",isShake);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            //如果当前是修改事件，则判断是否有数据被修改
            if (activityName==1){
                if((!data.equals(text.getText().toString())||(!timetemp.equals(alarmTime)))||(!nextContentFromAdapter.equals(nextContent))
                        ||(level!=content.getLevel())){
                    quitAlarm();
                }else{

                    finish();
                }
            }else{
                //如果当前为新建事件
                //如果用户已经输入内容
                if(!text.getText().toString().equals("")){
                    //如果用户没有保存，则进行询问是否要退出
                    if (isSave!=1){
                        quitAlarm();
                    }else{
                        //如果用户已经保存，则直接退出
                        //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ContentActivity.this, linearLayout, "sharedView").toBundle());

                        finish();
                    }
                    //如果用户新建事件时没有输入内容则直接退出
                }else{
                    finish();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.backup:
               /* Intent intent=new Intent(ContentActivity.this,MainActivity.class);
                startActivity(intent);*/
               /*返回到主界面
               * */
               //如果当前是修改事件，则判断是否有数据被修改
               if (activityName==1){
                   if((!data.equals(text.getText().toString())||(!timetemp.equals(alarmTime)))||(!nextContentFromAdapter.equals(nextContent))
                           ||(level!=content.getLevel())){
                       quitAlarm();
                   }else{
                       finish();
                   }
               }else{
               //如果当前为新建事件
                   //如果用户已经输入内容
                   if(!text.getText().toString().equals("")){
                       //如果用户没有保存，则进行询问是否要退出
                       if (isSave!=1){
                           quitAlarm();
                       }else{
                       //如果用户已经保存，则直接退出
                           finish();
                       }
                   //如果用户新建事件时没有输入内容则直接退出
                   }else{
                       finish();
                   }
               }
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

                    //当用户所输入的内容不为空时，执行保存操作
                    String msg=text.getText().toString();
                    if (!text.getText().toString().equals("")){
                        //将数据保存到数据库中，并在主界面添加事件
                        Content contentTemp=new Content("",false,false);
                        contentTemp.setMsg(text.getText().toString());
                        contentTemp.setTime(now);

                        if (!alarmTime.equals(now)){
                            contentTemp.setAlarmTime(alarmTime);}
                        else{
                            contentTemp.setAlarmTime(now);  //如果未设置提醒时间则初始化为当前事件
                        }
                        //contentTemp.setNum(number);
                        contentTemp.setDone(false);
                        contentTemp.setNextContent(nextContent);
                        contentTemp.setLevel(level);

                        try {
                            //发送网络请求，保存数据到服务端的数据库中
                            String url = "http://120.79.7.33/insert.php";
                            new MyTask().execute(url,msg,now,contentTemp.getAlarmTime(),level+"",nextContent,userid+"");
                            contentTemp.save();

                            /*
                              * 如果用户在创立事件时设置提醒时间，则发送广播*/
                            String temptime=ContentHelper.getTimeBetweenContents(str_time,alarmTime);  //事件创立时间与事件提醒时间的相差时间
                            if(!alarmTime.equals(now)) {
                                // setResult(RESULT_OK, intent);   //回调mainActivity中的onActivityResult方法
                                setReminder(true,Integer.parseInt(temptime)+currentTime,text.getText().toString(),numFromContentActivity,level);
                            }
                            isSave=1;
                            finish();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(this, getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                    //当用户所输入的内容为空提醒用户，但不保存
                        saveAlarm();
                    }
                }else if(activityName==1){  //由ContentAdapter跳转到当前活动则进行修改更新数据
                    /*最新修改 2018-3-13 11.20*/
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                    String nowtemp = sdf.format(new Date());
                    Long currentTime=System.currentTimeMillis();
                    String temptime=ContentHelper.getTimeBetweenContents(nowtemp,alarmTime);  //事件创立时间与事件提醒时间的相差时间

                    ContentValues values=new ContentValues();
                    values.put("msg",text.getText().toString());
                    values.put("alarmtime",alarmTime);
                    if (Long.parseLong(temptime)>=0) {
                        values.put("isdone", false);
                    }
                    values.put("buildtime",nowtemp);
                    values.put("nextcontent",nextContent);
                    values.put("level",level);
                    time.setText(nowtemp);

                    //如果已经保存修改，则更新数据
                    data=text.getText().toString();
                    timetemp=alarmTime;
                    nextContentFromAdapter=nextContent;
                    level=content.getLevel();

                    /*Log.d("alarmContent=",text.getText().toString());
                    Log.d("numFromContentActivity",numFromContentActivity+"");*/
                    //DataSupport.updateAll(Content.class,values,"num=?",numFromContentActivity+"");

                    int id=numFromContentActivity;
                    String msg=text.getText().toString();

                    try {
                        //发送网络请求，更新服务端的数据
                        String url = "http://120.79.7.33/update.php";
                        new MyUpdateTask().execute(url, msg, nowtemp, alarmTime, level + "", nextContent, id + "");
                        //更新本地数据
                        DataSupport.updateAll(Content.class,values,"contentid=?",id+"");
                        if (Long.parseLong(temptime) >= 0) {
                            setReminder(true, Integer.parseInt(temptime) + currentTime, text.getText().toString(), numFromContentActivity,level);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //Toast.makeText(ContentActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    //finish();
                }
                break;

            default:

        }
        return true;
    }

    //异步任务发送网络请求
    class MyTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttp(params[0],params[1],params[2],params[3],params[4],params[5],params[6]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(ContentActivity.this, s, Toast.LENGTH_SHORT).show();

            try {
                JSONObject object = new JSONObject(s);
                int res=object.getInt("res");
                if (res==1){
                    Toast.makeText(ContentActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ContentActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class MyUpdateTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttp(params[0],params[1],params[2],params[3],params[4],params[5],params[6]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(ContentActivity.this, s, Toast.LENGTH_SHORT).show();

            try {
                JSONObject object = new JSONObject(s);
                int res=object.getInt("res");
                if (res==1){
                    Toast.makeText(ContentActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ContentActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    private void quitAlarm() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_normal, null);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        TextView alarmContent = (TextView) view.findViewById(R.id.alarm_content);
        alarmContent.setText(getResources().getText(R.string.areYouSureToQuit));
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void saveAlarm() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_save, null);
        TextView confirm = (TextView) view.findViewById(R.id.yes);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
