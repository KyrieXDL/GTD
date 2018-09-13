package com.example.administrator.gtd.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gtd.Content;
import com.example.administrator.gtd.R;


import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/2/18.
 */

public class TimeLineAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<String> GroupData ;//定义组数据
    private List<List<HistoryActivity.TimeLine>> ChildrenData ;//定义组中的子数据

    public TimeLineAdapter(Context context,ArrayList<String> GroupData, ArrayList<List<HistoryActivity.TimeLine>> ChildrenData) {
        this.context = context;
        this.ChildrenData=ChildrenData;
        this.GroupData=GroupData;
    }

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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_child_timeline, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.itemChildMonthDay = (TextView) convertView.findViewById(R.id.item_child_month_day);
            childViewHolder.contents = (TextView) convertView.findViewById(R.id.item_child_lv);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.contents.setText(ChildrenData.get(groupPosition).get(childPosition).getContent());
        String[] list=ChildrenData.get(groupPosition).get(childPosition).getDate().split("年");
        childViewHolder.itemChildMonthDay.setText(list[1]);
        SharedPreferences sharedPreferences=context.getSharedPreferences("data",MODE_PRIVATE);
        int mode=sharedPreferences.getInt("mode",0);
        if (mode==0){
            childViewHolder.contents.setTextColor(context.getResources().getColor(R.color.black));
            childViewHolder.itemChildMonthDay.setTextColor(context.getResources().getColor(R.color.black));

        }else{
            childViewHolder.contents.setTextColor(context.getResources().getColor(R.color.white));
            childViewHolder.itemChildMonthDay.setTextColor(context.getResources().getColor(R.color.white));

        }
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_timeline, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.itemGroupTopLine = convertView.findViewById(R.id.item_group_top_line);
            groupViewHolder.itemGroupYearMonth = (TextView) convertView.findViewById(R.id.item_group_year_month);
            groupViewHolder.itemGroupBottomLine = (TextView) convertView.findViewById(R.id.item_group_bottom_line);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        if (groupPosition == 0){
            groupViewHolder.itemGroupTopLine.setVisibility(View.INVISIBLE);
        }else {
            groupViewHolder.itemGroupTopLine.setVisibility(View.VISIBLE);
        }
        groupViewHolder.itemGroupYearMonth.setText(GroupData.get(groupPosition));

        SharedPreferences sharedPreferences=context.getSharedPreferences("data",MODE_PRIVATE);
        int mode=sharedPreferences.getInt("mode",0);
        if (mode==0){
            groupViewHolder.itemGroupYearMonth.setBackground(context.getDrawable(R.drawable.shape_orange_circle));
        }else{
            groupViewHolder.itemGroupYearMonth.setBackground(context.getDrawable(R.drawable.shape_orange_circle_night));

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

    static class GroupViewHolder {
        View itemGroupTopLine;
        TextView itemGroupYearMonth;
        View itemGroupBottomLine;
    }
    static class ChildViewHolder {
        TextView itemChildMonthDay;
        TextView contents;
    }
}
