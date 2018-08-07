package com.example.administrator.gtd.reiview_module;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.administrator.gtd.Content;
import com.example.administrator.gtd.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Content> list=new ArrayList<>();
    private ReviewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);
        recyclerView=(RecyclerView) findViewById(R.id.review_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        intial();
        Toast.makeText(this, ""+list.size(), Toast.LENGTH_SHORT).show();
        adapter=new ReviewAdapter(ReviewActivity.this,list);
        recyclerView.setAdapter(adapter);

    }

    public void intial(){
        List<Content> listTemp= DataSupport.order("msg desc").find(Content.class);
        for (int i=0;i<listTemp.size();i++){
            if (listTemp.get(i).isDone()){
                list.add(listTemp.get(i));
            }
        }
    }
}
