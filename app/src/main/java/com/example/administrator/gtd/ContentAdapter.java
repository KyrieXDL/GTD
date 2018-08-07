package com.example.administrator.gtd;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private boolean isOpen = false;
    private int mShortHeight;//限定行数高度
    private int mLongHeight;//展开全文高度
    private static LinearLayout.LayoutParams mLayoutParams;
    private int mMaxlines = 2;//设定显示的最大行数
    private int maxLine;//真正的最大行数
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView contentName;
        TextView contentTime;
        CheckBox checkBox;
        CardView cardView;
        //TextView mTextViewToggle;
        public ViewHolder(View view){
            super(view);
            contentTime=(TextView) view.findViewById(R.id.content_time);
            contentName=(TextView) view.findViewById(R.id.content_name);
            checkBox=(CheckBox) view.findViewById(R.id.checkbox);
            cardView=(CardView) view.findViewById(R.id.card_view);
          //  mTextViewToggle = (TextView) view.findViewById(R.id.tv_toggle);
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

                if (content.isDone()){
                Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "not done", Toast.LENGTH_SHORT).show();
                }
                if (!content.isShow()){
                    Intent intent=new Intent(context,ContentActivity.class);
                    intent.putExtra("content0",content.getMsg());
                    intent.putExtra("time0",content.getTime());
                    intent.putExtra("alarmtime0",content.getAlarmTime());
                    intent.putExtra("activityName",1);
                    intent.putExtra("numFromContentActivity",content.getContentid());
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
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
        //content.setNum(position);

        ContentValues values=new ContentValues();
        //int id=content.getNum();
        //values.put("num",position);
        //DataSupport.updateAll(Content.class,values,"num=?",content.getNum()+"");

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

        /*
        * 设置当前内容为收缩全文*/
        //获取收缩后的高度
        /*mShortHeight = getShortHeight(holder.contentName,mMaxlines,temp);

        holder.contentName.post(new Runnable() {
            @Override
            public void run() {
                maxLine = holder.contentName.getLineCount();//获取完全显示行数

                mLongHeight = getLongHeight(mShortHeight,mMaxlines,maxLine);//获取完全显示需要的高度
                Log.d("MainActivity", "1长的" + mLongHeight + ",短的=" + mShortHeight+", "+maxLine);

                //height=mLongHeight;
                if (mLongHeight <= mShortHeight) {
                    holder.mTextViewToggle.setVisibility(View.GONE);//完全显示需要的高度小于低的高度的时候，不需要展开
                }
            }
        });
        /*maxLine = holder.contentName.getLineCount();//获取完全显示行数

        mLongHeight = getLongHeight(mShortHeight,mMaxlines,maxLine);//获取完全显示需要的高度
        Log.d("MainActivity", "1长的" + mLongHeight + ",短的=" + mShortHeight+", "+maxLine);
        if (mLongHeight <= mShortHeight) {
            holder.mTextViewToggle.setVisibility(View.GONE);//完全显示需要的高度小于低的高度的时候，不需要展开
        }*/
        //显示少的高度
        /*mLayoutParams = (LinearLayout.LayoutParams) holder.contentName.getLayoutParams();
        mLayoutParams.height = mShortHeight;
        holder.contentName.setLayoutParams(mLayoutParams);
        holder.mTextViewToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle(holder.contentName,holder.mTextViewToggle,mLayoutParams,mShortHeight,mLongHeight);//开始展开收起动画
            }
        });*/
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

    //展开全文的动画
    private void toggle(final TextView textView, final TextView textView_toggle,final LinearLayout.LayoutParams mLayoutParams,int mShortHeight, int mLongHeight) {
        ValueAnimator animator;
        if (isOpen) {
            animator = ValueAnimator.ofInt(616, mShortHeight);
            isOpen = false;
        } else {
            animator = ValueAnimator.ofInt(mShortHeight, 616);
            isOpen = true;
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                mLayoutParams.height = value;
                textView.setLayoutParams(mLayoutParams);
                Log.d("MainActivity", "长的" + textView.getHeight());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isOpen) {
                    textView_toggle.setText("收起全文");
                } else {
                    textView_toggle.setText("展开全文");
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.setDuration(500);
        animator.start();
    }


    //获取收缩全文后的高度
    private int getShortHeight(TextView mTextView,int mMaxlines, String text) {//虚拟一个tv来获得理论高度值，短文高度
        int width = mTextView.getMeasuredWidth();//宽度

        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        textView.setMaxLines(mMaxlines);

        int measureSpecWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);//宽度match
        int measureSpecHeight = View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.AT_MOST);//高度wrap，1920为最大高度
        textView.measure(measureSpecWidth, measureSpecHeight);

        return textView.getMeasuredHeight();
    }

    //获取展开全文后的高度
    private int getLongHeight(int mShortHeight,int mMaxlines,int maxLine) {//长文高度

        int height = mShortHeight / mMaxlines * maxLine;

        return height;
    }

}
