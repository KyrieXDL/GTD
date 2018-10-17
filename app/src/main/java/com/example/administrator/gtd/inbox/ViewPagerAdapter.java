package com.example.administrator.gtd.inbox;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gtd.Content;
import com.example.administrator.gtd.HttpUtil;
import com.example.administrator.gtd.R;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/9/14 0014.
 */

public class ViewPagerAdapter extends  PagerAdapter{
    private List<String> list;
    private Context context;
    private LayoutInflater inflater;



    public ViewPagerAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.pager_item, container, false);
        container.addView(view);

        TextView textView=(TextView) view.findViewById(R.id.tv);
        textView.setText(list.get(position));

        TextView numView=(TextView) view.findViewById(R.id.num);
        numView.setText((position+1)+"/"+list.size());

        List<Content> tempList= DataSupport.where("msg=?",list.get(position)).find(Content.class);
        final Content content=tempList.get(0);

        TextView timeView=(TextView) view.findViewById(R.id.time);
        timeView.setText(content.getAlarmTime());

        final FrameLayout frameLayout=(FrameLayout) view.findViewById(R.id.framelayout);
        //final Button button=(Button) view.findViewById(R.id.button_do);
        final com.dd.CircularProgressButton circularProgressButton=(com.dd.CircularProgressButton) view.findViewById(R.id.btnWithText);
        if (content.isDone()){

            circularProgressButton.setText(context.getString(R.string.button_text_redo));
            circularProgressButton.setProgress(100);
            frameLayout.setVisibility(View.VISIBLE);
            //button.setText(context.getString(R.string.button_text_redo));
        }else{

            circularProgressButton.setProgress(0);
            frameLayout.setVisibility(View.INVISIBLE);
            //button.setText(context.getString(R.string.button_text_start));
        }
        circularProgressButton.setIndeterminateProgressMode(true); // turn on indeterminate progress
        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circularProgressButton.getText().toString().equals(context.getString(R.string.button_text_start))){
                    //Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                    circularProgressButton.setProgress(1); // set progress to 0 to switch back to normal state
                    circularProgressButton.setProgress(100);
                    String updateurl="http://120.79.7.33/update3.php?contentid="+content.getContentid();
                    new MyUpdateTask().execute(updateurl);
                    frameLayout.setVisibility(View.VISIBLE);
                    circularProgressButton.setText(context.getString(R.string.button_text_redo));
                    ContentValues value=new ContentValues();
                    value.put("isdone",true);
                    DataSupport.updateAll(Content.class,value,"contentid=?",content.getContentid()+"");
                }
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    class MyUpdateTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                int res=object.getInt("res");
                if (res==0){
                    //Toast.makeText(MainActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                }else{

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}

