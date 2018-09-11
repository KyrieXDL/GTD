package com.example.administrator.gtd.loginUI;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.gtd.MainActivity;
import com.example.administrator.gtd.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private CardView cv;
    private FloatingActionButton fab;

    private int res;
    private String msg;

    private String username="";
    private String password="";
    private ActivityOptionsCompat oc2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.login_ui);
        initView();
        setListener();
    }

    private void initView() {
        etUsername =(EditText) findViewById(R.id.et_username);
        etPassword =(EditText) findViewById(R.id.et_password);
        btGo =(Button) findViewById(R.id.bt_go);
        cv =(CardView) findViewById(R.id.cv);
        fab =(FloatingActionButton) findViewById(R.id.fab);
        /*supportActionBar = getSupportActionBar();
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }*/
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //设置登录切换动画
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);

                username=etUsername.getText().toString();
                password=etPassword.getText().toString();
                String url = "http://120.79.7.33/login/login.php?username="+username+"&"+"password="+password;
                if (username.equals("")){
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        //doLogin(url);
                        new LoginTask().execute(url);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(LoginActivity.this, RegistActivity.class), ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, "loginFab").toBundle());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

   /* private String doLogin(final String urltmp) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                try{
                    URL url=new URL(urltmp);
                    connection=(HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream in=connection.getInputStream();

                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    JSONObject object=new JSONObject(response.toString());
                    res=object.getInt("res");
                    msg=object.getString("msg");
                    int id=object.getInt("id");
                    if (res==1){
                        showResponse(msg);
                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userid",id);

                        startActivity(intent);
                    }else{
                        showResponse(msg);
                    }
                    //showResponse(response.toString());
                }catch(Exception e){
                    e.printStackTrace();
                    showResponse(getResources().getString(R.string.net_error));
                }finally {
                    if (reader!=null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                    if (connection !=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();

        return null;
    }*/

    class LoginTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            Response response = null;
            OkHttpClient okHttpClient = new OkHttpClient();
            //request
            RequestBody formBody = new FormBody.Builder().build();
            Request request = new Request.Builder()
                    .url(url).build();
            try {
                response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
        protected void onPostExecute(String s){
            super.onPostExecute(s);
           // Toast.makeText(getActivity(),""+s,Toast.LENGTH_LONG).show();
            int id=0;
            try{
                JSONObject object=new JSONObject(s);
                res=object.getInt("res");
                msg=object.getString("msg");
                id=object.getInt("id");
            }catch (Exception e){
                e.printStackTrace();
            }

            if (res==1){
                //showResponse(msg);
               // Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userid",id);
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
                startActivity(intent,oc2.toBundle());
            }else{
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                //showResponse(msg);
            }

        }
    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
