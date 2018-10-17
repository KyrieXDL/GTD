package com.example.administrator.gtd.loginUI;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gtd.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetPasswordActivity extends AppCompatActivity {

    private Button getCode;
    private Button changePassword;
    private EditText et_phone;
    private EditText et_code;
    private EditText et_password;
    private EditText et_password2;
    final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password);
        initView();
        initClick();
    }

    private void initView(){
        getCode=(Button) findViewById(R.id.getCode);
        changePassword=(Button) findViewById(R.id.changePassword);
        et_phone=(EditText) findViewById(R.id.phone);
        et_code=(EditText) findViewById(R.id.code);
        et_password=(EditText) findViewById(R.id.password);
        et_password2=(EditText) findViewById(R.id.password2);
    }

    public void initClick(){


        //给Button设置点击时间,触发倒计时
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=et_phone.getText().toString();
                if (phone.length()!=11){
                    Toast.makeText(GetPasswordActivity.this, "电话格式错误", Toast.LENGTH_SHORT).show();
                }else {
                    String url = "http://120.79.7.33/gtd/sms_curl.php?phone=" + et_phone.getText().toString();
                    new GetCodeTask().execute(url);
                }

            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=et_password.getText().toString();
                String password2=et_password2.getText().toString();
                String phone=et_phone.getText().toString();
                String code=et_code.getText().toString();
                if (password.equals("") || password2.equals("") || phone.equals("") || code.equals("")){
                    Toast.makeText(GetPasswordActivity.this, "信息不完整", Toast.LENGTH_SHORT).show();
                }else if (phone.length()!=11){
                    Toast.makeText(GetPasswordActivity.this, "电话格式错误", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(password2)){
                    Toast.makeText(GetPasswordActivity.this, "两次密码不同", Toast.LENGTH_SHORT).show();
                }else{
                    String url="http://120.79.7.33/gtd/changepassword.php?password="+password+"&phone="+phone+"&code="+code;
                    new SendTask().execute(url);
                }
            }
        });
    }

    class SendTask extends AsyncTask<String,Integer,String> {

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
            int res=0;
            String msg="";
            try {
                JSONObject object = new JSONObject(s);
                res = object.getInt("res");
                msg = object.getString("msg");
            }catch (Exception e){
                e.printStackTrace();
            }
            if (res==1){
                Toast.makeText(GetPasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
            }

        }
    }


    class GetCodeTask extends AsyncTask<String,Integer,String> {

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

            if (s.equals("100")){
                myCountDownTimer.start();
                Toast.makeText(GetPasswordActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
            }else if(s.equals("121")){
                Toast.makeText(GetPasswordActivity.this, "手机号码错误", Toast.LENGTH_SHORT).show();
            }else if(s.equals("108") || s.equals("110")){
                Toast.makeText(GetPasswordActivity.this, "验证过于频繁", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(GetPasswordActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                //showResponse(msg);
            }

        }
    }


    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            getCode.setClickable(false);
            getCode.setText(l/1000+"s后可重新发送");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            getCode.setText("重新获取验证码");
            //设置可点击
            getCode.setClickable(true);
        }
    }
}
