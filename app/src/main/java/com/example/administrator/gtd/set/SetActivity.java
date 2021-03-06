package com.example.administrator.gtd.set;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.gtd.R;
import com.example.administrator.gtd.ThemeManager;

import java.io.File;

public class SetActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener{

    private com.suke.widget.SwitchButton switchButton1;
    private com.suke.widget.SwitchButton switchButton2;
    private RadioGroup radioGroup;
    private RadioButton radioButton1,radioButton2,radioButton3,radioButton4;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private LinearLayout musicLayout;
    private RelativeLayout background_layout;
    private RelativeLayout setShakeLayout;
    private RelativeLayout setMusicLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ThemeManager.registerThemeChangeListener(this);
        Intent intent=getIntent();
        int mode=intent.getIntExtra("mode",0);

        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
        }

        initViews();
        setListener();
        initTheme();
    }

    //初始化控件
    private void initViews(){
        switchButton1=(com.suke.widget.SwitchButton) findViewById(R.id.switch_button1);
        switchButton2=(com.suke.widget.SwitchButton) findViewById(R.id.switch_button2);
        radioGroup=(RadioGroup) findViewById(R.id.radio_group);
        radioButton1=(RadioButton) findViewById(R.id.music1);
        radioButton2=(RadioButton) findViewById(R.id.music2);
        radioButton3=(RadioButton) findViewById(R.id.music3);
        radioButton4=(RadioButton) findViewById(R.id.music4);
        musicLayout=(LinearLayout) findViewById(R.id.music);
        background_layout=(RelativeLayout) findViewById(R.id.background_layout);
        setShakeLayout=(RelativeLayout) findViewById(R.id.set1);
        setMusicLayout=(RelativeLayout) findViewById(R.id.set2);
        sharedPreferences=getSharedPreferences("music_data",MODE_PRIVATE);
        editor=getSharedPreferences("music_data",MODE_PRIVATE).edit();
        int playMusic=sharedPreferences.getInt("play_music",0);
        int isShake=sharedPreferences.getInt("isShake",0);
        int music=sharedPreferences.getInt("music",0);
        //Toast.makeText(this, "start"+music, Toast.LENGTH_SHORT).show();
        if (music==1){
            radioButton1.setChecked(true);
        }else if (music==2){
            radioButton2.setChecked(true);
        }else if (music==3){
            radioButton3.setChecked(true);
        }else if (music==4){
            radioButton4.setChecked(true);
        }

        if (playMusic==1){
            switchButton2.setChecked(true);
        }else {
            switchButton2.setChecked(false);
            initAnim();
        }

        if (isShake==1){
            switchButton1.setChecked(true);
        }else{
            switchButton1.setChecked(false);
        }
    }

    //设置监听事件
    private void setListener(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == radioButton1.getId()) {
                    mediaPlayer.reset();
                    editor.putInt("music",1);
                    editor.apply();
                    Uri uri=Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music1);
                    initMediaPlayer(uri);
                    mediaPlayer.start();
                } else if (checkedId == radioButton2.getId()) {
                    mediaPlayer.reset();
                    editor.putInt("music",2);
                    editor.apply();
                    Uri uri=Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music2);
                    initMediaPlayer(uri);
                    mediaPlayer.start();
                }else  if(checkedId==radioButton3.getId()){
                    mediaPlayer.reset();
                    editor.putInt("music",3);
                    editor.apply();
                    Uri uri=Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music3);
                    initMediaPlayer(uri);
                    mediaPlayer.start();
                }else if(checkedId==radioButton4.getId()){
                    mediaPlayer.reset();
                    editor.putInt("music",4);
                    editor.apply();
                    Uri uri=Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music4);
                    initMediaPlayer(uri);
                    mediaPlayer.start();
                }
            }
        });

        switchButton2.setOnCheckedChangeListener(new com.suke.widget.SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(com.suke.widget.SwitchButton view, boolean isChecked) {
                //TODO do your job
                if (isChecked){
                    editor.putInt("play_music",1);
                    editor.apply();
                    startAnim();
                }else{
                    editor.putInt("play_music",0);
                    //editor.putInt("music",0);
                    editor.apply();
                    endAnim();
                }
            }
        });

        switchButton1.setOnCheckedChangeListener(new com.suke.widget.SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(com.suke.widget.SwitchButton view, boolean isChecked) {
                //TODO do your job
                if (isChecked){
                    editor.putInt("isShake",1);
                    editor.apply();
                }else{
                    editor.putInt("isShake",0);
                    editor.apply();
                }
            }
        });
    }

    private void initMediaPlayer(Uri uri){
        try{
            //File file=new File(path);
            mediaPlayer.setDataSource(SetActivity.this,uri);
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void startAnim(){

        float height = musicLayout.getHeight();
        // 获得当前按钮的位置
        ObjectAnimator animator = ObjectAnimator.ofFloat(musicLayout, "scaleY", 0, 1f);
        animator.setDuration(500);
        animator.start();

    }

    private void endAnim(){
        float height = musicLayout.getHeight();
        // 获得当前按钮的位置
        ObjectAnimator animator = ObjectAnimator.ofFloat(musicLayout, "scaleY", 1f,0);
        animator.setDuration(500);
        animator.start();

    }

    private void initAnim(){
        float height = musicLayout.getHeight();
        // 获得当前按钮的位置
        ObjectAnimator animator = ObjectAnimator.ofFloat(musicLayout, "scaleY", 1f,0);
        animator.setDuration(0);
        animator.start();

    }

    public void initTheme(){
        // 设置标题栏颜色
        /*if(supportActionBar != null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(ThemeManager.getCurrentThemeRes(ExpandableListView.this, R.color.colorPrimary))));
        }*/
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes(SetActivity.this, R.color.colorPrimary)));
        }

        background_layout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(SetActivity.this, R.color.backgroundColor)));
        musicLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(SetActivity.this, R.color.yellow)));
        setShakeLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(SetActivity.this, R.color.yellow)));
        setMusicLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(SetActivity.this, R.color.yellow)));
    }

    @Override
    public void onThemeChanged() {
        initTheme();
    }
}
