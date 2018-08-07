package com.example.administrator.gtd.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.gtd.MainActivity;
import com.example.administrator.gtd.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText username_edit;
    private EditText password_edit;
    private Button loginButton;

    private int res;
    private String msg;

    private String username="";
    private String password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username_edit=(EditText) findViewById(R.id.username);
        password_edit=(EditText) findViewById(R.id.password);
        loginButton=(Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "login", Toast.LENGTH_SHORT).show();
                username=username_edit.getText().toString();
                password=password_edit.getText().toString();
                String url = "http://120.79.7.33/login/login.php?username="+username+"&"+"password="+password;
                try {
                    doLogin(url);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.loginButton:

                Log.d("msgggg","hhhhhhh");
                Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
                username=username_edit.getText().toString();
                password=password_edit.getText().toString();
                String url = "http://220.167.43.113/login/login.php?username="+username+"&"+"password="+password;
                doLogin(url);
                break;*/
        }
    }

    private String doLogin(final String urltmp) {

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
