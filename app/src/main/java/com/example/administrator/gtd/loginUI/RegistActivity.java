package com.example.administrator.gtd.loginUI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.gtd.HttpUtil;
import com.example.administrator.gtd.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


public class RegistActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private CardView cvAdd;
    private List<User> list;
    private EditText username_edit,password_edit,password2_edit;
    private Button button;
   /* private ImageView img1;
    private ImageView img2;
    private ImageView img3;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_ui);
        ShowEnterAnimation();
        initView();
        String url="http://120.79.7.33/gtd/queryuser.php";
        new MyTask().execute(url);
        setListener();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
                cancelFocus();
            }
        });
    }

    private void initView() {
        fab =(FloatingActionButton) findViewById(R.id.fab);
        cvAdd =(CardView) findViewById(R.id.cv_add);
        username_edit=(EditText) findViewById(R.id.et_username);
        password_edit=(EditText) findViewById(R.id.et_password);
        password2_edit=(EditText) findViewById(R.id.et_repeatpassword);
        button=(Button) findViewById(R.id.bt_go);
        /*img1=(ImageView) findViewById(R.id.img1);
        img2=(ImageView) findViewById(R.id.img2);
        img3=(ImageView) findViewById(R.id.img3);
        img1.setVisibility(View.INVISIBLE);
        img2.setVisibility(View.INVISIBLE);
        img3.setVisibility(View.INVISIBLE);*/
    }

    private void setListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username_edit.getText().toString().equals("")||password_edit.getText().toString().equals("")||password2_edit.getText().toString().equals("")){
                    Toast.makeText(RegistActivity.this, "注册信息不完整", Toast.LENGTH_SHORT).show();
                }else{
                    //imageView2.setVisibility(View.VISIBLE);
                    String password=password_edit.getText().toString();
                    String password2=password2_edit.getText().toString();
                    if (password.equals(password2)){
                        //imageView2.setImageResource(R.drawable.valid);
                        String url="http://120.79.7.33/gtd/regist.php?username="+username_edit.getText().toString()+"&password="+password;
                        new MyRegistTask().execute(url);
                    }else{
                        Toast.makeText(RegistActivity.this, "两次密码不同", Toast.LENGTH_SHORT).show();
                        //imageView2.setImageResource(R.drawable.invalid);
                    }
                }
            }
        });

        username_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(RegistActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                setFocus();
            }
        });
    }

    public void setFocus(){
        username_edit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    //img1.setVisibility(View.VISIBLE);
                    String name=username_edit.getText().toString();
                    if (name.equals("")){
                        Toast.makeText(RegistActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                        //imageView.setImageResource(R.drawable.invalid);
                    }
                    if (checkUsername(name)){
                        Toast.makeText(RegistActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                        //imageView.setImageResource(R.drawable.invalid);
                    }
                    //Toast.makeText(RegistActivity.this, "nu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        password_edit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    // imageView1.setVisibility(View.VISIBLE);
                    String password=password_edit.getText().toString();
                    if (!password.equals("")){
                        //  imageView1.setImageResource(R.drawable.valid);
                    }else {
                        // imageView1.setImageResource(R.drawable.invalid);
                        Toast.makeText(RegistActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        password2_edit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    // imageView1.setVisibility(View.VISIBLE);
                    String password=password_edit.getText().toString();
                    String password2=password2_edit.getText().toString();
                    if (!password.equals(password2)){
                        Toast.makeText(RegistActivity.this, "两次密码不同", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public void cancelFocus(){
        username_edit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        password_edit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        password2_edit.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
    }


    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegistActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    private boolean checkUsername(String username){
        //Toast.makeText(this, "checking...", Toast.LENGTH_SHORT).show();
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
                    //finish();
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
