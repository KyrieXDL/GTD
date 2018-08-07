package com.example.administrator.gtd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpandableListView extends AppCompatActivity implements ThemeManager.OnThemeChangeListener{

    private List<String> GroupData ;//定义组数据
    private List<List<String>> ChildrenData ;//定义组中的子数据
    private List<Content> list=new ArrayList<>();
    private RelativeLayout groupView;
    private LinearLayout childView;
    private ActionBar supportActionBar;
    private LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list_view);
       // supportActionBar = getSupportActionBar();
        ThemeManager.registerThemeChangeListener(this);
        linearLayout=(LinearLayout) findViewById(R.id.expand_list_view);

        Intent intent=getIntent();
        int mode=intent.getIntExtra("mode",0);

        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
        }

        initial();

        android.widget.ExpandableListView myExpandableListView = (android.widget.ExpandableListView)findViewById(R.id.myExpandableListView);
        myExpandableListView.setAdapter(new ExpandableAdapter());
        myExpandableListView.setGroupIndicator(null);
        myExpandableListView.setDivider(null);
        //myExpandableListView.setBackgroundResource(R.drawable.background);
        myExpandableListView.expandGroup(0);

        initTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initial();
        android.widget.ExpandableListView myExpandableListView = (android.widget.ExpandableListView)findViewById(R.id.myExpandableListView);
        myExpandableListView.setAdapter(new ExpandableAdapter());
        myExpandableListView.setGroupIndicator(null);
        myExpandableListView.setDivider(null);
        //myExpandableListView.setBackgroundResource(R.drawable.background);
        myExpandableListView.expandGroup(0);

    }

    public void initTheme(){
        // 设置标题栏颜色
        /*if(supportActionBar != null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(ThemeManager.getCurrentThemeRes(ExpandableListView.this, R.color.colorPrimary))));
        }*/
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ExpandableListView.this, R.color.colorPrimary)));
        }

        linearLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(ExpandableListView.this, R.color.backgroundColor)));
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

    //将数据库中的数据初始化到expandablelistview界面上
    //并根据所设置的属性调整每个group的item，以及顺序
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

    private class ExpandableAdapter extends BaseExpandableListAdapter {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return ChildrenData.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder childViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ExpandableListView.this).inflate(R.layout.item_expand_child, parent, false);
                childViewHolder = new ChildViewHolder();
                childViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_child);
                childViewHolder.time=(TextView) convertView.findViewById(R.id.expand_time);
                childViewHolder.layout=(LinearLayout) convertView.findViewById(R.id.child_item);
                convertView.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }
            childViewHolder.tvTitle.setText(ChildrenData.get(groupPosition).get(childPosition));

            List<Content> tempList=DataSupport.where("msg=?",ChildrenData.get(groupPosition).get(childPosition)).find(Content.class);
            final Content content=tempList.get(0);

            SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
            int mode=sharedPreferences.getInt("mode",0);

            if (mode==0) {
                if (content.getLevel() == 3) {
                    childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                } else if (content.getLevel() == 2) {
                    childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.color5));
                    childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.color5));
                } else if (content.getLevel() == 1) {
                    childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.yellow));
                    childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.yellow));
                }
            }else{
                if (content.getLevel() == 3) {
                    childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.colorAccent_night));
                    childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.colorAccent_night));
                } else if (content.getLevel() == 2) {
                    childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.color5_night));
                    childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.color5_night));
                } else if (content.getLevel() == 1) {
                    childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.yellow_night));
                    childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.yellow_night));
                }
            }

            childViewHolder.time.setText(content.getAlarmTime());
            childViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ExpandableListView.this,ContentActivity.class);
                    intent.putExtra("content0",content.getMsg());
                    intent.putExtra("time0",content.getTime());
                    intent.putExtra("alarmtime0",content.getAlarmTime());
                    intent.putExtra("activityName",1);
                    intent.putExtra("numFromContentActivity",content.getContentid());
                    intent.putExtra("nextContentFromAdapter",content.getNextContent());
                    SharedPreferences sharedPreferences0=getSharedPreferences("data",MODE_PRIVATE);
                    int mode0=sharedPreferences0.getInt("mode",0);
                    intent.putExtra("mode",mode0);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return ChildrenData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return GroupData.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return GroupData.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder groupViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ExpandableListView.this).inflate(R.layout.item_expand_group, parent, false);
                groupViewHolder = new GroupViewHolder();
                groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_group);
                groupViewHolder.imageView=(ImageView) convertView.findViewById(R.id.arrow);
                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }
            groupViewHolder.tvTitle.setText(GroupData.get(groupPosition));
            SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
            int mode=sharedPreferences.getInt("mode",0);
            if (mode==0) {
                groupViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.yellow));
            }else{
                groupViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.yellow_night));
            }
            if(isExpanded){
                groupViewHolder.imageView.setBackgroundResource(R.drawable.expand_down);
            }else{
                groupViewHolder.imageView.setBackgroundResource(R.drawable.expand_right);
            }
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
    static class GroupViewHolder {
        ImageView imageView;
        TextView tvTitle;
    }
    static class ChildViewHolder {
        TextView tvTitle;
        TextView time;
        LinearLayout layout;
    }
}
