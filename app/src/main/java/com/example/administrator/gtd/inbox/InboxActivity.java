package com.example.administrator.gtd.inbox;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.administrator.gtd.Content;
import com.example.administrator.gtd.ContentActivity;
import com.example.administrator.gtd.ContentHelper;
import com.example.administrator.gtd.R;
import com.example.administrator.gtd.ThemeManager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InboxActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener{

    private Spinner spinner;
    private ArrayAdapter adapter;
    private List<String> planlist;
    private ArrayList<List<Content>> contentlist;

    private List<String> GroupData ;//定义组数据
    private List<List<String>> ChildrenData ;//定义组中的子数据
    private List<Content> list=new ArrayList<>();
    private ViewPager viewPager;
    private ViewPagerAdapter viewPageradapter;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        ThemeManager.registerThemeChangeListener(this);

        Intent intent=getIntent();
        int mode=intent.getIntExtra("mode",0);

        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
        }
        //initData();
        initial();
        initView();
        initTheme();

        setClicked(0);

        //设置下拉框的选择事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                notifyRefreshAdapter(pos);
                setClicked(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        initial();
        spinner.setSelection(0);
        notifySpinner();
        notifyRefreshAdapter(0);
    }

    public void setClicked(final int position){
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            int flage = 0 ;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        flage = 0 ;
                        break ;
                    case MotionEvent.ACTION_MOVE:
                        flage = 1 ;
                        break ;
                    case  MotionEvent.ACTION_UP :
                        if (flage == 0) {
                            int item = viewPager.getCurrentItem();
                            List<Content> tempList=DataSupport.where("msg=?",ChildrenData.get(position).get(item)).find(Content.class);
                            final Content content=tempList.get(0);
                            Intent intent = new Intent(InboxActivity.this, ContentActivity.class);
                            intent.putExtra("content0",content.getMsg());
                            intent.putExtra("time0",content.getTime());
                            intent.putExtra("alarmtime0",content.getAlarmTime());
                            intent.putExtra("activityName",1);
                            intent.putExtra("numFromContentActivity",content.getContentid());
                            intent.putExtra("nextContentFromAdapter",content.getNextContent());
                            SharedPreferences sharedPreferences0=getSharedPreferences("data",MODE_PRIVATE);
                            int mode0=sharedPreferences0.getInt("mode",0);
                            intent.putExtra("mode",mode0);

                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(InboxActivity.this, viewPager, "sharedView").toBundle());

                        }
                        break ;


                }
                return false;
            }
        });
    }

    private void notifyRefreshAdapter(int position) {
        if(viewPageradapter != null){
            viewPageradapter = null;
        }
        viewPageradapter = new ViewPagerAdapter(InboxActivity.this, ChildrenData.get(position));
        viewPager.setAdapter(viewPageradapter);
    }

    private void notifySpinner(){
        if(adapter != null){
            adapter = null;
        }
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,GroupData);

        //设置下拉框的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public void initTheme(){
        // 设置标题栏颜色
        /*if(supportActionBar != null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(ThemeManager.getCurrentThemeRes(ExpandableListView.this, R.color.colorPrimary))));
        }*/
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes(InboxActivity.this, R.color.colorPrimary)));
        }

        relativeLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(InboxActivity.this, R.color.color5)));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeManager.unregisterThemeChangeListener(this);
    }
    @Override
    public void onThemeChanged() {
        initTheme();
    }

    private void initial(){
        list.clear();
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        list.addAll(newList);
        GroupData = new ArrayList<String>();
        ChildrenData = new ArrayList<List<String>>();
        List<String> lastContentList=new ArrayList<>();
        ContentHelper.findLastContent(lastContentList,list);
        if(lastContentList.size()>0){
            for(int i=0;i<lastContentList.size();i++){
                GroupData.add(getResources().getText(R.string.schedule).toString()+"_"+(i+1));

                List<String> Child = new ArrayList<String>();

                Child.add(lastContentList.get(i));
                String temp=lastContentList.get(i);
                //Toast.makeText(ExpandableListView.this,temp,Toast.LENGTH_SHORT).show();
                //getPreContent(list,temp);
                while(!ContentHelper.getPreContent(list,temp).equals("")){
                    Child.add(ContentHelper.getPreContent(list,temp));
                    temp=ContentHelper.getPreContent(list,temp);
                }
                Collections.reverse(Child);
                ChildrenData.add(Child);
            }
        }
    }


    public void initView(){
        spinner=(Spinner) findViewById(R.id.spinner);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,GroupData);

        //设置下拉框的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setPageMargin(10);
        viewPager.setOffscreenPageLimit(3);

        viewPageradapter = new ViewPagerAdapter(this, ChildrenData.get(0));
        viewPager.setAdapter(viewPageradapter);
        /*viewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                48, getResources().getDisplayMetrics()));*/
        viewPager.setPageTransformer(false, new ScaleTransformer(this));

        relativeLayout=(RelativeLayout) findViewById(R.id.relative_layout);
    }
}
