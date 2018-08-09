package com.example.administrator.gtd.user_info;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/7/29 0029.
 */

public class HttpUtil {

    public static String sendHttpRequest(String url){
        //异步GET请求
        //创建OKHTTPCLIENT对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //create request object
        final Request request = new Request.Builder()
                .url(url)
                .build();
        // create call
        //Call call2 = okHttpClient.newCall(request);
        Response response = null;
        String s="";
        try {
            //request net task
            response = okHttpClient.newCall(request).execute();//得到Response 对象
            s=response.body().string();;
        }catch (Exception e){
            e.printStackTrace();
        }

        return s;
    }
}
