package com.example.administrator.gtd.reiview_module;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.gtd.Content;
import com.example.administrator.gtd.ContentAdapter;
import com.example.administrator.gtd.R;


import java.util.List;

/**
 * Created by Administrator on 2018/6/19 0019.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static List<Content> list;
    private Context context;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView time;

        public ViewHolder(View view){
            super(view);
            time=(TextView) view.findViewById(R.id.completed_time);
            content=(TextView) view.findViewById(R.id.completed_content);

        }
    }

    public ReviewAdapter(Context mcontext, List<Content> mList){
        list=mList;
        context=mcontext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);
        final ReviewAdapter.ViewHolder holder=new ReviewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.content.setText(list.get(position).getMsg());
        holder.time.setText(list.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
