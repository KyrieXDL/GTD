package com.example.administrator.gtd;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/5/7 0007.
 */

public class ContentHelper {
    //返回nextContent为nothing的content
    public static void findLastContent(List<String> tempList, List<Content> listTemp){
        for(int i=0;i<listTemp.size();i++){
            if((listTemp.get(i).getNextContent()==null)||(listTemp.get(i).getNextContent().equals("nothing"))){
                tempList.add(listTemp.get(i).getMsg());
            }
        }
    }

    //返回下一件事等于str的content的msg,即str的前一件事
    public static  String getPreContent(List<Content> listTemp,String str){
        //遍历数据库，返回nextContentdent与str的msg
        for(int i=0;i<listTemp.size();i++){
            if((listTemp.get(i).getNextContent()!=null)&&(!listTemp.get(i).getNextContent().equals("nothing"))){
                if (listTemp.get(i).getNextContent().equals(str)){
                    return listTemp.get(i).getMsg();
                }
            }
        }
        return "";
    }

    //nextcontent的数组中删除提醒时间早于当前提醒时间的content
    public static ArrayList<String> removeEarlyContent(String time, ArrayList<String> list1){
        ArrayList<String> listResult=new ArrayList<>();
        for (int i=1;i<list1.size();i++){
            List<Content> tempList= DataSupport.where("msg=?",list1.get(i)).find(Content.class);
            Content content=tempList.get(0);
            String timeDifference=getTimeBetweenContents(time,content.getAlarmTime());  //事件创立时间与事件提醒时间的相差时间
            if (Long.parseLong(timeDifference)>=0){
                listResult.add(content.getMsg());
            }
        }
        return listResult;
    }

    //获取两段时间间的毫秒数
    public static String getTimeBetweenContents(String starTime, String endTime) {
        String timeString = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse = dateFormat.parse(starTime);
            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();
            String string = Long.toString(diff);
            timeString=string;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeString;
    }
}
