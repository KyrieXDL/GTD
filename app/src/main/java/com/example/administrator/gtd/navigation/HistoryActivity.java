package com.example.administrator.gtd.navigation;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.gtd.Content;
import com.example.administrator.gtd.R;
import com.example.administrator.gtd.ThemeManager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener{

    private ExpandableListView elv_Main;
    private TimeLineAdapter timeLineAdapter;
    private ArrayList<String> GroupData;
    private ArrayList<List<TimeLine>> ChildData;
    private List<Content> list=new ArrayList<>();
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ThemeManager.registerThemeChangeListener(this);
        Intent intent=getIntent();
        int mode=intent.getIntExtra("mode",0);

        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
        }
        initData();
        initView();
        initTheme();
    }

    public void initTheme(){
        // 设置标题栏颜色
        /*if(supportActionBar != null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(ThemeManager.getCurrentThemeRes(ExpandableListView.this, R.color.colorPrimary))));
        }*/
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes(HistoryActivity.this, R.color.colorPrimary)));
        }

        linearLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(HistoryActivity.this, R.color.backgroundColor)));
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

    public void initData(){
        list.clear();
        List<Content> newList= DataSupport.order("msg desc").find(Content.class);
        list.addAll(newList);

        GroupData=new ArrayList<>();
        ChildData=new ArrayList<List<TimeLine>>();


        for (int i=0;i<list.size();i++){
            String timeStr=list.get(i).getAlarmTime();

            String[] list=timeStr.split("-");
            String year=list[0];
            String month=list[1];
            String day=list[2].substring(0,2);
            //listLine.add(new TimeLine(year,month,day));

            if (GroupData.contains(month+"月\n"+year+"年")){
                continue;
            }else{
               // strTemp+=year+month;
                GroupData.add(month+"月\n"+year+"年");
            }

        }

        for (int i=0;i<GroupData.size();i++){

            List<TimeLine> Child=new ArrayList<>();
            for (int j=0;j<list.size();j++){

                String timeStr=list.get(j).getAlarmTime();

                String[] listtemp=timeStr.split("-");
                String year=listtemp[0];
                String month=listtemp[1];
                String day=listtemp[2].substring(0,2);

                if (GroupData.get(i).equals(month+"月\n"+year+"年")){
                    Child.add(new TimeLine(year+"年"+month+"月"+day+"日",list.get(j).getMsg()));
                }
            }

            union(Child);
            sortChild(Child);
            ChildData.add(Child);

        }

        sortGroup(GroupData,ChildData);

    }

    private void initView() {
        elv_Main = (ExpandableListView) findViewById(R.id.main_elv);
        timeLineAdapter = new TimeLineAdapter(HistoryActivity.this,GroupData,ChildData);
        elv_Main.setAdapter(timeLineAdapter);
        elv_Main.setDivider(null);
        elv_Main.setGroupIndicator(null);

        for (int i = 0;i<timeLineAdapter.getGroupCount();i++){
            elv_Main.expandGroup(i);
        }

        linearLayout=(LinearLayout) findViewById(R.id.linearlayout);
    }

    public void sortChild(List<TimeLine> Child){

        List<Float> numList=new ArrayList<>();
        for (int i=0;i<Child.size();i++){

            TimeLine timeline=Child.get(i);
            String month=timeline.getDate().substring(5,7);
            String day=timeline.getDate().substring(8,10);
            float num=Float.parseFloat(day)/100+Float.parseFloat(month);

            numList.add(num);
        }

        for (int i=0;i<numList.size()-1;i++){

            for (int j=i+1;j<numList.size();j++){

                if (numList.get(i)>numList.get(j)){
                    float temp=numList.get(i);
                    numList.set(i,numList.get(j));
                    numList.set(j,temp);


                    TimeLine timelinetemp=Child.get(i);
                    Child.set(i,Child.get(j));
                    Child.set(j,timelinetemp);
                }
            }
        }


    }

    public void sortGroup(ArrayList<String> GroupData,ArrayList<List<TimeLine>> ChildData){

        List<Float> numList=new ArrayList<>();

        for (int i=0;i<GroupData.size();i++){

            String strDate=GroupData.get(i);
            String month=strDate.substring(0,2);
            String year=strDate.substring(3,8);
            float num=Float.parseFloat(month)/100+Float.parseFloat(year);

            numList.add(num);
        }

        for (int i=0;i<numList.size()-1;i++){

            for (int j=i+1;j<numList.size();j++){

                if (numList.get(i)>numList.get(j)){
                    float temp=numList.get(i);
                    numList.set(i,numList.get(j));
                    numList.set(j,temp);

                    String strtemp=GroupData.get(i);
                    GroupData.set(i,GroupData.get(j));
                    GroupData.set(j,strtemp);

                    List<TimeLine> listTemp=ChildData.get(i);
                    ChildData.set(i,ChildData.get(j));
                    ChildData.set(j,listTemp);
                }
            }
        }
    }

    public void union(List<TimeLine> Child){

        for (int i=0;i<Child.size();i++){
            TimeLine timeLine=Child.get(i);
            for (int j=0;j<Child.size();j++){

                if (timeLine.getDate().equals(Child.get(j).getDate()) && j!=i){


                    timeLine.setContent(timeLine.getContent()+"\n"+Child.get(j).getContent());
                    Child.remove(j);

                }

            }

            Child.get(i).setContent(timeLine.getContent());

        }

    }

    class TimeLine{

        String date;

        String content;

        public TimeLine(String date,String content){
            this.date=date;
            this.content=content;
        }

        public String getDate(){
            return date;
        }

        public String getContent(){
            return content;
        }

        public void setContent(String content){
            this.content=content;
        }
    }
}
