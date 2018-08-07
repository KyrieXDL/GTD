package com.example.administrator.gtd;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/7/21 0021.
 */

public class HttpUtil {

    public static String sendHttpRequest(String urltmp){
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        try{
            URL url=new URL(urltmp);
            connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in=connection.getInputStream();

            reader=new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line="";
            while ((line=reader.readLine())!=null){
                response.append(line);
            }
            return response.toString();
        }catch(Exception e){
            e.printStackTrace();
            return e.getMessage()+"json";
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

    public static String sendHttp(String urltmp,String msg, String buildtime,String alarmtime,String level,String nextcontent,String userid){
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        StringBuilder response = new StringBuilder();

        try{
            /*URL url=new URL("http://120.79.7.33/insert.php?msg="+msg+"&buildtime="+buildtime+"&alarmtime="+alarmtime+
                                    "&level="+level+"&nextcontent="+ nextcontent + "&userid=" + userid);*/
            URL url=new URL(urltmp);
            connection=(HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(8000);
            //connection.setUseCaches(false);//不使用缓存
            //connection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            connection.setReadTimeout(8000);//响应的超时时间
            connection.setRequestMethod("POST");//设置请求的方式
            connection.setRequestProperty("Accept-Charset", "GBK");
            connection.setRequestProperty("contentType", "GBK");

            String info="msg="+msg+"&buildtime="+buildtime+"&alarmtime="+alarmtime+
                    "&level="+level+"&nextcontent="+ nextcontent + "&userid=" + userid;
            DataOutputStream out=new DataOutputStream(connection.getOutputStream());
            out.writeBytes(info);

            InputStream in=connection.getInputStream();
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while ((line=reader.readLine())!=null){
                response.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return response.toString();
    }

}
