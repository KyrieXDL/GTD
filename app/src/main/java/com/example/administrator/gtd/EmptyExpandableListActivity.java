package com.example.administrator.gtd;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

public class EmptyExpandableListActivity extends AppCompatActivity implements ThemeManager.OnThemeChangeListener{

    private RelativeLayout relativeLayout;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_expandable_list);
        ThemeManager.registerThemeChangeListener(this);
        relativeLayout=(RelativeLayout) findViewById(R.id.relative);
        text=(TextView) findViewById(R.id.text);
        SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        int mode=sharedPreferences.getInt("mode",0);
        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
        }
        initTheme();
        String ss = Locale.getDefault().getLanguage();
        if (ss.equals("zh")) {
            text.setText( getResources().getString(R.string.not_add_anthing));
        }else{
            text.setText( getResources().getString(R.string.not_add_anthing_en));
        }
    }

    public void initTheme(){
        // 设置标题栏颜色
        /*if(supportActionBar != null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(ThemeManager.getCurrentThemeRes(ExpandableListView.this, R.color.colorPrimary))));
        }*/
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes(EmptyExpandableListActivity.this, R.color.colorPrimary)));
        }

        relativeLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(EmptyExpandableListActivity.this, R.color.backgroundColor)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeManager.unregisterThemeChangeListener(this);
    }
    @Override
    public void onThemeChanged() {
        initTheme();
    }
}
