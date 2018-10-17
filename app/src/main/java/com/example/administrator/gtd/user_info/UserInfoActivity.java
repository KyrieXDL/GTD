package com.example.administrator.gtd.user_info;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.gtd.MainActivity;
import com.example.administrator.gtd.R;
import com.example.administrator.gtd.ThemeManager;
import com.example.administrator.gtd.loginUI.RegistActivity;
import com.example.administrator.gtd.loginUI.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.reflect.TypeToken;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener, ThemeManager.OnThemeChangeListener{
    private Button button;
    private Button button1;
    private Button button2;
    //private Button upload_button;
    private String imagePath="";
    private ImageView head_image;
    private int flag=1;
    private EditText edit_name,edit_tele,edit_address;
    private RadioGroup radioGroup;
    private RadioButton radioButton1,radioButton2;
    private String sex="-1";
    private int userid;
    private CircleImageView circleImageView;

    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;

    private TextView textName;
    private TextView textAddress;
    private TextView textTele;
    private TextView textSex;
    //private com.dd.CircularProgressButton circularProgressButton;
    private List<User> list=new ArrayList<>();

    private com.dd.CircularProgressButton circularProgressButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initView();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButton1.getId()) {
                    sex="1";  //男性
                } else if (checkedId == radioButton2.getId()) {
                    sex="0";   //女性
                }
            }
        });

        String url="http://120.79.7.33/gtd/queryuser.php";
        new CheckTask().execute(url);
        Intent intent=getIntent();
        int userid =intent.getIntExtra("userid",0);

        circleImageView=(CircleImageView) findViewById(R.id.head);
        String imgurl="http://120.79.7.33/gtd/load.php?userid="+userid;
        Glide.with(UserInfoActivity.this).load(imgurl).error(R.drawable.head_img).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(circleImageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag==1){
                    startAnim();
                }else{
                    endAnim();
                }
            }
        });
        new  MyQueryTask().execute("http://120.79.7.33/gtd/queryuserinfo.php?userid="+userid);

        int mode=intent.getIntExtra("mode",0);
        String name=intent.getStringExtra("name");
        edit_name.setText(name);

        if (mode==1){
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT );
            relativeLayout.setBackground(getApplicationContext().getDrawable(R.drawable.night_bg));
        }else{
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY );
            relativeLayout.setBackground(getApplicationContext().getDrawable(R.drawable.day_bg));
        }
        initTheme();

        circularProgressButton=(com.dd.CircularProgressButton) findViewById(R.id.btnWithText);
        circularProgressButton.setIndeterminateProgressMode(true); // turn on indeterminate progress
        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edit_name.getText().toString();
                String tele=edit_tele.getText().toString();
                String address=edit_address.getText().toString();

                if (name.equals("")||tele.equals("")||address.equals("")||sex.equals("-1")){
                    Toast.makeText(UserInfoActivity.this, "信息不能为空", Toast.LENGTH_SHORT).show();
                }else if(tele.length()!=11){
                    Toast.makeText(UserInfoActivity.this, "电话号码格式不对", Toast.LENGTH_SHORT).show();
                }else if(checkPhone(tele)){
                    Toast.makeText(UserInfoActivity.this, "该电话号码已注册", Toast.LENGTH_SHORT).show();
                }else {

                    circularProgressButton.setProgress(1); // set progress to 0 to switch back to normal state
                    Intent intent = getIntent();
                    int userid = intent.getIntExtra("userid", 0);
                    String url = "http://120.79.7.33/gtd/adduserinfo.php";
                    new MyTask().execute(name, address, tele, sex, "" + userid, url, imagePath);
                }
            }
        });
    }

    @Override
    public void onThemeChanged() {
        initTheme();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeManager.unregisterThemeChangeListener(this);
    }
    public void initTheme(){
        // 设置标题栏颜色
        /*if(supportActionBar != null){
            supportActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(ThemeManager.getCurrentThemeRes(ExpandableListView.this, R.color.colorPrimary))));
        }*/
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.colorPrimary)));
        }

        linearLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.backgroundColor)));
        edit_tele.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));
        edit_address.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));
        edit_name.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));
        textName.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));
        textTele.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));
        textAddress.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));
        textSex.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));
        radioButton1.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));
        radioButton2.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(UserInfoActivity.this, R.color.itemColor)));

    }

    public void initView(){

        button=(Button) findViewById(R.id.button);  //打开相册
        button1=(Button) findViewById(R.id.button1);  //打开相机
        button2=(Button) findViewById(R.id.button2);  //取消
        //head_image=(ImageView) findViewById(R.id.head);//头像
        edit_name=(EditText) findViewById(R.id.edit_name);
        edit_address=(EditText) findViewById(R.id.edit_address);
        edit_tele=(EditText) findViewById(R.id.edit_tele);
        //upload_button=(Button) findViewById(R.id.upload);
        radioGroup=(RadioGroup) findViewById(R.id.radio_group);
        radioButton1=(RadioButton) findViewById(R.id.man);
        radioButton2=(RadioButton) findViewById(R.id.woman);
        linearLayout=(LinearLayout) findViewById(R.id.linearlayout);
        relativeLayout=(RelativeLayout) findViewById(R.id.relative);
        textName=(TextView) findViewById(R.id.text_name);
        textAddress=(TextView) findViewById(R.id.text_address);
        textTele=(TextView) findViewById(R.id.text_tele);
        textSex=(TextView) findViewById(R.id.text_sex);

        //head_image.setOnClickListener(this);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        //upload_button.setOnClickListener(this);

        button.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button:
                openAlbum();
                button.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.INVISIBLE);
                button1.setVisibility(View.INVISIBLE);
                circularProgressButton.setVisibility(View.VISIBLE);
                break;

            case R.id.button2:
                endAnim();
                break;

            /*case R.id.upload:

                String name=edit_name.getText().toString();
                String tele=edit_tele.getText().toString();
                String address=edit_address.getText().toString();

                if (name.equals("")||tele.equals("")||address.equals("")||sex.equals("-1")){
                    Toast.makeText(this, "信息不能为空", Toast.LENGTH_SHORT).show();
                }else if(tele.length()!=11){
                    Toast.makeText(this, "电话号码格式不对", Toast.LENGTH_SHORT).show();
                }else if(checkPhone(tele)){
                    Toast.makeText(this, "该电话号码已注册", Toast.LENGTH_SHORT).show();
                }else {

                    circularProgressButton.setProgress(1); // set progress to 0 to switch back to normal state
                    Intent intent = getIntent();
                    int userid = intent.getIntExtra("userid", 0);
                    String url = "http://120.79.7.33/gtd/adduserinfo.php";
                    new MyTask().execute(name, address, tele, sex, "" + userid, url, imagePath);
                }
                break;*/
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

    private boolean checkPhone(String phone){
        for (int i=0;i<list.size();i++){
            if (list.get(i).getPhone()!=null){
                if (list.get(i).getPhone().equals(phone)){
                    return true;
                }
            }
        }

        return false;
    }

    private class MyTask extends AsyncTask<String,Integer,String>
    {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String address=params[1];
            String tele=params[2];
            String sex=params[3];
            String userid=params[4];
            String url = params[5];
            String path=params[6];
            Response response = null;

            OkHttpClient mOkHttpClent = new OkHttpClient();
            File file = new File(path);
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("img", file.getName(),
                            okhttp3.RequestBody.create(  MediaType.parse("image/png"), file))
                    .addFormDataPart("name",name)
                    .addFormDataPart("address",address)
                    .addFormDataPart("tele",tele)
                    .addFormDataPart("sex",sex)
                    .addFormDataPart("userid",userid);

            okhttp3.RequestBody requestBody2 = builder
                    .build();
            Request request2 = new Request.Builder()
                    .url(url)
                    .post(requestBody2)
                    .build();
            //Call call = mOkHttpClent.newCall(request2);

            try {
                response = mOkHttpClent.newCall(request2).execute();
                //responseData = response.body().string();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "23";
        }

        protected  void onPostExecute(String s){
            super.onPostExecute(s);
            Toast.makeText(UserInfoActivity.this, ""+s, Toast.LENGTH_SHORT).show();

            try {
                JSONObject jsonObject = new JSONObject(s);
                int result = jsonObject.getInt("res");
                if(result==1){
                    Toast.makeText(UserInfoActivity.this, "Upload succeed!!", Toast.LENGTH_SHORT).show();
                    circularProgressButton.setProgress(100);
                }else{
                    Toast.makeText(UserInfoActivity.this , "Upload failed!!", Toast.LENGTH_SHORT).show();
                    circularProgressButton.setProgress(-1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void setShadow(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.user, new ShadowFragment());
        transaction.commit();
    }

    class CheckTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return com.example.administrator.gtd.HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                Type type = new TypeToken<List<User>>() {}.getType();
                list = gson.fromJson(s, type);
                //Toast.makeText(UserInfoActivity.this, ""+list.get(0).getName()+" "+list.get(0).getPhone(), Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.getMessage();
            }
        }
    }

    class MyQueryTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpUtil.sendHttpRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //Toast.makeText(getActivity().getApplicationContext(), "response:"+s, Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                Type type = new TypeToken<List<Userinfo>>() {}.getType();
                List<Userinfo> list = gson.fromJson(s, type);
                //edit_name.setText(list.get(0).getName());
                edit_address.setText(list.get(0).getAddress());
                edit_tele.setText(list.get(0).getTele());
                if (list.get(0).getSex().equals("1")){
                    radioButton1.setChecked(true);
                }else{
                    radioButton2.setChecked(true);
                }

            } catch (Exception e) {
                e.getMessage();
                if (s.equals("")) {
                } else {

                }
            }
        }
    }

    private void startAnim(){
        button.setVisibility(View.VISIBLE);  //open album
        button1.setVisibility(View.VISIBLE);  //open camera
        button2.setVisibility(View.VISIBLE);  //cancel
        circularProgressButton.setVisibility(View.INVISIBLE);
        //setShadow();
        flag=0;
        float curTranslationY = button.getTranslationY();
        // 获得当前按钮的位置
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "translationY", curTranslationY, -(button2.getHeight()+10));
        animator.setDuration(500);
        animator.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(button1, "translationY", curTranslationY, -(button2.getHeight()*2+10));
        animator2.setDuration(500);
        animator2.start();
    }

    private void endAnim(){
        flag=1;

        float curTranslationY = button.getTranslationY();
        // 获得当前按钮的位置
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "translationY", curTranslationY,button2.getHeight()+10);
        animator.setDuration(500);
        animator.start();

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(button1, "translationY",  curTranslationY,button2.getHeight()*2+10);
        animator2.setDuration(500);
        animator2.start();

        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                button.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.INVISIBLE);
                button1.setVisibility(View.INVISIBLE);
                circularProgressButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

    }

    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 2:
                if (resultCode==RESULT_OK){
                    //Toast.makeText(this, "onActivityResult", Toast.LENGTH_SHORT).show();
                    String path=handleImageOnKitKat(data);
                    imagePath=path;
                    //Toast.makeText(this, ""+imagePath, Toast.LENGTH_SHORT).show();*/
                    //ImageView imageView=(ImageView) findViewById(R.id.image);
                    displayImage(path);
                }
                break;
        }
    }

    private String handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        //displayImage(imagePath);
        return imagePath;
    }

    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath!=null){
            /*Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            circleImageView.setImageBitmap(bitmap);*/
            Glide.with(UserInfoActivity.this).load(imagePath).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(circleImageView);
        }else{
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
