package com.example.administrator.gtd;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeManager.registerThemeChangeListener(this);

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

        // 默认设置为日间模式
        //ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );

        //
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

        //设置navigationView的item的点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //在这里处理item的点击事件
                switch (item.getItemId()){
                    case R.id.inbox_item:
                        Intent intent =new Intent(MainActivity.this,ExpandableListView.class);
                        //SharedPreferences sharedPreferences0=getSharedPreferences("data",MODE_PRIVATE);
                        int mode0=sharedPreferences.getInt("mode",0);
                        intent.putExtra("mode",mode0);
                        startActivity(intent);
                        break;

                    case R.id.mode:
                        /*int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                        getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO
                                ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                        // 同样需要调用recreate方法使之生效
                        //recreate();

                        if (night_mode==0){
                            night_mode=1;
                        }else{
                            night_mode=0;
                        }

                        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.putInt("mode",night_mode);
                        editor.apply();

                        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
                        int mode=sharedPreferences.getInt("mode",0);
                        Toast.makeText(MainActivity.this,""+mode,Toast.LENGTH_SHORT).show();*/

                        //Toast.makeText(MainActivity.this,""+night_mode,Toast.LENGTH_SHORT).show();

                        /*ThemeManager.setThemeMode(ThemeManager.getThemeMode() == ThemeManager.ThemeMode.DAY
                                ? ThemeManager.ThemeMode.NIGHT : ThemeManager.ThemeMode.DAY);*/

                        //SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
                        int mode=sharedPreferences.getInt("mode",0);

                        if (mode==0){
                            mode=1;
                            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
                            Toast.makeText(MainActivity.this,"切换到夜间模式",Toast.LENGTH_SHORT).show();

                        }else{
                            mode=0;
                            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
                            Toast.makeText(MainActivity.this,"切换到日间模式",Toast.LENGTH_SHORT).show();
                        }

                        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.putInt("mode",mode);
                        editor.apply();

                        Toast.makeText(MainActivity.this,"night_mode= "+mode,Toast.LENGTH_SHORT).show();


                        for(int i=0;i<list.size();i++){
                            adapter.notifyItemChanged(i);
                        }

                        //Toast.makeText(MainActivity.this,"night_mode= "+list.get(0).getNight_mode(),Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.language:
                        String ss = Locale.getDefault().getLanguage();
                        //用if语句判断，如果当前为中文就变成英文，反之变成中文
                        if (ss.equals("zh")){
                            Locale.setDefault(Locale.ENGLISH);
                            Configuration configuration = getBaseContext().getResources().getConfiguration();
                            configuration.locale = Locale.ENGLISH;
                            getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
                            //recreate();
                            changeLanguage(1);
                        }else {
                            Locale.setDefault(Locale.CHINESE);
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

        //初始化删除和全选按钮为不可见
        deleteButton=(Button) findViewById(R.id.delete_button);
        selectButton=(Button) findViewById(R.id.select_all);
        deleteButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);

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
                intent.putExtra("num0",list.size()+1);

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
                startActivity(intent);

                menuItem.setTitle(getResources().getText(R.string.edit));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        List<Content> newList=DataSupport.order("msg desc").find(Content.class);
        list.addAll(newList);
        adapter.notifyDataSetChanged();

        //获取当地已存储的主题模式，并设置
        //SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        int mode=sharedPreferences.getInt("mode",0);

        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
        }

        //初始化控件不可见
        deleteButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);

    }

    public void onThemeChanged() {
        initTheme();
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
                            values.put("nextContent","nothing");
                            DataSupport.updateAll(Content.class,values,"msg=?",ContentHelper.getPreContent(tempList,list.get(i).getMsg()));
                        }

                        DataSupport.deleteAll(Content.class,"msg=?",list.get(i).getMsg());

                        list.remove(i);
                        adapter.notifyItemInserted(i);
                        adapter.notifyItemRangeChanged(0,list.size());
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
            navigationView.getMenu().getItem(3).setTitle(getResources().getString(R.string.recycle_bin));
            navigationView.getMenu().getItem(4).setTitle(getResources().getString(R.string.night_day_mode));
            navigationView.getMenu().getItem(5).setTitle(getResources().getString(R.string.language));

            menuItem.setTitle(getResources().getString(R.string.edit));
            selectButton.setText(getResources().getString(R.string.select_all));
            deleteButton.setText(getResources().getString(R.string.delete));

        }else{
        //type=1时设置为英文
            navigationView.getMenu().getItem(0).setTitle(getResources().getString(R.string.inbox_en));
            navigationView.getMenu().getItem(1).setTitle(getResources().getString(R.string.set_en));
            navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.review_en));
            navigationView.getMenu().getItem(3).setTitle(getResources().getString(R.string.recycle_bin_en));
            navigationView.getMenu().getItem(4).setTitle(getResources().getString(R.string.night_day_mode_en));
            navigationView.getMenu().getItem(5).setTitle(getResources().getString(R.string.language_en));

            menuItem.setTitle(getResources().getString(R.string.edit_en));
            selectButton.setText(getResources().getString(R.string.select_all_en));
            deleteButton.setText(getResources().getString(R.string.delete_en));
        }
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

