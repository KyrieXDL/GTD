package com.example.administrator.gtd;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.gtd.animator.Background_anim;
import com.example.administrator.gtd.animator.MoonAnim1;
import com.example.administrator.gtd.animator.MoonAnim2;
import com.example.administrator.gtd.animator.SunAnim;
import com.example.administrator.gtd.animator.SunAnim_Lines;
import com.example.administrator.gtd.inbox.InboxActivity;
import com.example.administrator.gtd.navigation.HistoryActivity;

import com.example.administrator.gtd.set.SetActivity;
import com.example.administrator.gtd.user_info.UserInfoActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener{

    public List<Content> list=new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ContentAdapter adapter;
    private static Button selectButton;
    private Button deleteButton;
    private ArrayList<String> strList=new ArrayList<>();
    private NavigationView navigationView;
    private CheckBox checkBox;
    private ActionBar supportActionBar;
    private MenuItem menuItem;
    private RecyclerView recyclerView;
    private FloatingActionButton button;
    private LinearLayout dialog_delete;
    private LinearLayout dialog_normal;

    private SharedPreferences sharedPreferences;

    //动画
    private SunAnim sunAnim;
    private SunAnim_Lines sunAnim_lines;
    private MoonAnim1 moonAnim1;
    private MoonAnim2 moonAnim2;
    private CoordinatorLayout coordinatorLayout;

    private long mExitTime;
    private PullToRefreshLayout pullToRefreshLayout;
    private CircleImageView circleImageView;

    private int userid;
    private RelativeLayout header_bg;
    private View headview;
    private TextView username_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        ThemeManager.registerThemeChangeListener(this);
        DataSupport.deleteAll(Content.class);



        //适配器
        recyclerView=(RecyclerView) findViewById(R.id.conten_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        StaggeredGridLayoutManager staggeredGridLayoutManager= new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //设置布局管理器
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        adapter=new ContentAdapter(MainActivity.this,list);
        recyclerView.setAdapter(adapter);
        sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);

        //向服务端发送请求，获取数据
        Intent intent=getIntent();
        userid=intent.getIntExtra("userid",1);
        final String url = "http://120.79.7.33/query.php?userid="+userid;
        new MyTask().execute(url);

        initialView();
        setListener();

        username_text.setText(intent.getStringExtra("name"));


    }

    public void setListener(){
        //设置下拉刷新
        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //向服务端发送请求，获取数据
                        Intent intent=getIntent();
                        int userid=intent.getIntExtra("userid",1);
                        String url = "http://120.79.7.33/query.php?userid="+userid;
                        new MyTask().execute(url);
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束加载更多
                        pullToRefreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });

        //加载头像图片
        //View headview=navigationView.inflateHeaderView(R.layout.header);
        circleImageView=(CircleImageView) headview.findViewById(R.id.header_img);
        username_text=(TextView) headview.findViewById(R.id.username);
        String imgurl="http://120.79.7.33/gtd/load.php?userid="+userid;
        Glide.with(MainActivity.this).load(imgurl).error(R.drawable.head_img).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(circleImageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "clicked image", Toast.LENGTH_SHORT).show();
                Intent intent0=getIntent();
                //设置点击头像后的事件
                Intent intent1=new Intent(MainActivity.this, UserInfoActivity.class);
                intent1.putExtra("userid",userid);
                intent1.putExtra("name",intent0.getStringExtra("name"));
                int mode=sharedPreferences.getInt("mode",0);
                intent1.putExtra("mode",mode);
                startActivity(intent1);
            }
        });

        //点击全选按钮的事件
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectButton.getText().toString().equals(getResources().getText(R.string.select_all))){
                    selectButton.setText(getResources().getText(R.string.not_select_all));
                    for(int i=0;i<list.size();i++){
                        //LitePal更新数据库 2018.3.7
                        /*Content content=list.get(i);
                        content.setChecked(true);
                        content.updateAll();*/

                        list.get(i).setChecked(true);
                        //list.get(i).setChecked(true);
                        adapter.notifyItemInserted(i);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    selectButton.setText(getResources().getText(R.string.select_all));
                    for(int i=0;i<list.size();i++){
                        //LitePal更新数据库2018.3.7
                       /* Content content=list.get(i);
                        content.setChecked(false);
                        content.updateAll();*/

                        list.get(i).setChecked(false);
                        adapter.notifyItemInserted(i);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });


        //点击删除按钮的事件
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 点击删除按钮后的将事件从数据库删除
                 */
                //首先判断是否选择事件去删除，如果一件事都没选则提醒用户
                if (ContentAdapter.isAllNotSelected()){
                    deleteNullAlarm();
                }else{
                    //如果用户已经选择事件去删除，则询问用户是否确定要删除，避免用户误操作
                    deleteAlarm();
                }

            }
        });


        //注册悬浮窗按钮事件
        button=(FloatingActionButton) findViewById(R.id.new_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LitePal.getDatabase();
                Intent intent=new Intent(MainActivity.this,ContentActivity.class);
                // Log.d("numId",list.get(0).getNum()+"");
                intent.putExtra("num0",list.size());

                List<Content> newList=DataSupport.order("msg desc").find(Content.class);
                strList.clear();
                strList.add("nothing");
                if (newList.size()>0){
                    for (int i=0;i<newList.size();i++){
                        strList.add(newList.get(i).getMsg());
                    }
                }
                intent.putStringArrayListExtra("list",strList);
                //SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
                int mode=sharedPreferences.getInt("mode",0);
                intent.putExtra("mode",mode);
                intent.putExtra("userid",userid);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, button, "sharedView").toBundle());

                menuItem.setTitle(getResources().getText(R.string.edit));
            }
        });


        //设置navigationView的item的点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //在这里处理item的点击事件
                switch (item.getItemId()){
                    case R.id.inbox_item:
                        Intent intent;
                        if (list.size()>0) {
                            intent = new Intent(MainActivity.this, InboxActivity.class);
                            int mode0=sharedPreferences.getInt("mode",0);
                            intent.putExtra("mode",mode0);
                            startActivity(intent);
                        }else{
                            /*intent = new Intent(MainActivity.this, InboxActivity.class);
                            startActivity(intent);*/
                            Toast.makeText(MainActivity.this, "暂未添加事件", Toast.LENGTH_SHORT).show();
                        }
                        //SharedPreferences sharedPreferences0=getSharedPreferences("data",MODE_PRIVATE);
                        break;

                    case R.id.set_item:
                        Intent setIntent=new Intent(MainActivity.this,SetActivity.class);
                        int mode_set=sharedPreferences.getInt("mode",0);
                        setIntent.putExtra("mode",mode_set);
                        startActivity(setIntent);
                        break;

                    case R.id.mode:

                        drawerLayout.closeDrawers();
                        replaceFragment(new Background_anim());
                        int mode=sharedPreferences.getInt("mode",0);

                        if (mode==0){
                            mode=1;
                            changeToNight_Anim();
                            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
                            item.setIcon(R.drawable.sun);
                            header_bg.setBackground(MainActivity.this.getDrawable(R.drawable.night_bg));
                        }else{
                            mode=0;
                            ValueAnimator animatorSet=changeToDay_Anim();
                            header_bg.setBackground(MainActivity.this.getDrawable(R.drawable.day_bg));
                            item.setIcon(R.drawable.night);
                            animatorSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }
                            });
                        }

                        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.putInt("mode",mode);
                        editor.apply();

                        for(int i=0;i<list.size();i++){
                            adapter.notifyItemChanged(i);
                        }

                        //Toast.makeText(MainActivity.this,"night_mode= "+list.get(0).getNight_mode(),Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.review:
                        Intent intentReview=new Intent(MainActivity.this, HistoryActivity.class);
                        int mode0=sharedPreferences.getInt("mode",0);
                        intentReview.putExtra("mode",mode0);
                        startActivity(intentReview);
                        break;


                    case R.id.language:
                        String ss = Locale.getDefault().getLanguage();
                        //用if语句判断，如果当前为中文就变成英文，反之变成中文
                        if (ss.equals("zh")){
                            Locale.setDefault(Locale.ENGLISH);
                            item.setIcon(R.drawable.cn_en);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.locale = Locale.ENGLISH;
                            getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
                            //recreate();
                            changeLanguage(1);
                        }else {
                            Locale.setDefault(Locale.CHINESE);
                            item.setIcon(R.drawable.en_cn);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.locale = Locale.CHINESE;
                            getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
                            //recreate();
                            changeLanguage(0);
                        }
                        break;

                }
                return true;
            }
        });

    }

    public void initialView(){
         /*初始化布局文件*/
        pullToRefreshLayout=(PullToRefreshLayout) findViewById(R.id.refresh);

        sunAnim=(SunAnim) findViewById(R.id.sunAnim);
        sunAnim_lines=(SunAnim_Lines) findViewById(R.id.sunAnim_Lines);
        moonAnim1=(MoonAnim1) findViewById(R.id.moonAnim1);
        moonAnim2=(MoonAnim2) findViewById(R.id.moonAnim2);
        coordinatorLayout=(CoordinatorLayout) findViewById(R.id.right);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        supportActionBar = getSupportActionBar();
        navigationView=(NavigationView) findViewById(R.id.nav_view);
        Resources resource=(Resources)getBaseContext().getResources();
        ColorStateList csl=(ColorStateList)resource.getColorStateList(R.color.itemColor);
        navigationView.setItemTextColor(csl);

        drawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        View view_normal = View.inflate(this, R.layout.dialog_normal, null);
        View view_delete = View.inflate(this, R.layout.dialog_delete, null);
        dialog_delete=(LinearLayout) view_delete.findViewById(R.id.dialog_delete);
        dialog_normal=(LinearLayout) view_normal.findViewById(R.id.dialog_normal);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        headview=navigationView.inflateHeaderView(R.layout.header);
        header_bg=(RelativeLayout) headview.findViewById(R.id.header_bg);

        //初始化删除和全选按钮为不可见
        deleteButton=(Button) findViewById(R.id.delete_button);
        selectButton=(Button) findViewById(R.id.select_all);
        deleteButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String imgurl="http://120.79.7.33/gtd/load.php?userid="+userid;
        Glide.with(MainActivity.this).load(imgurl).error(R.drawable.head_img).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(circleImageView);
        list.clear();
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        list.addAll(newList);
        //Toast.makeText(this, ""+list.size(), Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
        //获取当地已存储的主题模式，并设置
        //SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        int mode=sharedPreferences.getInt("mode",0);

        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
            initTheme();
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
        }

        //初始化控件不可见
        deleteButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);

    }

    //通过异步任务发送请求查询，返回数据并保存到本地数据库
    class MyTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                pullToRefreshLayout.finishRefresh();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                Type type = new TypeToken<List<Content>>() {
                }.getType();
                List<Content> listtmp = gson.fromJson(s, type);
                DataSupport.deleteAll(Content.class);
                for (Content content : listtmp) {
                    content.setContentid(content.getId());
                    //Log.d("contentID",content.getId()+"");
                    content.save();
                }
                list.clear();
                list.addAll(listtmp);
                Log.d("contentID",list.get(0).getContentid()+"");

                for (int i = 0; i < list.size(); i++) {
                    adapter.notifyItemInserted(i);
                    adapter.notifyDataSetChanged();
                }
              //  Toast.makeText(getApplicationContext(),getResources().getString(R.string.refresh_success), Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                e.getMessage();
                if (s.equals("")){
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.no_data) , Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.net_error) , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void onThemeChanged() {
        initTheme();
    }

    @Override
    public void onBackPressed() {
        //与上次点击返回键时刻作差
        if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                String ss = Locale.getDefault().getLanguage();
                if (ss.equals("zh")) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
                }
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                super.onBackPressed();
            }
        }else{
            drawerLayout.closeDrawers();
        }
    }

    class MyDeleteTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

            try {
                JSONObject object = new JSONObject(s);
                int res=object.getInt("res");
                if (res==1){
                    Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeManager.unregisterThemeChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.edit_item:
                if(item.getTitle().toString().equals(getResources().getText(R.string.edit))){
                    item.setTitle(getResources().getText(R.string.cancle));
                    deleteButton.setVisibility(View.VISIBLE);
                    selectButton.setVisibility(View.VISIBLE);
                    selectButton.setText(getResources().getText(R.string.select_all));
                    for(int i=0;i<list.size();i++){

                        list.get(i).setShow(true);
                        adapter.notifyItemInserted(i);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    item.setTitle(getResources().getText(R.string.edit));

                    deleteButton.setVisibility(View.GONE);
                    selectButton.setVisibility(View.GONE);
                    for(int i=0;i<list.size();i++){

                        list.get(i).setShow(false);
                        list.get(i).setChecked(false);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
        }
        return true;
    }

    public void initTheme() {
        // 设置标题栏颜色
        if(supportActionBar != null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.colorPrimary))));
        }
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.colorPrimary)));
        }
        drawerLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.backgroundColor)));
        recyclerView.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.backgroundColor)));
        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.colorAccent))));

        navigationView.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.backgroundColor)));
        View view = View.inflate(this, R.layout.content_item, null);

        GradientDrawable p = (GradientDrawable) dialog_delete.getBackground();
        p.setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.backgroundColor)));

        GradientDrawable p2 = (GradientDrawable) dialog_normal.getBackground();
        p2.setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.backgroundColor)));

        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(ThemeManager.getCurrentThemeRes(MainActivity.this, R.color.itemColor))));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maintoolbar,menu);
        menuItem= menu.findItem(R.id.edit_item);
        return true;
    }

    class MyUpdateTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

            try {
                JSONObject object = new JSONObject(s);
                int res=object.getInt("res");
                if (res==0){
                    Toast.makeText(MainActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void deleteAlarm() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_normal, null);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView confirm = (TextView) view.findViewById(R.id.confirm);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当点击删除按钮后“全不选”变为“全选”
                if (selectButton.getText().toString().equals(getResources().getText(R.string.not_select_all))){
                    selectButton.setText(getResources().getText(R.string.select_all));
                }

                //用户执行完删除操作后将删除和全选按钮设为不可见
                deleteButton.setVisibility(View.GONE);
                selectButton.setVisibility(View.GONE);

                //设置每个content的选择框为不可见
                for(int i=0;i<list.size();i++){

                    list.get(i).setShow(false);
                    list.get(i).setChecked(false);
                    adapter.notifyDataSetChanged();
                }
                menuItem.setTitle(getResources().getText(R.string.edit));
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<list.size();){
                    if(list.get(i).isChecked()){
                        //litepal删除checkBox被选中的数据
                        List<Content> tempList=DataSupport.order("msg desc").find(Content.class);
                        /*如果当前存在一个content，其下一件要做的事情就是当前即将删除的事情
                        *则先将这个content的下一件事设为nothing
                        */
                        if (!ContentHelper.getPreContent(tempList,list.get(i).getMsg()).equals("")){
                            ContentValues values=new ContentValues();
                            values.put("nextcontent","nothing");
                            //更新服务端数据
                            List<Content> contentList=DataSupport.where("msg=?",ContentHelper.getPreContent(tempList,list.get(i).getMsg())).find(Content.class);
                            String updateurl="http://120.79.7.33/update2.php?contentid="+contentList.get(0).getContentid();
                            new MyUpdateTask().execute(updateurl);
                            //更新本地数据
                            DataSupport.updateAll(Content.class,values,"msg=?",ContentHelper.getPreContent(tempList,list.get(i).getMsg()));
                        }

                        //发送请求删除服务端数据
                        String url="http://120.79.7.33/delete.php?contentid="+list.get(i).getContentid();
                        new MyDeleteTask().execute(url);
                        //删除本地数据
                        DataSupport.deleteAll(Content.class,"contentid=?",list.get(i).getContentid()+"");

                        list.remove(i);
                        adapter.notifyItemChanged(i);
                    }else{
                        i++;
                    }
                    //adapter.notifyItemInserted(i);
                }
                adapter.notifyDataSetChanged();
                //当点击删除按钮后“全不选”变为“全选”
                if (selectButton.getText().toString().equals(getResources().getText(R.string.not_select_all))){
                    selectButton.setText(getResources().getText(R.string.select_all));
                }

                //用户执行完删除操作后将删除和全选按钮设为不可见
                deleteButton.setVisibility(View.GONE);
                selectButton.setVisibility(View.GONE);

                //设置每个content的选择框为不可见
                for(int i=0;i<list.size();i++){

                    list.get(i).setShow(false);
                    list.get(i).setChecked(false);
                    adapter.notifyDataSetChanged();
                }
                menuItem.setTitle(getResources().getText(R.string.edit));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteNullAlarm() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_delete, null);
        TextView confirm = (TextView) view.findViewById(R.id.cannot_yes);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void changeLanguage(int type){

        //type=0时设置为中文
        if (type==0){
            navigationView.getMenu().getItem(0).setTitle(getResources().getString(R.string.inbox));
            navigationView.getMenu().getItem(1).setTitle(getResources().getString(R.string.set));
            navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.review));
           // navigationView.getMenu().getItem(3).setTitle(getResources().getString(R.string.recycle_bin));
            navigationView.getMenu().getItem(3).setTitle(getResources().getString(R.string.night_day_mode));
            navigationView.getMenu().getItem(4).setTitle(getResources().getString(R.string.language));

            menuItem.setTitle(getResources().getString(R.string.edit));
            selectButton.setText(getResources().getString(R.string.select_all));
            deleteButton.setText(getResources().getString(R.string.delete));

        }else{
        //type=1时设置为英文
            navigationView.getMenu().getItem(0).setTitle(getResources().getString(R.string.inbox_en));
            navigationView.getMenu().getItem(1).setTitle(getResources().getString(R.string.set_en));
            navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.review_en));
           // navigationView.getMenu().getItem(3).setTitle(getResources().getString(R.string.recycle_bin_en));
            navigationView.getMenu().getItem(3).setTitle(getResources().getString(R.string.night_day_mode_en));
            navigationView.getMenu().getItem(4).setTitle(getResources().getString(R.string.language_en));

            menuItem.setTitle(getResources().getString(R.string.edit_en));
            selectButton.setText(getResources().getString(R.string.select_all_en));
            deleteButton.setText(getResources().getString(R.string.delete_en));
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.right,fragment);
        transaction.commit();
    }

    public void changeToNight_Anim(){
        moonAnim1.setVisibility(View.VISIBLE);
        moonAnim2.setVisibility(View.VISIBLE);

        ObjectAnimator valueAnimator0 = ObjectAnimator.ofFloat(moonAnim1,"tmpAngle",0, 270);
        ObjectAnimator valueAnimator1 = ObjectAnimator.ofFloat(moonAnim2,"tmpAngle",0,180);

        ObjectAnimator animator0 = ObjectAnimator.ofFloat(moonAnim1,"alpha",1f, 0f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(moonAnim2,"alpha",1f,0f);
        animator0.setDuration(500);
        animator1.setDuration(500);

        final AnimatorSet animSet0 = new AnimatorSet();
        //AnimatorSet animSet1 = new AnimatorSet();
        //animSet1.play(animator0).with(animator1);
        animSet0.play(valueAnimator0).with(valueAnimator1);//.before(animSet1);
        animSet0.setDuration(1000);
        animSet0.start();

        animSet0.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //animSet1.start();
                moonAnim1.setVisibility(View.GONE);
                moonAnim2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }

    public ValueAnimator changeToDay_Anim(){
        sunAnim.setVisibility(View.VISIBLE);
        sunAnim_lines.setVisibility(View.VISIBLE);

        ObjectAnimator valueAnimator2 = ObjectAnimator.ofFloat(sunAnim,"tmpAngle",0,360);
        ObjectAnimator valueAnimator3 = ObjectAnimator.ofInt(sunAnim_lines,"linesNum",1,9);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(sunAnim,"alpha",1f, 0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(sunAnim_lines,"alpha",1f,0f);

        AnimatorSet animSet_sun = new AnimatorSet();
        //AnimatorSet animSet_sun_alpha = new AnimatorSet();
        //animSet_sun_alpha.play(animator2).with(animator3);
        //animSet_sun_alpha.setDuration(500);

        animSet_sun.play(valueAnimator2).with(valueAnimator3);//.before(animSet_sun_alpha);
        animSet_sun.setDuration(1000);
        animSet_sun.start();

        animSet_sun.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //animSet1.start();
                sunAnim.setVisibility(View.GONE);
                sunAnim_lines.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        return valueAnimator2;
    }


    public static class Select{

        public static void setButtonSelectAll(){
            if (selectButton.getText().toString().equals("全不选")){
                selectButton.setText("全选");
            }else if(selectButton.getText().toString().equals("NOT_SELECT_ALL")){
                selectButton.setText("SELECT_ALL");
            }
        }

        public static void setButtonNotSelectAll(){
            if (selectButton.getText().toString().equals("全选")){
                selectButton.setText("全不选");
            }else if(selectButton.getText().toString().equals("SELECT_ALL")){
                selectButton.setText("NOT_SELECT_ALL");
            }
        }
    }

}

