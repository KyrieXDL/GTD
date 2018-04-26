package com.example.administrator.gtd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpandableListView extends AppCompatActivity {

    private List<String> GroupData ;//定义组数据
    private List<List<String>> ChildrenData ;//定义组中的子数据
    private List<Content> list=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list_view);

        Toast.makeText(ExpandableListView.this,"Fuck!!!",Toast.LENGTH_SHORT).show();
        initial();

        android.widget.ExpandableListView myExpandableListView = (android.widget.ExpandableListView)findViewById(R.id.myExpandableListView);
        myExpandableListView.setAdapter(new ExpandableAdapter());
        myExpandableListView.setGroupIndicator(null);
        myExpandableListView.setDivider(null);
        //myExpandableListView.setBackgroundResource(R.drawable.background);
        myExpandableListView.expandGroup(0);
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

    //将数据库中的数据初始化到expandablelistview界面上
    //并根据所设置的属性调整每个group的item，以及顺序
    private void initial(){
        list.clear();
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        list.addAll(newList);
        GroupData = new ArrayList<String>();
        ChildrenData = new ArrayList<List<String>>();
        List<String> lastContentList=new ArrayList<>();
        findLastContent(lastContentList,list);
        if(lastContentList.size()>0){
            for(int i=0;i<lastContentList.size();i++){
                GroupData.add("schedule"+i);

                List<String> Child = new ArrayList<String>();

                Child.add(lastContentList.get(i));
                String temp=lastContentList.get(i);
                //Toast.makeText(ExpandableListView.this,temp,Toast.LENGTH_SHORT).show();
                //getPreContent(list,temp);
                while(!getPreContent(list,temp).equals("")){
                    Child.add(getPreContent(list,temp));
                    temp=getPreContent(list,temp);
                }
                Collections.reverse(Child);
                ChildrenData.add(Child);
            }
        }
    }

    //返回nextContent为nothing的content
    private void findLastContent(List<String> tempList,List<Content> listTemp){
        //List<String> temp=new ArrayList<>();
        for(int i=0;i<listTemp.size();i++){
            if((listTemp.get(i).getNextContent()==null)||(listTemp.get(i).getNextContent().equals("nothing"))){
                tempList.add(listTemp.get(i).getMsg());
            }
        }
    }

    //返回下一件事等于str的content的msg
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

            if (content.getLevel()==3){
                childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }else if (content.getLevel()==2){
                childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.color5));
                childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.color5));
            }else if (content.getLevel()==1){
                childViewHolder.tvTitle.setBackgroundColor(getResources().getColor(R.color.yellow));
                childViewHolder.time.setBackgroundColor(getResources().getColor(R.color.yellow));
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
                    intent.putExtra("numFromContentActivity",content.getNum());
                    intent.putExtra("nextContentFromAdapter",content.getNextContent());
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
