package com.example.administrator.gtd;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.DrawableImageViewTarget;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/1/25 0025.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder>{
    private List<String> contentList;
    private static List<Content> list;
    private Context context;
    private EditText text;
    private int night_mode=0;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView contentName;
        TextView contentTime;
        CheckBox checkBox;
        CardView cardView;
        public ViewHolder(View view){
            super(view);
            contentTime=(TextView) view.findViewById(R.id.content_time);
            contentName=(TextView) view.findViewById(R.id.content_name);
            checkBox=(CheckBox) view.findViewById(R.id.checkbox);
            cardView=(CardView) view.findViewById(R.id.card_view);
        }
    }

    /*public ContentAdapter(Context mcontext,List<String> mcontentList){
        contentList=mcontentList;
        context=mcontext;
    }*/
    public ContentAdapter(Context mcontext,List<Content> mList){
        list=mList;
        context=mcontext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        text=(EditText) view.findViewById(R.id.edit_text);
        holder.contentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Content content=list.get(position);

                if (!content.isShow()){
                    Intent intent=new Intent(context,ContentActivity.class);
                    intent.putExtra("content0",content.getMsg());
                    intent.putExtra("time0",content.getTime());
                    intent.putExtra("alarmtime0",content.getAlarmTime());
                    intent.putExtra("activityName",1);
                    intent.putExtra("numFromContentActivity",content.getNum());
                    intent.putExtra("nextContentFromAdapter",content.getNextContent());
                    SharedPreferences sharedPreferences0=context.getSharedPreferences("data",MODE_PRIVATE);
                    int mode0=sharedPreferences0.getInt("mode",0);
                    intent.putExtra("mode",mode0);
                    context.startActivity(intent);
                }

                //text.setText(content.getMsg());
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                Content content = list.get(position);
                // content.setChecked(true);
                if (content.isChecked()) {
                    content.setChecked(false);
                    Log.d("position----",position+"");

                    //当用户取消一个content的选择时就设置选择按钮（如果当前不是显示全选）为“全选”
                    MainActivity.Select.setButtonSelectAll();
                    //LitePal更新数据库 2018.3.7
                    //Content content0=new Content();
                    //content0.setChecked(false);
                    //content.updateAll("num=?", position + 1 + "");
                } else {
                    content.setChecked(true);
                    Log.d("position----",position+"");

                    if (isAllSelected()){
                        MainActivity.Select.setButtonNotSelectAll();
                    }
                    //LitePal更新数据库 2018.3.7
                    //Content content0=new Content();
                    //content0.setChecked(true);
                    //content.updateAll("num=?", position + 1 + "");

                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //String name=contentList.get(position);
        Content content=list.get(position);
        String temp=content.getMsg();
        /*if(temp.length()>10){
            temp=temp.substring(0,10);
        }*/
        holder.contentName.setText(temp);
        if(!content.isShow()){
            holder.checkBox.setVisibility(View.GONE);
        }else{
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        if(!content.isChecked()){
            holder.checkBox.setChecked(false);
        }else{
            holder.checkBox.setChecked(true);
        }
        content.setNum(position);

        ContentValues values=new ContentValues();
        values.put("num",position);
        DataSupport.updateAll(Content.class,values,"msg=?",content.getMsg());

        if(content.isDone()){
            holder.contentName.setTextColor(Color.BLACK);
        }else{
            holder.contentName.setTextColor(Color.RED);
        }

        SharedPreferences sharedPreferences=context.getSharedPreferences("data",MODE_PRIVATE);
        int mode=sharedPreferences.getInt("mode",0);

        if (mode==0) {
            //根据content的level值设置背景色
            if (content.getLevel() == 3) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            } else if (content.getLevel() == 2) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.color5));
            } else if (content.getLevel() == 1) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.yellow));
            }
        } else if (mode==1){
            if (content.getLevel() == 3) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent_night));
            } else if (content.getLevel() == 2) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.color5_night));
            } else if (content.getLevel() == 1) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.yellow_night));
            }
        }
        holder.contentTime.setText(content.getTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public boolean isAllSelected(){

        for (int i=0;i<list.size();i++){
            if (!list.get(i).isChecked()){
                return false;
            }
        }
        return true;
    }

    public static boolean isAllNotSelected(){

        for (int i=0;i<list.size();i++){
            if (list.get(i).isChecked()){
                return false;
            }
        }
        return true;
    }

}
