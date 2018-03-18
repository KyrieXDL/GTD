package com.example.administrator.gtd;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public List<Content> list=new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ContentAdapter adapter;
    private Button selectButton;
    private Button deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //适配器
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.conten_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new ContentAdapter(MainActivity.this,list);
        recyclerView.setAdapter(adapter);

        //
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        //初始化删除和全选按钮为不可见
        deleteButton=(Button) findViewById(R.id.delete_button);
        selectButton=(Button) findViewById(R.id.select_all);
        deleteButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);

        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //点击全选按钮的事件
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectButton.getText().toString().equals("全选")){
                    selectButton.setText("全不选");
                    for(int i=0;i<list.size();i++){
                        //LitePal更新数据库 2018.3.7
                        /*Content content=list.get(i);
                        content.setChecked(true);
                        content.updateAll();*/

                        list.get(i).setChecked(true);
                        //list.get(i).setChecked(true);
                        adapter.notifyItemInserted(i);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    selectButton.setText("全选");
                    for(int i=0;i<list.size();i++){
                        //LitePal更新数据库2018.3.7
                       /* Content content=list.get(i);
                        content.setChecked(false);
                        content.updateAll();*/

                        list.get(i).setChecked(false);
                        adapter.notifyItemInserted(i);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });


        //点击删除按钮的事件
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 点击删除按钮后的将事件从数据库删除
                 */
                //DataSupport.deleteAll(Content.class,"isChecked = ?","true");
                //List<Integer> count=new ArrayList<Integer>();


                // DataSupport.deleteAll(Content.class,"num=?",String.valueOf(true));
                for(int i=0;i<list.size();){
                    if(list.get(i).isChecked()){
                        //litepal删除checkBox被选中的数据
                        DataSupport.deleteAll(Content.class,"msg=?",list.get(i).getMsg());

                        list.remove(i);
                        adapter.notifyItemInserted(i);
                        adapter.notifyItemRangeChanged(0,list.size());
                    }else{
                        i++;
                    }
                    //adapter.notifyItemInserted(i);
                }

                adapter.notifyDataSetChanged();
                //当点击删除按钮后“全不选”变为“全选”
                if (selectButton.getText().toString().equals("全不选")){
                    selectButton.setText("全选");
                }
            }
        });


        //注册悬浮窗按钮事件
        FloatingActionButton button=(FloatingActionButton) findViewById(R.id.new_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LitePal.getDatabase();
                Intent intent=new Intent(MainActivity.this,ContentActivity.class);
               // Log.d("numId",list.get(0).getNum()+"");
                intent.putExtra("num0",list.size()+1);
                startActivityForResult(intent,1);
                //setReminder(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        list.addAll(newList);
        adapter.notifyDataSetChanged();
        Log.d("hh","re");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){

                    //当save按钮被按下时，回调当前方法，并新建变量获取用户填写的数据
                   /* String content=data.getStringExtra("content");
                    String name=data.getStringExtra("name");
                    String time=data.getStringExtra("time");
                    String alarmTime=data.getStringExtra("alarmTime");
                    Long currentTime=data.getLongExtra("currentTime",0);

                    String temptime=getTimeDifferenceHour(time,alarmTime);  //事件创立时间与事件提醒时间的相差时间
                    //Log.d("time",temptime);
                    Content contenttemp=new Content(content,false,false);  //新建content
                   // contenttemp.setName(name);  //设置事件名称
                    contenttemp.setTime(time);  //设置事件时间
                    contenttemp.setAlarmTime(alarmTime);  //设置提醒事件
                    contenttemp.setNum(list.size()+1);  //设置事件标号num

                    //Log.d("number---",contenttemp.getNum()+"");
                    //Log.d("numbersize---",list.size()+"");
                    //list.add(list.size(),contenttemp);

                    //adapter.notifyItemRangeChanged(0,list.size());
                    //adapter.notifyItemInserted(list.size()-1);
                    setReminder(true,Integer.parseInt(temptime)+currentTime,content,contenttemp.getNum());*/
                }
                break;
            default:
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.edit_item:
                if(item.getTitle().toString().equals("EDIT")){
                    item.setTitle("CANCEL");
                    deleteButton.setVisibility(View.VISIBLE);
                    selectButton.setVisibility(View.VISIBLE);
                    for(int i=0;i<list.size();i++){

                        list.get(i).setShow(true);
                        adapter.notifyItemInserted(i);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    item.setTitle("EDIT");

                    deleteButton.setVisibility(View.GONE);
                    selectButton.setVisibility(View.GONE);
                    for(int i=0;i<list.size();i++){

                        list.get(i).setShow(false);
                        list.get(i).setChecked(false);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maintoolbar,menu);
        return true;
    }



}
