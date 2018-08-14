package com.example.administrator.gtd.login;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.gtd.Content;
import com.example.administrator.gtd.HttpUtil;
import com.example.administrator.gtd.MainActivity;
import com.example.administrator.gtd.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Type;
import java.util.List;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener{

    private List<User> list;
    private EditText username_edit,password_edit,password2_edit;
    private Button button;
    private ImageView imageView,imageView1,imageView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        username_edit=(EditText) findViewById(R.id.username_edit);
        password_edit=(EditText) findViewById(R.id.password_edit);
        password2_edit=(EditText) findViewById(R.id.password2_edit);
        imageView=(ImageView) findViewById(R.id.username_check);
        imageView1=(ImageView) findViewById(R.id.password0_check);
        imageView2=(ImageView) findViewById(R.id.password_check);
        button=(Button) findViewById(R.id.regist);
        password_edit.setOnClickListener(this);
        password2_edit.setOnClickListener(this);
        button.setOnClickListener(this);
        imageView.setVisibility(View.INVISIBLE);
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);

        String url="http://120.79.7.33/gtd/queryuser.php";
        new MyTask().execute(url);

        username_edit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    imageView.setVisibility(View.VISIBLE);
                    String name=username_edit.getText().toString();
                    if (!checkUsername(name) && !name.equals("")){
                        imageView.setImageResource(R.drawable.valid);
                    }else if (name.equals("")){
                        Toast.makeText(RegistActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.invalid);
                    }else if (checkUsername(name)){
                        Toast.makeText(RegistActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                        imageView.setImageResource(R.drawable.invalid);
                    }
                }
            }
        });

        password_edit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    imageView1.setVisibility(View.VISIBLE);
                    String password=password_edit.getText().toString();
                    if (!password.equals("")){
                        imageView1.setImageResource(R.drawable.valid);
                    }else {
                        imageView1.setImageResource(R.drawable.invalid);
                        Toast.makeText(RegistActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.regist:
                imageView2.setVisibility(View.VISIBLE);
                String password=password_edit.getText().toString();
                String password2=password2_edit.getText().toString();
                if (password.equals(password2)){
                    imageView2.setImageResource(R.drawable.valid);
                    String url="http://120.79.7.33/gtd/regist.php?username="+username_edit.getText().toString()+"&password="+password;
                    new MyRegistTask().execute(url);
                }else{
                    Toast.makeText(RegistActivity.this, "两次密码不同", Toast.LENGTH_SHORT).show();
                    imageView2.setImageResource(R.drawable.invalid);
                }
                break;
        }
    }

    private boolean checkUsername(String username){
        for (int i=0;i<list.size();i++){
            if (list.get(i).getName().equals(username)){
                return true;
            }
        }

        return false;
    }
    class MyRegistTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object=new JSONObject(s);
                int res=object.getInt("res");

                if (res==1){
                    Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(RegistActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.getMessage();
            }
        }
    }


    class MyTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                Type type = new TypeToken<List<User>>() {}.getType();
                list = gson.fromJson(s, type);

            }catch (Exception e){
                e.getMessage();
            }
        }
    }

}
